#include "syscall.h"

int main()
{
	
	int i;
	char *message = "This should print first in both SJF and HRRN because small CPU time,long wait\n\r";
	int messageLength = getStringSize(message);
	PredictCPU(10);
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