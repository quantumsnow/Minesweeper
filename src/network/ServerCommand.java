package network;

import lib.Protocol;

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