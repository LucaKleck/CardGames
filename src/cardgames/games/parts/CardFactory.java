package cardgames.games.parts;

import java.util.ArrayList;
import java.util.Collections;

import cardgames.client.GameModes;
import cardgames.constants.CardGamesConstants;

public class CardFactory<CardT extends Card> {
	
	
	@SuppressWarnings("unchecked")
	public ArrayList<CardT> createDeck(GameModes gamemode) {
		if(gamemode.getModeID() == 1) {
			ArrayList<CardT> cardList = new ArrayList<CardT>();
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_RED, CardGamesConstants.CARD_ZERO));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_BLUE, CardGamesConstants.CARD_ZERO));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_GREEN, CardGamesConstants.CARD_ZERO));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_YELLOW, CardGamesConstants.CARD_ZERO));
			
			// Pairs of two, each color
			for(int color = 0; color <= 3; color++) {
				for(int cardid = 1; cardid < 12; cardid++) {
					for(int cardamount = 0; cardamount < 2; cardamount++)  {
						cardList.add((CardT) new UnoCard(color, cardid));
					}
				}
			}
			
			// draw red
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_RED, CardGamesConstants.CARD_DRAW_TWO));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_RED, CardGamesConstants.CARD_DRAW_TWO));
			
			// draw blue	
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_BLUE, CardGamesConstants.CARD_DRAW_TWO));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_BLUE, CardGamesConstants.CARD_DRAW_TWO));
			
			// draw green
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_GREEN, CardGamesConstants.CARD_DRAW_TWO));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_GREEN, CardGamesConstants.CARD_DRAW_TWO));
			
			// draw yellow
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_YELLOW, CardGamesConstants.CARD_DRAW_TWO));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_YELLOW, CardGamesConstants.CARD_DRAW_TWO));
			
			// Wild cards
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_WILD, CardGamesConstants.CARD_WILD));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_WILD, CardGamesConstants.CARD_WILD));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_WILD, CardGamesConstants.CARD_WILD));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_WILD, CardGamesConstants.CARD_WILD));
			
			// Draw Four
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_WILD, CardGamesConstants.CARD_DRAW_FOUR));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_WILD, CardGamesConstants.CARD_DRAW_FOUR));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_WILD, CardGamesConstants.CARD_DRAW_FOUR));
			cardList.add((CardT) new UnoCard(CardGamesConstants.COLOR_WILD, CardGamesConstants.CARD_DRAW_FOUR));
			
			Collections.shuffle(cardList);
			return cardList;
		}
		return null;
	}
}
