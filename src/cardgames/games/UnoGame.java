package cardgames.games;

import java.util.ArrayList;

import cardgames.client.Client;
import cardgames.client.ClientServerConnector;
import cardgames.client.GameModes;
import cardgames.games.parts.CardDeck;
import cardgames.games.parts.CardFactory;
import cardgames.games.parts.Pair;
import cardgames.games.parts.UnoCard;
import cardgames.server.Server;
import cardgames.server.ServerCommands;
import cardgames.server.ServerOutputPackage;

public class UnoGame extends Game {
	// only one of these can be !=null
	private Server server;
	private ClientServerConnector clientServerConnector;
	private Pair<Client, ArrayList<UnoCard>> playerHand;
	
	// server side data
	private CardDeck<UnoCard> cardDeck;
	private ArrayList<UnoCard> placedCards;
	
	private ArrayList<Pair<Client, ArrayList<UnoCard>>> playerArrayList;
	private Client currentClient;
	private UnoCard currentCard;
	
	/**
	 * How many cards does the player need to draw
	 */
	private int drawCardStackNumber = 0;
	/**
	 * if drawCardStackNumber is >0 player needs to place a card (stack +2/+4) or draw n cards
	 */
	private boolean hasDrawn = false;
	private boolean isReverse = false;
	
	// server side game 
	/**
	 * players don't see the stack
	 * game logic is done on server
	 * selection is done locally
	 * current card is sent from server as gameData
	 * placedCards is calculated locally
	 * @param s
	 */
	public UnoGame(Server s) {
		this.server = s;
		placedCards = new ArrayList<UnoCard>();
		playerArrayList = new ArrayList<Pair<Client,ArrayList<UnoCard>>>();
		for(Client c : s.getClients()) {
			playerArrayList.add(new Pair<Client, ArrayList<UnoCard>>(c, drawHand()));
		}
		setCurrentClient(playerArrayList.get(0).getClient());
	}
	
	// client side game
	/**
	 * Client sided game explained <br>
	 * 1. client acts (places card etc.) <br>
	 * 2. client sends action to server <br>
	 * @param csc
	 */
	public UnoGame(ClientServerConnector csc) {
		this.clientServerConnector = csc;
		// starts empty then ask for game data (game data gets sent here so needs to be an instance)
	}
	
	/**
	 * 1. client sends action (and does it locally)
	 * 2. connection forwards it here
	 * 3. server processes it
	 * 4. server broadcasts Data to others if needed
	 * 5. they process it
	 */
	public synchronized void processClientGameAction(ClientGameAction gat) {
		switch (gat.getGameActionType()) {
		case DRAW_CARD:
			// draws n cards -> send n cards to player
			break;
		case PLACE_CARD:
			// places selected card
			break;
		case SET_CARD_COLOR:
			// sets the color placed wild card
			break;
		case REQ_PAIR:
			// asks for his own hand
			for(Pair<Client, ArrayList<UnoCard>> c : playerArrayList) {
				if(c.getClient().equals(gat.getOriginClient())) {
					ArrayList<Object> data = new ArrayList<Object>();
					ArrayList<Object> gameData = new ArrayList<Object>();
					gameData.add(c);
					data.add(new GameData(GameDataType.REQ_PAIR, gameData));
					server.sendServerOutputPackage(new ServerOutputPackage(ServerCommands.GAME_DATA, gameData), c.getClient());
				}
			}
		default:
			return;
		}
	}
	
	/**
	 * client side processing
	 */
	public synchronized void processServerData(GameData gd) {
		switch (gd.getGdt()) {
		case CARDS_DRAWN:
			// 
			break;
		case CARD_LIST:
			break;
		case CURRENT_CARD:
			break;
		case CURRENT_HAS_DRAWN:
			break;
		case CURRENT_PLAYER:
			break;
		case CURRENT_REVERSE:
			break;
		case CURRENT_STACK_NUMBER:
			break;
		case PLACED_CARD:
			break;
		case REQ_PAIR:
			break;
		default:
			break;
		}
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

	public Pair<Client, ArrayList<UnoCard>> getPlayerHand() {
		return playerHand;
	}

	public void setPlayerHand(Pair<Client, ArrayList<UnoCard>> playerHand) {
		this.playerHand = playerHand;
	}

	public ClientServerConnector getClientServerConnector() {
		return clientServerConnector;
	}

	public ArrayList<UnoCard> getPlacedCards() {
		return placedCards;
	}

	public Client getCurrentClient() {
		return currentClient;
	}

	public void setCurrentClient(Client currentClient) {
		this.currentClient = currentClient;
	}

	public UnoCard getCurrentCard() {
		return currentCard;
	}

	public void setCurrentCard(UnoCard currentCard) {
		this.currentCard = currentCard;
	}

	public int getDrawCardStackNumber() {
		return drawCardStackNumber;
	}

	public boolean isHasDrawn() {
		return hasDrawn;
	}

	public boolean isReverse() {
		return isReverse;
	}
	
}
