package network;

import lib.Protocol;

public class MinesweeperProtocol implements Protocol {
	public enum ServerCommand implements Protocol.Command {
		REGISTER(new String[] { "R: " }, true), DEREGISTER(new String[] { "D" }, false), NEW_GAME_PRESET(
				new String[] { "N: " }, true), NEW_GAME_CUSTOM(new String[] { "N: (", ", ", ", ", ")" }, false), OPEN(
						new String[] { "O: (", ", ", ")" },
						false), MARK(new String[] { "M: (", ", ", ", ", ")" }, false);

		private String[] blocks;
		private boolean hasTrailingArg;

		ServerCommand(String[] blocks, boolean hasTrailingArg) {
			this.blocks = blocks;
			this.hasTrailingArg = hasTrailingArg;
		}

		@Override
		public String[] getBlocks() {
			return blocks;
		}
	}

	public enum ClientCommand implements Protocol.Command {
		OK(new String[] { "OK" }, false), REGISTER(ServerCommand.REGISTER.blocks,
				ServerCommand.REGISTER.hasTrailingArg), DEREGISTER(ServerCommand.DEREGISTER.blocks,
						ServerCommand.DEREGISTER.hasTrailingArg), NEW_GAME(ServerCommand.NEW_GAME_CUSTOM.blocks,
								ServerCommand.NEW_GAME_CUSTOM.hasTrailingArg), OPEN(
										new String[] { "O: (", ", ", ", ", ")" },
										ServerCommand.OPEN.hasTrailingArg), LOST(
												new String[] { "L: (", ", ", "), (", ")" },
												false), WON(new String[] { "W: (", ", ", "), (", ")" }, false), MARK(
														ServerCommand.MARK.blocks,
														ServerCommand.MARK.hasTrailingArg), ERROR(
																new String[] { "ERROR" }, false);

		private String[] blocks;
		private boolean hasTrailingArg;

		ClientCommand(String[] blocks, boolean hasTrailingArg) {
			this.blocks = blocks;
			this.hasTrailingArg = hasTrailingArg;
		}

		@Override
		public String[] getBlocks() {
			return blocks;
		}
	}

	@Override
	public String generateCommand(NodeType destinationType, Command command, Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}
}
