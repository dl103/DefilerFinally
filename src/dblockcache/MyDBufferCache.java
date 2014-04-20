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
	private Queue<MyDBuffer> myBufferQueue = new LinkedList<MyDBuffer>();
	private Queue<Integer> myIntegerQueue = new LinkedList<Integer>();
	
	
	public MyDBufferCache(int cacheSize) throws FileNotFoundException, IOException {
		super(cacheSize);
		myDisk = new MyVirtualDisk(Constants.vdiskName, false);
		Thread t = new Thread(myDisk);
		t.start();
		myBufferQueue = new LinkedList<MyDBuffer>();
		
		for (int i = 0; i<cacheSize;i++){//do we know what is the number of buffers?
			myBufferQueue.add(new MyDBuffer(0,myDisk));//default blockid is 0?
		}
	}
	

	@Override
	public MyDBuffer getBlock(int blockID) {
		
		boolean isEvicted=false;
		synchronized(this){
//			if the buffer is in the cache
			for (MyDBuffer buf: myBufferQueue){
				if (buf.getBlockID() == blockID){
					//remove the dbuff and add it back to the queue
					myBufferQueue.remove(buf);
					myIntegerQueue.remove(buf.getBlockID());
					myBufferQueue.add(buf);
					myIntegerQueue.add(buf.getBlockID());
					return buf;
				}
			}
			
//			the buffer is not in the cache ie we need to evict.
//			PROBLEM: if all buffers are busy
			while(!isEvicted){
				for (MyDBuffer buf: myBufferQueue){
					//if the buffer is not busy
					if (!buf.isBusy()){
						//if the buffer is dirty, sync it
						if(!buf.checkClean()){
							buf.waitClean();
							buf.startPush();
						}
						//hold buffer
						buf.holdBuffer();
						myBufferQueue.remove(buf);
						myIntegerQueue.remove(buf.getBlockID());
						
						//carry out IO operation
						MyDBuffer dbuf = new MyDBuffer(blockID, myDisk);
						dbuf.startFetch();
						
						myBufferQueue.add(dbuf);
						myIntegerQueue.add(dbuf.getBlockID());
						return dbuf;	
					}
				}
			}
		}
		//something went wrong if we reach here
		return null;
		/*
		if (!myCache.containsKey(blockID)) {
			MyDBuffer dbuf = new MyDBuffer(blockID, myDisk);
			dbuf.startFetch();
			myCache.put(blockID, dbuf);
		}
		return myCache.get(blockID);
		*/
	}

	public void releaseBlock(MyDBuffer buf) {
		// TODO Auto-generated method stub
		try{
			buf.releaseBuffer();
		}
		catch(Exception E){
			E.printStackTrace();
		}
		//notify that the block is released.
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		for (MyDBuffer buf: myBufferQueue){
			buf.startPush();
			buf.waitClean();
			
			
		}
	}


	@Override
	public void releaseBlock(DBuffer buf) {
		// TODO Auto-generated method stub
		
	}

}
