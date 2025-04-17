package view.login;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.Client;
import client.AbstractClient.UseWhatToConnect;
import constant.CommonClass;
import constant.CryptoConstant;
import constant.NetConstant;
import constant.ViewConstant;
import method.CommonMethod;
import network.ErrorResponseException;
import network.HeartbeatThread;
import network.Hello;
import view.MainUI;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class LoginDialog extends JDialog implements NetConstant, ViewConstant, CommonClass, CryptoConstant {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTextField usernameTF;
	private JPasswordField passwordPF;
	private Client client;

	private final ButtonGroup useWhatButtonGroup = new ButtonGroup();
	private JRadioButton useHostnameRadio;
	private JRadioButton useDomainNameRadio;
	private JLabel lblServerIp;
	private JLabel lblServerPort;

	JLabel hostnameLabel;
	JLabel IPLabel;
	JLabel portLabel;
	JLabel domainNameLabel;

	private JComponent[] useHostname;
	private JComponent[] useDomainName;
	private JComponent[] useIP;

	public static void main(String[] args) {
		new LoginDialog(new Client()).setVisible(true);
	}

	// ----------- get set------------------
	public JTextField getUsernameTF() {
		return usernameTF;
	}

	public void setUsernameTF(JTextField usernameTF) {
		this.usernameTF = usernameTF;
	}

	// --------- get set--------------------
	/**
	 * Create the dialog.
	 */
	public LoginDialog(Client client) {
		this.client = client;

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("Login");
		setBounds(500, 200, 562, 380);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel lblUserName = new JLabel("User Name:");
		lblUserName.setBounds(10, 41, 85, 21);
		contentPanel.add(lblUserName);

		usernameTF = new JTextField();
		usernameTF.setBounds(105, 40, 113, 21);
		contentPanel.add(usernameTF);
		usernameTF.setColumns(10);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(10, 72, 85, 21);
		contentPanel.add(lblPassword);

		passwordPF = new JPasswordField();
		passwordPF.setBounds(105, 72, 113, 21);
		contentPanel.add(passwordPF);

		JButton btnNetworkConfig = new JButton("Network Config");
		btnNetworkConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!"".equals(hostnameLabel.getText())) {
					new LoginNetworkConfigDialog(client, LoginDialog.this).setVisible(true);
				} else {
					new LoginNetworkConfigDialog(client, LoginDialog.this).setVisible(true);
				}

			}
		});
		btnNetworkConfig.setBounds(10, 10, 208, 21);
		contentPanel.add(btnNetworkConfig);

		lblServerIp = new JLabel("Server IP:");
		lblServerIp.setBounds(259, 10, 157, 21);
		contentPanel.add(lblServerIp);

		lblServerPort = new JLabel("Server Port:");
		lblServerPort.setBounds(228, 104, 105, 23);
		contentPanel.add(lblServerPort);

		IPLabel = new JLabel("");
		IPLabel.setBounds(426, 10, 100, 21);
		contentPanel.add(IPLabel);

		portLabel = new JLabel("");
		portLabel.setBounds(347, 104, 50, 23);
		contentPanel.add(portLabel);

		JButton btnRegister = new JButton("Register  Page");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (client.getServerIP() == null && client.getServerHostname() == null) {
					JOptionPane.showMessageDialog(null, "Server's info is not set!", WARNING_TITLE,
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				new RegisterDialog(client).setVisible(true);
			}
		});
		btnRegister.setBounds(363, 297, 163, 23);
		contentPanel.add(btnRegister);

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLogin.setEnabled(false);

				MyData<String> host = new MyData<String>();
				switch (client.getUseWhat()) {
				case Hostname:
					host.setData(client.getServerHostname());
					break;
				case DomainName:
					host.setData(client.getServerDomainName());
					break;
				default:
					host.setData(client.getServerIP());
					break;
				}

				if (host.getData() == null || "".equals(host.toString())) {
					JOptionPane.showMessageDialog(null, "Server's info is not set!", WARNING_TITLE,
							JOptionPane.WARNING_MESSAGE);
					btnLogin.setEnabled(true);
					return;
				}

				String username = usernameTF.getText();
				String password = new String(passwordPF.getPassword());

				new Thread(() -> {

					// 初始化网络参数
					Socket socket = new Socket();
					InetSocketAddress addr = new InetSocketAddress(host.toString(), client.getServerPort());
					ObjectOutputStream obtoc = null;
					ObjectInputStream obfromc = null;
					try {
						socket.connect(addr);
						MyData<Boolean> end = new MyData<Boolean>(true);
						new Thread(() -> {
							try {
								Thread.sleep(DEFAULT_HEARTBEAT_TIMEOUT);
								// 甚至无需比较，直接根据5秒后end是否为0来决定了
								if (end.getData()) {
									socket.close();
								}
							} catch (InterruptedException | IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}).start();
						obtoc = new ObjectOutputStream(socket.getOutputStream());
						obfromc = new ObjectInputStream(socket.getInputStream());
						end.setData(false);

					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Format error," + client.getUseWhat() + " : ", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
						btnLogin.setEnabled(true);
						try {
							if (socket != null)
								socket.close();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						return;
					} catch (ConnectException e1) {
						JOptionPane.showMessageDialog(null, "Connection failed!", ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE);
						btnLogin.setEnabled(true);
						try {
							if (socket != null)
								socket.close();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						return;
					} catch (SocketException e2) {
						// TODO: handle exception
						JOptionPane.showMessageDialog(null, "Connection failed!", ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE);
						btnLogin.setEnabled(true);
						try {
							if (socket != null)
								socket.close();
						} catch (IOException e22) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						return;
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "IO Error!", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
						btnLogin.setEnabled(true);
						try {
							if (socket != null)
								socket.close();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						return;
					}

					// 发送登录请求
					try {
						Hello.forwardlyHello(obfromc, obtoc);
						obtoc.writeInt(CLIENT_LOGIN);
						obtoc.flush();
						int response = obfromc.readInt();
						switch (response) {
						case DB_UNCONNECTED:
							JOptionPane.showMessageDialog(null, "Server didn't connect to database!", ERROR_TITLE,
									ERROR_MESSAGE_JOPT);
							btnLogin.setEnabled(true);
							socket.close();
							return;
						case LOGIN_DENIED:
							JOptionPane.showMessageDialog(null, "Server doesn't support login now!", PROMPT_TITLE,
									INFO_MESSAGE_JOPT);
							btnLogin.setEnabled(true);
							socket.close();
							return;
						case LOGIN_PERMITTED:
							break;
						default:
							JOptionPane.showMessageDialog(null, "Error response: " + response, ERROR_TITLE,
									ERROR_MESSAGE_JOPT);
							btnLogin.setEnabled(true);
							socket.close();
							return;
						}
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						btnLogin.setEnabled(true);
						try {
							socket.close();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						return;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "IO Error!", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
						btnLogin.setEnabled(true);
						try {
							socket.close();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						return;
					} catch (ErrorResponseException e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Error response: " + e1.getMessage(), ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
						btnLogin.setEnabled(true);
						try {
							socket.close();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						return;
					}

					// 发送登陆数据
					try {
						MessageDigest digest = MessageDigest.getInstance(DEFAULT_HASH_ALGORITHM);
						byte[] hashed1pwdbytes = digest.digest(password.getBytes(DEFAULT_CHARSET));
						String hashed1pwd = CommonMethod.byteToHexStr(hashed1pwdbytes);

						String[] user = { username, hashed1pwd };
						obtoc.writeObject(user);

					} catch (NoSuchAlgorithmException e1) {
						// TODO Auto-generated catch block
						btnLogin.setEnabled(true);
						try {
							socket.close();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						return;
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						btnLogin.setEnabled(true);
						try {
							socket.close();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						return;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						btnLogin.setEnabled(true);
						try {
							socket.close();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						return;
					}

					// 获取登陆结果
					try {
						int loginResult = obfromc.readInt();
						switch (loginResult) {
						case LOGIN_NOSUCHUSER:
							JOptionPane.showMessageDialog(null, "Username or password is wrong!", ERROR_TITLE,
									ERROR_MESSAGE_JOPT);
							break;
						case LOGIN_FAILED:
							JOptionPane.showMessageDialog(null, "Network status error!", ERROR_TITLE,
									ERROR_MESSAGE_JOPT);
							break;
						case LOGIN_SUCCEED:
							// 设置必要通信参数，登陆成功才能有设置的意义
							socket.setKeepAlive(true);
							LoginDialog.this.client.setSocket(socket);
							client.initServerInfo(socket, client.getServerDomainName());
							client.setUsername(username);
							client.setObtoc(obtoc);
							client.setObfromc(obfromc);
							HeartbeatThread heartbeatThread = new HeartbeatThread(client);
							client.setHeartbeat(heartbeatThread);
							heartbeatThread.start();

							MainUI mainUI = new MainUI(client);
							mainUI.setTitle("Client " + username);
							client.setMainui(mainUI);
							LoginDialog.this.dispose();
							mainUI.setVisible(true);
							break;
						default:
							JOptionPane.showMessageDialog(null, "Error response: " + loginResult, ERROR_TITLE,
									ERROR_MESSAGE_JOPT);
							break;
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						btnLogin.setEnabled(true);
					}

				}).start();

			}
		});
		btnLogin.setBounds(10, 103, 93, 23);
		contentPanel.add(btnLogin);

		JButton btnTestConnect = new JButton("Test Connect");
		btnTestConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MyData<String> host = new MyData<String>();
				switch (client.getUseWhat()) {
				case IP:
					host.setData(client.getServerIP());
					break;
				case Hostname:
					host.setData(client.getServerHostname());
					break;
				case DomainName:
					host.setData(client.getServerDomainName());
					break;
				default:
					JOptionPane.showMessageDialog(null, "A connection mode must be chosen", WARNING_TITLE,
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				if (host.toString() == null) {
					JOptionPane.showMessageDialog(null, "The selected connection mode's information was not set!",
							WARNING_TITLE, JOptionPane.WARNING_MESSAGE);
					return;
				}

				new Thread(() -> {
					btnTestConnect.setEnabled(false);
					InetSocketAddress socketaddr = new InetSocketAddress(host.toString(), client.getServerPort());
					Socket socket = new Socket();
					try {

						socket.connect(socketaddr, DEFAULT_HEARTBEAT_TIMEOUT);
						MyData<Boolean> end = new MyData<Boolean>(true);
						new Thread(() -> {
							try {
								Thread.sleep(DEFAULT_HEARTBEAT_TIMEOUT);
								// 甚至无需比较，直接根据5秒后end是否为0来决定了
								if (end.getData()) {
									socket.close();
								}
							} catch (InterruptedException | IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}).start();
						ObjectOutputStream obToC = new ObjectOutputStream(socket.getOutputStream());
						ObjectInputStream obFromC = new ObjectInputStream(socket.getInputStream());
						end.setData(false);

						Hello.forwardlyHello(obFromC, obToC);
						obToC.writeInt(CLIENT_TEST);
						obToC.flush();
						int returncode = obFromC.readInt();
						if (returncode == DB_UNCONNECTED) {
							JOptionPane.showMessageDialog(null, "Server didn't connect to database!", WARNING_TITLE,
									WARNING_MESSAGE_JOPT);
							return;
						}
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Invalid IP,hostname or domain name!", ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE);
						return;
					} catch (ConnectException e1) {
						JOptionPane.showMessageDialog(null, "Connection failed!", ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE);
						return;
					} catch (SocketException e2) {
						// TODO: handle exception
						JOptionPane.showMessageDialog(null, "Connection failed!", ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE);
						return;
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "IO Error!", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
						return;
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
						return;
					} catch (ErrorResponseException e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Server's response is " + e1.getMessage(), ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
						return;
					} finally {
						btnTestConnect.setEnabled(true);
						if (socket != null) {
							try {
								socket.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					JOptionPane.showMessageDialog(null, "Connect successfully!", SUCCESS_TITLE, INFO_MESSAGE_JOPT);
					CommonMethod.setComponentsEnable(btnRegister, btnLogin);
				}).start();

			}
		});
		btnTestConnect.setBounds(10, 297, 142, 23);
		contentPanel.add(btnTestConnect);

		JLabel lblServerHostname = new JLabel("Server Hostname:");
		lblServerHostname.setBounds(259, 41, 157, 21);
		contentPanel.add(lblServerHostname);

		hostnameLabel = new JLabel("");
		hostnameLabel.setBounds(426, 41, 100, 21);
		contentPanel.add(hostnameLabel);

		useHostnameRadio = new JRadioButton("");
		useHostnameRadio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (useHostnameRadio.isSelected()) {
					CommonMethod.setComponentsEnable(useHostname);
					CommonMethod.setComponentsDisable(useIP);
					CommonMethod.setComponentsDisable(useDomainName);
					client.setUseWhat(UseWhatToConnect.Hostname);
				}
			}
		});
		useWhatButtonGroup.add(useHostnameRadio);
		useHostnameRadio.setBounds(228, 41, 21, 21);
		contentPanel.add(useHostnameRadio);

		useDomainNameRadio = new JRadioButton("");
		useDomainNameRadio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (useDomainNameRadio.isSelected()) {
					CommonMethod.setComponentsEnable(useDomainName);
					CommonMethod.setComponentsDisable(useIP);
					CommonMethod.setComponentsDisable(useHostname);
					client.setUseWhat(UseWhatToConnect.DomainName);
				}
			}
		});
		useWhatButtonGroup.add(useDomainNameRadio);
		useDomainNameRadio.setBounds(228, 72, 21, 21);
		contentPanel.add(useDomainNameRadio);

		JLabel lblServerDomainName = new JLabel("Server Domain Name:");
		lblServerDomainName.setBounds(259, 72, 157, 21);
		contentPanel.add(lblServerDomainName);

		domainNameLabel = new JLabel("");
		domainNameLabel.setBounds(426, 72, 100, 21);
		contentPanel.add(domainNameLabel);

		useIP = new JComponent[] { lblServerIp, IPLabel };
		useHostname = new JComponent[] { lblServerHostname, hostnameLabel };
		useDomainName = new JComponent[] { lblServerDomainName, domainNameLabel };

		// 放在最后，保证监听事件被下面手动设置为true的行为影响所调用时所有要操作的组建都已经初始化，不会空指针异常
		JRadioButton useIPRadio = new JRadioButton("");
		useIPRadio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (useIPRadio.isSelected()) {
					CommonMethod.setComponentsEnable(useIP);
					CommonMethod.setComponentsDisable(useHostname);
					CommonMethod.setComponentsDisable(useDomainName);
					client.setUseWhat(UseWhatToConnect.IP);
				}
			}
		});
		useWhatButtonGroup.add(useIPRadio);
		useIPRadio.setBounds(228, 10, 21, 21);
		useIPRadio.setSelected(true);
		contentPanel.add(useIPRadio);
	}
}
