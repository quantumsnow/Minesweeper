package lib;

import java.lang.reflect.Field;

public abstract class Command {
	public static abstract class Server extends Command {
		public static abstract class Action extends Command.Action {
			protected abstract void run(lib.Server server, String ip, int port, String[] args);
		}
		
		public Server(String[] blocks, boolean hasTrailingArg, Server.Action action) {
			super(blocks, hasTrailingArg, action);
		}

		public void run(lib.Server server, String ip, int port, String message) {
			((Server.Action) action).run(server, ip, port, getArgs(message));
		}
	}
	
	public static abstract class Client extends Command {
		public static abstract class Action extends Command.Action {
			protected abstract void run(lib.Client client, String[] args);
		}
		
		public Client(String[] blocks, boolean hasTrailingArg, Client.Action action) {
			super(blocks, hasTrailingArg, action);
		}

		public void run(lib.Client client, String message) {
			((Client.Action) action).run(client, getArgs(message));
		}
	}
	
	protected static abstract class Action {
	}
	
	public static abstract class Block {
		public static class Constant extends Block {
			private String content;
			
			public Constant(String content) {
				this.content = content;
			}
			
			@Override
			public String getRegex() {
				return content;
			}
		}
		
		public static class Parameter<E> extends Block {
			private Class<E> type;
			
			public Parameter() {
				type = E.;
			}
			
			@Override
			public String getRegex() {
				
			}
		}
		
		public abstract String getRegex();
	}

	protected String[] blocks;
	protected boolean hasTrailingArg;
	protected Action action;

	public Command(String[] blocks, boolean hasTrailingArg, Action action) {
		this.blocks = blocks;
		this.hasTrailingArg = hasTrailingArg;
		this.action = action;
	}

	protected static Command[] getSet(Class<? extends Command> type) {
		Field[] fields = type.getFields();
		List commandList = new List();
		int setSize = 0;
		for (Field field : fields) {
			try {
				field.get(null);
				setSize++;
			} catch (Exception e) {
			}
		}

		Command[] commands = new Command[setSize];
		commandList.toFirst();
		for (int i = 0; i < setSize; i++) {
			commands[i] = (Command) commandList.getObject();
		}

		return commands;
	}

	public String[] getBlocks() {
		return blocks;
	}

	public int getParameterCount() {
		int parameterCount = blocks.length;
		return (hasTrailingArg ? parameterCount : --parameterCount);
	}

	private String generateRegex() {
		String regex = blocks[0];
		for (int i = 1; i < blocks.length; i++) {
			regex += ".+" + blocks[i];
		}
		return (hasTrailingArg ? regex + ".+" : regex);
	}

	public String generateCommand(Object[] args) {
		int parameterCount = getParameterCount();
		String commandString = "";
		if (parameterCount == 0 && (args == null || args.length == 0)) {
			return blocks[0];
		} else if (args.length == parameterCount) {
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

	public String[] getArgs(String message) {
		if (this.matches(message)) {
			String splitRegex = blocks[0];
			for (int i = 1; i < blocks.length; i++) {
				splitRegex += "|" + blocks[i];
			}

			int parameterCount = getParameterCount();
			String[] untrimmedParameters = message.split(splitRegex), parameters = new String[parameterCount];
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
			throw new IllegalArgumentException("Message '" + message + "' does not match syntax '" + toString() + "'");
		}
	}
}
