package view;

import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import client.Client;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BasePairShowDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private String[] encryptedEncodingBasePairTableColumnNames = { "Base Pair", "Encoding Base Pair", "Encrypted A",
			"Encrypted B" };
	private JTable BasePairTable;
	private DefaultTableModel encryptedModel;
	private JLabel selectedRowLabel;

	public static void main(String[] args) {
		try {
			BasePairShowDialog dialog = new BasePairShowDialog(new Client());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BasePairShowDialog(Client client) {
		setBounds(100, 100, 864, 619);
		getContentPane().setLayout(null);

		JScrollPane BasePairSP = new JScrollPane();
		BasePairSP.setBounds(10, 10, 666, 500);
		getContentPane().add(BasePairSP);

		encryptedModel = new DefaultTableModel(encryptedEncodingBasePairTableColumnNames, 0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};

		BasePairTable = new JTable(encryptedModel);
		BasePairTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = BasePairTable.getSelectedRow();
					int column = BasePairTable.getSelectedColumn();
					String value = (String) encryptedModel.getValueAt(row, column);
					if (value == null || "".equals(value)) {
						return;
					}
					String type;
					if (column == 2) {
						type = "A";
					} else if (column == 3) {
						type = "B";
					} else {
						return;
					}
					new BCPParameterDialog((row + 1) + " row " + type, value, value.length()).setVisible(true);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				selectedRowLabel.setText(String.valueOf(BasePairTable.getSelectedRow() + 1));
			}
		});
		BasePairTable.getTableHeader().setReorderingAllowed(false);
		BasePairTable.getTableHeader().setResizingAllowed(false);
		BasePairTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		BasePairTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableColumnModel encryptedColumnModel = BasePairTable.getColumnModel();

		encryptedColumnModel.getColumn(0).setPreferredWidth(70);
		encryptedColumnModel.getColumn(1).setPreferredWidth(120);
		encryptedColumnModel.getColumn(2).setPreferredWidth(BasePairSP.getWidth() / 2);
		encryptedColumnModel.getColumn(3).setPreferredWidth(BasePairSP.getWidth() / 2);

		BasePairSP.setViewportView(BasePairTable);

		JLabel lblBasePairLength = new JLabel("Base Pair Length");
		lblBasePairLength.setBounds(690, 10, 140, 27);
		getContentPane().add(lblBasePairLength);

		JLabel basePairLenLabel = new JLabel("");
		basePairLenLabel.setBounds(690, 50, 140, 27);
		getContentPane().add(basePairLenLabel);

		JLabel lblSelectedRow = new JLabel("Selected Row");
		lblSelectedRow.setBounds(690, 90, 136, 27);
		getContentPane().add(lblSelectedRow);

		selectedRowLabel = new JLabel("");
		selectedRowLabel.setBounds(690, 130, 136, 27);
		getContentPane().add(selectedRowLabel);

		JLabel promptLabel = new JLabel("Click encrypted data two times can view the details");
		promptLabel.setBounds(10, 523, 666, 27);
		getContentPane().add(promptLabel);
		if (client.getBasePairs() != null) {
			basePairLenLabel.setText(String.valueOf(client.getBasePairs().length()));
		}

		String basePairs = client.getBasePairs();
		ArrayList<String> list = client.getEncodingBasePairsList();
		BigInteger[][] encrypted = client.getEncryptedEncodingBasePairs();
		if (basePairs == null) {
			return;
		}
		for (int i = 0; i < basePairs.length(); i++) {
			int add = 0;
			String[] row = new String[BasePairTable.getColumnCount()];
			row[add++] = String.valueOf(basePairs.charAt(i));
			if (list != null) {
				row[add++] = list.get(i);
			}
			if (encrypted != null) {
				row[add++] = encrypted[i][0].toString();
				row[add++] = encrypted[i][1].toString();
			}
			encryptedModel.addRow(row);
		}

	}
}
