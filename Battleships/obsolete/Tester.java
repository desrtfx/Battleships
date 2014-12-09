import java.util.Random;
 
public class Tester {
 
	public static Random rnd = new Random();
 
	public static void main(String[] args) {
 
		char[][] playerBoard = createGameBoard();
		char[][] compBoard = createGameBoard();
		int playerShots = rnd.nextInt(100);
		int playerHits = rnd.nextInt(50);
		int computerShots = rnd.nextInt(100);
		int computerHits = rnd.nextInt(50);
		boolean playerNextMove = true;
		
		GameState saveState = new GameState(playerBoard, compBoard, playerShots, playerHits,
				computerShots, computerHits, playerNextMove);
				
				
		
		FileIO.writeFile("savegame.txt", saveState);
		
		
		GameState loadState = FileIO.readFile("savegame.txt");
		
 
		System.out
				.println(compareBoards(playerBoard, loadState.getBoardPlayer() ) ? "Both Playerboards are identical"
						: "Playerboards not identical!");
		System.out
		.println(compareBoards(compBoard, loadState.getBoardComputer() ) ? "Both Computerboards are identical"
				: "Computerboards not identical!");
 
	}
 
	public static void printBoard(char[][] board) {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				System.out.print(board[row][col] + " ");
			}
			System.out.println();
		}
	}
 
	public static boolean compareBoards(char[][] board1, char[][] board2) {
		if (board1.length != board2.length) {
			return false;
		}
		for (int row = 0; row < board1.length; row++) {
			if (board1[row].length != board2[row].length) {
				return false;
			}
			for (int col = 0; col < board1[row].length; col++) {
				if (board1[row][col] != board2[row][col]) {
					return false;
				}
			}
		}
 
		return true;
	}
 
	public static char[][] createGameBoard() {
		char[][] board = new char[10][10];
 
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				board[row][col] = (char) (rnd.nextInt(26) + 65);
			}
		}
		return board;
	}
 
}