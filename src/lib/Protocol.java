package lib;

public interface Protocol {
	public enum NodeType {
		SERVER, CLIENT;
	}
	
	public interface Command {
		public String[] getBlocks();
	}
	
	public String generateCommand(NodeType destinationType, Command command, Object[] args);
}
