#include "syscall.h"

int main()
{
	
	int i;
	char *message1 = "3_1: This should print last in both SJF and SRT\n\r";

	int messageLength = getStringSize(message1);
	PredictCPU(1050);
	
	
	for (i = 0; i < 1000; i++) {
		i += 1;
	}
	Write(message1, messageLength, 1);
	
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