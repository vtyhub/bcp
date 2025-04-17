package view;

import javax.swing.JPanel;

import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import server_c.ServerC;
import javax.swing.JTextField;

import constant.CommonClass;
import constant.NetConstant;
import constant.RegExConstant;
import constant.ViewConstant;
import method.CommonMethod;
import network.listen.ListenToSDaemon;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;

public class ConnectWithSPanel extends JPanel implements CommonClass, NetConstant, ViewConstant, RegExConstant {

	private static final long serialVersionUID = 1L;

	private ServerC c;

	// forwardly
	private JRadioButton rdbtnForwardly;
	private JTextField sIPTF;
	private JLabel lblSsIp;
	private JLabel lblSsPort;
	private JTextField sPortTF;
	private JButton btnConnect;
	private JTextField sDomainTF;
	private JCheckBox useDomainChk;

	// passively
	private JRadioButton rdbtnPassively;
	private JTextField listeningPortTF;
	private JLabel lblListeningPort;
	private JButton btnListen;
	private JLabel lblPort;

	private JComponent[] passivelyComponentGroup = {};
	private JComponent[] forwardlyComponentGroup = { sIPTF, lblSsIp, lblSsPort, sPortTF, btnConnect, sDomainTF,
			useDomainChk };

	private JLabel lblSsDomainName;

	private JLabel connStatusLabel;

	/**
	 * Create the panel.
	 */
	public ConnectWithSPanel(ServerC c) {
		this.c = c;
		setLayout(null);

		lblListeningPort = new JLabel("Listening port:");
		lblListeningPort.setBounds(10, 276, 130, 21);
		add(lblListeningPort);

		rdbtnForwardly = new JRadioButton("Forwardly connect to S");
		rdbtnForwardly.setEnabled(false);
		rdbtnForwardly.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnForwardly.isSelected()) {
					CommonMethod.setComponentsDisable(passivelyComponentGroup);
					CommonMethod.setComponentsEnable(forwardlyComponentGroup);
					if (useDomainChk.isSelected()) {
						lblSsDomainName.setEnabled(true);
						sDomainTF.setEnabled(true);

						lblSsIp.setEnabled(false);
						sIPTF.setEnabled(false);
					} else {
						lblSsIp.setEnabled(true);
						sIPTF.setEnabled(true);

						sDomainTF.setEnabled(false);
						lblSsDomainName.setEnabled(false);
					}
				}
			}
		});
		rdbtnForwardly.setBounds(10, 10, 228, 23);
		add(rdbtnForwardly);

		listeningPortTF = new JTextField();
		listeningPortTF.setDocument(new PortTFDocument(listeningPortTF));
		listeningPortTF.setBounds(10, 307, 130, 23);
		add(listeningPortTF);
		listeningPortTF.setColumns(10);
		listeningPortTF.setText("2000");

		btnListen = new JButton("Listen");
		btnListen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (c.getDbConnection() == null) {
					JOptionPane.showMessageDialog(null, "Please connect to database first!", WARNING_TITLE,
							JOptionPane.WARNING_MESSAGE, null);
					return;
				}

				String portText = listeningPortTF.getText();
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
				// port可能为null
				try {
					ServerSocket passivelyServerSocket = c.getPassivelyServerSocket();
					if (passivelyServerSocket != null) {
						passivelyServerSocket.close();
					}

					c.listenToS(port);
					ListenToSDaemon listenToSDaemon = new ListenToSDaemon(c, c.getPassivelyServerSocket());
					c.setListenToSTask(listenToSDaemon);
					new Thread(listenToSDaemon).start();
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
				lblPort.setText(portText);
			}
		});
		btnListen.setBounds(150, 307, 93, 23);
		add(btnListen);

		lblPort = new JLabel("");
		lblPort.setBounds(150, 276, 93, 21);
		add(lblPort);

		rdbtnPassively = new JRadioButton("Passively connected by S");
		rdbtnPassively.setSelected(true);
		rdbtnPassively.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnPassively.isSelected()) {
					CommonMethod.setComponentsEnable(passivelyComponentGroup);
					CommonMethod.setComponentsDisable(forwardlyComponentGroup);
				}
			}
		});
		rdbtnPassively.setBounds(10, 243, 228, 23);
		add(rdbtnPassively);

		ButtonGroup forPassGroup = new ButtonGroup();
		forPassGroup.add(rdbtnForwardly);
		forPassGroup.add(rdbtnPassively);

		lblSsIp = new JLabel("S's IP:");
		lblSsIp.setEnabled(false);
		lblSsIp.setBounds(10, 43, 63, 21);
		add(lblSsIp);

		sIPTF = new JTextField();
		sIPTF.setEnabled(false);
		sIPTF.setBounds(83, 43, 99, 21);
		add(sIPTF);
		sIPTF.setColumns(10);

		lblSsPort = new JLabel("S's port:");
		lblSsPort.setEnabled(false);
		lblSsPort.setBounds(10, 107, 93, 21);
		add(lblSsPort);

		sPortTF = new JTextField();
		sPortTF.setEnabled(false);
		sPortTF.setDocument(new PortTFDocument(sPortTF));
		sPortTF.setBounds(131, 107, 99, 21);
		add(sPortTF);
		sPortTF.setColumns(10);

		btnConnect = new JButton("Connect");
		btnConnect.setEnabled(false);
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String portText = sPortTF.getText();
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
				String IP = "";
				if (useDomainChk.isSelected()) {
					IP = sDomainTF.getText();
					if (!IP.matches(DOMAIN_NAME_REGEX)) {
						JOptionPane.showMessageDialog(null, "Domain name is illegal!", ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE, null);
						return;
					}
				} else {
					IP = sIPTF.getText();
					if (!IP.matches(IPv4REGEXjb)) {
						JOptionPane.showMessageDialog(null, "IP address is illegal!", ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE, null);
						return;
					}

				}

				try {
					ConnectWithSPanel.this.c.connectToS(IP, port);
				} catch (ConnectException e0) {
					JOptionPane.showMessageDialog(null, "Connected failed!", ERROR_TITLE, JOptionPane.ERROR_MESSAGE,
							null);
					return;
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
				System.out.println("success");

			}
		});
		btnConnect.setBounds(10, 138, 93, 23);
		add(btnConnect);

		useDomainChk = new JCheckBox("Use domain name");
		useDomainChk.setEnabled(false);
		useDomainChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (useDomainChk.isSelected()) {
					lblSsDomainName.setEnabled(true);
					sDomainTF.setEnabled(true);

					lblSsIp.setEnabled(false);
					sIPTF.setEnabled(false);
				} else {
					lblSsIp.setEnabled(true);
					sIPTF.setEnabled(true);

					sDomainTF.setEnabled(false);
					lblSsDomainName.setEnabled(false);
				}
			}
		});
		useDomainChk.setBounds(10, 74, 164, 23);
		add(useDomainChk);

		lblSsDomainName = new JLabel("S's domain name:");
		lblSsDomainName.setEnabled(false);
		lblSsDomainName.setBounds(192, 43, 136, 21);
		add(lblSsDomainName);

		sDomainTF = new JTextField();
		sDomainTF.setEnabled(false);
		sDomainTF.setBounds(342, 42, 122, 21);
		add(sDomainTF);
		sDomainTF.setColumns(10);

		this.passivelyComponentGroup = new JComponent[] { listeningPortTF, lblListeningPort, btnListen, lblPort };
		this.forwardlyComponentGroup = new JComponent[] { sIPTF, lblSsIp, lblSsPort, sPortTF, btnConnect,
				lblSsDomainName, sDomainTF, useDomainChk };

		JLabel lblConnectionStatus = new JLabel("Connection Status:");
		lblConnectionStatus.setBounds(316, 276, 164, 21);
		add(lblConnectionStatus);

		connStatusLabel = new JLabel("");
		connStatusLabel.setBounds(481, 276, 171, 21);
		add(connStatusLabel);
	}

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);// 没有这一行会造成显示异常
		int firstline = rdbtnPassively.getY() - 10;
		g.drawLine(0, firstline, this.getWidth(), firstline);
	}

	public JLabel getConnStatusLabel() {
		return connStatusLabel;
	}

	public void setConnStatusLabel(JLabel connStatusLabel) {
		this.connStatusLabel = connStatusLabel;
	}

}
