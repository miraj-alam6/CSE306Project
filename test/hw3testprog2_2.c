#include "syscall.h"

int main()
{
	
	int i;
	char *message = "2_2: Should print before 2_3 in HRRN because waited long enough\n\r";
	int messageLength = getStringSize(message);
	PredictCPU(55);
	
	for(i = 0; i < 100; i++){
		i++;
	}
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