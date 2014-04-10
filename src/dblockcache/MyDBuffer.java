package dblockcache;

public class MyDBuffer extends DBuffer {

	private boolean isClean, isValid, isBusy, isPinned, isHeld;
	private byte[] myBuffer;
	
	
	
	
	
	
	/* Start an asynchronous fetch of associated block from the volume */
	public void startFetch();

	/* Start an asynchronous write of buffer contents to block on volume */
	public void startPush();

	/* Check whether the buffer has valid data 
	 A dbuf is valid iff it has the “correct” copy of the data */ 
	public boolean checkValid(){
		return isValid;
	}
	
	/*Suggestion.  
	 * A dbuf is pinned if I/O is in progress, i.e., a VDF request has started but not yet completed.  
	 * A dbuf is held if DFS obtained a reference to the dbuf from getBlock but has not yet released the dbuf.  
	 * Don’t evict a dbuf that is pinned or held: pick another candidate.
	 * */
	public boolean checkPinned(){
		return isPinned;
	}
	
	public boolean checkHeld(){
		return isHeld;
	}

	/* Wait until the buffer has valid data, i.e., wait for fetch to complete */
	public boolean waitValid(){
		while (true){
			//
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
		while (true){

		}
		return true;
	}

	/* Check if buffer is evictable: not evictable if I/O in progress, or buffer is held */
	public boolean isBusy(){
		return isHeld || isBusy;
	}

	/*
	 * reads into the buffer[] array from the contents of the DBuffer. Check
	 * first that the DBuffer has a valid copy of the data! startOffset and
	 * count are for the buffer array, not the DBuffer. Upon an error, it should
	 * return -1, otherwise return number of bytes read.
	 */
	public int read(byte[] buffer, int startOffset, int count);

	/*
	 * writes into the DBuffer from the contents of buffer[] array. startOffset
	 * and count are for the buffer array, not the DBuffer. Mark buffer dirty!
	 * Upon an error, it should return -1, otherwise return number of bytes
	 * written.
	 */
	public int write(byte[] buffer, int startOffset, int count);

	/* An upcall from VirtualDisk layer to inform the completion of an IO operation */
	public void ioComplete();

	/* An upcall from VirtualDisk layer to fetch the blockID associated with a startRequest operation */
	public int getBlockID();

	/* An upcall from VirtualDisk layer to fetch the buffer associated with DBuffer object*/
	public byte[] getBuffer(){
		return myBuffer;
	}
}