import java.util.ArrayList;
import java.util.Arrays;

import common.DFileID;

import dfs.DFS;
import dfs.MyDFS;


public class TestBasic {

	public static void main (String[] args) throws Exception {
		/*
		 * Testing writing and reading
		 */
		
		MyDFS dfs = new MyDFS();
		DFileID newFile = dfs.createDFile();
		DFileID newFile1 = dfs.createDFile();
		//check for null?
		byte[] writeBuf = "This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!".getBytes();
		
		dfs.write(newFile, writeBuf, 0, 4002);
		byte[] readBuf = new byte[4002];
		dfs.read(newFile, readBuf, 0, 4002);
		
		DFileID newFile2 = dfs.createDFile();
		//check for null?
		byte[] writeBuf2 = "Another stringgggg!".getBytes();
		dfs.write(newFile2, writeBuf2, 0, 19);
		byte[] readBuf2 = new byte[19];
		dfs.read(newFile2, readBuf2, 0, 19);
		
		System.out.println("TestBasic.main: " + Arrays.toString(readBuf));
		System.out.println("TestBasic.main: " + Arrays.toString(readBuf2));
		System.out.println("TestBasic.main: File size for file 1 = " + dfs.sizeDFile(newFile)); 
		
		/*
		 * testing destroy file
		 */
		ArrayList<DFileID> fileList = dfs.getFileList();
		System.out.println("filelist before delete: " + fileList.toString());
		System.out.println("inodeBitMap before delete: " + Arrays.toString(dfs.myInodeBitMap));
		dfs.destroyDFile(newFile2);
		System.out.println("filelist after delete: " + fileList.toString());
		System.out.println("inodeBitMap after delete: " + Arrays.toString(dfs.myInodeBitMap));
		
		
		
	}
}
