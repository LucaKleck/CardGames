package cardgames.frames.games.uno;

import java.util.ArrayList;

import javax.swing.JPanel;

import cardgames.games.parts.UnoCard;
import net.miginfocom.swing.MigLayout;

public class PlacedCardsPanel extends JPanel {
	private static final long serialVersionUID = 3187355188642721716L;
	private UnoPane unoPanel;
	
	public PlacedCardsPanel(UnoPane unoPanel) {
		setLayout(new MigLayout("", "[150px]", "[250px]"));
		this.unoPanel = unoPanel;
		updatePanel();
	}

	public synchronized void updatePanel() {
		removeAll();
		ArrayList<UnoCard> placedCards = unoPanel.getGame().getPlacedCards();
		UnoCard currenUnoCard = unoPanel.getGame().getCurrentCard();
		
		GraphicUnoCard currentUnoCard = new GraphicUnoCard(currenUnoCard);
		currentUnoCard.setIsCurrentCard(true);
		this.add(currentUnoCard, "cell 0 0,alignx left,aligny top");
		for(int i = 0; i <placedCards.size(); i++) {
			try {
				this.add(new GraphicUnoCard(placedCards.get(i)), "cell "+(placedCards.size()-i-1)+" 1");
			} catch (NullPointerException e) {
			}
		}
		revalidate();
	}
	
}
