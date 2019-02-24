package uno;

import java.io.Serializable;

import core.Player;

public class PlayerInfoPair<PlayerHand> implements Serializable {
	private static final long serialVersionUID = 5079256780845609869L;
	
	private Player player;
	private UnoPlayerHand playerHand;

	public PlayerInfoPair(Player p, UnoPlayerHand playerHand) {
		this.player = p;
		this.playerHand = playerHand;
	}
	
	public Player getPlayer() {
		return player;
	}

	public UnoPlayerHand getHand() {
		return playerHand;
	}

}
