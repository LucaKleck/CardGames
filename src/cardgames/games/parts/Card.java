package cardgames.games.parts;

import java.io.Serializable;

public abstract class Card implements Serializable {
	private static final long serialVersionUID = -9158338172493655449L;
	public final CardType cardType;
	
	public Card( CardType cardType ) {
		this.cardType = cardType;
	}
	
}
