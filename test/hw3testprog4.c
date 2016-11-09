#include "syscall.h"

int main(int n)
{	
	int i;
	PredictCPU(12 * n);
	
	for (i = 0; i < n; i++) {
		;
	}
	Exit(0);
}
	