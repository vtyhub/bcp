package view;

import javax.swing.JPanel;

import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import constant.CommonClass;
import constant.NetConstant;
import constant.RegExConstant;
import constant.ViewConstant;
import heartbeat.HeartbeatDetection;
import method.CommonMethod;
import network.ErrorResponseException;
import network.Hello;
import network.PutMessageTask;
import network.TakeMessageTask;
import server_s.AbstractServerS.UseWhatToConnect;
import server_s.ServerS;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ButtonGroup;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.awt.event.ItemEvent;

public class ConnectToCPanel extends JPanel implements RegExConstant, ViewConstant, CommonClass, NetConstant {

	private static final long serialVersionUID = 1L;

	private ServerS S;

	private JTextField IPTF;
	private JTextField hostnameTF;
	private JTextField domainNameTF;
	private final ButtonGroup cInfoGroup = new ButtonGroup();

	private JRadioButton IPRadio;

	private JRadioButton hostnameRadio;

	private JRadioButton domainNameRadio;

	private JComponent[] IPArray;
	private JComponent[] hostnameArray;
	private JComponent[] domainNameArray;
	private JTextField portTF;

	private JLabel portLabel;

	private JLabel connectionStatusLabel;

	/**
	 * Create the panel.
	 */
	public ConnectToCPanel(ServerS s) {
		this.S = s;

		setLayout(null);

		JLabel lblCsIp = new JLabel("C's IP:");
		lblCsIp.setBounds(10, 10, 130, 23);
		add(lblCsIp);

		IPTF = new JTextField();
		IPTF.setText("127.0.0.1");
		IPTF.setBounds(150, 9, 150, 24);
		add(IPTF);
		IPTF.setColumns(10);

		JLabel lblCsHostname = new JLabel("C's Hostname:");
		lblCsHostname.setBounds(10, 43, 130, 23);
		add(lblCsHostname);

		JLabel lblCsDomainName = new JLabel("C's Domain Name:");
		lblCsDomainName.setBounds(10, 76, 130, 23);
		add(lblCsDomainName);

		hostnameTF = new JTextField();
		hostnameTF.setBounds(150, 43, 150, 24);
		add(hostnameTF);
		hostnameTF.setColumns(10);

		domainNameTF = new JTextField();
		domainNameTF.setBounds(150, 76, 150, 24);
		add(domainNameTF);
		domainNameTF.setColumns(10);

		JLabel lblCIpStatus = new JLabel("C's IP:");
		lblCIpStatus.setBounds(353, 10, 130, 23);
		add(lblCIpStatus);

		JLabel lblCHostnameStatus = new JLabel("C's Hostname:");
		lblCHostnameStatus.setBounds(353, 43, 130, 23);
		add(lblCHostnameStatus);

		JLabel lblCDomainNameStatus = new JLabel("C's Domain Name:");
		lblCDomainNameStatus.setBounds(353, 76, 130, 23);
		add(lblCDomainNameStatus);

		JLabel IPLabel = new JLabel("");
		IPLabel.setBounds(493, 10, 100, 23);
		add(IPLabel);

		JLabel hostnameLabel = new JLabel("");
		hostnameLabel.setBounds(493, 43, 100, 23);
		add(hostnameLabel);

		JLabel domainNameLabel = new JLabel("");
		domainNameLabel.setBounds(493, 76, 100, 23);
		add(domainNameLabel);

		JLabel lblConnectionStatus = new JLabel("Connection Status:");
		lblConnectionStatus.setBounds(353, 142, 150, 23);
		add(lblConnectionStatus);

		connectionStatusLabel = new JLabel("");
		connectionStatusLabel.setBounds(519, 142, 100, 23);
		add(connectionStatusLabel);

		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MyData<String> host = new MyData<String>();
				switch (ConnectToCPanel.this.S.getForwardlyUseWhat()) {
				case Hostname:
					host.setData(hostnameLabel.getText());
					break;
				case DomainName:
					host.setData(domainNameLabel.getText());
					break;
				default:
					host.setData(IPLabel.getText());
					break;
				}

				if (host.toString() == null || "".equals(host.toString())) {
					JOptionPane.showMessageDialog(null, "Selected server's info is not set!", WARNING_TITLE,
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				Integer port = Integer.valueOf(portLabel.getText());

				InetSocketAddress addr = new InetSocketAddress(host.toString(), port);
				Socket socket = new Socket();

				MyData<Boolean> bool = new MyData<Boolean>(false);
				try {
					socket.connect(addr);

					new Thread(() -> {
						try {
							Thread.sleep(DEFAULT_TIMEOUT);
							if (!bool.getData()) {
								socket.close();
							}
						} catch (InterruptedException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}).start();

					ObjectOutputStream obtoc = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream obfromc = new ObjectInputStream(socket.getInputStream());
					bool.setData(true);

					Hello.forwardlyAloha(obfromc, obtoc);

					// 认证通过，成为正式连接
					socket.setKeepAlive(true);

					HeartbeatDetection heartbeatDetection = new HeartbeatDetection(S);
					LinkedBlockingQueue<Object> socketQueue = new LinkedBlockingQueue<>();
					PutMessageTask putTask = new PutMessageTask(obfromc, socketQueue, heartbeatDetection);
					TakeMessageTask takeTask = new TakeMessageTask(S, obtoc, socketQueue);

					S.setSocketQueue(socketQueue);
					S.setForwardlyObToC(obtoc);
					S.setForwardlyObFromC(obfromc);
					S.setForwardlySocket(socket);

					S.setHeartbeatDetectionTask(heartbeatDetection);

					new Thread(putTask).start();
					new Thread(takeTask).start();
					new Thread(heartbeatDetection).start();

					// ForwardlyConnectToC forwardly = new ForwardlyConnectToC(S, socket, obfromc,
					// obtoc,
					// heartbeatDetection);
					// S.setForwardlyCommunicateTask(forwardly);
					// new Thread(forwardly).start();

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Connection failed!", WARNING_TITLE,
							JOptionPane.WARNING_MESSAGE);
					return;
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (ErrorResponseException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(ConnectToCPanel.this, "Error response: " + e1.getMessage(),
							ERROR_TITLE, ERROR_MESSAGE_JOPT);
					return;
				}
				// 连接成功
				JOptionPane.showMessageDialog(ConnectToCPanel.this, "Successfully connected!", SUCCESS_TITLE,
						INFO_MESSAGE_JOPT);
			}
		});
		btnConnect.setBounds(187, 142, 113, 23);
		add(btnConnect);

		JButton btnSet = new JButton("Set");
		btnSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String IP = IPTF.getText();
				if (IP != null && !"".equals(IP)) {
					if (!IP.matches(IPv4REGEXjb)) {
						JOptionPane.showMessageDialog(ConnectToCPanel.this, "IPv4 address is illegal!", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					} else {
						IPLabel.setText(IP);
					}
				}

				String hostname = hostnameTF.getText();
				if (hostname != null && !"".equals(hostname)) {
					hostnameLabel.setText(hostname);
				}

				String domainName = domainNameTF.getText();
				if (domainName != null && !"".equals(domainName)) {
					if (!domainName.matches(DOMAIN_NAME_REGEX)) {
						JOptionPane.showMessageDialog(ConnectToCPanel.this, "Domain name is illegal!", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					} else {
						domainNameLabel.setText(domainName);
					}
				}

				String portText = portTF.getText();
				if (portText != null && !"".equals(portText)) {
					try {
						Integer port = Integer.valueOf(portText);
						if (port < 0 || port > 65535) {
							throw new NumberFormatException();
						} else {
							portLabel.setText(portText);
						}
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(ConnectToCPanel.this, "Port number is illegal!", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					}
				}

			}
		});
		btnSet.setBounds(10, 142, 113, 23);
		add(btnSet);

		hostnameRadio = new JRadioButton("");
		hostnameRadio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (hostnameRadio.isSelected()) {
					CommonMethod.setComponentsEnable(hostnameArray);
					CommonMethod.setComponentsDisable(IPArray);
					CommonMethod.setComponentsDisable(domainNameArray);
					s.setForwardlyUseWhat(UseWhatToConnect.Hostname);
				}
			}
		});
		cInfoGroup.add(hostnameRadio);
		hostnameRadio.setBounds(320, 43, 23, 23);
		add(hostnameRadio);

		domainNameRadio = new JRadioButton("");
		domainNameRadio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (domainNameRadio.isSelected()) {
					CommonMethod.setComponentsEnable(domainNameArray);
					CommonMethod.setComponentsDisable(IPArray);
					CommonMethod.setComponentsDisable(hostnameArray);
					s.setForwardlyUseWhat(UseWhatToConnect.DomainName);
				}
			}
		});
		cInfoGroup.add(domainNameRadio);
		domainNameRadio.setBounds(320, 76, 23, 23);
		add(domainNameRadio);

		IPArray = new JComponent[] { lblCIpStatus, IPLabel };
		hostnameArray = new JComponent[] { lblCHostnameStatus, hostnameLabel };
		domainNameArray = new JComponent[] { lblCDomainNameStatus, domainNameLabel };

		IPRadio = new JRadioButton("");
		IPRadio.setSelected(true);
		IPRadio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (IPRadio.isSelected()) {
					CommonMethod.setComponentsEnable(IPArray);
					CommonMethod.setComponentsDisable(hostnameArray);
					CommonMethod.setComponentsDisable(domainNameArray);
					s.setForwardlyUseWhat(UseWhatToConnect.IP);
				}
			}
		});
		cInfoGroup.add(IPRadio);
		IPRadio.setBounds(320, 10, 23, 23);
		add(IPRadio);

		JLabel lblCsPort = new JLabel("C's Port:");
		lblCsPort.setBounds(10, 109, 130, 23);
		add(lblCsPort);

		portTF = new JTextField();
		portTF.setBounds(150, 109, 86, 23);
		portTF.setDocument(new limitedPortTFDocument(portTF, DEFAULT_PORT_MAXLEN));
		add(portTF);
		portTF.setColumns(10);
		portTF.setText("2000");

		JLabel lblCPort = new JLabel("C's Port:");
		lblCPort.setBounds(353, 109, 130, 23);
		add(lblCPort);

		portLabel = new JLabel("");
		portLabel.setBounds(493, 109, 100, 23);
		add(portLabel);

	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);

		int firstverticalline = IPTF.getX() + IPTF.getWidth() + 10;
		g.drawLine(firstverticalline, 0, firstverticalline, getHeight());
	}

	public static void main(String[] args) {

	}
}
