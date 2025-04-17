package view;

import javax.swing.JTabbedPane;

import server_s.ServerS;

public class ConnectWithCTabbed extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	
	private ConnectedByCPanel connectedByCPanel;
	private ConnectToCPanel connectToCPanel;

	public ConnectWithCTabbed(ServerS s) {
		// TODO Auto-generated constructor stub
		setBounds(10, 10, 700, 700);
		
		connectedByCPanel = new ConnectedByCPanel(s);
		addTab("Passively", connectedByCPanel);

		connectToCPanel = new ConnectToCPanel(s);
		addTab("Forwardly", connectToCPanel);
	}
	
}
