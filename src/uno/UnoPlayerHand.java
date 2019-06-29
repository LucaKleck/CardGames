package uno;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import core.Client;
import core.Server.ServerCommands;

public class UnoPlayerHand implements Serializable {
	/**
	 * PlayerHand
	 */
	private static final long serialVersionUID = -2836501365680670633L;
	private static int nrOfHands = 0;
	private int playerNumber;
	private ArrayList<UnoCard> playerCards = new ArrayList<>();
	private UnoCard selectedCard = null; 
	
	public UnoPlayerHand(UnoPlayingField unoPlayingField) {
		nrOfHands++;
		playerNumber = nrOfHands;
		drawStartHand(unoPlayingField);
	}
	
	public void drawStartHand(UnoPlayingField unoPlayingField) {
		for(int i = 0; i < 7; i++) {
			playerCards.add(unoPlayingField.drawCard());
		}
	}
	
	public ArrayList<UnoCard> getPlayerCards() {
		return playerCards;
	}

	public UnoCard getSelectedCard() {
		return selectedCard;
	}
	
	public UnoCard playSelectedCard() {
		UnoCard toBePlayed = selectedCard;
		selectedCard = null;
		return toBePlayed;
	}

	public UnoCard setSelectedCard(UnoCard selectedCard, UnoPlayingField unoPlayingField) {
		if(unoPlayingField.getPlayer().isClient) {
			LinkedList<Object> data = new LinkedList<Object>();
			data.add(selectedCard);
			setSelectedCard((UnoCard) Client.clientObject.createServerRequest(ServerCommands.setPlayerHandSelectedCard, data).data.get(0));
		} else {
			setSelectedCard(selectedCard);
		}
		return selectedCard;
	}
	
	public UnoCard setSelectedCard(UnoCard selectedCard) {
		if(!playerCards.contains(selectedCard)) {
			for(UnoCard card : playerCards) {
				if(card.equals(selectedCard)) {
					playerCards.remove(card);
					break;
				}
			}
		} else {
			playerCards.remove(selectedCard);
		}
		if(this.selectedCard != null) {
			playerCards.add(this.selectedCard);
		}
		this.selectedCard = selectedCard;
		return selectedCard;
	}
	
	public int getPlayerNumber() {
		return playerNumber;
	}

	@Override
	public String toString() {
		return "PlayerHand [playerCards=" + playerCards + "]";
	}

}
