package network;

import lib.Command;
import lib.List;
import lib.Server;
import logic.MinesweeperGame.UserInterface;
import network.MinesweeperClient.ClientCommand;

public class MinesweeperServer extends Server implements UserInterface {
	public static class ServerCommand extends Command {
		public static final Command REGISTER = new ServerCommand(new String[] { "R: " }, true),
				DEREGISTER = new ServerCommand(new String[] { "D" }, false),
				NEW_GAME_PRESET = new ServerCommand(new String[] { "N: " }, true),
				NEW_GAME_CUSTOM = new ServerCommand(new String[] { "N: (", ", ", ", ", ")" }, false),
				OPEN = new ServerCommand(new String[] { "O: (", ", ", ")" }, false),
				MARK = new ServerCommand(new String[] { "M: (", ", ", ", ", ")" }, false);
		
		public static final Command[] SET = getSet(ClientCommand.class);

		private ServerCommand(String[] blocks, boolean hasTrailingArg) {
			super(blocks, hasTrailingArg);
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
			this.nick = nick;
		}

		boolean isRegistered() {
			return nick != null;
		}
	}

	private List players;

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
		// TODO Auto-generated method stub

	}

	@Override
	public void processClosedConnection(String ip, int port) {
		Player currentPlayer;
		players.toFirst();
		while (players.hasAccess()) {
			currentPlayer = (Player) players.getObject();
			if (currentPlayer.getIp().equals(ip) && currentPlayer.getPort() == port) {
				players.remove();
				return;
			}
		}
	}

	@Override
	public void mark(int x, int y, int mark) {
		// TODO Auto-generated method stub

	}

	@Override
	public void open(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lost(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void won() {
		// TODO Auto-generated method stub

	}
}
