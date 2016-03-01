package logic;

import lib.Queue;
import logic.MinesweeperGame.Field.Flag;

public class MinesweeperGame {
	public enum Difficulty {
		EASY(10, 10, 30), MEDIUM(20, 20, 150), HARD(50, 50, 1500);
		
		private int width, height, mineCount;

		private Difficulty(int width, int height, int mineCount) {
			this.width = width;
			this.height = height;
			this.mineCount = mineCount;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public int getMineCount() {
			return mineCount;
		}
	}
	
	public class Field {
		public enum Flag {
			MINE, UNKNOWN;
		}

		private boolean mined, opened;
		private Flag flag;

		Field(boolean mined) {
			this.mined = mined;
		}

		public boolean isMined() {
			return mined;
		}

		public boolean isOpened() {
			return opened;
		}

		public Flag getFlag() {
			return flag;
		}

		public void open() {
			this.opened = true;
		}

		public void flag(Flag flag) {
			this.flag = flag;
		}
	}
	
	private class Coordinates {
		private int x, y;

		public Coordinates(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	private Field[][] fields;
	
	public MinesweeperGame(Difficulty difficulty) {
		this(difficulty.getWidth(), difficulty.getHeight(), difficulty.getMineCount());
	}

	public MinesweeperGame(int width, int height, int mineCount) {
		this.fields = new Field[width][height];
		// TODO: generate field
	}

	private Queue getNeighbors(Coordinates coordinates) {
		int x = coordinates.getX(), y = coordinates.getY();
		Queue neighbors = new Queue();
		int xLowerOffset = x > 0 ? -1 : 0, xHigherOffset = x < fields.length - 1 ? 1 : 0;
		boolean yAddLower = y > 0, yAddHigher = y < fields[x].length - 1;
		
		if (yAddLower) {
			for (int i = xLowerOffset; i <= xHigherOffset; i++) {
				neighbors.enqueue(new Coordinates(x + i, y - 1));
			}
		}
		if (xLowerOffset != 0) {
			neighbors.enqueue(new Coordinates(x + xLowerOffset, y));
		}
		if (xHigherOffset != 0) {
			neighbors.enqueue(new Coordinates(x + xHigherOffset, y));
		}
		if (yAddHigher) {
			for (int i = xLowerOffset; i <= xHigherOffset; i++) {
				neighbors.enqueue(new Coordinates(x + i, y + 1));
			}
		}
		return neighbors;
	}
	
	public void open(int x, int y) {
		open(new Coordinates(x, y));
	}

	private void open(Coordinates coordinates) {
		Field field = fields[coordinates.getX()][coordinates.getY()];
		field.open();
		if (getNumber(coordinates.getX(), coordinates.getY()) == 0) {
			Queue neighbors = getNeighbors(coordinates);
			while (!neighbors.isEmpty()) {
				open((Coordinates) neighbors.front());
				neighbors.dequeue();
			}
		}
	}
	
	public byte getNumber(int x, int y) {
		return getNumber(new Coordinates(x, y));
	}

	private byte getNumber(Coordinates coordinates) {
		byte number = 0;
		Queue neighbors = getNeighbors(coordinates);
		while (!neighbors.isEmpty()) {
			if (((Field) neighbors.front()).isMined()) {
				number++;
			}
			neighbors.dequeue();
		}
		return number;
	}

	public void flag(int x, int y, Flag flag) {
		fields[x][y].flag(flag);
	}
}
