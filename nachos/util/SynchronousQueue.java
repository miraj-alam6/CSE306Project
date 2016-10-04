package nachos.util;

import nachos.kernel.threads.*;
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
    boolean tryingToPut = true;
    boolean tryingToTake = true;
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
    }

    /**
     * Adds the specified object to this queue,
     * waiting if necessary for another thread to remove it.
     *
     * @param obj The object to add.
     */
    public boolean put(T obj) { 
	objectLock.P();
	dataAvail.V();
	sl.acquire();
	tryingToPut = true;
	object = obj;
	sl.release();
	objectLock.V();
	consumeAvail.P();
	tryingToPut = false;
	return true;
    }

    /**
     * Retrieves and removes the head of this queue,
     * waiting if necessary for another thread to insert it.
     *
     * @return the head of this queue.
     */
    public T take() {
	dataAvail.P();
	objectLock.P();
	sl.acquire();
	tryingToTake = true;
	T returnObj = object;
	object = null;	
	sl.release();
	objectLock.V();
	consumeAvail.V();
	tryingToTake = false;
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
	
	if(tryingToTake){
	    object = e;
	    dataAvail.V();
	    return true;
	}
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
	if(tryingToPut){

	    consumeAvail.V();
	    return object;
	}
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
    public boolean offer(T e, int timeout) {
	return false;
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
    public T poll(int timeout) {
	return null;
    }
    
    

}