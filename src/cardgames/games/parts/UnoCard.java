package cardgames.games.parts;

import cardgames.constants.CardGamesConstants;

public class UnoCard extends Card {
	private static final long serialVersionUID = 9011741522108466788L;
	private static int cardCount = 0;

	// Resources
	private int color;
	private int cardId;
	private int cardIdentifier = ++cardCount;
	
	public UnoCard(int color, int cardId) {
		super(CardType.UNO);
		this.color = color;
		this.cardId = cardId;
	}
	
	/**
	 * @param desiredColor
	 */
	public void setColor(int desiredColor) {
		if(this.cardId == CardGamesConstants.CARD_WILD || this.cardId == CardGamesConstants.CARD_DRAW_FOUR) {
			this.color = desiredColor;
		}
	}
	
	public int getColor() {
		return color;
	}

	public int getCardId() {
		return cardId;
	}

	@Override
	public String toString() {
		switch(cardId) {
		case -1: return "";
		case 10: return "Skip";
		case 11: return "Reverse";
		case 12: return "Wild";
		case 13: return "Draw Two";
		case 14: return "Draw Four";
		default: return ""+cardId;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UnoCard) {
			if(((UnoCard) obj).cardIdentifier == cardIdentifier) {
				return true;
			}
		}
		return super.equals(obj);
	}
	
}
