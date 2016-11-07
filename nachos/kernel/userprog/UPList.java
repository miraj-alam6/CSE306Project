package nachos.kernel.userprog;
import java.util.PriorityQueue;
import java.util.ArrayList;

//UPList: UserProcess List
public interface UPList {
    
    public ArrayList<UserThread> userThreads = new ArrayList<UserThread>();
    //This will set the next process that should run, which is
    //not necessarily the next one in the basic list. It will
    //depend on which scheduling process implements it
    
    public UserThread getNextProcess();
    public void finishThread(int spaceID);
    public void addProcess(UserThread uT);
    public void addTime(int ticksToAdd);
}

