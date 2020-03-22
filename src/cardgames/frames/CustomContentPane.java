package cardgames.frames;

import java.awt.Dimension;

import javax.swing.JPanel;



public class CustomContentPane extends JPanel {
	private static final long serialVersionUID = 4351457583875170361L;
	public static final String MAIN_MENU = "MAIN_MENU";
	public static final String UNO_GAME = "UNO_GAME";
	public static final String LOCAL_CLIENT_LIST = "LOCAL_CLIENT_LIST";
	public static final String LOCAL_SERVER_INFO = "LOCAL_SERVER_INFO";
	public static final String LOCAL_GAME = "LOCAL_GAME";
	public static final String LOBBY = "LOBBY";

	private CardPane containerJPanel;
	
	public CustomContentPane(CardPane containerJPanel) {
		this.containerJPanel = containerJPanel;
		setMinimumSize(new Dimension(1280, 720));
	}
	
	public CardPane getContainerJPanel() {
		return containerJPanel;
	}

}
