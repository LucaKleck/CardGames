import java.io.Serializable;

public class Player implements Serializable {
	/**
	 * Player
	 */
	private static final long serialVersionUID = 5207847101051209828L;
	
	private String playerName;
	
	public Player(String playerName) {
		this.playerName = playerName;
	}

	public synchronized String getPlayerName() {
		return playerName;
	}

	@Override
	public String toString() {
		return "Player " + playerName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Player) {
			if(((Player) obj).getPlayerName().matches(playerName)) return true;
		}
		return super.equals(obj);
	}

}
