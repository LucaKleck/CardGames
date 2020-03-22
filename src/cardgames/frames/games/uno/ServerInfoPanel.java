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
	
	public ServerInfoPanel(UnoPane unoPane) {
		this.unoPane = unoPane;
		setLayout(new MigLayout("", "[]", "[][][][]"));
		
		lblServerName = new JLabel("server name not updated");
		add(lblServerName, "cell 0 0");
		
		lblClientList = new JLabel("play list not updated");
		lblClientList.setText(unoPane.getClientConnection().getLocalClientList().toString());
		add(lblClientList, "cell 0 1");
		
		lblCurrentPlayer = new JLabel("Current Player: ?");
		add(lblCurrentPlayer, "cell 0 2");
		
		lblNextPlayer = new JLabel("Next Player: ?");
		add(lblNextPlayer, "cell 0 3");
		updateInfo();
	}

	public void updateInfo() {
		if(unoPane.getGame() == null) return;
		lblServerName.setText(unoPane.getGame().getClientServerConnector().getLocalServerInfo().getServerName());
		lblNextPlayer.setText("Next Player: "+unoPane.getGame().getNextPlayer());
		lblCurrentPlayer.setText("Current Player: "+unoPane.getGame().getCurrentPlayer());
	}
}
