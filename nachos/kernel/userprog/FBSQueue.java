package nachos.kernel.userprog;


import java.util.ArrayList;

import nachos.Debug;
import nachos.machine.NachosThread;
import nachos.util.FIFOQueue;
import nachos.util.Queue;

public class FBSQueue implements UPList {

    private int[] quantums = {1000,900,800,700,600};
    private ArrayList<Queue<UserThread>> queues = new ArrayList<Queue<UserThread>>();
    
    public FBSQueue()
    {
	Queue<UserThread> queue1 = new FIFOQueue<UserThread>();
	Queue<UserThread> queue2 = new FIFOQueue<UserThread>();
	Queue<UserThread> queue3 = new FIFOQueue<UserThread>();
	Queue<UserThread> queue4 = new FIFOQueue<UserThread>();
	Queue<UserThread> queue5 = new FIFOQueue<UserThread>();
	queues.add(queue1);
	queues.add(queue2);
	queues.add(queue3);
	queues.add(queue4);
	queues.add(queue5);
	
    }
    
    public ArrayList<Queue<UserThread>> getFBSQueues()
    {
	return queues;
    }
    
    public int[] getQuantums()
    {
	return quantums;
    }
    
    @Override
    public UserThread getNextProcess() {
	// TODO Auto-generated method stub
	UserThread nextProcess = null;
	//Debug.println('+', "Reached here");

	    if(queues.get(0).peek() != null)
	    {
		nextProcess = queues.get(0).peek();
		nextProcess.setIsRunning(true);
		Debug.println('q',"In first queue");
	    }
	    else
	    {
		if(queues.get(1).peek() != null)
		{
		    nextProcess = queues.get(1).peek();
		    nextProcess.setIsRunning(true);
		    Debug.println('q',"In 2nd queue");
		}
		else
		{
		    if(queues.get(2).peek() != null)
		    {
			nextProcess = queues.get(2).peek();
			nextProcess.setIsRunning(true);
			Debug.println('q',"In 3rd queue");
		    }
		    else
		    {
			if(queues.get(3).peek() != null)
			{
			    nextProcess = queues.get(3).peek();
			    nextProcess.setIsRunning(true);
			    Debug.println('q',"In fourth queue");
			}
			else
			{
			    if(queues.get(4).peek() != null)
			    {
				nextProcess = queues.get(4).peek();
				nextProcess.setIsRunning(true);
				Debug.println('q',"In fifth queue");
			    }
			}
		    }
		}
	    }
	
	
	return nextProcess;
    }

    @Override
    public void finishThread(int spaceID) {
	// TODO Auto-generated method stub
	for(int i = 0; i < queues.size(); i++){
	   if(queues.get(i).peek() != null)
	   {
	       if(queues.get(i).peek().space.getSpaceID() == spaceID){
		   queues.get(i).poll();
	       }
	   }
	}
    }

    @Override
    public void addProcess(UserThread uT) {
	queues.get(0).offer(uT);
	Debug.println('q', "In FSB size is " + queues.get(0).peek().name);
    }

    @Override
    public void addTime(int ticksToAdd) {
	//FCFS doesn't need to do anything for here
	
    }

    @Override
    public void removeProcess(UserThread uT) {
	// TODO Auto-generated method stub
    }

}
