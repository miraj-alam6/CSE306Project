// ConsoleTest.java
//
//	Class for testing the Console hardware device.
//
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and 
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog.test;

import nachos.Debug;
import nachos.Options;
import nachos.kernel.Nachos;
import nachos.kernel.devices.ConsoleDriver;
import nachos.machine.NachosThread;

/**
 * Class for testing the Console hardware device.
 * 
 * @author Peter Druschel (Rice University)
 * @author Eugene W. Stark (Stony Brook University)
 */
public class ConsoleProgTest implements Runnable {

    /** Reference to the console device driver. */
    private ConsoleDriver console;

    /**
     * Test the console by echoing characters typed at the input onto
     * the output.  Stop when the user types a 'q'.
     */
    public void run() {
	Debug.println('+', "ConsoleTest: starting");
	Debug.ASSERT(Nachos.consoleDriver != null,
			"There is no console device to test!");

	console = Nachos.consoleDriver;
	
	while (true) {
	    char ch = console.getChar();
	    console.putChar(ch);	// echo it!

	    if(ch == '\n')
		console.putChar('\r');

	    if (ch == 'q') {
		Debug.println('+', "ConsoleTest: quitting");
		console.stop();
		Nachos.scheduler.finishThread();    // if q, quit
	    }
	}
    }

    /**
     * Entry point for the Console test.  If "-c" is included in the
     * command-line arguments, then run the console test; otherwise, do
     * nothing.
     *
     * The console test reads characters from the input and echoes them
     * onto the output.  The test ends when a 'q' is read.
     */
    public static void start() {
	NachosThread thread = new NachosThread("Console test", new ConsoleProgTest());
	Nachos.scheduler.readyToRun(thread);
	
	final int[] count = new int[1];
	
	Nachos.options.processOptions
	(new Options.Spec[] {
		new Options.Spec
			("-xc",
			 new Class[] {String.class},
			 "Usage: -xc <executable file>",
			 new Options.Action() {
			    public void processOption(String flag, Object[] params) {
				new ProgTest((String)params[0], count[0]++);
			    }
			 })
	 });
    }
}


