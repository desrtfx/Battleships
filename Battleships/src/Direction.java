/**
 * Enum for the ship directions (HORIZ, VERT)<BR>
 * <BR>
 * The enums have their respective row and column offsets associated<BR>
 * to make it easier to traverse the board
 */
public enum Direction {
	HORIZ("horiz", 0, 1), VERT("vert", 1, 0);

	private final String dirText;
	private final int colOffs;
	private final int rowOffs;

	/**
	 * Private constructor for the enum.<BR>
	 * Sets the respective row and column offsets.
	 * 
	 * @param rowOffs
	 *            row offset
	 * @param colOffs
	 *            column offset
	 */
	private Direction(String dirText, int rowOffs, int colOffs) {
		this.dirText = dirText;
		this.rowOffs = rowOffs;
		this.colOffs = colOffs;
	}

	/**
	 * @return the column offset
	 */
	public int colOffs() {
		return colOffs;
	}

	/**
	 * @return the row offset
	 */
	public int rowOffs() {
		return rowOffs;
	}
	
	public String dirText() {
		return dirText;
	}
	
	public static Direction getFromText(String text) {
		for(Direction d : values()) {
			if(d.dirText.equals(text)) {
				return d;
			}
		}
		return null;
	}
	
	public static Direction get(String text) {
		if ("H".equalsIgnoreCase(text.substring(0,1))) {
			return Direction.HORIZ;
		}
		return Direction.VERT;
	}
	
}
