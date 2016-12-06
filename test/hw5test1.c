#include "syscall.h"

int main(int n)
{	/*Why does count being 285 to 286 freeze it*/
/*289 is limit where stack becomes too big*/
	int count = 288;
	int hugeArray[count];
	int i;
	PredictCPU(200);
	
	for (i = 0; i < count; i++) {
		hugeArray[i] = i;
	}
	Exit(0);
}
	