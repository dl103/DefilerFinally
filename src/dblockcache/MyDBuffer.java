package dblockcache;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import virtualdisk.MyVirtualDisk;

import common.Constants;

public class MyDBuffer extends DBuffer {

	private boolean isClean, isValid, isBusy, isPinned, isHeld, isPushing, isFetching;
	private byte[] myBuffer;
	private int blockID;
	private Object validLock, cleanLock;
	private MyVirtualDisk disk; // or whatever you call it

	public MyDBuffer(){
		
		//blockID= //some id
		//disk = //somedisk
	}
	/* Start an asynchronous fetch of associated block from the volume */
	public void startFetch(){
		isFetching=true;

		try {
			disk.startRequest(this, Constants.DiskOperationType.READ);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/* Start an asynchronous write of buffer contents to block on volume */
	public void startPush(){
		isPushing=true;
		
		try {
			disk.startRequest(this, Constants.DiskOperationType.WRITE);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/* Check whether the buffer has valid data 
	 A dbuf is valid iff it has the â€œcorrectâ€� copy of the data */ 
	public boolean checkValid(){
		return isValid;
	}

	/*Suggestion.  
	 * A dbuf is pinned if I/O is in progress, i.e., a VDF request has started but not yet completed.  
	 * Donâ€™t evict a dbuf that is pinned or held: pick another candidate.
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

	/* Wait until the buffer has valid data, i.e., wait for fetch to complete */
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
	public boolean isBusy(){
		return isHeld || isBusy|| isPinned;
	}

	/*
	 * reads into the buffer[] array from the contents of the DBuffer. Check
	 * first that the DBuffer has a valid copy of the data! startOffset and
	 * count are for the buffer array, not the DBuffer. Upon an error, it should
	 * return -1, otherwise return number of bytes read.
	 */
	public int read(byte[] buffer, int startOffset, int count){
		//check that dbuffer has a valid copy of the data
		//check that count are for the buffer array
		if (!isHeld || !isPinned || !isValid){
			System.out.println("cannot read invalid or something");
			return -1;
		}
		if (count>myBuffer.length || count > buffer.length){
			System.out.println("Buffer length is too small");
			return -1;
		}
		int bytesRead=0;
		
		for (int i=0; i<count;i++){

			buffer[i+startOffset]=myBuffer[i];
			bytesRead++;
		}

		return bytesRead;
	}

	/*
	 * writes into the DBuffer from the contents of buffer[] array. startOffset
	 * and count are for the buffer array, not the DBuffer. Mark buffer dirty!
	 * Upon an error, it should return -1, otherwise return number of bytes
	 * written.s
	 */
	public int write(byte[] buffer, int startOffset, int count){
		if (isHeld||!isPinned){
			return -1;
		}
		int bytesWritten=0;
		
		isClean=false;
		return bytesWritten;
	}
	
	public List<Integer> getBlockmap() {
		IntBuffer intBuf = ByteBuffer.wrap(myBuffer).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
		int[] array = new int[intBuf.remaining()];
		intBuf.get(array);
		List<Integer> blockmap = new ArrayList<Integer>();
		for (int block : array) {
			blockmap.add(block);
		}
		return blockmap;
	}

	/* An upcall from VirtualDisk layer to inform the completion of an IO operation */
	public void ioComplete(){
		//no idea what to do here
	}

	/* An upcall from VirtualDisk layer to fetch the blockID associated with a startRequest operation */
	public int getBlockID(){
		return blockID;
	}

	/* An upcall from VirtualDisk layer to fetch the buffer associated with DBuffer object*/
	public byte[] getBuffer(){
		return myBuffer;
	}
}