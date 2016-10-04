package nachos.kernel.threads.test;


import nachos.Debug;
import nachos.machine.NachosThread;
import nachos.kernel.Nachos;

public class CalloutTest implements Runnable{

 /** Integer identifier that indicates which thread we are. */
 private int which;
 private int numTicks;
 /**
  * Initialize an instance of ThreadTest and start a new thread running
  * on it.
  *
  * @param w  An integer identifying this instance of ThreadTest.
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
	for (int i = 1; i <= 5; i++) {
	    Debug.println('+', "*** thread " + which + " will go to sleep now. Will wake up in " + i * numTicks + " from now.");
	    //Debug.println('+', "" + Nachos.scheduler);
	    Nachos.scheduler.sleepThread(i * numTicks);
	    Debug.println('+', "*** thread " + which + "has woken up.");
	}
	
	//Nachos.scheduler.stopCalloutClock();
	Nachos.scheduler.finishThread();
	
 }
 
 /**
  * Entry point for the test.
  */
 public static void start() {
	Debug.println('+', "Entering CalloutTest");
	new CalloutTest(1, 28);
	new CalloutTest(2, 40);

 }

}
