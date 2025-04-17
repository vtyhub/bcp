package view;

import javax.swing.JTabbedPane;

import server_c.ServerC;

public class MainTabbed extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	
	private ServerC C;
	
	private NetworkTabbed networkTabbed;
	private DataTabbed dataTabbed;

	public MainTabbed(ServerC c) {
		// TODO Auto-generated constructor stub
		C = c;
		this.setBounds(10, 10, 700, 700);
		networkTabbed = new NetworkTabbed(C);
		addTab("Network", networkTabbed);

		dataTabbed = new DataTabbed(C);
		addTab("Data", dataTabbed);
	}

	public NetworkTabbed getNetworkTabbed() {
		return networkTabbed;
	}

	public void setNetworkTabbed(NetworkTabbed networkTabbed) {
		this.networkTabbed = networkTabbed;
	}

	public DataTabbed getDataTabbed() {
		return dataTabbed;
	}

	public void setDataTabbed(DataTabbed dataTabbed) {
		this.dataTabbed = dataTabbed;
	}
	
	
}
