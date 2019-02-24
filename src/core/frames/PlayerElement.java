package core.frames;

import javax.swing.JPanel;

import core.Player;
import uno.UnoPlayingField;

import javax.swing.JLabel;

import java.awt.Color;

import javax.swing.JButton;

public class PlayerElement extends JPanel {
		private static final long serialVersionUID = 225925338698072009L;
		
		/**
		 * TODO change unoPlayingField to lobby
		 * @param p
		 * @param unoPlayingField
		 */
		public PlayerElement(Player p, UnoPlayingField unoPlayingField) {
			
			setBackground(Color.WHITE);
			JLabel lblPlayername = new JLabel(p.getPlayerName());
			add(lblPlayername);
			
			if(!unoPlayingField.isClient()) {
				JButton btnKick = new JButton("Kick");
				btnKick.addActionListener(e -> unoPlayingField.removePlayer(p));
				add(btnKick);
			}
			
		}
		
}