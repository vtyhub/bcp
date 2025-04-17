package view.login;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.Client;
import constant.CommonClass;
import constant.CryptoConstant;
import constant.NetConstant;
import constant.ViewConstant;
import constant.interaction.BusinessConstant;
import method.CommonMethod;
import network.ErrorResponseException;
import network.Hello;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.awt.event.ActionEvent;

public class RegisterDialog extends JDialog
		implements ViewConstant, BusinessConstant, CommonClass, NetConstant, CryptoConstant {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private Client client;
	private JTextField usernameTF;
	private JPasswordField pwdPF;
	private JPasswordField confirmPF;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			RegisterDialog dialog = new RegisterDialog(new Client());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public RegisterDialog(Client client) {
		this.client = client;
		setBounds(100, 100, 427, 203);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		contentPanel.setLayout(null);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(10, 10, 150, 21);
		contentPanel.add(lblUsername);

		usernameTF = new JTextField();
		usernameTF.setDocument(new limitedUserTFDocument(usernameTF, MAXIMUM_LENGTH_USERNAME));
		usernameTF.setBounds(170, 10, 186, 21);
		contentPanel.add(usernameTF);
		usernameTF.setColumns(10);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(10, 41, 150, 21);
		contentPanel.add(lblPassword);

		pwdPF = new JPasswordField();
		pwdPF.setDocument(new limitedUserTFDocument(pwdPF, MAXIMUM_LENGTH_PASSWORD));
		pwdPF.setBounds(170, 41, 186, 21);
		contentPanel.add(pwdPF);

		JLabel lblConfirmPassword = new JLabel("Confirm password:");
		lblConfirmPassword.setBounds(10, 72, 150, 21);
		contentPanel.add(lblConfirmPassword);

		confirmPF = new JPasswordField();
		confirmPF.setDocument(new limitedUserTFDocument(confirmPF, MAXIMUM_LENGTH_PASSWORD));
		confirmPF.setBounds(170, 72, 186, 21);
		contentPanel.add(confirmPF);

		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			// socket
			public void actionPerformed(ActionEvent e) {

				String username = usernameTF.getText();
				if (username.length() < MINIMUM_LENGTH_USERNAME || username.length() > MAXIMUM_LENGTH_USERNAME) {
					JOptionPane.showMessageDialog(null, "Username should be between " + MINIMUM_LENGTH_USERNAME
							+ " and " + MAXIMUM_LENGTH_USERNAME + " !", WARNING_TITLE, WARNING_MESSAGE_JOPT);
					return;
				}

				String pwd = new String(pwdPF.getPassword());
				if (pwd.length() < MINIMUM_LENGTH_PASSWORD || pwd.length() > MAXIMUM_LENGTH_PASSWORD) {
					JOptionPane.showMessageDialog(null, "Password's length should be between " + MINIMUM_LENGTH_PASSWORD
							+ " and " + MAXIMUM_LENGTH_PASSWORD + " !", WARNING_TITLE, WARNING_MESSAGE_JOPT);
					return;
				}
				if (!pwd.equals(new String(confirmPF.getPassword()))) {
					JOptionPane.showMessageDialog(null, "Confirm password is wrong!", WARNING_TITLE,
							WARNING_MESSAGE_JOPT);
					return;
				}

				String host = null;
				switch (RegisterDialog.this.client.getUseWhat()) {
				case IP:
					host = RegisterDialog.this.client.getServerIP();
					break;
				case Hostname:
					host = RegisterDialog.this.client.getServerHostname();
					break;
				case DomainName:
					host = RegisterDialog.this.client.getServerDomainName();
					break;
				default:
					JOptionPane.showMessageDialog(null, "A connection mode must be chose", WARNING_TITLE,
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				if (host == null) {
					JOptionPane.showMessageDialog(null, "The selected server's information is not set!", WARNING_TITLE,
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				Socket socket = new Socket();
				InetSocketAddress addr = new InetSocketAddress(host, client.getServerPort());
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
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}).start();
					obtoc = new ObjectOutputStream(socket.getOutputStream());
					obfromc = new ObjectInputStream(socket.getInputStream());
					end.setData(false);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null,
							"Server's " + client.getUseWhat() + " : " + host + " is not a valid one!", ERROR_TITLE,
							ERROR_MESSAGE_JOPT);
					try {
						if (socket != null)
							socket.close();
					} catch (IOException e22) {
						// TODO Auto-generated catch block
						e22.printStackTrace();
					}
					return;
				} catch (SocketException e2) {
					// TODO: handle exception
					JOptionPane.showMessageDialog(null, "Connection failed!", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
					try {
						if (socket != null)
							socket.close();
					} catch (IOException e22) {
						// TODO Auto-generated catch block
						e22.printStackTrace();
					}
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Connect to server error!", ERROR_TITLE, ERROR_MESSAGE_JOPT);
					e1.printStackTrace();
					try {
						if (socket != null)
							socket.close();
					} catch (IOException e22) {
						// TODO Auto-generated catch block
						e22.printStackTrace();
					}
					return;
				}

				Closeable[] streams = new Closeable[] { obtoc, obfromc, socket };
				// hello
				try {
					Hello.forwardlyHello(obfromc, obtoc);
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Connection error", ERROR_TITLE, ERROR_MESSAGE_JOPT);
					e1.printStackTrace();
					return;
				} catch (ErrorResponseException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Server's response is " + e1.getMessage(), ERROR_TITLE,
							ERROR_MESSAGE_JOPT);
					e1.printStackTrace();
					return;
				}

				// register judge
				try {
					obtoc.writeInt(CLIENT_REGISTER);
					obtoc.flush();
					int registerResponse = obfromc.readInt();
					switch (registerResponse) {
					case REGISTER_PERMITTED:
						// 允许注册，进入下一步
						break;
					case REGISTER_DENIED:
						JOptionPane.showMessageDialog(null, "Server doesn't support register now!", PROMPT_TITLE,
								INFO_MESSAGE_JOPT);
						CommonMethod.batchClose(streams);
						return;
					case DB_UNCONNECTED:
						JOptionPane.showMessageDialog(null, "Server didn't connect to database!", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
						CommonMethod.batchClose(streams);
						return;
					default:
						JOptionPane.showMessageDialog(null, "Error response: " + registerResponse, ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
						CommonMethod.batchClose(streams);
						return;
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}

				// register
				try {
					MessageDigest digest = MessageDigest.getInstance(DEFAULT_HASH_ALGORITHM);
					byte[] hashed1pwdbytes = digest.digest(pwd.getBytes(DEFAULT_CHARSET));
					String hashed1pwd = CommonMethod.byteToHexStr(hashed1pwdbytes);

					String[] user = { username, hashed1pwd };
					obtoc.writeObject(user);// 发送注册数据
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null,
							"Encoding character " + DEFAULT_CHARSET + " doesn't exist,register failed!", ERROR_TITLE,
							ERROR_MESSAGE_JOPT);
					return;
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null,
							"Algorithm " + DEFAULT_HASH_ALGORITHM + "doesn't exist,register failed!", ERROR_TITLE,
							ERROR_MESSAGE_JOPT);
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Connection error,register failed!", ERROR_TITLE,
							ERROR_MESSAGE_JOPT);
					e1.printStackTrace();
					return;
				}

				// 获取注册结果
				try {
					int registerResult = obfromc.readInt();
					switch (registerResult) {
					case REGISTER_SUCCEEDED:
						JOptionPane.showMessageDialog(null, "Register succeed!", PROMPT_TITLE, INFO_MESSAGE_JOPT);
						break;
					case REGISTER_FAILED:
						JOptionPane.showMessageDialog(null, "Network status error!", ERROR_TITLE, ERROR_MESSAGE_JOPT);
						break;
					case REGISTER_USEREXISTED:
						JOptionPane.showMessageDialog(null, "User: " + username + " already existed!", PROMPT_TITLE,
								INFO_MESSAGE_JOPT);
						break;
					default:
						JOptionPane.showMessageDialog(null, "Error response: " + registerResult, ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
						break;
					}
					CommonMethod.batchClose(streams);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				RegisterDialog.this.dispose();
			}

		});
		btnRegister.setBounds(248, 103, 108, 23);
		contentPanel.add(btnRegister);
		contentPanel.getRootPane().setDefaultButton(btnRegister);
		// System.out.println(this.getRootPane() ==
		// contentPanel.getRootPane());//结果为true
	}
}
