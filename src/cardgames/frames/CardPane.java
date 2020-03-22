package cardgames.frames;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import cardgames.server.Server;
import net.miginfocom.swing.MigLayout;

public class CardPane  extends JPanel {
	private static final long serialVersionUID = 3423999979374647772L;
	private JPanel contentContainer;
	private Server server; // if player makes server set it here
	
	public CardPane() {
		super();
		setLayout(new MigLayout("insets 0 0 0 0", "[grow,fill]", "[grow,fill]"));
		contentContainer = new JPanel();
		contentContainer.setMinimumSize(new Dimension(1280, 720));
		add(contentContainer, "cell 0 0");
		contentContainer.setLayout(new CardLayout());
		contentContainer.add(new MainMenuPane(this), CustomContentPane.MAIN_MENU);
	}

	public JPanel getContentContainer() {
		return contentContainer;
	}
	
	public Server getServer() {
		return server;
	}
	
	public void setServer(Server server) {
		this.server = server;
	}
}
