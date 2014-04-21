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
		System.out.println("Initializing DFS...");
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
				System.out.println("MyDFS.init(): " + i + " file is in use");
				myInodeBitMap[i] = false;
			}
			for (int b = 0; b < blockList.size(); b++) {
				//				System.out.println("MyDFS.init(): " + blockList.get(b) + " block is in use");
				myBlockBitMap[blockList.get(b)] = false;
			}
		}

	}

	@Override
	public DFileID createDFile() throws Exception {
		int newFile = findFirstFreeInode();
		if (newFile == -1) {
			System.err.println("No more free files!");
			throw new Exception();
		}
		System.out.println("MyDFS.createDFile(): First free file is " + newFile);
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
			byte[] blankBuffer = new byte[Constants.BLOCK_SIZE];
			DBuffer block = myCache.getBlock(blockList.get(i));
			block.write(blankBuffer, 0, Constants.BLOCK_SIZE);
		}
		ArrayList<Integer> newBlocks = new ArrayList<Integer>();
		for (int i = 0; i < 500; i++) {
			newBlocks.add(0);
		}
		inodeBlock.writeBlockmap(newBlocks);
		myInodeBitMap[dFID.getDFileID()] = true;			//indicate in inodebitmap that file is free


	}

	@Override
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {

		DBuffer inodeBlock = myCache.getBlock(dFID.getDFileID());
		List<Integer> blockList = inodeBlock.getBlockmap();
		System.out.println("MyDFS.read(): " + inodeBlock.getBlockID() + "'s BlockList: " + blockList.toString());
		System.out.println("Found this block to read to. MyDFS.read(): " + blockList.toString());
		int byteCount = 0;
		for (int i = 0; i < blockList.size(); i++) {
			int blockID = blockList.get(i);
			//			System.out.println("Reading from this block. MyDFS.reading(): " + blockID);
			DBuffer block = myCache.getBlock(blockID);
			if (i < blockList.size() - 1) {
				count = Constants.BLOCK_SIZE;
			} else {
				count = count % Constants.BLOCK_SIZE;
			}
			int offset = i * Constants.BLOCK_SIZE;
			byteCount += block.read(buffer, offset, count);
		}

		System.out.println("Reading: " + Arrays.toString(buffer));
		return byteCount; //what is this supposed to return?
	}

	@Override
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {


		DBuffer inodeBlock = myCache.getBlock(dFID.getDFileID());
		List<Integer> blockList = inodeBlock.getBlockmap();
		System.out.println("MyDFS.write(): " + inodeBlock.getBlockID() + "'s BlockList before is: " + blockList.toString());

		while (blockList.size() * Constants.BLOCK_SIZE < count) {
			blockList.add(findFirstFreeBlock());
		}
		if (count > inodeBlock.getFilesize()) inodeBlock.writeFilesize(count);
		inodeBlock.writeBlockmap(blockList);
		System.out.println("MyDFS.write(): " + inodeBlock.getBlockID() + "'s BlockList after is: " + blockList.toString());
		int byteCount = 0;

		for (int i = 0; i < blockList.size(); i++) {
			int blockID = blockList.get(i);
			//			System.out.println("Writing to this block: " + blockID);
			DBuffer block = myCache.getBlock(blockID);
			if (i < blockList.size() - 1) {
				count = Constants.BLOCK_SIZE;
			} else {
				count = count % Constants.BLOCK_SIZE;
			}
			int offset = i * Constants.BLOCK_SIZE;
			byteCount += block.write(buffer, offset, count);
		}

		System.out.println("Writing: " + Arrays.toString(buffer));
		return byteCount;
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
		myCache.sync();

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