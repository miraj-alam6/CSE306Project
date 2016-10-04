package nachos.kernel.threads.test;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.NachosThread;

public class SynchronousQueueTest implements Runnable{
    
    //Current thread we are in
    private int threadNum;
    //producer or consumer thread -- 1 for producer --- 2 for consumer
    private int porc;
    
    public SynchronousQueueTest(int th_num, int pc)
    {
	threadNum = th_num;
	this.porc = pc;
	NachosThread th = new NachosThread("Test thread " + th_num, this);
	Nachos.scheduler.readyToRun(th);
    }
    
    public void run()
    {
	//if thread is producer
	if(porc == 1)
	{
	    Integer s = new Integer(threadNum + 5);
	    Debug.println('+', "*** thread " + threadNum + " will call put and add" + s);
	    Nachos.scheduler.getSyncQ().put(s);
	}
	//if thread is consumer
	else if(porc == 2)
	{
	    Debug.println('+', "*** thread " + threadNum + " will call take");
	    Integer s = Nachos.scheduler.getSyncQ().take();
	    Debug.println('+', "*** thread " + threadNum + " retrieved" + s);
	}
	
	
	Nachos.scheduler.finishThread();
    }

    
    /**
     * Entry point for the test.
     */
    public static void start() {
   	Debug.println('+', "Entering SynchronousQueueTest");
   	
   	new SynchronousQueueTest(3, 2);
   	new SynchronousQueueTest(1, 1);
   	   	
   	new SynchronousQueueTest(5, 2);
   	new SynchronousQueueTest(6, 2);
   	new SynchronousQueueTest(2, 1);
   	new SynchronousQueueTest(4, 1);

    }
}
