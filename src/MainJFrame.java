import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

public class MainJFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public MainJFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(new Dimension(1280, 720));
		this.setLayout(new BorderLayout());
		
		this.add(new MainMenuPanel(this));
		
		
		setVisible(true);
	}


}
