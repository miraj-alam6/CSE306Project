#include "syscall.h"

int main()
{
	
	int i;
	char *message1 = "3: No loop started yet.\n\r";
	char *message2 = "3: First loop done. Should now be preempted\n\r";
	char *message3 = "3: Printed after second loop.\n\r";
	int messageLength = getStringSize(message1);
	PredictCPU(1000);
	
	Write(message1, messageLength, 1);
	for (i = 0; i < 1000; i++) {
		i += 1;
	}
	messageLength = getStringSize(message2);
	Write(message2, messageLength, 1);
	Exec("test/hw3testprog3_1");
	Exec("test/hw3testprog3_2");
	
	for (i = 0; i < 1000; i++) {
		i += 1;
	}
	messageLength = getStringSize(message3);
	Write(message3, messageLength, 1);
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