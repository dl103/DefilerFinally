import java.util.Arrays;

import common.DFileID;

import dfs.MyDFS;


public class Client implements Runnable {

	private MyDFS myDfs;
	
	public Client(MyDFS dfs) {
		myDfs = dfs;
	}
	
	@Override
	public void run() {
		try {
			byte[] writeBuf = "111111111111111111111111111111111111111111111111111111111111111111".getBytes();
			double random = Math.random();
			if(random < .3) {
				myDfs.write(new DFileID(0), writeBuf, 0, writeBuf.length);
			}
			if(random > .3 && random < .7) {
				writeBuf = "222222222222222222222222222222222222222222222222222222222222222222".getBytes();
				myDfs.write(new DFileID(0), writeBuf, 0, writeBuf.length);
			}
			if(random > .7) {
				myDfs.read(new DFileID(0), writeBuf, 0, writeBuf.length);
				byte base = writeBuf[0];
				for (byte check : writeBuf) {
					if (check != base) throw new Exception();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
