package nachos.kernel.threads;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.CPU;
import nachos.machine.InterruptHandler;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.machine.Simulation;
import nachos.machine.Timer;

import java.util.*;



//This will be a class for Callout

public class Callout {
    
    //PriorityQueue of callouts with the priority being on which callout should be made first
    PriorityQueue<CalloutWithTime> scheduledCallouts;
    //startTime of the machine
    int startTime;
    //total elapsed time of the machine
    int elapsedTime;
    //the timer to handle the interrupts for callout
    Timer timer;
    Semaphore s; 
    /** Spin lock for mutually exclusive access to scheduler state. */
    SpinLock sl;
    
    /*
     * Initialize everything here and start the timer when callout is created
     */
    public Callout()
    {
	elapsedTime = startTime = 0;
	//Create the priorityqueue with a comparator that compares the number of ticksFromNow between callouts being added
	scheduledCallouts = new PriorityQueue<CalloutWithTime>(new CalloutComparator());
	s = new Semaphore("calloutSem",1);
	sl = new SpinLock("callout mutex");
	timer = Machine.getTimer(0); 
	timer.setHandler(new CalloutTimerInterruptHandler(timer));
	//timer.start(); //commented this out for hw # 2
    }
    
    
    /**
     * Schedule a callout to occur at a specified number of
     * ticks in the future.
     *
     * @param runnable  A Runnable to be invoked when the specified
     * time arrives.
     * @param ticksFromNow  The number of ticks in the future at
     * which the callout is to occur.
     */
    public void schedule(Runnable runnable, int ticksFromNow)
    {
	
	//Do semaphore P() here, stop interrupts.
	int oldLevel = CPU.setLevel(CPU.IntOff);
	 
	 s.P();    
	 sl.acquire(); //Want to hold spin lock for as little time as possible
	
	//Since this is a priority queue, simply adding the callout will lead to
	//the next callout to be at the front.
	scheduledCallouts.add(new CalloutWithTime(runnable, ticksFromNow));

	//Do semaphore V() here, critical section over
	
	sl.release(); 
	s.V();
	CPU.setLevel(oldLevel);
	
    }
    
   
    
    /**
     * Interrupt handler for the time-slice timer.  A timer is set up to
     * interrupt the CPU periodically (once every Timer.DefaultInterval ticks).
     * The handleInterrupt() method is called with interrupts disabled each
     * time there is a timer interrupt.
     * Default Interval is 100 already as indicated by the class File for Timer.
     */
    private class CalloutTimerInterruptHandler implements InterruptHandler {

	/** The Timer device this is a handler for. */
	private final Timer timer;

	/**
	 * Initialize an interrupt handler for a specified Timer device.
	 * 
	 * @param timer  The device this handler is going to handle.
	 */
	
	
	public CalloutTimerInterruptHandler(Timer timer) {
	    this.timer = timer;
	}

	public void handleInterrupt() {
	    if(scheduledCallouts.peek() == null){
		       timer.stop();
		   }
	    
	    //add 100 to elapsed time because timer calls handleinterrupt in intervals of 100 ticks
	   elapsedTime += 100; 
	   

	   //Process all requests that have to be done.
	   //First check if there are any scheduled callouts with shortcircuiting.
	   sl.acquire();
	   while(scheduledCallouts.peek()!= null && 
		   scheduledCallouts.peek().getScheduledTimeToCallout() <= elapsedTime){
	       
	       sl.release(); //I think this will be a good place to release the spinlock because it is before run()
	       //critical section should be over by this time.
	       
	       scheduledCallouts.poll().getActualCallout().run();
	       
	       Debug.println('z', "*** at ticks " + elapsedTime + ", a callout has occured.");
	       sl.acquire(); //because the loop will check the conditions again.
	   } 
	   sl.release();	   
	}
	
    }

    /*
     * Callout class
     */
    private class CalloutWithTime{
	private Runnable actualCallout;
	private int scheduledTimeToCallout;
	
	//comparator uses this function
	public int getScheduledTimeToCallout(){
	    
	    return scheduledTimeToCallout;
	}
	
	public Runnable getActualCallout(){
	    
	    return actualCallout;
	}
	
	public CalloutWithTime(Runnable callout, int ticks){
	    actualCallout = callout;
	    scheduledTimeToCallout = elapsedTime + ticks;
	        
	}	
    }
    
    /*
     * Comparator for priorityqueue
     * Checks the difference in ticksFromNow of each Callout
     */
    private static class CalloutComparator implements Comparator<CalloutWithTime>{

	@Override
	public int compare(CalloutWithTime c1, CalloutWithTime c2) {
	   if(c1.getScheduledTimeToCallout() > c2.getScheduledTimeToCallout()){
	       return 1;
	   }
	   else if (c1.getScheduledTimeToCallout() < c2.getScheduledTimeToCallout()){
	       return -1;
	   }
	   else{
	       return 0;
	   }
	}
	
	
    }
    
}
