#include "syscall.h"

extern void *heap_start;
extern void *heap_limit;


int main(int n)
{	
	((char *)heap_start)[0] = 'a';
	
	Exit(0);
}
	