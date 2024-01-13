#include <jni.h>
#include "vulnerable.h"
#include "logs.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_nullbit_pocinject_MainActivity_getABI(JNIEnv* env, jclass) {
    #if defined(__arm__)
        #if defined(__ARM_ARCH_7A__)
            #if defined(__ARM_NEON__)
                #if defined(__ARM_PCS_VFP)
                    #define ABI "armeabi-v7a/NEON (hard-float)"
                #else
                    #define ABI "armeabi-v7a/NEON"
                #endif
            #else
                #if defined(__ARM_PCS_VFP)
                    #define ABI "armeabi-v7a (hard-float)"
                #else
                    #define ABI "armeabi-v7a"
                #endif
            #endif
        #else
            #define ABI "armeabi"
        #endif
    #elif defined(__i386__)
        #define ABI "x86"
    #elif defined(__x86_64__)
        #define ABI "x86_64"
    #elif defined(__mips64)
        #define ABI "mips64"
    #elif defined(__mips__)
        #define ABI "mips"
    #elif defined(__aarch64__)
        #define ABI "arm64-v8a"
    #else
        #define ABI "unknown"
    #endif
    return env->NewStringUTF(ABI);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_nullbit_pocinject_MainActivity_getHijackAddress(JNIEnv* env, jclass) {
    uintptr_t hijackAddr = reinterpret_cast<uintptr_t>(&hijackMe);
    return (jlong)hijackAddr;
}

extern "C" JNIEXPORT void JNICALL
Java_com_nullbit_pocinject_MainActivity_Update(JNIEnv* env, jclass) {
    hijackMe(0, "lol", false);
}

