package uno;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import core.CardDeck;
import core.Client;
import core.GameModes;
import core.Player;
import core.PlayerPair;
import core.Server;
import core.Server.ServerCommands;


public class UnoPlayingField extends PlayingField {
	/**
	 * Playing field for UNO
	 */
	private static final long serialVersionUID = -3446064696734256037L;
	// property flags
	public static final String PLACED_CARD_FLAG = "pcf";
	public static final String CHANGE_FLAG = "change";
	
	public GameModes gameMode = GameModes.UNO;
	
	private LinkedList<PlayerPair<UnoPlayerHand>> players = new LinkedList<>();
	private Player player;
	private CardDeck<UnoCard> cardDeck;
	private ArrayList<UnoCard> placedCards = new ArrayList<UnoCard>();
	public PropertyChangeSupport propertyChangeSupport;
	
	private UnoCard currentCard = null;
	private Player currentPlayer = null;
	
	private int drawCardStackNumber = 0;
	
	private boolean isReverse = false;
	private boolean hasDrawn = false;
	
	public UnoPlayingField(Player client) {
		this.setPlayer(client);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		
		if(client.isClient) {
			currentCard = getCurrentCard();
			currentPlayer = getCurrentPlayer();
			
			addPlayer(client);
		} else {
			Server.server.setPlayingField(this);
			cardDeck = new CardDeck<UnoCard>(UnoCard.createDeck());
			
			addPlayer(client);
			
			currentCard = drawCard();
			if(currentCard.getColor() == UnoCard.COLOR_WILD) {
				Random r = new Random();
				currentCard.setColor(r.nextInt(3));
			}
			currentPlayer = client;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public LinkedList<PlayerPair<UnoPlayerHand>> getPlayerList() {
		if(player.isClient) {
			return (LinkedList<PlayerPair<UnoPlayerHand>>) Client.clientObject.createServerRequest(ServerCommands.getPlayerList, null).data.get(0);
		} else {
			return players;
		}
	}
	
	public UnoCard getCurrentCard() {
		if(player.isClient) {
			UnoCard uc = (UnoCard) Client.clientObject.createServerRequest(ServerCommands.getCurrentCard, null).data.get(0);
			return uc;
		} else {
			return currentCard;
		}
	}
	
	public Player getCurrentPlayer() {
		if(player.isClient) {
			return (Player) Client.clientObject.createServerRequest(ServerCommands.getCurrentPlayer, null).data.get(0);
		} else {
			return currentPlayer;
		}
	}
	
	public UnoPlayerHand getPlayerHand(Player p) {
		if(player.isClient) {
			LinkedList<Object> data = new LinkedList<Object>();
			data.add(p);
			return (UnoPlayerHand) Client.clientObject.createServerRequest(ServerCommands.getPlayerHand, data).data.get(0);
		} else {
			UnoPlayerHand hand = null;
			for(PlayerPair<UnoPlayerHand> pair : players) {
				if (pair.getPlayer().equals(p)) {
					hand = pair.getPairItem();
				}
			}
			return hand; 
		}
	}
	
	public UnoCard getSelectedCard(Player clientPlayer) {
		if(player.isClient) {
			LinkedList<Object> data = new LinkedList<Object>();
			data.add(clientPlayer);
			return (UnoCard) Client.clientObject.createServerRequest(ServerCommands.getSelectedCard, data).data.get(0);
		} else {
			UnoCard uCard = getPlayerHand(clientPlayer).getSelectedCard(); 
			return uCard;
		}
	}
	
	public UnoCard drawCard() {
		if(player.isClient) {
			return (UnoCard) Client.clientObject.createServerRequest(ServerCommands.drawCard, null).data.get(0);
		} else {
			if(cardDeck.isEmpty()) {
				refillDeck();
			}
			return cardDeck.drawCard();
		}
	}
	/**
	 * Draws cards and adds them to player's hand
	 * @param source
	 */
	public void drawCard(Player source) {
		if (player.isClient) {
			LinkedList<Object> data = new LinkedList<Object>();
			data.add(source);
			Client.clientObject.createServerRequest(ServerCommands.drawCardForPlayer, data);
		} else {
			if(hasDrawn) {
				currentPlayer = getNextPlayer(source);
				hasDrawn = false;
				return;
			}
			if(drawCardStackNumber != 0) {
				while (drawCardStackNumber > 0) {
					getPlayerHand(source).getPlayerCards().add(drawCard());
					drawCardStackNumber--;
				}
			} else {
				getPlayerHand(source).getPlayerCards().add(drawCard());
			}
			hasDrawn = true;
			if(!canPlaceCard(source)) {
				currentPlayer = getNextPlayer(source);
				hasDrawn=false;
			}
		}
		
	}
	/**
	 * returns true if player can place any card
	 * @param player
	 * @return canPlaceCard
	 */
	private boolean canPlaceCard(Player player) {
		for(UnoCard u : getPlayerHand(player).getPlayerCards()) {
			if(isPlacable(u)) return true;
		}
		if(isPlacable(getPlayerHand(player).getSelectedCard())) {
			return true;
		}
		return false;
	}

	/**
	 * Fills the deck with a new full set of UNO cards
	 */
	private void refillDeck() {
		cardDeck.fill(UnoCard.createDeck());
	}

	/**
	 * Plays a card
	 * @param uCard - uCard (user card), the card you want to play, usually only a copy of the selected card
	 * @param sender - the player that sent the request to have his card played
	 * @return returns true if card was placed successfully
	 */
	public boolean placeCard(UnoCard uCard, Player sender) {
		if(!isPlacable(uCard)) return false;
		if(sender.isClient) { // Send card to place card of server
			LinkedList<Object> data = new LinkedList<Object>();
			data.add(uCard);
			data.add(sender);
			Client.clientObject.createServerRequest(ServerCommands.placeCard, data);
			propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, CHANGE_FLAG, false, true));
		} else {
			placedCards.add(currentCard);
			
			if(placedCards.size() > 5) {
				placedCards.remove(0);
			}
			
			currentCard = null;
			currentCard = getPlayerHand(sender).playSelectedCard();
			
			if(currentCard.getCardId() == 11) {
				isReverse = !isReverse;
			}
			if(currentCard.getCardId() == 10) {
				currentPlayer = getNextPlayer(currentPlayer);
			}
			if(currentCard.getCardId() == 13) {
				drawCardStackNumber += 2;
			}
			if(currentCard.getCardId() == 14) {
				drawCardStackNumber += 4;
			}
			
			currentPlayer = getNextPlayer(currentPlayer);

			propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, PLACED_CARD_FLAG, false, true));
			//Server.server.BroadcastToClients(new ServerResponse(ClientCommands.updateCards, null));
		}
		hasDrawn = false;
		return true;
	}
	
	public void setCardColor(Player sender, int color) {
		if(player.isClient) { // Send card and color to server to change there
			LinkedList<Object> data = new LinkedList<Object>();
			data.add(sender);
			data.add(new Integer(color));
			Client.clientObject.createServerRequest(ServerCommands.setCardColor, data);
			propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, CHANGE_FLAG, false, true));
		} else {
			getSelectedCard(sender).setColor(color);
			
			propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, CHANGE_FLAG, false, true));
			//Server.server.BroadcastToClients(new ServerResponse(ClientCommands.change, null));
		}
	}
	
	public Player getNextPlayer(Player p) {
		Player nextPlayer = null;
		if(player.isClient) {
			LinkedList<Object> data = new LinkedList<Object>();
			data.add(p);
			nextPlayer = (Player) Client.clientObject.createServerRequest(ServerCommands.getNextPlayer, data).data.get(0);
		} else {
			if(isReverse) {
				for(ListIterator<PlayerPair<UnoPlayerHand>> iterator = players.listIterator(0); iterator.hasNext();) {
					if (p.equals(iterator.next().getPlayer())) {
						if(iterator.hasPrevious()) {
							nextPlayer = iterator.previous().getPlayer();
						} else {
							nextPlayer = players.getLast().getPlayer();
						}
						return nextPlayer;
					}
				}
			} else {
				for(Iterator<PlayerPair<UnoPlayerHand>> iterator = players.iterator(); iterator.hasNext(); ) {
					if (p.equals(iterator.next().getPlayer())) {
						if(iterator.hasNext()) {
							nextPlayer = iterator.next().getPlayer();
						} else {
							nextPlayer = players.getFirst().getPlayer();
						}
						return nextPlayer;
					}
				}
			}
		}
		return nextPlayer;
	}

	// TODO add rule sets / flags
	private boolean isPlacable(UnoCard uCard) {
		UnoCard currentCard = getCurrentCard();
		if(uCard == null) return false;
		
		if(uCard.getCardId() == UnoCard.CARD_DRAW_FOUR) return true; // Always play +4 cards
		if(uCard.getCardId() == UnoCard.CARD_DRAW_TWO && // If it's a draw two card and there is a draw four on the field || Might not be in the real game
				(currentCard.getCardId() == UnoCard.CARD_DRAW_FOUR 
				|| (currentCard.getCardId() == UnoCard.CARD_DRAW_TWO) ) ) {
			return true;
		}
		if(uCard.getColor() == currentCard.getColor() && drawCardStackNumber > 0 && !(uCard.getCardId() == UnoCard.CARD_DRAW_FOUR || uCard.getCardId() == UnoCard.CARD_DRAW_TWO) && (currentCard.getCardId() == UnoCard.CARD_DRAW_FOUR || currentCard.getCardId() == UnoCard.CARD_DRAW_TWO) ) {
			return false;
		}
		// if player plays wild and there is no +4 / +2 card
		if(uCard.getCardId() == UnoCard.CARD_WILD && // If it's a draw two card and there is a draw four on the field || Might not be in the real game
				!(currentCard.getCardId() == UnoCard.CARD_DRAW_FOUR 
				|| (currentCard.getCardId() == UnoCard.CARD_DRAW_TWO) ) ) {
			return true;
		}
		// General statement for other cases
		// if the number/symbol on the card is the same the card can be placed
		if(uCard.getCardId() == currentCard.getCardId()) {
			return true;
		}
		// if the colors are the same the card can be placed
		if(uCard.getColor() == currentCard.getColor()) {
			return true;
		}
		return false;
	}
	/**
	 * @return the array list of all currently placed cards
	 */
	@SuppressWarnings("unchecked")
	public synchronized ArrayList<UnoCard> getPlacedCards() {
		if(player.isClient) {
			return (ArrayList<UnoCard>) Client.clientObject.createServerRequest(ServerCommands.getPlacedCards, null).data.get(0);
		} else {
			return placedCards;
		}
	}
	
	/**
	 * @param p - player you'd like to add
	 */
	public synchronized void addPlayer(Player p) {
		if(player.isClient) {
			Client.clientObject.createServerRequest(ServerCommands.addPlayer, null);
		} else {
			PlayerPair<UnoPlayerHand> pair = getPlayerInfoPair(p);
			if(pair == null) {
				players.add(new PlayerPair<UnoPlayerHand>(p, new UnoPlayerHand(this)));
			}
		}
	}
	
	/**
	 * This should only be executed by the server
	 * @param p - player you wish to remove
	 */
	public synchronized void kick(Player p) {
		if(p.equals(currentPlayer)) currentPlayer = getNextPlayer(currentPlayer);
		players.remove(getPlayerInfoPair(p));
	}
	/**
	 * For synchronization
	 * @param p - player to match
	 * @return returns the matching PlayerInfoPair or null if there is no match
	 */
	private synchronized PlayerPair<UnoPlayerHand> getPlayerInfoPair(Player p) {
		for(PlayerPair<UnoPlayerHand> pair : players) {
			if (pair.getPlayer().equals(p)) {
				return pair;
			}
		}
		return null;
	}
	
	public synchronized Player getPlayer() {
		return player;
	}

	private synchronized void setPlayer(Player player) {
		this.player = player;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
}
