package view;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import constant.ViewConstant;
import database.DBConstant;
import network.listen.ListenToSDaemon;
import server_c.ServerC;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

@SuppressWarnings("unused")
public class DBPanel extends JPanel implements DBConstant, ViewConstant {

	private static final long serialVersionUID = 1L;
	private JTextField URLTF;
	private ServerC c;
	private JTextField usernameTF;
	private JTextField pwdPF;
	private JButton btnCloseConnection;

	/**
	 * Create the panel.
	 */
	public DBPanel(ServerC c) {
		this.c = c;
		setLayout(null);

		URLTF = new JTextField();
		URLTF.setBounds(10, 41, 430, 21);
		add(URLTF);
		URLTF.setColumns(10);

		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Class.forName(MYSQL_DRIVER_NAME);
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
				String url = URLTF.getText();
				String username = usernameTF.getText();
				String pwd = pwdPF.getText();
				if ("".equals(url)) {
					JOptionPane.showMessageDialog(null, "URL can't be empty!", WARNING_TITLE, WARNING_MESSAGE_JOPT);
					return;
				}
				try {
					Connection conn = DriverManager.getConnection(url, username, pwd);
					c.setDbConnection(conn);
				} catch (CommunicationsException e2) {
					// TODO: handle exception
					JOptionPane.showMessageDialog(null, "Database hasn't been established!", ERROR_TITLE,
							JOptionPane.ERROR_MESSAGE, null);
					return;
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Username,password or URL is wrong!", ERROR_TITLE,
							JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				// JOptionPane.showMessageDialog(null, "Successfully connect!", PROMPT_TITLE,
				// JOptionPane.INFORMATION_MESSAGE, null);

				btnCloseConnection.setEnabled(true);
			}
		});
		btnConnect.setBounds(347, 139, 93, 23);
		add(btnConnect);

		JLabel lblDatabaseUrl = new JLabel("Database URL");
		lblDatabaseUrl.setBounds(10, 10, 108, 21);
		add(lblDatabaseUrl);

		JCheckBox chckbxUseDefaultUrl = new JCheckBox("Use default URL");
		chckbxUseDefaultUrl.addItemListener((l) -> {
			if (chckbxUseDefaultUrl.isSelected()) {
				URLTF.setEditable(false);
				URLTF.setText(DEFAULT_URL_MYSQL);
			} else {
				URLTF.setEditable(true);
				URLTF.setText("");
			}
		});
		chckbxUseDefaultUrl.setSelected(true);//调试
		chckbxUseDefaultUrl.setBounds(10, 72, 149, 23);
		add(chckbxUseDefaultUrl);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(10, 105, 75, 21);
		add(lblUsername);

		usernameTF = new JTextField();
		usernameTF.setText("root");
		usernameTF.setBounds(95, 105, 108, 21);
		add(usernameTF);
		usernameTF.setColumns(10);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(213, 105, 81, 21);
		add(lblPassword);

		pwdPF = new JPasswordField();
		pwdPF.setBounds(304, 105, 136, 21);
		add(pwdPF);
		pwdPF.setColumns(10);

		btnCloseConnection = new JButton("Close connection");
		btnCloseConnection.setEnabled(false);
		btnCloseConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ServerSocket passivelyServerSocket = c.getPassivelyServerSocket();
					Socket passivelySocket = c.getPassivelySocket();
					ListenToSDaemon listenToSTask = c.getListenToSTask();
					if (passivelyServerSocket != null || passivelySocket != null || listenToSTask != null) {
						int result = JOptionPane.showConfirmDialog(null,
								"This operation will also break off the connection with S,are you sure to do that?",
								WARNING_TITLE, YES_NO_OPT);
						if (result != YES_RESULT) {
							return;
						}
						passivelyServerSocket.close();
						c.setPassivelyServerSocket(null);
						passivelySocket.close();
						c.setPassivelySocket(null);
						listenToSTask.setEnd(true);
						c.setListenToSTask(null);
					}
					c.closeDBConnection();
					btnCloseConnection.setEnabled(false);
				} catch (NullPointerException e1) {
					JOptionPane.showMessageDialog(null, "Database hasn't been connected!", ERROR_TITLE,
							JOptionPane.ERROR_MESSAGE, null);
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "SQL Error!", ERROR_TITLE, JOptionPane.ERROR_MESSAGE, null);
					e2.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnCloseConnection.setBounds(147, 139, 186, 23);
		add(btnCloseConnection);

	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(100, 100, 500, 500);
		f.setContentPane(new DBPanel(new ServerC()));
		f.setVisible(true);
	}
}
