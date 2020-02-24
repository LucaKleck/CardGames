package cardgames.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cardgames.client.Client;
import cardgames.client.ClientOutputPackage;
import cardgames.games.ClientGameAction;

public class ClientHandler {
	private ExecutorService inputExecutor;
	private Socket clientSocket;
	ObjectOutputStream outputStream;
	
	private Client client; 	// client will send his info
	@SuppressWarnings("unused")
	private Server server; 	// host server
	private ClientRole role;// role will be assigned by server host default is player
	
	@SuppressWarnings("unchecked")
	public ClientHandler(Socket clientSocket, Server server) {

		inputExecutor = Executors.newSingleThreadExecutor();
		
		this.clientSocket = clientSocket;
		this.server = server;
		role = ClientRole.player;
		
		try {
			outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
			// wait and receive client if client is banned or name is taken block connection
			boolean isConnected = false;
			while(!isConnected) {
				try {
					ClientOutputPackage cop = (ClientOutputPackage) inputStream.readObject();
					switch (cop.getCommand()) {
					case CONNECT:
						ArrayList<Object> data = (ArrayList<Object>) inputStream.readObject();
						this.client = (Client) data.get(0);
						ArrayList<Object> serverInfo = new ArrayList<Object>();
						serverInfo.add(server.getServerInfo());
						outputStream.writeObject(new ServerOutputPackage(ServerCommands.SERVER_INFO, serverInfo));
						isConnected = true;
						break;
					default:
						System.exit(0);
						break;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			inputExecutor.execute(new InputHandler(inputStream));
		} catch (IOException e) {
			e.printStackTrace();
			// Failed connection -> retry or just disconnect and reconnect
		}
	}
	
	public void sendServerOutputPackage(ServerOutputPackage serverOutputPackage) {
		try {
			outputStream.writeObject(serverOutputPackage);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Client getClient() {
		return client;
	}

	public ClientRole getRole() {
		return role;
	}

	private class InputHandler implements Runnable {
		private ObjectInputStream inputStream;
		
		public InputHandler(ObjectInputStream inputStream) {
			this.inputStream = inputStream;
		}
		
		@Override
		public void run() {
			/*
			 *  receive structure:
			 *   1. get ClientCommands
			 *   2. switch case to what to do
			 *   3. unpack data act and flush connection
			 *   4. wait for ClientCommands again
			 */
			
			
			/*
			 * Example
			 * Receive gameAction -> play action in server (and verify action) -> send to everyone
			 * if action is invalid send client current game state
			 */
			while(!clientSocket.isClosed()) {
				try {
					ClientOutputPackage cop = (ClientOutputPackage) inputStream.readObject();
					switch (cop.getCommand()) {
					case CHAT:
						break;
					case CLIENT_LIST:
						break;
					case CONNECT:
						break;
					case DISCONNECT:
						break;
					case GAME_ACTION:
						server.getGame().processClientGameAction((ClientGameAction) cop.getData().get(0));
						break;
					case RECONNECT:
						break;
					case SERVER_INFO:
						ArrayList<Object> data = new ArrayList<Object>();
						data.add(server.getServerInfo()); 	
						outputStream.writeObject(new ServerOutputPackage(ServerCommands.SERVER_INFO, data));
						
						break;
					case SET_ROLE:
						break;
					default:
						break;
					}
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
