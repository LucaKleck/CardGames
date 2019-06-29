package core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import core.Server.ServerCommands;
import core.Server.ServerResponse;
import uno.PlayingField;

public class Client {
	public static Client clientObject;
	
	private ObjectInputStream clientOis = null;
	private ObjectOutputStream clientOos = null;
	
	public final Player client;
	private Socket clientSocket = null;
	@SuppressWarnings("unused")
	private PlayingField pf;
	
	public Client(Player client, InetAddress serverAddress) {
		clientObject = this;
		this.client = client;
		// start connection
		try {
			clientSocket = new Socket(serverAddress, Server.PORT);
			clientOos = new ObjectOutputStream(clientSocket.getOutputStream());
			clientOis = new ObjectInputStream(clientSocket.getInputStream());
			LinkedList<Object> data = new LinkedList<Object>();
			data.add(client);
			new ServerRequest(ServerCommands.connect, data, client).run();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void setPlayingField(PlayingField pf) {
		this.pf = pf;
	}
	
	/**
	 * Designed to send data to server
	 * @param serverCommand
	 * @param data
	 * @return
	 */
	public ServerResponse createServerRequest(ServerCommands serverCommand, LinkedList<Object> data) {
		ServerRequest srq = new ServerRequest(serverCommand, data, client);
		srq.run();
		return srq.sr;
	}
	
	public enum ClientCommands {
		updateCards,
		change,
		setCard,
		defaultResponse;
	}
	
	private class ServerRequest implements RunnableFuture<ServerResponse> {

		ServerResponse sr;
		
		ServerCommands serverCommand;
		LinkedList<Object> data;
		Player source;
		private boolean isDone = false;
		private boolean isCancelled = false;
		
		public ServerRequest(ServerCommands serverCommand, LinkedList<Object> data, Player source) {
			this.serverCommand = serverCommand;
			this.data = data;
			this.source = source;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			isCancelled = true;
			return true;
		}

		@Override
		public boolean isCancelled() {
			return isCancelled;
		}

		@Override
		public boolean isDone() {
			return isDone;
		}

		@Override
		public ServerResponse get() throws InterruptedException, ExecutionException {
			return sr;
		}

		@Override
		public ServerResponse get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			return sr;
		}

		@Override
		public void run() {
			try {
				clientOos.writeObject(serverCommand);
				clientOos.writeObject(data);
				clientOos.writeObject(source);
				
				clientOos.flush();
				
				//System.out.println("get: "+serverCommand);
				sr = (ServerResponse) clientOis.readObject();
				//System.out.println(serverCommand+" - response: "+sr.toString());
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				isCancelled = true;
				isDone = true;
				return;
			}
			isDone = true;
		}
		
		@Override
		public String toString() {
			if(data != null) {
				return serverCommand + " p:"+client+ " d:"+data.toString();
			}
			return serverCommand + " p:"+client;
		}
		
	}

}
