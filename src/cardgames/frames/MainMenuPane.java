package cardgames.frames;

import java.awt.CardLayout;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cardgames.client.Client;
import cardgames.client.ClientServerConnector;
import cardgames.constants.CardGamesConstants;
import cardgames.server.Server;
import net.miginfocom.swing.MigLayout;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import java.awt.Component;

@SuppressWarnings("unused")
public class MainMenuPane extends CustomContentPane {
	private static final long serialVersionUID = -2045335928027275723L;

	private JButton createServer;
	private JButton joinServer;
	private JTextField txtIP;
	private JLabel lblHostIpempty;
	private JButton exit;
	private JTextField tfServerName;
	private JLabel lblServerName;
	private JLabel lblUserName;
	
	
	public MainMenuPane(CardPane containerJPanel) {
		super(containerJPanel);
		CardLayout c1 = (CardLayout) containerJPanel.getContentContainer().getLayout();
		setLayout(new MigLayout("", "[grow]", "[][][][]"));
		Random r = new Random();
		
		lblHostIpempty = new JLabel("Host Ip (empty == localHost)");
		lblHostIpempty.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(lblHostIpempty, "flowy,cell 0 0");
		
		txtIP = new JTextField(20);
		add(txtIP, "cell 0 0");
				
		lblUserName = new JLabel("Username:");
		lblUserName.setHorizontalTextPosition(SwingConstants.LEFT);
		lblUserName.setPreferredSize(new Dimension(66, 14));
		lblUserName.setMinimumSize(new Dimension(66, 14));
		lblUserName.setMaximumSize(new Dimension(66, 14));
		add(lblUserName, "flowy,cell 0 1");
		
		JTextField txtUsername = new JTextField();
		txtUsername.setText("User"+r.nextInt(Integer.MAX_VALUE));
		txtUsername.setColumns(20);
		add(txtUsername, "cell 0 1");
		
		lblServerName = new JLabel("Server Name:");
		add(lblServerName, "flowy,cell 0 2");
		
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
				Client client = new Client(txtUsername.getText(), InetAddress.getByName(ip));
				ClientServerConnector csc = new ClientServerConnector(client, getAddress());
				containerJPanel.getContentContainer().add(new Lobby(containerJPanel, csc), CustomContentPane.LOBBY);
				c1.show(containerJPanel.getContentContainer(), CustomContentPane.LOBBY);
				containerJPanel.revalidate();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		});
		add(joinServer, "flowx,cell 0 3,alignx left");
		
		
		tfServerName = new JTextField();
		add(tfServerName, "cell 0 2,alignx left");
		tfServerName.setText("server"+r.nextInt(Integer.MAX_VALUE));
		tfServerName.setColumns(20);
		
		createServer = new JButton("Create Server");
		createServer.addActionListener(e -> {
			String ip = null;
			try(final DatagramSocket socket = new DatagramSocket()){
				  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
				  ip = socket.getLocalAddress().getHostAddress();
			} catch (SocketException | UnknownHostException e1) {
				e1.printStackTrace();
			}
			try {
				Client client = new Client(txtUsername.getText(), InetAddress.getByName(ip));
				Server server = new Server(tfServerName.getText());
				containerJPanel.setServer(server);
				ClientServerConnector csc = new ClientServerConnector(client, getAddress());
				containerJPanel.getContentContainer().add(new Lobby(containerJPanel, csc), CustomContentPane.LOBBY);
				c1.show(containerJPanel.getContentContainer(), CustomContentPane.LOBBY);
				containerJPanel.revalidate();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		add(createServer, "cell 0 3,alignx left");
		
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
