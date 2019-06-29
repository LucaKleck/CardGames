package core;

import java.io.Serializable;

public class PlayerPair<PairItem> implements Serializable {
	private static final long serialVersionUID = 5079256780845609869L;
	
	private Player player;
	private PairItem pairItem;

	public PlayerPair(Player p, PairItem PairItem) {
		this.player = p;
		this.pairItem = PairItem;
	}
	
	public Player getPlayer() {
		return player;
	}

	public PairItem getPairItem() {
		return pairItem;
	}

}
