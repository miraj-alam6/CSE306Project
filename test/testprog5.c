/* read.c

*/

#include "syscall.h"

int main()
{
	char *message = "Join worked if this printed last\n";
	int messageLength = getStringSize(message);
	
	int progID = Exec("test/write");
	Join(progID);	

	Write(message, messageLength, 1);
	return 0;
}

int getStringSize(char *s) {
	int count = 0;
	while (*s != 0) {
		s++;
		count++;
	}
	/*count++;*/
	return count;
}
