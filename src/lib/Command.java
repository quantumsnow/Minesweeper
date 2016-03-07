package lib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Command {
	protected String[] blocks;
	protected boolean hasTrailingArg;
	
	public Command(String[] blocks, boolean hasTrailingArg) {
		this.blocks = blocks;
		this.hasTrailingArg = hasTrailingArg;
	}
	
	public String[] getBlocks() {
		return blocks;
	}

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
}
