package nachos.util;

import nachos.kernel.Nachos;
import nachos.kernel.threads.*;
import nachos.Debug;
/**
 * This class is patterned after the SynchronousQueue class
 * in the java.util.concurrent package.
 *
 * A SynchronousQueue has no capacity: each insert operation
 * must wait for a corresponding remove operation by another
 * thread, and vice versa.  A thread trying to insert an object
 * enters a queue with other such threads, where it waits to
 * be matched up with a thread trying to remove an object.
 * Similarly, a thread trying to remove an object enters a
 * queue with other such threads, where it waits to be matched
 * up with a thread trying to insert an object.
 * If there is at least one thread waiting to insert and one
 * waiting to remove, the first thread in the insertion queue
 * is matched up with the first thread in the removal queue
 * and both threads are allowed to proceed, after transferring
 * the object being inserted to the thread trying to remove it.
 * At any given time, the <EM>head</EM> of the queue is the
 * object that the first thread on the insertion queue is trying
 * to insert, if there is any such thread, otherwise the head of
 * the queue is null.
 */

public class SynchronousQueue<T> implements Queue<T> {
    
    Semaphore objectLock;
    Semaphore dataAvail;
    Semaphore consumeAvail;
    Semaphore producerLock;
    Semaphore consumerLock;
    Semaphore offerLock;
    Semaphore pollLock;
    boolean tryingToPut = false;
    boolean tryingToTake = false;
//    boolean gaveDataThruOffer = false; //If you give data through offer, do not
    T object;
    SpinLock sl;
    /**
     * Initialize a new SynchronousQueue object.
     */
    public SynchronousQueue() {
	sl = new SpinLock("SynchronousQueue mutex");
	
	objectLock = new Semaphore("objectLock", 1);
	dataAvail = new Semaphore("dataAvail",0);
	consumeAvail = new Semaphore("consumeAvail",0);
	producerLock = new Semaphore("producerLock", 1);
	consumerLock = new Semaphore("consumerLock", 1);
	offerLock = new Semaphore("offerLock", 1);
	pollLock = new Semaphore("pollLock", 1);
    }

    /**
     * Adds the specified object to this queue,
     * waiting if necessary for another thread to remove it.
     *
     * @param obj The object to add.
     */
    public boolean put(T obj) { 
	producerLock.P();
	tryingToPut = true;
	consumeAvail.P();
	object = obj;
	dataAvail.V();
	producerLock.V();
	return true;
    }

    /**
     * Retrieves and removes the head of this queue,
     * waiting if necessary for another thread to insert it.
     *
     * @return the head of this queue.
     */
    public T take() {
	consumerLock.P();
	Debug.println('+',"set trying to take to true");
	tryingToTake = true;
	consumeAvail.V();
	dataAvail.P();
	T returnObj = object;
	object = null;	
	consumerLock.V();
	return returnObj;
    }

    /**
     * Adds an element to this queue, if there is a thread currently
     * waiting to remove it, otherwise returns immediately.
     * 
     * @param e  The element to add.
     * @return  true if the element was successfully added, false if the element
     * was not added.
     */
    @Override
    public boolean offer(T e) {
	offerLock.P(); //to prevent concurrency messing with shared data
	Debug.println('m',"Reached here offer");
	if(tryingToTake){
	  //  Debug.println('+',"Reached here1");
	    object = e;
	    consumeAvail.P(); //there will be no waiting here because, only way you got in here is if
	    //the semaphore already has a value of 1
	    dataAvail.V();
	    offerLock.V(); //to prevent concurrency messing with shared data
	    tryingToTake = false;
	    return true;
	}
	//Debug.println('+',"Reached here2");
	offerLock.V(); //to prevent concurrency messing with shared data
	return false;
    }
    
    /**
     * Retrieves and removes the head of this queue, if another thread
     * is currently making an element available.
     * 
     * @return  the head of this queue, or null if no element is available.
     */
    @Override
    public T poll() { 
	pollLock.P();
	Debug.println('m', "trying" + tryingToPut);
	if(tryingToPut){
	    
	    consumeAvail.V();
	    dataAvail.P();//there is effectively no waiting here because, only way we got here is if  consumeAvail.P()
	    //was waiting in the put function, and just as .P() ends in the put function it will carry out
	    //dataAvail.V() thus, even though I wrote dataAvail.P() here, there is never any chance that we will
	    //idly waiting for it, thus it is a poll function, and not a take function
	    tryingToPut = false;
	    pollLock.V();
	    return object;
	}
	pollLock.V();
	return null;
    }
    
    /**
     * Always returns null.
     *
     * @return  null
     */
    @Override
    public T peek() { return null; }
    
    /**
     * Always returns true.
     * 
     * @return true
     */
    @Override
    public boolean isEmpty() { return true; }

    // The following methods are to be implemented for the second
    // part of the assignment.

    /**
     * Adds an element to this queue, waiting up to the specified
     * timeout for a thread to be ready to remove it.
     * 
     * @param e  The element to add.
     * @param timeout  The length of time (in "ticks") to wait for a
     * thread to be ready to remove the element, before giving up and
     * returning false.
     * @return  true if the element was successfully added, false if the element
     * was not added.
     */
    public boolean offer(T e, int timeout) 
    {
	
	boolean offered;
	
	Runnable scheduledCallout = new Runnable(){
		    @Override
		    public void run() {
			producerLock.V();
			return;
		    }
	};
	
	Nachos.scheduler.getCalloutF().schedule(scheduledCallout, timeout);
	
	producerLock.P();
	tryingToPut = true;
	consumeAvail.P();
	object = e;
	offered = true;
	dataAvail.V();
	producerLock.V();
	return offered;
    }
    
    /**
     * Retrieves and removes the head of this queue, waiting up to the
     * specified timeout for a thread to make an element available.
     * 
     * @param timeout  The length of time (in "ticks") to wait for a
     * thread to make an element available, before giving up and returning
     * true.
     * @return  the head of this queue, or null if no element is available.
     */
    public T poll(int timeout) 
    {
	
	Runnable scheduledCallout = new Runnable(){
		    @Override
		    public void run() {
			consumerLock.V();
			return;
		    }
	};
    
    Nachos.scheduler.getCalloutF().schedule(scheduledCallout, timeout);
    consumerLock.P();
    Debug.println('m',"set trying to take to true");
    tryingToTake = true;
    consumeAvail.V();
    dataAvail.P();
    T returnObj = object;
    object = null;	
    consumerLock.V();
    return returnObj;
    }
    
    

}