package network;

import lib.Client;
import lib.Command;
import network.MinesweeperServer.ServerCommand;

public class MinesweeperClient extends Client {
	public static class ClientCommand extends Command {
		public static final Command OK = new ClientCommand(new String[] { "OK" }, false),
				REGISTER = new ClientCommand(new String[] { "R: " }, true),
				DEREGISTER = new ClientCommand(new String[] { "D" }, false),
				NEW_GAME = new ClientCommand(new String[] { "N: (", ", ", ", ", ")" }, false),
				OPEN = new ClientCommand(new String[] { "O: (", ", ", ", ", ")" }, false),
				LOST = new ClientCommand(new String[] { "L: (", ", ", "), (", ")" }, false),
				WON = new ClientCommand(new String[] { "W: (", ", ", "), (", ")" }, false),
				MARK = new ClientCommand(new String[] { "M: (", ", ", ", ", ")" }, false),
				ERROR = new ClientCommand(new String[] { "ERROR" }, false);
		
		public static final Command[] COMMANDS = getSet(ClientCommand.class);

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
		
	}

	public void open(int x, int y) {
		send(ServerCommand.OPEN.generateCommand(new String[] { "" + x, "" + y }));
	}

	public void mark(int x, int y) {
		send(ServerCommand.MARK.generateCommand(new String[] { "" + x, "" + y }));
	}
}
