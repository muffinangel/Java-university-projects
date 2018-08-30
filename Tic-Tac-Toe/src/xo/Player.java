package xo;

public class Player {
	private String gameChar;
	
	public Player(String c) {
		setGameChar(c);
	}

	public String getGameChar() {
		return gameChar;
	}

	public void setGameChar(String gameChar) {
		this.gameChar = gameChar;
	}
	
	public String getOpponentChar() {
		if(gameChar.equals("X"))
			return "O";
		return "X";
	}
}
