import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class represents a single, specific ship
 *
 */

public class Ship implements Serializable {

	/**
	 * Object version ID for serialization
	 */
	private static final long serialVersionUID = -4780343545574675038L;

	private static final String DELIMITER = "§";

	private ShipType type;
	private int startRow;
	private int startCol;
	private Direction direction;
	private boolean[] hit;
	private boolean sunk;
	private boolean placed;
	private boolean sinkReported;

	/**
	 * Constructs a new ship of the passed type so that the ship can later be
	 * placed
	 * 
	 * @param type
	 *            the type of the ship
	 */
	public Ship(ShipType type) {
		this.type = type;
		placed = false;
		initHit(type.size());
	}

	/**
	 * Constructs a new, fully placed ship object
	 * 
	 * @param type
	 *            the type of the ship
	 * @param startRow
	 *            the start row of the ship
	 * @param startCol
	 *            the start column of the ship
	 * @param direction
	 *            the orientation of the ship
	 */
	public Ship(ShipType type, int startRow, int startCol, Direction direction) {
		this.type = type;
		this.startRow = startRow;
		this.startCol = startCol;
		placed = true;
		this.direction = direction;
		initHit(type.size());
	}

	/**Constructs a new ship from input
	 * from the BufferedReader
	 * @param br reader to read String from
	 */
	public Ship(BufferedReader br) {
		try {
			String line = br.readLine();
			parseData(line);
		} catch (IOException e) {
			System.out.println("Unable to read file ");
		}
	}
	
	/** Constructs a Ship from a given String
	 * @param data String in the format written by prepareData
	 */
	public Ship(String data) {
		parseData(data);
	}
	

	/**
	 * Initializes the hit array and sunk boolean
	 * 
	 * @param size
	 *            the size of the ship
	 */
	private void initHit(int size) {
		hit = new boolean[size];
		for (int i = 0; i < size; i++) {
			hit[i] = false;
		}
		sunk = false;
		sinkReported = false;
	}

	/**
	 * Returns the name of the ship
	 * 
	 * @return the ship name
	 */
	public String getName() {
		return type.shipName();
	}

	/**
	 * Returns the board symbol of the ship
	 * 
	 * @return symbol of the ship
	 */
	public char getSymbol() {
		return type.symbol();
	}

	/**
	 * Returns the size of the ship
	 * 
	 * @return size of the ship
	 */
	public int getSize() {
		return type.size();
	}

	/**
	 * Returns the direction of the ship
	 * 
	 * @return the direction of the ship
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Returns the start row of the ship
	 * 
	 * @return start row of the ship
	 */
	public int getStartRow() {
		return startRow;
	}

	/**
	 * Returns the start column of the ship
	 * 
	 * @return start column of the ship
	 */
	public int getStartCol() {
		return startCol;
	}

	/**
	 * Places the ship on the board.<BR>
	 * Records the start row, start column, and orientation
	 * 
	 * @param startRow
	 *            start row of the ship
	 * @param startCol
	 *            start column of the ship
	 * @param direction
	 *            direction of the ship
	 */
	public void place(int startRow, int startCol, Direction direction) {
		this.startRow = startRow;
		this.startCol = startCol;
		this.direction = direction;
		placed = true;
	}

	/**
	 * Checks if the ship is already placed
	 * 
	 * @return true if the ship is placed, otherwise false
	 */
	public boolean isPlaced() {
		return placed;
	}

	/**
	 * Checks if the ship is sunken
	 * 
	 * @return true if the ship is sunken, otherwise false
	 */
	public boolean isSunk() {
		// If the ship is already sunken, no need to check
		// again, just return true
		if (sunk) {
			return true;
		}
		// Up to now the ship is not marked as sunken,
		// so the hit array needs to be checked
		for (int i = 0; i < type.size(); i++) {
			if (!hit[i]) {
				// a single element of the array is enough
				// to tell that the ship is not yet sunken
				return false;
			}
		}
		// the ship is sunken, flag it as sunken
		sunk = true;
		return true;
	}

	/**
	 * Returns whether the sinking of the ship was already displayed (reported)
	 * or not. A freshly sunken ship has not been reported.
	 * 
	 * @return true if the sinking has already been reported, false otherwise.
	 */
	public boolean isReported() {
		return sinkReported;
	}

	/**
	 * Set the reported flag to true so that the ship can be excluded when
	 * reporting a new sunken ship.
	 */
	public void setSinkReported() {
		sinkReported = true;
	}

	/**
	 * Checks if the ship at position row, col is hit<BR>
	 * <BR>
	 * Can also be used to test if a ship is at position row, col
	 * 
	 * @param row
	 *            the row to check
	 * @param col
	 *            the column to check
	 * @return true if ship is hit, otherwise false
	 */
	public boolean isHit(int row, int col) {
		if (direction == Direction.HORIZ) {
			return ((row == startRow) && ((col >= startCol) && (col <= startCol
					+ type.size())));
		}
		return ((col == startCol) && ((row >= startRow) && (row <= startRow
				+ type.size())));
	}

	/**
	 * Flag the ship's square at row, col as hit
	 * 
	 * @param row
	 *            the row to set
	 * @param col
	 *            the col to set
	 * @throws IllegalArgumentException
	 *             if the row for vertical or the column for horizontal ships is
	 *             outside the ship's bounds.
	 */
	public void setHit(int row, int col) {
		if (direction == Direction.HORIZ) {
			if ((col < startCol) || (col > startCol + type.size())) {
				throw new IllegalArgumentException(
						"The column is outside the ship's bounds");
			}
			hit[col - startCol] = true;
		} else {
			if ((row < startRow) || (row > startRow + type.size())) {
				throw new IllegalArgumentException(
						"The row is outside the ship's bounds");
			}
			hit[row - startRow] = true;
		}
	}

	/**Writes the Ship data to the given BufferedOutputStream
	 * @param bw stream to save to
	 */
	public void save(BufferedWriter bw) {
		try {
			bw.append(prepareData());
		} catch (IOException e) {
			System.out.println("Unable to write file.");
		}
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-12s", type.shipName()));
		sb.append(String.format("%-6s", hitShape()));
		sb.append(String.format("%-6s",  !isPlaced()? "place" : isSunk() ? "sunken" : ""));
		return String.format("%-25s", sb.toString());
	}
	
	private String hitShape() {
		StringBuilder sb = new StringBuilder();
		for (boolean h : hit) {
			sb.append(h ? 'X' : type.symbol());
		}
		return sb.toString();
	}
	

	// Data preparation and parsing for Save / load

	/**
	 * Prepares a specially formatted String containing all the ship's fields
	 * data
	 * 
	 * @return ship data in String format
	 */
	public String prepareData() {
		StringBuilder sb = new StringBuilder();
		sb.append(type.symbol());
		sb.append(DELIMITER);
		sb.append(String.valueOf(startRow));
		sb.append(DELIMITER);
		sb.append(String.valueOf(startCol));
		sb.append(direction.dirText());
		sb.append(DELIMITER);
		for (int i = 0; i < hit.length; i++) {
			sb.append(String.valueOf(hit[i]));
			if (i < hit.length - 1) {
				sb.append(",");
			}
		}
		sb.append(DELIMITER);
		sb.append(String.valueOf(sunk));
		sb.append(DELIMITER);
		sb.append(String.valueOf(placed));
		sb.append(DELIMITER);
		sb.append(String.valueOf(sinkReported));
		sb.append(System.lineSeparator());
		return sb.toString();
	}

	/**
	 * Parses a String in the format of prepareData into the individual fields
	 * of the ship
	 * 
	 * @param line
	 *            formatted String as in prepareData
	 */
	private void parseData(String line) {
		String[] data = line.split(DELIMITER);
		type = ShipType.get(data[0].charAt(0));
		startRow = Integer.parseInt(data[1]);
		startCol = Integer.parseInt(data[2]);
		direction = Direction.getFromText(data[3]);
		String[] sHits = data[4].split(",");
		hit = new boolean[type.size()];
		for (int i = 0; i < sHits.length; i++) {
			hit[i] = Boolean.parseBoolean(sHits[i]);
		}
		sunk = Boolean.parseBoolean(data[5]);
		placed = Boolean.parseBoolean(data[6]);
		sinkReported = Boolean.parseBoolean(data[7]);
	}
	
	

}
