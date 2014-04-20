package virtualdisk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.Constants.DiskOperationType;

import dblockcache.DBuffer;

public class MyVirtualDisk extends VirtualDisk implements Runnable {
	
	private Queue<Request> myProcessQueue;
	
	public MyVirtualDisk(String volName, boolean format) throws FileNotFoundException, IOException {
		super(volName, format);
		myProcessQueue = new ConcurrentLinkedQueue<Request>();
	}

	@Override
	public void startRequest(DBuffer buf, DiskOperationType operation)
			throws IllegalArgumentException, IOException {
		Request newRequest = new Request(buf, operation);
		myProcessQueue.add(newRequest);
	}

	@Override
	public void run() {
		Request currentJob = myProcessQueue.remove();
		while (true) {
			switch (currentJob.getType()) {
			case READ:
				try {
					readBlock(currentJob.getBuffer());
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case WRITE:
				try {
					writeBlock(currentJob.getBuffer());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	

}
