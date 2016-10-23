package nachos.kernel.userprog;

import java.util.*;

import nachos.Debug;
import nachos.kernel.threads.Lock;

public class PMM {
    public PMEntry physicalPages[];
    private Lock allocatorLock;
    
    public PMM (int numPhysicalPages){
	physicalPages = new PMEntry[numPhysicalPages];
	allocatorLock = new Lock("allocatorLock");
	//need to initialize all the elements because they are objects and need
	//to initliaze or else null pointer exception
	for(int i = 0; i < physicalPages.length; i++){
	    physicalPages[i] = new PMEntry();
	}
    }

    //This is to allocate physical memory page for a process that is not
    //trying to share with an existing physical memory. PMP:Physical Memory Page
    //The return value is the PPN. If -1 is returned that means there is no
    //more avaliable physical memory.
    //The parameter is the VPN
    public int allocatePMP(int VPN){
	//Need to use a Lock here
	allocatorLock.acquire();
	for (int i = 0; i < physicalPages.length; i += 1){
	    if(physicalPages[i].entryStatus == 0){
		physicalPages[i].setPage(VPN);
		//Need to free a lock here
		Debug.println('a', "Successfully allocated a PPN " + i + " to VPN " + VPN
			+ "of thread " );
		allocatorLock.release();
		return i;
	    }	    
	}
	
	//Need to free a Lock here
	allocatorLock.release();
	return -1;
	
    }
    //Physical Memory Entry
    private class PMEntry{
	public int entryStatus; // 0 means free, 1 means occupied, other numebers for future use
	public ArrayList<Integer> virtualPages;  //List of all virtual pages that refer to it
	//not sure if above is flawed or not, because two different processes may have same
	//virtual page and map to same physical page if it is shared data.
	public boolean shareAllowed;
	public PMEntry(){
	    entryStatus = 0;
	    virtualPages = new ArrayList<Integer>();
	    shareAllowed = false;
	}
	public PMEntry(boolean canShare){
	    entryStatus = 0;
	    virtualPages = new ArrayList<Integer>();
	    shareAllowed = canShare;
	}
	//correspond to this physical page. 
	public void setPage(int virtualPageNumber){
	   virtualPages.add(virtualPageNumber);
	   entryStatus = 1;
	}
	
    }
}
