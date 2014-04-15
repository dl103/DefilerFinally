package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import virtualdisk.MyVirtualDisk;

import common.Constants;
import common.DFileID;
import dblockcache.DBufferCache;
import dblockcache.MyDBufferCache;

public class MyDFS extends DFS {
	
	private int fileCount = 0;
	private ArrayList<DFileID> fileList = new ArrayList<DFileID>();
	private MyVirtualDisk myDisk; 
	private MyDBufferCache cache;  //need to make this
	
	@Override
	public void init() {
		try {
			myDisk = new MyVirtualDisk(Constants.vdiskName, true);
			cache = new MyDBufferCache(Constants.NUM_OF_CACHE_BLOCKS); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public DFileID createDFile() {
		if (fileCount >= Constants.MAX_DFILES) {
			fileCount = 0;
		}
		DFileID newFile = new DFileID(fileCount);
		
		return newFile;
	}

	@Override
	public void destroyDFile(DFileID dFID) {
		
	}

	@Override
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		
		int blockID = dFID.getDFileID();
		cache.getBlock(blockID);
		
		return 0;
	}

	@Override
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {
		
		int blockID = dFID.getDFileID();
		
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