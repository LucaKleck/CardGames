package uno.frames;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;

import core.Player;
import net.miginfocom.swing.MigLayout;
import uno.UnoCard;
import uno.UnoPlayingField;

public class UnoPlayingFieldPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private UnoPlayingField unoPlayingField;
	private Player client;
	
	private CardFieldPanel lastCardsField;
	private UnoPlayerPanel playerHandPanel;
	private JTextPane textPaneInfo;
	private JButton btnDrawCard;
	private AtomicInteger color = new AtomicInteger(-1); // -1 == wait for color pick
	private JButton btnRed;
	private JButton btnBlue;
	private JButton btnGreen;
	private JButton btnYellow;

	private JLabel lblColorInfo;
	private JPanel colorPanel;
	private UnoPlayerInfoPanel unoPlayerInfoPanel;
	
	private Timer t = new Timer(5000, e-> {
		lastCardsField.updateField();
		playerHandPanel.updateField();
		updateText();
		unoPlayerInfoPanel.updateContent();
		
		if(unoPlayingField.getCurrentPlayer().equals(client))
		
		validate();
	}); 
	
	public UnoPlayingFieldPanel(Player clientPlayer, boolean isClient, InetAddress hostIP) {
		this.client = clientPlayer;
		if(!isClient) {
			try {
				this.unoPlayingField = new UnoPlayingField(clientPlayer);
			} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
				System.exit(0);
			}
		} else {
			try {
				this.unoPlayingField = new UnoPlayingField(clientPlayer, hostIP);
			} catch (ClassNotFoundException | IOException | InterruptedException e1) {
				e1.printStackTrace();
				System.exit(0);
			}
		}
		this.setBackground(Color.WHITE);
		
		unoPlayingField.addPropertyChangeListener(new CardChangeListener());
		setLayout(new MigLayout("insets 0 0 0 0, gap 0px", "[60%,fill][20%:n,grow][20%,grow,fill]", "[32.5%:32.5%:32.5%,grow,fill][32.5%:32.5%:32.5%,grow,fill][17.5%:17.5%:17.5%,fill][17.5%:17.5%:17.5%,fill]"));
		
		textPaneInfo = new JTextPane();
		updateText();
		
		unoPlayerInfoPanel = new UnoPlayerInfoPanel(unoPlayingField);
		add(unoPlayerInfoPanel, "cell 1 0 1 4,grow");
		textPaneInfo.setEditable(false);
		add(textPaneInfo, "flowy,cell 2 0,grow");
		
		colorPanel = new JPanel();
		colorPanel.setVisible(false);
		add(colorPanel, "cell 2 1,grow");
		colorPanel.setLayout(new MigLayout("", "[50%,fill][50%,fill]", "[33%][33%][33%]"));
		
		btnRed = new JButton("Red");
		colorPanel.add(btnRed, "cell 0 0,grow");
		btnRed.setBackground(Color.RED);
		btnRed.addActionListener(e -> {
			
			if(unoPlayingField.getSelectedCard(clientPlayer).getCardId() == UnoCard.CARD_WILD || unoPlayingField.getSelectedCard(clientPlayer).getCardId() == UnoCard.CARD_DRAW_FOUR) {
				color.set(0);
			} else {
				color.set(-1);
			}
		});
		
		btnBlue = new JButton("Blue");
		colorPanel.add(btnBlue, "cell 1 0,grow");
		btnBlue.setBackground(Color.BLUE);
		btnBlue.addActionListener(e -> {
			
			if(unoPlayingField.getSelectedCard(clientPlayer).getCardId() == UnoCard.CARD_WILD || unoPlayingField.getSelectedCard(clientPlayer).getCardId() == UnoCard.CARD_DRAW_FOUR) {
				color.set(1);
			} else {
				color.set(-1);
			}
		});
		
		btnGreen = new JButton("Green");
		colorPanel.add(btnGreen, "flowx,cell 0 1,grow");
		btnGreen.setBackground(Color.GREEN);
		btnGreen.addActionListener(e -> {
			
			if(unoPlayingField.getSelectedCard(clientPlayer).getCardId() == UnoCard.CARD_WILD || unoPlayingField.getSelectedCard(clientPlayer).getCardId() == UnoCard.CARD_DRAW_FOUR) {
				color.set(2);
			} else {
				color.set(-1);
			}
		});
		
		btnYellow = new JButton("Yellow");
		btnYellow.setBackground(Color.YELLOW);
		btnYellow.addActionListener(e -> {
			
			if(unoPlayingField.getSelectedCard(clientPlayer).getCardId() == UnoCard.CARD_WILD || unoPlayingField.getSelectedCard(clientPlayer).getCardId() == UnoCard.CARD_DRAW_FOUR) {
				color.set(3);
			} else {
				color.set(-1);
			}
		});
		colorPanel.add(btnYellow, "cell 1 1,grow");
		
		lblColorInfo = new JLabel("Select Color!");
		colorPanel.add(lblColorInfo, "cell 0 2 2 1,alignx center,aligny center");
		
		
		
		// Other Panels
		playerHandPanel = new UnoPlayerPanel(clientPlayer, unoPlayingField);
		playerHandPanel.setBackground(Color.GRAY);
		add(playerHandPanel, "cell 0 2 1 2,alignx left,aligny top");
			
		
		lastCardsField = new CardFieldPanel(unoPlayingField);
		add(lastCardsField, "cell 0 0 1 2,alignx left,growy");
		
		JButton btnPlaySelectedCard = new JButton("Play Card");
		btnPlaySelectedCard.setBounds(120, 30, 120, 20);
		this.add(btnPlaySelectedCard, "cell 2 2,alignx left,growy");
		
		btnDrawCard = new JButton("Draw Card");
		add(btnDrawCard, "cell 2 3");
		/*
		 * Creates Runnable that will be run on button press
		 * plays currently selected card and if it's a wild/+4 card waits until the player selected a color
		 */
		btnPlaySelectedCard.addActionListener(e -> {
			Runnable run = new Runnable() {
				
				@Override
				public void run() {
					if(clientPlayer.equals(unoPlayingField.getCurrentPlayer())) {
						
						playerHandPanel.toggleDisableCards();
						btnDrawCard.setVisible(false);
						
						if(unoPlayingField.getSelectedCard(clientPlayer) != null) {
							if(unoPlayingField.getSelectedCard(clientPlayer).getCardId() == UnoCard.CARD_WILD || unoPlayingField.getSelectedCard(clientPlayer).getCardId() == UnoCard.CARD_DRAW_FOUR) {
								waitForColorPick();
								unoPlayingField.setCardColor(clientPlayer, color.get());
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
								}
							}
							unoPlayingField.placeCard(unoPlayingField.getSelectedCard(clientPlayer), clientPlayer);
						}
						
						resetColor();
						btnDrawCard.setVisible(true);
						playerHandPanel.toggleDisableCards();
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
		colorPanel.setVisible(true);
		while(color.get() == -1) {
		}
		colorPanel.setVisible(false);
		return color.get();
	}

	public void resetColor() {
		color.set(-1);
	}
	
	private void updateText() {
		try {
			textPaneInfo.setText("You are: "+unoPlayingField.getPlayer()+"\n Current Player: "+unoPlayingField.getCurrentPlayer().getPlayerName()+"\n NextPlayer: "+unoPlayingField.getNextPlayer(unoPlayingField.getCurrentPlayer()).getPlayerName());
		} catch(NullPointerException e) {
			try {
				textPaneInfo.setText("You are: "+unoPlayingField.getPlayer()+"\n Current Player: "+unoPlayingField.getCurrentPlayer().getPlayerName());
			} catch (Exception e2) {
				textPaneInfo.setText("ERROR WHILE READING FROM HOST");
			}
			
		}
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
					unoPlayerInfoPanel.updateContent();
				}
			}
			if(evt.getPropertyName().matches("Change")) {
				if(((Boolean) evt.getNewValue()).booleanValue() == true) {
					lastCardsField.updateField();
					updateText();
					validate();
					unoPlayerInfoPanel.updateContent();
				}
			}
		}
		
	}
}
