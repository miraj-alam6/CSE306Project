// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog;

import nachos.Debug;
import nachos.machine.CPU;
import nachos.machine.MIPS;
import nachos.machine.Machine;
import nachos.machine.MachineException;
import nachos.machine.NachosThread;
import nachos.kernel.Nachos;
import nachos.kernel.userprog.Syscall;

/**
 * An ExceptionHandler object provides an entry point to the operating system
 * kernel, which can be called by the machine when an exception occurs during
 * execution in user mode.  Examples of such exceptions are system call
 * exceptions, in which the user program requests service from the OS,
 * and page fault exceptions, which occur when the user program attempts to
 * access a portion of its address space that currently has no valid
 * virtual-to-physical address mapping defined.  The operating system
 * must register an exception handler with the machine before attempting
 * to execute programs in user mode.
 */
public class ExceptionHandler implements nachos.machine.ExceptionHandler {

  /**
   * Entry point into the Nachos kernel.  Called when a user program
   * is executing, and either does a syscall, or generates an addressing
   * or arithmetic exception.
   *
   * 	For system calls, the following is the calling convention:
   *
   * 	system call code -- r2,
   *		arg1 -- r4,
   *		arg2 -- r5,
   *		arg3 -- r6,
   *		arg4 -- r7.
   *
   *	The result of the system call, if any, must be put back into r2. 
   *
   * And don't forget to increment the pc before returning. (Or else you'll
   * loop making the same system call forever!)
   *
   * @param which The kind of exception.  The list of possible exceptions 
   *	is in CPU.java.
   *
   * @author Thomas Anderson (UC Berkeley), original C++ version
   * @author Peter Druschel (Rice University), Java translation
   * @author Eugene W. Stark (Stony Brook University)
   */
    public void handleException(int which) {
	int type = CPU.readRegister(2);

	if (which == MachineException.SyscallException) {

	    switch (type) {
	    case Syscall.SC_Halt:
		Syscall.halt();
		break;
	    case Syscall.SC_Exit:
		int exitRet = Syscall.exit(CPU.readRegister(4));
		CPU.writeRegister(2, exitRet); //return value should be written to r2
		break;
	    case Syscall.SC_Exec:
		//Syscall.exec(""); //This line was originally here
		/* Miraj change start here*/
		int ptr0 = CPU.readRegister(4);
		// Debugging stuff start here
		Debug.println('w', "Gonna try to dereference address " +ptr0);
		//Debug.println('+', "" + AddrSpace.dereferenceString(ptr0));	
		int execRet = Syscall.exec(
			((UserThread)NachosThread.currentThread()).space.dereferenceString(ptr0));
		CPU.writeRegister(2, execRet); //return value should be written to r2
		/* Miraj change end here*/
		break;
	    case Syscall.SC_Write:
		int ptr = CPU.readRegister(4);
		int len = CPU.readRegister(5);
		byte buf[] = new byte[len];
		//the following wouldn't work, it's using virtual address as physical address
		//System.arraycopy(Machine.mainMemory, ptr, buf, 0, len);
		buf = ((UserThread)NachosThread.currentThread()).space.getByteArray(ptr,len);
		Syscall.write(buf, len, CPU.readRegister(6));
		break;
	    case Syscall.SC_Yield:
		Syscall.yield();
		break;
	    case Syscall.SC_Fork:
		int func = CPU.readRegister(4);
		Syscall.fork(func);
		break;
	    case Syscall.SC_Join:
		Syscall.join(CPU.readRegister(4));
		break;
	    case Syscall.SC_Read:
		int address = CPU.readRegister(4);
		byte buff[] = new byte[CPU.readRegister(5)];
		int leng = Syscall.read(buff, CPU.readRegister(5), CPU.readRegister(6));
		((UserThread)NachosThread.currentThread()).space.readByteintoMem(address,leng,buff);
		break;
	    case Syscall.SC_PredictCPU:
		Syscall.predictCPU(CPU.readRegister(4));
		break;
	    }

	    // Update the program counter to point to the next instruction
	    // after the SYSCALL instruction.
	    CPU.writeRegister(MIPS.PrevPCReg,
		    CPU.readRegister(MIPS.PCReg));
	    CPU.writeRegister(MIPS.PCReg,
		    CPU.readRegister(MIPS.NextPCReg));
	    CPU.writeRegister(MIPS.NextPCReg,
		    CPU.readRegister(MIPS.NextPCReg)+4);
	    return;
	}
	//Dealing with address error exception
	if(which == 5){
	   Debug.println('+', "Address error exception");

	   
	   Debug.println('+', "WHYYYY " +  
	   ((UserThread)NachosThread.currentThread()).space.getPageTable().length);
	   
	   
	   
	   //Do not erase this
	   ((UserThread)NachosThread.currentThread()).space.extendPageTable();
	   
	   
	   
	   Debug.println('+', "WHYYYY " +  
		   ((UserThread)NachosThread.currentThread()).space.getPageTable().length);
	   
	   
	   //TODO: need to either go to next instruction or do the
	   //same instruction again?
	   CPU.writeRegister(MIPS.PrevPCReg,
		    CPU.readRegister(MIPS.BadVAddrReg));
	   Debug.println('+', "BAD ADDRESS IS " + CPU.readRegister(MIPS.BadVAddrReg));
	    CPU.writeRegister(MIPS.PCReg,
		    CPU.readRegister(MIPS.BadVAddrReg));
	   // CPU.writeRegister(MIPS.NextPCReg,
	//	    CPU.readRegister(MIPS.BadVAddrReg));
	    //I don't get page fault exception, if I do this

	   
/* 
	   CPU.writeRegister(MIPS.PrevPCReg,
		    CPU.readRegister(MIPS.PCReg));
	    CPU.writeRegister(MIPS.PCReg,
		    CPU.readRegister(MIPS.BadVAddrReg));
	    CPU.writeRegister(MIPS.NextPCReg,
		    CPU.readRegister(MIPS.NextPCReg)-4);
	   /* */
	   return;
	}

	//Dealing with page fault exception
	if(which == 2){
		   Debug.println('+', "Page fault exception");
		   ((UserThread)NachosThread.currentThread()).space.onDemandPhysicalPage();
		
//		    CPU.writeRegister(MIPS.PCReg,
//			    CPU.readRegister(MIPS.BadVAddrReg));
		    CPU.writeRegister(MIPS.NextPCReg,
			    CPU.readRegister(MIPS.BadVAddrReg));
		    return;
		   //need to do return here, once implemented
		}
	
	
	//Take out the Debug.ASSERT so that program doesn't crash
	System.out.println("Unexpected user mode exception " + which +
		", " + type);
	Debug.ASSERT(false);

    }
    
    //Abbreviation for characterArrayToString
    public String cArrToStr(char[] arr){
	String s = "";
	for(int i = 0; i < arr.length; i++){
	    s+= arr[i];
	}
	return s;
    }
    
}
