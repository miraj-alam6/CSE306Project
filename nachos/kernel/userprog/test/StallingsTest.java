// ProgTest.java
//	Test class for demonstrating that Nachos can load
//	a user program and execute it.  
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog.test;

import nachos.Debug;
import nachos.Options;
import nachos.machine.CPU;
import nachos.machine.NachosThread;
import nachos.kernel.Nachos;
import nachos.kernel.userprog.AddrSpace;
import nachos.kernel.userprog.UserThread;
import nachos.kernel.filesys.OpenFile;

/**
 * This is a test class for demonstrating that Nachos can load a user
 * program and execute it.
 * 
 * @author Thomas Anderson (UC Berkeley), original C++ version
 * @author Peter Druschel (Rice University), Java translation
 * @author Eugene W. Stark (Stony Brook University)
 */
public class StallingsTest implements Runnable {

    /** The name of the program to execute. */
    private String execName;

    /**
     * Start the test by creating a new address space and user thread,
     * then arranging for the new thread to begin executing the run() method
     * of this class.
     *
     * @param filename The name of the program to execute.
     */
    public StallingsTest(String filename, int num) {
	String name = "ProgTest"+ num + "(" + filename + ")";
	
	//Debug.println('+', "starting ProgTest: " + name);

	execName = filename;
	AddrSpace space = new AddrSpace(num);
	UserThread t = new UserThread(name, this, space);
	Nachos.incrementProgramID(); // #MIRAJ added this Next process that calls
	//the constructor of ProgTest should pass in the ID with the value that
	//is gotten due to this incrementProgramID() function
	//TODO: Get rid of this 
	//Nachos.scheduler.readyToRun(t);
	//TODO: Eventually just the following will be enough
	//Add to the userProcList
	Nachos.scheduler.addProcessToList(t);

    }

    /**
     * Entry point for the thread created to run the user program.
     * The specified executable file is used to initialize the address
     * space for the current thread.  Once this has been done,
     * CPU.run() is called to transfer control to user mode.
     */
    public void run() {
	OpenFile executable;

	//NOTE: not realistic behavior of an operating system when an
	//executable is not run
	if((executable = Nachos.fileSystem.open(execName)) == null) {
	    Debug.println('+', "Unable to open executable file: " + execName);
	    Nachos.scheduler.finishThread();
	    return;
	}

	AddrSpace space = ((UserThread)NachosThread.currentThread()).space;
	//NOTE: once again not realistic
	//Note to self: this is basically reading in the executable, instead of
	//actually executing it. CPU.runUserCode() is what actually executes it
	if(space.exec(executable) == -1) {
	    Debug.println('+', "Unable to read executable file: " + execName);
	    Nachos.scheduler.finishThread();
	    return;
	}

	space.initRegisters();		// set the initial register values
	space.restoreState();		// load page table register
	CPU.writeRegister(4, Nachos.randomTicks);
	CPU.runUserCode();			// jump to the user progam
	Debug.ASSERT(false);		// machine->Run never returns;
	// the address space exits
	// by doing the syscall "exit"
    }

    /**
     * Entry point for the test.  Command line arguments are checked for
     * the name of the program to execute, then the test is started by
     * creating a new ProgTest object.
     */
    public static void start() {
	Debug.ASSERT(Nachos.options.FILESYS_REAL || Nachos.options.FILESYS_STUB,
			"A filesystem is required to execute user programs");
	int count = 0;
	Nachos.stallingsHelper.masterHelper(); //call this once here, rest of the times it'll be called
	//by the general timer. This will randomize the ticks
	new StallingsTest("test/hw3testprog4", count++);
	
    }
}
