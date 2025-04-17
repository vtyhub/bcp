package view.result;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import client.Client;
import constant.ComputeConstant;
import constant.SysConstant;
import constant.ViewConstant;
import constant.CommonClass.SingleClassFileSaveFilter;
import constant.interaction.LoggedConstant;
import cryptography.BCPForClient;
import cryptography.PP;
import genome.ComputeResult;
import genome.DecryptedResult;
import genome.Result;
import genome.SingleBasePairComputeResult;
import genome.SingleDecryption;
import genome.TruthTable;
import method.CommonMethod;
import operatestring.GenerateString;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.awt.event.ActionEvent;

public class ResultPanel extends JPanel implements LoggedConstant, ViewConstant, ComputeConstant, SysConstant {

	private static final long serialVersionUID = 1L;

	private Client Client;
	private JTable resultTable;
	private DefaultTableModel resultTableModel;

	private JButton btnOutputResult;

	private static String[] resultTableColumnNames = { "Sequence", "Participant", "Base pairs' similarity ratio" };

	public ResultPanel(Client client) {
		setBounds(10, 10, 900, 600);
		Client = client;
		setLayout(null);

		resultTableModel = new DefaultTableModel(resultTableColumnNames, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};

		resultTable = new JTable(resultTableModel);

		JScrollPane resultSP = new JScrollPane();
		resultSP.setBounds(10, 10, 965, 363);// 876 363
		resultSP.setViewportView(resultTable);
		add(resultSP);

		JButton btnGetResult = new JButton("Get Result");
		btnGetResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BCPForClient bcp = Client.getBcp();
				if (bcp == null) {
					JOptionPane.showMessageDialog(null, "BCP is not set!", WARNING_TITLE, WARNING_MESSAGE_JOPT);
					return;
				}
				PP pp = bcp.getPP();
				if (pp == null) {
					JOptionPane.showMessageDialog(null, "PP is not set!", WARNING_TITLE, WARNING_MESSAGE_JOPT);
					return;
				}
				BigInteger N = pp.getN();
				if (N == null) {
					JOptionPane.showMessageDialog(null, "N is not set!", WARNING_TITLE, WARNING_MESSAGE_JOPT);
					return;
				}
				BigInteger h = bcp.getH(), a = bcp.getA();
				if (h == null || a == null) {
					JOptionPane.showMessageDialog(null, "Key pairs are not set!", WARNING_TITLE, WARNING_MESSAGE_JOPT);
					return;
				}

				ComputeResult[] computeResult = Client.getComptuteResult();
				if (computeResult == null) {
					try {
						ObjectOutputStream obtoc = Client.getObtoc();
						ObjectInputStream obfromc = Client.getObfromc();
						if (obtoc == null || obfromc == null) {
							return;
						}
						Client.getHeartbeat().setPause(true);
						obtoc.writeObject(RESULT_OPER);
						obtoc.writeObject(GETRESULT);
						Object response = obfromc.readObject();
						if (response.equals(GETRESULT_UNFINISHED)) {
							JOptionPane.showMessageDialog(null, "Compuation hasn't finished!", PROMPT_TITLE,
									INFO_MESSAGE_JOPT);
							return;
						} else if (response.equals(GETRESULT_UNSUBMITTED)) {
							JOptionPane.showMessageDialog(null, "Data hasn't been submitted!", WARNING_TITLE,
									WARNING_MESSAGE_JOPT);
							return;
						} else if (response.equals(GETRESULT_DENY)) {
							JOptionPane.showMessageDialog(null, "Server denied the get request operation!", ERROR_TITLE,
									ERROR_MESSAGE_JOPT);
							return;
						} else if (response.equals(GETRESULT_PERMIT)) {

							computeResult = (ComputeResult[]) obfromc.readObject();
							Client.setComptuteResult(computeResult);

						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						Client.getHeartbeat().setPause(false);
					}
				}
				// 解密返回结果
				DecryptedResult[] dec = new DecryptedResult[computeResult.length];
				for (int i = 0; i < computeResult.length; i++) {
					dec[i] = decryptResult(N, a, computeResult[i]);
				}
				Client.setDecResult(dec);
				Result[] res = new Result[computeResult.length];
				for (int i = 0; i < dec.length; i++) {
					SingleDecryption[] singleDec = dec[i].getResult();
					String usernamea = dec[i].getUsernamea();
					String usernameb = dec[i].getUsernameb();

					Result result = new Result();

					boolean[] truth = compareTruth(singleDec);
					result.setResult(truth);

					double rate = CommonMethod.computeRate(truth);
					result.setRate(rate);

					result.setUsernamea(usernamea);
					result.setUsernameb(usernameb);

					res[i] = result;
				}
				Client.setFinalResult(res);
				// 更新视图
				clearTable(resultTableModel);
				updateResultView(resultTableModel, res);

				btnOutputResult.setEnabled(true);
			}
		});
		btnGetResult.setBounds(10, 386, 113, 27);
		add(btnGetResult);

		btnOutputResult = new JButton("Output Result");
		btnOutputResult.setEnabled(false);
		btnOutputResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(DESKTOP_PATH);
				jfc.setFileFilter(new SingleClassFileSaveFilter(RESULT_EXTENSION));
				jfc.setAcceptAllFileFilterUsed(false);
				// jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);// 对save来说，设置不设置不影响
				jfc.showSaveDialog(new JPanel());
				File file = jfc.getSelectedFile();
				if (file == null) {
					return;
				}
				String path = file.getAbsolutePath() + "." + RESULT_EXTENSION;
				try (PrintWriter pw = new PrintWriter(path)) {

					for (int i = 0; i < resultTableModel.getRowCount(); i++) {
						StringBuilder resultsb = new StringBuilder();

						// 序号
						Object sequence = resultTableModel.getValueAt(i, 0);
						resultsb.append(sequence + GenerateString.copyMould(DEFAULT_BLANK, " "));

						// 本用户
						resultsb.append(Client.getUsername() + GenerateString.copyMould(DEFAULT_BLANK, " "));

						Object usernameb = resultTableModel.getValueAt(i, 1);
						resultsb.append(usernameb + GenerateString.copyMould(DEFAULT_BLANK, " "));

						Object rate = resultTableModel.getValueAt(i, 2);
						resultsb.append(rate);

						pw.println(resultsb.toString());
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnOutputResult.setBounds(722, 386, 164, 27);
		add(btnOutputResult);

		JButton btnResetResult = new JButton("Reset Result");
		btnResetResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 清除视图以及内存中的结果实例
				clearTable(resultTableModel);
				Client.setComptuteResult(null);
				btnOutputResult.setEnabled(false);
			}
		});
		btnResetResult.setBounds(10, 426, 141, 27);
		add(btnResetResult);
	}

	public static DecryptedResult decryptResult(BigInteger N, BigInteger a, ComputeResult result) {
		String ua = result.getUsernameA();
		String ub = result.getUsernameB();
		SingleBasePairComputeResult[] resulti = result.getResult();

		SingleDecryption[] list = new SingleDecryption[resulti.length];
		for (int j = 0; j < resulti.length; j++) {
			SingleDecryption singleDecryption = new SingleDecryption();

			BigInteger[] addResult = resulti[j].getAddResult();
			BigInteger[] multResult = resulti[j].getMultResult();

			String decAdd = BCPForClient.dec(N, a, addResult).toString(2);
			String decMult = BCPForClient.dec(N, a, multResult).toString(2);

			singleDecryption.setAdd(decAdd);
			singleDecryption.setMult(decMult);
			list[j] = singleDecryption;
		}
		return new DecryptedResult(ua, ub, list);
	}

	public static boolean[] compareTruth(String[] result) {
		boolean[] com = new boolean[result.length];
		for (int i = 0; i < result.length; i++) {
			com[i] = TruthTable.getTruth(result[i]);
		}
		return com;
	}

	public static boolean[] compareTruth(SingleDecryption[] result) {
		boolean[] com = new boolean[result.length];
		for (int i = 0; i < result.length; i++) {
			com[i] = TruthTable.getTruth(result[i].all());
		}
		return com;
	}

	public static void clearTable(DefaultTableModel resultTableModel) {
		for (int i = 0; i < resultTableModel.getRowCount();) {
			resultTableModel.removeRow(0);
		}
	}

	public static void updateResultView(DefaultTableModel resultTableModel, Result[] result) {
		for (int i = 0; i < result.length; i++) {
			resultTableModel.addRow(new Object[] { i + 1, result[i].getUsernameb(),
					String.format("%." + RETENTION_NUMBER + "f", result[i].getRate() * 100) + "%" });
		}
	}
}
