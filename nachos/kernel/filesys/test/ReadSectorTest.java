// FileSystemTest.java
//	Simple test routines for the file system.  
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and 
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.filesys.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import nachos.Debug;
import nachos.Options;
import nachos.machine.NachosThread;
import nachos.machine.Simulation;
import nachos.kernel.Nachos;
import nachos.kernel.filesys.OpenFile;

/**
 * This class implements some simple test routines for the file system.
 * We implement:
 *	   Copy -- copy a file from UNIX to Nachos;
 *	   Print -- cat the contents of a Nachos file;
 *	   Perftest -- a stress test for the Nachos file system
 *		read and write a really large file in tiny chunks
 *		(won't work on baseline system!).
 *
 * @author Thomas Anderson (UC Berkeley), original C++ version
 * @author Peter Druschel (Rice University), Java translation
 * @author Eugene W. Stark (Stony Brook University)
 */
public class ReadSectorTest implements Runnable {

    /** Transfer data in small chunks, just to be difficult. */
    private static final int TransferSize = 10;

    private static int jobCount = 0;
    //This will be set to the current value of jobCount
    public static int threadIDCount = 0;
    public static int diskSectorSize = Nachos.diskDriver.getDisk().geometry.SectorSize;
    public int threadIDNumber = 0;
    public int sectorNumber;
    //
    public int index;
    public int sectorSize;
    
    public ReadSectorTest(int n, int i) {
	sectorNumber = n;
	index = i;
    }











    /**

     */
    public void run() {
	
	
	//int diskSectorSize
	//int firstSector = (int)position / diskSectorSize;
	//int lastSector = ((int)position + numBytes - 1) / diskSectorSize;
	//numSectors = 1 + lastSector - firstSector;
	byte[] buf = new byte[diskSectorSize*2];
	Nachos.diskDriver.readSector(sectorNumber, buf, index);
	Nachos.scheduler.finishThread();
    }

    public static void MakeAnotherThread(){
	
    }
    /**
     * Entry point for the FileSystem test.
     */
    public static void start() {
	Debug.println('+', "Doing readsector test");
	/**/
	createTest(1,2);
	createTest(5,2);
	createTest(8,2);
	
	createTest(10,2);
	createTest(1,2);
	createTest(12,2);
	createTest(13,2);
	createTest(14,2);
	createTest(1,10);
	createTest(4,12);
	createTest(6,12);
	createTest(8,12);
	createTest(10,12);
	createTest(14,12);
	createTest(6,9);
	createTest(3,5);
	createTest(2,6);
	createTest(4,6);
	createTest(5,5);
	createTest(1,6);
	/**/
	/*
	createTest(7,0);
	createTest(6,0);
	createTest(5,0);
	createTest(4,0);
	createTest(3,0);
	createTest(2,0);
	createTest(1,0);
	*/
    }
    
    public static void createTest(int sectorNumber, int index){
	NachosThread temp = new NachosThread("Thread"+threadIDCount,
		new ReadSectorTest(sectorNumber,index));
	Nachos.scheduler.readyToRun(temp);
    }
}
