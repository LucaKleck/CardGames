package uno;

import java.io.Serializable;

import core.GameModes;
import core.Player;

public abstract class PlayingField implements Serializable {
	private static final long serialVersionUID = -1601159585459251982L;
	public GameModes gameMode = GameModes.NONE;

	public void kick(Player p) {
		
	}
	
}
