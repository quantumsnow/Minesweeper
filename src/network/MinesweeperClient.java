package network;

import lib.Client;
import lib.List;
import logic.MinesweeperGame.Coordinates;

public class MinesweeperClient extends Client {
	public static class Command extends lib.Command.Client {
		public static final Command REGISTER = new Command(new String[] { "R: " }, true, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.addPlayer(args[0]);
			}
		}), PLAYERS = new Command(new String[] { "P: " }, true, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				String[] players = args[0].split(", ");
				for (String player : players) {
					((MinesweeperClient) client).gui.addPlayer(player);
				}
			}
		}), FIELD = new Command(new String[] { "F: " }, true, new Action() {
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
		}), DEREGISTER = new Command(new String[] { "D: " }, true, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.removePlayer(args[0]);
			}
		}), NEW_GAME = new Command(new String[] { "N: ", ", ", ", " }, true, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.setField(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
						Integer.parseInt(args[2]));
			}
		}), OPEN = new Command(new String[] { "O: ", ", ", ", " }, true, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.open(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			}
		}), LOST = new Command(new String[] { "L: " }, true, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.lost(parseCellList(args[0]));
			}
		}), WON = new Command(new String[] { "W" }, false, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.won();
			}
		}), MARK = new Command(new String[] { "M: ", ", ", ", " }, true, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.mark(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
						Integer.parseInt(args[2]));
			}
		}), ERROR = new Command(new String[] { "ERROR" }, false, new Action() {
			@Override
			protected void run(lib.Client client, String[] args) {
				((MinesweeperClient) client).gui.serverError();
			}
		});

		public static final Command[] SET = { OPEN, MARK, REGISTER, DEREGISTER, WON, LOST, NEW_GAME, FIELD, PLAYERS, ERROR };

		private Command(String[] blocks, boolean hasTrailingArg, lib.Command.Client.Action action) {
			super(blocks, hasTrailingArg, action);
		}

		private static List parseCellList(String list) {
			String[] stringCells = list.substring(1, list.length() - 1).split("\\], \\[");
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

	private MinesweeperGUI gui;

	public MinesweeperClient(String ip, int port, String nick, MinesweeperGUI gui) {
		super(ip, port);
		this.gui = gui;
		send(MinesweeperServer.Command.REGISTER.generateCommand(new String[] { nick }));
	}

	@Override
	public void processMessage(String message) {
		for (Command command : Command.SET) {
			try {
				command.run(this, message);
				return;
			} catch (IllegalArgumentException e) {
			}
		}
	}

	public void open(int x, int y) {
		send(MinesweeperServer.Command.OPEN.generateCommand(new String[] { "" + x, "" + y }));
	}

	public void mark(int x, int y, int mark) {
		send(MinesweeperServer.Command.MARK.generateCommand(new String[] { "" + x, "" + y, "" + mark }));
	}
	
	public void newGame(int width, int height, int mineCount) {
		send(MinesweeperServer.Command.NEW_GAME_CUSTOM.generateCommand(new Integer[] { width, height, mineCount }));
	}
}
