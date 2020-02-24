package cardgames.games;

import java.io.Serializable;
import java.util.ArrayList;

import cardgames.client.Client;

public class ClientGameAction implements Serializable {
	private static final long serialVersionUID = -4554868246995268584L;
	
	private Client originClient;
	private GameActionType gat;
	/**
	 *  data example: <br>
	 *   placeCard -> data = card & if card.color == WILD add color <br>
	 *   drawCard -> data = null <br>
	 */
	private ArrayList<Object> data;
	
	/**
	 * @param origin
	 * @param gat
	 * @param data
	 * Clients send actions to server
	 */
	public ClientGameAction(Client origin, GameActionType gat, ArrayList<Object> data) {
		originClient = origin;
		this.gat = gat;
		this.data = data;
	}
	
	public Client getOriginClient() {
		return originClient;
	}
	
	public GameActionType getGameActionType() {
		return gat;
	}
	
	public ArrayList<Object> getData() {
		return data;
	}
	
}
