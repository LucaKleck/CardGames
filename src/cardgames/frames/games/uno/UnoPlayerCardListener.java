package cardgames.frames.games.uno;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

import cardgames.client.Client;
import cardgames.constants.CardGamesConstants;
import cardgames.games.UnoGame;
import cardgames.games.parts.UnoCard;

public class UnoPlayerCardListener implements MouseListener {
	private UnoCard unoCard;
	private PlayerHandPanel playerHand;
	private Client localClient;
	int selectedColor;
	
	public UnoPlayerCardListener(UnoCard unoCard, PlayerHandPanel playerHand) {
		this.unoCard = unoCard;
		this.playerHand = playerHand;
		this.localClient = playerHand.getUnoPanel().getClientConnection().getClient();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(localClient.equals(playerHand.getUnoPanel().getGame().getCurrentPlayer())) {
			selectedColor = -1;
			if(unoCard.getCardId() == CardGamesConstants.CARD_WILD || unoCard.getCardId() == CardGamesConstants.CARD_DRAW_FOUR) {
				// card color prompt
				String color = JOptionPane.showInputDialog(playerHand, "Choose Color 0-3 [red,blue,green,yellow]");
				selectedColor = Integer.parseInt(color);
			}
			UnoGame g = playerHand.getUnoPanel().getGame();
			g.placeCard(unoCard, selectedColor);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
