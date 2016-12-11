#include "syscall.h"

extern void *heap_start;
extern void *heap_limit;


int main(int n)
{	

	((char *)heap_start)[0] = 'a';
	((char *)heap_start)[1] = 'b';
	((char *)heap_start)[2] = 'c';
	((char *)heap_start)[3] = 'd';
	
	
	Write(heap_start, 4, 1);	
	Exit(0);
}
	