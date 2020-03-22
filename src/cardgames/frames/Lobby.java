package cardgames.frames;
import java.awt.CardLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import cardgames.client.Client;
import cardgames.client.ClientServerConnector;
import cardgames.frames.games.uno.UnoPane;
import cardgames.games.UnoGame;
import cardgames.server.ServerInfo;
import net.miginfocom.swing.MigLayout;

public class Lobby extends CustomContentPane implements PropertyChangeListener {
	private static final long serialVersionUID = 8583445542765547926L;
	private ClientServerConnector clientConnection;
	private JTextField textField;
	private JLabel lblServerName;
	private JScrollPane scrollPane;
	private JPanel serverControlPanel;
	private JLabel lblNewLabel_1;
	private JTextPane chat;
	private JButton btnStartGame;
	private JLabel lblClientList;
	
	public Lobby(CardPane containerJPanel, ClientServerConnector clientConnection) {
		super(containerJPanel);
		this.clientConnection = clientConnection;
		setLayout(new MigLayout("insets 0 0 0 0", "[300px:512px,grow,fill][120px,grow,fill][512px,grow,fill]", "[][grow][]"));
		
		String serverName = "";
		while(clientConnection.getLocalServerInfo() == null);
		serverName = clientConnection.getLocalServerInfo().getServerName();
		lblServerName = new JLabel("Server: " + serverName);
		add(lblServerName, "cell 0 0");
		
		while(clientConnection.getLocalClientList() == null);
		lblClientList = new JLabel(clientConnection.getLocalClientList().toString());
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, "cell 0 1,grow");
		
		JPanel panel = new JPanel();
		scrollPane.setViewportView(panel);
		panel.setLayout(new MigLayout("", "[grow][][]", "[]"));
		
		
		panel.add(lblClientList, "cell 0 0");
		
		JButton btnNewButton = new JButton("kick");
		panel.add(btnNewButton, "cell 1 0");
		
		JButton btnNewButton_1 = new JButton("ban");
		panel.add(btnNewButton_1, "cell 2 0");
		
		serverControlPanel = new JPanel();
		serverControlPanel.setBackground(Color.DARK_GRAY);
		add(serverControlPanel, "cell 1 1,grow");
		serverControlPanel.setLayout(new MigLayout("", "[grow,fill]", "[][]"));
		
		JLabel lblGameModes = new JLabel("Game Modes:");
		serverControlPanel.add(lblGameModes, "cell 0 0,growx");
		
		JRadioButton rdbtnUno = new JRadioButton("Uno");
		rdbtnUno.setSelected(true);
		serverControlPanel.add(rdbtnUno, "cell 0 1,growx");
		
		lblNewLabel_1 = new JLabel("Chat");
		add(lblNewLabel_1, "flowy,cell 2 1");
		
		chat = new JTextPane();
		add(chat, "cell 2 1,grow");
		
		textField = new JTextField();
		add(textField, "cell 2 1");
		textField.setColumns(10);
		
		btnStartGame = new JButton("Start Game");
		btnStartGame.addActionListener(e -> {
			if(containerJPanel.getServer() == null) return;
			containerJPanel.getServer().startGame();
		});
		add(btnStartGame, "cell 2 2,aligny top");
	
		clientConnection.addPropertyChangeListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case CustomContentPane.LOCAL_CLIENT_LIST:
			lblClientList.setText( ( (ArrayList<Client>) evt.getNewValue()).toString());
			break;
		case CustomContentPane.LOCAL_GAME:
			UnoGame game = ((UnoGame) evt.getNewValue());
			getContainerJPanel().getContentContainer().add(new UnoPane(getContainerJPanel(), clientConnection, game), CustomContentPane.UNO_GAME);
			CardLayout c1 = (CardLayout) getContainerJPanel().getContentContainer().getLayout();
			c1.show(getContainerJPanel().getContentContainer(), CustomContentPane.UNO_GAME);
			break;
		case CustomContentPane.LOCAL_SERVER_INFO:
			lblServerName.setText("Server: "+((ServerInfo) evt.getNewValue()).getServerName());
			break;
		default:
			break;
		}
	}
	
	

}
