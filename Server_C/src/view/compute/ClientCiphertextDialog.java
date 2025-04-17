package view.compute;

import java.awt.BorderLayout;
import java.math.BigInteger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import client.ComputeClient;
import genome.ComputeResult;
import server_c.ServerC;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClientCiphertextDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private ServerC C;
	private String[] cipherColumnNames = { "Sequence", "Original cipherA", "Original cipherB",
			"CipherA enctypted on PK", "CipherA enctypted on PK", "ResultA encrypted on PK",
			"ResultB encrypted on PK" };

	private final JPanel contentPanel = new JPanel();
	private JTable cipherTable;
	private DefaultTableModel cipherTableModel;

	public ClientCiphertextDialog(ServerC c, ComputeClient client) {
		C = c;
		setTitle(client.getUsername() + "'s ciphertext");
		System.out.println(getDefaultCloseOperation());
		setBounds(100, 100, 717, 483);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JScrollPane cipherSP = new JScrollPane();
		cipherSP.setBounds(14, 13, 671, 371);
		contentPanel.add(cipherSP);

		cipherTableModel = new DefaultTableModel(cipherColumnNames, 0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};

		cipherTable = new JTable(cipherTableModel);
		cipherTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cipherTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {

				}
			}
		});
		cipherSP.setViewportView(cipherTable);

		JButton btnOutput = new JButton("Output");
		btnOutput.setBounds(14, 397, 113, 27);
		contentPanel.add(btnOutput);

		BigInteger[][] originalCiphertext = client.getOriginalCiphertext();
		if (originalCiphertext == null) {
			// 若连基本密文都未设置，则剩余也没有展示的必要
			return;
		}
		for (int i = 0; i < originalCiphertext.length; i++) {
			Object[] row = new Object[cipherTable.getColumnCount()];
			int add = 0;
			row[add++] = i + 1;
			row[add++] = originalCiphertext[i][0];
			row[add++] = originalCiphertext[i][1];
			cipherTableModel.addRow(row);// 此时PK加密密文未设置
		}
		BigInteger[][] originalEncryptedOnPK = client.getOriginalEncryptedOnPK();
		if (originalEncryptedOnPK == null || originalEncryptedOnPK != originalCiphertext) {
			return;
		}
		for (int i = 0; i < originalEncryptedOnPK.length; i++) {
			int add = 3;// 用因为不是最后的列，所以不能减去

			cipherTable.setValueAt(originalEncryptedOnPK[i][0], i, add++);
			cipherTable.setValueAt(originalEncryptedOnPK[i][1], i, add++);
		}
		ComputeResult[] computeResult = C.getComputeResult();
		if (computeResult == null) {
			return;
		}
		// for (int i = 0; i < computeResult.length; i++) {
		// computeResult[i].get
		// }
	}
}
