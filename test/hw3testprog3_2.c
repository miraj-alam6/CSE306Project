#include "syscall.h"

int main()
{
	
	int i;
	char *message1 = "3_2: Should print after 2nd loop in 3 in SJF, before 2nd in 3 in SRT\n\r";
	int messageLength = getStringSize(message1);
	PredictCPU(5);
	
	
	for (i = 0; i < 10; i++) {
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