#include "syscall.h"

extern void *heap_start;
extern void *heap_limit;


int main(int n)
{	
/*
	Why does this go crazy, and not work at all
	((char *)heap_start)[1] = 'a';
	((char *)heap_start)[2] = 'b';
	((char *)heap_start)[3] = 'c';
	((char *)heap_start)[4] = 'd';
	
	*/
	
	/*Why does this partially work. 'a' is not printed*/
	/*Conclusion: whatever instruction was responsible for table extension, is not run.*/
	/*((char *)heap_start)[0] = 'a'; */
	((char *)heap_start)[0] = 'a'; 
	((char *)heap_start)[1] = 'b';
	((char *)heap_start)[2] = 'c';
	((char *)heap_start)[3] = 'd';
	
	Write(heap_start, 4, 1);	
	Exit(0);
}
	