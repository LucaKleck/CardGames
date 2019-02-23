package core;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

public class CardDeck<CardType extends Card> implements Serializable {
	private static final long serialVersionUID = 1761187119831119070L;
	
	private Stack<CardType> stack = new Stack<CardType>();
	
	public CardDeck(ArrayList<CardType> cardsToInsertArrayList) {
		stack.addAll(cardsToInsertArrayList);
		this.stack.setSize(cardsToInsertArrayList.size());
	}
	
	public boolean isEmpty() {
		return stack.empty();
	}
	
	public CardType drawCard() {
		return stack.pop();
	}
	
	public CardType lookAtCard() {
		return stack.peek();
	}
	
	public void placeCardOnTop(CardType e) {
		stack.add(e);
	}

	public void fill(ArrayList<CardType> createDeck) {
		stack.addAll(createDeck);
	}

}
