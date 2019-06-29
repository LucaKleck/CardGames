package core;
import java.io.Serializable;
import java.net.InetAddress;

public class Player implements Serializable {
	/**
	 * Player
	 */
	private static final long serialVersionUID = 5207847101051209828L;
	
	public final String playerName;
	public final InetAddress playerAddress;
	public final boolean isClient;
	
	public Player(String playerName, InetAddress playerAddress, boolean isClient) {
		this.playerName = playerName;
		this.playerAddress = playerAddress;
		this.isClient = isClient;
	}

	@Override
	public String toString() {
		return "Player " + playerName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Player) {
			if( ((Player) obj).playerName.matches(playerName) && ((Player) obj).playerAddress.equals(playerAddress)) return true;
		}
		return super.equals(obj);
	}

}
