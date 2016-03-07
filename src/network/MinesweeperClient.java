package network;

import lib.Client;

public class MinesweeperClient extends Client {
	private MinesweeperGUI gui;

	public MinesweeperClient(String ip, int port, MinesweeperGUI gui) {
		super(ip, port);
		this.gui = gui;
	}

	@Override
	public void processMessage(String message) {
		// TODO Auto-generated method stub

	}

	public void open(int x, int y) {
		// TODO
	}

	public void mark(int x, int y) {
		// TODO
	}
}
