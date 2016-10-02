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
    long startTime;
    long elapsedTime;
    Timer timer;
    Semaphore s;
    
    public Callout()
    {
	startTime = Simulation.currentTime();  //Gets the simulated time
	elapsedTime = startTime;
	scheduledCallouts = new PriorityQueue<CalloutWithTime>(new CalloutComparator());
	s = new Semaphore("calloutSem",1);
	//timer = Machine.getTimer(0); 
//	timer.setHandler(new TimerInterruptHandler(timer));
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
	s.P();
	
	//Since this is a priority queue, simply adding the callout will lead to
	//the next callout to be at the front.
	scheduledCallouts.add(new CalloutWithTime(runnable, ticksFromNow));

	//Do semaphore V() here, critical section over
	s.V();
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
	public CalloutTimerInterruptHandler() {
	    this.timer =  Machine.getTimer(0);
	   
	}
	
	public CalloutTimerInterruptHandler(Timer timer) {
	    this.timer = timer;
	}

	public void handleInterrupt() {
	   
	   elapsedTime = Simulation.currentTime() - startTime;
	   
	   //ticksFromNow 20 < elapsedTime = 34
	   //Process all requests that have to be done.
	   //First check if there are any scheduled callouts with shortcircuiting.
	   while(scheduledCallouts.peek()!= null && 
		   scheduledCallouts.peek().getScheduledTimeToCallout() <= elapsedTime){
	      
	       Nachos.scheduler.makeReady(	);
	       //scheduledCallouts.poll().getActualCallout()
	       //scheduledCallouts.poll().getActualCallout()
	   } 
	}


    }


    private static class CalloutWithTime{
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
	    scheduledTimeToCallout = Simulation.currentTime() + ticks;
	    
	    
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
