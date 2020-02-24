package cardgames.games;

public enum GameActionType  {
	PLACE_CARD,			// place card if card is wild send color
	DRAW_CARD,			// draw card
	REQ_PAIR, 			// ask for pair using local client
	REQ_CURRENT_CARD,	// ask for current card
	REQ_SYNCH,			// ask for synchronization
	;
}
