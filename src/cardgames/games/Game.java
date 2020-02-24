package cardgames.games;

public abstract class Game {
	/*
	 * Structure of the Games:
	 * GameMode is taken as constructor
	 * ID -> every player is assigned an ID (could be chosen manually maybe? - for reconnect)
	 * Hands are assigned the ID's (Pair object)
	 */
	
	/*
	 * how to
	 * Client sends join request
	 * client connects to lobby
	 * lobby starts game
	 * gametype get sent to everyone and they start fake game
	 * everyone sends data to server (validity of play will be checked locally)
	 * server sends to everyone except sender what changed
	 * Client handler needs client to have access to locally stored game
	 */
	
	public abstract void processClientGameAction(ClientGameAction gat);
	public abstract void processServerData(GameData gd);
}
