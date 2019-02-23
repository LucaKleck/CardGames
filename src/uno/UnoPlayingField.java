package uno;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import core.CardDeck;
import core.Player;


public class UnoPlayingField implements Serializable {
	/**
	 * Playing field for UNO
	 */
	private static final long serialVersionUID = -3446064696734256037L;
	
	public static final int PORT = 9876;
	
	private boolean isClient = false;
	// if isClient == true
	public InetAddress host = InetAddress.getLocalHost();
	
	// if isClient == false
	private static ExecutorService serverExecutor = Executors.newFixedThreadPool(1);
	private static ServerSocket server; // TODO change to lobby or something

	private PropertyChangeSupport propertyChangeSupport;
	private LinkedList< PlayerInfoPair > players = new LinkedList<>();
	private Player player;
	private CardDeck<UnoCard> cardDeck;
	private ArrayList<UnoCard> placedCards = new ArrayList<UnoCard>();
	private UnoCard currentCard = null;
	private Player currentPlayer = null;
	private Boolean placedCardFlag = false;
	private int drawCardStackNumber = 0;
	private boolean isReverse = false;
	private boolean hasDrawn = false;
	
	public UnoPlayingField(Player hostPlayer) throws IOException, ClassNotFoundException {
		this.setPlayer(hostPlayer);
		cardDeck = new CardDeck<UnoCard>(UnoCard.createDeck());
		
		server = new ServerSocket(PORT);
		
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		
		addPlayer(hostPlayer);
		
		currentCard = drawCard();
		if(currentCard.getColor() == UnoCard.COLOR_WILD) {
			Random r = new Random();
			currentCard.setColor(r.nextInt(3));
		}
		currentPlayer = hostPlayer;
		
		serverExecutor.execute(new ServerClientCommandHandler());
	}
	
	public UnoPlayingField(Player clientPlayer, InetAddress hostIP) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {
		this.setPlayer(clientPlayer);
		this.host = hostIP;
		System.out.println(hostIP.toString());
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		
		this.isClient=true;

		Socket clientSocket = null;
		ObjectOutputStream clientOos = null;
		ObjectInputStream clientOis = null;
		
		clientSocket = new Socket(host.getHostName(), PORT);
		
		clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
		clientOis = new ObjectInputStream(clientSocket.getInputStream());
		
		clientOos.writeObject(ClientCommands.join);
		clientOos.writeObject(clientPlayer);
		
		Player p = (Player) clientOis.readObject();
		
		if (p.getPlayerName().matches(clientPlayer.getPlayerName())) {
		} else {
			System.exit(0);
		}
		
		clientOos.flush();
		
		clientOis.close();
		clientOos.close();
		clientSocket.close();
		
		
		currentCard = getCurrentCard();
		currentPlayer = getCurrentPlayer();
		
	}
	
	public UnoCard getCurrentCard() {
		if(isClient) {
			UnoCard serverCard = null;
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				ObjectInputStream clientOis = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOis = new ObjectInputStream(clientSocket.getInputStream());
				
				clientOos.writeObject(ClientCommands.getCurrentCard);
				serverCard = (UnoCard) clientOis.readObject();
				
				clientOos.flush();
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
				
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			return serverCard;
		} else {
			return currentCard;
		}
	}
	
	public Player getCurrentPlayer() {
		if(isClient) {
			Player serverPlayer = null;
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				ObjectInputStream clientOis = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOis = new ObjectInputStream(clientSocket.getInputStream());
				
				clientOos.writeObject(ClientCommands.getCurrentPlayer);
				serverPlayer = (Player) clientOis.readObject();
				
				clientOos.flush();
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
				
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			return serverPlayer;
		} else {
			return currentPlayer;
		}
	}
	
	public PlayerHand getPlayerHand(Player p) {
		PlayerHand hand = null;
		if(isClient) {
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				ObjectInputStream clientOis = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOis = new ObjectInputStream(clientSocket.getInputStream());
				
				clientOos.writeObject(ClientCommands.getPlayerHand);
				clientOos.writeObject(p);
				
				hand = (PlayerHand) clientOis.readObject();
				
				clientOos.flush();
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
				
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			return hand;
		} else {
			for(PlayerInfoPair pair : players) {
				if (pair.getPlayer().equals(p)) {
					hand = pair.getHand();
				}
			}
			return hand; 
		}
	}
	
	public UnoCard getSelectedCard(Player clientPlayer) {
		if(isClient) {
			UnoCard uCard = null;
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				ObjectInputStream clientOis = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOis = new ObjectInputStream(clientSocket.getInputStream());
				
				clientOos.writeObject(ClientCommands.getSelectedCard);
				clientOos.writeObject(clientPlayer);
				uCard = (UnoCard) clientOis.readObject();
				
				clientOos.flush();
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
				
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			return uCard;
		} else {
			UnoCard uCard = getPlayerHand(clientPlayer).getSelectedCard(); 
			return uCard;
		}
	}
	
	public synchronized boolean isClient() {
		return isClient;
	}

	public UnoCard drawCard() {
		if(isClient) {
			UnoCard unoCard = null;
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				ObjectInputStream clientOis = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOis = new ObjectInputStream(clientSocket.getInputStream());
				
				clientOos.writeObject(ClientCommands.drawCard);
				unoCard = (UnoCard) clientOis.readObject();
				
				clientOos.flush();
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
				
			} catch (IOException | ClassNotFoundException e) {
			}
			return unoCard;
		} else {
			if(cardDeck.isEmpty()) {
				refillDeck();
			}
			return cardDeck.drawCard();
		}
	}
	/**
	 * Draws cards and adds them to player's hand
	 * @param player
	 */
	public void drawCard(Player player) {
		if (isClient) {
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				ObjectInputStream clientOis = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOis = new ObjectInputStream(clientSocket.getInputStream());
				
				clientOos.writeObject(ClientCommands.drawCardForPlayer);
				clientOos.writeObject(player);
				
				clientOos.flush();
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
				
			} catch (IOException e) {
			}
		} else {
			if(hasDrawn) {
				currentPlayer = getNextPlayer(player);
				hasDrawn = false;
				return;
			}
			if(drawCardStackNumber != 0) {
				while (drawCardStackNumber > 0) {
					getPlayerHand(player).getPlayerCards().add(drawCard());
					drawCardStackNumber--;
				}
			} else {
				getPlayerHand(player).getPlayerCards().add(drawCard());
			}
			hasDrawn = true;
			if(!canPlaceCard(player)) {
				currentPlayer = getNextPlayer(player);
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
		Boolean oldPlacedCardFlag = new Boolean(placedCardFlag);
		placedCardFlag  = true;
		if(isClient) { // Send card to place card of server
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				ObjectInputStream clientOis = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOis = new ObjectInputStream(clientSocket.getInputStream());
				
				clientOos.writeObject(ClientCommands.placeCard);
				clientOos.writeObject(uCard);
				clientOos.writeObject(sender);
				
				clientOos.flush();
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
				propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, "PlacedCardFlag", oldPlacedCardFlag, placedCardFlag));
			} catch (IOException e) {
			}
		} else {
			placedCards.add(currentCard);
			
			currentCard = null;
			currentCard = getPlayerHand(sender).playSelectedCard();
			
			if(currentCard.getCardId() == 11) {
				isReverse = !isReverse;
			}
			if(currentCard.getCardId() == 10) {
				getNextPlayer(currentPlayer);
			}
			if(currentCard.getCardId() == 13) {
				drawCardStackNumber += 2;
			}
			if(currentCard.getCardId() == 14) {
				drawCardStackNumber += 4;
			}
			
			currentPlayer = getNextPlayer(currentPlayer);
			propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, "PlacedCardFlag", oldPlacedCardFlag, placedCardFlag));
		}
		return true;
	}
	
	public void setCardColor(Player sender, int color) {
		if(isClient) { // Send card and color to server to change there
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				
				clientOos.writeObject(ClientCommands.setCardColor);
				clientOos.writeObject(sender);
				clientOos.writeInt(color);
				
				clientOos.flush();
				
				clientOos.close();
				
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, "Change", false, true));
		} else {
			getSelectedCard(sender).setColor(color);
			propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, "Change", false, true));
		}
	}
	
	public Player getNextPlayer(Player p) {
		Player nextPlayer = null;
		if(isClient) {
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				ObjectInputStream clientOis = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOis = new ObjectInputStream(clientSocket.getInputStream());
				
				clientOos.writeObject(ClientCommands.getNextPlayer);
				clientOos.writeObject(p);
				
				nextPlayer = (Player) clientOis.readObject();
				
				clientOos.flush();
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
			} catch (IOException | ClassNotFoundException e) {
			}
		} else {
			if(isReverse) {
				for(ListIterator<PlayerInfoPair> iterator = players.listIterator(0); iterator.hasNext();) {
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
				for(Iterator<PlayerInfoPair> iterator = players.iterator(); iterator.hasNext(); ) {
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
	public ArrayList<UnoCard> getplacedUnoCards() {
		if(isClient) {
			ArrayList<UnoCard> pc = new ArrayList<UnoCard>();
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				ObjectInputStream clientOis = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOis = new ObjectInputStream(clientSocket.getInputStream());
				
				clientOos.writeObject(ClientCommands.getPlacedCards);
				pc = (ArrayList<UnoCard>) clientOis.readObject();
				
				clientOos.flush();
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
				
			} catch (IOException | ClassNotFoundException e) {
			}
			return pc;
		} else {
			return placedCards;
		}
	}
	
	/**
	 * This should only be executed by the server
	 * @param p - player you'd like to add
	 */
	public synchronized void addPlayer(Player p) {
		PlayerInfoPair pair = getPlayerInfoPair(p);
		if(pair == null) {
			players.add(new PlayerInfoPair(p, new PlayerHand(this)));
		}
	}
	
	/**
	 * This should only be executed by the server
	 * @param p - player you wish to remove
	 */
	public synchronized void removePlayer(Player p) {
		players.remove(getPlayerInfoPair(p));
	}
	/**
	 * For synchronization
	 * @param p - player to match
	 * @return returns the matching PlayerInfoPair or null if there is no match
	 */
	private synchronized PlayerInfoPair getPlayerInfoPair(Player p) {
		for(PlayerInfoPair pair : players) {
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

	private class ServerClientCommandHandler implements Runnable {
		private ServerClientCommandHandler() {
		}
		@Override
		public void run() {
			while(true) { // TODO change this to only listen when client request  comes in from lobby object 
				try {
					Socket socket  = server.accept();
					
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					// Instruction will be sent by client
					ClientCommands instruction = (ClientCommands) ois.readObject();
					
					// Server will try to match the instruction and respond with the desired object
					if(instruction == ClientCommands.join) {
						Player joinedPlayer = (Player) ois.readObject();
						addPlayer(joinedPlayer);
						System.out.println(joinedPlayer.toString()+" joined the game!");
						oos.writeObject(joinedPlayer);
					}
					if(instruction == ClientCommands.drawCard) {
						UnoCard drawnCard = drawCard();
						oos.writeObject(drawnCard);
					}
					if(instruction == ClientCommands.drawCardForPlayer) {
						drawCard((Player) ois.readObject());
					}
					if(instruction == ClientCommands.placeCard) {
						placeCard((UnoCard) ois.readObject(), (Player) ois.readObject());
					}
					// Server getter
					if(instruction == ClientCommands.getCurrentCard) {
						UnoCard serverCard = getCurrentCard();
						oos.writeObject(serverCard);
					}
					if(instruction == ClientCommands.getPlacedCards) {
						oos.writeObject(getplacedUnoCards());
					}
					if(instruction == ClientCommands.getCurrentPlayer) {
						Player serverPlayer = getCurrentPlayer();
						oos.writeObject(serverPlayer);
					}
					if(instruction == ClientCommands.getPlayerHand) {
						Player p = (Player) ois.readObject();
						PlayerHand serverPlayerHand = null;
						for(PlayerInfoPair pair : players) {
							if (pair.getPlayer().equals(p)) {
								serverPlayerHand = pair.getHand();
							}
						}
						oos.writeObject(serverPlayerHand);
					}
					if(instruction == ClientCommands.getSelectedCard) {
						Player p = (Player) ois.readObject();
						oos.writeObject(getSelectedCard(p));
					}
					if(instruction == ClientCommands.getNextPlayer) {
						Player p = getNextPlayer((Player) ois.readObject());
						oos.writeObject(p);
					}
					// Server setter
					if(instruction == ClientCommands.setPlayerHandSelectedCard) {
						Player sender = (Player) ois.readObject();
						UnoCard toBeSelectedUnoCard = (UnoCard) ois.readObject();
						PlayerHand serverPlayerHand = null;
						serverPlayerHand = getPlayerInfoPair(sender).getHand();
						serverPlayerHand.setSelectedCard(toBeSelectedUnoCard);
					}
					if(instruction == ClientCommands.setCardColor) {
						Player sender = (Player) ois.readObject();
						int color = ois.readInt();
						setCardColor(sender, color);
					}
					
					oos.flush();
					
					ois.close();
					oos.close();
					socket.close();
					
				} catch (IOException e) {
				} catch (ClassNotFoundException e) {
				}
				
			}
		}
		
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public enum ClientCommands {
		// setter
		setCardColor,
		setPlayerHandSelectedCard,
		
		// getter
		getSelectedCard,
		getPlayerHand,
		getCurrentPlayer,
		getPlacedCards,
		getCurrentCard,
		getNextPlayer,
		
		// other
		placeCard,
		drawCardForPlayer,
		drawCard,
		join;
	}

}
