package uno;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import core.Card;

public class UnoCard extends Card implements Serializable {
	/**
	 * Uno Card
	 */
	private static final long serialVersionUID = -1541976897604713032L;

	private static int cardCount = 0;
	
	public static final int CARD_WIDTH = 150;
	public static final int CARD_HEIGHT = 300;
	
	public static final int COLOR_RED = 0;
	public static final int COLOR_BLUE = 1;
	public static final int COLOR_GREEN = 2;
	public static final int COLOR_YELLOW = 3;
	public static final int COLOR_WILD = 4;
	
	// NUMERALS
	public static final int CARD_ZERO = 0;
	public static final int CARD_ONE = 1;
	public static final int CARD_TWO = 2;
	public static final int CARD_THREE = 3;
	public static final int CARD_FOUR = 4;
	public static final int CARD_FIVE = 5;
	public static final int CARD_SIX = 6;
	public static final int CARD_SEVEN = 7;
	public static final int CARD_EIGHT = 8;
	public static final int CARD_NINE = 9;
	
	// SPECIAL CARDS
	public static final int CARD_SKIP = 10;
	public static final int CARD_REVERSE = 11;
	public static final int CARD_WILD = 12;
	public static final int CARD_DRAW_TWO = 13;
	public static final int CARD_DRAW_FOUR = 14;
	
	// Resources
	private int color;
	private int cardId;
	private int cardIdentifier = ++cardCount;
	
	public UnoCard(int color, int cardId) {
		super("UnoCard");
		this.color = color;
		this.cardId = cardId;
	}

	/**
	 * @param desiredColor
	 * @param wildOrFourCard
	 */
	public void setColor(int desiredColor, UnoCard wildOrFourCard) {
		System.out.println(wildOrFourCard);
		System.out.println(desiredColor);
		if(wildOrFourCard.getCardId() == 12 || wildOrFourCard.getCardId() == 14) {
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
		case 10: return "Skip";
		case 11: return "Reverse";
		case 12: return "Wild";
		case 13: return "Draw Two";
		case 14: return "Draw Four";
		default: return ""+cardId;
		}
	}
	
	public BufferedImage getUnoCardImage() {
		BufferedImage bufferedImage = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.setColor(getColorFromCard(this));
		g.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);
		g.setColor(Color.BLACK);
		g.setFont(new Font("TimesRoman", Font.BOLD, 16)); 
		g.drawString(this.toString(), 6, 18);
		g.setColor(Color.WHITE);
		g.setFont(new Font("TimesRoman", Font.BOLD, 16)); 
		g.drawString(this.toString(), 5, 18);
		return bufferedImage;
	}

	private static Color getColorFromCard(UnoCard card) {
		if(card == null) return Color.PINK; 
		switch(card.getColor()) {
		case 0: return Color.RED;
		case 1: return Color.BLUE;
		case 2: return Color.GREEN;
		case 3: return Color.YELLOW;
		case 4: return Color.BLACK;
		default: return Color.PINK;
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
