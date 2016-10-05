package nachos.kernel.threads.test;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.NachosThread;



/*
 * To test if put and take works properly
 */
public class POCTest implements Runnable{
    
    //Current thread we are in
    private int threadNum;
    //producer or consumer thread -- 1 for producer --- 2 for consumer
    private int timeoutT;
    private int threadType;
    
    public POCTest(int th_num, int timeout,int pc)
    {
	threadNum = th_num;
	this.timeoutT = timeout;
	this.threadType = pc;
	NachosThread th = new NachosThread("Test thread " + th_num, this);
	Nachos.scheduler.readyToRun(th);
    }
    
    public void run()
    {
	//if thread is producer
	if(threadType == 1)
	{
	    Integer s = new Integer(threadNum + 5);
	    Debug.println('z', "*** thread " + threadNum + " will call put and add" + s);
	    boolean success = Nachos.scheduler.getSyncQ().put(s);
	    Debug.println('z',  "*** thread " + threadNum + " Successfully called put and added = " + success);
	}
	//if thread is consumer
	else if(threadType == 2)
	{
	    Debug.println('z', "*** thread " + threadNum + " will call take");
	    Integer s = Nachos.scheduler.getSyncQ().take();
	    Debug.println('z', "*** thread " + threadNum + " retrieved" + s);
	}	
	//This is an offer
	else if (threadType == 3){
	   
	    Integer s = new Integer(threadNum + 5);
	    Debug.println('z', "*** thread " + threadNum + " will offer the Integer "+s);
	    boolean success = Nachos.scheduler.getSyncQ().offer(s,timeoutT);
	    if(success){
		Debug.println('z',  "*** thread " + threadNum + " Successfully called put and added = " + success);
		Debug.println('m', "A thread was trying to take the object, so thread " +threadNum
			+ " gave Integer " + s + " to it");
	    }
	    else{
		Debug.println('z',  "*** thread " + threadNum + " Successfully called put and added = " + success);
		Debug.println('m', "No thread trying to take the object, so thread " +threadNum
			+ " did not give Integer " + s + " to anything");
	    }
	}
	
	//This is a poll
		else if (threadType == 4){
		    
		    Debug.println('z', "*** thread " + threadNum + " will poll for an object(an Integer in this test).");
		    Integer s = Nachos.scheduler.getSyncQ().poll(timeoutT);
		    if(s != null){
			Debug.println('z',  "*** thread " + threadNum + " Retreived: " + s);
			Debug.println('m', "A thread was trying to put Integer " + s  + 
				"so thread " + threadNum + " took it with poll function.");
		    }
		    else{
			Debug.println('z',  "*** thread " + threadNum + " Retreived: " + s);
			Debug.println('m', "No thread trying to put an object so poll did not get anything.");
		    }
		}
	Debug.println('m', "wat");
	Nachos.scheduler.finishThread();
    }

    
    /**
     * Entry point for the test.
     */
    public static void start() {
   	Debug.println('+', "Entering POCTest");
   //	new POCTest(7, 2); // Thread 7 will execute take
  // 	new POCTest(8, 3); //Thread 8 will execute offer(new Integer(8 + 5))
  // 	new POCTest(10, 3); //Thread 10 will execute offer(new Integer(10 + 5))
  // 	new POCTest(9, 1); //Thread 9 will execute put(new Integer(9 + 5))
  // 	new POCTest(11, 4); //Thread 11 will execute poll()
  // 	new POCTest(12, 4); //Thread 8 will execute poll()
   	
   	/*new POCTest(1,60,2);
   	new POCTest(2,120,3);
   	new POCTest(3,100,1);
   	new POCTest(4,90,4);*/
  	new POCTest(5,50,1);
   	new POCTest(6,50,4);
   	new POCTest(7,150,3);
   	//new POCTest(8,210,3);

    }
}
