#ifndef __malloc.h
#define __malloc.h
#endif
struct memory_region {
           struct memory_region *next;
           int size;
		   char data[0];  /* The data starts here and continues on. */
        };        
void *malloc(unsigned int size);
void free(void *ptr);