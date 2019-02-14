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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.util.Pair;


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
	private static ServerSocket server;

	private PropertyChangeSupport propertyChangeSupport;
	private LinkedList< Pair<Player, PlayerHand> > players = new LinkedList<>();
	private Player player;
	private CardDeck<UnoCard> cardDeck;
	private ArrayList<UnoCard> placedCards = new ArrayList<UnoCard>();
	private UnoCard currentCard = null;
	private Player currentPlayer = null;
	private Boolean placedCardFlag = false;
	private int drawCardStackNumber = 0;
	private boolean isReverse = false;
	
	public UnoPlayingField(Player hostPlayer) throws IOException, ClassNotFoundException {
		this.setPlayer(hostPlayer);
		cardDeck = new CardDeck<UnoCard>(createDeck());
		
		server = new ServerSocket(PORT);
		
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		
		players.add(new Pair<Player, PlayerHand>(hostPlayer, new PlayerHand(this)));
		
		currentCard = drawCard();
		if(currentCard.getColor() == 4) {
			Random r = new Random();
			currentCard.setColor(r.nextInt(3), currentCard);
		}
		currentPlayer = hostPlayer;
		
		serverExecutor.execute(new ServerClass());
		waitForPlayerTwo();
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
		
		clientOos.writeObject("join");
		clientOos.writeObject(clientPlayer);
		
		Player p = (Player) clientOis.readObject();
		
		if (p.getPlayerName().matches(clientPlayer.getPlayerName())) {
		} else {
			System.exit(0);
		}
		
		
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
				
				clientOos.writeObject("getCurrentCard");
				serverCard = (UnoCard) clientOis.readObject();
				
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
				
				clientOos.writeObject("getCurrentPlayer");
				serverPlayer = (Player) clientOis.readObject();
				
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
				
				clientOos.writeObject("getPlayerHand");
				clientOos.writeObject(p);
				
				hand = (PlayerHand) clientOis.readObject();
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
				
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			return hand;
		} else {
			for(Pair<Player, PlayerHand> pair : players) {
				if (pair.getKey().equals(p)) {
					hand = pair.getValue();
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
				
				clientOos.writeObject("getSelectedCard");
				clientOos.writeObject(clientPlayer);
				uCard = (UnoCard) clientOis.readObject();
				
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
				
				clientOos.writeObject("drawCard");
				unoCard = (UnoCard) clientOis.readObject();
				
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
				
				clientOos.writeObject("drawCardForPlayer");
				clientOos.writeObject(player);
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
				
			} catch (IOException e) {
			}
		} else {
			if(drawCardStackNumber != 0) {
				while (drawCardStackNumber > 0) {
					getPlayerHand(player).getPlayerCards().add(drawCard());
					drawCardStackNumber--;
				}
			} else {
				getPlayerHand(player).getPlayerCards().add(drawCard());
			}
			if(!canPlaceCard(player)) {
				nextPlayer(currentPlayer);
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
		return false;
	}

	/**
	 * Fills the deck with a new full set of UNO cards
	 */
	private void refillDeck() {
		cardDeck.fill(createDeck());
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
				
				clientOos.writeObject("placeCard");
				clientOos.writeObject(uCard);
				clientOos.writeObject(sender);
				
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
				nextPlayer(currentPlayer);
			}
			if(currentCard.getCardId() == 13) {
				drawCardStackNumber += 2;
			}
			if(currentCard.getCardId() == 14) {
				drawCardStackNumber += 4;
			}
			
			nextPlayer(currentPlayer);
			propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, "PlacedCardFlag", oldPlacedCardFlag, placedCardFlag));
		}
		return true;
	}
	
	public void setCardColor(Player sender, UnoCard selectedCard, int color) {
		if(isClient) { // Send card and color to server to change there
			System.out.println("SENT");
			System.out.println(sender);
			System.out.println(color);
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				ObjectInputStream clientOis = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOis = new ObjectInputStream(clientSocket.getInputStream());
				
				clientOos.writeObject("setCardColor");
				clientOos.writeObject(sender);
				clientOos.writeObject(selectedCard);
				clientOos.writeInt(color);
				
				clientOis.close();
				clientOos.close();
				clientSocket.close();
				propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, "Change", false, true));
			} catch (IOException e) {
			}
		} else {
			selectedCard.setColor(color, selectedCard);
			propertyChangeSupport.getPropertyChangeListeners()[0].propertyChange(new PropertyChangeEvent(this, "Change", false, true));
		}
	}
	
	private void nextPlayer(Player p) {
		if(isReverse) {
			// TODO add logic if the reverse card was played
			// Placeholder
			for(Iterator<Pair<Player, PlayerHand>> iterator = players.iterator(); iterator.hasNext(); ) {
				if (p.equals(iterator.next().getKey())) {
					if(iterator.hasNext()) {
						currentPlayer = iterator.next().getKey();
					} else {
						currentPlayer = players.getFirst().getKey();
					}
					return;
				}
			}
		} else {
			for(Iterator<Pair<Player, PlayerHand>> iterator = players.iterator(); iterator.hasNext(); ) {
				if (p.equals(iterator.next().getKey())) {
					if(iterator.hasNext()) {
						currentPlayer = iterator.next().getKey();
					} else {
						currentPlayer = players.getFirst().getKey();
					}
					return;
				}
			}
		}
	}

	private boolean isPlacable(UnoCard uCard) {
		if(uCard == null) return false;
		
		if(uCard.getCardId() == 14) return true; // Always play +4 cards
		
		if(uCard.getCardId() == getCurrentCard().getCardId() 
				|| (uCard.getColor() == this.getCurrentCard().getColor() 
					&& getCurrentCard().getCardId() != 12 
					&& getCurrentCard().getCardId() != 14) ) {
			
			// if its a wild card and there is a +X card as current card
			if( (uCard.getCardId() == 12 
					&& getCurrentCard().getCardId() == 14 
					&& getCurrentCard().getCardId() == 13)) return false; 
			
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<UnoCard> getplacedUnoCards() {
		if(isClient) {
			ArrayList<UnoCard> pc = null;
			try {
				Socket clientSocket = null;
				ObjectOutputStream clientOos = null;
				ObjectInputStream clientOis = null;
				
				clientSocket = new Socket(host.getHostName(), PORT);
				
				clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOis = new ObjectInputStream(clientSocket.getInputStream());
				
				clientOos.writeObject("getPlacedCards");
				pc = (ArrayList<UnoCard>) clientOis.readObject();
				
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
		players.add(new Pair<Player, PlayerHand>(p, new PlayerHand(this)));
	}
	
	/**
	 * This should only be executed by the server
	 * @param p - player you wish to remove
	 */
	public synchronized void removePlayer(Player p) {
		Pair<Player, PlayerHand> pPair = null;
		for(Pair<Player, PlayerHand> pair : players) {
			if (pair.getKey().equals(p)) {
				pPair = pair;
			}
		}
		players.remove(pPair);
	}
	
	private void waitForPlayerTwo() {
		while(players.size() < 2) System.out.print("");
	}
	
	public Player getPlayer() {
		return player;
	}

	private void setPlayer(Player player) {
		this.player = player;
	}

	private class ServerClass implements Runnable {
		public ServerClass() {
		}
		@Override
		public void run() {
			while(true) {
				try {
					Socket socket  = server.accept();
					
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					// Instruction will be sent by client
					String instruction = (String) ois.readObject();
					
					// Server will try to match the instruction and respond with the desired object
					if(instruction.matches("join")) {
						Player joinedPlayer = (Player) ois.readObject();
						addPlayer(joinedPlayer);
						System.out.println(joinedPlayer.toString()+" joined the game!");
						oos.writeObject(joinedPlayer);
					}
					if(instruction.matches("drawCard")) {
						UnoCard drawnCard = drawCard();
						oos.writeObject(drawnCard);
					}
					if(instruction.matches("drawCardForPlayer")) {
						drawCard((Player) ois.readObject());
					}
					if(instruction.matches("placeCard")) {
						placeCard((UnoCard) ois.readObject(), (Player) ois.readObject());
					}
					// Server getter
					if(instruction.matches("getCurrentCard")) {
						UnoCard serverCard = getCurrentCard();
						oos.writeObject(serverCard);
					}
					if(instruction.matches("getPlacedCards")) {
						oos.writeObject(getplacedUnoCards());
					}
					if(instruction.matches("getCurrentPlayer")) {
						Player serverPlayer = getCurrentPlayer();
						oos.writeObject(serverPlayer);
					}
					if(instruction.matches("getPlayerHand")) {
						Player p = (Player) ois.readObject();
						PlayerHand serverPlayerHand = null;
						for(Pair<Player, PlayerHand> pair : players) {
							if (pair.getKey().equals(p)) {
								serverPlayerHand = pair.getValue();
							}
						}
						oos.writeObject(serverPlayerHand);
					}
					if(instruction.matches("getSelectedCard")) {
						Player p = (Player) ois.readObject();
						oos.writeObject(getSelectedCard(p));
					}
					
					// Server setter
					if(instruction.matches("setPlayerHandSelectedCard")) {
						Player p = (Player) ois.readObject();
						UnoCard toBeSelectedUnoCard = (UnoCard) ois.readObject();
						PlayerHand serverPlayerHand = null;
						
						for(Pair<Player, PlayerHand> pair : players) {
							if (pair.getKey().equals(p)) {
								serverPlayerHand = pair.getValue();
							}
						}
						
						serverPlayerHand.setSelectedCard(toBeSelectedUnoCard);
					}
					if(instruction.matches("setCardColor")) {
						Player sender = (Player) ois.readObject();
						UnoCard toBeChangedUnoCard = (UnoCard) ois.readObject();
						int color = ois.readInt();
						PlayerHand serverPlayerHand = null;
						
						for(Pair<Player, PlayerHand> pair : players) {
							System.out.println("pair check");
							if (pair.getKey().equals(sender)) {
								System.out.println("gets to sender");
								serverPlayerHand = pair.getValue();
								break;
							}
						}
						
						for (UnoCard unoCard : serverPlayerHand.getPlayerCards()) {
							System.out.println("uno card check");
							if(unoCard.equals(toBeChangedUnoCard)) {
								setCardColor(sender, unoCard, color);
								break;
							}
						}
					}
					ois.close();
					oos.close();
					socket.close();
					
				} catch (IOException e) {
				} catch (ClassNotFoundException e) {
				}
				
			}
		}
		
	}
	
	private ArrayList<UnoCard> createDeck() {
		ArrayList<UnoCard> cardList = new ArrayList<UnoCard>();
		
		cardList.add(new UnoCard(UnoCard.COLOR_RED, UnoCard.CARD_ZERO));
		cardList.add(new UnoCard(UnoCard.COLOR_BLUE, UnoCard.CARD_ZERO));
		cardList.add(new UnoCard(UnoCard.COLOR_GREEN, UnoCard.CARD_ZERO));
		cardList.add(new UnoCard(UnoCard.COLOR_YELLOW, UnoCard.CARD_ZERO));
		
		// Pairs of two, each color
		for(int color = 0; color <= 3; color++) {
			for(int cardid = 1; cardid < 12; cardid++) {
				for(int cardamount = 0; cardamount < 2; cardamount++)  {
					cardList.add(new UnoCard(color, cardid));
				}
			}
		}
			
		// draw red
		cardList.add(new UnoCard(UnoCard.COLOR_RED, UnoCard.CARD_DRAW_TWO));
		cardList.add(new UnoCard(UnoCard.COLOR_RED, UnoCard.CARD_DRAW_TWO));
		
		// draw blue	
		cardList.add(new UnoCard(UnoCard.COLOR_BLUE, UnoCard.CARD_DRAW_TWO));
		cardList.add(new UnoCard(UnoCard.COLOR_BLUE, UnoCard.CARD_DRAW_TWO));
		
		// draw green
		cardList.add(new UnoCard(UnoCard.COLOR_GREEN, UnoCard.CARD_DRAW_TWO));
		cardList.add(new UnoCard(UnoCard.COLOR_GREEN, UnoCard.CARD_DRAW_TWO));
		
		// draw yellow
		cardList.add(new UnoCard(UnoCard.COLOR_YELLOW, UnoCard.CARD_DRAW_TWO));
		cardList.add(new UnoCard(UnoCard.COLOR_YELLOW, UnoCard.CARD_DRAW_TWO));
			
		// Wild cards
		/* temp disabled since they dont work
		cardList.add(new UnoCard(UnoCard.COLOR_WILD, UnoCard.CARD_WILD));
		cardList.add(new UnoCard(UnoCard.COLOR_WILD, UnoCard.CARD_WILD));
		cardList.add(new UnoCard(UnoCard.COLOR_WILD, UnoCard.CARD_WILD));
		cardList.add(new UnoCard(UnoCard.COLOR_WILD, UnoCard.CARD_WILD));
			
		// Draw Four
		cardList.add(new UnoCard(UnoCard.COLOR_WILD, UnoCard.CARD_DRAW_FOUR));
		cardList.add(new UnoCard(UnoCard.COLOR_WILD, UnoCard.CARD_DRAW_FOUR));
		cardList.add(new UnoCard(UnoCard.COLOR_WILD, UnoCard.CARD_DRAW_FOUR));
		cardList.add(new UnoCard(UnoCard.COLOR_WILD, UnoCard.CARD_DRAW_FOUR));
		*/
		Collections.shuffle(cardList);
		return cardList;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

}
