package view;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import constant.CommonClass;
import server_s.AbstractServerS.LogType;
import server_s.ServerS;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConnectedByCPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField portTF;

	/**
	 * Create the panel.
	 */
	public ConnectedByCPanel(ServerS S) {
		// JPanel
		setLayout(null);

		// listeningTF
		portTF = new JTextField();
		portTF.setEnabled(false);
		portTF.setDocument(new CommonClass.PortTFDocument(portTF));
		portTF.setBounds(161, 9, 66, 23);
		add(portTF);
		portTF.setColumns(10);

		// Listening Label
		JLabel lblListeningPort = new JLabel("Listening Port:");
		lblListeningPort.setEnabled(false);
		lblListeningPort.setBounds(10, 10, 137, 23);
		add(lblListeningPort);

		// status
		JLabel lblStatus = new JLabel(ListeningStatus.IDLE.name());
		lblStatus.setEnabled(false);
		lblStatus.setBounds(468, 14, 66, 15);
		add(lblStatus);

		// statusName
		JLabel lblStatusName = new JLabel("Status:");
		lblStatusName.setEnabled(false);
		lblStatusName.setBounds(357, 14, 75, 15);
		add(lblStatusName);

		JButton btnListen = new JButton("Listen");
		btnListen.setEnabled(false);
		btnListen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnListen.setBounds(14, 46, 93, 23);
		add(btnListen);

	}

	private static enum ListeningStatus {
		IDLE, LISTENING, CONNECTED
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame();
		JTabbedPane jt = new JTabbedPane();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 默认为1,隐藏并不结束进程
		jf.setBounds(10, 10, 700, 500);
		jt.setBounds(jf.getBounds());
		ServerS S = new ServerS();
		ConnectedByCPanel nep = new ConnectedByCPanel(S);
		jt.addTab("socket", nep);
		LogPanel log = new LogPanel(S, LogType.networkLog);
		jt.addTab("log", log);
		jf.setContentPane(jt);
		S.initializebcp(256, 100);
		jf.setVisible(true);

	}
}
