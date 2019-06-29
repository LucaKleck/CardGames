package core;
import java.io.Serializable;

public abstract class Card implements Serializable {
	/**
	 * Card
	 */
	private static final long serialVersionUID = -5860863980460237488L;

	private CardType type;
	
	public Card(CardType type) {
		this.type = type;
	}
	
	public CardType getType() {
		return type;
	}
	
	public enum CardType {
		UNO("Uno");
		
		private final String typeName;
		
		CardType(String s) {
			typeName = s;
		}
		
		public String typeName() {
			return typeName;
		}
	}
	

}
