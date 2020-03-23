package cardgames.frames.games.uno;

import javax.swing.JButton;
import javax.swing.JPanel;

import cardgames.client.Client;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

public class PlayerControlPanel extends JPanel {
	private static final long serialVersionUID = 3506904958679912859L;
	private JButton btnDrawCards;
	private Client localClient;
	private UnoPane unoPane;
	private JLabel lblCanPlace;
	
	public PlayerControlPanel(UnoPane unoPane) {
		setLayout(new MigLayout("", "[]", "[][]"));
		
		localClient = unoPane.getGame().getClientServerConnector().getClient();
		this.unoPane = unoPane;
		
		btnDrawCards = new JButton("Draw Card(s)");
		add(btnDrawCards, "cell 0 0");
		
		lblCanPlace = new JLabel("You have at least a placable card");
		add(lblCanPlace, "cell 0 1");
		
		btnDrawCards.addActionListener(e -> {
			unoPane.getGame().drawCards();
		});
		
		updatePanel();
	}
	
	public void updatePanel() {
		if(localClient.equals(unoPane.getGame().getCurrentPlayer()) &! unoPane.getGame().isHasDrawn()) {
			btnDrawCards.setEnabled(true);
		} else {
			btnDrawCards.setEnabled(false);
		}
		if(unoPane.getGame().canPlaceCard(localClient)) {
			lblCanPlace.setText("You have at least a placable card");
		} else {
			lblCanPlace.setText("You have no placable card, draw until you can place a card");
		}
	}

}
