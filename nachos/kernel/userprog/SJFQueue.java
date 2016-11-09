package nachos.kernel.userprog;

import nachos.Debug;
import nachos.machine.CPU;
import nachos.machine.Machine;
import nachos.machine.NachosThread;

public class SJFQueue implements UPList{
    
    //Using arrays to account for multiple cpus
    private int currentIndices[];   
    //This will only be true if a new process was created
    //every CPU interrupt this will be set back to false
    public boolean considerYieldings[];
    
    public SJFQueue(){
	currentIndices = new int[Machine.NUM_CPUS];
	considerYieldings = new boolean[Machine.NUM_CPUS];
	//Initialize
	for(int i = 0; i < currentIndices.length; i ++){
	    currentIndices[i] = -1;
	}
	for(int j = 0; j < considerYieldings.length; j++){
	    considerYieldings[j] = false;
	}
	//TODO: use this laterCPU.currentCPU().unit
    }
    @Override
    public UserThread getNextProcess() {
	// TODO Auto-generated method stub
	UserThread nextProcess = null;
	//Debug.println('+', "Reached here");
	
	//New Stuff start here
	//First check if anything has not had its
	//ticks left set yet so that it becomes the next thread to run.
	for(int i = 0 ; i < userThreads.size(); i++){
	    if(userThreads.get(i).getTicksLeft() <= -1){
		nextProcess = userThreads.get(i);
		currentIndices[CPU.currentCPU().unit] = i;
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
	      // Debug.println('q', "In SJF size is " + userThreads.size());
	   } 
	    
	}
    }

    @Override
    public void addProcess(UserThread uT) {
	considerYieldings[CPU.currentCPU().unit] = true;
	userThreads.add(uT);
	//Debug.println('+', "In SJF size is " + userThreads.size());
    }

    @Override
    public void addTime(int ticksToAdd) {
	//SJF doesn't need to do anything for here
	
    }

    @Override
    public void removeProcess(UserThread uT) {
    }
    
    public boolean getConsiderYielding(){
	return considerYieldings[CPU.currentCPU().unit];
    }
    public void setConsiderYielding(boolean b){
	considerYieldings[CPU.currentCPU().unit] = b;
    }
    
    //CPU timer in scheduler will get this and decide to yield if
    //this says true
    public boolean shouldYield(UserThread currentThread){
	//if(currentIndices[CPU.currentCPU().unit] != -1){	    
	for(int i = 0 ; i < userThreads.size(); i++){
	    if(userThreads.get(i) != currentThread){
		if(userThreads.get(i).getTicksLeft() < currentThread.getTicksLeft()){
		    currentIndices[CPU.currentCPU().unit] = i;
		    return true;
		}
	    }
	   // }
	    return false;
	}
	return false;
    }
    public void reduceTicksWaiting(int ticks) {
	
    }
}
