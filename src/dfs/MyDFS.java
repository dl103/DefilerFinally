package dfs;

import java.util.List;

import virtualdisk.VirtualDisk;

import common.Constants;
import common.DFileID;

import dblockcache.DBuffer;
import dblockcache.DBufferCache;
import dblockcache.MyDBuffer;

public class MyDFS extends DFS {
	
	private DBuffer myDBuffer;
	private DBufferCache myDBufferCache;
	private VirtualDisk myVirtualDisk;
	private int idCount = 0;
	
	
	@Override
	public void init() {
		//myDBufferCache = new DBufferCache(Constants.NUM_OF_CACHE_BLOCKS);
		//myVirtualDisk = new VirtualDisk(Constants.vdiskName, true);
	}

	@Override
	public DFileID createDFile() {
		
		if (idCount == Constants.MAX_DFILES) {
			idCount = 0;
		}
		DFileID newFile = new DFileID(idCount);
		
		
		return null;
	}

	@Override
	public void destroyDFile(DFileID dFID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int sizeDFile(DFileID dFID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DFileID> listAllDFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		
	}
	
	

}
