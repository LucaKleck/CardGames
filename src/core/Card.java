package core;
import java.io.Serializable;

public abstract class Card implements Serializable{
	/**
	 * Card
	 */
	private static final long serialVersionUID = -5860863980460237488L;
	private String typeName;
	public Card(String typeName) {
		this.typeName = typeName;
	}
	
	public String getTypeName() {
		return typeName;
	}

}
