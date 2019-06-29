package uno.frames;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import uno.UnoCard;
import uno.UnoPlayerHand;
import uno.UnoPlayingField;

public class UnoPlayerCardListener implements MouseListener {
	private UnoCard unoCard;
	private UnoPlayerHand playerHand;
	private UnoPlayerPanel playerPanel;
	private UnoPlayingField unoSpielfeld;

	public UnoPlayerCardListener(UnoCard unoCard, UnoPlayerHand playerHand, UnoPlayingField unoSpielfeld, UnoPlayerPanel playerPanel) {
		this.playerPanel = playerPanel;
		this.unoCard = unoCard;
		this.playerHand = playerHand;
		this.unoSpielfeld = unoSpielfeld;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(playerPanel.isDisabledCards()) return;
		
		playerHand.setSelectedCard(unoCard, unoSpielfeld);
		
		playerPanel.updateField();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
