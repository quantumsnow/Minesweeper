import network.MinesweeperConnectGUI;
import network.MinesweeperServer;

public class Test {
	private static final int PORT = 9000;
	
	public static void main(String[] args) {
		new MinesweeperServer(PORT);
		MinesweeperConnectGUI.main(null);
		MinesweeperConnectGUI.main(null);
	}
}