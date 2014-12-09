public class GameState {
	
	// This is basically a helper class to 
	// make it easier to transfer the game state
	// back and forth between save files
	
	private char[][] boardPlayer;
	private char[][] boardComputer;
	
	private int shotsPlayer;
	private int hitsPlayer;
	private int shotsComputer;
	private int hitsComputer;
	
	private boolean nextMovePlayer;
	
	public GameState() {
		
	}
 
	public GameState(char[][] boardPlayer, char[][] boardComputer,
			int shotsPlayer, int hitsPlayer, int shotsComputer,
			int hitsComputer, boolean nextMovePlayer) {
		super();
		this.boardPlayer = boardPlayer;
		this.boardComputer = boardComputer;
		this.shotsPlayer = shotsPlayer;
		this.hitsPlayer = hitsPlayer;
		this.shotsComputer = shotsComputer;
		this.hitsComputer = hitsComputer;
		this.nextMovePlayer = nextMovePlayer;
	}
 
	public char[][] getBoardPlayer() {
		return boardPlayer;
	}
 
	public void setBoardPlayer(char[][] boardPlayer) {
		this.boardPlayer = boardPlayer;
	}
 
	public char[][] getBoardComputer() {
		return boardComputer;
	}
 
	public void setBoardComputer(char[][] boardComputer) {
		this.boardComputer = boardComputer;
	}
 
	public int getShotsPlayer() {
		return shotsPlayer;
	}
 
	public void setShotsPlayer(int shotsPlayer) {
		this.shotsPlayer = shotsPlayer;
	}
 
	public int getHitsPlayer() {
		return hitsPlayer;
	}
 
	public void setHitsPlayer(int hitsPlayer) {
		this.hitsPlayer = hitsPlayer;
	}
 
	public int getShotsComputer() {
		return shotsComputer;
	}
 
	public void setShotsComputer(int shotsComputer) {
		this.shotsComputer = shotsComputer;
	}
 
	public int getHitsComputer() {
		return hitsComputer;
	}
 
	public void setHitsComputer(int hitsComputer) {
		this.hitsComputer = hitsComputer;
	}
 
	public boolean isNextMovePlayer() {
		return nextMovePlayer;
	}
 
	public void setNextMovePlayer(boolean nextMovePlayer) {
		this.nextMovePlayer = nextMovePlayer;
	}
	
	
}