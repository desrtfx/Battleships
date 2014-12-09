import java.io.File;
import java.util.Scanner;

/**
 * The menu class holds the displayMenu method that constructs the output of the
 * main menu. Holds the The processUserChoices method which takes the user input
 * and outputs the corresponding response.
 * 
 * @author Trevor Bradley
 * @version 09.11.2014
 */

// comment desrtfx: I'd make all methods other than **menu()** private so that
// they are not accessible from outside the class

public class Menu {
	public static Scanner userinput = new Scanner(System.in);

	private static HighScores highScores = new HighScores("highScores.gz");;

	public static void menu() {
		int option; // declare the option field
		do {
			menuScreen();
			System.out
					.println("\n\t\t\t\t\t\t\t\t\t\tPlease select an option from above(1 - 7): "
							+ "" + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"); // prompt
																			// user
																			// to
																			// enter
																			// a
																			// selection
			option = Integer.parseInt(userinput.nextLine());
			switch (option) { // Play game
			case 1:
				newGame();
				break;

			case 2: // Load Saved Game -- Updated should actually be working now
				// clrscr(); // -- Probably not necessary here
				loadGame();
				break;

			case 3: // Display High Scores
				// clrscr();
				displayScores();
				break;

			case 4: // How to play
				clrscr();
				howToPlay();
				pressKey();
				break;

			case 5: // How to play
				clrscr();
				printCredits();
				pressKey();
				break;

			case 6: // How to play
				clrscr();
				printTrophies();
				pressKey();
				break;

			default: // everything else, do nothing
				break;
			}

		} while (option != 7);
		clrscr();
		System.out
				.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t°°°°°°°°°°°°°°°°°°°°°° FAREWELL COMMANDER! °°°°°°°°°°°°°°°°°°°°°°");
		printWave();
		System.out
				.println("\t\t\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° I hope you shall lead us again soon! °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}

	/**
	 * Display the 'How To Play' message to the screen
	 * 
	 * @param none
	 * @return none
	 */
	public static void howToPlay() {
		System.out
				.println("\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° HOW TO PLAY °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
		printWave();
		System.out
				.println("\t\tThe game has two 10x10 boards, from co-ordinates 0 to 9.\n"
						+ "\t\tYou are playing against a computer, whose board is on the right. Your board is on the left\n"
						+ "\t\tColumns are identified as 0 - 9 and Rows are identified as A - J.\n"
						+ "\t\tThe first co-ordinate you always enter is the row, followed by the column.\n"
						+ "\t\tWaves are denoted by '~', misses are denoted by 'ø', and hits are denoted by 'X'.\n"
						+ "\t\tSunken computer ships display what ship they were. E.G. Sink a Submarine and it will be displayed as 'S'.\n"
						+ "\t\tYou begin the game by entering the starting co-ordinates of your 9 ships,\n"
						+ "\t\tand if the boat is Horizontal (H) or Vertical (V)\n"
						+ "\t\tSo, if you wanted a ship placed vertical at row 4, column 2, you would type 4,2,V\n");
		printWave();
		System.out
				.println("\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° PLEASE NOTE °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n"
						+ "\t\tA quick way to start a game (AND PROBABLY THE BEST) is to enter '*' as your first location.\n"
						+ "\t\tThis will result in all boats being randomly placed on your grid.\n");
		printWave();
		System.out
				.println("\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° HOW TO CHEAT °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n"
						+ "\t\tIf you wish to see the locations of the enemy ships, enter '-1,-1' as your shot location.\n"
						+ "\t\tThe enemy ships will appear on the enemy grid as the same style as your own ships on your grid.\n");
	}

	/**
	 * Display the 'How To Play' message to the screen
	 * 
	 * @param none
	 * @return none
	 */
	public static void printCredits() {
		System.out
				.println("\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° CREDITS °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
		printWave();
		System.out
				.println("\t\t\t\t'Battelships Commander is an original game, designed and developed by Trevor Bradley.\n"
						+ "\t\t\t\tThe game was designed as an an assignment for the year 1 'Introduction to Software Development'\n"
						+ "\t\t\t\tmodule as part of the Applied Computing: Human Computer Interaction degree.\n"
						+ "\t\t\t\tAll of the code and design of this project are original content.\n\n"
						+ "\t\t\t\tIf you have any comments or questions about the game, you can contact me via email: t.z.bradley@dundee.ac.uk");
	}

	/**
	 * Display the 'Trophies' message to the screen
	 * 
	 * @param none
	 * @return none
	 */
	public static void printTrophies() {
		int input;
		System.out
				.println("\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° TROPHIES °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
		printWave();
		System.out
				.println("\t\t\t\tThere are a total of 12 trophies that can be achieved in during gameplay.\n"
						+ "\t\t\t\tThe trophies are listed below.\n\n"
						+ "\t\t\t\t ••'Beginners Luck?'\n"
						+ "\t\t\t\t ••'Don't Give Up!'\n"
						+ "\t\t\t\t ••'You Do Know How To Play Right?'\n"
						+ "\t\t\t\t ••'Faulty Keyboard!'\n"
						+ "\t\t\t\t ••'Just Making Sure!'\n"
						+ "\t\t\t\t ••'One Hit Wonder!'\n"
						+ "\t\t\t\t ••'I'm Telling!'\n"
						+ "\t\t\t\t ••'ZZZZzzzz...'\n"
						+ "\t\t\t\t ••'Must Be The Programmers Fault...'\n"
						+ "\t\t\t\t ••'Artificial Non-Intelligence'\n"
						+ "\t\t\t\t ••'Can AI Have Luck?'\n"
						+ "\t\t\t\t ••'8 Bit Wonder?'\n\n"
						+ "\t\t\t\tTo reveal how these trophies are achieved, enter '1'.\n\n");
		input = Integer.parseInt(userinput.nextLine());

		if (input == 1) {
			clrscr();
			System.out
					.println("\t\t\t\t\t\t ALL TROPHIES AND HOW TO UNLOCK THEM.\n\n");
			printWave();
			System.out
					.println("\t\t\t\t ••'Beginners Luck?' (hit a ship with your first shot)\n"
							+ "\t\t\t\t ••'Don't Give Up!' (miss with 5 shots in a row)\n"
							+ "\t\t\t\t ••'You Do Know How To Play Right?' (miss with 10 shots in a row)\n"
							+ "\t\t\t\t ••'Faulty Keyboard!' (entered wrong co-ordinate format 3 times)\n"
							+ "\t\t\t\t ••'Just Making Sure!' (enter co-ordinate that's already been hit 3 times)\n"
							+ "\t\t\t\t ••'One Hit Wonder!' (sink a submarine with your first shot)\n"
							+ "\t\t\t\t ••'I'm Telling!' (activate the cheat to reveal the enemy ship locations)\n"
							+ "\t\t\t\t ••'ZZZZzzzz...' (fire 80 missiles without the game ending)\n"
							+ "\t\t\t\t ••'Must Be The Programmers Fault...' (computer missed 5 shots in a row)\n"
							+ "\t\t\t\t ••'Artificial Non-Intelligence' (computer missed 10 shots in a row)\n"
							+ "\t\t\t\t ••'Can AI Have Luck?' (computer hit a ship with first shot)\n"
							+ "\t\t\t\t ••'8 Bit Wonder?' (computer sunk a submarine with first shot)\n\n\n\n");
		}
	}

	public static void menuScreen() {
		// display the menu
		clrscr();
		System.out
				.println("\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
		System.out
				.println("\t\t\t\t°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°••°•°•°•°•°•°• BATTLESHIPS COMMANDER™ •°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°•°");
		System.out
				.println("\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
		System.out
				.println("\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° an original game by Trevor Bradley© °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
		System.out
				.println("\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
		printWave();
		System.out
				.println("\n\n\t\t\t\t\t\t\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
		System.out.println("\t\t\t\t\t\t\t\t\t\t° »1«  Begin New game      °");
		System.out.println("\t\t\t\t\t\t\t\t\t\t° »2«  Load Saved Game     °");
		System.out.println("\t\t\t\t\t\t\t\t\t\t° »3«  Display High Scores °");
		System.out.println("\t\t\t\t\t\t\t\t\t\t° »4«  How To Play         °");
		System.out.println("\t\t\t\t\t\t\t\t\t\t° »5«  Credits             °");
		System.out.println("\t\t\t\t\t\t\t\t\t\t° »6«  Trophies           °");
		System.out.println("\t\t\t\t\t\t\t\t\t\t° »7«  Quit Game           °");
		System.out
				.println("\t\t\t\t\t\t\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n\n");
	}

	public static void newGame() {
		clrscr();
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
		BattleshipGame.setName(userinput.nextLine());
		clrscr();
		System.out.println("\t\t\t\t\t\t\t\tWelcome Commander "
				+ BattleshipGame.getName());
		printWave();
		System.out.println("!\n\n\t\t\t\t\t\t\tWe shall be valiant in battle!");
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		BattleshipGame game = new BattleshipGame();
		BattleshipInterface userInterface = new BattleshipInterface();
		userInterface.run(game);
	}

	public static void loadGame() {
		boolean fileIsValid;
		String filename = "";
		File file;
		do {
			fileIsValid = true;
			clrscr();
			System.out
					.println("\t\t\t\t\t\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° Load a saved game Commander °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
			printWave();
			if (!fileIsValid) {
				System.out.print("\n\nSorry, commander, your file name: "
						+ filename + " does not exist.");
			}
			System.out.println("");
			System.out
					.print("\n\nPlease enter a file name, leave blank for default, type q to quit: "); // Ask
																										// the
																										// player
																										// for
																										// file
																										// name
			String input = userinput.nextLine();
			filename = input;
			if ("".equals(filename)) {
				filename = "savegame.gz";
			} else {
				if ("q".equalsIgnoreCase(input.substring(0, 1))) {
					return;
				}
			}
			file = new File(filename);
			if (!file.exists()) {
				fileIsValid = false;
			}
		} while (!fileIsValid);
		clrscr();
		System.out
				.println("\t\t\t\t\t\t\t\tWelcome back Commander! Let us continue in battle!");
		BattleshipGame game = new BattleshipGame(file); // Initialize the game
														// from SaveGame State.
		BattleshipInterface userInterface = new BattleshipInterface();
		userInterface.run(game);
	}

	public static void saveScores() {
		highScores.saveToFile("highScores.gz");
	}

	public static void displayScores() {

		clrscr();

		System.out
				.println("\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°° HIGHSCORE LEADERBOARDS °°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
		printWave();
		System.out.println("\n\n");
		System.out.println("\t\t\t\t\t\t\t\tRANK        PLAYER NAME                  FINAL SCORE");
		System.out.println("\t\t\t\t\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
		// display highscores
		for(int i=0; i < highScores.getSize(); i++) {
			System.out.printf("\t\t\t\t\t\t\t\t%3d.        %-30s%7d\n",i + 1,highScores.get(i).getName(),highScores.get(i).getScore());
		}

		pressKey();

	}

	public static void printWave() {
		System.out
				.println("¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·."
						+ "´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.¸¸.·´¯`·.¸");
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

		System.out.print("\nPress return to continue Commander: \n");
		userinput.nextLine();
	}

	public static void addHighScore(String name, int finalScore) {
		highScores.add(name, finalScore);
	}

	public static boolean isHighScore(int finalScore) {
		return highScores.isHighScore(finalScore);
	}
}