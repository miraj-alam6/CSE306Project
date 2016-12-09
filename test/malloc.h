#ifndef __malloc.h
#define __malloc.h
#include <stddef.h>

void *malloc(unsigned int size);
void free(void *ptr);