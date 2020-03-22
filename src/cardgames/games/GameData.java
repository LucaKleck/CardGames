package cardgames.games;

import java.io.Serializable;
import java.util.ArrayList;

public class GameData implements Serializable {
	private static final long serialVersionUID = -5414405448934650207L;

	private GameDataType gdt;
	private ArrayList<Object> data;
	
	public GameData(GameDataType gdt, ArrayList<Object> gameData) {
		this.gdt = gdt;
		this.data = gameData;
	}
	
	public GameDataType getGdt() {
		return gdt;
	}
	
	public ArrayList<Object> getData() {
		return data;
	}
	
	@Override
	public String toString() {
		if(data == null) return gdt.toString();
		return gdt.toString()+" with payload{"+data.toString()+"}";
	}
}
