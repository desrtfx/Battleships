import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BattleshipGame {

	// The following single character string is used
	// to separate the rows of the game board
	// it can be changed to any single character that **does not**
	// exist in the game board.
	public static final String DELIMITER = "§";

	public static final int CMD_PASSED = 0;
	public static final int CMD_MENU = -1;
	public static final int CMD_QUIT = -2;
	public static final int CMD_INVALID = Integer.MIN_VALUE;

	// This class contains the important methods to generate the game and its
	// rules

	private char[][] playerBoard; // Array for the Player's Board
	private char[][] compBoard; // Array for the Computer's Board
	private final static int boardSize = 10;
	private String input, shipType, shipDirection = ""; // initializes
	// shipDirection to
	// avoid
	// NullPointerExceptions
	private String lastMove = ""; // The Computer's last move, initialized to
	// avoid NullPointerExceptions
	public static String name; // The player's name, to be used without creating
	// an object
	private int shipRowLoc, shipColLoc;
	private int savedCompRow, savedCompCol; // savedComp variables save the
	// previous row/column
	private boolean allowed, random = false, showShips = false;
	private MoveType moveType;
	private ArrayList<Battleships> enemyShips = new ArrayList<Battleships>();
	int count = 0;
	int finalScore = 0;
	int wrongShot = 0;
	int sameShot = 0;
	int missCount = 0;
	int hitCount = 0;
	int waveCount = 100;
	boolean firstShot, fiveShot, tenShot, firstCompShot, fiveCompShot,
			tenCompShot, badShot, alreadyShot, subShot, subCompShot, cheater,
			bigCount;
	static Scanner userinput = new Scanner(System.in);

	// GameState saveState = new GameState(playerBoard, compBoard, count);
	public BattleshipGame() {
		playerBoard = new char[boardSize][boardSize];
		compBoard = new char[boardSize][boardSize];
		initializeBoard(); // Set the board to all waves
		placeShip(); // Starts the ship placing
	}

	/**
	 * Load from File constructor<BR>
	 * 
	 * This constructor expects a filename from which it loads the saved game
	 * 
	 * @param filename
	 */
	public BattleshipGame(File file) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new FileInputStream(file))))) {
			// First step: player Board
			playerBoard = decodeBoard(br.readLine());
			// Second step: computer Board
			compBoard = decodeBoard(br.readLine());
			// Third step: Enemy Ships
			enemyShips.clear();
			String line = br.readLine();
			while (!DELIMITER.equals(line)) {
				enemyShips.add(new Battleships(line));
				line = br.readLine();
			}
			// Fourth Step: Player Name
			name = br.readLine();
			// Fifth Step: Integers
			count = Integer.parseInt(br.readLine());
			wrongShot = Integer.parseInt(br.readLine());
			sameShot = Integer.parseInt(br.readLine());
			// Final Step: Booleans
			decodeBooleans(Integer.parseInt(br.readLine()));
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + file.toString());
		} catch (IOException e) {
			System.out.println("Unable to write file " + file.toString());
		}
	}

	public void initializeBoard() {
		// This makes both boards filled with only "waves"
		for (int i = 0; i < boardSize; i++)
			// for (int j = 0; j < 10; j++) {
			// char ch = (char) ('A' + j);
			for (int j = 0; j < boardSize; j++) {
				playerBoard[i][j] = '~';
				compBoard[i][j] = '~';
			}
	}

	public void placeShip() {
		// Call the other placeShip methods
		placeShip("Battleship", 'B', 4); // Ask the user for the first ship, or
											// if they want to randomize the
											// ships
		if (!random) { // If they don't keep asking them for ship locations and
						// stop allowing them to randomize the ships
			placeShip("Battleship", 'B', 4);
			placeShip("Cruiser", 'C', 3);
			placeShip("Cruiser", 'C', 3);
			placeShip("Destroyer", 'D', 2);
			placeShip("Destroyer", 'D', 2);
			placeShip("Destroyer", 'D', 2);
			placeShip("Submarine", 'S', 1);
			placeShip("Submarine", 'S', 1);
			placeShip("Submarine", 'S', 1);
		} else {
			initializeBoard(); // If they want to randomize the ships, start
								// over the placeShip process but this time
								// randomly
			placeRandom(playerBoard, 'B', 4);
			placeRandom(playerBoard, 'C', 3);
			placeRandom(playerBoard, 'C', 3);
			placeRandom(playerBoard, 'D', 2);
			placeRandom(playerBoard, 'D', 2);
			placeRandom(playerBoard, 'D', 2);
			placeRandom(playerBoard, 'S', 1);
			placeRandom(playerBoard, 'S', 1);
			placeRandom(playerBoard, 'S', 1);
		}
		// Always randomly place the computer's ships
		placeRandom(compBoard, 'B', 4);
		placeRandom(compBoard, 'C', 3);
		placeRandom(compBoard, 'C', 3);
		placeRandom(compBoard, 'D', 2);
		placeRandom(compBoard, 'D', 2);
		placeRandom(compBoard, 'D', 2);
		placeRandom(compBoard, 'S', 1);
		placeRandom(compBoard, 'S', 1);
		placeRandom(compBoard, 'S', 1);
	}

	public void placeShip(String aShipType, char shipSymbol, int shipSize) {
		shipType = aShipType;
		int horiz, vert; // Multipliers to be used when incrementing through the
							// array

		do {
			// clrscr();
			System.out
					.print("\tEnter "
							+ shipType
							+ " location and direction: (e.g. D,2,H) ENTER '*' PLACE SHIPS RANDOMLY: ");
			userSetShips(); // Asks user to enter the ship location, then
							// converts them to the appropriate types

			if (getRowLoc() < 0 || getRowLoc() >= boardSize) { // Make sure the
																// coordinates
																// entered are
																// within the
																// board
				System.out
						.println("Please enter a row value inside the boundaries of the grid.");
				allowed = false; // If they are not, start over
			}

			if (getRowLoc() > (boardSize - shipSize)
					&& getDirection().equals("V")) {
				// Keeps the ship within the board if it is vertical
				System.out
						.println("Your ship goes outside the battle grid, please try again.");
				allowed = false;
			}

			if (getColLoc() < 0 || getColLoc() >= boardSize) { // Make sure the
																// coordinates
																// entered are
																// within the
																// board
				System.out
						.println("Please enter a column value inside the boundaries of the battle grid.");
				allowed = false; // If they are not, start over
			}

			if (getColLoc() > (boardSize - shipSize)
					&& getDirection().equals("H")) {
				// Keeps the ship within the board if it is horizontal
				System.out
						.println("Your ship goes outside the battle grid, please try again.");
				allowed = false;
			}

			if (getDirection().equals("H")) {
				horiz = 1;
				vert = 0;
			} else {
				horiz = 0;
				vert = 1;
			}

			if (random)
				break;

			// Make sure nothing is already placed in any of the spots needed to
			// place the ship
			if (allowed) { // Do this only if the coordinates are within the
							// boards limits
				for (int i = 0; i < shipSize; ++i) {
					// If horizontal, column increments and row doesn't, and
					// vice versa.
					if (playerBoard[getRowLoc() + (i * vert)][getColLoc()
							+ (i * horiz)] != '~') {
						System.out
								.println("Your ship placement overlaps another ship, please try another location Commander.");
						allowed = false;
					}
				}
			}

		} while (!allowed); // Repeatedly does this until everything is typed
							// and converted properly

		// Replace waves with user set ships
		if (!random)
			for (int i = 0; i < shipSize; ++i) {
				playerBoard[getRowLoc() + (i * vert)][getColLoc() + (i * horiz)] = shipSymbol;
			}

	}

	public void placeRandom(char[][] board, char shipSymbol, int shipSize) {
		int startRow = 0, startCol = 0; // Starting coordinate of the randomly
										// placed ship
		int horiz, vert; // Multipliers to be used when incrementing through the
							// array

		Random random = new Random();
		boolean horizontal = random.nextBoolean(); // Randomly decide if the
													// ship is horizontal or
													// vertical
		if (horizontal) {
			horiz = 1;
			vert = 0;
		} else {
			horiz = 0;
			vert = 1;
		}

		boolean allowed = false;
		while (!allowed) {
			// Chose random spot to put the ship
			startRow = (int) (Math.random() * (boardSize - shipSize * vert)); // Randomly
																				// set
																				// ship
																				// starting
																				// location
			startCol = (int) (Math.random() * (boardSize - shipSize * horiz)); // and
																				// keep
																				// it
																				// inside
																				// the
																				// boundaries
																				// of
																				// the
																				// grid
			allowed = true;

			// Verify that nothing else is already placed in any of the
			// coordinates needed
			for (int i = 0; i < shipSize; ++i) {
				if (board[startRow + (i * vert)][startCol + (i * horiz)] != '~') {
					allowed = false;
				}
			}
		}
		// Randomly place ships
		for (int i = 0; i < shipSize; ++i) {
			board[startRow + (i * vert)][startCol + (i * horiz)] = shipSymbol;
		}
		// If were are randomly placing computer ships, add them to the
		// enemyShips ArrayList full of 'Battleships' objects
		if (board.equals(compBoard))
			enemyShips.add(new Battleships(startRow, startCol, shipSymbol,
					shipSize, horizontal));
	}

	// Get/Set Methods
	public int getRowLoc() {
		return shipRowLoc;
	}

	public int getColLoc() {
		return shipColLoc;
	}

	public String getDirection() {
		return shipDirection;
	}

	public static String getName() {
		return name;
	}

	public ArrayList<Battleships> getEnemyShips() {
		return enemyShips;
	}

	public static void setName(String aName) {
		name = aName;
	} // Static so we can set it without creating an object

	public void userSetShips() {
		// This method gets input from the user, interprets it, converts it, and
		// error checks it
		allowed = true; // If it remains true, the user did everything right
		input = userinput.nextLine();
		String[] inputs = input.split("\\,"); // Split user's input into an
												// array if they entered commas

		if (input.equals("*")) { // If user enteres '*' then randomly generate
									// ships
			random: // If they already placed a ship, exit out of this loop
			for (int i = 0; i < boardSize; i++)
				for (int j = 0; j < boardSize; j++) {
					if (playerBoard[i][j] == '~')
						random = true; // Check if a ship has been placed yet
					else { // If it has, don't let them randomly place the ships
						random = false;
						System.out
								.println("You already set a ship, you cannot randomly place them anymore. You can exit the game and set randomly again if you wish.");
						break random;
					}
				}
			allowed = false;
		} else if (inputs.length == 3) { // Check if user entered
											// row,col,direction format, so the
											// array is size 3
			inputs[2] = inputs[2].toUpperCase(); // Makes whatever the third
													// thing typed upper case
			try { // Check if Row value is a number
				shipRowLoc = Integer.parseInt(inputs[0]); // If it is, convert
															// String to int
			} catch (NumberFormatException e) { // If it is not, ask them to
												// re-enter their input
				System.out
						.println("You incorrectly set the row, please try again.");
				allowed = false;
			}

			try { // Check if Column value is a number
				shipColLoc = Integer.parseInt(inputs[1]); // If it is, convert
															// String to int
			} catch (NumberFormatException e) { // If it is not, ask them to
												// re-enter their input
				System.out
						.println("You incorrectly set the column, please try again.");
				allowed = false;
			}
			if (inputs[2].equals("H") || inputs[2].equals("V")) // If third
																// thing is H or
																// V
				shipDirection = inputs[2]; // Set shipDirection to the third
											// thing entered by user
			else {
				System.out
						.println("You incorrectly set the direction, please try again.");
				allowed = false;
			}
		}

		else { // If they didn't input in the right format, ask them to enter it
				// again
			System.out
					.println("You did not enter in the right format, please try again.");
			allowed = false;
		}

	}

	public void replaceShips(int index) {
		// Once the ship is completely hit i.e. hits == length, replace the
		// values in the array with the shipSymbol
		int horiz, vert; // Multipliers that allows the program to increment
							// correctly through the array
		compBoard[enemyShips.get(index).getRow()][enemyShips.get(index)
				.getCol()] = enemyShips.get(index).getSymbol();
		if (enemyShips.get(index).isHorizontal()) {
			horiz = 1;
			vert = 0;
		} else {
			horiz = 0;
			vert = 1;
		}
		int shipSize = enemyShips.get(index).getShipSize();
		char theSymbol = enemyShips.get(index).getSymbol();
		for (int i = 0; i < shipSize; ++i) {
			compBoard[enemyShips.get(index).getRow() + (i * vert)][enemyShips
					.get(index).getCol() + (i * horiz)] = theSymbol;
		}
	}

	public void checkSunk() {
		// Check if the computer's ships and player's ships have been sunk yet
		// 0 = Battleship, 1 = Cruiser, 2 = Cruiser, 3 = Destroyer 4 = Destroyer
		// 5 = Destroyer 6 = Submarine 7 = Submarine 8 = Submarine
		boolean compSunk[] = { true, true, true, true, true, true, true, true,
				true };
		boolean playerSunk[] = { true, true, true, true, true, true, true,
				true, true };
		for (int i = 0; i < boardSize; i++)
			for (int j = 0; j < boardSize; j++) {
				if (compBoard[i][j] == 'B')
					compSunk[0] = false;
				if (compBoard[i][j] == 'C')
					compSunk[1] = false;
				if (compBoard[i][j] == 'C')
					compSunk[2] = false;
				if (compBoard[i][j] == 'D')
					compSunk[3] = false;
				if (compBoard[i][j] == 'D')
					compSunk[4] = false;
				if (compBoard[i][j] == 'D')
					compSunk[5] = false;
				if (compBoard[i][j] == 'S')
					compSunk[6] = false;
				if (compBoard[i][j] == 'S')
					compSunk[7] = false;
				if (compBoard[i][j] == 'S')
					compSunk[8] = false;
				if (playerBoard[i][j] == 'B')
					playerSunk[0] = false;
				if (playerBoard[i][j] == 'C')
					playerSunk[1] = false;
				if (playerBoard[i][j] == 'C')
					playerSunk[2] = false;
				if (playerBoard[i][j] == 'D')
					playerSunk[3] = false;
				if (playerBoard[i][j] == 'D')
					playerSunk[4] = false;
				if (playerBoard[i][j] == 'D')
					playerSunk[5] = false;
				if (playerBoard[i][j] == 'S')
					playerSunk[6] = false;
				if (playerBoard[i][j] == 'S')
					playerSunk[7] = false;
				if (playerBoard[i][j] == 'S')
					playerSunk[8] = false;
			}
		// The Computer's Ships
		if (compSunk[0]) {
			replaceShips(0); // Replaces the X's with the 'B'symbol
			enemyShips.get(0).setSunk(true);
			System.out.println("\n\n\t\t\t\t\t\t\t\t\tBLAST YOU " + name
					+ "! You sank my Battleship!");
		}
		if (compSunk[1]) {
			replaceShips(1); // Replaces the X's with the 'C' symbol
			enemyShips.get(1).setSunk(true);
			System.out.println("\n\n\t\t\t\t\t\t\t\t\tBLAST YOU " + name
					+ "! You sank my Cruiser!");
		}
		if (compSunk[2]) {
			replaceShips(2); // Replaces the X's with the 'C'symbol
			enemyShips.get(2).setSunk(true);
			System.out.println("\n\n\t\t\t\t\t\t\t\t\tBLAST YOU " + name
					+ "! You sank my Cruiser!");
		}
		if (compSunk[3]) {
			replaceShips(3); // Replaces the X's with the 'D'symbol
			enemyShips.get(3).setSunk(true);
			System.out.println("\n\n\t\t\t\t\t\t\t\t\tBLAST YOU " + name
					+ "! You sank my Destroyer!");
		}
		if (compSunk[4]) {
			replaceShips(4); // Replaces the X's with the 'D'symbol
			enemyShips.get(4).setSunk(true);
			System.out.println("\n\n\t\t\t\t\t\t\t\t\tBLAST YOU " + name
					+ "! You sank my Destroyer!");
		}
		if (compSunk[5]) {
			replaceShips(5); // Replaces the X's with the 'D'symbol
			enemyShips.get(5).setSunk(true);
			System.out.println("\n\n\t\t\t\t\t\t\t\t\tBLAST YOU " + name
					+ "! You sank my Destroyer!");
		}
		if (compSunk[6]) {
			replaceShips(6); // Replaces the X's with the 'S' symbol
			enemyShips.get(6).setSunk(true);
			System.out.println("\n\n\t\t\t\t\t\t\t\t\tBLAST YOU " + name
					+ "! You sank my Submarine!");
		}
		if (compSunk[7]) {
			replaceShips(7); // Replaces the X's with the 'S' symbol
			enemyShips.get(7).setSunk(true);
			System.out.println("\n\n\t\t\t\t\t\t\t\t\tBLAST YOU " + name
					+ "! You sank my Submarine!");
		}
		if (compSunk[8]) {
			replaceShips(8); // Replaces the X's with the 'S' symbol
			enemyShips.get(8).setSunk(true);
			System.out.println("\n\n\t\t\t\t\t\t\t\t\tBLAST YOU " + name
					+ "! You sank my Submarine!");
		}
		// The Player's Ships
		if (playerSunk[0]) {
			System.out.println("\n\n\t\t\t\t\t\t\t\t\t" + name
					+ "! Ha Ha, I sank your Battleship!");
		}
		if (playerSunk[1]) {
			System.out.println("\n\n\t\t\t\t\t\t\t\t\t" + name
					+ "! Ha Ha, I sank your Cruiser!");
		}
		if (playerSunk[2]) {
			System.out.println("\n\n\t\t\t\t\t\t\t\t\t" + name
					+ "! Ha Ha, I sank your Cruiser!");
		}
		if (playerSunk[3]) {
			System.out.println("\n\n\t\t\t\t\t\t\t\t\t" + name
					+ "! Ha Ha, I sank your Destroyer!");
		}
		if (playerSunk[4]) {
			System.out.println("\n\n\t\t\t\t\t\t\t\t\t" + name
					+ "! Ha Ha, I sank your Destroyer!");
		}
		if (playerSunk[5]) {
			System.out.println("\n\n\t\t\t\t\t\t\t\t\t" + name
					+ "! Ha Ha, I sank your Destroyer!");
		}
		if (playerSunk[6]) {
			System.out.println("\n\n\t\t\t\t\t\t\t\t\t" + name
					+ "! Ha Ha, I sank your Submarine!");
		}
		if (playerSunk[7]) {
			System.out.println("\n\n\t\t\t\t\t\t\t\t\t" + name
					+ "! Ha Ha, I sank your Submarine!");
		}
		if (playerSunk[8]) {
			System.out.println("\n\n\t\t\t\t\t\t\t\t\t" + name
					+ "! Ha Ha, I sank your Submarine!");
		}
	}

	public String returnRowLetter(int i) {
		if ((i >= 0) && (i <= 9)) {
			return String.valueOf(i + 'A');
		}
		return ""; // out of range. possible erroneous value.
	}

	public int returnRowNumber(String i) {
		char ch = i.toUpperCase().charAt(0);
		if ((ch >= 'A') && (ch <= 'J')) {
			return ch - 'A';
		}
		return -1; // out of range. possible erroneous value.
	}

	public void printBoard() {
		System.out
				.println("¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·."
						+ "´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.¸");
		checkSunk(); // Checks if the previous move caused any boats to be sunk,
						// altering what gets printed
		// Prints both boards beside each other, but doesn't show computer ships
		// unless showShips is true
		String output = "  ";
		System.out.println("\t\t\t\t\t\t\t\t  Commander "
				+ BattleshipGame.getName() + "\t\t\t  Computer\n");
		// Print the column numbers for both boards
		for (int i = 0; i < boardSize; i++)
			output = output + i + " ";

		output = "\t\t\t\t\t\t\t\t" + output + "           ";

		for (int i = 0; i < boardSize; i++)
			output = output + i + " ";

		System.out.println(output);

		// Print the information of both boards
		for (int i = 0; i < boardSize; i++) {
			output = "\t\t\t\t\t\t\t\t" + returnRowLetter(i) + " ";

			for (int j = 0; j < boardSize; j++)
				output += playerBoard[i][j] + " ";

			output += "         " + returnRowLetter(i) + " ";

			for (int j = 0; j < boardSize; j++) {
				if (compBoard[i][j] == 'ø' || compBoard[i][j] == 'X')
					output += compBoard[i][j] + " "; // If already hit or miss,
														// show that they have
				else if (showShips)
					output += compBoard[i][j] + " "; // If they entered -1,-1,
														// show the ship
														// locations
				else if (isSunk(i, j))
					output += compBoard[i][j] + " "; // If the ship has been
														// sunk, it is allowed
														// to print its symbol
				else
					output += '~' + " "; // Just show waves
			}
			output += "\n";
			System.out.println(output);
		}
		printWave();
		System.out
				.println("\n\t\t\t\t[BOARD DETAILS] HIT - X("
						+ hitCount
						+ ")"
						+ "       MISS - ø("
						+ missCount
						+ ")"
						+ "       UNTOUCHED - ~("
						+ waveCount
						+ ")"
						+ "       Missiles Fired: "
						+ count
						+ "\n\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°"
						+ "\n\t\t\t\t[INPUT DETAILS] SAVE GAME - type 's'    EXIT TO MENU - type 'm'    QUIT GAME - type 'q'    CHEAT - type -1,-1\n");

	}

	/**
	 * If it is horizontal, the rows of the boat are all the same The allowed
	 * columns will go from the starting column of the boat to the starting
	 * column plus the ship size It also checks if the boat is sunk If it is
	 * vertical, the columns of the boat are all the same The allowed rows will
	 * go from the starting row of the boat to the starting row plus the ship
	 * size It also checks if the boat is sunk If all of this is true, the
	 * program is allowed to print the symbol
	 * 
	 * @param Integer
	 *            row, Integer col
	 * @return false
	 */
	public boolean isSunk(int row, int col) {
		// Checks if the current shot is on a boat that has been sunk

		if (enemyShips.get(0).isHorizontal()) {
			if ((enemyShips.get(0).getRow() == row)
					&& ((col - enemyShips.get(0).getCol() >= 0) && (col
							- enemyShips.get(0).getCol() < enemyShips.get(0)
							.getShipSize())) && enemyShips.get(0).isSunk())
				return true;
		}

		else {
			if ((enemyShips.get(0).getCol() == col)
					&& ((row - enemyShips.get(0).getRow() >= 0) && (row
							- enemyShips.get(0).getRow() < enemyShips.get(0)
							.getShipSize())) && enemyShips.get(0).isSunk())
				return true;
		}

		if (enemyShips.get(1).isHorizontal()) {
			if ((enemyShips.get(1).getRow() == row)
					&& ((col - enemyShips.get(1).getCol() >= 0) && (col
							- enemyShips.get(1).getCol() < enemyShips.get(1)
							.getShipSize())) && enemyShips.get(1).isSunk())
				return true;
		}

		else {
			if ((enemyShips.get(1).getCol() == col)
					&& ((row - enemyShips.get(1).getRow() >= 0) && (row
							- enemyShips.get(1).getRow() < enemyShips.get(1)
							.getShipSize())) && enemyShips.get(1).isSunk())
				return true;
		}

		if (enemyShips.get(2).isHorizontal()) {
			if ((enemyShips.get(2).getRow() == row)
					&& ((col - enemyShips.get(2).getCol() >= 0) && (col
							- enemyShips.get(2).getCol() < enemyShips.get(2)
							.getShipSize())) && enemyShips.get(2).isSunk())
				return true;
		}

		else {
			if ((enemyShips.get(2).getCol() == col)
					&& ((row - enemyShips.get(2).getRow() >= 0) && (row
							- enemyShips.get(2).getRow() < enemyShips.get(2)
							.getShipSize())) && enemyShips.get(2).isSunk())
				return true;
		}

		if (enemyShips.get(3).isHorizontal()) {
			if ((enemyShips.get(3).getRow() == row)
					&& ((col - enemyShips.get(3).getCol() >= 0) && (col
							- enemyShips.get(3).getCol() < enemyShips.get(3)
							.getShipSize())) && enemyShips.get(3).isSunk())
				return true;
		}

		else {
			if ((enemyShips.get(3).getCol() == col)
					&& ((row - enemyShips.get(3).getRow() >= 0) && (row
							- enemyShips.get(3).getRow() < enemyShips.get(3)
							.getShipSize())) && enemyShips.get(3).isSunk())
				return true;
		}

		if (enemyShips.get(4).isHorizontal()) {
			if ((enemyShips.get(4).getRow() == row)
					&& ((col - enemyShips.get(4).getCol() >= 0) && (col
							- enemyShips.get(4).getCol() < enemyShips.get(4)
							.getShipSize())) && enemyShips.get(4).isSunk())
				return true;
		}

		else {
			if ((enemyShips.get(4).getCol() == col)
					&& ((row - enemyShips.get(4).getRow() >= 0) && (row
							- enemyShips.get(4).getRow() < enemyShips.get(4)
							.getShipSize())) && enemyShips.get(4).isSunk())
				return true;
		}

		if (enemyShips.get(5).isHorizontal()) {
			if ((enemyShips.get(5).getRow() == row)
					&& ((col - enemyShips.get(5).getCol() >= 0) && (col
							- enemyShips.get(5).getCol() < enemyShips.get(5)
							.getShipSize())) && enemyShips.get(5).isSunk())
				return true;
		}

		else {
			if ((enemyShips.get(5).getCol() == col)
					&& ((row - enemyShips.get(5).getRow() >= 0) && (row
							- enemyShips.get(5).getRow() < enemyShips.get(5)
							.getShipSize())) && enemyShips.get(5).isSunk())
				return true;
		}

		if (enemyShips.get(6).isHorizontal()) {
			if ((enemyShips.get(6).getRow() == row)
					&& ((col - enemyShips.get(6).getCol() >= 0) && (col
							- enemyShips.get(6).getCol() < enemyShips.get(6)
							.getShipSize())) && enemyShips.get(6).isSunk())
				return true;
		}

		else {
			if ((enemyShips.get(6).getCol() == col)
					&& ((row - enemyShips.get(6).getRow() >= 0) && (row
							- enemyShips.get(6).getRow() < enemyShips.get(6)
							.getShipSize())) && enemyShips.get(6).isSunk())
				return true;
		}

		if (enemyShips.get(7).isHorizontal()) {
			if ((enemyShips.get(7).getRow() == row)
					&& ((col - enemyShips.get(7).getCol() >= 0) && (col
							- enemyShips.get(7).getCol() < enemyShips.get(7)
							.getShipSize())) && enemyShips.get(7).isSunk())
				return true;
		}

		else {
			if ((enemyShips.get(7).getCol() == col)
					&& ((row - enemyShips.get(7).getRow() >= 0) && (row
							- enemyShips.get(7).getRow() < enemyShips.get(7)
							.getShipSize())) && enemyShips.get(7).isSunk())
				return true;
		}

		if (enemyShips.get(8).isHorizontal()) {
			if ((enemyShips.get(8).getRow() == row)
					&& ((col - enemyShips.get(8).getCol() >= 0) && (col
							- enemyShips.get(8).getCol() < enemyShips.get(8)
							.getShipSize())) && enemyShips.get(8).isSunk())
				return true;
		}

		else {
			if ((enemyShips.get(8).getCol() == col)
					&& ((row - enemyShips.get(8).getRow() >= 0) && (row
							- enemyShips.get(8).getRow() < enemyShips.get(8)
							.getShipSize())) && enemyShips.get(8).isSunk())
				return true;
		}
		return false;
	}

	public boolean isShootable(int row, int col) {
		// Checks if the attempted shot at the computer's boat is allowed based
		// on if it has already been sunk
		if (compBoard[row][col] == 'B' && enemyShips.get(0).isSunk())
			return false;
		else if (compBoard[row][col] == 'C' && enemyShips.get(1).isSunk())
			return false;
		else if (compBoard[row][col] == 'C' && enemyShips.get(2).isSunk())
			return false;
		else if (compBoard[row][col] == 'D' && enemyShips.get(3).isSunk())
			return false;
		else if (compBoard[row][col] == 'D' && enemyShips.get(4).isSunk())
			return false;
		else if (compBoard[row][col] == 'D' && enemyShips.get(5).isSunk())
			return false;
		else if (compBoard[row][col] == 'S' && enemyShips.get(6).isSunk())
			return false;
		else if (compBoard[row][col] == 'S' && enemyShips.get(7).isSunk())
			return false;
		else if (compBoard[row][col] == 'S' && enemyShips.get(8).isSunk())
			return false;
		return true;
	}

	public void cheat() {
		// Causes the computer's board to be printed instead of just waves, hits
		// and misses
		showShips = true;
	}

	/**
	 * The command parser method receives the command String from the User input
	 * and processes it respectively.<BR>
	 * <BR>
	 * This ensures that only valid row and column coordinates are passed to the
	 * move method <BR>
	 * Valid commands for the parser are:<BR>
	 * int row, int col - player move<BR>
	 * m - back to menu without saving - not implemented yet<BR>
	 * s - save the game<BR>
	 * q - quit the game - not implemented yet<BR>
	 * 
	 * @param command
	 *            The command String to be parsed
	 */
	public int commandParser(String command) {
		// Handle empty commands
		if ((command == null) || ("".equals(command)) || (command.length() < 1)) {
			displayCommandError();
			return CMD_INVALID;
		}
		// Command is not empty, continue
		String cmd = command.toLowerCase().substring(0, 1); // for now we are
		// only interested
		// in the first
		// letter
		if ("msq".contains(cmd)) { // one of the menu commands
			switch (cmd) {
			case "s": // Save the game
				String filename = "savegame.gz";
				if (command.contains(" ")) {
					String[] data = command.split(" ");
					filename = data[1];
				}
				saveGame(filename);
				return CMD_PASSED;
			case "m": // Back to menu without saving
				// not yet implemented
				return CMD_MENU;
			case "q": // quit game
				// not yet implemented;
				return CMD_QUIT;
			default: // Just for completeness
				break;
			}
		}

		// If the execution reaches here, command was no menu command.

		// If the user enters "B4" instead of "B,4" add a comma
		if (command.length() == 2) {
			command = command.substring(0, 1) + "," + command.substring(1, 2);
		}

		// Let's see if it is a valid row/column combination
		StringTokenizer splitter = new StringTokenizer(command, ",");
		if (splitter.countTokens() == 2) {
			String row = splitter.nextToken().trim();
			// Convert the row to it's integer representation
			row = sanitizeRow(row);
			if ((row.length() != 1) || ("".equals(row))) {
				displayCommandError();
				return CMD_INVALID;
			}
			String col = splitter.nextToken().trim();
			playerMove(row, col);
		} else {
			displayCommandError();
			return CMD_INVALID;
		}
		return 0; // Currently future use to process menu and quit commands
	}

	private String sanitizeRow(String row) {
		// Helper method to make sure the row is a String
		// in the range of "0" to "9"
		// Even if the user entered "A" to "J"
		// returns an empty String if the row is not in
		// the valid range.
		char tmp = row.toUpperCase().charAt(0);
		int code;
		if ((tmp >= 'A') && (tmp <= 'J')) {
			code = tmp - 'A';
		} else {
			if ((tmp >= '0') && (tmp <= '9')) {
				code = tmp - '0';
			} else {
				return "";
			}
		}
		String cleanRow = String.valueOf(code);
		return cleanRow;
	}

	private void displayCommandError() {
		System.out.println();
		System.out
				.println("\t\t\t\tI am sorry Commander "
						+ BattleshipGame.getName()
						+ ".\n\n"
						+ "\t\t\t\tIt seems that you did not enter your co-ordinate correctly.... It must be a faulty keyboard Commander....\n");
	}

	// The playerMove routine should only be called when valid rows and colums
	// can be passed as parameters
	// Should really receive only int row and int col and do the actual move,
	// nothing more.
	// So, I've hooked up a command parser that parses the User Input before it
	// even reaches the playerMove method
	public void playerMove(String stringRow, String stringCol) {
		boolean cheat = false; // They haven't recently made the computers ship
								// locations visible
		int row = -5, col = -5;
		// If parsing doesn't work, the MoveType is MOVE_INVALID
		moveType = null; // Resets the moveType from the previous move

		// First attempt to convert row and col to ints
		boolean worked = true; // Remains true if the parsing worked
		try { // Check if Row value is a number
			row = Integer.parseInt(stringRow); // If it is, convert String to
												// int
		} catch (NumberFormatException e) { // If it is not, ask them to try
											// again
			System.out
					.println("You incorrectly set the row, please try again.");
			worked = false;
		}
		try { // Check if Row value is a number
			col = Integer.parseInt(stringCol); // If it is, convert String to
												// int
		} catch (NumberFormatException e) { // If it is not, ask them to
											// re-enter their input
			System.out
					.println("You incorrectly set the column, please try again.");
			worked = false;
		}

		// Do this only if parsing worked
		if (worked) {
			// Check if they entered -1,-1
			if (row == -1 && col == -1) {
				cheat();
				cheat = true;
				cheater = true;
				System.out
						.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: 'I'm Telling!'••");
			}

			// Check to see if the coordinates are out of bounds
			else if (row >= boardSize || row < 0 || col >= boardSize || col < 0) {
				wrongShot++;
				moveType = MoveType.MOVE_INVALID;
				if (wrongShot == 3) {
					finalScore += 100;
					badShot = true;
					System.out
							.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: 'Faulty Keyboard!'••");
				}
			}
			// See if it's a miss
			else if (compBoard[row][col] == '~') {
				compBoard[row][col] = 'ø';
				moveType = MoveType.MOVE_MISS;
				count++;
				missCount++;
				waveCount--;
				if (count == 5) {
					finalScore += 100;
					fiveShot = true;
					System.out
							.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: 'Don't Give Up!'••");
				}
				if (count == 10) {
					finalScore += 100;
					tenShot = true;
					System.out
							.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: 'You Do Know How To Play Right?'••");
				}
			} else if (compBoard[row][col] == 'ø' || compBoard[row][col] == 'X') {
				sameShot++;
				moveType = MoveType.MOVE_ALREADY; // They already shot here
				if (sameShot == 3) {
					finalScore += 100;
					alreadyShot = true;
					System.out
							.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: 'Just Making Sure!'••");
				}
			} else if (!isShootable(row, col))
				moveType = MoveType.MOVE_ALREADY; // See if the coordinate being
													// shot at is hitting a
													// sunken ship
			else {
				// It must be a hit
				compBoard[row][col] = 'X';
				moveType = MoveType.MOVE_HIT;
				count++;
				hitCount++;
				waveCount--;
				if (count == 1 && compBoard[row][col] == 'X') {
					finalScore += 400;
					firstShot = true;
					System.out
							.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: 'Beginners Luck?'••");
				}
				if (count == 1 && compBoard[row][col] == 'S') {
					finalScore += 500;
					subShot = true;
					System.out
							.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: 'One Hit Wonder?'••");
				}
			}
			if (count == 80) {
				finalScore += 100;
				bigCount = true;
				System.out
						.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: 'ZZZZzzzz...'••");
			}
		} else
			moveType = MoveType.MOVE_INVALID;
		if (moveType == MoveType.MOVE_HIT || moveType == MoveType.MOVE_MISS)
			compMove();
		if (!cheat) // Don't do this if they just made the computer's ship
					// locations visible
			System.out.println("\n\t\t\t\t\t\t\t\t\tCommander " + name
					+ ", that was a " + moveType);
		while (row == 'Q' && col == 'Q') {
			break;
		}
	}

	public void compMove() {
		// Randomly choose locations
		boolean compAllowed = false;
		if (lastMove.equals("hit"))
			compAI();
		else {
			lastMove = "";// Calls the compAI method to provide a smart strategy
							// for the computer
			while (!compAllowed) {
				int row = (int) (Math.random() * boardSize);
				int col = (int) (Math.random() * boardSize);

				if (playerBoard[row][col] == '~') {
					playerBoard[row][col] = 'ø';
					if (count == 5 && playerBoard[row][col] != 'X') {
						finalScore += 100;
						fiveCompShot = true;
						System.out
								.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: 'Must Be The Programmers Fault...'••");
					}
					if (count == 10 && playerBoard[row][col] != 'X') {
						finalScore += 100;
						tenCompShot = true;
						System.out
								.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: 'Artificial Non-Intelligence'••");
					}
					compAllowed = true;
				} else if (playerBoard[row][col] == 'ø')
					compAllowed = false; // Already made this move
				else if (playerBoard[row][col] == 'X')
					compAllowed = false; // Already made this move
				else { // Must be a hit
					playerBoard[row][col] = 'X';

					lastMove = "hit";
					savedCompRow = row;
					savedCompCol = col;

					compAllowed = true;
					if (count == 1 && playerBoard[row][col] == 'X') {
						finalScore += 400;
						firstCompShot = true;
						System.out
								.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: 'Some AI's Have All The Luck?'••");
					}
					if (count == 1 && playerBoard[row][col] == 'S') {
						finalScore += 500;
						subCompShot = true;
						System.out
								.println("\n\t\t\t\t\t\t\t\t\t ••YOU UNLOCKED THE TROPHY: '8 Bit Wonder!'••");
					}

				}
			}
		}
	}

	public void compAI() {
		// Randomly goes up, down, left, or right from the previous spot to
		// attempt to sink ship
		boolean compAllowed = false;
		int forwards = 0, up = 0;
		int shotCount = 0;
		while (!compAllowed) {
			int direction = (int) (Math.random() * 4);
			if (direction == 0)
				forwards = 1;
			if (direction == 1)
				forwards = -1;
			if (direction == 2)
				up = 1;
			if (direction == 3)
				up = -1;

			int valueCol = savedCompCol;
			int valueRow = savedCompRow;
			if (direction == 0 || direction == 1) {
				valueCol += forwards;
				valueCol = Math.max(Math.min(valueCol, 9), 0);
			} else {
				valueRow += up;
				valueRow = Math.max(Math.min(valueRow, 9), 0);
			}

			if (playerBoard[valueRow][valueCol] == '~') {
				playerBoard[valueRow][valueCol] = 'ø';
				// lastMove = "miss";
				compAllowed = true;
			} else if (playerBoard[valueRow][valueCol] == 'ø')
				compAllowed = false;
			else if (playerBoard[valueRow][valueCol] == 'X')
				compAllowed = false;
			else {
				playerBoard[valueRow][valueCol] = 'X';
				lastMove = "hit";
				savedCompRow = valueRow;
				savedCompCol = valueCol;
				compAllowed = true;
			}
			shotCount++;
			if (shotCount > 20) {
				lastMove = "";
				compMove();
				return;
			}
		}
	}

	public boolean gameOver() {
		// Determines if the game is still going, if not, the game is over
		if ((!playerWon()) && (!compWon())) {
			// Neither player, not computer won, no need to continue checking
			// return false and exit the method
			return false;
		}

		if (playerWon() && compWon()) { // If somehow both the computer and
										// player win, it is a tie!
			setScore();
			System.out
					.println("Commander "
							+ BattleshipGame.name
							+ ", it is a tie! \n\nThe spoils of war are even this time!");
			System.out
					.println("¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·."
							+ "´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.¸");
			System.out.println("\n\n\t\t\t\tYour final score was: "
					+ finalScore);
			System.out
					.println("\n\n\t\t\t\tCheck the leaderboards in the Main Menu to see how you compare to other players!\n\n\n\n");
			System.out
					.println("\n\n\t\t\t\tUNLOCKED TROPHIES ARE DISPLAYED BELOW\n\n");
			getTrophies();
			pressKey();
			return true;
		}
		if (playerWon() || compWon()) {
			// If either the player or computer has won, tell them

			printBoard(); // Prints the board one last time, to show the player
			// how the game ended

			if (playerWon()) {
				setScore();
				clrscr();
				System.out
						.println("\n\t\t\t\t\t\t\t\tCommander "
								+ BattleshipGame.name
								+ "!"
								+ "\n\n\t\t\t\t\t\tWe have accomplished our mission! The enemy fleet is destroyed!");
				printWave();
				System.out.println("\n\n\t\t\t\tYour final score was: "
						+ finalScore);
				System.out
						.println("\n\n\t\t\t\tCheck the highscore leaderboards in the Main Menu to see how you compare to other players!\n\n\n\n");
				getTrophies();
				pressKey();
				return true;
			} else if (compWon()) {
				setScore();
				finalScore = finalScore - 200;
				clrscr();
				System.out
						.println("\n\t\t\t\t\t\t\t\tCommander "
								+ BattleshipGame.name
								+ "."
								+ "\n\n\t\t\t\tI am afraid to report that we have been defeated...");
				printWave();
				System.out
						.println("\n\n\t\t\t\tYour final score was: "
								+ finalScore
								+ " (You suffered a point deduction penalty because of defeat)");
				System.out
						.println("\n\n\t\t\t\tCheck the leaderboards in the Main Menu to see how you compare to other players!\n\n\n\n");
				getTrophies();
				pressKey();
				return true;
			}

		}
		return false;
	}

	public void getTrophies() {
		System.out
				.println("\n\n\t\t\t\tWELL DONE! YOU UNLOCKED THE FOLLOWING TROPHIES!\n\n");
		if (firstShot == true) {
			System.out
					.println("\t\t\t\t ••'Beginners Luck?' (hit a ship with your first shot)°°°400 bonus points!°°°");
		}
		if (fiveShot == true) {
			System.out
					.println("\t\t\t\t ••'Don't Give Up!' (miss with 5 shots in a row)°°°100 bonus points!°°°");
		}
		if (tenShot == true) {
			System.out
					.println("\t\t\t\t ••'You Do Know How To Play Right?' (miss with 10 shots in a row)°°°100 bonus points!°°°");
		}
		if (badShot == true) {
			System.out
					.println("\t\t\t\t ••'Faulty Keyboard!' (entered wrong co-ordinate format 3 times)°°°100 bonus points!°°°");
		}
		if (alreadyShot == true) {
			System.out
					.println("\t\t\t\t ••'Just Making Sure!' (enter co-ordinate that's already been hit 3 times)°°°100 bonus points!°°°");
		}
		if (subShot == true) {
			System.out
					.println("\t\t\t\t ••'One Hit Wonder!' (sink a submarine with your first shot)°°°500 bonus points!°°°");
		}
		if (cheater == true) {
			System.out
					.println("\t\t\t\t ••'I'm Telling!' (activate the cheat to reveal the enemy ship locations)");
		}
		if (bigCount == true) {
			System.out
					.println("\t\t\t\t ••'ZZZZzzzz...' (fire 80 missiles without the game ending)°°°100 bonus points!°°°");
		}
		if (fiveCompShot == true) {
			System.out
					.println("\t\t\t\t ••'Must Be The Programmers Fault...' (computer missed 5 shots in a row)°°°100 bonus points!°°°");
		}
		if (tenCompShot == true) {
			System.out
					.println("\t\t\t\t ••'Artificial Non-Intelligence' (computer missed 10 shots in a row)°°°100 bonus points!°°°");
		}
		if (firstCompShot = true) {
			System.out
					.println("\t\t\t\t ••'Some AI's Have All The Luck?' (computer hit a ship with first shot)°°°400 bonus points!°°°");
		}
		if (subCompShot == true) {
			System.out
					.println("\t\t\t\t ••'8 Bit Wonder?' (computer sunk a submarine with first shot)°°°500 bonus points!°°°");
		}
	}

	public void setScore() {
		if (count < 20) {
			count = count + 1500;
			finalScore = count;
		}
		if (count >= 20 && count < 25) {
			count = count + 1200;
			finalScore = count;
		}
		if (count >= 25 && count < 30) {
			count = count + 1100;
			finalScore = count;
		}
		if (count >= 30 && count < 40) {
			count = count + 1000;
			finalScore = count;
		}
		if (count >= 40 && count < 50) {
			count = count + 950;
			finalScore = count;
		}
		if (count >= 50 && count < 60) {
			count = count + 850;
			finalScore = count;
		}
		if (count >= 60 && count < 70) {
			count = count + 800;
			finalScore = count;
		}
		if (count >= 70 && count < 80) {
			count = count + 750;
			finalScore = count;
		}
		if (count >= 80 && count < 90) {
			count = count + 700;
			finalScore = count;
		}
		if (count >= 90 && count < 100) {
			count = count + 650;
			finalScore = count;
		}
	}

	public boolean playerWon() {
		// If the player has sunk all of the computer's ships, the player wins
		checkSunk(); // Calls the checkSunk method to set any ships to "sunk"
						// that haven't been set yet
		// Otherwise another turn would be required to end the game
		return (enemyShips.get(0).isSunk() && enemyShips.get(1).isSunk()
				&& enemyShips.get(2).isSunk() && enemyShips.get(3).isSunk()
				&& enemyShips.get(4).isSunk() && enemyShips.get(5).isSunk()
				&& enemyShips.get(6).isSunk() && enemyShips.get(7).isSunk() && enemyShips
				.get(8).isSunk());
	}

	public boolean compWon() {
		// If the player has no ships left, the player has lost to the computer
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (playerBoard[i][j] != '~' && playerBoard[i][j] != 'X'
						&& playerBoard[i][j] != 'ø')
					return false;
			}
		}
		return true;
	}

	// Auxiliary methods for preparing file-saves and file-loads

	private void saveGame(String filename) {
		// New method to save a file
		File file = new File(filename);
		try (BufferedOutputStream br = new BufferedOutputStream(
				new GZIPOutputStream(new FileOutputStream(file)));) {
			// Write the player board
			br.write(prepareData().getBytes());
			System.out.println("\t\t\t\t\t\t\t\t\t\t•• GAME SAVED! ••");
		} catch (IOException e) {
			System.out.println("Unable to write file " + file.toString());
		}
	}

	private String prepareData() {
		StringBuilder sb = new StringBuilder();
		// First step: Handle the Gameboards
		// Player Board
		sb.append(encodeBoard(playerBoard));
		sb.append(System.lineSeparator());
		// Computer Board
		sb.append(encodeBoard(compBoard));
		sb.append(System.lineSeparator());

		// Second step: ArrayList enemyShips
		for (Battleships b : enemyShips) {
			sb.append(b.toString());
			sb.append(System.lineSeparator());
		}
		sb.append(DELIMITER);
		sb.append(System.lineSeparator());
		// Third step: Remaining game Variables
		// The name
		sb.append(name);
		sb.append(System.lineSeparator());
		// The int - variables:
		sb.append(String.valueOf(count));
		sb.append(System.lineSeparator());
		sb.append(String.valueOf(wrongShot));
		sb.append(System.lineSeparator());
		sb.append(String.valueOf(sameShot));
		sb.append(System.lineSeparator());
		sb.append(String.valueOf(hitCount));
		sb.append(System.lineSeparator());
		sb.append(String.valueOf(missCount));
		sb.append(System.lineSeparator());
		sb.append(String.valueOf(waveCount));
		sb.append(System.lineSeparator());
		// The booleans
		sb.append(String.valueOf(encodeBooleans()));
		// Final step: return the completed String
		return sb.toString();
	}

	private int encodeBooleans() {
		int sum = 0;
		sum += (firstShot ? 1 : 0);
		sum += (fiveShot ? 2 : 0);
		sum += (tenShot ? 4 : 0);
		sum += (firstCompShot ? 8 : 0);
		sum += (fiveCompShot ? 16 : 0);
		sum += (tenCompShot ? 32 : 0);
		sum += (badShot ? 64 : 0);
		sum += (alreadyShot ? 128 : 0);
		sum += (subShot ? 256 : 0);
		sum += (cheater ? 512 : 0);
		sum += (allowed ? 1024 : 0);
		sum += (random ? 2048 : 0);
		sum += (showShips ? 4096 : 0);
		return sum;
	}

	private void decodeBooleans(int value) {
		firstShot = (value % 2 == 1 ? true : false);
		value /= 2;
		fiveShot = (value % 2 == 1 ? true : false);
		value /= 2;
		tenShot = (value % 2 == 1 ? true : false);
		value /= 2;
		firstCompShot = (value % 2 == 1 ? true : false);
		value /= 2;
		fiveCompShot = (value % 2 == 1 ? true : false);
		value /= 2;
		tenCompShot = (value % 2 == 1 ? true : false);
		value /= 2;
		badShot = (value % 2 == 1 ? true : false);
		value /= 2;
		alreadyShot = (value % 2 == 1 ? true : false);
		value /= 2;
		subShot = (value % 2 == 1 ? true : false);
		value /= 2;
		cheater = (value % 2 == 1 ? true : false);
		value /= 2;
		allowed = (value % 2 == 1 ? true : false);
		value /= 2;
		random = (value % 2 == 1 ? true : false);
		value /= 2;
		showShips = (value % 2 == 1 ? true : false);
	}

	// Converts the board char array into a String for saving
	private static String encodeBoard(char[][] board) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < board.length; i++) {
			String str = new String(board[i]);
			sb.append(str);
			sb.append(DELIMITER);
		}

		return sb.toString();
	}

	// converts a String representing the game board into a char array
	private static char[][] decodeBoard(String board) {
		char[][] tmpBoard;
		String[] tmpString = board.split(DELIMITER);
		tmpBoard = new char[tmpString.length][tmpString[0].length()];
		for (int i = 0; i < tmpString.length; i++) {
			tmpBoard[i] = tmpString[i].toCharArray();
		}
		return tmpBoard;
	}

	/**
	 * @clrscr removes all text from the screen
	 * @param none
	 */
	public static void clrscr() {
		for (int i = 1; i <= 50; i++)
			System.out.println();
	}

	/**
	 * @pressKey requires the user to press return to continue
	 * @param none
	 */
	public static void pressKey() {
		System.out.print("\nCommander, Please press return to continue : \n");
		userinput.nextLine();
	}

	public static void printWave() {
		System.out
				.println("¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·."
						+ "´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.¸");
	}

	public int getFinalScore() {
		return finalScore;
	}

}