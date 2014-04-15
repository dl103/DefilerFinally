package dblockcache;

import java.util.HashMap;

public class MyDBufferCache extends DBufferCache {
	
	private HashMap<Integer, MyDBuffer> cache = new HashMap<Integer, MyDBuffer>();
	//instantiate a queue system
	
	
	public MyDBufferCache(int cacheSize) {
		super(cacheSize);
		
	}
	

	@Override
	public MyDBuffer getBlock(int blockID) {
		
		/*pseudocode*/
		synchronized(this){
//			if the buffer is in the cache
			for all dbuffers in the queue{
				if dbuffer.getblockid == blockID:
					//remove the dbuff and add it back to the queue
					//do we need to hold the buffer or something?
					queue.remove debuff
					queue.add debuff
					return dbuffer;
			}
			
//			the buffer is not in the cache ie we need to evict.
			for all dbuffers in the queue{
				//if the buffer is not busy
				if (!buffer.isBusy){
					queue.pop(buffer)
				}
			}
//			if we have gone through all the buffers but we dont find any to evict
//			we have to wait for one buffer to be available to get evicted
//			then we evict that shit
//			push that block into buffer
		}
		
		
		//return some debuffer
		return null;
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
		for all the dbuffers in debuff queue:
			dbuff.waitClean();
			dbuff.startPush();
	}

}
