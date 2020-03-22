package cardgames.frames.games.uno;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import cardgames.client.ClientCommands;
import cardgames.client.ClientOutputPackage;
import cardgames.games.ClientGameAction;
import cardgames.games.GameActionType;
import net.miginfocom.swing.MigLayout;

public class PlayerControlPanel extends JPanel {
	private static final long serialVersionUID = 3506904958679912859L;
	private JButton btnDrawCards;
	
	public PlayerControlPanel(UnoPane unoPane) {
		setLayout(new MigLayout("", "[]", "[]"));
		
		btnDrawCards = new JButton("Draw Card(s)");
		add(btnDrawCards, "cell 0 0");
		
		btnDrawCards.addActionListener(e -> {
			ArrayList<Object> dataContianer = new ArrayList<Object>();
			dataContianer.add(new ClientGameAction(unoPane.getGame().getClientServerConnector().getClient(), GameActionType.DRAW_CARD, null));
			unoPane.getGame().getClientServerConnector().sendClientOutputPackage(new ClientOutputPackage(ClientCommands.GAME_ACTION, dataContianer));
		});
	}

}
