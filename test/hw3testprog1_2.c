#include "syscall.h"

int main()
{
	int i;
	char *message = "Message from hw3testprog1_2\n\r";
	int messageLength = getStringSize(message);

	for (i = 0; i < 2; i++) {
		Write(message, messageLength, 1);
	}
	PredictCPU(50);/* Why doesn't this work*/
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