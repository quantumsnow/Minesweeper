package lib;

public abstract class Command {
	public String[] getBlocks();

	public int getParameterCount();
	
	public String generateCommand(Object[] args);
}
