package uno.frames;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import uno.UnoPlayerHand;
import uno.UnoCard;
import uno.UnoPlayingField;

public class UnoPlayerCardListener implements MouseListener {
	private PropertyChangeSupport propertyChangeSupport;
	private Boolean selectedCardFlag = false;
	private UnoCard unoCard;
	private UnoPlayerHand playerHand;
	private UnoPlayerPanel playerPanel;
	private UnoPlayingField unoSpielfeld;

	public UnoPlayerCardListener(UnoCard unoCard, UnoPlayerHand playerHand, UnoPlayingField unoSpielfeld, UnoPlayerCardChangeListener cl, UnoPlayerPanel playerPanel) {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.playerPanel = playerPanel;
		this.unoCard = unoCard;
		this.playerHand = playerHand;
		this.unoSpielfeld = unoSpielfeld;
		addPropertyChangeListener(cl);
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
		
		Boolean oldSelectedCardFlag = selectedCardFlag;
		selectedCardFlag = true;
		propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, "selectedCardFlag", oldSelectedCardFlag, selectedCardFlag));
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
}
