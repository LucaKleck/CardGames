package cardgames.frames.games.uno;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import cardgames.constants.CardGamesConstants;
import cardgames.frames.games.CardGraphicFactory;
import cardgames.games.parts.UnoCard;

public class GraphicUnoCard extends Component {
	private static final long serialVersionUID = 3268477177661609774L;

	private BufferedImage unoCardImage = null;
	private boolean isCurrentCard = false;
	
	public GraphicUnoCard(UnoCard unoCard) {
		if(unoCard != null) {
			unoCardImage = CardGraphicFactory.getUnoCardImage(unoCard);
		}
		this.setPreferredSize(new Dimension(CardGamesConstants.CARD_WIDTH, CardGamesConstants.CARD_HEIGHT));
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(unoCardImage != null) {
			g.drawImage(unoCardImage, 0, 0, getWidth(), getHeight(), getParent());
		}
		if(isCurrentCard) {
			g.setColor(Color.RED);
			g.drawLine(0, 0, 0, getHeight());
		}
	}
	
	public void setIsCurrentCard(boolean isCurrentCard) {
		this.isCurrentCard = isCurrentCard;
	}
}
