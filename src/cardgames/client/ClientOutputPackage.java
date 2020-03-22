package cardgames.client;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientOutputPackage implements Serializable {
	private static final long serialVersionUID = 242638050444812928L;
	private ClientCommands command;
	private ArrayList<Object> data;

	// for GameAction data will be GameAction Object with command gameAction
	
	public ClientOutputPackage(ClientCommands command, ArrayList<Object> data) {
		this.command = command;
		this.data = data;
	}

	public ClientCommands getCommand() {
		return command;
	}

	public ArrayList<Object> getData() {
		return data;
	}
	
	@Override
	public String toString() {
		if(data == null) return command.toString();
		return command.toString()+" with payload{"+data.toString()+"}";
	}
}
