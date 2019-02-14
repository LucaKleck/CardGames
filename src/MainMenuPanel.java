import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

public class MainMenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JButton btnJoinAsClient;
	private JButton btnStartAsHost;
	private JLabel lblUserName;
	private JLabel lblHostIpempty;
	private JTextField txtIP;

	public MainMenuPanel(MainJFrame container) {
		setLayout(new MigLayout("", "[150px,fill][95px,grow][95px]", "[23px][][100%][]"));
		
		Random r = new Random();
		
		lblUserName = new JLabel("User Name:");
		add(lblUserName, "flowy,cell 0 0");
		
		JTextField txtUsername = new JTextField();
		txtUsername.setText("User"+r.nextInt());
		add(txtUsername, "cell 0 0,alignx left,aligny center");
		txtUsername.setColumns(10);
		
		lblHostIpempty = new JLabel("Host Ip (empty == localHost)");
		add(lblHostIpempty, "flowy,cell 1 0 2 1");
		
		txtIP = new JTextField(35);
		add(txtIP, "cell 1 0,alignx left");
		
		btnStartAsHost = new JButton("Start as Host");
		btnStartAsHost.addActionListener(e -> {
			try {
				container.add(new UnoPlayingFieldPanel(new Player(txtUsername.getText()), false, getAddress()));
				container.remove(this);
				container.revalidate();
			} catch (UnknownHostException e1) {
			}
		});
		add(btnStartAsHost, "cell 0 3,alignx left,aligny top");
		
		btnJoinAsClient = new JButton("Join as Client");
		btnJoinAsClient.addActionListener(e -> {
			try {
				container.add(new UnoPlayingFieldPanel(new Player(txtUsername.getText()), true, getAddress() ));
				container.remove(this);
				container.revalidate();
			} catch (UnknownHostException e1) {
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
