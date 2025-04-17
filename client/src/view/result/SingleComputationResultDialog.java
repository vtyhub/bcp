package view.result;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;


import client.Client;
import genome.DecryptedResult;
import genome.SingleDecryption;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;

public class SingleComputationResultDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private JTable singleTable;
	private String[] singleColumns = { "sequence", "UserA", "UserB", "add", "mult" };

	public static void main(String[] args) {
		try {
			SingleComputationResultDialog dialog = new SingleComputationResultDialog(new Client(), 1);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public SingleComputationResultDialog(Client client, int index) {
		setBounds(100, 100, 866, 417);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JScrollPane singleSP = new JScrollPane();
		singleSP.setBounds(0, 0, 674, 336);
		contentPanel.add(singleSP);

		DefaultTableModel singleTableModel = new DefaultTableModel(singleColumns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};

		singleTable = new JTable(singleTableModel);
		singleSP.setViewportView(singleTable);

		JLabel lblNewLabel = new JLabel("00 00");
		lblNewLabel.setBounds(693, 24, 72, 18);
		contentPanel.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("10 01");
		lblNewLabel_1.setBounds(693, 55, 72, 18);
		contentPanel.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("100 100");
		lblNewLabel_2.setBounds(693, 86, 72, 18);
		contentPanel.add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("110 1001");
		lblNewLabel_3.setBounds(693, 132, 72, 18);
		contentPanel.add(lblNewLabel_3);

		DecryptedResult[] decResult = client.getDecResult();
		SingleDecryption[] result = decResult[index].getResult();
		for (int i = 0; i < result.length; i++) {
			singleTableModel.addRow(new Object[] { i + 1, decResult[index].getUsernamea(),
					decResult[index].getUsernameb(), result[i].getAdd(), result[i].getMult() });
		}

	}
}
