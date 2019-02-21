package uno.frames;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import uno.UnoCard;

public class CardFieldPanel extends JScrollPane {
	private static final long serialVersionUID = 1L;
	
	private UnoPlayingField unoSpielfeld;
	private JPanel viewport = new JPanel();
	
	public CardFieldPanel(UnoPlayingField unoSpielfeld) {
		this.unoSpielfeld = unoSpielfeld;
		viewport.setLayout(new MigLayout("insets 0 0 0 0", "["+UnoCard.CARD_WIDTH+"px]["+UnoCard.CARD_WIDTH+"px]", "["+UnoCard.CARD_HEIGHT+"px]["+UnoCard.CARD_HEIGHT+"px]"));
		this.setViewportView(viewport);
		
		addComponents();
		
	}

	private void addComponents() {
		GraphicUnoCard graphicUnoCard = new GraphicUnoCard(unoSpielfeld.getCurrentCard());
		viewport.add(graphicUnoCard, "cell 0 0,alignx center,aligny top");
		
		for(int i = 0; i < unoSpielfeld.getplacedUnoCards().size(); i++) {
			try {
				viewport.add(new GraphicUnoCard(unoSpielfeld.getplacedUnoCards().get(i)), "cell "+(unoSpielfeld.getplacedUnoCards().size()-i-1)+" 1");
			} catch (NullPointerException e) {
			}
		}		
	}

	public void updateField() {
		viewport.removeAll();
		addComponents();
		validate();
		repaint();
	}
	
}
