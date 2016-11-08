#include "syscall.h"

int main()
{
	
	int i;
	char *message = "2_1: hw3testprog2_1 done executing\n\r";
	int messageLength = getStringSize(message);
	PredictCPU(1000);
	/*Under SJF hw3testprog2_3 should run before hw3testprog2_2, but
	under HRRN testprog2_2 should run before because of how long it has been
	waiting*/
	
	Exec("test/hw3testprog2_2");
	Exec("test/hw3testprog2_4");
	
	for (i = 0; i < 10000; i++) {
		i += 1;
	}
	
	Exec("test/hw3testprog2_3");
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