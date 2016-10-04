package nachos.kernel.threads.test;


import nachos.Debug;
import nachos.machine.NachosThread;
import nachos.kernel.Nachos;

public class CalloutTest implements Runnable{

 /** Integer identifier that indicates which thread we are. */
 private int which;
 private int numTicks;
 
 /**
  * Initialize an instance of CalloutTest and start a new thread running
  * on it.
  *
  * @param w  An integer identifying this instance of CalloutTest.
  */
 public CalloutTest(int w, int numTicks) {
	which = w;
	this.numTicks = numTicks;
	NachosThread t = new NachosThread("Test thread " + w, this);
	Nachos.scheduler.readyToRun(t);

 }

 /**
  * Schedule many callouts that will wake up your threads after they go to sleep.
  *
  */
 public void run() {
	for (int i = 1; i <= 4; i++) {
	    Debug.println('z', "*** thread " + which + " will go to sleep now. Will wake up in " + i * numTicks + " from now.");
	    Nachos.scheduler.sleepThread(i * numTicks);
	    Debug.println('z', "*** thread " + which + "has woken up.");
	}
	
	//End the thread
	Nachos.scheduler.finishThread();
	
 }
 
 /**
  * Entry point for the test.
  */
 public static void start() {
	Debug.println('+', "Entering CalloutTest");
	new CalloutTest(1, 28);
	new CalloutTest(2, 63);
	new CalloutTest(3, 129);
	new CalloutTest(4, 250);

 }

}
