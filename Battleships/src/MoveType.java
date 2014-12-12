
/**
 * The MoveType enum lists all possible outcomes of a move.<BR>
 * This makes it easy to check moves.
 *
 */
public enum MoveType {

	MOVE_MISS(" Miss!"),
	MOVE_HIT(" Hit!"),
	MOVE_ALREADY(" move you already made!"),
	MOVE_INVALID(" an invalid move!");
	
	private final String text;
	
	private MoveType(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}
}
