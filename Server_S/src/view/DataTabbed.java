package view;

import javax.swing.JTabbedPane;

import server_s.ServerS;

public class DataTabbed extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	private BCPPanel bcpPanel;

	public DataTabbed(ServerS s) {
		// TODO Auto-generated constructor stub
		setSize(700, 700);
		
		bcpPanel = new BCPPanel(s);
		addTab("BCP", bcpPanel);
	}
}
