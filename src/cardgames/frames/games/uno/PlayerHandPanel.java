package cardgames.frames.games.uno;

import java.util.ArrayList;

import javax.swing.JLayeredPane;

import cardgames.games.parts.UnoCard;
import net.miginfocom.swing.MigLayout;

public class PlayerHandPanel extends JLayeredPane {
	private static final long serialVersionUID = 9048084746521718257L;
	private UnoPane unoPanel;
	
	public PlayerHandPanel(UnoPane unoPanel) {
		this.unoPanel = unoPanel;
		setLayout(new MigLayout("", "[]", "[]"));
		updatePanel();
	}

	public synchronized void updatePanel() {
		removeAll();
		ArrayList<UnoCard> playerCards;
		playerCards = unoPanel.getGame().getPlayerHand().getHand();
		if(playerCards == null) return;
		for(int i = 0; i < playerCards.size(); i++) {
			GraphicUnoCard graphicUnoCard = new GraphicUnoCard(playerCards.get(i));
			graphicUnoCard.addMouseListener( new UnoPlayerCardListener(playerCards.get(i), this));
			add(graphicUnoCard, "cell "+(i)+" 0");
		}
		revalidate();
	}
	
	public UnoPane getUnoPanel() {
		return unoPanel;
	}
	
}
