
public enum ShipType {

	BATTLESHIP("Battleship",'B',4),
	CRUISER("Cruiser",'C',3),
	DESTROYER("Destroyer", 'D', 2),
	SUBMARINE("Submarine", 'S', 1);
	
	private static final String DELIMITER = "§";
	
	private final String shipName;
	private final char symbol;
	private final int size;
	
	private ShipType(String name, char symbol, int size) {
		this.shipName = name;
		this.symbol = symbol;
		this.size = size;
	}

	public String shipName() {
		return shipName;
	}
	
	public char symbol() {
		return symbol;
	}
	
	public int size() {
		return size;
	}
	
	public static ShipType get(char symbol) {
		for(ShipType s : values()) {
			if(s.symbol == symbol) {
				return s;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return shipName + DELIMITER + symbol + DELIMITER + size;
	}
	
	
}
