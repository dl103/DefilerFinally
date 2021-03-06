package dblockcache;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import virtualdisk.VirtualDisk;

import common.Constants;

public class MyDBuffer extends DBuffer {

	private boolean isClean, isValid, isBusy, isPinned, isHeld, isPushing, isFetching;
	private byte[] myBuffer;
	private int blockID;
	private Object validLock, cleanLock;
	private VirtualDisk disk; // or whatever you call it

	public MyDBuffer(int id, VirtualDisk d){
		blockID = id;
		disk = d;
		myBuffer=new byte[Constants.BLOCK_SIZE];
		//blockID= //some id
		isClean=true;
		isValid=false;
		isBusy=false;
		isPinned=false;
		isHeld=false;
		isPushing=false;
		isFetching=false;
		validLock = new Object();
		cleanLock = new Object();
	}
	/* Start an asynchronous fetch of associated block from the volume */
	@Override
	public void startFetch(){
		try {
			synchronized (myBuffer) {
				isFetching=true;
				disk.startRequest(this, Constants.DiskOperationType.READ);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public void clearDBuffer(){
		myBuffer = new byte[Constants.BLOCK_SIZE];
		isHeld = false;
		isValid = false;
		isClean = true;//it is always clean from the start
		isFetching = false;
		isPushing = false;
		isPinned = false;
	}
	/* Start an asynchronous write of buffer contents to block on volume */
	@Override
	public void startPush(){
		try {
			isPushing=true;
			disk.startRequest(this, Constants.DiskOperationType.WRITE);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/* Check whether the buffer has valid data 
	 A dbuf is valid iff it has the correct copy of the data */
	@Override
	public boolean checkValid(){
		return isValid;
	}

	/*Suggestion.  
	 * A dbuf is pinned if I/O is in progress, i.e., a VDF request has started but not yet completed.  
	 * Don't evict a dbuf that is pinned or held: pick another candidate.
	 * */
	public boolean checkPinned(){
		return isPinned;
	}

	/*
	 *  A dbuf is held if DFS obtained a reference to the dbuf from getBlock but has not yet released the dbuf.  
	 */
	public boolean checkHeld(){
		return isHeld;
	}

	public void holdBuffer(){
		isHeld=true;
	}

	public void releaseBuffer(){
		isHeld=false;
	}
	/* Wait until the buffer has valid data, i.e., wait for fetch to complete */
	@Override
	public boolean waitValid(){
		while (!isValid && isFetching){
			try{
				synchronized(validLock){
					validLock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/* Check whether the buffer is dirty, i.e., has modified data written back to disk? 
	 * A dbuf is dirty iff it is valid and has an update (a write) that has not yet been written to disk. 
	 * */
	@Override
	public boolean checkClean(){
		return isClean;
	}

	/* Wait until the buffer is clean, i.e., wait until a push operation completes */
	public boolean waitClean(){
		while (isPushing&&!isClean){
			try{
				synchronized(cleanLock){
					cleanLock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/* Check if buffer is evictable: not evictable if I/O in progress, or buffer is held */
	@Override
	public boolean isBusy(){
		return isHeld || isBusy|| isPinned;
	}

	/*
	 * reads into the buffer[] array from the contents of the DBuffer. Check
	 * first that the DBuffer has a valid copy of the data! startOffset and
	 * count are for the buffer array, not the DBuffer. Upon an error, it should
	 * return -1, otherwise return number of bytes read.
	 * For a read: the read may run off the end of the file.  
	 * This is an "end of file" condition: just return the number of bytes read successfully.
	 */
	@Override
	public int read(byte[] buffer, int startOffset, int count){
		//check that dbuffer has a valid copy of the data
		//check that count are for the buffer array
		/*if (!isHeld || !isPinned || !isValid){
			System.out.println("cannot read. invalid or something");
			return -1;
		}*/
		int numBytesRead=0;
		waitValid();
		synchronized (myBuffer) {
			for (int i=0; i<count;i++){
				if (i>myBuffer.length){
					System.out.println("Buffer length is too small");
					return numBytesRead;
				}
				buffer[i+startOffset]=myBuffer[i];
				numBytesRead++;
			}
		}
//		System.out.println("MyDBuffer.read(): Actual buffer reads: " + Arrays.toString(myBuffer));
		return numBytesRead;
	}

	/*
	 * writes into the DBuffer from the contents of buffer[] array. startOffset
	 * and count are for the buffer array, not the DBuffer. Mark buffer dirty!
	 * Upon an error, it should return -1, otherwise return number of bytes
	 * written.s
	 * For a write: if the write extends the file beyond the maximum supported length,
	 * just stop there and return the number of bytes written successfully.
	 */
	@Override
	public int write(byte[] buffer, int startOffset, int count){
		/*if (isHeld||!isPinned){
			return -1;
		}*/
		waitValid();
		int numBytesWritten=0;
		isClean=false;
		synchronized (myBuffer) {
			for (int i=0; i<count;i++){
				if (i>myBuffer.length){
					System.out.println("Buffer length is too small");
					return numBytesWritten;
				}
				myBuffer[i]=buffer[i+startOffset];
				numBytesWritten++;
			}
		}
//		System.out.println(blockID + "'s Parameter: " + Arrays.toString(buffer));
//		System.out.println(blockID + "'s Buffer: " + Arrays.toString(myBuffer));
		return numBytesWritten;
	}

	/*
	 * Inode Methods
	 */

	public int getFilesize() {
		IntBuffer intBuf = ByteBuffer.wrap(myBuffer).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
		int[] array = new int[intBuf.remaining()];
		intBuf.get(array);
		return array[0];
	}

	public void writeFilesize(int filesize) {
		byte[] byteArray = ByteBuffer.allocate(4).putInt(filesize).array();
		for (int i = 0; i < 4; i++) {
			myBuffer[i] = byteArray[i];
		}
	}

	public List<Integer> getBlockmap() {
		waitValid();
		IntBuffer intBuf = ByteBuffer.wrap(myBuffer).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
		int[] array = new int[intBuf.remaining()];
		intBuf.get(array);
		List<Integer> blockmap = new ArrayList<Integer>();
		for (int i = 1; i < array.length; i++) {
			if (array[i] != 0) blockmap.add(array[i]);
		}
		return blockmap;
	}

	public void writeBlockmap(List<Integer> blockmap) {
		waitValid();
		int[] intArray = new int[blockmap.size()];
		for (int i = 0; i < blockmap.size(); i++) {
			intArray[i] = blockmap.get(i);
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(intArray);
		byte[] byteArray = byteBuffer.array();
		//Offset since myBuffer[i] starts at 4 and byteArray[i] starts at 0
		for (int i = 4; i < byteArray.length+4; i++) {
			myBuffer[i] = byteArray[i-4];
		}
		System.out.println("MyDBuffer.writeBlockmap(): " + blockmap.toString());
	}

	/*
	 * Upcalls from VirtualDisk
	 */

	/* An upcall from VirtualDisk layer to inform the completion of an IO operation */
	@Override
	public void ioComplete(){
		if (isFetching){
			isFetching=false;
			isValid=true;
			synchronized(validLock){
				validLock.notify();
			}
		}
		if (isPushing){
			isPushing=false;
			isClean=true;
			synchronized(cleanLock){
				cleanLock.notify();
			}
		}


	}

	/* An upcall from VirtualDisk layer to fetch the blockID associated with a startRequest operation */
	@Override
	public int getBlockID(){
		return blockID;
	}

	/* An upcall from VirtualDisk layer to fetch the buffer associated with DBuffer object*/
	@Override
	public byte[] getBuffer(){
		return myBuffer;
	}
}