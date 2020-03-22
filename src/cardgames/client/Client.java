package cardgames.client;

import java.io.Serializable;
import java.net.InetAddress;

import cardgames.server.ClientRole;

public class Client implements Serializable {
	private static final long serialVersionUID = -8841755473617218029L;
	private final String playerName;
	public final InetAddress playerAddress;
	private ClientRole role;
	
	public Client(String playerName, InetAddress playerAddress) {
		this.playerName = playerName;
		this.playerAddress = playerAddress;
		setRole(ClientRole.PLAYER);
	}

	public String getPlayerName() {
		return playerName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Client) {
			if( ((Client) obj).playerName.matches(playerName) && ((Client) obj).playerAddress.equals(playerAddress)) return true;
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return playerName;
	}

	public ClientRole getRole() {
		return role;
	}

	public void setRole(ClientRole role) {
		this.role = role;
	}
}
