// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.kernel.devices.ConsoleDriver;
import nachos.kernel.userprog.test.ProgTest;
import nachos.machine.CPU;
import nachos.machine.NachosThread;
import nachos.machine.Simulation;

/**
 * Nachos system call interface.  These are Nachos kernel operations
 * 	that can be invoked from user programs, by trapping to the kernel
 *	via the "syscall" instruction.
 *
 * @author Thomas Anderson (UC Berkeley), original C++ version
 * @author Peter Druschel (Rice University), Java translation
 * @author Eugene W. Stark (Stony Brook University)
 */
public class Syscall {

    // System call codes -- used by the stubs to tell the kernel 
    // which system call is being asked for.

    /** Integer code identifying the "Halt" system call. */
    public static final byte SC_Halt = 0;

    /** Integer code identifying the "Exit" system call. */
    public static final byte SC_Exit = 1;

    /** Integer code identifying the "Exec" system call. */
    public static final byte SC_Exec = 2;

    /** Integer code identifying the "Join" system call. */
    public static final byte SC_Join = 3;

    /** Integer code identifying the "Create" system call. */
    public static final byte SC_Create = 4;

    /** Integer code identifying the "Open" system call. */
    public static final byte SC_Open = 5;

    /** Integer code identifying the "Read" system call. */
    public static final byte SC_Read = 6;

    /** Integer code identifying the "Write" system call. */
    public static final byte SC_Write = 7;

    /** Integer code identifying the "Close" system call. */
    public static final byte SC_Close = 8;

    /** Integer code identifying the "Fork" system call. */
    public static final byte SC_Fork = 9;

    /** Integer code identifying the "Yield" system call. */
    public static final byte SC_Yield = 10;

    /** Integer code identifying the "Remove" system call. */
    public static final byte SC_Remove = 11;

    /** Integer code identifying the "PredictCPU" system call. */
    public static final byte SC_PredictCPU = 12;
    
    /**
     * Stop Nachos, and print out performance stats.
     */
    public static void halt() {
	
	Debug.print('+', "Shutdown, initiated by user program halt syscall.\n");
	Simulation.stop();
    }

    /* Address space control operations: Exit, Exec, and Join */

    /**
     * This user program is done.
     *
     * @param status Status code to pass to processes doing a Join().
     * status = 0 means the program exited normally.
     */
    public static int exit(int status) {
	Debug.println('+', "Exit system call with status=" + status
				+ ": " + NachosThread.currentThread().name);
	if(NachosThread.currentThread() instanceof UserThread){
	    Debug.println('w', "About to deallocate memory of the process with ID "+ 
		    ((UserThread)NachosThread.currentThread()).space.getSpaceID());
	    ((UserThread)NachosThread.currentThread()).space.freeAddrSpace();
	}
	
	Nachos.programSemV(((UserThread)NachosThread.currentThread()).space.getSpaceID());
	Nachos.scheduler.endProcess((UserThread)NachosThread.currentThread());
	Nachos.scheduler.finishThread();
	return status;
    }

    /**
     * Run the executable, stored in the Nachos file "name", and return the 
     * address space identifier.
     *
     * @param name The name of the file to execute.
     */
    public static int exec(String name) {
	Debug.println('+', "Exec system call in thread: " + NachosThread.currentThread().name);
	//Trying the main part of the syscall now:
	
	Debug.println('+', "Thread " + NachosThread.currentThread().name + " will try to execute program " + name);
	int id = Nachos.nextProgramID;
	
	new ProgTest(name, Nachos.nextProgramID);
	Debug.println('+', "New program to execute is ID" +id);
	Nachos.addNewProgram(id);
	if(Nachos.options.SRT_SCHEDULING){
	   yield(); //yield so that we can calculate the cpu burst time
	   //of the newly added process
	}
	//NEXT LINE IS WRONG, be careful, the new Prog is not the currentThread, just use
	//Nachos.addNewProgram(((UserThread)NachosThread.currentThread()).space.getSpaceID());
	//int spaceid = ((UserThread)NachosThread.currentThread()).space.getSpaceID();
	//Debug.ASSERT(id == spaceid);
	//Done with main part of the syscall
	
	return id;}

    /**
     * Wait for the user program specified by "id" to finish, and
     * return its exit status.
     *
     * @param id The "space ID" of the program to wait for.
     * @return the exit status of the specified program.
     */
    public static int join(int id) {
	Debug.println('+', "Join system call in thread: " + NachosThread.currentThread().name);
	Nachos.programSemP(id);
	Debug.println('+', "got this id "+ id);
	return 0;}


    /* File system operations: Create, Open, Read, Write, Close
     * These functions are patterned after UNIX -- files represent
     * both files *and* hardware I/O devices.
     *
     * If this assignment is done before doing the file system assignment,
     * note that the Nachos file system has a stub implementation, which
     * will work for the purposes of testing out these routines.
     */

    // When an address space starts up, it has two open files, representing 
    // keyboard input and display output (in UNIX terms, stdin and stdout).
    // Read and write can be used directly on these, without first opening
    // the console device.

    /** OpenFileId used for input from the keyboard. */
    public static final int ConsoleInput = 0;

    /** OpenFileId used for output to the display. */
    public static final int ConsoleOutput = 1;

    /**
     * Create a Nachos file with a specified name.
     *
     * @param name  The name of the file to be created.
     */
    public static void create(String name) {
	Debug.println('+', "Stub for create system call in thread: " + NachosThread.currentThread().name);
    }

    /**
     * Remove a Nachos file.
     *
     * @param name  The name of the file to be removed.
     */
    public static void remove(String name) {
	Debug.println('+', "Stub for remove system call in thread: " + NachosThread.currentThread().name);
    }

    /**
     * Open the Nachos file "name", and return an "OpenFileId" that can 
     * be used to read and write to the file.
     *
     * @param name  The name of the file to open.
     * @return  An OpenFileId that uniquely identifies the opened file.
     */
    public static int open(String name) {
	Debug.println('+', "Stub for open system call in thread: " + NachosThread.currentThread().name);
	return 0;}

    /**
     * Write "size" bytes from "buffer" to the open file.
     *
     * @param buffer Location of the data to be written.
     * @param size The number of bytes to write.
     * @param id The OpenFileId of the file to which to write the data.
     */
    public static void write(byte buffer[], int size, int id) {
	int oldLevel = CPU.setLevel(CPU.IntOff);
	Debug.println('+', "Write system call in thread: " + NachosThread.currentThread().name);
	String s = "";
	if (id == ConsoleOutput) {
	    Nachos.writingToConsole = true;
	    for(int i = 0; i < size; i++) {
		//doesn't use physical address
		Nachos.consoleDriver.putChar((char)buffer[i]);
		s += (char)buffer[i];
	    }
	    Nachos.writingToConsole = false;
	}
	CPU.setLevel(oldLevel);
	
    }

    /**
     * Read "size" bytes from the open file into "buffer".  
     * Return the number of bytes actually read -- if the open file isn't
     * long enough, or if it is an I/O device, and there aren't enough 
     * characters to read, return whatever is available (for I/O devices, 
     * you should always wait until you can return at least one character).
     *
     * @param buffer Where to put the data read.
     * @param size The number of bytes requested.
     * @param id The OpenFileId of the file from which to read the data.
     * @return The actual number of bytes read.
     */
    public static int read(byte buffer[], int size, int id) {
	Debug.println('+', "Read system call in thread: " + NachosThread.currentThread().name);
	int x = 0;
	String s = ""; // #Miraj me trying to debug something
	if(id == ConsoleInput)
	{
	    ConsoleDriver conso = Nachos.consoleDriver;
	    while(x < size)
	    {
	       char ch = conso.getChar();
	       conso.putChar(ch);	// echo it!

	       if(ch == '\n')
	       {
		   conso.putChar('\r');
		   break;
	       }
	       
	       buffer[x] = (byte)ch;
	       s+= ch;
	       x++;
	    }
	}
	Debug.println('+', "What was read is " + s);
	return x;
	}

    /**
     * Close the file, we're done reading and writing to it.
     *
     * @param id  The OpenFileId of the file to be closed.
     */
    public static void close(int id) {
	Debug.println('+', "Stub for close system call in thread: " + NachosThread.currentThread().name);
	
    }


    /*
     * User-level thread operations: Fork and Yield.  To allow multiple
     * threads to run within a user program. 
     */

    /**
     * Fork a thread to run a procedure ("func") in the *same* address space 
     * as the current thread.
     *
     * @param func The user address of the procedure to be run by the
     * new thread.
     */
    public static void fork(int func) 
    {
	
    }

    /**
     * Yield the CPU to another runnable thread, whether in this address space 
     * or not. 
     */
    public static void yield() {
	Debug.println('+', "Through syscall yielding thread "+ NachosThread.currentThread().name);
	Nachos.scheduler.yieldThread();
    }
    
    public static void predictCPU(int ticks){
	Debug.println('+', "PredictCPU syscall with ticks " + ticks);
	((UserThread)NachosThread.currentThread()).setTicksLeft(ticks);
	if(Nachos.options.SRT_SCHEDULING){
	    //Don't always yield here. Only yield if your ticks is less
	    //than another process.AKA, this is not the shortest process
	    //thus the actual shortest process should run, only reason we got
	    //here is because default CPU ticks is -1 so we had to predictcpu
	    //before determining if this is smallest
	    if(NachosThread.currentThread() instanceof UserThread){
		if(Nachos.scheduler.getUPList() instanceof SJFQueue){
		   if( ((SJFQueue)Nachos.scheduler.getUPList()).
			  shouldYield((UserThread)NachosThread.currentThread())){
		       yield();  
		   }
		}
		
	    }
	    
	}
	else{
	    yield();
	}
	
    }

}
