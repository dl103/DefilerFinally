import common.DFileID;

import dfs.DFS;
import dfs.MyDFS;


public class TestBasic {

	public static void main (String[] args) {
		DFS dfs = new MyDFS();
		DFileID newFile = dfs.createDFile();
		//check for null?
		byte[] writeBuf = "This is a test string!!".getBytes();
		dfs.write(newFile, writeBuf, 1000, 1024);
		byte[] readBuf = new byte[1024];
		dfs.read(newFile, readBuf, 0, 1024);
	}
}
