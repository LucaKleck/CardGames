package core;

import java.net.InetAddress;

import core.Server.ServerCommands;

/**
 * Host opens a lobby, players join it.
 * @author Luca Kleck
 *
 */
public class Lobby {
	private GameModes gameMode;
	public Client playerClient;
	
	public Lobby(Player client) {
		Server s = new Server(client, this);
		new Thread(s).start();
	}
	
	public Lobby(Player client, InetAddress address) {
		playerClient = new Client(client, address);
	}

	public GameModes getGameMode() {
		if(playerClient.client.isClient) {
			return (GameModes) playerClient.createServerRequest(ServerCommands.getGameMode, null).data.get(0);
		}
		return gameMode;
	}

	public void setGameMode(GameModes gameMode) {
		if(playerClient.client.isClient) return;
		this.gameMode = gameMode;
	}
	
}
