package cardgames.games;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import cardgames.client.Client;
import cardgames.client.ClientCommands;
import cardgames.client.ClientOutputPackage;
import cardgames.client.ClientServerConnector;
import cardgames.client.GameModes;
import cardgames.constants.CardGamesConstants;
import cardgames.games.parts.CardDeck;
import cardgames.games.parts.CardFactory;
import cardgames.games.parts.Pair;
import cardgames.games.parts.UnoCard;
import cardgames.server.ClientRole;
import cardgames.server.Server;
import cardgames.server.ServerCommands;
import cardgames.server.ServerOutputPackage;

/**
 * @author Luca
 *
 */
public class UnoGame extends Game {
	private static final long serialVersionUID = -6701262866765959008L;

	private static final String UPDATE = "update";

	// only one of these can be !=null
	private Server server;									// server
	private ClientServerConnector clientServerConnector; 	// local server connection
	
	private Pair<Client, ArrayList<UnoCard>> playerHand; 	// local player hand
	private UnoCard selectedCard;							// local selected card
	private Client nextPlayer;								// local/server 

	private CardDeck<UnoCard> cardDeck;						// server cards
	private LinkedList<Pair<Client, ArrayList<UnoCard>>> playerList;// server list
	
	private ArrayList<UnoCard> placedCards;					// server/local placed cards
	private Client currentPlayer;							// server
	private UnoCard currentCard;							// server
	private ArrayList<Pair<Client, Integer>> cardlessPlayers;// Players that ended the game
	
	/**
	 * How many cards does the player need to draw
	 */
	private int drawCardStackNumber = 0;					// server
	/**
	 * if drawCardStackNumber is >0 player needs to place a card (stack +2/+4) or draw n cards
	 */
	private boolean hasDrawn = false;						// server
	private boolean isReverse = false;						// server
	
	/**
	 * Server side UNO game
	 * @param s
	 */
	public UnoGame(Server s) {
		this.server = s;
		placedCards = new ArrayList<UnoCard>(5);
		CardFactory<UnoCard> cf = new CardFactory<UnoCard>();
		cardDeck = new CardDeck<UnoCard>(cf.createDeck(GameModes.UNO));
		
		playerList = new LinkedList<Pair<Client,ArrayList<UnoCard>>>();
		for(Client c : s.getClients(ClientRole.PLAYER)) {
			playerList.add(new Pair<Client, ArrayList<UnoCard>>(c, drawHand()));
		}
		
		setCurrentPlayer(playerList.get(0).getClient());
		
		currentCard = drawCard();
		
		if(currentCard.getColor() == CardGamesConstants.COLOR_WILD) {
			Random r = new Random();
			currentCard.setColor(r.nextInt(3));
		}
		
		nextPlayer = getNextPlayer();
	}

	/**
	 * Client side UNO game
	 * @param csc
	 */
	@SuppressWarnings("unchecked")
	public UnoGame(ClientServerConnector csc, GameData gd) {
		this.clientServerConnector = csc;
		setCurrentCard((UnoCard) gd.getData().get(0));
		setPlacedCards((ArrayList<UnoCard>) gd.getData().get(1));
		setCurrentPlayer((Client) gd.getData().get(2));
		setReverse(((Boolean) gd.getData().get(3)).booleanValue());
		setHasDrawn(((Boolean) gd.getData().get(4)).booleanValue());
		setDrawCardStackNumber(((Integer) gd.getData().get(5)).intValue());
		if(clientServerConnector.getClient().getRole().equals(ClientRole.PLAYER)) {
			setPlayerHand((Pair<Client, ArrayList<UnoCard>>) gd.getData().get(6));
			setNextPlayer((Client) gd.getData().get(7));
		} else {
			setNextPlayer((Client) gd.getData().get(6)); // TODO change order so player hand is last and can be if'ed easier
		}
		firePropertyChange(UPDATE, null, null);
	}
	
	public synchronized void processClientGameAction(ClientGameAction gat) {
		switch (gat.getGameActionType()) {
		case DRAW_CARD:
		{
			{
				// draws n cards -> send n cards to player
				ArrayList<UnoCard> cards = new ArrayList<UnoCard>();
				if(drawCardStackNumber == 0) cards.add(drawCard());
				for(int i = 0; i < drawCardStackNumber; drawCardStackNumber--) {
					cards.add(drawCard());
				}
				ArrayList<Object> dataContainer = new ArrayList<Object>();
				ArrayList<Object> gameData = new ArrayList<Object>();
				gameData.add(cards);
				getPlayerPair(gat.getOriginClient()).getHand().addAll(cards);
				dataContainer.add(new GameData(GameDataType.CARDS_DRAWN, gameData));
				ServerOutputPackage sop1 = new ServerOutputPackage(ServerCommands.GAME_DATA, dataContainer);
				server.sendServerOutputPackage(sop1, gat.getOriginClient());
				// no placement after force drawing cards
				if(currentCard.getCardId() == CardGamesConstants.CARD_DRAW_FOUR || currentCard.getCardId() == CardGamesConstants.CARD_DRAW_TWO) {
					setHasDrawn(false);
					currentPlayer = getNextPlayer();
					sendNextPlayer();
				} else {
					if(!canPlaceCard(currentPlayer)) { // draw until you can place
						setHasDrawn(false);
					} else {
						setHasDrawn(true);
					}
				}
			}
			// update all clients draw card stack number & has drawn
			{
				ArrayList<Object> dataContainer = new ArrayList<Object>();
				ArrayList<Object> gameData = new ArrayList<Object>();
				gameData.add(Integer.valueOf(drawCardStackNumber));
				// pack it
				dataContainer.add(new GameData(GameDataType.CURRENT_STACK_NUMBER, gameData));
				ServerOutputPackage sop = new ServerOutputPackage(ServerCommands.GAME_DATA, dataContainer );
				// send it
				server.broadcastServerOutputPackage(sop);
			}
			{
				ArrayList<Object> dataContainer = new ArrayList<Object>();
				ArrayList<Object> gameData = new ArrayList<Object>();
				gameData.add(Boolean.valueOf(hasDrawn));
				// pack it
				dataContainer.add(new GameData(GameDataType.CURRENT_HAS_DRAWN, gameData));
				ServerOutputPackage sop = new ServerOutputPackage(ServerCommands.GAME_DATA, dataContainer );
				// send it
				server.broadcastServerOutputPackage(sop);
			}
			break;
		}
		case PLACE_CARD:
		{
			// places selected card
			UnoCard card = (UnoCard) gat.getData().get(0); 						// client side card
			Pair<Client, ArrayList<UnoCard>> playerpair = getPlayerPair(gat.getOriginClient()); 	
			card = getCardFromHand(playerpair.getHand(), card); 				// server side card
			int color = (((Integer) gat.getData().get(1)).intValue());			// take data
			if(placeCard(card, color)) {										// call server side placement
				playerpair.getHand().remove(card);
				// send player the card to remove
				{
					ArrayList<Object> dataContainer = new ArrayList<Object>();
					ArrayList<Object> gameData = new ArrayList<Object>();
					gameData.add(gat.getData().get(0));
					dataContainer.add(new GameData(GameDataType.REMOVE_CARD, gameData));
					ServerOutputPackage sop1 = new ServerOutputPackage(ServerCommands.GAME_DATA, dataContainer);
					server.sendServerOutputPackage(sop1, gat.getOriginClient());
				}
				if(playerpair.getHand().isEmpty()) {
					// VI VON ZULUL
				}
				// broadcast PLACED_CARD to each client
				{
					ArrayList<Object> dataContainer = new ArrayList<Object>();
					ArrayList<Object> gameData = new ArrayList<Object>();
					gameData.add(card);
					gameData.add(currentPlayer);
					gameData.add(Integer.valueOf(drawCardStackNumber));
					gameData.add(Boolean.valueOf(isReverse));
					gameData.add(Boolean.valueOf(hasDrawn));
					// pack it
					dataContainer.add(new GameData(GameDataType.PLACED_CARD, gameData));
					ServerOutputPackage sop = new ServerOutputPackage(ServerCommands.GAME_DATA, dataContainer );
					// send it
					server.broadcastServerOutputPackage(sop);
				}
			}
			break;
		}
		case REQ_PAIR:
		{
			// asks for his own hand
			for(Pair<Client, ArrayList<UnoCard>> pair : playerList) {
				if(pair.getClient().equals(gat.getOriginClient())) {
					ArrayList<Object> dataContainer = new ArrayList<Object>();
					ArrayList<Object> gameData = new ArrayList<Object>();
					gameData.add(pair);
					dataContainer.add(new GameData(GameDataType.PAIR, gameData));
					server.sendServerOutputPackage(new ServerOutputPackage(ServerCommands.GAME_DATA, dataContainer), pair.getClient());
				}
			}
			break;
		}
		case REQ_SYNCH:
		{
			ArrayList<Object> dataContainer = new ArrayList<Object>();
			ArrayList<Object> gameData = new ArrayList<Object>();

			gameData.add(currentCard);
			gameData.add(placedCards);
			gameData.add(currentPlayer);
			gameData.add(Boolean.valueOf(isReverse));
			gameData.add(Boolean.valueOf(hasDrawn));
			gameData.add(Integer.valueOf(drawCardStackNumber));
			if(gat.getOriginClient().getRole().equals(ClientRole.PLAYER)) {
				gameData.add(getPlayerPair(gat.getOriginClient()));
			}
			gameData.add(getNextPlayer());
			if(gat.getOriginClient().getRole().equals(ClientRole.PLAYER)) {
				dataContainer.add(new GameData(GameDataType.SYNCH_PLAYER_TO_SERVER, gameData));
			} else {
				dataContainer.add(new GameData(GameDataType.SYNCH_SPECTATOR_TO_SERVER, gameData));
			}
			server.sendServerOutputPackage(new ServerOutputPackage(ServerCommands.GAME_DATA, dataContainer), gat.getOriginClient());
			break;
		}
		case REQ_NEXT_PLAYER: 
		{
			ArrayList<Object> data = new ArrayList<Object>();
			ArrayList<Object> gameData = new ArrayList<Object>();
			gameData.add(getNextPlayer());
			server.sendServerOutputPackage(new ServerOutputPackage(ServerCommands.GAME_DATA, data), gat.getOriginClient());
			break;
		}
		default:
			return;
		}
	}
	
	/**
	 * client side processing
	 */
	@SuppressWarnings("unchecked")
	public synchronized void processServerData(GameData gd) {
		switch (gd.getGdt()) {
		case SYNCH_PLAYER_TO_SERVER:
			setCurrentCard((UnoCard) gd.getData().get(0));
			setPlacedCards((ArrayList<UnoCard>) gd.getData().get(1));
			setCurrentPlayer((Client) gd.getData().get(2));
			setReverse(((Boolean) gd.getData().get(3)).booleanValue());
			setHasDrawn(((Boolean) gd.getData().get(4)).booleanValue());
			setDrawCardStackNumber(((Integer) gd.getData().get(5)).intValue());
			setPlayerHand((Pair<Client, ArrayList<UnoCard>>) gd.getData().get(6));
			setNextPlayer((Client) gd.getData().get(7));
			break;
		case SYNCH_SPECTATOR_TO_SERVER:
			setCurrentCard((UnoCard) gd.getData().get(0));
			setPlacedCards((ArrayList<UnoCard>) gd.getData().get(1));
			setCurrentPlayer((Client) gd.getData().get(2));
			setReverse(((Boolean) gd.getData().get(3)).booleanValue());
			setHasDrawn(((Boolean) gd.getData().get(4)).booleanValue());
			setDrawCardStackNumber(((Integer) gd.getData().get(5)).intValue());
			setNextPlayer((Client) gd.getData().get(6));
			break;
		case CARDS_DRAWN:
			// cards to be added to client hand
			playerHand.getHand().addAll((ArrayList<UnoCard>) gd.getData().get(0));
			// server will update other players as well
			break;
		case CARD_LIST:
			setPlacedCards((ArrayList<UnoCard>) gd.getData().get(0));
			break;
		case CURRENT_CARD:
			setCurrentCard((UnoCard) gd.getData().get(0));
			break;
		case CURRENT_HAS_DRAWN:
			setHasDrawn(((Boolean) gd.getData().get(0)).booleanValue());
			break;
		case CURRENT_PLAYER:
			setCurrentPlayer((Client) gd.getData().get(0));
			break;
		case CURRENT_REVERSE:
			setReverse(((Boolean) gd.getData().get(0)).booleanValue());
			break;
		case CURRENT_STACK_NUMBER:
			setDrawCardStackNumber(((Integer) gd.getData().get(0)).intValue());
			break;
		case PLACED_CARD:
			placedCards.add(currentCard);
			
			if(placedCards.size() > 5) {
				placedCards.remove(0);
			}
			
			setCurrentCard((UnoCard) gd.getData().get(0));
			setCurrentPlayer((Client) gd.getData().get(1));
			setDrawCardStackNumber(((Integer) gd.getData().get(2)).intValue());
			setReverse(((Boolean) gd.getData().get(3)).booleanValue());
			setHasDrawn(((Boolean) gd.getData().get(4)).booleanValue());
			break;
		case PAIR:
			setPlayerHand((Pair<Client, ArrayList<UnoCard>>) gd.getData().get(0));
			break;
		case CURRENT_NEXT_PLAYER:
			setNextPlayer((Client) gd.getData().get(0));
			break;
		case REMOVE_CARD:
			removeCardFromClient((UnoCard) gd.getData().get(0));
			break;
		default:
			break;
		}
		firePropertyChange(UPDATE, null, null);
	}
	
	/**
	 * @param card
	 * @param color
	 * @return returns true on successful placement
	 */
	public synchronized boolean placeCard(UnoCard card, int color) {
		if(!isPlacable(card)) {
			return false;
		}
		if(server == null) { // if its a client instance
			// send server a place card command
			ArrayList<Object> data = new ArrayList<Object>();
			ArrayList<Object> data1 = new ArrayList<Object>();
			data1.add(card);
			data1.add(Integer.valueOf(color));
			data.add(new ClientGameAction(getClientServerConnector().getClient(), GameActionType.PLACE_CARD, data1));
			clientServerConnector.sendClientOutputPackage(new ClientOutputPackage(ClientCommands.GAME_ACTION, data));
			card.setColor(color);
		} else { // if it's on the server
			placedCards.add(currentCard);
			
			card.setColor(color);
			
			if(placedCards.size() > 5) {
				placedCards.remove(0);
			}
			
			currentCard = card;
			
			if(currentCard.getCardId() == CardGamesConstants.CARD_REVERSE) {
				isReverse = !isReverse;
			}
			if(currentCard.getCardId() == CardGamesConstants.CARD_SKIP) {
				currentPlayer = getNextPlayer();
			}
			if(currentCard.getCardId() == CardGamesConstants.CARD_DRAW_TWO) {
				drawCardStackNumber += 2;
			}
			if(currentCard.getCardId() == CardGamesConstants.CARD_DRAW_FOUR) {
				drawCardStackNumber += 4;
			}
			
			currentPlayer = getNextPlayer();
			hasDrawn = false;
			// exit and send it to other clients
		}
		return true;
	}
	
	// TODO add rule sets / flags
	private synchronized boolean isPlacable(UnoCard uCard) {
		UnoCard localCurrentCard = getCurrentCard();
		if(uCard == null) return false;
		
		if(uCard.getCardId() == CardGamesConstants.CARD_DRAW_FOUR) return true; // Always play +4 cards
		if(uCard.getCardId() == CardGamesConstants.CARD_DRAW_TWO && // If it's a draw two card and there is a draw four on the field || Might not be in the real game
				(localCurrentCard.getCardId() == CardGamesConstants.CARD_DRAW_FOUR 
				|| (localCurrentCard.getCardId() == CardGamesConstants.CARD_DRAW_TWO) ) ) {
			return true;
		}
		if(uCard.getColor() == localCurrentCard.getColor() && drawCardStackNumber > 0 && !(uCard.getCardId() == CardGamesConstants.CARD_DRAW_FOUR || uCard.getCardId() == CardGamesConstants.CARD_DRAW_TWO) && (localCurrentCard.getCardId() == CardGamesConstants.CARD_DRAW_FOUR || localCurrentCard.getCardId() == CardGamesConstants.CARD_DRAW_TWO) ) {
			return false;
		}
		// if player plays wild and there is no +4 / +2 card
		if(uCard.getCardId() == CardGamesConstants.CARD_WILD && 
				!(localCurrentCard.getCardId() == CardGamesConstants.CARD_DRAW_FOUR 
				|| (localCurrentCard.getCardId() == CardGamesConstants.CARD_DRAW_TWO) ) ) {
			return true;
		}
		// General statement for other cases
		// if the number/symbol on the card is the same the card can be placed
		if(uCard.getCardId() == localCurrentCard.getCardId()) {
			return true;
		}
		// if the colors are the same the card can be placed
		if(uCard.getColor() == localCurrentCard.getColor()) {
			return true;
		}
		return false;
	}
	
	/**
	 * returns true if player can place any card
	 * @param player
	 * @return canPlaceCard
	 */
	public boolean canPlaceCard(Client player) {
		if(server == null) {
			for(UnoCard u : getPlayerHand().getHand()) {
				if(isPlacable(u)) return true;
			}
		} else {
			for(UnoCard u : getPlayerPair(player).getHand()) {
				if(isPlacable(u)) return true;
			}
		}
		return false;
	}
	// TODO FIX IT DAMNIT!
	public Client getNextPlayer() {
		if(server == null) {
			return nextPlayer;
		} else {
			Client nextPlayer = null;
			if(isReverse) {
				for(Iterator<Pair<Client, ArrayList<UnoCard>>> iterator = playerList.listIterator(0); iterator.hasNext();) {
					if (currentPlayer.equals(iterator.next().getClient())) {
						if(((ListIterator<Pair<Client, ArrayList<UnoCard>>>) iterator).hasPrevious()) {
							nextPlayer = ((ListIterator<Pair<Client, ArrayList<UnoCard>>>) iterator).previous().getClient();
						} else {
							nextPlayer = playerList.getLast().getClient();
						}
						return nextPlayer;
					}
				}
			} else {
				for(Iterator<Pair<Client, ArrayList<UnoCard>>> iterator = playerList.iterator(); iterator.hasNext(); ) {
					if (currentPlayer.equals(iterator.next().getClient())) {
						if(iterator.hasNext()) {
							nextPlayer = iterator.next().getClient();
						} else {
							nextPlayer = playerList.getFirst().getClient();
						}
						return nextPlayer;
					}
				}
			}
			return nextPlayer;
		}
	}
	
	private synchronized void sendNextPlayer() {
		ArrayList<Object> data = new ArrayList<Object>();
		ArrayList<Object> gameData = new ArrayList<Object>();
		gameData.add(nextPlayer);
		data.add(new GameData(GameDataType.CURRENT_NEXT_PLAYER, gameData ));
		server.broadcastServerOutputPackage(new ServerOutputPackage(ServerCommands.GAME_DATA, data));
	}
	
	private synchronized UnoCard drawCard() {
		if(server == null) {
			// Clients can't interact with cardDeck (server only)
			return null;
		} else {
			if(cardDeck.isEmpty()) {
				refillDeck();
			}
			return cardDeck.drawCard();
		}
	}
	
	/**
	 * @return a 7 card hand
	 * Server Only
	 */
	private ArrayList<UnoCard> drawHand() {
		ArrayList<UnoCard> hand = new ArrayList<UnoCard>();
		for(int i = 0; i < 7; i++) {
			hand.add(drawCard());
		}
		return hand;
	}
	
	/**
	 * Server Only
	 */
	private void refillDeck() {
		cardDeck.fill(new CardFactory<UnoCard>().createDeck(GameModes.UNO));
	}
	
	private synchronized Pair<Client, ArrayList<UnoCard>> getPlayerPair(Client wantedPairClient) {
		for(Pair<Client, ArrayList<UnoCard>> pair : playerList) {
			if(wantedPairClient.equals(pair.getClient())) return pair;
		}
		Pair<Client, ArrayList<UnoCard>> newPlayer = new Pair<Client, ArrayList<UnoCard>>(wantedPairClient, drawHand());
		playerList.add(newPlayer);
		return newPlayer;
	}
	
	private synchronized UnoCard getCardFromHand(ArrayList<UnoCard> hand, UnoCard card) {
		for(UnoCard c : hand) {
			if(c.equals(card)) return c;
		}
		return null;
	}
	/**
	 * draw card(s) depending on current stack count
	 */
	public synchronized void drawCards() {
		if(server == null) {
			if(clientServerConnector.getClient().equals(getCurrentPlayer())) {
				ArrayList<Object> dataContianer = new ArrayList<Object>();
				dataContianer.add(new ClientGameAction(clientServerConnector.getClient(), GameActionType.DRAW_CARD, null));
				clientServerConnector.sendClientOutputPackage(new ClientOutputPackage(ClientCommands.GAME_ACTION, dataContianer));
			}
		} else {
			// server can't draw cards ???? LUL KEKW
		}
	}
	
	public synchronized Pair<Client, ArrayList<UnoCard>> getPlayerHand() {
		return playerHand;
	}

	public synchronized UnoCard getSelectedCard() {
		return selectedCard;
	}

	public synchronized ClientServerConnector getClientServerConnector() {
		return clientServerConnector;
	}

	public synchronized ArrayList<UnoCard> getPlacedCards() {
		return placedCards;
	}

	public synchronized LinkedList<Pair<Client, ArrayList<UnoCard>>> getPlayerList() {
		return playerList;
	}

	public synchronized Client getCurrentPlayer() {
		return currentPlayer;
	}

	public synchronized UnoCard getCurrentCard() {
		return currentCard;
	}

	public synchronized int getDrawCardStackNumber() {
		return drawCardStackNumber;
	}

	public synchronized boolean isHasDrawn() {
		return hasDrawn;
	}

	public synchronized boolean isReverse() {
		return isReverse;
	}
	
	public synchronized void setSelectedCard(UnoCard selectedCard) {
		this.selectedCard = selectedCard;
	}
	
	private void setCurrentPlayer(Client client) {
		currentPlayer = client;
	}
	
	private synchronized void setPlayerHand(Pair<Client, ArrayList<UnoCard>> playerHand) {
		this.playerHand = playerHand;
	}
	
	private synchronized void removeCardFromClient(UnoCard unoCard) {
		playerHand.getHand().remove(unoCard);
	}
	
	private synchronized void setNextPlayer(Client nextPlayer) {
		this.nextPlayer = nextPlayer;
	}

	public synchronized void setPlacedCards(ArrayList<UnoCard> placedCards) {
		this.placedCards = placedCards;
	}

	public synchronized void setCurrentCard(UnoCard currentCard) {
		this.currentCard = currentCard;
	}

	public synchronized void setDrawCardStackNumber(int drawCardStackNumber) {
		this.drawCardStackNumber = drawCardStackNumber;
	}

	public synchronized void setHasDrawn(boolean hasDrawn) {
		this.hasDrawn = hasDrawn;
	}

	public synchronized void setReverse(boolean isReverse) {
		this.isReverse = isReverse;
	}
	
	@Override
	public String toString() {
		return "Game [CurrentCard="+currentCard.toString()+"]";
	}

	@Override
	public GameData createSynchData(Client c) {
		if(c.getRole().equals(ClientRole.PLAYER)) {
			ArrayList<Object> gameData = new ArrayList<Object>();
			
			gameData.add(currentCard);
			gameData.add(placedCards);
			gameData.add(currentPlayer);
			gameData.add(Boolean.valueOf(isReverse));
			gameData.add(Boolean.valueOf(hasDrawn));
			gameData.add(Integer.valueOf(drawCardStackNumber));
			gameData.add(getPlayerPair(c));
			gameData.add(getNextPlayer());
			return new GameData(GameDataType.SYNCH_PLAYER_TO_SERVER, gameData);
		}
		if(c.getRole().equals(ClientRole.SPECTATOR)) {
			ArrayList<Object> gameData = new ArrayList<Object>();
			
			gameData.add(currentCard);
			gameData.add(placedCards);
			gameData.add(currentPlayer);
			gameData.add(Boolean.valueOf(isReverse));
			gameData.add(Boolean.valueOf(hasDrawn));
			gameData.add(Integer.valueOf(drawCardStackNumber));
			gameData.add(getNextPlayer());
			
			return new GameData(GameDataType.SYNCH_SPECTATOR_TO_SERVER, gameData);
		}
		return null;
	}

	@Override
	public boolean hasPlayer(Client client) {
		for(Pair<Client, ArrayList<UnoCard>> pair : playerList) {
			if(pair.getClient().equals(client)) return true;
		}
		return false;
	}
	
}
