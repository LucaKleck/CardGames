package cardgames.frames.games.uno;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class ServerInfoPanel extends JPanel {
	private static final long serialVersionUID = -6110122956430107581L;
	private JLabel lblServerName;
	private JLabel lblClientList;
	private JLabel lblNextPlayer;
	private JLabel lblCurrentPlayer;
	private UnoPane unoPane;
	private JLabel lblGameMode;
	
	public ServerInfoPanel(UnoPane unoPane) {
		this.unoPane = unoPane;
		setLayout(new MigLayout("", "[]", "[][][][][]"));
		
		lblServerName = new JLabel("server name not updated");
		add(lblServerName, "cell 0 0");
		
		lblGameMode = new JLabel("game mode not updated");
		add(lblGameMode, "cell 0 1");
		
		lblClientList = new JLabel("play list not updated");
		add(lblClientList, "cell 0 2");
		
		lblCurrentPlayer = new JLabel("Current Player: ?");
		add(lblCurrentPlayer, "cell 0 3");
		
		lblNextPlayer = new JLabel("Next Player: ?");
		add(lblNextPlayer, "cell 0 4");
		updatePanel();
	}

	public void updatePanel() {
		if(unoPane.getGame() == null) return;
		lblServerName.setText(unoPane.getGame().getClientServerConnector().getLocalServerInfo().getServerName());
		lblGameMode.setText(unoPane.getClientConnection().getLocalServerInfo().getGameMode().toString());
		lblClientList.setText(unoPane.getClientConnection().getLocalClientList().toString());
		lblNextPlayer.setText("Next Player: "+unoPane.getGame().getNextPlayer());
		lblCurrentPlayer.setText("Current Player: "+unoPane.getGame().getCurrentPlayer());
	}
}
