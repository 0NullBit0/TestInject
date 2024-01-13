
#ifndef POCINJECT_VULNERABLE_H
#define POCINJECT_VULNERABLE_H



#include <stdio.h>
#include <unistd.h>
#include "logs.h"

extern "C" int hijackMe(int a, char *b, bool c);
extern "C" void test();

#endif //POCINJECT_VULNERABLE_H
