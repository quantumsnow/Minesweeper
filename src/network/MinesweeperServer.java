package network;

import lib.Command;
import lib.List;
import lib.Server;
import logic.MinesweeperGame;
import logic.MinesweeperGame.Coordinates;
import logic.MinesweeperGame.UserInterface;
import network.MinesweeperClient.ClientCommand;

public class MinesweeperServer extends Server implements UserInterface {
	public static class ServerCommand extends Command.Server {
		public static final Command REGISTER = new ServerCommand(new String[] { "R: " }, true, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				msServer.goToPlayer(ip, port);
				try {
					((Player) msServer.players.getObject()).register(args[0]);
					msServer.sendToAll(MinesweeperClient.ClientCommand.REGISTER.generateCommand(args));
				} catch (IllegalStateException e) {
					msServer.send(ip, port, MinesweeperClient.ClientCommand.ERROR.generateCommand(null));
				}
			}
		}), DEREGISTER = new ServerCommand(new String[] { "D" }, false, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				msServer.goToPlayer(ip, port);
				msServer.sendToAll(MinesweeperClient.ClientCommand.DEREGISTER.generateCommand(new String[] { ((Player) msServer.players.getObject()).getNick() }));
				msServer.players.remove();
			}
		}), NEW_GAME_PRESET = new ServerCommand(new String[] { "N: " }, true, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				msServer.game = new MinesweeperGame(MinesweeperGame.Difficulty.fromByte(Byte.parseByte(args[0])),
						msServer);
				// TODO notify players
			}
		}), NEW_GAME_CUSTOM = new ServerCommand(new String[] { "N: (", ", ", ", ", ")" }, false, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				msServer.game = new MinesweeperGame(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
						Integer.parseInt(args[2]), msServer);
				// TODO notify players
			}
		}), OPEN = new ServerCommand(new String[] { "O: (", ", ", ")" }, false, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				msServer.game.open(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			}
		}), MARK = new ServerCommand(new String[] { "M: (", ", ", ", ", ")" }, false, new Action() {
			@Override
			protected void run(lib.Server server, String ip, int port, String[] args) {
				MinesweeperServer msServer = (MinesweeperServer) server;
				msServer.game.mark(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Byte.parseByte(args[3]));
			}
		});

		public static final Command[] SET = getSet(ClientCommand.class);

		private ServerCommand(String[] blocks, boolean hasTrailingArg, Command.Server.Action action) {
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
			if (nick == null) {
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
		for (Command command : ServerCommand.SET) {
			try {
				((ServerCommand) command).run(this, ip, port, message);
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
		sendToAll(MinesweeperClient.ClientCommand.MARK.generateCommand(new Object[] { x, y, mark }));
	}

	@Override
	public void open(int x, int y) {
		sendToAll(MinesweeperClient.ClientCommand.OPEN.generateCommand(new Object[] { x, y }));
	}

	@Override
	public void lost(int x, int y, List coordinates) {
		sendToAll(MinesweeperClient.ClientCommand.LOST
				.generateCommand(new Object[] { x, y, buildCoordinateList(coordinates) }));
	}

	@Override
	public void won(List coordinates) {
		sendToAll(MinesweeperClient.ClientCommand.LOST
				.generateCommand(new Object[] { buildCoordinateList(coordinates) }));
	}

	private static String buildCoordinateList(List coordinates) {
		if (!coordinates.isEmpty()) {
			coordinates.toFirst();
			String strList = ((Coordinates) coordinates.getObject()).toString();
			coordinates.next();
			while (coordinates.hasAccess()) {
				strList += ", " + ((Coordinates) coordinates.getObject()).toString();
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
		}
	}
}
