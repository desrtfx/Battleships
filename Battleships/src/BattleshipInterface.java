import java.util.Scanner;

// The class that runs the battleship game
// The class that runs the battleship game
public class BattleshipInterface {

	public static Scanner userinput = new Scanner(System.in);

	public static void main(String[] args) {
		Menu.menu();
	}

	public void run(BattleshipGame game) {
		boolean backToMenu = false;
		int command;

		while ((!game.gameOver()) && (!backToMenu)) {
			// clrscr();
			game.printBoard();
			System.out
					.print("\n\n\t\t\t\t\t\t\tCommander "
							+ BattleshipGame.getName()
							+ ". Please Enter Missile Target Co-ordinates (e.g. B,4): ");
			String move = userinput.nextLine();

			// This is the call to the new command parser
			command = game.commandParser(move);
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

		}
		if (game.playerWon()) {
			if (Menu.isHighScore(game.getFinalScore())) {
				// Display a message that Player has a highscore
				// TODO: Write display message
				// Add the Highscore to the HighscoreList
				Menu.addHighScore(BattleshipGame.getName(),game.getFinalScore());
				Menu.saveScores();
			}
		}
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
		System.out.print("\nPress return to continue : \n");
		userinput.nextLine();
	}
}