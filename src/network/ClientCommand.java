package network;

import lib.Command;

public enum ClientCommand extends Command {
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
	
	@Override
	public int getParameterCount() {
		int parameterCount = blocks.length;
		return (hasTrailingArg ? parameterCount : --parameterCount);
	}
	
	private String generateRegex() {
		String regex = "";
		for (int i = 0; i < blocks.length; i++) {
			regex += ".+" + blocks[i];
		}
		return (hasTrailingArg ? regex + ".+" : regex);
	}
	
	@Override
	public String generateCommand(Object[] args) {
		int parameterCount = getParameterCount();
		String commandString = "";
		if (parameterCount == 0 && (args == null || args.length == 0)) {
			return blocks[0];
		} else if (args.length == parameterCount){
			for (int i = 0; i < args.length; i++) {
				commandString += blocks[i] + args[i];
			}
			if (!hasTrailingArg) {
				commandString += blocks[blocks.length - 1];
			}
			return commandString;				
		} else {
			throw new IllegalArgumentException("Invalid parameter count");
		}
	}
	
	public boolean matches(String message) {
		return message.matches(generateRegex());
	}

	public String[] getParameters(String message) {
		if (this.matches(message)) {
			String splitRegex = blocks[0];
			for (int i = 1; i < blocks.length; i++) {
				splitRegex += "|" + blocks[i];
			}

			int parameterCount = getParameterCount();
			String[] untrimmedParameters = message.split(splitRegex),
					parameters = new String[parameterCount];
			int j = 0;
			for (int i = 0; i < parameterCount; i++) {
				while (untrimmedParameters[j].isEmpty()) {
					j++;
				}
				parameters[i] = untrimmedParameters[j];
				j++;
			}
			return parameters;
		} else {
			throw new IllegalArgumentException(
					"Message '" + message + "' does not match syntax '" + toString() + "'");
		}
	}
}