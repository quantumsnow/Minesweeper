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
		EASY(0, 10, 10, 10), MEDIUM(1, 15, 15, 40), HARD(2, 20, 20, 100);

		private int number;
		private int width, height, mineCount;

		public static Difficulty fromNumber(int number) {
			for (Difficulty difficulty : values()) {
				if (difficulty.number == number) {
					return difficulty;
				}
			}
			throw new IllegalArgumentException("No preset with number: " + number);
		}

		private Difficulty(int number, int width, int height, int mineCount) {
			this.number = number;
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
		
		public int getNumber() {
			return number;
		}
	}
	
	public enum State {
		RUNNING, WON, LOST;
	}

	public class Cell {
		public static final int MARK_NONE = -1, MARK_MINE = -2, MARK_UNKNOWN = -3;

		private boolean mined, open;
		private int mark;
		private byte number;
		
		private Cell() {
			mark = MARK_NONE;
		}

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
		
		@Override
		public String toString() {
			if (mined) {
				return "x";
			} else {
				return "" + number;
			}
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
		
		@Override
		public boolean equals(Object object) {
			try {
				Coordinates coordinates = (Coordinates) object;
				return x == coordinates.x && y == coordinates.y;
			} catch (ClassCastException e) {
				return super.equals(object);
			}
		}
	}

	private static final String GAME_ENDED = "The game has ended";

	private int mineCount;
	private Cell[][] field;
	private List mines, openingPending;
	private boolean generated;
	private State state;
	private Coordinates lossTrigger;

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
			
			state = State.RUNNING;
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
				do {
					x = (int) Math.round(Math.random() * (field.length - 1));
					y = (int) Math.round(Math.random() * (field[x].length - 1));
				} while (field[x][y].isMined()
						|| (x >= openedCoordinates.getX() - 1 && x <= openedCoordinates.getX() + 1
								&& y >= openedCoordinates.getY() - 1 && y <= openedCoordinates.getY() + 1)); // field
																												// must
																												// not
																												// have
																												// a
																												// mine
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
		int xMinOffset = x > 0 ? -1 : 0, xMaxOffset = x < field.length - 1 ? 1 : 0;
		boolean yAddLower = y > 0, yAddHigher = y < field[x].length - 1;

		if (yAddLower) {
			for (int xOffset = xMinOffset; xOffset <= xMaxOffset; xOffset++) {
				neighbors.enqueue(new Coordinates(x + xOffset, y - 1));
			}
		}
		if (xMinOffset != 0) {
			neighbors.enqueue(new Coordinates(x + xMinOffset, y));
		}
		if (xMaxOffset != 0) {
			neighbors.enqueue(new Coordinates(x + xMaxOffset, y));
		}
		if (yAddHigher) {
			for (int xOffset = xMinOffset; xOffset <= xMaxOffset; xOffset++) {
				neighbors.enqueue(new Coordinates(x + xOffset, y + 1));
			}
		}
		return neighbors;
	}

	public Cell[][] getField() {
		return field;
	}

	public int getMineCount() {
		return mineCount;
	}
	
	public State getState() {
		return state;
	}
	
	public Coordinates getLossTrigger() {
		switch (state) {
		case LOST:
			return lossTrigger;
		default:
			throw new IllegalStateException("The game is still running");
		}
	}
	
	public List getMineCoordinates() throws IllegalAccessException {
		if (state == State.RUNNING) {
			throw new IllegalAccessException("The game is still running");
		} else {
			return mines;
		}
	}

	public void open(int x, int y) {
		if (state == State.RUNNING) {
			open(new Coordinates(x, y));
			if (openingPending.isEmpty()) {
				state = State.WON;
				ui.won(mines);
			}
		} else {
			throw new IllegalStateException(GAME_ENDED);
		}
	}

	private void open(Coordinates coordinates) {
		if (!generated) {
			generateField(coordinates);
			generated = true;
		}

		try {
			Cell cell = field[coordinates.getX()][coordinates.getY()];
			cell.open();

			if (!cell.isMined()) {
				ui.open(coordinates.getX(), coordinates.getY(), cell.getNumber());
				
				openingPending.toFirst();
				while (openingPending.hasAccess()) {
					if (((Cell) openingPending.getObject()) == cell) {
						openingPending.remove();
						break;
					} else {
						openingPending.next();
					}
				}

				if (cell.getNumber() == 0) {
					Queue neighbors = getNeighbors(coordinates);
					while (!neighbors.isEmpty()) {
						open((Coordinates) neighbors.front());
						neighbors.dequeue();
					}
				}
			} else {
				state = State.LOST;
				lossTrigger = coordinates;
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
		if (state == State.RUNNING) {
			field[x][y].mark(mark);
			ui.mark(x, y, mark);
		} else {
			throw new IllegalStateException(GAME_ENDED);
		}
	}
}
