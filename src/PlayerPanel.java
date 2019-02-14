import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

public class PlayerPanel extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private UnoPlayingField unoSpielfeld;
	private Player player;
	private JPanel viewport = new JPanel();
	
	public PlayerPanel(Player player, UnoPlayingField unoSpielfeld) {
		this.unoSpielfeld = unoSpielfeld;
		this.player = player;
		viewport.setLayout(new MigLayout("insets 0 0 0 0", "["+UnoCard.CARD_WIDTH+"px]", "["+UnoCard.CARD_HEIGHT+"px][20px]"));
		this.setViewportView(viewport);
		
		addComponents();
		
	}

	private void addComponents() {
		if(unoSpielfeld.getPlayerHand(player) == null) return;
		GraphicUnoCard graphicUnoCard = new GraphicUnoCard(unoSpielfeld.getPlayerHand(player).getSelectedCard());
		viewport.add(graphicUnoCard, "cell 0 0,alignx center,aligny top");
		viewport.add(new JLabel("Selected Card"), "cell 0 1,alignx center,aligny top");
		
		for(int i = 0; i < unoSpielfeld.getPlayerHand(player).getPlayerCards().size(); i++) {
			GraphicUnoCard graphicUnoCard2 = new GraphicUnoCard(unoSpielfeld.getPlayerHand(player).getPlayerCards().get(i));
			graphicUnoCard2.addMouseListener( new PlayerCardListener(unoSpielfeld.getPlayerHand(player).getPlayerCards().get(i), unoSpielfeld.getPlayerHand(player), unoSpielfeld, new PlayerCardChangeListener(this) ));
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

	public void hideCards() {
		
	}
	
}
