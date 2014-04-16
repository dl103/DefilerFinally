import java.util.Arrays;

import common.DFileID;

import dfs.DFS;
import dfs.MyDFS;


public class TestBasic {

	public static void main (String[] args) {
		DFS dfs = new MyDFS();
		DFileID newFile = dfs.createDFile();
		DFileID newFile1 = dfs.createDFile();
		//check for null?
		byte[] writeBuf = "This is a test string!!".getBytes();
		dfs.write(newFile, writeBuf, 0, 23);
		byte[] readBuf = new byte[1024];
		dfs.read(newFile, readBuf, 0, 23);
		byte[] writeBuf1 = "I want to graduate!!!".getBytes();
		dfs.write(newFile1, writeBuf1, 0, 21);
		byte[] readBuf1 = new byte[1024];
		dfs.read(newFile1, readBuf1, 0, 21);
		System.out.println(dfs.listAllDFiles().toString());
		//System.out.println(Arrays.toString(readBuf));
	}
}
