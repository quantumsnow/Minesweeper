package network;

import lib.List;
import lib.Server;
import logic.MinesweeperGame;
import logic.MinesweeperGame.Cell;
import logic.MinesweeperGame.Coordinates;
import logic.MinesweeperGame.Difficulty;

public class MinesweeperServer extends Server implements MinesweeperGame.UserInterface {
	public static class Command extends lib.Command.Server {
		public static final Command REGISTER = new Command(new String[] { "R: " }, true, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				try {
					msServer.goToPlayer(ip, port);
					((Player) msServer.players.getObject()).register(args[0]);
					msServer.sendToAll(MinesweeperClient.Command.REGISTER.generateCommand(args));

					// send players
					String players = "";
					if (!msServer.players.isEmpty()) {
						msServer.players.toFirst();
						players = ((Player) msServer.players.getObject()).getNick();
						msServer.players.next();
						while (msServer.players.hasAccess()) {
							players += ", " + ((Player) msServer.players.getObject()).getNick();
							msServer.players.next();
						}
					}
					msServer.send(ip, port,
							MinesweeperClient.Command.PLAYERS.generateCommand(new String[] { players }));
					
					if (msServer.game != null) {
						// send field
						Cell[][] field = msServer.game.getField();
						String fieldString = "([" + getStateInt(field[0][0]);
						for (int i = 1; i < field[0].length; i++) {
							fieldString += ", " + getStateInt(field[i][0]);
						}
						fieldString += "]";
						for (int i = 1; i < field[0].length; i++) { // row
							fieldString += ", [" + getStateInt(field[0][i]);
							for (int j = 1; j < field.length; j++) { // column
								fieldString += ", " + getStateInt(field[j][i]);
							}
							fieldString += "]";
						}
						fieldString += ")";
						msServer.send(ip, port,
								MinesweeperClient.Command.FIELD.generateCommand(new String[] { fieldString }));
					}
				} catch (IllegalStateException e) {
					msServer.send(ip, port, MinesweeperClient.Command.ERROR.generateCommand(null));
				}
			}
			
			private int getStateInt(Cell cell) {
				if (cell.isOpen()) {
					return cell.getNumber();
				} else {
					return cell.getMark();
				}
			}
		}), DEREGISTER = new Command(new String[] { "D" }, false, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				msServer.goToPlayer(ip, port);
				msServer.sendToAll(MinesweeperClient.Command.DEREGISTER
						.generateCommand(new String[] { ((Player) msServer.players.getObject()).getNick() }));
				msServer.players.remove();
			}
		}), NEW_GAME_PRESET = new Command(new String[] { "N_P: " }, true, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				Difficulty difficulty = MinesweeperGame.Difficulty.fromByte(Byte.parseByte(args[0]));
				msServer.game = new MinesweeperGame(difficulty, msServer);
				msServer.notifyPlayersOfNewGame(difficulty.getWidth(), difficulty.getHeight(), difficulty.getMineCount());
			}
		}), NEW_GAME_CUSTOM = new Command(new String[] { "N: ", ", ", ", " }, true, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				int width = Integer.parseInt(args[0]), height = Integer.parseInt(args[1]),
						mineCount = Integer.parseInt(args[2]);
				msServer.game = new MinesweeperGame(width, height, mineCount, msServer);
				msServer.notifyPlayersOfNewGame(width, height, mineCount);
			}
		}), OPEN = new Command(new String[] { "O: ", ", " }, true, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				msServer.game.open(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			}
		}), MARK = new Command(new String[] { "M: ", ", ", ", " }, true, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				msServer.game.mark(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Byte.parseByte(args[2]));
			}
		});

		public static final Command[] SET = { OPEN, MARK, REGISTER, DEREGISTER, NEW_GAME_PRESET, NEW_GAME_CUSTOM };

		private Command(String[] blocks, boolean hasTrailingArg, lib.Command.Server.Action action) {
			super(blocks, hasTrailingArg, action);
		}
	}

	private class Player {
		private String nick, ip;
		private int port;

		public Player(String ip, int port) {
			super();
			this.ip = ip;
			this.port = port;
		}

		public String getNick() {
			return nick;
		}

		public String getIp() {
			return ip;
		}

		public int getPort() {
			return port;
		}

		void register(String nick) {
			if (this.nick == null) {
				this.nick = nick;
			} else {
				throw new IllegalStateException("Already registered");
			}
		}
	}

	private List players;
	private MinesweeperGame game;

	public MinesweeperServer(int port) {
		super(port);
		players = new List();
	}

	@Override
	public void processNewConnection(String ip, int port) {
		players.append(new Player(ip, port));
	}

	@Override
	public void processMessage(String ip, int port, String message) {
		for (Command command : Command.SET) {
			try {
				command.run(this, ip, port, message);
				return;
			} catch (IllegalArgumentException e) {
			}
		}
	}

	@Override
	public void processClosedConnection(String ip, int port) {
		goToPlayer(ip, port);
		players.remove();
	}

	@Override
	public void mark(int x, int y, int mark) {
		sendToAll(MinesweeperClient.Command.MARK.generateCommand(new Integer[] { x, y, mark }));
	}

	@Override
	public void open(int x, int y, int number) {
		sendToAll(MinesweeperClient.Command.OPEN.generateCommand(new Integer[] { x, y, number }));
	}

	@Override
	public void lost(List coordinates) {
		sendToAll(MinesweeperClient.Command.LOST
				.generateCommand(new Object[] { buildCoordinateList(coordinates) }));
	}

	@Override
	public void won(List coordinates) {
		sendToAll(MinesweeperClient.Command.WON
				.generateCommand(null));
	}

	private static String buildCoordinateList(List coordinates) {
		if (!coordinates.isEmpty()) {
			coordinates.toFirst();
			String strList = ((Coordinates) coordinates.getObject()).toString();
			coordinates.next();
			while (coordinates.hasAccess()) {
				strList += ", " + ((Coordinates) coordinates.getObject()).toString();
				coordinates.next();
			}
			return strList;
		} else {
			return "";
		}
	}

	private void goToPlayer(String ip, int port) {
		Player currentPlayer;
		players.toFirst();
		while (players.hasAccess()) {
			currentPlayer = (Player) players.getObject();
			if (currentPlayer.getIp().equals(ip) && currentPlayer.getPort() == port) {
				return;
			}
			players.next();
		}
	}

	private void notifyPlayersOfNewGame(int width, int height, int mineCount) {
		sendToAll(MinesweeperClient.Command.NEW_GAME.generateCommand(new Integer[] { width, height, mineCount }));
	}
}
