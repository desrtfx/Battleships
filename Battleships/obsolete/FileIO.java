import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
 
public class FileIO {
 
	// The following single character string is used
	// to separate the rows of the game board
	// it can be changed to any single character that **does not**
	// exist in the game board.
	public static final String DELIMITER = "§";
	
	public static void writeFile(String fileName, GameState gameState) {
		File file = new File(fileName);
		writeFile(file, gameState);
	}
 
	public static void writeFile(File file, GameState gameState) {
		try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
			// Write the player board
			br.write(encodeBoard(gameState.getBoardPlayer()));
			br.newLine();
			// Write the computer board
			br.write(encodeBoard(gameState.getBoardComputer()));
			br.newLine();
			// Write the number of shots a player fired
			br.write(String.valueOf(gameState.getShotsPlayer()));
			br.newLine();
			// Write the number of hits a player had
			br.write(String.valueOf(gameState.getHitsPlayer()));
			br.newLine();
			// Write the number of shots the computer fired
			br.write(String.valueOf(gameState.getShotsComputer()));
			br.newLine();
			// Write the number of hits the computer had
			br.write(String.valueOf(gameState.getHitsComputer()));
			br.newLine();
			// Write **true** if the player has the next move, 
			// **false** if the computer has the next move
			br.write(gameState.isNextMovePlayer() ? "True" : "False");
			br.newLine();
		} catch (IOException e) {
			System.out.println("Unable to write file " + file.toString());
		}
	}
	
	public static GameState readFile(String fileName) {
		File file = new File(fileName);
		return readFile(file);
	}
	
 
	private static GameState readFile(File file) {
		GameState gameState = new GameState();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			if (line != null) { // Read and decode the player board
				gameState.setBoardPlayer(decodeBoard(line));
			}
			line = br.readLine();
			if (line != null) { // Read and decode the computer board
				gameState.setBoardComputer(decodeBoard(line));
			}
			line = br.readLine();
			if (line != null) { // Read the shots the player fired
				gameState.setShotsPlayer(Integer.parseInt(line));
			}
			line = br.readLine();
			if (line != null) { // Read the hits the player had
				gameState.setHitsPlayer(Integer.parseInt(line));
			}
			line = br.readLine();
			if (line != null) { // Read the shots the computer fired
				gameState.setShotsComputer(Integer.parseInt(line));
			}
			line = br.readLine();
			if (line != null) { // Read the hits the computer had
				gameState.setHitsComputer(Integer.parseInt(line));
			}
			line = br.readLine();
			if (line != null) { // Read whose move is next (true = player, false = computer)
				gameState.setNextMovePlayer("true".equalsIgnoreCase(line));
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + file.toString());
		} catch (IOException e) {
			System.out.println("Unable to write file " + file.toString());
		} 
		return gameState; // Return the completed gameState
	}
 
	// Helper methods to encode and decode the game boards
	
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
}