/**
 * The Battleships class stores the important information for each of the
 * computer's randomized ships. Also stores the starting row location, starting
 * column location, ship symbol, ship size, and if it is horizontal.
 */

public class Battleships {

	private int row;
	private int col;
	private char charSymbol;
	private int shipSize;
	private boolean horizontal, sunk = false;
	private boolean[] hit = new boolean[shipSize];

	public Battleships(int row, int col, char charSymbol, int shipSize,
			boolean horizontal) {
		this.row = row;
		this.col = col;
		this.charSymbol = charSymbol;
		this.shipSize = shipSize;
		this.horizontal = horizontal;
		for (int i = 0; i < shipSize; i++) {
			hit[i] = false;
		}
	}

	/**
	 * This constructor takes a String in the same format as output by the
	 * .toString method and prepares the fields
	 * 
	 * @param shipData
	 *            String in the .toString format
	 */
	public Battleships(String shipData) {
		String[] data = shipData.split(", ");
		if (data.length != 6) {
			throw new IllegalArgumentException(
					"Invalid data String.\n Data String is: " + shipData);
		}
		this.row = Integer.parseInt(data[0]);
		this.col = Integer.parseInt(data[1]);
		this.charSymbol = data[2].charAt(0);
		this.shipSize = Integer.parseInt(data[3]);
		this.horizontal = Boolean.parseBoolean(data[4]);
		decodeHitArray(Integer.parseInt(data[5]));
	}

	@Override
	public String toString() {
		// For testing & saving
		return ("" + row + ", " + col + ", " + charSymbol + ", " + shipSize
				+ ", " + horizontal + ", " + encodeHitArray());
	}

	// Get methods
	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public char getSymbol() {
		return charSymbol;
	}

	public int getShipSize() {
		return shipSize;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public boolean isSunk() {
		for (int i = 0; i < shipSize; i++) {
			if (!hit[i]) {
				return false;
			}
		}
		return true;
	}

	public void setHit(int row, int col) {
		int rowOffset = row - this.row;
		int colOffset = col - this.col;
		if (horizontal) {
			setHit(colOffset);
		} else {
			setHit(rowOffset);
		}
	}

	public void setHit(int index) {
		if ((index >= 0) && (index < shipSize)) {
			hit[index] = true;
		} else {
			throw new IndexOutOfBoundsException(
					"Index out of bounds. Valid indexes are 0 to "
							+ (shipSize - 1));
		}
	}

	/**
	 * The only set method, will turn the sunk value from false to true, and
	 * only from false to true
	 * 
	 * @param boolean sunk
	 * @return none
	 */
	public void setSunk(boolean sunk) {
		this.sunk = sunk;
	}

	private int encodeHitArray() {
		int hits = 0;
		for (int i = 0; i < shipSize; i++) {
			hits += (hit[i] ? Math.pow(2, i) : 0);
		}
		return hits;
	}

	private void decodeHitArray(int hits) {
		for (int i = 0; i < shipSize; i++) {
			hit[i] = ((hits % 2) == 1 ? true : false);
			hits = hits / 2;
		}
	}
}