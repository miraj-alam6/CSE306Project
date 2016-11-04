// AddrSpace.java
//	Class to manage address spaces (executing user programs).
//
//	In order to run a user program, you must:
//
//	1. link with the -N -T 0 option 
//	2. run coff2noff to convert the object file to Nachos format
//		(Nachos object code format is essentially just a simpler
//		version of the UNIX executable object code format)
//	3. load the NOFF file into the Nachos file system
//		(if you haven't implemented the file system yet, you
//		don't need to do this last step)
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog;

import nachos.Debug;
import nachos.machine.CPU;
import nachos.machine.MIPS;
import nachos.machine.Machine;
import nachos.machine.TranslationEntry;
import nachos.noff.NoffHeader;
import nachos.kernel.Nachos;
import nachos.kernel.filesys.OpenFile;

/**
 * This class manages "address spaces", which are the contexts in which
 * user programs execute.  For now, an address space contains a
 * "segment descriptor", which describes the the virtual-to-physical
 * address mapping that is to be used when the user program is executing.
 * As you implement more of Nachos, it will probably be necessary to add
 * other fields to this class to keep track of things like open files,
 * network connections, etc., in use by a user program.
 *
 * NOTE: Most of what is in currently this class assumes that just one user
 * program at a time will be executing.  You will have to rewrite this
 * code so that it is suitable for multiprogramming.
 * 
 * @author Thomas Anderson (UC Berkeley), original C++ version
 * @author Peter Druschel (Rice University), Java translation
 * @author Eugene W. Stark (Stony Brook University)
 */
public class AddrSpace {

  /** Page table that describes a virtual-to-physical address mapping. */
  private TranslationEntry pageTable[];

  /** Default size of the user stack area -- increase this as necessary! */
  private static final int UserStackSize = 1024;

  private NoffHeader noffH;
  private int spaceID;
  /**
   * Create a new address space.
   */
  public AddrSpace(int ID) { spaceID = ID; Debug.println('z', "SpaceID is "+spaceID);}

  /**
   * Load the program from a file "executable", and set everything
   * up so that we can start executing user instructions.
   *
   * Assumes that the object code file is in NOFF format.
   *
   * First, set up the translation from program memory to physical 
   * memory.  For now, this is really simple (1:1), since we are
   * only uniprogramming.
   *
   * @param executable The file containing the object code to 
   * 	load into memory
   * @return -1 if an error occurs while reading the object file,
   *    otherwise 0.
   */
  public int exec(OpenFile executable) {
    long size;
    
    
    if((noffH = NoffHeader.readHeader(executable)) == null)
	return(-1);
    
    // how big is address space?
    size = roundToPage(noffH.code.size)
	     + roundToPage(noffH.initData.size + noffH.uninitData.size)
	     + UserStackSize;	// we need to increase the size
    				// to leave room for the stack
    int numPages = (int)(size / Machine.PageSize);
    int numPagesForCode = (int) (roundToPage(noffH.code.size) / Machine.PageSize);
    int numPagesForData = (int) (roundToPage(noffH.initData.size) / Machine.PageSize);
    Debug.ASSERT((numPages <= Machine.NumPhysPages),// check we're not trying
		 "AddrSpace constructor: Not enough memory!");
                                                // to run anything too big --
						// at least until we have
						// virtual memory

    Debug.println('z', "Initializing address space, numPages=" 
		+ numPages + ", size=" + size);
    Debug.println('z', "Total Pages on Machine is " 
		+ Machine.NumPhysPages);

    // first, set up the translation 
    pageTable = new TranslationEntry[numPages];
    for (int i = 0; i < numPages; i++) {
      pageTable[i] = new TranslationEntry();
      pageTable[i].virtualPage = i; // for now, virtual page# = phys page#
      pageTable[i].physicalPage = Nachos.pMM.allocatePMP(i, spaceID); // #Changed this line from being i to allocating
      pageTable[i].valid = true;
      pageTable[i].use = false;
      pageTable[i].dirty = false;
      pageTable[i].readOnly = false;  // if code and data segments live on
				      // separate pages, we could set code 
				      // pages to be read-only
    }
    
    // Zero out the entire address space, to zero the uninitialized data 
    // segment and the stack segment.
    // #NOTE HW 2: YOU NEED TO CHANGE THIS PART, THIS IS COMPLETELY WRONG AS SOON AS
    //TWO PROCESSESS BEGIN TO RUN.
    /*I commented out the original incorrect code
    for(int i = 0; i < size; i++){
	Machine.mainMemory[i] = (byte)0;
    }
    */
    /*
     * I made a Potential solution below, need to somehow test it
     */
    for(int i = 0; i < numPages; i++){
	for(int j = 0; j < Machine.PageSize; j++){
	    Machine.mainMemory[getPhysicalAddress(i*Machine.PageSize, j)] = (byte)0;
	}
    } 
     /* Potential solution done*/
    
    restoreState();
    // then, copy in the code and data segments into memory
    if (noffH.code.size > 0) {
      Debug.println('a', "Initializing code segment, at " +
	    noffH.code.virtualAddr + ", size " +
	    noffH.code.size);

      executable.seek(noffH.code.inFileAddr);
      //executable.read(Machine.mainMemory, noffH.code.virtualAddr, noffH.code.size);
      //I thought next line would fix stuff, but it made no difference
     // Debug.println('z', "Code is "+noffH.code.virtualAddr);
     // Debug.println('z', "Data is"+noffH.initData.virtualAddr);
      //executable.read(Machine.mainMemory, Machine.mainMemory[pageTable[noffH.code.virtualAddr].physicalPage], noffH.code.size);
      //I think this works.
      for(int i = 0; i < numPagesForCode; i++){
	  executable.read(Machine.mainMemory, pageTable[i].physicalPage
		  * Machine.PageSize, Machine.PageSize);
      }
    }

    if (noffH.initData.size > 0) {
      Debug.println('a', "Initializing data segment, at " +
	    noffH.initData.virtualAddr + ", size " +
	    noffH.initData.size);

      executable.seek(noffH.initData.inFileAddr);
 
      //executable.read(Machine.mainMemory, noffH.initData.virtualAddr, noffH.initData.size);
      //I thought next line would fix stuff, but it made no difference
      // executable.read(Machine.mainMemory, Machine.mainMemory[pageTable[noffH.initData.virtualAddr].physicalPage], noffH.initData.size);
      //Gonna try to read the code in, I think this works.
      for(int i = numPagesForCode; i < numPagesForCode + numPagesForData; i++){
	  executable.read(Machine.mainMemory, pageTable[i].physicalPage
		  * Machine.PageSize, Machine.PageSize);
      }
    }

    return(0);
  }
  public void freeAddrSpace(){
      if(noffH == null){
		return;
      }
	    
	    // how big is address space?
      long size = roundToPage(noffH.code.size)
		     + roundToPage(noffH.initData.size + noffH.uninitData.size)
		     + UserStackSize;	// we need to increase the size
	    				// to leave room for the stack
      int numPages = (int)(size / Machine.PageSize);
 
      //Clear the memory, probably not necessary
      for(int i = 0; i < numPages; i++){
	for(int j = 0; j < Machine.PageSize; j++){
	    Machine.mainMemory[getPhysicalAddress(i*Machine.PageSize, j)] = (byte)0;
	}
      } 
      //Deallocate the actual memory, definitely necessary
      for(int i = 0; i < numPages; i++){
	  Nachos.pMM.deallocatePMP(i,spaceID);
      } 
  }
  public int getSpaceID(){
      return spaceID;
  }
  public void copyAddrSapce(AddrSpace x)
  {
      long size;
      noffH = x.noffH;
      size = roundToPage(noffH.code.size)
		     + roundToPage(noffH.initData.size + noffH.uninitData.size)
		     + UserStackSize;	// we need to increase the size
	    				// to leave room for the stack
      int numPages = (int)(size / Machine.PageSize);
      int numPagesForCode = (int) (roundToPage(noffH.code.size) / Machine.PageSize);
      int numPagesForData = (int) (roundToPage(noffH.initData.size) / Machine.PageSize);
      
      pageTable = new TranslationEntry[numPages];
      for (int i = 0; i < (numPagesForCode+numPagesForData); i++) {
        pageTable[i] = new TranslationEntry();
        pageTable[i].virtualPage = i; // for now, virtual page# = phys page#
        pageTable[i].physicalPage = x.pageTable[i].physicalPage; // #Changed this line from being i to allocating
        pageTable[i].valid = true;
        pageTable[i].use = false;
        pageTable[i].dirty = false;
        pageTable[i].readOnly = false;
      }
      
      for(int i = (numPagesForCode+numPagesForData); i < size; i++)
      {
	  pageTable[i] = new TranslationEntry();
	  pageTable[i].virtualPage = i; // for now, virtual page# = phys page#
	  pageTable[i].physicalPage = Nachos.pMM.allocatePMP(i,spaceID); // #Changed this line from being i to allocating
	  pageTable[i].valid = true;
	  pageTable[i].use = false;
	  pageTable[i].dirty = false;
	  pageTable[i].readOnly = false;
      }
      
  }
  /**
   * Initialize the user-level register set to values appropriate for
   * starting execution of a user program loaded in this address space.
   *
   * We write these directly into the "machine" registers, so
   * that we can immediately jump to user code.
   */
  public void initRegisters() {
    int i;
   
    for (i = 0; i < MIPS.NumTotalRegs; i++)
      CPU.writeRegister(i, 0);

    // Initial program counter -- must be location of "Start"
    CPU.writeRegister(MIPS.PCReg, 0);	

    // Need to also tell MIPS where next instruction is, because
    // of branch delay possibility
    CPU.writeRegister(MIPS.NextPCReg, 4);

    // Set the stack register to the end of the segment.
    // NOTE: Nachos traditionally subtracted 16 bytes here,
    // but that turns out to be to accomodate compiler convention that
    // assumes space in the current frame to save four argument registers.
    // That code rightly belongs in start.s and has been moved there.
    int sp = pageTable.length * Machine.PageSize;
    CPU.writeRegister(MIPS.StackReg, sp);
    Debug.println('a', "Initializing stack register to " + sp);
    
  }

  //This will take an address for a string and return the string
  //THis is probably not good enough if the physical pages are not continguous for
  //a string. You'll need to use virtual address instead.
  //#ASK the prof if the thing i get in ReadRegister(4) is a virtual or physical address
  public String dereferenceString(int address){
	String s = "";
	int index = 0;
	//Machine.mainMemory[pageTable[address].]
	int PhysAddr = getPhysicalAddress(address, index);
	Debug.println('z',"phys address i'm using I think is " + PhysAddr);
	Debug.println('z',"virtual address I'm using " + (address+index));
	
	while(Machine.mainMemory[PhysAddr] != 0){
	    //Debug.println('z',"virt page " + pageTable[address+index].virtualPage);
	    s += (char)Machine.mainMemory[PhysAddr];
	    index++;
	    Debug.println('z',"virtual address I'm using " + (address+index));
	    PhysAddr = getPhysicalAddress(address, index);
	    Debug.println('z',"phys address i'm using I think is " + PhysAddr);
	    Debug.println('z', "Translation has gotten me " + 
		    (char)Machine.mainMemory[PhysAddr]);
	}
	
	return s;
    }
  
  public void readByteintoMem(int address,int numRead, byte buff[])
  {
      //int index = 0;
      //int PhysAddr = getPhysicalAddress(address,index);
      
      
      for(int i = 0; i < numRead; i++)
      {
	  int PhysAddr = getPhysicalAddress(address,i);
	  Debug.println('z',"phys address i'm using I think is " + PhysAddr);
	  Debug.println('z',"virtual address I'm using " + (address+i));
	  Machine.mainMemory[PhysAddr] = buff[i];
	  Debug.println('z', "Translation has gotten me " + 
		    (char)Machine.mainMemory[PhysAddr]);
      }
  }
  
  //This function will get an array of bytes by using an address
  public byte[] getByteArray(int address,int len){
      	byte[] bytes;
      	int index = 0; //gonna loop thru with this
      	//how to get physical address
	int PhysAddr = getPhysicalAddress(address, index);
      	
	int size = 0;
      	//first get the size of the array
      	while(/*Machine.mainMemory[PhysAddr] != 0 &&*/ index < len){
      	    size++;
      	    index++;
      	}
      	
      	bytes = new byte[size];
      	
      //restart index for second pass where we actually get the data from the array
      	index = 0;
      	PhysAddr = getPhysicalAddress(address, index);
      	while(/*Machine.mainMemory[PhysAddr] != 0 &&*/ index < len){
      	    bytes[index] = Machine.mainMemory[PhysAddr];
      	    size++;
      	    index++;
      	    PhysAddr = getPhysicalAddress(address, index);
      	}
      	
      	
	return bytes;
    }
  
//This function will get an array of ints by using an address
  public int[] getIntArray(int address){
      	int[] ints;
      	int index = 0; //gonna loop thru with this
      	//how to get physical address
	int PhysAddr = getPhysicalAddress(address, index);
      	
	int size = 0;
      	//first get the size of the array
      	while(Machine.mainMemory[PhysAddr] != 0){
      	    size++;
      	    index+= 4;
      	}
      	
      	ints = new int[size];
      	
      //restart index for second pass where we actually get the data from the array
      	index = 0;
      	PhysAddr = getPhysicalAddress(address, index);
      	while(Machine.mainMemory[PhysAddr] != 0){
      	    ints[index] = Machine.mainMemory[PhysAddr];
      	    size++;
      	    index+=4;
      	    PhysAddr = getPhysicalAddress(address, index);
      	}
      	
      	
	return ints;
    }
  
  //This function will get a physical address based off of the virtual address and its index
  //index itself is basically an offset from address that is already possibly offsetted,
  //and thus the already present offset is calculated, and together offest + index is
  //the actual offset from the start of a page.
  public int getPhysicalAddress(int address, int index){
    	int VPN = address/Machine.PageSize; 
	int offset = address - VPN * Machine.PageSize;
	int PPN = pageTable[VPN].physicalPage;
	int PhysAddr = PPN * Machine.PageSize + offset + index;
	return PhysAddr;
  }
  
  /**
   * On a context switch, save any machine state, specific
   * to this address space, that needs saving.
   *
   * For now, nothing!
   */
  public void saveState() {}

  /**
   * On a context switch, restore any machine state specific
   * to this address space.
   *
   * For now, just tell the machine where to find the page table.
   */
  public void restoreState() {
    CPU.setPageTable(pageTable);
  }

  /**
   * Utility method for rounding up to a multiple of CPU.PageSize;
   */
  private long roundToPage(long size) {
    return(Machine.PageSize * ((size+(Machine.PageSize-1))/Machine.PageSize));
  }
}
