package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import virtualdisk.MyVirtualDisk;

import common.Constants;
import common.DFileID;
import dblockcache.DBuffer;
import dblockcache.DBufferCache;
import dblockcache.MyDBuffer;
import dblockcache.MyDBufferCache;

public class MyDFS extends DFS {
	
	private int fileCount = 0;
	private ArrayList<DFileID> fileList = new ArrayList<DFileID>();
	private MyVirtualDisk myDisk; 
	private MyDBufferCache cache;  //need to make this
	private PriorityQueue<DFileID> fileQueue = new PriorityQueue<DFileID>();
	private HashMap<DFileID, ArrayList<Integer>> inodeMap = new HashMap<DFileID, ArrayList<Integer>>();
	private ArrayList<Boolean> bitMap = new ArrayList<Boolean>(); //Wait for Dayvid
	
	
	@Override
	public void init(){
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
		fileQueue.add(newFile);
		inodeMap.put(newFile, new ArrayList<Integer>());
		
	
		
		return newFile;
	}

	@Override
	public void destroyDFile(DFileID dFID) {
		
	}

	@Override
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		
		ArrayList<Integer> blockList = inodeMap.get(dFID);
		for (int i = 0; i < blockList.size(); i++) {
			int blockID = blockList.get(i);
			DBuffer block = cache.getBlock(blockID);
			block.read(buffer, startOffset, count);
		}
		
		
		return 0; //what is this supposed to return?
	}

	@Override
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {
		
		
		if (!inodeMap.containsKey(dFID)) {
			dFID = createDFile();
		}
		ArrayList<Integer> blockList = inodeMap.get(dFID);
		
		/* Need to implement:
		 * If not enough blocks, consult bitmap and add to arraylist
		 * 
		 */
		
		for (int i = 0; i < blockList.size(); i++) {
			int blockID = blockList.get(i);
			DBuffer block = cache.getBlock(blockID);
			block.write(buffer, startOffset, count);
		}
		
		return 0;
	}

	@Override
	public int sizeDFile(DFileID dFID) {
		
		
		
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