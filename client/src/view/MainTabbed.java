package view;

import javax.swing.JTabbedPane;

import client.Client;
import view.bcp.BCPPanel;
import view.computation.InvitationPanel;
import view.result.ResultPanel;

public class MainTabbed extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	private BCPPanel bcpPanel;

	private DataPanel dataPanel;

	private ResultPanel resultPanel;

	private InvitationPanel invitationPanel;

	public MainTabbed(Client Client) {
		this.setBounds(10, 10, 989, 600);// 900 600 result页面增加了40+49=89 900+89=989

		bcpPanel = new BCPPanel(Client);
		this.add("BCP", bcpPanel);

		dataPanel = new DataPanel(Client);
		this.addTab("Data", dataPanel);

		resultPanel = new ResultPanel(Client);
		this.addTab("Result", resultPanel);

		invitationPanel = new InvitationPanel(Client);
		this.addTab("Invitation", invitationPanel);

	}

}
