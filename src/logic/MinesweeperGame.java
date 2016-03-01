package logic;

import logic.MinesweeperGame.Field.Flag;

public class MinesweeperGame {
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

	private Field[][] fields;

	public MinesweeperGame(int width, int height) {
		this.fields = new Field[width][height];
	}

	public void open(int x, int y) {
		Field field = fields[x][y];
		field.open();
		if ()
	}

	private byte getNumber(int x, int y) {
		int number = 0;
		for (int i = 0; i < 3; i++) {
			if (fields[x + i][y].isMined()) {
				number++;
			}
		}
		
		// TODO rest of fields
		for (int i = 0; i < 3; i++) {
			if (fields[x + i][y].isMined()) {
				number++;
			}
		}
	}

	public void flag(int x, int y, Flag flag) {
		fields[x][y].flag(flag);
	}
}
