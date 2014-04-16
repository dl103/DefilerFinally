import java.util.Arrays;

import common.DFileID;

import dfs.DFS;
import dfs.MyDFS;


public class TestBasic {

	public static void main (String[] args) {
		DFS dfs = new MyDFS();
		DFileID newFile = dfs.createDFile();
		//check for null?
		byte[] writeBuf = "This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!This is a test string!!".getBytes();
		byte[] smallBuf = "This is a small buf".getBytes();
		dfs.write(newFile, writeBuf, 0, 4002);
		byte[] readBuf = new byte[4002];
		dfs.read(newFile, readBuf, 0, 4002);
		dfs.write(newFile, smallBuf, 0, 19);
		
		DFileID newFile2 = dfs.createDFile();
		//check for null?
		byte[] writeBuf2 = "Another stringgggg!".getBytes();
		dfs.write(newFile2, writeBuf2, 0, 19);
		byte[] readBuf2 = new byte[19];
		dfs.read(newFile2, readBuf2, 0, 19);
		
		System.out.println("TestBasic.main: " + Arrays.toString(readBuf));
		System.out.println("TestBasic.main: " + Arrays.toString(readBuf2));
		System.out.println("TestBasic.main: File size for file 1 = " + dfs.sizeDFile(newFile)); 
	}
}
