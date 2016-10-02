package nachos.kernel.threads;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.CPU;
import nachos.machine.InterruptHandler;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.machine.Timer;
import java.util.*;



//This will be a class for Callout

public class Callout {
    
    //Put the queue  here    
    ArrayList<CalloutWithTime> scheduledCallouts;
    
    public Callout()
    {
	scheduledCallouts = new ArrayList<CalloutWithTime>();
	Timer timer = Machine.getTimer(0); //I don't think this would be good place idk
    }
    
//schedule    
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
	//Do semaphore P() here
	
	scheduledCallouts.add(new CalloutWithTime(runnable, ticksFromNow));
	//Invoke Sort list here
	
	//Do semaphore V() here
    }
    
    
    
    /**
     * Interrupt handler for the time-slice timer.  A timer is set up to
     * interrupt the CPU periodically (once every Timer.DefaultInterval ticks).
     * The handleInterrupt() method is called with interrupts disabled each
     * time there is a timer interrupt.
     */
    private static class TimerInterruptHandler implements InterruptHandler {

	/** The Timer device this is a handler for. */
	private final Timer timer;

	/**
	 * Initialize an interrupt handler for a specified Timer device.
	 * 
	 * @param timer  The device this handler is going to handle.
	 */
	public TimerInterruptHandler() {
	    this.timer =  Machine.getTimer(0);
	    
	}
	
	public TimerInterruptHandler(Timer timer) {
	    this.timer = timer;
	}

	public void handleInterrupt() {
	    Debug.println('i', "Timer interrupt: " + timer.name);
	    // Note that instead of calling yield() directly (which would
	    // suspend the interrupt handler, not the interrupted thread
	    // which is what we wanted to context switch), we set a flag
	    // so that once the interrupt handler is done, it will appear as 
	    // if the interrupted thread called yield at the point it is 
	    // was interrupted.
	    yieldOnReturn();
	}

	/**
	 * Called to cause a context switch (for example, on a time slice)
	 * in the interrupted thread when the handler returns.
	 *
	 * We can't do the context switch right here, because that would switch
	 * out the interrupt handler, and we want to switch out the 
	 * interrupted thread.  Instead, we set a hook to kernel code to be executed
	 * when the current handler returns.
	 */
	private void yieldOnReturn() {
	    Debug.println('i', "Yield on interrupt return requested");
	    CPU.setOnInterruptReturn
	    (new Runnable() {
		public void run() {
		    if(NachosThread.currentThread() != null) {
			Debug.println('t', "Yielding current thread on interrupt return");
			Nachos.scheduler.yieldThread();
		    } else {
			Debug.println('i', "No current thread on interrupt return, skipping yield");
		    }
		}
	    });
	}

    }


    private static class CalloutWithTime{
	Runnable actualCallout;
	int ticksFromNow;
	
	public CalloutWithTime(Runnable callout, int ticks){
	    actualCallout = callout;
	    ticksFromNow = ticks;
	    
	}

	
    }
    
}
