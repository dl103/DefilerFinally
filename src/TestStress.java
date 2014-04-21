import dfs.MyDFS;


public class TestStress {

	public static void main (String[] args) {
		MyDFS dfs = new MyDFS();
		dfs.init();
		for (int i = 0; i < 500; i++) {
			Client c = new Client(dfs);
			Thread t = new Thread(c);
			t.start();
		}
	}
}
