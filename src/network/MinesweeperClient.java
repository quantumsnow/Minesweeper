package network;

import lib.Client;
import lib.Command;

public class MinesweeperClient extends Client {
	public static abstract class ClientCommand extends Command {
		public static final Command OK = new Command(new String[] { "OK" }, false),
				REGISTER = new Command(new String[] { "R: " }, true),
				DEREGISTER = new Command(new String[] { "D" }, false),
				NEW_GAME = new Command(new String[] { "N: (", ", ", ", ", ")" }, false),
				OPEN = new Command(new String[] { "O: (", ", ", ", ", ")" }, false),
				LOST = new Command(new String[] { "L: (", ", ", "), (", ")" }, false),
				WON = new Command(new String[] { "W: (", ", ", "), (", ")" }, false),
				MARK = new Command(new String[] { "M: (", ", ", ", ", ")" }, false),
				ERROR = new Command(new String[] { "ERROR" }, false);

		private ClientCommand(String[] blocks, boolean hasTrailingArg) {
			super(blocks, hasTrailingArg);
		}
	}

	public class Cell {
		public static final byte FLAG_NONE = 0, FLAG_MINE = 1, FLAG_UNKNOWN = 2;
		
		private boolean open;
		private byte number;
		private byte Flag;

		public Cell(boolean open, byte number, byte flag) {
			super();
			this.open = open;
			this.number = number;
			Flag = flag;
		}

		public boolean isOpen() {
			return open;
		}

		public byte getNumber() {
			return number;
		}

		public byte getFlag() {
			return Flag;
		}
	}

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

	public Cell[][] getField() {
		// TODO
		return null;
	}
}
