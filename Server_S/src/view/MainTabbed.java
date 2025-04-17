package view;

import javax.swing.JTabbedPane;

import server_s.ServerS;

public class MainTabbed extends JTabbedPane{
	
	private static final long serialVersionUID = 1L;
	
	private ServerS S;
	private ConnectWithCTabbed connectWithCTabbed;
	private DataTabbed dataTabbed;
	
	public MainTabbed(ServerS s) {
		S=s;
		connectWithCTabbed = new ConnectWithCTabbed(S);
		addTab("Network", connectWithCTabbed);
		
		dataTabbed = new DataTabbed(S);
		addTab("Data", dataTabbed);
	}
	
}
