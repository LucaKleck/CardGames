package core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import core.Client.ClientCommands;
import uno.PlayingField;
import uno.UnoCard;
import uno.UnoPlayingField;

public class Server implements Runnable {
	public static final int PORT = 9876;
	public static Server server;
	private static LinkedList<ServerInstance> players;
	private ExecutorService serverExecutor = Executors.newCachedThreadPool();
	private ServerSocket serverSocket;
	
	private final Player host;
	private final Lobby lobby;
	private PlayingField playingField;

	public Server(Player client, Lobby lobby) {
		server = this;
		host = client;
		this.lobby = lobby;
		if(players == null) {
			players = new LinkedList<ServerInstance>();
		}
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void setPlayingField(PlayingField pf) {
		playingField = pf;
	}
	
	public ServerInstance join(Socket clientSocket) {
		ServerInstance s = new ServerInstance(clientSocket);
		serverExecutor.execute(s);
		players.add(s);
		return s;
	}
	
	public boolean kick(Player p) {
		ServerInstance kick = null;
		for(ServerInstance s : players) {
			if(s.client.equals(p)) kick = s; 
		}
		if(playingField != null) playingField.kick(p);
		return players.remove(kick);
	}
	
	public void BroadcastToClients(ServerResponse srp) {
		for(ServerInstance s : players) {
			try {
				ObjectOutputStream clientOos = new ObjectOutputStream(s.clientSocket.getOutputStream());
				
				clientOos.writeObject(srp);
				
				clientOos.flush();
				clientOos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void SendToClient(ServerResponse srp, Player p) {
		for(ServerInstance s : players) {
			if(s.client.equals(p)) {
				try {
					ObjectOutputStream clientOos = new ObjectOutputStream(s.clientSocket.getOutputStream());
					
					clientOos.writeObject(srp);
					
					clientOos.flush();
					clientOos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	@Override
	public void run() {
		// Listens to connection offers and adds players
		while(true) {
			try {
				Socket clientSocket = serverSocket.accept();
				clientSocket.setKeepAlive(true);
				join(clientSocket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * Listens to Client and answers as needed
	 * @author Luca
	 */
	private class ServerInstance implements Runnable {
		private final Socket clientSocket;
		private Player client = null;
		ObjectInputStream clientOis = null;
		ObjectOutputStream clientOos = null;
		
		@SuppressWarnings({ "unchecked", "unused" })
		public ServerInstance(Socket clientSocket) {
			this.clientSocket = clientSocket;
				try {
					if(clientOis == null) {
						clientOis = new ObjectInputStream(clientSocket.getInputStream());
					}
					if(clientOos == null) {
						clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
					}
					ServerCommands sc = (ServerCommands) clientOis.readObject();
					
					LinkedList<Object> data = (LinkedList<Object>) clientOis.readObject();
					client = (Player) clientOis.readObject();
					
					clientOos.writeObject(new ServerResponse());
					clientOos.flush();
					
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
		}
		
		@SuppressWarnings("unused")
		@Override
		public void run() {
			while(true) {
				try {
					ClientCommands response = ClientCommands.defaultResponse;
					LinkedList<Object> sendData = new LinkedList<Object>();
					
					ServerCommands sc = null;
					sc = (ServerCommands) clientOis.readObject();
					
					@SuppressWarnings("unchecked")
					LinkedList<Object> data = (LinkedList<Object>) clientOis.readObject();
					Player source = (Player) clientOis.readObject();
					
					switch (sc) {
					case connectionCheck:
						break;
					case drawCard:
						if(playingField instanceof UnoPlayingField) {
							sendData.add( ((UnoPlayingField) playingField).drawCard() );
						}
						break;
					case drawCardForPlayer:
						if(playingField instanceof UnoPlayingField) {
							((UnoPlayingField) playingField).drawCard( (Player) data.get(0) );
						}
						break;
					case getCurrentCard:
						if(playingField instanceof UnoPlayingField) {
							sendData.add( ((UnoPlayingField) playingField).getCurrentCard() );
						}
						break;
					case getCurrentPlayer:
						if(playingField instanceof UnoPlayingField) {
							sendData.add( ((UnoPlayingField) playingField).getCurrentPlayer() );
						}
						break;
					case getGameMode:
						sendData.add(lobby.getGameMode());
						break;
					case getNextPlayer:
						if(playingField instanceof UnoPlayingField) {
							sendData.add( ((UnoPlayingField) playingField).getNextPlayer((Player) data.get(0)) );
						}
						break;
					case getPlacedCards:
						if(playingField instanceof UnoPlayingField) {
							sendData.add( ((UnoPlayingField) playingField).getPlacedCards() );
						}
						break;
					case getPlayerHand:
						if(playingField instanceof UnoPlayingField) {
							sendData.add( ((UnoPlayingField) playingField).getPlayerHand((Player) data.get(0)) );
						}
						break;
					case getPlayerList:
						if(playingField instanceof UnoPlayingField) {
							sendData.add( ((UnoPlayingField) playingField).getPlayerList() );
						}
						break;
					case getSelectedCard:
						if(playingField instanceof UnoPlayingField) {
							sendData.add( ((UnoPlayingField) playingField).getSelectedCard((Player) data.get(0)) );
						}
						break;
					case placeCard:
						if(playingField instanceof UnoPlayingField) {
							sendData.add( ((UnoPlayingField) playingField).placeCard( (UnoCard) data.get(0), (Player) data.get(1)) );
						}
						break;
					case setCardColor:
						if(playingField instanceof UnoPlayingField) {
							((UnoPlayingField) playingField).setCardColor((Player) data.get(0), (Integer) data.get(1) );;
						}
						break;
					case setPlayerHandSelectedCard:
						if(playingField instanceof UnoPlayingField) {
							response = ClientCommands.setCard;
							sendData.add(((UnoPlayingField) playingField).getPlayerHand(client).setSelectedCard( (UnoCard) data.get(0) ));
						}
						break;
					case addPlayer:
						if(playingField instanceof UnoPlayingField) {
							((UnoPlayingField) playingField).addPlayer(client);
						}
						break;
					case connect: // !! nothing !!
						break;
					default:
						break;
					}
					
					clientOos.writeObject(new ServerResponse(response, sendData));
					
					clientOos.flush();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
		
	}

	public enum ServerCommands {
		// setter
		setCardColor,
		setPlayerHandSelectedCard,
		
		// getter
		getPlayerList,
		getSelectedCard,
		getPlayerHand,
		getCurrentPlayer,
		getPlacedCards,
		getCurrentCard,
		getNextPlayer,
		getGameMode,
		
		// other
		addPlayer,
		placeCard,
		drawCardForPlayer,
		drawCard,
		connectionCheck,
		connect;
	}
	
	public static class ServerResponse implements Serializable {
		private static final long serialVersionUID = 4661296235340882947L;
		public final ClientCommands clientCommand;
		public final LinkedList<Object> data;
		public final Player source;
		
		public ServerResponse(ClientCommands clientCommand, LinkedList<Object> data) {
			this.clientCommand = clientCommand;
			this.data = data;
			source = Server.server.host;
		}

		public ServerResponse() {
			clientCommand = ClientCommands.defaultResponse;
			data = null;
			source = Server.server.host;
		}
		
		@Override
		public String toString() {
			if (data != null) {
				return clientCommand.name()+" source: "+ source + " data:" + data.toString();
			}
			return clientCommand.name()+" source: "+ source + " no data"; 
		}
		
	}

}
