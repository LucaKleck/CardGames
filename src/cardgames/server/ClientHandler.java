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
	private Server server; 	// host server
	
	
	public ClientHandler(Socket clientSocket, Server server) {
		inputExecutor = Executors.newSingleThreadExecutor();
		
		this.clientSocket = clientSocket;
		this.server = server;
		
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
						ArrayList<Object> data = cop.getData();
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
	
	public synchronized void sendServerOutputPackage(ServerOutputPackage serverOutputPackage) {
		try {
			System.out.println("server sent["+client+"]: "+serverOutputPackage);
			outputStream.writeObject(serverOutputPackage);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Client getClient() {
		return client;
	}

	private class InputHandler implements Runnable {
		private ObjectInputStream inputStream;
		
		public InputHandler(ObjectInputStream inputStream) {
			this.inputStream = inputStream;
		}
		
		@Override
		public void run() {
			
			while(!clientSocket.isClosed()) {
				try {
					ClientOutputPackage cop = (ClientOutputPackage) inputStream.readObject();
					System.out.println("server received cop["+client+"]: "+cop);
					switch (cop.getCommand()) {
					case CHAT:
						break;
					case CLIENT_LIST:
						ArrayList<Object> data1 = new ArrayList<Object>();
						data1.add(server.getClients());
						outputStream.writeObject(new ServerOutputPackage(ServerCommands.CLIENT_LIST, data1));
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
						ArrayList<Object> data5 = new ArrayList<Object>();
						data5.add(server.getServerInfo()); 	
						outputStream.writeObject(new ServerOutputPackage(ServerCommands.SERVER_INFO, data5));
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
					System.exit(0);
				}
			}
		}
		
	}

}
