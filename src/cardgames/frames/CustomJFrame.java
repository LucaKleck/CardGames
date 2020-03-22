package cardgames.frames;

import java.awt.Dimension;

import javax.swing.JFrame;

public class CustomJFrame extends JFrame {
	private static final long serialVersionUID = -1472670154998080689L;

	public CustomJFrame() {
		super("Card Game Collection");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1280, 720));
		setPreferredSize(new Dimension(1280, 720));
		setExtendedState( getExtendedState()|JFrame.MAXIMIZED_BOTH );
		setContentPane(new CardPane());
		pack();
		setVisible(true);
	}

}
