import java.util.ArrayList;
import java.util.Stack;

public class CardDeck<E> {
	private Stack<E> stack = new Stack<E>();
	
	public CardDeck(ArrayList<E> cardsToInsertArrayList) {
		stack.addAll(cardsToInsertArrayList);
		this.stack.setSize(cardsToInsertArrayList.size());
	}
	
	public boolean isEmpty() {
		return stack.empty();
	}
	
	public E drawCard() {
		return stack.pop();
	}
	
	public E lookAtCard() {
		return stack.peek();
	}
	
	public void placeCardOnTop(E e) {
		stack.add(e);
	}

	public void fill(ArrayList<E> createDeck) {
		stack.addAll(createDeck);
	}

}
