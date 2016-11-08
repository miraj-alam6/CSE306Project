package nachos.kernel.userprog;

import nachos.Debug;
import nachos.machine.NachosThread;

public class FBSQueue implements UPList {

    
    @Override
    public UserThread getNextProcess() {
	// TODO Auto-generated method stub
	UserThread nextProcess = null;
	//Debug.println('+', "Reached here");
	
	//New Stuff start here
	//First check if anything has not had its
	//ticks left set yet so that it becomes the next thread to run.
	/* Don't need to do this for FCFS
	for(int i = 0 ; i < userThreads.size(); i++){
	    if(userThreads.get(i).getTicksLeft() <= -1){
		nextProcess = userThreads.get(i);
		return nextProcess;
	    }
	}
	//New Stuff end here
	/**/
	
	if(userThreads.size() == 0){
	    return null;
	}
	if(NachosThread.currentThread() instanceof UserThread){
	    if(userThreads.size() == 1 && 
		NachosThread.currentThread() == userThreads.get(0)){
		return nextProcess;
	    }
	    else if(NachosThread.currentThread() == userThreads.get(0)){
		nextProcess = userThreads.get(1);
	    }
	    else{
		
		nextProcess = userThreads.get(0);
	    }
	    
	}
	else{
	    nextProcess = userThreads.get(0);
	}
	return nextProcess;
    }

    @Override
    public void finishThread(int spaceID) {
	// TODO Auto-generated method stub
	for(int i =0; i < userThreads.size(); i++){
	   if(userThreads.get(i).space.getSpaceID() == spaceID){
	       userThreads.remove(i);
	       Debug.println('q', "In FCFS size is " + userThreads.size());
	   } 
	    
	}
    }

    @Override
    public void addProcess(UserThread uT) {
	userThreads.add(uT);
	Debug.println('q', "In FCFS size is " + userThreads.size());
    }

    @Override
    public void addTime(int ticksToAdd) {
	//FCFS doesn't need to do anything for here
	
    }

    @Override
    public void removeProcess(UserThread uT) {
	// TODO Auto-generated method stub
	userThreads.remove(uT);
    }

}
