package nachos.kernel.threads;

import nachos.machine.Machine;

//This will be a class for Callout

public class Callout {
    
//Put the queue  here    
    
    public Callout()
    {
	Machine.getTimer(0);
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
	
    }

}
