#include "syscall.h"

int main()
{
	
	int i;
	char *message = "Message from hw3testprog1_3\n\r";
	int messageLength = getStringSize(message);

	PredictCPU(125);
	
	for (i = 0; i < 5; i++) {
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