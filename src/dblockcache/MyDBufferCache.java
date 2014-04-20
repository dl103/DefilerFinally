package dblockcache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import virtualdisk.MyVirtualDisk;
import virtualdisk.VirtualDisk;
import common.Constants;

public class MyDBufferCache extends DBufferCache {
	
	private VirtualDisk myDisk;
	private HashMap<Integer, MyDBuffer> myCache = new HashMap<Integer, MyDBuffer>();
	//instantiate a queue system
	
	
	public MyDBufferCache(int cacheSize) throws FileNotFoundException, IOException {
		super(cacheSize);
		myDisk = new MyVirtualDisk(Constants.vdiskName, false);
		Queue<MyDBuffer> myQueue = new LinkedList<MyDBuffer>();
		
		for (int i = 0; i<numBuffers;i++){//do we know what is the number of buffers?
			myQueue.add(new MyDBuffer(0,myDisk));//default blockid is 0?
		}
	}
	

	@Override
	public DBuffer getBlock(int blockID) {
		
		/*pseudocode*/
		
		synchronized(this){
//			if the buffer is in the cache
			for (MyDBuffer buf: myQueue){
				if (buf.getBlockID() == blockID){
					//remove the dbuff and add it back to the queue
					//do we need to hold the buffer or something?
					myQueue.remove(buf);
					myQueue.add(buf);
					return buf;
				}
			}
			
//			the buffer is not in the cache ie we need to evict.
			while(eviction is not done){
				for (MyDBuffer buf: myQueue){
					//if the buffer is not busy
					if (!buf.isBusy){
						myQueue.remove(buf);
						//switch clear the buffer and add that block in
						clear the buffer
						update inode
						find block from disk
						put block into buffer
						myQueue.add(buf)
					}
				}
			}
			return that buffer
		}

		return null;
		
		
		if (!myCache.containsKey(blockID)) {
			MyDBuffer dbuf = new MyDBuffer(blockID, myDisk);
			dbuf.startFetch();
			myCache.put(blockID, dbuf);
		}
		return myCache.get(blockID);
	}

	@Override
	public void releaseBlock(DBuffer buf) {
		// TODO Auto-generated method stub
		try{
			//dbuffer.isHeld=false;
		}
		catch(Exception E){
			E.printStackTrace();
		}
		//notify that the block is released.
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		/*for all the dbuffers in debuff queue:
			dbuff.waitClean();
			dbuff.startPush();
		 */
	}

}
