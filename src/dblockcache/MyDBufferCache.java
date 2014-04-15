package dblockcache;

import java.util.HashMap;

public class MyDBufferCache extends DBufferCache {
	
	private HashMap<Integer, MyDBuffer> cache = new HashMap<Integer, MyDBuffer>();
	
	
	
	public MyDBufferCache(int cacheSize) {
		super(cacheSize);
		
	}

	@Override
	public DBuffer getBlock(int blockID) {
		
		
		
		
		return null;
	}

	@Override
	public void releaseBlock(DBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		
	}

}
