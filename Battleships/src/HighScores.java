import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class HighScores {

	// Maximum allowed size for the list
	private static final int MAX_SIZE = 10;
	
	//private static final String DELIMITER = "§";
	
	private List<HighScore> scores = new ArrayList<HighScore>();
	
	public HighScores() {
		initList();
		for(int i=0; i < MAX_SIZE; i++) {
			add("Default",0);
		}
	}
	
	public HighScores(String fileName) {
		// if a valid filename is supplied, load from file,
		// else clear the scores
		if (isValidFile(fileName)) {
			loadFromFile(new File(fileName));
		} else {
			initList();
			for(int i=0; i < MAX_SIZE; i++) {
				add("Default",0);
			}
		}
	}


	public HighScores(File file)  {
		// if a valid file is supplied, load from file,
		// else clear the scores
		if (isValidFile(file)) {
			loadFromFile(file);
		} else {
			initList();
		}
	}



	public void loadFromFile(File file) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new FileInputStream(file))))) {
			String line;
			while ((line = br.readLine()) != null) {
				add(line);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + file.toString());
		} catch (IOException e) {
			System.out.println("Unable to write file " + file.toString());
		}		
		
	}
	


	public void saveToFile(String fileName) {
		File file = new File(fileName);
		saveToFile(file);
	}
	
	public void saveToFile(File file) {
		try (BufferedOutputStream br = new BufferedOutputStream(
				new GZIPOutputStream(new FileOutputStream(file)));) {
			// Write the HighScore list
			
			br.write(prepareData().getBytes());
			
		} catch (IOException e) {
			System.out.println("Unable to write file " + file.toString());
		}
	}


	
	public void add(String line) {
		HighScore hScore = new HighScore(line);
		add(hScore);
		
	}
	public void add(String name, int score) {
		HighScore hScore = new HighScore(name, score);
		add(hScore);
	}
	
	
	
	public void add(HighScore hScore) {
		// Add a score to the highscore list
		scores.add(hScore);
		// sort the list
		Collections.sort(scores);
		// If there are more than max elements in the list
		// remove the last one.
		if (scores.size() > MAX_SIZE) {
			scores.remove(scores.size()-1);
		}
	}
	
	public boolean isHighScore(int score) {
		Collections.sort(scores);
		return (score >= scores.get(scores.size()-1).getScore());
	}
	
	

	public int getSize() {
		return scores.size();
	}
	
	public HighScore get(int index) {
		if ((index < 0) || (index > scores.size()-1)) {
			throw new IndexOutOfBoundsException("Index out of bounds");
		}
		return scores.get(index);
	}
	
	// formatted String for the output of the HighScore list
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		// make sure the list is sorted
		Collections.sort(scores);
		// Iterate through the list from top to bottom
		for(int i=0; i<scores.size();i++) {
			// leading tabs
			sb.append("\t\t\t\t");
			// rank
			sb.append(String.format("%3d.", (i + 1)));
			// Space between rank and name
			sb.append("\t\t");
			// name
			sb.append(scores.get(i).getName());
			// space between name and score
			sb.append("\t\t\t");
			// score
			sb.append(String.format("%5d",scores.get(i).getScore()));
			// new Line
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
	
	// Clear the highscore list 
	private void initList() {
		scores.clear();
	}
	
	private boolean isValidFile(String fileName) {
		File file = new File(fileName);
		return isValidFile(file);
	}
	
	private boolean isValidFile(File file) {
		return file.exists();
	}
	
	private String prepareData() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i < scores.size(); i++) {
			sb.append(scores.get(i).toString());
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
}
