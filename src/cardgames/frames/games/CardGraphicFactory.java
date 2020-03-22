package cardgames.frames.games;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import cardgames.constants.CardGamesConstants;
import cardgames.games.parts.UnoCard;

public class CardGraphicFactory {

	public static BufferedImage getUnoCardImage(UnoCard card) {
		BufferedImage bufferedImage = new BufferedImage(CardGamesConstants.CARD_WIDTH, CardGamesConstants.CARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.setColor(getColorFromCard(card));
		g.fillRect(0, 0, CardGamesConstants.CARD_WIDTH, CardGamesConstants.CARD_HEIGHT);
		g.setColor(Color.BLACK);
		g.setFont(new Font("TimesRoman", Font.BOLD, 18)); 
		g.drawString(card.toString(), 6, 19);
		g.setColor(Color.WHITE);
		g.setFont(new Font("TimesRoman", Font.BOLD, 18)); 
		g.drawString(card.toString(), 5, 18);
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
		default: return new Color(0, 0, 0, 0);
		}
	}
}
