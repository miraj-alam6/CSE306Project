/* testprog4.c

*/

#include "syscall.h"
char *message = "This is a message. In Data section";
/*Length of above message is 35 including null pointer*/
int main()
{

	Write(message, 35, 1);

	return 0;
}
