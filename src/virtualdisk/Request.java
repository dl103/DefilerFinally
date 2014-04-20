package virtualdisk;

import common.Constants.DiskOperationType;

import dblockcache.DBuffer;

public class Request {

	private DBuffer myBuffer;
	private DiskOperationType myType;
	
	public Request (DBuffer dbuf, DiskOperationType type) {
		myBuffer = dbuf;
		myType = type;
	}
	
	public DBuffer getBuffer() {
		return myBuffer;
	}
	
	public DiskOperationType getType() {
		return myType;
	}
}
