package view.login;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.AbstractClient;
import constant.CommonClass;
import constant.NetConstantClient;
import constant.RegExConstant;
import constant.ViewConstant;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

@SuppressWarnings("unused")
public class LoginNetworkConfigDialog extends JDialog
		implements CommonClass, RegExConstant, ViewConstant, NetConstantClient {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTextField serverIPTF;
	private JTextField serverPortTF;
	private LoginDialog login;
	private AbstractClient client;
	private JTextField HostnameTF;
	private JLabel lblServerIP;
	private JLabel lblServerPort;
	private JLabel lblServerHostName;
	private JLabel lblServerDomainName;
	private JTextField domainNameTF;

	private String defaultIP = "127.0.0.1";

	/**
	 * Create the dialog.
	 */
	public LoginNetworkConfigDialog(AbstractClient client, LoginDialog login) {
		setTitle("Network Config");
		this.login = login;
		this.client = client;

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);// 仅仅关闭当前窗体,使用EXIT会抛出异常
		setBounds(100, 100, 440, 223);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 434, 261);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(null);
		setContentPane(contentPanel);

		lblServerIP = new JLabel("Server IP:");
		lblServerIP.setBounds(10, 10, 161, 21);
		contentPanel.add(lblServerIP);

		serverIPTF = new JTextField();
		serverIPTF.setText(defaultIP);
		// if (client.getServerIP() == null) {
		// serverIPTF.setText(DEFAULT_SERVER_TEST_IP);
		// }
		serverIPTF.setBounds(181, 10, 99, 21);
		serverIPTF.setColumns(10);
		contentPanel.add(serverIPTF);

		lblServerPort = new JLabel("Server Port:");
		lblServerPort.setBounds(10, 103, 119, 21);
		contentPanel.add(lblServerPort);

		serverPortTF = new JTextField();
		serverPortTF.setText("20000");
		serverPortTF.setBounds(181, 103, 99, 21);
		serverPortTF.setColumns(10);
		serverPortTF.setDocument(new limitedPortTFDocument(serverPortTF, DEFAULT_PORT_MAXLEN));
		if (client.getServerPort() == DEFAULT_SERVER_UNSET_PORT) {
			serverPortTF.setText("20000");
		}
		contentPanel.add(serverPortTF);

		JButton btnSubmit = new JButton("Confirm");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// port
				String portText = serverPortTF.getText();
				if (!"".equals(portText)) {
					int port = Integer.valueOf(portText).intValue();
					if (port < 0 || port > 65535) {
						JOptionPane.showMessageDialog(null, "Server's port is illegal!", WARNING_TITLE,
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					client.setServerPort(port);// 设置服务器端口
					login.portLabel.setText(String.valueOf(port));
				}

				// IP
				String IP = serverIPTF.getText();
				if (!"".equals(IP)) {
					if (!IP.matches(IPv4REGEXjb)) {
						JOptionPane.showMessageDialog(null, "Server's IP is illegal!", "Server IP Error",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					client.setServerIP(IP);// 设置服务器IP
					login.IPLabel.setText(IP);// 在登陆界面显示
				}

				// hostname
				String Hostname = HostnameTF.getText();
				if (!"".equals(Hostname)) {
					client.setServerHostname(Hostname);
					login.hostnameLabel.setText(Hostname);
				}

				// domain Name
				String domainName = domainNameTF.getText();
				if (!"".equals(domainName)) {
					if (!domainName.matches(DOMAIN_NAME_REGEX)) {
						JOptionPane.showMessageDialog(null, "That is not a domain name", PROMPT_TITLE,
								INFO_MESSAGE_JOPT);
						return;
					}
					client.setServerDomainName(domainName);
					login.domainNameLabel.setText(domainName);
				}

				LoginNetworkConfigDialog.this.dispose();
			}
		});
		btnSubmit.setBounds(10, 134, 93, 23);
		contentPanel.add(btnSubmit);

		lblServerHostName = new JLabel("Server Hostname:");
		lblServerHostName.setBounds(10, 41, 161, 21);
		contentPanel.add(lblServerHostName);

		HostnameTF = new JTextField();
		HostnameTF.setBounds(181, 41, 150, 21);
		HostnameTF.setColumns(10);

		String defaultDomainName = login.hostnameLabel.getText();
		if (!"".equals(defaultDomainName)) {
			HostnameTF.setText(defaultDomainName);
		}
		contentPanel.add(HostnameTF);

		lblServerDomainName = new JLabel("Server Domain Name:");
		lblServerDomainName.setBounds(10, 72, 161, 21);
		contentPanel.add(lblServerDomainName);

		domainNameTF = new JTextField();
		domainNameTF.setBounds(181, 72, 150, 21);
		contentPanel.add(domainNameTF);
		domainNameTF.setColumns(10);

		this.getRootPane().setDefaultButton(btnSubmit);// 按下回车时相当于按钮
	}
}
