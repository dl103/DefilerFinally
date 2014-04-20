package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.event.ListSelectionEvent;

import common.Constants;
import common.DFileID;

import dblockcache.DBuffer;
import dblockcache.MyDBufferCache;

public class MyDFS extends DFS {

	private int fileCount = 0;
	private ArrayList<DFileID> fileList = new ArrayList<DFileID>();
	private MyDBufferCache myCache;  //need to make this
	private PriorityQueue<DFileID> fileQueue = new PriorityQueue<DFileID>();
	
	//true means there's free space!!!
	private boolean[] myBlockBitMap = new boolean[Constants.NUM_OF_BLOCKS]; //Wait for Dayvid
	public boolean[] myInodeBitMap = new boolean[Constants.MAX_DFILES];

	public MyDFS() {
		init();
	}
	
	public ArrayList<DFileID> getFileList() {
		return fileList;
	}

	@Override
	public void init() {
		try {
			myCache = new MyDBufferCache(Constants.NUM_OF_CACHE_BLOCKS);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		//Saving first few i's for inode blocks
		for (int i = Constants.MAX_DFILES; i < myBlockBitMap.length; i++) {
			myBlockBitMap[i] = true;
		}
		for (int i = 0; i < myInodeBitMap.length; i++) {
			myInodeBitMap[i] = true;
		}

		/*
		 * Need to retrieve the bitmap from virtual disk. 
		 */
		for (int i = 0; i < Constants.MAX_DFILES; i++) {
			DBuffer inodeBlock = myCache.getBlock(i);
			List<Integer> blockList = inodeBlock.getBlockmap();
			//			System.out.println("Initializing: " + i);
			if (blockList.size() > 0) {
				myInodeBitMap[i] = false;
			}
			for (int b = 0; b < blockList.size(); b++) {
				//Accounts for the 0th entry being the file size
				if (b != 0) myBlockBitMap[b] = false;
			}
		}

	}

	@Override
	public DFileID createDFile() throws Exception {
		int newFile = findFirstFreeInode();
		DFileID dFile = new DFileID(newFile);
		fileList.add(dFile);
		return dFile;
	}

	@Override
	public void destroyDFile(DFileID dFID) {
		fileList.remove(dFID);
		DBuffer inodeBlock = myCache.getBlock(dFID.getDFileID());
		List<Integer> blockList = inodeBlock.getBlockmap();
		for (int i = 0; i < blockList.size(); i++) {
			myBlockBitMap[blockList.get(i)] = true;			//change in blockbitmap to indicate it is free
			/*
			 * Need to get actual block and clear it. 
			 */
		}
		myInodeBitMap[dFID.getDFileID()] = true;			//indicate in inodebitmap that file is free
		
		
	}

	@Override
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {

		DBuffer inodeBlock = myCache.getBlock(dFID.getDFileID());
		List<Integer> blockList = inodeBlock.getBlockmap();
		System.out.println(inodeBlock.getBlockID() + "'s BlockList: " + blockList.toString());
		System.out.println("Found this block to read to. MyDFS.read(): " + blockList.toString());
		for (int i = 0; i < blockList.size(); i++) {
			int blockID = blockList.get(i);
			System.out.println("Reading from this block. MyDFS.reading(): " + blockID);
			DBuffer block = myCache.getBlock(blockID);
			if (count > Constants.BLOCK_SIZE) count = Constants.BLOCK_SIZE;
			block.read(buffer, startOffset, count);
		}

		System.out.println("Reading: " + Arrays.toString(buffer));
		return 0; //what is this supposed to return?
	}

	@Override
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {


		DBuffer inodeBlock = myCache.getBlock(dFID.getDFileID());
		List<Integer> blockList = inodeBlock.getBlockmap();
		System.out.println(inodeBlock.getBlockID() + "'s BlockList: " + blockList.toString());

		while (blockList.size() * Constants.BLOCK_SIZE < count) {
			blockList.add(findFirstFreeBlock());
		}
		if (count > inodeBlock.getFilesize()) inodeBlock.writeFilesize(count);
		inodeBlock.writeBlockmap(blockList);
		System.out.println("Found this block to write to: MyDFS.write(): " + blockList.toString());

		for (int i = 0; i < blockList.size(); i++) {
			int blockID = blockList.get(i);
			System.out.println("Writing to this block: " + blockID);
			DBuffer block = myCache.getBlock(blockID);
			if (count > Constants.BLOCK_SIZE) count = Constants.BLOCK_SIZE;
			block.write(buffer, startOffset, count);
		}
		
		System.out.println("Writing: " + Arrays.toString(buffer));
		System.out.println("MyDFS.write's blockList after is: " + blockList.toString());
		return 0;
	}

	@Override
	public int sizeDFile(DFileID dFID) {
		return myCache.getBlock(dFID.getDFileID()).getFilesize();
	}

	@Override
	public List<DFileID> listAllDFiles() {
		return fileList;
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

	public int findFirstFreeInode() throws Exception {
		for (int i = 0 ; i < myInodeBitMap.length; i++) {
			if (myInodeBitMap[i] == true) {
				myInodeBitMap[i] = false;
				return i;
			}			
		}
		
		Exception e = new Exception("Max # of files");
		throw e;
			
	}

}