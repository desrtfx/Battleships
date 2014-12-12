import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class BattleshipGame.
 */
public class BattleshipGame {

	// The following single character string is used
	// to separate the rows of the game board
	// it can be changed to any single character that **does not**
	// exist in the game board.
	/** The Constant DELIMITER. */
	public static final String DELIMITER = "§";

	/** The Constant LEADER. */
	public static final String LEADER = "  ";

	/** The Constant SEPARATOR_SHORT. */
	private static final String SEPARATOR_SHORT = "\t";

	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = SEPARATOR_SHORT + SEPARATOR_SHORT;

	/** The Constant CMD_PASSED. */
	public static final int CMD_PASSED = 0;

	/** The Constant CMD_MENU. */
	public static final int CMD_MENU = -1;

	/** The Constant CMD_QUIT. */
	public static final int CMD_QUIT = -2;

	/** The Constant CMD_INVALID. */
	public static final int CMD_INVALID = Integer.MIN_VALUE;

	// This class contains the important methods to generate the game and its
	// rules

	/** The Constant boardSize. */
	private final static int boardSize = 10;

	/** The player board. */
	private char[][] playerBoard; // Array for the Player's Board

	/** The comp board. */
	private char[][] compBoard; // Array for the Computer's Board

	/** The input. */
	private String input;

	/** The last move. */
	private String lastMove = ""; // The Computer's last move, initialized to
	// avoid NullPointerExceptions
	/** The name. */
	public static String name; // The player's name, to be used without creating
	// an object
	/** The ship col loc. */
	private int shipRowLoc, shipColLoc;

	/** The ship direction. */
	private Direction shipDirection;

	/** The saved comp col. */
	private int savedCompRow, savedCompCol; // savedComp variables save the
	// previous row/column
	/** The show ships. */
	private boolean allowed, random = false, showShips = false;

	/** The move type. */
	private MoveType moveType;

	/** The enemy ships. */
	private ArrayList<Battleships> enemyShips = new ArrayList<Battleships>();

	/** The player ships. */
	private Ships playerShips;

	/** The comp ships. */
	private Ships compShips;

	/** The count. */
	int count = 0;

	/** The final score. */
	int finalScore = 0;

	/** The wrong shot. */
	int wrongShot = 0;

	/** The same shot. */
	int sameShot = 0;

	/** The miss count. */
	int missCount = 0;

	/** The hit count. */
	int hitCount = 0;

	/** The wave count. */
	int waveCount = 100;

	/** The big count. */
	boolean firstShot, fiveShot, tenShot, firstCompShot, fiveCompShot,
			tenCompShot, badShot, alreadyShot, subShot, subCompShot, cheater,
			bigCount;

	/** The userinput. */
	static Scanner userinput = new Scanner(System.in);

	/** The rnd. */
	private Random rnd = new Random();

	private boolean backToMenu;

	// GameState saveState = new GameState(playerBoard, compBoard, count);
	/**
	 * Instantiates a new battleship game.
	 */
	public BattleshipGame() {
		playerBoard = new char[boardSize][boardSize];
		compBoard = new char[boardSize][boardSize];
		playerShips = new Ships();
		compShips = new Ships();
	}

	/**
	 * Load from File constructor<BR>
	 * 
	 * This constructor expects a filename from which it loads the saved game.
	 *
	 * @param file
	 *            the file
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
			System.out.println("Unable to read file " + file.toString());
		}
	}

	/**
	 * Cheat.
	 */
	public void cheat() {
		// Causes the computer's board to be printed instead of just waves, hits
		// and misses
		showShips = true;
	}

	/**
	 * Check sunk.
	 */
	public void checkSunk() {
		for (Ship s : playerShips.getShips()) {
			if (s.isSunk() && !s.isReported()) {
				s.setSinkReported();
				System.out.println("\n\n\t\t\t\t\t\t\t\t\t" + name
						+ "! Ha Ha, I sank your " + s.getName() + "!");
			}
		}
		for (Ship s : compShips.getShips()) {
			if (s.isSunk() && !s.isReported()) {
				s.setSinkReported();
				System.out.println("\n\n\t\t\t\t\t\t\t\t\tBLAST YOU " + name
						+ "! You sank my " + s.getName() + "!");
			}
		}
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
	 * @return the int
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
			if ("".equals(row)) {
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

	/**
	 * Comp ai.
	 */
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

	/**
	 * Comp move.
	 */
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

	/**
	 * Comp won.
	 *
	 * @return true, if successful
	 */
	public boolean compWon() {
		return playerShips.allSunken();
	}

	/**
	 * Game over.
	 *
	 * @return true, if successful
	 */
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

	/**
	 * Gets the final score.
	 *
	 * @return the final score
	 */
	public int getFinalScore() {
		return finalScore;
	}

	/**
	 * Gets the trophies.
	 *
	 * @return the trophies
	 */
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

	/**
	 * Inits the Boards and places the ships.
	 */
	public void init() {
		// Initialize the game boards
		initializeBoard(playerBoard);
		initializeBoard(compBoard);

		// Get Commander's name
		System.out
				.println("\t\t\t\t\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° Welcome to the Battleship War Commander! °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
		printWave();
		System.out.println("");
		System.out
				.print("\n\nFirstly Commander, what name would you like to be addressed by? "); // Ask
																								// the
																								// player
																								// their
																								// name
		name = userinput.nextLine();
		// clrscr();
		System.out.println("\n\n\n\t\t\t\t\t\t\t\tWelcome Commander "
				+ BattleshipGame.getName());
		printWave();
		System.out.println("!\n\n\t\t\t\t\t\t\tWe shall be valiant in battle!");
		System.out.println("\n\n\n");

		// Start ship placement

		String reply = "n";
		do {
			printWave();
			if (!"yn".contains(reply.toLowerCase().substring(0, 1))) {
				System.out.println("\t\t\t\t\t\t\t\tCommander " + name
						+ " please enter only \"y\" or \"n\"\n\n\n");
			}
			System.out.print("\t\t\t\t\t\t\t\tCommander " + name
					+ " do you want your ships placed randomly (y/n)? ");
			reply = userinput.nextLine();
		} while (!"yn".contains(reply.toLowerCase().substring(0, 1)));
		random = "y".equals(reply.toLowerCase().substring(0, 1));

		// Place player Ships
		for (Ship ship : playerShips.getShips()) {
			if (random) {
				placeRandom(playerBoard, ship);
			} else {
				placeShip(ship);
			}
		}

		// Place computer ships
		for (Ship ship : compShips.getShips()) {
			placeRandom(compBoard, ship);
		}

	}

	public Command run() {
		Command cmd = Command.NO_ACTION;
		while (!gameOver() && (cmd != Command.MENU) && (cmd != Command.QUIT)) {
			printBoard();
			cmd = userMove();

			if ((cmd == Command.NO_ACTION) && !playerWon()) {

			}
		}

		return cmd;
	}

	private Command userMove() {
		System.out.print("\n\n\t\t\t\t\t\t\tCommander " + name
				+ ". Please Enter Missile Target Co-ordinates (e.g. B,4): ");
		String move = userinput.nextLine();

		// This is the call to the new command parser
		int command = commandParser(move);
		// The new command parser would actually return an int
		// to tell the main loop whether to quit to the main menu
		// or to quit the game completely, but neither of these
		// features is implemented yet, so it's safe to discard the
		// return value of the command parser.
		switch (command) {
		case BattleshipGame.CMD_QUIT: // Quit
			Menu.saveScores();
			clrscr();
			System.out
					.println("\t\t\t\t\t\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° FAREWELL COMMANDER! °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
			Menu.printWave();
			System.out
					.println("\t\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° I hope you shall lead us again soon! °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n\n\n\n\n\n\n\n\n\n\n\n\n");
			System.exit(0);
			break;
		case BattleshipGame.CMD_MENU: // return to menu
			backToMenu = true;
			break;
		default:
			break;
		}

		if (playerWon()) {
			if (Menu.isHighScore(getFinalScore())) {
				// Display a message that Player has a highscore
				// TODO: Write display message
				// Add the Highscore to the HighscoreList
				Menu.addHighScore(BattleshipGame.getName(), getFinalScore());
				Menu.saveScores();
			}
		}

		return null;
	}

	/**
	 * Initialize board.
	 *
	 * @param board
	 *            the board
	 */
	public void initializeBoard(char[][] board) {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				board[row][col] = '~';
			}
		}
	}

	/**
	 * Int to string.
	 *
	 * @param i
	 *            the i
	 * @return the string
	 */
	public String intToString(int i) {
		if ((i >= 0) && (i <= 9)) {
			return String.valueOf((char) (i + 'A'));
		}
		return ""; // out of range. possible erroneous value.
	}

	/**
	 * Checks if is shootable.
	 *
	 * @param row
	 *            the row
	 * @param col
	 *            the col
	 * @return true, if is shootable
	 */
	public boolean isShootable(int row, int col) {
		// Checks if the attempted shot at the computer's boat is allowed based
		// on if it has already been sunk
		return (!compShips.isSunk(row, col));
	}

	/**
	 * Place ship.
	 *
	 * @param ship
	 *            the ship
	 */
	public void placeShip(Ship ship) {
		do {
			clrscr();
			printBoard();
			System.out.print("\tEnter the " + ship.getName()
					+ "'s location and direction (e.g. D,2,H): ");
			userSetShips(); // Asks user to enter the ship location, then
			// converts them to the appropriate types

			if (shipRowLoc < 0 || shipRowLoc >= boardSize) { // Make sure the
				// coordinates
				// entered are
				// within the
				// board
				System.out
						.println("Please enter a row value inside the boundaries of the grid.");
				allowed = false; // If they are not, start over
			}

			if (shipRowLoc > (boardSize - ship.getSize())
					&& (shipDirection == Direction.VERT)) {
				// Keeps the ship within the board if it is vertical
				System.out
						.println("Your ship goes outside the battle grid, please try again.");
				allowed = false;
			}

			if (shipRowLoc < 0 || shipColLoc >= boardSize) { // Make sure the
				// coordinates
				// entered are
				// within the
				// board
				System.out
						.println("Please enter a column value inside the boundaries of the battle grid.");
				allowed = false; // If they are not, start over
			}

			if (shipColLoc > (boardSize - ship.getSize())
					&& (shipDirection == Direction.HORIZ)) {
				// Keeps the ship within the board if it is horizontal
				System.out
						.println("Your ship goes outside the battle grid, please try again.");
				allowed = false;
			}

			if (random)
				break;

			// Make sure nothing is already placed in any of the spots needed to
			// place the ship
			if (allowed) { // Do this only if the coordinates are within the
				// boards limits
				ship.place(shipRowLoc, shipColLoc, shipDirection);
				for (int i = 0; i < ship.getSize(); ++i) {
					// If horizontal, column increments and row doesn't, and
					// vice versa.
					if (playerBoard[shipRowLoc
							+ (i * ship.getDirection().rowOffs())][shipColLoc
							+ (i * ship.getDirection().colOffs())] != '~') {
						System.out
								.println("Your ship placement overlaps another ship, please try another location Commander.");
						allowed = false;
					}
				}
			}

		} while (!allowed); // Repeatedly does this until everything is typed
		// and converted properly
		if (!random) {
			placeShipMarker(playerBoard, ship);
		}
	}

	// The playerMove routine should only be called when valid rows and colums
	// can be passed as parameters
	// Should really receive only int row and int col and do the actual move,
	// nothing more.
	// So, I've hooked up a command parser that parses the User Input before it
	// even reaches the playerMove method
	/**
	 * Player move.
	 *
	 * @param stringRow
	 *            the string row
	 * @param stringCol
	 *            the string col
	 */
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
				// Battleships ship = foeShips.get(makePos(row, col));
				// ship.setHit(row, col);
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

	/**
	 * Player won.
	 *
	 * @return true, if successful
	 */
	public boolean playerWon() {
		// If the player has sunk all of the computer's ships, the player wins
		return compShips.allSunken();
	}

	/**
	 * Prints the board.
	 */
	public void printBoard() {

		// Prints both boards beside each other, but doesn't show computer ships
		// unless showShips is true

		// Print the header with fixed lengths
		StringBuilder sb = new StringBuilder();
		sb.append(SEPARATOR);
		sb.append(LEADER);
		sb.append(prepareCentered("Commander", name, 20));
		sb.append(SEPARATOR);
		sb.append(LEADER);
		sb.append(prepareCentered("Commander", "Computer", 20));
		sb.append(SEPARATOR);
		sb.append(String.format("%-25s%s%-25s", "Player Ships",
				SEPARATOR_SHORT, "Computer ships"));
		sb.append("\n");

		// Print the column numbers for both boards
		sb.append(SEPARATOR);
		sb.append(LEADER);
		sb.append(prepareHeader());
		sb.append(SEPARATOR);
		sb.append(LEADER);
		sb.append(prepareHeader());
		sb.append(SEPARATOR);
		sb.append(String.format("%-25s%s%-25s", "=========================",
				SEPARATOR_SHORT, "========================="));
		sb.append("\n");

		// Print each board row + ship status
		for (int row = 0; row < boardSize; row++) {
			sb.append(SEPARATOR); // Leading spacer
			sb.append(String.format("%-2s", intToString(row))); // Row letter
			for (int col = 0; col < boardSize; col++) {
				sb.append(String.format("%-2s", playerBoard[row][col]));
			}
			sb.append(SEPARATOR);
			sb.append(String.format("%-2s", intToString(row))); // Row letter
			for (int col = 0; col < boardSize; col++) {
				sb.append(String.format("%-2s", getCompSymbol(row, col)));
			}
			// If there are ships to display
			if (row < playerShips.getShips().size()) {
				sb.append(SEPARATOR);
				sb.append(playerShips.get(row).toString());

				sb.append(SEPARATOR_SHORT);
				if (compShips.get(row).isSunk()) {
					sb.append(compShips.get(row).toString());
				} else {
					sb.append(compShips.get(row).getName());
				}
			}
			sb.append("\n");
		}

		System.out.println(sb.toString());

		printSpacer();
		// End method here?
		// Move next sysout to new line
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

	public void printSpacer() {
		System.out.println();
		System.out.println();
		printWave();
		System.out.println();
		System.out.println();
	}

	/**
	 * Replace ships.
	 *
	 * @param index
	 *            the index
	 */
	public void replaceShips(int index) {
		// Once the ship is completely hit i.e. hits == length, replace the
		// values in the array with the shipSymbol
		// correctly through the array
		Ship ship = compShips.get(index);
		for (int i = 0; i < ship.getSize(); ++i) {
			compBoard[ship.getStartRow() + (i * ship.getDirection().rowOffs())][ship
					.getStartCol() + (i * ship.getDirection().colOffs())] = ship
					.getSymbol();
		}
	}

	/**
	 * Sets the score.
	 */
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

	/**
	 * String to int.
	 *
	 * @param i
	 *            the i
	 * @return the int
	 */
	public int stringToInt(String i) {
		char ch = i.toUpperCase().charAt(0);
		if ((ch >= 'A') && (ch <= 'J')) {
			return ch - 'A';
		}
		if ((ch >= '0') && (ch <= '9')) {
			return ch - '0';
		}
		return -1; // out of range. possible erroneous value.
	}

	/**
	 * Decode booleans.
	 *
	 * @param value
	 *            the value
	 */
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

	/**
	 * Display command error.
	 */
	private void displayCommandError() {
		System.out.println();
		System.out
				.println("\t\t\t\tI am sorry Commander "
						+ BattleshipGame.getName()
						+ ".\n\n"
						+ "\t\t\t\tIt seems that you did not enter your co-ordinate correctly.... It must be a faulty keyboard Commander....\n");
	}

	/**
	 * Encode booleans.
	 *
	 * @return the int
	 */
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

	/**
	 * Gets the comp symbol.
	 *
	 * @param row
	 *            the row
	 * @param col
	 *            the col
	 * @return the comp symbol
	 */
	private String getCompSymbol(int row, int col) {
		if (compBoard[row][col] == 'ø' || compBoard[row][col] == 'X') {
			return compBoard[row][col] + " "; // If already hit or miss, show
			// that they have
		}
		if (showShips) {
			return compBoard[row][col] + " "; // If they entered -1,-1, show the
			// ship locations
		}
		if (compShips.isSunk(row, col)) {
			return compBoard[row][col] + " "; // If the ship has been sunk, it
			// is allowed to print its
			// symbol
		}
		return '~' + " "; // Just show waves
	}

	/**
	 * Place random.
	 *
	 * @param board
	 *            the board
	 * @param ship
	 *            the ship
	 */
	private void placeRandom(char[][] board, Ship ship) {
		int startRow = 0, startCol = 0; // Starting coordinate of the randomly
		// placed ship
		Direction dir = (rnd.nextBoolean() ? Direction.HORIZ : Direction.VERT);

		boolean allowed = false;
		while (!allowed) {
			// Chose random spot to put the ship
			startRow = (int) (rnd.nextInt(boardSize - ship.getSize()
					* dir.rowOffs())); // Randomly set ship starting row
			startCol = (int) (rnd.nextInt(boardSize - ship.getSize()
					* dir.colOffs())); // Randomly set ship starting col
			allowed = true;

			// Verify that nothing else is already placed in any of the
			// coordinates needed
			for (int i = 0; i < ship.getSize(); ++i) {
				if (board[startRow + (i * dir.rowOffs())][startCol
						+ (i * dir.colOffs())] != '~') {
					allowed = false;
				}
			}
		}
		// Randomly place ships
		ship.place(startRow, startCol, dir);
		placeShipMarker(board, ship);

	}

	/**
	 * Place ship marker.
	 *
	 * @param board
	 *            the board
	 * @param ship
	 *            the ship
	 */
	private void placeShipMarker(char[][] board, Ship ship) {
		for (int i = 0; i < ship.getSize(); i++) {
			board[ship.getStartRow() + (i * ship.getDirection().rowOffs())][ship
					.getStartCol() + (i * ship.getDirection().colOffs())] = ship
					.getSymbol();
		}
	}

	/**
	 * Prepare centered.
	 *
	 * @param title
	 *            the title
	 * @param sname
	 *            the sname
	 * @param maxLength
	 *            the max length
	 * @return the string
	 */
	private String prepareCentered(String title, String sname, int maxLength) {
		String tmp = title + ":" + sname;
		String tmpTitle = title;
		if (tmp.length() > maxLength) {
			tmpTitle = "";
		}
		tmp = tmpTitle + ":" + sname;
		int curLength = tmp.length();
		int spacesNeeded = (maxLength - curLength) / 2;
		for (int i = 0; i < spacesNeeded; i++) {
			tmp = " " + tmp + " ";
		}
		return tmp;
	}

	/**
	 * Prepare data.
	 *
	 * @return the string
	 */
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

	/**
	 * Prepare header.
	 *
	 * @return the string
	 */
	private String prepareHeader() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < boardSize; i++) {
			sb.append(String.format("%-2d", i));
		}
		return sb.toString();
	}

	// Auxiliary methods for preparing file-saves and file-loads

	/**
	 * Sanitize row.
	 *
	 * @param row
	 *            the row
	 * @return the string
	 */
	private String sanitizeRow(String row) {
		// Helper method to make sure the row is a String
		// in the range of "0" to "9"
		// Even if the user entered "A" to "J"
		// returns an empty String if the row is not in
		// the valid range.
		if ("-1".equals(row)) { // Cheat code
			return "-1";
		}
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

	/**
	 * Save game.
	 *
	 * @param filename
	 *            the filename
	 */
	private void saveGame(String filename) {
		// New method to save a file
		File file = new File(filename);
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new GZIPOutputStream(new FileOutputStream(file)), "UTF-8"));) {
			// Write the player board
			bw.append(prepareData());
			System.out.println("\t\t\t\t\t\t\t\t\t\t•• GAME SAVED! ••");
		} catch (IOException e) {
			System.out.println("Unable to write file " + file.toString());
		}

	}

	/**
	 * User set ships.
	 */
	private void userSetShips() {
		// This method gets input from the user, interprets it, converts it, and
		// error checks it
		allowed = true; // If it remains true, the user did everything right
		input = userinput.nextLine().toUpperCase();
		/*
		 * Check with regular expression if the user input is syntactically and
		 * semantically correct.
		 * 
		 * <pre> The regular expression pattern means: [A-J0-9] : a single
		 * character in the range A-J or 0 to 9 ,? : zero or one comma as a
		 * literal [0-9] : a single character in the range 0 to 9 ,? : zero or
		 * one comma as a literal [HV] : Either H or V </pre> Only if all of the
		 * above are fulfilled, the .matches method returns true
		 */
		if (!input.matches("[A-J0-9],?[0-9],?[HV]")) {
			System.out.println("Sorry commander " + name
					+ ". I don't understand your input. Please try again.");
			allowed = false;
			return; // Input is wrong, no need to check further - exit method
			// early
		}
		// Check if the user entered a comma, if not, add commas in the proper
		// positions
		if (!input.contains(",")) {
			input = input.substring(0, 1) + "," + input.substring(1, 2) + ","
					+ input.substring(2, 3);
		}
		// Here we are sure that the input is in proper row, col, direction
		// format
		// and that it contains only valid values
		// Split user's input into an array
		String[] inputs = input.split(",");

		// distribute the data
		shipRowLoc = stringToInt(inputs[0]);
		shipColLoc = stringToInt(inputs[1]);
		shipDirection = Direction.get(inputs[2]);

	}

	/**
	 * Clrscr.
	 *
	 * @clrscr removes all text from the screen
	 */
	public static void clrscr() {
		for (int i = 1; i <= 50; i++)
			System.out.println();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public static String getName() {
		return name;
	}

	/**
	 * Press key.
	 *
	 * @pressKey requires the user to press return to continue
	 */
	public static void pressKey() {
		System.out.print("\nCommander, Please press return to continue : \n");
		userinput.nextLine();
	}

	/**
	 * Prints the wave.
	 */
	public static void printWave() {
		System.out
				.println("\n¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·."
						+ "´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.¸\n");
	}

	/**
	 * Sets the name.
	 *
	 * @param aName
	 *            the new name
	 */
	public static void setName(String aName) {
		name = aName;
	} // Static so we can set it without creating an object

	// converts a String representing the game board into a char array
	/**
	 * Decode board.
	 *
	 * @param board
	 *            the board
	 * @return the char[][]
	 */
	private static char[][] decodeBoard(String board) {
		char[][] tmpBoard;
		String[] tmpString = board.split(DELIMITER);
		tmpBoard = new char[tmpString.length][tmpString[0].length()];
		for (int i = 0; i < tmpString.length; i++) {
			tmpBoard[i] = tmpString[i].toCharArray();
		}
		return tmpBoard;
	}

	// Converts the board char array into a String for saving
	/**
	 * Encode board.
	 *
	 * @param board
	 *            the board
	 * @return the string
	 */
	private static String encodeBoard(char[][] board) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < board.length; i++) {
			String str = new String(board[i]);
			sb.append(str);
			sb.append(DELIMITER);
		}

		return sb.toString();
	}

}