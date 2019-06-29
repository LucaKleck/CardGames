package uno.frames;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import core.Player;
import net.miginfocom.swing.MigLayout;
import uno.UnoCard;
import uno.UnoPlayerHand;
import uno.UnoPlayingField;

public class UnoPlayerPanel extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private UnoPlayingField unoSpielfeld;
	private Player player;
	private JPanel viewport = new JPanel();
	private boolean disableCards = false;
	
	public UnoPlayerPanel(Player player, UnoPlayingField unoSpielfeld) {
		this.unoSpielfeld = unoSpielfeld;
		this.player = player;
		viewport.setLayout(new MigLayout("insets 0 0 0 0", "["+UnoCard.CARD_WIDTH+"px]", "["+UnoCard.CARD_HEIGHT+"px][20px]"));
		this.setViewportView(viewport);
		
		addComponents();
		
	}

	private void addComponents() {
		UnoPlayerHand uph = unoSpielfeld.getPlayerHand(player);
		if(uph == null) return;
		
		GraphicUnoCard graphicUnoCard = new GraphicUnoCard(uph.getSelectedCard());
		viewport.add(graphicUnoCard, "cell 0 0,alignx center,aligny top");
		viewport.add(new JLabel("Selected Card"), "cell 0 1,alignx center,aligny top");
		for(int i = 0; i < uph.getPlayerCards().size(); i++) {
			GraphicUnoCard graphicUnoCard2 = new GraphicUnoCard(uph.getPlayerCards().get(i));
			graphicUnoCard2.addMouseListener( new UnoPlayerCardListener(uph.getPlayerCards().get(i), uph, unoSpielfeld, this ));
			viewport.add(graphicUnoCard2, "cell "+(i+1)+" 0");
		}
	}

	public void updateField() {
		viewport.removeAll();
		addComponents();
		validate();
		repaint();
	}

	public synchronized Player getPlayer() {
		return player;
	}

	public boolean isDisabledCards() {
		return disableCards;
	}
	
	public void toggleDisableCards() {
		this.disableCards = !disableCards; 
	}
	
}
