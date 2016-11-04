package nachos.kernel.userprog;

import nachos.Debug;

public class FCFSQueue implements UPList {

    @Override
    public UserThread getNextProcess() {
	// TODO Auto-generated method stub
	if(userThreads.size() == 0){
	    return null;
	}
	UserThread nextProcess = userThreads.get(0);
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

}
