package view;

import javax.swing.JTabbedPane;

import server_c.ServerC;
import view.compute.ComputePanel;

public class DataTabbed extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	private ServerC C;
	private BCPPanel bcpPanel;
	private ComputePanel computePanel;

	public DataTabbed(ServerC c) {
		// TODO Auto-generated constructor stub
		C = c;
		this.setBounds(10, 10, 700, 700);
		
		bcpPanel = new BCPPanel(C);
		addTab("BCP", bcpPanel);
		
		
		computePanel = new ComputePanel(C);
		addTab("Compute", computePanel);
		
	}

	public BCPPanel getBcpPanel() {
		return bcpPanel;
	}

	public void setBcpPanel(BCPPanel bcpPanel) {
		this.bcpPanel = bcpPanel;
	}

	public ComputePanel getComputePanel() {
		return computePanel;
	}

	public void setComputePanel(ComputePanel computePanel) {
		this.computePanel = computePanel;
	}
	
	
	
}
