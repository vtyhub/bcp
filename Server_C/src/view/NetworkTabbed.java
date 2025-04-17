package view;

import javax.swing.JTabbedPane;

import server_c.ServerC;

public class NetworkTabbed extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	private DBPanel dbPanel;
	private ConnectWithClientPanel connectWithClientPanel;
	private ConnectWithSPanel connectWithSPanel;

	public NetworkTabbed(ServerC c) {
		// super(top)没有意义，跟无参父类构造方法一样
		setBounds(10, 10, 700, 700);
		// dbpanel
		dbPanel = new DBPanel(c);
		this.addTab("Database", dbPanel);

		// client
		connectWithClientPanel = new ConnectWithClientPanel(c);
		this.addTab("Client", connectWithClientPanel);

		// server S
		connectWithSPanel = new ConnectWithSPanel(c);
		this.addTab("ServerS", connectWithSPanel);

	}

	public DBPanel getDbPanel() {
		return dbPanel;
	}

	public void setDbPanel(DBPanel dbPanel) {
		this.dbPanel = dbPanel;
	}

	public ConnectWithClientPanel getConnectWithClientPanel() {
		return connectWithClientPanel;
	}

	public void setConnectWithClientPanel(ConnectWithClientPanel connectWithClientPanel) {
		this.connectWithClientPanel = connectWithClientPanel;
	}

	public ConnectWithSPanel getConnectWithSPanel() {
		return connectWithSPanel;
	}

	public void setConnectWithSPanel(ConnectWithSPanel connectWithSPanel) {
		this.connectWithSPanel = connectWithSPanel;
	}

}
