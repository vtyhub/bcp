package view.computation;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import invitation.Invitee;

import javax.swing.JScrollPane;
import javax.swing.JTable;

public class InviteeDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTable inviteeTable;

	private DefaultTableModel inviteeTableModel;

	private static String[] inviteeArray = { "Sequence", "Invitee", "Length" };

	/**
	 * Create the dialog.
	 */
	public InviteeDialog(ArrayList<Invitee> invitees) {

		setTitle("Invitees");
		setBounds(100, 100, 570, 444);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JScrollPane inviteeSP = new JScrollPane();
		inviteeSP.setBounds(10, 10, 538, 374);
		contentPanel.add(inviteeSP);

		inviteeTableModel = new DefaultTableModel(inviteeArray, 0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};

		if (invitees != null) {

			for (int i = 0; i < invitees.size(); i++) {
				Invitee invitee = invitees.get(i);
				String inviteeName = invitee.getInviteeName();
				int length = invitee.getLength();

				inviteeTableModel.addRow(new Object[] { i + 1, inviteeName, length == 0 ? "Uncommitted" : length });
			}
		}

		inviteeTable = new JTable(inviteeTableModel);
		inviteeTable.getTableHeader().setResizingAllowed(false);
		inviteeTable.getTableHeader().setReorderingAllowed(false);
		inviteeSP.setViewportView(inviteeTable);
	}
}
