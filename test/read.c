/* read.c

*/

#include "syscall.h"

int main()
{
	
	int length = 25;
	char buffer[length];
	Read(buffer,length,0); /*0 is for ConsoleInput*/
	return 0;
}
