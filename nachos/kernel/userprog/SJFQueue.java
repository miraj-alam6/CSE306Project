package nachos.kernel.userprog;

import nachos.Debug;
import nachos.machine.NachosThread;

public class SJFQueue implements UPList{
    @Override
    public UserThread getNextProcess() {
	// TODO Auto-generated method stub
	UserThread nextProcess = null;
	Debug.println('+', "Reached here");
	
	//New Stuff start here
	//First check if anything has not had its
	//ticks left set yet so that it becomes the next thread to run.
	for(int i = 0 ; i < userThreads.size(); i++){
	    if(userThreads.get(i).getTicksLeft() <= -1){
		nextProcess = userThreads.get(i);
		return nextProcess;
	    }
	}
	//New Stuff end here
	/**/
	
	
	//If you got here in the code that means ticks left have been calculated
	//for all the user threads.
	if(userThreads.size() == 0){
	    return null;
	}
	
	int smallestIndex = 0;
	UserThread smallestTimeThread = userThreads.get(0);
	for(int i = 1; i < userThreads.size(); i++){
	    if (userThreads.get(i).getTicksLeft() < smallestTimeThread.getTicksLeft()){
		smallestTimeThread = userThreads.get(i);
		smallestIndex = i;
	    }
	}
	if(NachosThread.currentThread() == smallestTimeThread){
	    nextProcess = null;
	}
	else{
	    nextProcess = smallestTimeThread;
	}
	
	return nextProcess;
    }

    @Override
    public void finishThread(int spaceID) {
	// TODO Auto-generated method stub
	for(int i =0; i < userThreads.size(); i++){
	   if(userThreads.get(i).space.getSpaceID() == spaceID){
	       userThreads.remove(i);
	       Debug.println('q', "In SJF size is " + userThreads.size());
	   } 
	    
	}
    }

    @Override
    public void addProcess(UserThread uT) {
	userThreads.add(uT);
	Debug.println('+', "In SJF size is " + userThreads.size());
    }

    @Override
    public void addTime(int ticksToAdd) {
	//SJF doesn't need to do anything for here
	
    }

    @Override
    public void removeProcess(UserThread uT) {
    }
    
    public void reduceTicksWaiting(int ticks) {
	
    }
}
