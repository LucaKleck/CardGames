package cardgames.games.parts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

public class CardDeck<CardT extends Card> implements Serializable {
	private static final long serialVersionUID = 3296115322938080658L;
	
	private Stack<CardT> stack = new Stack<CardT>();
	
	public CardDeck(ArrayList<CardT> cardsToInsertArrayList) {
		stack.addAll(cardsToInsertArrayList);
		this.stack.setSize(cardsToInsertArrayList.size());
	}
	
	public boolean isEmpty() {
		return stack.empty();
	}
	
	public CardT drawCard() {
		return (CardT) stack.pop();
	}
	
	public CardT lookAtCard() {
		return stack.peek();
	}
	
	public void placeCardOnTop(CardT e) {
		stack.add(e);
	}

	public void fill(ArrayList<CardT> createDeck) {
		stack.addAll(createDeck);
	}
	
}
