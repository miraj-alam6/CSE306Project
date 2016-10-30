#include "syscall.h"

int main()
{
	int i;
	char *message = "Message from testAnotherLongLoop";
	int messageLength = getStringSize(message);

	for (i = 0; i < 50; i++) {
		Write(message, messageLength, 1);
	}

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