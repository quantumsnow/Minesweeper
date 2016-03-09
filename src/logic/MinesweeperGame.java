package logic;

import lib.List;
import lib.Queue;

public class MinesweeperGame {
	public interface UserInterface {
		public void mark(int x, int y, int mark);
		
		public void open(int x, int y);
		
		public void lost(int x, int y, List mineCoordinates);

		public void won(List mineCoordinates);
	}

	public enum Difficulty {
		EASY(10, 10, 30), MEDIUM(20, 20, 150), HARD(50, 50, 1500);

		private int width, height, mineCount;
		
		public static Difficulty fromByte(byte preset) {
			switch (preset) {
			case 0:
				return EASY;
			case 1:
				return MEDIUM;
			case 2:
				return HARD;
			default:
				throw new IllegalArgumentException("Invalid difficulty constant: " + preset);
			}
		}

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

	public class Cell {
		public static final int MARK_NONE = -1, MARK_MINE = -2, MARK_UNKNOWN = -3;

		private boolean mined, open;
		private int mark;
		private byte number;

		private Cell(boolean mined) {
			this.mined = mined;
		}

		boolean isMined() {
			return mined;
		}

		public boolean isOpen() {
			return open;
		}

		public byte getNumber() {
			return number;
		}

		private void increaseNumber() {
			if (!mined) {
				number++;
			} else {
				throw new UnsupportedOperationException("Cell is mined");
			}
		}

		public int getMark() {
			return mark;
		}

		private void open() {
			if (!open) {
				this.open = true;
			} else {
				throw new IllegalStateException("Already open");
			}
		}

		private void mark(int mark) {
			this.mark = mark;
		}
	}

	public static class Coordinates {
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
		
		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}

	private int width, height, mineCount;
	private Cell[][] field;
	private List mines;
	private boolean ended;

	private UserInterface ui;

	public MinesweeperGame(Difficulty difficulty, UserInterface ui) {
		this(difficulty.getWidth(), difficulty.getHeight(), difficulty.getMineCount(), ui);
	}

	public MinesweeperGame(int width, int height, int mineCount, UserInterface ui) {
		if (mineCount <= width * height - 9) { // Opened cell must be zero
			this.width = width;
			this.height = height;
			this.mineCount = mineCount;
			this.ui = ui;
		} else {
			throw new IllegalArgumentException(
					"mineCount must be small enough to make at least one zero-cell possible");
		}
	}

	private void generateField(Coordinates openedCoordinates) {
		field = new Cell[width][height];

		field[openedCoordinates.getX()][openedCoordinates.getY()] = new Cell(false);
		{ // Opened cell must be zero
			Queue neighbors = getNeighbors(openedCoordinates);
			Coordinates currentNeighbor;
			while (!neighbors.isEmpty()) {
				currentNeighbor = (Coordinates) neighbors.front();
				field[currentNeighbor.getX()][currentNeighbor.getY()] = new Cell(false);
				neighbors.dequeue();
			}
		}

		// fill field
		mines = new List();
		{ // generate mines
			int x, y;
			for (int i = 0; i <= mineCount; i++) {
				// Find cell suitable for mine
				do {
					x = (int) Math.round(Math.random() * width);
					y = (int) Math.round(Math.random() * height);
				} while (field[x][y] != null); // field must not have a mine
												// already

				mines.append(new Coordinates(x, y));
				field[x][y] = new Cell(true);
			}
		}

		// fill empty cells
		for (Cell[] column : field) {
			for (int i = 0; i < column.length; i++) {
				if (column[i] == null) {
					column[i] = new Cell(false);
				}
			}
		}

		{ // generate numbers
			Queue neighbors;
			Coordinates currentNeighbor;
			mines.toFirst();
			while (mines.hasAccess()) {
				neighbors = getNeighbors((Coordinates) mines.getObject());
				while (!neighbors.isEmpty()) {
					currentNeighbor = (Coordinates) neighbors.front();
					try {
						field[currentNeighbor.getX()][currentNeighbor.getY()].increaseNumber();
					} catch (UnsupportedOperationException e) {
					}
					neighbors.dequeue();
				}
				mines.next();
			}
		}
	}

	private Queue getNeighbors(Coordinates coordinates) {
		int x = coordinates.getX(), y = coordinates.getY();
		Queue neighbors = new Queue();
		int xLowerOffset = x > 0 ? -1 : 0, xHigherOffset = x < field.length - 1 ? 1 : 0;
		boolean yAddLower = y > 0, yAddHigher = y < field[x].length - 1;

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

	public Cell[][] getField() {
		return field;
	}
	
	public void open(int x, int y) {
		open(new Coordinates(x, y));
		
		// check if won
		for (Cell[] column : field) {
			for (Cell cell : column) {
				if (!cell.isMined() && !cell.isOpen()) {
					return;
				}
			}
		}
		ended = true;
		ui.won(mines);
	}

	private void open(Coordinates coordinates) {
		if (field == null) {
			generateField(coordinates);
		}

		try {
			Cell cell = field[coordinates.getX()][coordinates.getY()];
			cell.open();

			if (!cell.isMined()) {
				ui.open(coordinates.getX(), coordinates.getY());
				if (cell.getNumber() == 0) {
					Queue neighbors = getNeighbors(coordinates);
					while (!neighbors.isEmpty()) {
						open((Coordinates) neighbors.front());
						neighbors.dequeue();
					}
				}
			} else {
				ended = true;
				ui.lost(coordinates.getX(), coordinates.getY(), mines);
			}
		} catch (IllegalStateException e) {
		}
	}

	// private byte getNumber(int x, int y) {
	// if (field == null || !field[x][y].isOpened()) {
	// throw new IllegalStateException("Cell must be open.");
	// } else {
	// return field[x][y].getNumber();
	// }
	// }

	public void mark(int x, int y, int mark) {
		try {
			field[x][y].mark(mark);
			ui.mark(x, y, mark);
		} catch (NullPointerException e) {
			throw new IllegalStateException("Can not mark before a cell is opened.");
		}
	}
}
