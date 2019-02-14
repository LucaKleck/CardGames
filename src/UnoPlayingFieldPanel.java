import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

public class UnoPlayingFieldPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private UnoPlayingField unoPlayingField;
	private CardFieldPanel lastCardsField;
	private PlayerPanel playerHandPanel;
	private JTextPane textPaneInfo;
	private JButton btnDrawCard;
	private AtomicInteger color = new AtomicInteger(-1); // -1 = wait for color pick
	private JButton btnRed;
	private JButton btnBlue;
	private JButton btnGreen;
	private JButton btnYellow;

	
	private Timer t = new Timer(1000, e-> {
		lastCardsField.updateField();
		playerHandPanel.updateField();
		updateText();
		validate();
	}); 
	private JLabel lblColorInfo;
	public UnoPlayingFieldPanel(Player clientPlayer, boolean isClient) {
		if(!isClient) {
			try {
				this.unoPlayingField = new UnoPlayingField(clientPlayer);
			} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
				System.exit(0);
			}
		} else {
			try {
				this.unoPlayingField = new UnoPlayingField(clientPlayer, true);
			} catch (ClassNotFoundException | IOException | InterruptedException e1) {
				e1.printStackTrace();
				System.exit(0);
			}
		}
		this.setBackground(Color.WHITE);
		
		unoPlayingField.addPropertyChangeListener(new CardChangeListener());
		setLayout(new MigLayout("insets 0 0 0 0, gap 0px", "[80%,fill][20%,grow,fill]", "[32.5%:32.5%:32.5%,grow,fill][32.5%:32.5%:32.5%,grow,fill][17.5%:17.5%:17.5%,fill][17.5%:17.5%:17.5%,fill]"));
		
		textPaneInfo = new JTextPane();
		updateText();
		textPaneInfo.setEditable(false);
		add(textPaneInfo, "cell 1 0,grow");
		
		JPanel colorPanel = new JPanel();
		add(colorPanel, "cell 1 1,grow");
		colorPanel.setLayout(new MigLayout("", "[][fill]", "[top][top][]"));
		
		btnRed = new JButton("Red");
		btnRed.setBackground(Color.RED);
		btnRed.addActionListener(e -> {
			
			if(unoPlayingField.getSelectedCard(clientPlayer).getCardId() == 12 || unoPlayingField.getSelectedCard(clientPlayer).getCardId() == 14) {
				color.set(0);
			} else {
				color.set(-1);
			}
		});
		colorPanel.add(btnRed, "flowx,cell 0 0 2 1,grow");
		
		btnBlue = new JButton("Blue");
		btnBlue.setBackground(Color.BLUE);
		btnBlue.addActionListener(e -> {
			
			if(unoPlayingField.getSelectedCard(clientPlayer).getCardId() == 12 || unoPlayingField.getSelectedCard(clientPlayer).getCardId() == 14) {
				color.set(1);
			} else {
				color.set(-1);
			}
		});
		colorPanel.add(btnBlue, "cell 0 0 2 1,grow");
		
		btnGreen = new JButton("Green");
		btnGreen.setBackground(Color.GREEN);
		btnGreen.addActionListener(e -> {
			
			if(unoPlayingField.getSelectedCard(clientPlayer).getCardId() == 12 || unoPlayingField.getSelectedCard(clientPlayer).getCardId() == 14) {
				color.set(2);
			} else {
				color.set(-1);
			}
		});
		colorPanel.add(btnGreen, "flowx,cell 0 1 2 1,grow");
		
		btnYellow = new JButton("Yellow");
		btnYellow.setBackground(Color.YELLOW);
		btnYellow.addActionListener(e -> {
			
			if(unoPlayingField.getSelectedCard(clientPlayer).getCardId() == 12 || unoPlayingField.getSelectedCard(clientPlayer).getCardId() == 14) {
				color.set(3);
			} else {
				color.set(-1);
			}
		});
		colorPanel.add(btnYellow, "cell 0 1 2 1,grow");
		
		lblColorInfo = new JLabel("No Wildcard / Draw 4 Selected");
		colorPanel.add(lblColorInfo, "cell 1 2");
		
		
		
		// Other Panels
		playerHandPanel = new PlayerPanel(clientPlayer, unoPlayingField);
		playerHandPanel.setBackground(Color.GRAY);
		add(playerHandPanel, "cell 0 2 1 2,alignx left,aligny top");
			
		
		lastCardsField = new CardFieldPanel(unoPlayingField);
		add(lastCardsField, "cell 0 0 1 2,alignx left,growy");
		
		JButton btnPlaySelectedCard = new JButton("Play Card");
		btnPlaySelectedCard.setBounds(120, 30, 120, 20);
		this.add(btnPlaySelectedCard, "cell 1 2,alignx left,growy");
		
		btnDrawCard = new JButton("Draw Card");
		add(btnDrawCard, "cell 1 3");
		/*
		 * Creates Runnable that will be run on button press </br>
		 * plays currently selected card and if it's a wild/+4 card waits until the player selected a color
		 */
		btnPlaySelectedCard.addActionListener(e -> {
			Runnable run = new Runnable() {
				
				@Override
				public void run() {
					if(clientPlayer.equals(unoPlayingField.getCurrentPlayer())) {
						playerHandPanel.hideCards(); // TODO Implement this so that players can't change to other card while they select color
						if(unoPlayingField.getSelectedCard(clientPlayer).getCardId() == 12 || unoPlayingField.getSelectedCard(clientPlayer).getCardId() == 14) {
							waitForColorPick();
							unoPlayingField.setCardColor(unoPlayingField.getCurrentPlayer(), unoPlayingField.getPlayerHand(unoPlayingField.getCurrentPlayer()).getSelectedCard(), color.get());
						}
						unoPlayingField.placeCard(unoPlayingField.getSelectedCard(clientPlayer), clientPlayer);
						resetColor();
					}
					playerHandPanel.updateField();
					lastCardsField.updateField();
					updateText();
				}
			};
			Thread thread = new Thread(run);
			thread.start();
		});
		
		btnDrawCard.addActionListener(e -> {
			if(clientPlayer.equals(unoPlayingField.getCurrentPlayer())) {
				unoPlayingField.drawCard(clientPlayer);
			}
			playerHandPanel.updateField();
			lastCardsField.updateField();
			updateText();
		});
		t.setRepeats(true);
		t.start();
	}

	private int waitForColorPick() {
		color.set(-1);
		lblColorInfo.setText("Select Color!");
		while(color.get() == -1) {
			System.out.print("");
		}
		lblColorInfo.setText("No Wildcard / Draw 4 Selected");
		return color.get();
	}

	public void resetColor() {
		color.set(-1);
	}
	
	private void updateText() {
		textPaneInfo.setText("You are: "+unoPlayingField.getPlayer()+"\n Current Player: "+unoPlayingField.getCurrentPlayer().getPlayerName());
	}

	public UnoPlayingField getUnoSpielfeld() {
		return unoPlayingField;
	}

	private class CardChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getPropertyName().matches("PlacedCardFlag")) {
				if(((Boolean) evt.getNewValue()).booleanValue() == true) {
					lastCardsField.updateField();
					updateText();
					validate();
				}
			}
			if(evt.getPropertyName().matches("Change")) {
				if(((Boolean) evt.getNewValue()).booleanValue() == true) {
					lastCardsField.updateField();
					updateText();
					validate();
				}
			}
		}
		
	}
}
