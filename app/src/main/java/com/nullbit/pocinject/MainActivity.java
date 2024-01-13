package com.nullbit.pocinject;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;



public class MainActivity extends Activity {

    static {
        System.loadLibrary("pocinject");
    }

    EditText input;
    public String filterText = "";
    private LibAdapter adapter;
    private ListView listView;
    private final ArrayList<String> libraryList = new ArrayList<>();
    private final Handler handler = new Handler();
    private final Runnable updateLibrariesRunnable = new Runnable() {
        @Override
        public void run() {
            updateLibraries();
            Update();
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.input);
        String machineArch = Build.SUPPORTED_ABIS[0];
        String appArch = getABI();
        int pid = android.os.Process.myPid();
        long hijackAddress = getHijackAddress();
        Log.d("NULLOG", "PID: "+pid);
        Log.d("NULLOG", "Hijack fun address: 0x" + String.format("%x", hijackAddress).toUpperCase());
        Log.d("NULLOG", "App arch: "+appArch);
        Log.d("NULLOG", "Machine arch: "+machineArch);

        ((TextView)findViewById(R.id.machineArch)).append(machineArch);
        ((TextView)findViewById(R.id.appArch)).append(appArch);
        ((TextView)findViewById(R.id.pid)).append(Integer.toString(pid));
        ((TextView)findViewById(R.id.hijackAddr)).append("0x"+String.format("%x", hijackAddress).toUpperCase());

        listView = (ListView) findViewById(R.id._dynamic);
        adapter = new LibAdapter(this, libraryList);
        listView.setAdapter(adapter);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterText = editable.toString();
            }
        });

        handler.post(updateLibrariesRunnable);
    }

    private void updateLibraries() {


        libraryList.clear();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/self/maps"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(".so")) {
                    int start = line.lastIndexOf('/');
                    if (start != -1) {
                        if(line.substring(start+1).toLowerCase().contains(filterText.toLowerCase()) || filterText.trim().equals("")) {
                            libraryList.add(line.substring(start + 1));
                        }
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateLibrariesRunnable);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) {
            if (input != null) {
                int[] inputPos = new int[2];
                input.getLocationOnScreen(inputPos);
                int x = inputPos[0];
                int y = inputPos[1];
                int width = input.getWidth();
                int height = input.getHeight();
                float eventX = event.getRawX();
                float eventY = event.getRawY();
                if(!inRange(x,x+width,eventX) || !inRange(y, y+height, eventY)) {
                    input.setCursorVisible(false);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                }
                else
                    input.setCursorVisible(true);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean inRange(float min, float max, float val) {
        return val > min && val < max;
    }

    public static native String getABI();
    public static native void Update();
    public static native long getHijackAddress();
}
