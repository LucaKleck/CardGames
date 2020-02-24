package cardgames.frames;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JTextField;

import cardgames.client.Client;

@SuppressWarnings("unused")
public class MainMenuPane extends CustomContentPane {
	private static final long serialVersionUID = -2045335928027275723L;

	private JButton createServer;
	private JButton joinServer;
	private JTextField txtIP;
	private JButton exit;
	
	
	public MainMenuPane() {
		super();
		Random r = new Random();
		
		JTextField txtUsername = new JTextField();
		txtUsername.setText("User"+r.nextInt(Integer.MAX_VALUE));

		createServer = new JButton("Create Server");
		createServer.addActionListener(e -> {
			// create lobby 
			String ip = null;
			try(final DatagramSocket socket = new DatagramSocket()){
				  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
				  ip = socket.getLocalAddress().getHostAddress();
			} catch (SocketException | UnknownHostException e1) {
				e1.printStackTrace();
			}
			try {
				Client p = new Client(txtUsername.getText(), InetAddress.getByName(ip));
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		});
		
		joinServer = new JButton("Join Server");
		joinServer.addActionListener(e -> {
			try {
				String ip = null;
				try(final DatagramSocket socket = new DatagramSocket()){
					  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
					  ip = socket.getLocalAddress().getHostAddress();
				} catch (SocketException | UnknownHostException e1) {
					e1.printStackTrace();
				}
				Client p = new Client(txtUsername.getText(), InetAddress.getByName(ip));
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		});
		exit = new JButton("Exit");
		
		add(createServer);
		add(joinServer);
		add(exit);
	}
}
