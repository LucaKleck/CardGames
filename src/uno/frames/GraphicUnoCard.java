package uno.frames;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import uno.UnoCard;

public class GraphicUnoCard extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage unoCardImage = null;
	
	public GraphicUnoCard(UnoCard unoCard) {
		if(unoCard != null) {
			unoCardImage = unoCard.getUnoCardImage();
		}
		this.setPreferredSize(new Dimension(UnoCard.CARD_WIDTH, UnoCard.CARD_HEIGHT));
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(unoCardImage != null) {
			g.drawImage(unoCardImage, 0, 0, getWidth(), getHeight(), getParent());
		}
	}
	
}
