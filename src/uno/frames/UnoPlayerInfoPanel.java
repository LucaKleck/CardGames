package uno.frames;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import core.frames.PlayerElement;
import net.miginfocom.swing.MigLayout;
import uno.PlayerInfoPair;
import uno.UnoPlayerHand;
import uno.UnoPlayingField;

public class UnoPlayerInfoPanel extends JScrollPane {
	private static final long serialVersionUID = 1486390367689813925L;
	private UnoPlayingField unoPlayingField;
	private JPanel viewport;
	
	public UnoPlayerInfoPanel(UnoPlayingField unoPlayingField) {
		this.unoPlayingField = unoPlayingField;
		this.viewport = new JPanel();
		
		setViewportView(viewport);
		
		updateContent();
	}

	public void updateContent() {
		viewport.setLayout(new MigLayout("", "["+(getWidth()-32)+"]", "[20px]"));
		
		viewport.removeAll();
		
		JPanel infoPanel = new JPanel();
		infoPanel.add(new JLabel("PlayerList"));
		 
		viewport.add(infoPanel, "cell 0 0,grow");
		
		int i = 0;
		for(PlayerInfoPair<UnoPlayerHand> pair : unoPlayingField.getPlayerList()) {
			viewport.add(new PlayerElement(pair.getPlayer(), unoPlayingField), "cell 0 "+ (++i) +",grow");
		}
		
	}
	
}
