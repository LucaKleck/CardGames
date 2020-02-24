package cardgames.games.parts;

import java.io.Serializable;

import cardgames.client.Client;

public class Pair<C extends Client, Hand> implements Serializable {
	private static final long serialVersionUID = 937651350876506333L;
	private Client client;
	private Hand hand;
	
	public Pair(Client client, Hand h) {
		this.client = client;
		this.hand = h;
	}
	
	public Client getClient() {
		return client;
	}
	
	public Hand getHand() {
		return hand;
	}
	
	public void setHand(Hand hand) {
		this.hand = hand;
	}
}
