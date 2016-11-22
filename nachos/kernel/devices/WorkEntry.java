package nachos.kernel.devices;

import nachos.kernel.threads.Semaphore;

public class WorkEntry {
	private int sectorNum; //number of sectors to read
	private int index; // the index to read/write from
	private boolean willRead; //if true it reads, if false it will write
	private byte[] kernelBuffer;
	private Semaphore workSem;
	public int getSectorNum() {
	    return sectorNum;
	}

	public void setNumSectors(int sectorNum) {
	    this.sectorNum = sectorNum;
	}

	public boolean getWillRead() {
	    return willRead;
	}

	public void setWillRead(boolean willRead) {
	    this.willRead = willRead;
	}

	public byte[] getKernelBuffer() {
	    return kernelBuffer;
	}

	public void setKernelBuffer(byte[] kernelBuffer) {
	    this.kernelBuffer = kernelBuffer;
	}

	public int getIndex() {
	    return index;
	}

	public void setIndex(int index) {
	    this.index = index;
	}

	public Semaphore getWorkSem() {
	    return workSem;
	}

	public void setWorkSem(Semaphore workSem) {
	    this.workSem = workSem;
	}
	
	
	
}
