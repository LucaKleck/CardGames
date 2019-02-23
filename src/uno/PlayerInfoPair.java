package uno;

import core.Player;

public class PlayerInfoPair {
	private Player player;
	private PlayerHand playerHand;

	public PlayerInfoPair(Player p, PlayerHand playerHand) {
		this.player = p;
		this.playerHand = playerHand;
	}
	
	public Player getPlayer() {
		return player;
	}

	public PlayerHand getHand() {
		return playerHand;
	}

}
