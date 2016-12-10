#include "syscall.h"
#include "malloc.h"
extern void *heap_start;
extern void *heap_limit;


struct OBJ {
	int x;
   	struct OBJ *nextp;
 };

void f() 
{
   struct OBJ *objp = (struct OBJ *)malloc(sizeof(struct OBJ));
   objp->x = 3;
   objp->nextp = NULL;
   if(objp->x == 3){
   	Write("If this prints then allocation worked", 38, 1);
   }
   free(objp);
}



int main(int n)
{	
	f();	
	Exit(0);
}
	