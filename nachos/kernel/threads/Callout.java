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
    
    //Put the queue  here    
    PriorityQueue<CalloutWithTime> scheduledCallouts;
    int startTime;
    int elapsedTime;
    Timer timer;
    Semaphore s; // ASK is it okay to have a semaphore here to prevent interrupts when scheduling?
    /** Spin lock for mutually exclusive access to scheduler state. */
    SpinLock sl;
    public Callout()
    {
	//startTime = Simulation.currentTime();  //Gets the simulated time
	//elapsedTime = startTime;
	
	elapsedTime = startTime = 0;
	scheduledCallouts = new PriorityQueue<CalloutWithTime>(new CalloutComparator());
	s = new Semaphore("calloutSem",1);
	sl = new SpinLock("callout mutex");
	timer = Machine.getTimer(0); 
	timer.setHandler(new CalloutTimerInterruptHandler(timer));
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
	   
	   //elapsedTime = Simulation.currentTime() - startTime;
	   elapsedTime += 100; // Or maybe do elapsedTime += timer.interval;
	   

	   //Process all requests that have to be done.
	   //First check if there are any scheduled callouts with shortcircuiting.
	   sl.acquire();
	   while(scheduledCallouts.peek()!= null && 
		   scheduledCallouts.peek().getScheduledTimeToCallout() <= elapsedTime){
	       
	       //Nachos.scheduler.makeReady(	);nachos thread please
	       //scheduledCallouts.poll().getActualCallout().start(); // #ASK: why doesn't this work, is next line
	       						 	   //what I should do instead
	       sl.release(); //I think this will be a good place to release the spinlock because it is before run()
	       //critical section should be over by this time.
	       
	       scheduledCallouts.poll().getActualCallout().run(); // ASK: is this good enough? Should run with
	       							 //interrupts disabled
	       Debug.println('+', "*** at ticks " + elapsedTime + ", a callout has occured.");
	       sl.acquire(); //because the loop will check the conditions again.
	   } 
	   sl.release();
	   //run is called with spinlock held, so not the right place, change it.
	}
	

    }


    private class CalloutWithTime{
	private Runnable actualCallout;
	private int ticksFromNow;
	private int scheduledTimeToCallout;
	
	//comparator does not use this function. 
	public int getTicksFromNow(){
	    
	    return ticksFromNow;
	}
	
	//comparator uses this function
	public int getScheduledTimeToCallout(){
	    
	    return scheduledTimeToCallout;
	}
	
	public Runnable getActualCallout(){
	    
	    return actualCallout;
	}
	public CalloutWithTime(Runnable callout, int ticks){
	    actualCallout = callout;
	    ticksFromNow = ticks;
	    
	    scheduledTimeToCallout = elapsedTime + ticks;
	    
	    
	}


	
    }
    
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
