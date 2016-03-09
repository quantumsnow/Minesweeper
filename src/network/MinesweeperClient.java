package network;

import lib.Client;
import lib.Command;
import lib.List;
import logic.MinesweeperGame.Coordinates;
import network.MinesweeperServer.ServerCommand;

public class MinesweeperClient extends Client {
	public static class ClientCommand extends Command.Client {
		public static final Command OK = new ClientCommand(new String[] { "OK" }, false, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
			}
		}), REGISTER = new ClientCommand(new String[] { "R: " }, true, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.addPlayer(args[0]);
			}
		}), PLAYERS = new ClientCommand(new String[] { "P: " }, true, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				String[] players = args[0].split(", ");
				for (String player : players) {
					((MinesweeperClient) client).gui.addPlayer(player);
				}
			}
		}), FIELD = new ClientCommand(new String[] { "F: (" + ")" }, false, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				String[] rows = args[0].split("[|], [|]"), fields;
				int status;
				for (int y = 0; y < rows.length; y++) {
					fields = rows[y].split(", ");
					for (int x = 0; x < fields.length; x++) {
						status = Integer.parseInt(fields[x]);
						if (status >= 0) {
							((MinesweeperClient) client).gui.open(x, y, status);
						} else {
							((MinesweeperClient) client).gui.mark(x, y, status);
						}
					}
				}
			}
		}), DEREGISTER = new ClientCommand(new String[] { "D: " }, false, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.removePlayer(args[0]);
			}
		}), NEW_GAME = new ClientCommand(new String[] { "N: (", ", ", ", ", ")" }, false, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.newGame(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
						Integer.parseInt(args[2]));
			}
		}), OPEN = new ClientCommand(new String[] { "O: (", ", ", ", ", ")" }, false, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.open(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			}
		}), LOST = new ClientCommand(new String[] { "L: (", ", ", "), (", ")" }, false, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.lost(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
						parseCellList(args[2]));
			}
		}), WON = new ClientCommand(new String[] { "W: (", ", ", "), (", ")" }, false, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.won(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
						parseCellList(args[2]));
			}
		}), MARK = new ClientCommand(new String[] { "M: (", ", ", ", ", ")" }, false, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.mark(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
						Integer.parseInt(args[2]));
			}
		}), ERROR = new ClientCommand(new String[] { "ERROR" }, false, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.serverError();
			}
		});

		public static final Command[] SET = getSet(ClientCommand.class);

		private ClientCommand(String[] blocks, boolean hasTrailingArg, Command.Client.Action action) {
			super(blocks, hasTrailingArg, action);
		}

		private static List parseCellList(String list) {
			String[] stringCells = list.split("\\), \\(|\\(|\\)");
			List cells = new List();
			String[] cellCoordinates;
			for (String cell : stringCells) {
				cellCoordinates = cell.split(", ");
				cells.append(
						new Coordinates(Integer.parseInt(cellCoordinates[0]), Integer.parseInt(cellCoordinates[1])));
			}
			return cells;
		}
	}

//	public class Cell {
//		public static final byte FLAG_NONE = 0, FLAG_MINE = 1, FLAG_UNKNOWN = 2;
//
//		private boolean open;
//		private byte number;
//		private byte Flag;
//
//		public Cell(boolean open, byte number, byte flag) {
//			super();
//			this.open = open;
//			this.number = number;
//			Flag = flag;
//		}
//
//		public boolean isOpen() {
//			return open;
//		}
//
//		public byte getNumber() {
//			return number;
//		}
//
//		public byte getFlag() {
//			return Flag;
//		}
//	}

	private MinesweeperGUI gui;

	public MinesweeperClient(String ip, int port, MinesweeperGUI gui) {
		super(ip, port);
		this.gui = gui;
	}

	@Override
	public void processMessage(String message) {
		for (Command command : ClientCommand.SET) {
			try {
				((ClientCommand) command).run(this, message);
				return;
			} catch (IllegalArgumentException e) {
			}
		}
	}

	public void open(int x, int y) {
		send(ServerCommand.OPEN.generateCommand(new String[] { "" + x, "" + y }));
	}

	public void mark(int x, int y) {
		send(ServerCommand.MARK.generateCommand(new String[] { "" + x, "" + y }));
	}
}
