package nachos.kernel.userprog;

import nachos.Debug;
import nachos.machine.NachosThread;

public class HRRNQueue implements UPList{

    @Override
    public UserThread getNextProcess() {
	// TODO Auto-generated method stub
	UserThread nextProcess = null;
	// TODO Auto-generated method stub
	
	for(int i = 0 ; i < userThreads.size(); i++){
	    if(userThreads.get(i).getTicksLeft() <= -1 || userThreads.get(i).getTicksWaiting() <= -1){
		nextProcess = userThreads.get(i);
		return nextProcess;
	    }
	}
	
	if(userThreads.size() == 0){
	    return null;
	}
	
	int smallestIndex = 0;
	UserThread highestRatioThread = userThreads.get(0);
	for(int i = 1; i < userThreads.size(); i++){
	    int tRatio = (highestRatioThread.getTicksLeft()+highestRatioThread.getTicksWaiting())/highestRatioThread.getTicksLeft();
	    int ratio = (userThreads.get(i).getTicksLeft()+userThreads.get(i).getTicksWaiting())/userThreads.get(i).getTicksLeft();
	    if (ratio > tRatio){
		highestRatioThread = userThreads.get(i);
		smallestIndex = i;
	    }
	}
	
	if(NachosThread.currentThread() == highestRatioThread){
	    nextProcess = null;
	}
	else{
	    nextProcess = highestRatioThread;
	}
	
	if(nextProcess != null){
	    nextProcess.resetWaitingTime();
	}
	return nextProcess;
    }

    @Override
    public void finishThread(int spaceID) {
	// TODO Auto-generated method stub
	for(int i =0; i < userThreads.size(); i++){
	    	if(userThreads.get(i).space.getSpaceID() == spaceID){
	    	    userThreads.remove(i);
	    	    Debug.println('q', "In HRRN size is " + userThreads.size());
	    	} 
	}	
    }

    @Override
    public void addProcess(UserThread uT) {
	userThreads.add(uT);
	Debug.println('q', "In HRRN size is " + userThreads.size());
    }
    
    @Override
    public void addTime(int ticksToAdd) {
	for(int i = 0; i < userThreads.size(); i++){
	    if(userThreads.get(i) != NachosThread.currentThread() ){
		userThreads.get(i).addWaitingTime(ticksToAdd);
	    }
	}
	
    }

    @Override
    public void removeProcess(UserThread uT) {
    }

}
