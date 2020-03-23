package cardgames.server;

import java.io.Serializable;

import cardgames.client.GameModes;

public class ServerInfo implements Serializable {
	private static final long serialVersionUID = 1059949623756556263L;

	private String serverName;
	private GameModes gameMode;
	private boolean inGame;
	
	/**
	 * @param serverName - name of the server
	 * Server info contains: <br>
	 *  1. Server Name <br>
	 *  2. game mode <br>
	 */
	public ServerInfo(String serverName) {
		this.serverName = serverName;
		gameMode = GameModes.UNO;
		inGame = false;
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
	
	public boolean isInGame() {
		return inGame;
	}
	
	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	@Override
	public String toString() {
		return "Server["+serverName+"] "+gameMode;
	}
}
