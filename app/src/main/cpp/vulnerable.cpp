#include "vulnerable.h"

extern "C" int hijackMe(int a, char* b, bool c) {
    /*printf("I got called\n");
    printf("%d\n", a);
    printf("%s\n", b);
    printf(c ? "true\n" : "false\n");

    LOGI("I got called");
    LOGI("%d", a);
    LOGI("%s", b);
    LOGI(c ? "true" : "false");
    */
    return 0;
}

extern "C" void test() {
    printf("THE TEST FUNCTION WAS CALLED!\n");
    LOGI("THE TEST FUNCTION WAS CALLED\n");
}


