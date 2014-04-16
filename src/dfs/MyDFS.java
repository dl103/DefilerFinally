package dfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import virtualdisk.MyVirtualDisk;

import common.Constants;
import common.DFileID;

import dblockcache.DBuffer;
import dblockcache.MyDBufferCache;

public class MyDFS extends DFS {

	private int fileCount = 0;
	private ArrayList<DFileID> fileList = new ArrayList<DFileID>();
	private MyVirtualDisk myDisk; 
	private MyDBufferCache cache;  //need to make this
	private PriorityQueue<DFileID> fileQueue = new PriorityQueue<DFileID>();
	private boolean[] myBlockBitMap = new boolean[Constants.NUM_OF_BLOCKS]; //Wait for Dayvid
	private boolean[] myInodeBitMap = new boolean[Constants.MAX_DFILES];

	public MyDFS() {
		init();
	}
	
	@Override
	public void init(){
		try {
			myDisk = new MyVirtualDisk(Constants.vdiskName, true);
			cache = new MyDBufferCache(Constants.NUM_OF_CACHE_BLOCKS); 

			for (int i = 0; i < myBlockBitMap.length; i++) {
				myBlockBitMap[i] = true;
			}
			for (int i = 0; i < myInodeBitMap.length; i++) {
				myInodeBitMap[i] = true;
			}

			/*
			 * Need to retrieve the bitmap from virtual disk. 
			 */
			for (int i = 0; i < Constants.NUM_OF_BLOCKS; i++) {
				DBuffer inodeBlock = cache.getBlock(i);
				List<Integer> blockList = inodeBlock.getBlockmap();
				if (blockList.size() > 0) myInodeBitMap[i] = false;
				for (int b = 0; b < blockList.size(); b++) {
					//Accounts for the 0th entry being the file size
					if (b != 0) myBlockBitMap[b] = false;
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public DFileID createDFile() {
		int newFile = findFirstFreeInode();
		return new DFileID(newFile);
	}

	@Override
	public void destroyDFile(DFileID dFID) {

	}

	@Override
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {

		DBuffer inodeBlock = cache.getBlock(dFID.getDFileID());
		List<Integer> blockList = inodeBlock.getBlockmap();
		for (int i = 0; i < blockList.size(); i++) {
			//Accounts for the 0th entry being the file size
			if (i != 0) {
				int blockID = blockList.get(i);
				DBuffer block = cache.getBlock(blockID);
				block.read(buffer, startOffset, count);
			}
		}


		return 0; //what is this supposed to return?
	}

	@Override
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {


		DBuffer inodeBlock = cache.getBlock(dFID.getDFileID());
		List<Integer> blockList = inodeBlock.getBlockmap();

		while (blockList.size() * Constants.BLOCK_SIZE < count) {
			blockList.add(findFirstFreeBlock());
		}
		inodeBlock.writeBlockmap(blockList);

		for (int i = 0; i < blockList.size(); i++) {
			//Accounts for the 0th entry being file size
			if (i != 0) {
				int blockID = blockList.get(i);
				DBuffer block = cache.getBlock(blockID);
				block.write(buffer, startOffset, count);
			}
		}

		return 0;
	}

	@Override
	public int sizeDFile(DFileID dFID) {

		//for now, just consulting either a separate arraylist or first element
		//in inode arraylist

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

	public int findFirstFreeBlock() {		
		for (int i = 0 ; i < myBlockBitMap.length; i++) {
			if (myBlockBitMap[i] == true) {
				myBlockBitMap[i] = false;
				return i;
			}			
		}
		return -1;
	}

	public int findFirstFreeInode() {		
		for (int i = 0 ; i < myInodeBitMap.length; i++) {
			if (myInodeBitMap[i] == true) {
				myInodeBitMap[i] = false;
				return i;
			}			
		}
		return -1;
	}

}