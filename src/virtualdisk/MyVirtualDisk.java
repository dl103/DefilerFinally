package virtualdisk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.Constants.DiskOperationType;

import dblockcache.DBuffer;

public class MyVirtualDisk extends VirtualDisk {
	
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
//		System.out.println("Queue size: " + myProcessQueue.size());
	}

	@Override
	public void run() {
		while (true) {
			if (myProcessQueue.size() > 0) {
				Request currentJob = myProcessQueue.remove();
				switch (currentJob.getType()) {
				case READ:
					try {
						synchronized(currentJob.getBuffer()) {
							readBlock(currentJob.getBuffer());
							currentJob.getBuffer().ioComplete();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				case WRITE:
					try {
						synchronized(currentJob.getBuffer()) {
							writeBlock(currentJob.getBuffer());
							currentJob.getBuffer().ioComplete();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	

}
