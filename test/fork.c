/* read.c

*/

#include "syscall.h"

void print_message() {
	char *message = "Message that is printed is this.";
	/*Size of above string is 25 including null character*/

	Write(message, 25, 1);
}

int main()
{

	void(*a_function)(void) = &print_message;
	Fork(a_function);
	return 0;
}


