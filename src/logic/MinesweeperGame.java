package logic;

import lib.List;
import lib.Queue;

public class MinesweeperGame {
	public interface UserInterface {
		public void mark(int x, int y, int mark);

		public void open(int x, int y, int number);

		public void lost(List mineCoordinates);

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

		boolean isMined() {
			return mined;
		}

		void mine() {
			this.mined = true;
		}

		public boolean isOpen() {
			return open;
		}

		public byte getNumber() {
			return number;
		}

		private void increaseNumber() {
			number++;
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
			return "[" + x + ", " + y + "]";
		}
	}

	private static final String GAME_ENDED = "The game has ended";

	private int mineCount;
	private Cell[][] field;
	private List mines, openingPending;
	private boolean started, ended;

	private UserInterface ui;

	public MinesweeperGame(Difficulty difficulty, UserInterface ui) {
		this(difficulty.getWidth(), difficulty.getHeight(), difficulty.getMineCount(), ui);
	}

	public MinesweeperGame(int width, int height, int mineCount, UserInterface ui) {
		if (mineCount <= width * height - 9) { // Opened cell must be zero
			this.mineCount = mineCount;
			this.ui = ui;

			field = new Cell[width][height];

			for (Cell[] column : field) {
				for (int i = 0; i < column.length; i++) {
					column[i] = new Cell();
				}
			}
		} else {
			throw new IllegalArgumentException(
					"mineCount must be small enough to make at least one zero-cell possible");
		}
	}

	private void generateField(Coordinates openedCoordinates) {
		mines = new List();
		{ // generate mines
			int x, y;
			for (int i = 1; i <= mineCount; i++) {
				// Find cell suitable for mine
				do { // TODO check if not opened
					x = (int) Math.round(Math.random() * (field.length - 1));
					y = (int) Math.round(Math.random() * (field[x].length - 1));
				} while (field[x][y].isMined()); // field must not have a mine
													// already

				mines.append(new Coordinates(x, y));
				field[x][y].mine();
			}
		}
		
		// get non-mines
		openingPending = new List();
		for (Cell[] column : field) {
			for (Cell cell : column) {
				if (!cell.isMined()) {
					openingPending.append(cell);
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
					field[currentNeighbor.getX()][currentNeighbor.getY()].increaseNumber();
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
		if (!ended) {
			open(new Coordinates(x, y));
			if (openingPending.isEmpty()) {
				ended = true;
				ui.won(mines);
			}
		} else {
			throw new IllegalStateException(GAME_ENDED);
		}
	}

	private void open(Coordinates coordinates) {
		if (!started) {
			generateField(coordinates);
		}

		try {
			Cell cell = field[coordinates.getX()][coordinates.getY()];
			cell.open();
			ui.open(coordinates.getX(), coordinates.getY(), cell.getNumber());

			if (!cell.isMined()) {
				openingPending.toFirst();
				while (openingPending.hasAccess()) {
					if (((Cell) openingPending.getObject()) == cell) {
						openingPending.remove();
						break;
					}
					openingPending.next();
				}
				
				if (cell.getNumber() == 0) {
					Queue neighbors = getNeighbors(coordinates);
					while (!neighbors.isEmpty()) {
						open((Coordinates) neighbors.front());
						neighbors.dequeue();
					}
				}
			} else {
				ended = true;
				ui.lost(mines);
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
		if (!ended) {
			field[x][y].mark(mark);
			ui.mark(x, y, mark);
		} else {
			throw new IllegalStateException(GAME_ENDED);
		}
	}
}
