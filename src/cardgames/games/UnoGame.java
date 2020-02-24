package cardgames.games;

import java.util.ArrayList;
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
 * @author Luca-
 *
 */
public class UnoGame extends Game {
	// only one of these can be !=null
	private Server server;									// server
	private ClientServerConnector clientServerConnector; 	// local server connection
	private Pair<Client, ArrayList<UnoCard>> playerHand; 	// local player hand
	private UnoCard selectedCard;							// local selected card

	private CardDeck<UnoCard> cardDeck;						// server cards
	private ArrayList<Pair<Client, ArrayList<UnoCard>>> playerArrayList;// server list
	
	private ArrayList<UnoCard> placedCards;					// server/local placed cards
	private Client currentClient;							// server
	private UnoCard currentCard;							// server
	
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
	 * @param s
	 */
	public UnoGame(Server s) {
		this.server = s;
		currentCard = drawCard();
		
		if(currentCard.getColor() == CardGamesConstants.COLOR_WILD) {
			Random r = new Random();
			currentCard.setColor(r.nextInt(3));
		}
		
		placedCards = new ArrayList<UnoCard>();
		playerArrayList = new ArrayList<Pair<Client,ArrayList<UnoCard>>>();
		for(Client c : s.getClients(ClientRole.PLAYER)) {
			playerArrayList.add(new Pair<Client, ArrayList<UnoCard>>(c, drawHand()));
		}
		setCurrentClient(playerArrayList.get(0).getClient());
		// broadcast all relevant data once
		ArrayList<Object> data = new ArrayList<Object>();
		ArrayList<Object> gameData = new ArrayList<Object>();

		gameData.add(currentCard);
		gameData.add(placedCards);
		gameData.add(Boolean.valueOf(isReverse));
		gameData.add(Boolean.valueOf(hasDrawn));
		gameData.add(Integer.valueOf(drawCardStackNumber));

		data.add(new GameData(GameDataType.SYNCH_TO_SERVER, gameData));
		server.broadcastServerOutputPackage(new ServerOutputPackage(ServerCommands.GAME_DATA, data));
	}

	/**
	 * @param csc
	 */
	public UnoGame(ClientServerConnector csc) {
		this.clientServerConnector = csc;
		// starts empty then ask for game data (game data gets sent here so needs to be an instance)
	}
	
	public synchronized void processClientGameAction(ClientGameAction gat) {
		switch (gat.getGameActionType()) {
		case DRAW_CARD:
			// draws n cards -> send n cards to player
			ArrayList<UnoCard> cards = new ArrayList<UnoCard>();
			for(int i = 0; i > drawCardStackNumber; drawCardStackNumber--) {
				cards.add(drawCard());
			}
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(cards);
			server.sendServerOutputPackage(new ServerOutputPackage(ServerCommands.GAME_DATA, data), gat.getOriginClient());
			
			// update all clients draw card stack number
			ArrayList<Object> data1 = new ArrayList<Object>();
			data1.add(Integer.valueOf(drawCardStackNumber));
			server.broadcastServerOutputPackage(new ServerOutputPackage(ServerCommands.GAME_DATA, data1));
			break;
		case PLACE_CARD:
			// places selected card
			UnoCard card = (UnoCard) gat.getData().get(0); 						// client side card
			ArrayList<UnoCard> hand = getPair(gat.getOriginClient()).getHand(); // transform to server side card
			card = getCardFromHand(hand, card); 								// server side card
			int color = (((Integer) gat.getData().get(1)).intValue());			// take data
			placeCard(card, color);												// call server side placement
			hand.remove(card);
			// broadcast PLACED_CARD to each client other than origin
			ArrayList<Object> gameDataContainer = new ArrayList<Object>();
			ArrayList<Object> gameData = new ArrayList<Object>();
			gameData.add(card);
			gameData.add(currentClient);
			gameData.add(Integer.valueOf(drawCardStackNumber));
			gameData.add(Boolean.valueOf(isReverse));
			// pack it
			gameDataContainer.add(new GameData(GameDataType.PLACED_CARD, gameData));
			ServerOutputPackage sop = new ServerOutputPackage(ServerCommands.GAME_DATA, gameDataContainer );
			// send it
			server.broadcastServerOutputPackage(sop, gat.getOriginClient());
			break;
		case REQ_PAIR:
			// asks for his own hand
			for(Pair<Client, ArrayList<UnoCard>> c : playerArrayList) {
				if(c.getClient().equals(gat.getOriginClient())) {
					ArrayList<Object> data2 = new ArrayList<Object>();
					ArrayList<Object> gameData1 = new ArrayList<Object>();
					gameData1.add(c);
					data2.add(new GameData(GameDataType.REQ_PAIR, gameData1));
					server.sendServerOutputPackage(new ServerOutputPackage(ServerCommands.GAME_DATA, data2), c.getClient());
				}
			}
			break;
		case REQ_SYNCH:
			ArrayList<Object> data2 = new ArrayList<Object>();
			ArrayList<Object> gameData2 = new ArrayList<Object>();

			gameData2.add(currentCard);
			gameData2.add(placedCards);
			gameData2.add(Boolean.valueOf(isReverse));
			gameData2.add(Boolean.valueOf(hasDrawn));
			gameData2.add(Integer.valueOf(drawCardStackNumber));

			data2.add(new GameData(GameDataType.SYNCH_TO_SERVER, gameData2));
			server.broadcastServerOutputPackage(new ServerOutputPackage(ServerCommands.GAME_DATA, data2));
			break;
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
		case SYNCH_TO_SERVER:
			currentCard = (UnoCard) gd.getData().get(0);
			placedCards = (ArrayList<UnoCard>) gd.getData().get(1);
			currentClient = (Client) gd.getData().get(2);
			isReverse = ((Boolean) gd.getData().get(3)).booleanValue();
			hasDrawn = ((Boolean) gd.getData().get(4)).booleanValue();
			drawCardStackNumber = ((Integer) gd.getData().get(5)).intValue();
			break;
		case CARDS_DRAWN:
			// cards to be added to client hand
			playerHand.getHand().addAll((ArrayList<UnoCard>) gd.getData().get(0));
			// drawCardStackNumber will be broadcasted from server
			break;
		case CARD_LIST:
			this.placedCards = (ArrayList<UnoCard>) gd.getData().get(0);
			break;
		case CURRENT_CARD:
			currentCard = (UnoCard) gd.getData().get(0);
			break;
		case CURRENT_HAS_DRAWN:
			hasDrawn = ((Boolean) gd.getData().get(0)).booleanValue();
			break;
		case CURRENT_PLAYER:
			currentClient = (Client) gd.getData().get(0);
			break;
		case CURRENT_REVERSE:
			isReverse = ((Boolean) gd.getData().get(0)).booleanValue();
			break;
		case CURRENT_STACK_NUMBER:
			drawCardStackNumber = ((Integer) gd.getData().get(0)).intValue();
			break;
		case PLACED_CARD:
			placedCards.add(currentCard);
			currentCard = (UnoCard) gd.getData().get(0);
			currentClient = (Client) gd.getData().get(1);
			drawCardStackNumber = ((Integer) gd.getData().get(2)).intValue();
			isReverse = ((Boolean) gd.getData().get(3)).booleanValue();
			break;
		case REQ_PAIR:
			this.setPlayerHand((Pair<Client, ArrayList<UnoCard>>) gd.getData().get(0));
			break;
		default:
			break;
		}
	}
	
	/**
	 * @param card
	 * @param color
	 * @return returns true on successful placement
	 */
	public boolean placeCard(UnoCard card, int color) {
		if(!isPlacable(card)) return false;
		if(server == null) { // if its a client instance
			// send server a place card command
			ArrayList<Object> data = new ArrayList<Object>();
			ArrayList<Object> data1 = new ArrayList<Object>();
			data1.add(card);
			data1.add(Integer.valueOf(color));
			data.add(new ClientGameAction(getClientServerConnector().getClient(), GameActionType.PLACE_CARD, data1));
			clientServerConnector.sendClientOutputPackage(new ClientOutputPackage(ClientCommands.GAME_ACTION, data));
			// do stuff locally for quick response
			playerHand.getHand().remove(card);
			innerPlaceCard(card, color);
		} else { // if it's on the server
			innerPlaceCard(card, color); // do it for the server as well
			// exit and send it to other clients
		}
		return true;
	}
	
	public void innerPlaceCard(UnoCard card, int color) {
		placedCards.add(currentCard);
		card.setColor(color);
		
		if(placedCards.size() > 5) {
			placedCards.remove(0);
		}
		
		currentCard = null;
		currentCard = card;
		
		
		if(currentCard.getCardId() == CardGamesConstants.CARD_REVERSE) {
			isReverse = !isReverse;
		}
		if(currentCard.getCardId() == CardGamesConstants.CARD_SKIP) {
			currentClient = getNextClient();
		}
		if(currentCard.getCardId() == CardGamesConstants.CARD_DRAW_TWO) {
			drawCardStackNumber += 2;
		}
		if(currentCard.getCardId() == CardGamesConstants.CARD_DRAW_FOUR) {
			drawCardStackNumber += 4;
		}
		
		currentClient = getNextClient();
		hasDrawn = false;
	}
	
	// TODO add rule sets / flags
	private boolean isPlacable(UnoCard uCard) {
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
		if(uCard.getCardId() == CardGamesConstants.CARD_WILD && // If it's a draw two card and there is a draw four on the field || Might not be in the real game
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
	@SuppressWarnings("unused")
	private boolean canPlaceCard(Client player) {
		for(UnoCard u : getPair(player).getHand()) {
			if(isPlacable(u)) return true;
		}
		return false;
	}
	
	private Client getNextClient() {
		boolean sendNext = false;
		Pair<Client, ArrayList<UnoCard>> previousPair = null;
		for(Pair<Client, ArrayList<UnoCard>> pair : playerArrayList) {
			if(sendNext == true && isReverse == true) return previousPair.getClient();
			if(sendNext == true) return pair.getClient();
			if(pair.getClient().equals(currentClient)) {
				sendNext = true;
				continue;
			}
			previousPair = pair;
			
		}
		return null;
	}

	public synchronized UnoCard drawCard() {
		if(server == null) {
			return null; // return data received from server
		} else {
			if(cardDeck.isEmpty()) {
				refillDeck();
			}
			return cardDeck.drawCard();
		}
	}
	
	/**
	 * @return
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
	
	private synchronized Pair<Client, ArrayList<UnoCard>> getPair(Client wantedPairClient) {
		for(Pair<Client, ArrayList<UnoCard>> pair : playerArrayList) {
			if(wantedPairClient.equals(pair.getClient())) return pair;
		}
		return null;
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
			ArrayList<Object> data = new ArrayList<Object>();
			ArrayList<Object> gameData = new ArrayList<Object>();
			data.add(new ClientGameAction(clientServerConnector.getClient(), GameActionType.DRAW_CARD, gameData));
			ClientOutputPackage cop = new ClientOutputPackage(ClientCommands.GAME_ACTION, data);
			clientServerConnector.sendClientOutputPackage(cop);
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

	public synchronized ArrayList<Pair<Client, ArrayList<UnoCard>>> getPlayerArrayList() {
		return playerArrayList;
	}

	public synchronized Client getCurrentClient() {
		return currentClient;
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
	
	private void setCurrentClient(Client client) {
		currentClient = client;
	}
	
	private synchronized void setPlayerHand(Pair<Client, ArrayList<UnoCard>> playerHand) {
		this.playerHand = playerHand;
	}
	
}
