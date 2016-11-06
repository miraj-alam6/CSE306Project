#include "syscall.h"

int main()
{
	PredictCPU(2);
	Exec("test/hw3testprog1_1");
	Exec("test/hw3testprog1_2");
	Exec("test/hw3testprog1_3");
	return 0;
}