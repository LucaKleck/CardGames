package core.frames;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import core.Player;

public class PlayerElement extends JPanel {
		private static final long serialVersionUID = 225925338698072009L;
		
		/**
		 * @param p
		 * @param unoPlayingField
		 */
		public PlayerElement(Player p) {
			
			setBackground(Color.WHITE);
			JLabel lblPlayername = new JLabel(p.playerName);
			add(lblPlayername);
			
		}
		
}