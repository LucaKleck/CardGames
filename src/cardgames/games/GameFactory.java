package cardgames.games;

import cardgames.client.ClientServerConnector;
import cardgames.client.GameModes;
import cardgames.server.Server;

public class GameFactory {

	public static Game createGame(GameModes gameMode, Server server, ClientServerConnector csc) {
		if(server == null) {
			switch (gameMode) {
			case NO_GAME:
				break;
			case UNO:
				return new UnoGame(csc);
			default:
				break;
			}
		} else {
			switch (gameMode) {
			case NO_GAME:
				return null;
			case UNO:
				return new UnoGame(server);
			default:
				break;
			}
		}
		return null;
	}
}
