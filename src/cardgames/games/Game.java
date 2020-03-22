package cardgames.games;

import java.awt.Component;

import cardgames.client.Client;

public abstract class Game extends Component {
	private static final long serialVersionUID = -2580898150839465654L;
	
	public abstract void processClientGameAction(ClientGameAction gat);
	public abstract void processServerData(GameData gd);
	public abstract GameData createSynchData(Client c);
}
