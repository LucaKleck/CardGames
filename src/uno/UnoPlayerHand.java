package uno;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import uno.UnoPlayingField.ClientCommands;

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

	public void setSelectedCard(UnoCard selectedCard, UnoPlayingField unoPlayingField) {
		if(unoPlayingField.isClient()) {
			Socket clientSocket = null;
			ObjectOutputStream clientOos = null;
			ObjectInputStream clientOis = null;
			
			try {
			clientSocket = new Socket(unoPlayingField.host.getHostName(), UnoPlayingField.PORT);
			
			clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
			clientOis = new ObjectInputStream(clientSocket.getInputStream());
			
			clientOos.writeObject(ClientCommands.setPlayerHandSelectedCard);
			clientOos.writeObject(unoPlayingField.getPlayer());
			clientOos.writeObject(selectedCard);
			
			clientOis.close();
			clientOos.close();
			clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			setSelectedCard(selectedCard);
		}
		
	}
	
	public void setSelectedCard(UnoCard selectedCard) {
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
		
	}
	
	public int getPlayerNumber() {
		return playerNumber;
	}

	@Override
	public String toString() {
		return "PlayerHand [playerCards=" + playerCards + "]";
	}

}
