package nachos.kernel.userprog;

import nachos.Debug;
import nachos.machine.NachosThread;

public class HRRNQueue implements UPList{

    @Override
    public UserThread getNextProcess() {
	// TODO Auto-generated method stub
	UserThread nextProcess = null;
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
	// TODO Auto-generated method stub
	int ratio = (uT.getTicksLeft()+uT.getTicksWaiting())/uT.getTicksLeft();
	
	int count = 0;
	while(count < userThreads.size())
	{
	    UserThread temp = userThreads.get(count);
	    int tRatio = (temp.getTicksLeft()+temp.getTicksWaiting())/temp.getTicksLeft();
	    if(tRatio > ratio)
	    {
		count++;
	    }
	    else if(tRatio < ratio && count == 0)
	    {
		userThreads.add(1,uT);
		break;
	    }
	    else
	    {
		userThreads.add(count+1,uT);
		break;
	    }
	}
	Debug.println('q', "In HRRN size is " + userThreads.size());
    }

}
