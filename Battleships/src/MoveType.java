
public enum MoveType {
	MOVE_MISS {
		public String toString() {
			return " Miss!";
		}
	},
	MOVE_HIT {
		public String toString() {
			return " Hit!";
		}
	},
	MOVE_ALREADY {
		public String toString() {				// Prevent player from doing a move twice and the computer
			return " move you already made!";	// essentially getting a free turn
		}
	},
	MOVE_INVALID {
		public String toString() {
			return "an Invalid Move!"; 	// Fix grammar issues ("an Invalid" rather than "a Invalid")
		}
	}

}
// Make it only possible to have Move Types like these