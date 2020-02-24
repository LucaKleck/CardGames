package cardgames.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cardgames.constants.CardGamesConstants;
import cardgames.games.Game;
import cardgames.games.GameData;
import cardgames.games.GameFactory;
import cardgames.server.ServerInfo;
import cardgames.server.ServerOutputPackage;

public class ClientServerConnector {
	private ExecutorService inputExecutor;
	private Socket clientSocket;
	ObjectOutputStream outputStream;
	
	private ServerInfo localServerInfo;
	private Client client;
	private Game localGame;
	@SuppressWarnings("unused")
	private InetAddress serverAddress;
	
	// connects to server and clientGameInstance instance 
	
	public ClientServerConnector(Client client, InetAddress serverAddress) {
		this.client = client;
		this.serverAddress = serverAddress;

		inputExecutor = Executors.newSingleThreadExecutor();
		// start connection
		try {
			clientSocket = new Socket(serverAddress, CardGamesConstants.PORT);
			outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
			
			inputExecutor.execute(new InputHandler(inputStream, this));
			
			// send connection request & data
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(client);
			outputStream.writeObject(new ClientOutputPackage(ClientCommands.CONNECT, data));
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
			// failed connection retry or exit.
		}
		
		
	}

	public void sendClientOutputPackage(ClientOutputPackage clientOutputPackage) {
		try {
			outputStream.writeObject(clientOutputPackage);
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
		private ClientServerConnector csc;
		
		public InputHandler(ObjectInputStream inputStream, ClientServerConnector csc) {
			this.inputStream = inputStream;
			this.csc = csc;
		}
		
		@Override
		public void run() {
			// receive data from server
			while(!clientSocket.isClosed()) {
				try {
					ServerOutputPackage sop = (ServerOutputPackage) inputStream.readObject();
					switch (sop.getCommand()) {
					case BAN:
						// ur banned LUL
						break;
					case CHAT_MESSAGE:
						// add to chat window
						break;
					case GAME_DATA:
						// send to game
						if(localGame == null) break;
						localGame.processServerData((GameData) sop.getData().get(0));
						break;
					case KICK:
						// KICKED KEKW
						break;
					case SERVER_MESSAGE:
						// add to chat but formatted
						break;
					case SHUTDOWN:
						// server gone 
						break;
					case START_GAME:
						// create game
						localGame = GameFactory.createGame(localServerInfo.getGameMode(), null, csc);
						break;
					case SERVER_INFO:
						// set local server Info
						localServerInfo = (ServerInfo) sop.getData().get(0);
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
		
		// add methods to send data to client public method wrappers in container class
		
	}
}
