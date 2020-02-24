package cardgames.games;

public enum GameDataType {
	// server to client
	CARDS_DRAWN,			// sends ArrayList<Card>
	CARD_LIST,				// sends card list
	CURRENT_PLAYER,			// sends current player
	CURRENT_CARD,			// sends current card
	CURRENT_STACK_NUMBER,	// sends current stack number
	CURRENT_REVERSE,		// sends current reverse state
	CURRENT_HAS_DRAWN,		// sends current has drawn state
	PLACED_CARD, 			// sends card, next player, drawCardStackNumber, isReversed
	REQ_PAIR,				// sends requested <Client, hand> pair 
	;
	
}
