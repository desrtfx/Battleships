import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the information about all available ships<BR>
 * <BR>
 * 
 */
public class Ships implements Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -1126320926003338797L;
	
	private static final String DELIMITER = "@";
	
	/**
	 * Private List that holds all Ship objects
	 */
	private List<Ship> ships;

	/**
	 * Creates a new Ships object with the ships list pre-filled so that the
	 * ships are ready for use
	 */
	public Ships() {
		ships = new ArrayList<Ship>();
		ships.add(new Ship(ShipType.BATTLESHIP));
		ships.add(new Ship(ShipType.CRUISER));
		ships.add(new Ship(ShipType.CRUISER));
		ships.add(new Ship(ShipType.DESTROYER));
		ships.add(new Ship(ShipType.DESTROYER));
		ships.add(new Ship(ShipType.DESTROYER));
		ships.add(new Ship(ShipType.SUBMARINE));
		ships.add(new Ship(ShipType.SUBMARINE));
		ships.add(new Ship(ShipType.SUBMARINE));
	}
	
	public Ships(BufferedReader br) {
		try {
			String line = br.readLine();
			while (!DELIMITER.equals(line)) {
				ships.add(new Ship(line));
				line = br.readLine();
			}
		} catch (IOException e) {
			System.out.println("Unable to read file ");
		}
	}

	/**
	 * Checks if all ships are sunken
	 * 
	 * @return true if all ships are sunken else false
	 */
	public boolean allSunken() {
		for (Ship s : ships) {
			if (!s.isSunk()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if all ships are placed
	 * 
	 * @return true if all ships are placed
	 */
	public boolean allPlaced() {
		for (Ship s : ships) {
			if (!s.isPlaced()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets a ship instance at the given coordinates
	 * 
	 * @param row
	 *            the row coordinate
	 * @param col
	 *            the column coordinate
	 * @return the ship instance if found, otherwise null
	 */
	public Ship getShipAt(int row, int col) {
		for (Ship s : ships) {
			if (s.isHit(row, col)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Returns the ship instance at index
	 * 
	 * @param index
	 *            the index of the ship to get
	 * @return the ship instance
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the array index is invalid
	 */
	public Ship get(int index) {
		if ((index < 0) || (index > ships.size() - 1)) {
			throw new ArrayIndexOutOfBoundsException("Index out of Bounds.");
		}
		return ships.get(index);

	}

	/**
	 * Checks if the ship at index is sunken
	 * 
	 * @param index
	 *            the index of the ship to check
	 * @return true if the ship is sunken, otherwise false
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the array index is invalid
	 */
	public boolean isSunk(int index) {
		if ((index < 0) || (index > ships.size() - 1)) {
			throw new ArrayIndexOutOfBoundsException("Index out of Bounds.");
		}
		return ships.get(index).isSunk();
	}
	
	public boolean isSunk(int row, int col) {
		for (Ship s : ships) {
			if (s.isHit(row, col)) {
				if (s.isSunk()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the ship at index is placed
	 * 
	 * @param index
	 *            the index of the ship to check
	 * @return true if the ship is placed, otherwise false
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the array index is invalid
	 */
	public boolean isPlaced(int index) {
		if ((index < 0) || (index > ships.size() - 1)) {
			throw new ArrayIndexOutOfBoundsException("Index out of Bounds.");
		}
		return ships.get(index).isPlaced();
	}
	
	/**Writes the Ships data to the given BufferedWriter
	 * @param bw stream to save to
	 */
	public void save(BufferedWriter bw) {
		try {
			bw.append(prepareData());
		} catch (IOException e) {
			System.out.println("Unable to write file.");
		}
	}

	// Data preparation and parsing for Save / load

	/**
	 * Prepares a specially formatted String containing all the ships' fields
	 * data
	 * 
	 * @return ship data in String format
	 */
	public String prepareData() {
		StringBuilder sb = new StringBuilder();
		for (Ship s: ships) {
			sb.append(s.prepareData());
			sb.append(System.lineSeparator());
		}
		sb.append(DELIMITER);
		return sb.toString();
	}

	public List<Ship> getShips() {
		return ships;
	}
	
	public int size() {
		return ships.size();
	}
}
