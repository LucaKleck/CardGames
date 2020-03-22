package cardgames.frames.games.uno;

import cardgames.client.ClientServerConnector;
import cardgames.frames.CardPane;
import cardgames.frames.CustomContentPane;
import cardgames.frames.games.ChatPanel;
import cardgames.games.UnoGame;
import net.miginfocom.swing.MigLayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

public class UnoPane extends CustomContentPane implements PropertyChangeListener {
	private static final long serialVersionUID = -6020692673272564516L;
	private UnoGame game;
	private ClientServerConnector clientConnection;
	private PlacedCardsPanel placedCardsPanel;
	private ChatPanel chatPanel;
	private JPanel playerList;
	private ServerInfoPanel serverInfoPanel;
	private PlayerHandPanel playerHand;
	private PlayerControlPanel playerControlPanel;
	
	public UnoPane(CardPane containerJPanel, ClientServerConnector clientConnection, UnoGame game) {
		super(containerJPanel);
		this.game = game;
		game.addPropertyChangeListener(this);
		this.clientConnection = clientConnection;
		setLayout(new MigLayout("insets 0 0 0 0", "[grow][grow,center][grow]", "[grow][grow,center][grow]"));
		
		serverInfoPanel = new ServerInfoPanel(this);
		add(serverInfoPanel, "cell 2 0,grow");
		
		playerList = new JPanel();
		add(playerList, "cell 0 1,grow");
		
		placedCardsPanel = new PlacedCardsPanel(this);
		add(placedCardsPanel, "cell 1 1,grow");
		
		chatPanel = new ChatPanel();
		add(chatPanel, "cell 2 1,grow");
		
		playerHand = new PlayerHandPanel(this);
		add(playerHand, "cell 1 2,grow");
		
		playerControlPanel = new PlayerControlPanel(this);
		add(playerControlPanel, "cell 2 2,grow");
	}

	public UnoGame getGame() {
		return game;
	}

	public ClientServerConnector getClientConnection() {
		return clientConnection;
	}
	
	public void updatePanels() {
		playerHand.updatePlayerHand();
		placedCardsPanel.updatePlacedCards();
		serverInfoPanel.updateInfo();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		updatePanels();
	}
	
}
