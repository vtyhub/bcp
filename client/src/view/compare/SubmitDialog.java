package view.compare;

import javax.swing.JDialog;

import client.Client;


public class SubmitDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private SubmitPanel submitpanel;
	private Client client;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SubmitDialog dialog = new SubmitDialog(new SubmitPanel(new Client()));
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SubmitDialog(SubmitPanel sub) {
		submitpanel = sub;
		client = submitpanel.getClient();
		submitpanel.setSubdia(this);
		setTitle(client.getUsername());
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);

		setBounds(400, 100, 638, 500);
		setContentPane(submitpanel);

	}

}
