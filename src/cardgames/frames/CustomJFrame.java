package cardgames.frames;

import java.awt.Dimension;

import javax.swing.JFrame;

public class CustomJFrame extends JFrame {
	private static final long serialVersionUID = -1472670154998080689L;

	public CustomJFrame() {
		super("Card Game Collection");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1280, 720);
		setMinimumSize(new Dimension(1280, 720));
		setContentPane(new MainMenuPane());
		pack();
		setVisible(true);
	}

}
