package nachos.kernel.userprog;

import java.util.*;
public class PMM {
    public PMEntry physicalPages[];
    
    public PMM (int physicalMemSize){
	physicalPages = new PMEntry[physicalMemSize];
    }

    //This is to allocate physical memory page for a process that is not
    //trying to share with an existing physical memory. PMP:Physical Memory Page
    //The return value is the PPN. If -1 is returned that means there is no
    //more avaliable physical memory.
    //The parameter is the VPN
    public int allocatePMP(int VPN){
	//Need to Get VPO from lowest bits, may need to have
	//another parameter to specify size of VPN
	for (int i = 0; i < physicalPages.length; i ++){
	    if(physicalPages[i].entryStatus == 0){
		physicalPages[i].setPage(VPN);
		return i;
	    }	    
	}
	return -1;
	
    }
    //Physical Memory Entry
    private class PMEntry{
	public int entryStatus; // 0 means free, 1 means occupied, other numebers for future use
	public ArrayList<Integer> virtualPages;  //List of all virtual pages that
	public boolean shareAllowed;
	//correspond to this physical page. We need this to do shared memory.
	public void setPage(int virtualPageNumber){
	   virtualPages.add(virtualPageNumber);
	   entryStatus = 1;
	}
	
    }
}
