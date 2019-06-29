package core.frames;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.Lobby;
import core.Player;
import net.miginfocom.swing.MigLayout;
import uno.frames.UnoPlayingFieldPanel;

public class MainMenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JButton btnJoinAsClient;
	private JButton btnStartAsHost;
	private JLabel lblUserName;
	private JLabel lblHostIpempty;
	private JTextField txtIP;

	public MainMenuPanel(MainJFrame container) {
		setLayout(new MigLayout("", "[]", "[]"));
		
		Random r = new Random();
		
		lblUserName = new JLabel("User Name:");
		add(lblUserName, "flowy,cell 0 0");
		
		JTextField txtUsername = new JTextField();
		txtUsername.setText("User"+r.nextInt(Integer.MAX_VALUE));
		add(txtUsername, "cell 0 0,alignx left,aligny center");
		txtUsername.setColumns(10);
		
		lblHostIpempty = new JLabel("Host Ip (empty == localHost)");
		add(lblHostIpempty, "flowy,cell 1 0 2 1");
		
		txtIP = new JTextField(35);
		add(txtIP, "cell 1 0,alignx left");
		
		btnStartAsHost = new JButton("Create Lobby");
		btnStartAsHost.addActionListener(e -> {
			// create lobby 
			String ip = null;
			try(final DatagramSocket socket = new DatagramSocket()){
				  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
				  ip = socket.getLocalAddress().getHostAddress();
			} catch (SocketException | UnknownHostException e1) {
				e1.printStackTrace();
			}
			try {
				Player p = new Player(txtUsername.getText(), InetAddress.getByName(ip), false);
				new Lobby(p);
				container.remove(this);
				container.add(new UnoPlayingFieldPanel(p));
				container.revalidate();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		});
		add(btnStartAsHost, "cell 0 3,alignx left,aligny top");
		
		btnJoinAsClient = new JButton("Join Lobby");
		btnJoinAsClient.addActionListener(e -> {
			try {
				String ip = null;
				try(final DatagramSocket socket = new DatagramSocket()){
					  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
					  ip = socket.getLocalAddress().getHostAddress();
				} catch (SocketException | UnknownHostException e1) {
					e1.printStackTrace();
				}
				Player p = new Player(txtUsername.getText(), InetAddress.getByName(ip), true);
				new Lobby(p, getAddress());
				container.remove(this);
				container.add(new UnoPlayingFieldPanel(p));
				container.revalidate();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		});
		add(btnJoinAsClient, "cell 2 3,alignx left,aligny top");
	}
	
	private InetAddress getAddress() throws UnknownHostException {
		if(txtIP.getText().isEmpty()) {
			return InetAddress.getLocalHost();
		} else {
			InetAddress host = null;
			host = InetAddress.getByName(txtIP.getText());
			return host;
		}
		
	}
}
