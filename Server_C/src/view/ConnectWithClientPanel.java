package view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import constant.CommonClass;
import constant.NetConstant;
import constant.ViewConstant;
import network.listen.ListenToClientDaemon;
import server_c.ServerC;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.awt.event.ActionEvent;

@SuppressWarnings("unused")
public class ConnectWithClientPanel extends JPanel implements CommonClass, ViewConstant, NetConstant {

	private static final long serialVersionUID = 1L;
	
	private JTextField portTF;
	private ServerC c;
	private JButton btnStopListen;
	private JButton btnListen;
	private JButton btnResume;
	private JButton btnPauseListen;

	/**
	 * Create the panel.
	 */
	public ConnectWithClientPanel(ServerC c) {
		this.c = c;
		setLayout(null);

		JLabel lblListeningPort = new JLabel("Listening port:");
		lblListeningPort.setBounds(10, 10, 130, 23);
		add(lblListeningPort);

		JLabel portLabel = new JLabel("");
		portLabel.setBounds(150, 10, 93, 23);
		add(portLabel);

		portTF = new JTextField();
		portTF.setDocument(new PortTFDocument(portTF));
		portTF.setBounds(10, 43, 130, 23);
		portTF.setColumns(10);
		portTF.setText("20000");
		add(portTF);

		btnListen = new JButton("Listen");
		btnListen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String portText = portTF.getText();
				if ("".equals(portText)) {
					JOptionPane.showMessageDialog(null, "Port can't be empty!", ERROR_TITLE, JOptionPane.ERROR_MESSAGE,
							null);
					return;
				}

				Integer port = null;
				try {
					port = Integer.valueOf(portText);
					if (port < MINIMUM_PORT || port > MAXIMUM_PORT || port == null) {
						JOptionPane.showMessageDialog(null, "Port is out of range!", ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE, null);
						return;
					}
				} catch (NumberFormatException e0) {
					JOptionPane.showMessageDialog(null, "Port is out of range!", ERROR_TITLE, JOptionPane.ERROR_MESSAGE,
							null);
					return;
				}

				try {
					ServerSocket newServerSocket = new ServerSocket(port);

					ListenToClientDaemon oldListenTask = c.getListenToClientTask();
					if (oldListenTask != null) {
						oldListenTask.end();
					}

					ServerSocket oldServerSocket = c.getClientServerSocket();
					if (oldServerSocket != null) {
						oldServerSocket.close();
					}

					c.setClientServerSocket(newServerSocket);
					c.setListenToClientTask(new ListenToClientDaemon(c));
					new Thread(c.getListenToClientTask()).start();

					btnStopListen.setEnabled(true);
					btnPauseListen.setEnabled(true);
				} catch (BindException e0) {
					JOptionPane.showMessageDialog(null, "Port: " + port + " has been used!", ERROR_TITLE,
							JOptionPane.ERROR_MESSAGE, null);
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Listening failed!", ERROR_TITLE, JOptionPane.ERROR_MESSAGE,
							null);
					return;
				}

				portLabel.setText(portText);

			}
		});
		btnListen.setBounds(150, 43, 93, 23);
		add(btnListen);

		JLabel connectionStatusLabel = new JLabel("");
		connectionStatusLabel.setBounds(424, 10, 154, 23);
		add(connectionStatusLabel);

		JLabel lblConnectionStatus = new JLabel("Connection status:");
		lblConnectionStatus.setBounds(253, 11, 161, 23);
		add(lblConnectionStatus);

		JLabel lblOnlineCounts = new JLabel("Online counts:");
		lblOnlineCounts.setBounds(253, 43, 161, 23);
		add(lblOnlineCounts);

		JLabel onlineCountsLabel = new JLabel("");
		onlineCountsLabel.setBounds(424, 43, 154, 23);
		add(onlineCountsLabel);

		btnStopListen = new JButton("Stop Listen");
		btnStopListen.setEnabled(false);
		btnStopListen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListenToClientDaemon listenTask = c.getListenToClientTask();
				if (listenTask != null) {
					listenTask.end();
					c.setListenToClientTask(null);
					try {
						c.getClientServerSocket().close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					c.setClientServerSocket(null);
				}
				btnStopListen.setEnabled(false);
				btnResume.setEnabled(false);
				btnPauseListen.setEnabled(false);
			}
		});
		btnStopListen.setBounds(10, 76, 130, 23);
		add(btnStopListen);

		btnPauseListen = new JButton("Pause Listen");
		btnPauseListen.setEnabled(false);
		btnPauseListen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListenToClientDaemon listen = c.getListenToClientTask();
				if (listen != null) {
					listen.pause();
					btnResume.setEnabled(true);
					btnPauseListen.setEnabled(false);
				}
			}
		});
		btnPauseListen.setBounds(10, 109, 130, 23);
		add(btnPauseListen);

		btnResume = new JButton("Resume");
		btnResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListenToClientDaemon listen = c.getListenToClientTask();
				if (listen != null) {
					listen.resume();
					btnResume.setEnabled(false);
					btnPauseListen.setEnabled(true);
				}
			}
		});
		btnResume.setEnabled(false);
		btnResume.setBounds(10, 145, 130, 23);
		add(btnResume);

	}

	public static void main(String[] args) {

	}
}
