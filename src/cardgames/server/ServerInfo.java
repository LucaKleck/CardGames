package cardgames.server;

import java.io.Serializable;

import cardgames.client.GameModes;

public class ServerInfo implements Serializable {
	private static final long serialVersionUID = 1059949623756556263L;

	private String serverName;
	private GameModes gameMode;
	
	/**
	 * @param serverName - name of the server
	 * Server info contains: <br>
	 *  1. Server Name <br>
	 *  2. game mode <br>
	 */
	public ServerInfo(String serverName) {
		this.serverName = serverName;
	}

	public String getServerName() {
		return serverName;
	}

	public GameModes getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameModes gameMode) {
		this.gameMode = gameMode;
	}
}
