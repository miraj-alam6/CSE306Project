package nachos.kernel.threads.test;


import nachos.Debug;
import nachos.machine.NachosThread;
import nachos.kernel.Nachos;

public class CalloutTest implements Runnable{

 /** Integer identifier that indicates which thread we are. */
 private int which;

 /**
  * Initialize an instance of ThreadTest and start a new thread running
  * on it.
  *
  * @param w  An integer identifying this instance of ThreadTest.
  */
 public CalloutTest(int w) {
	which = w;
	NachosThread t = new NachosThread("Test thread " + w, this);
	Nachos.scheduler.readyToRun(t);
 }

 /**
  * Loop 5 times, yielding the CPU to another ready thread 
  * each iteration.
  */
 public void run() {
	for (int num = 0; num < 5; num++) {
	    Debug.println('+', "*** thread " + which + " looped " + num + " times");
	    Nachos.scheduler.yieldThread();
	}
	Nachos.scheduler.finishThread();
 }
 
 /**
  * Entry point for the test.
  */
 public static void start() {
	Debug.println('+', "Entering ThreadTest");
	new ThreadTest(1);
	new ThreadTest(2);
 }

}
