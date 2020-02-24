package cardgames.frames;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.SpringLayout;



public class CustomContentPane extends JPanel {
	private static final long serialVersionUID = 4351457583875170361L;
	
	private LayoutManager layout;
	
	public CustomContentPane() {
		layout = new SpringLayout();
		setLayout(layout);
	}

}
