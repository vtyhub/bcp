package view;

import javax.swing.JPanel;
import javax.swing.JTextField;

import client.Client;
import constant.SysConstant;
import constant.ViewConstant;
import constant.interaction.LoggedConstant;
import cryptography.BCPForClient;
import cryptography.PP;
import genome.Encode;
import genome.EncodingConstant;
import genome.WrongGenomeDataException;
import method.CommonMethod;
import view.compare.SubmitDialog;
import view.compare.SubmitPanel;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class DataPanel extends JPanel implements SysConstant, ViewConstant, EncodingConstant, LoggedConstant {

	private static final long serialVersionUID = 1L;
	private Client Client;
	private JTextField filepathTF;
	private JRadioButton rdbtnUseFile;
	private JRadioButton rdbtnUseTextAera;
	private JLabel importedFileLable;
	private JButton btnEncode;
	private JLabel TAEncodingStatusLabel;
	private JLabel lblImportedFile;
	private JLabel lblEncodingStatus;
	private JButton btnImportToText;
	private JTextArea contentTA;
	private JButton btnEncrypt;
	private JButton btnImport;

	private JComponent[] fileComponect;
	private JComponent[] TAComponect;
	private JLabel lblFileEncodingStatus;
	private JLabel fileEncodingStatusLabel;

	private static String ENCODED = "Successfully encoding";
	private static String UNENCODED = "Unencoded";

	public DataPanel(Client client) {
		this.Client = client;

		setLayout(null);

		filepathTF = new JTextField();
		filepathTF.setBounds(120, 10, 300, 27);
		add(filepathTF);
		filepathTF.setColumns(10);

		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(DESKTOP_PATH);
				jfc.setAcceptAllFileFilterUsed(true);
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jfc.showOpenDialog(DataPanel.this);
				File file = jfc.getSelectedFile();
				if (file == null || !file.exists() || file.isDirectory()) {
					return;
				}

				filepathTF.setText(file.getAbsolutePath());

			}
		});
		btnBrowse.setBounds(430, 10, 115, 27);
		add(btnBrowse);

		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SubmitDialog(new SubmitPanel(Client)).setVisible(true);
			}
		});
		btnSubmit.setBounds(264, 389, 115, 27);
		add(btnSubmit);

		btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = new File(filepathTF.getText());
				if (!file.exists() || file.isDirectory()) {
					JOptionPane.showMessageDialog(null, "The selected file is a directory or doesn't exist!",
							ERROR_TITLE, ERROR_MESSAGE_JOPT);
					return;
				}
				try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
						BufferedReader reader = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {

					ArrayList<String> encodingBasePairsList = new ArrayList<String>();
					String clientBasePairs = "";

					String oneLineBasePairs;
					while ((oneLineBasePairs = reader.readLine()) != null) {
						// 无需检测，Encode类中所有方法已经自带检测,readline后面不能trim，否则空指针
						oneLineBasePairs = oneLineBasePairs.trim();
						if (CommonMethod.isFullEmpty(oneLineBasePairs)) {
							continue;
						}
						ArrayList<String> oneLineEncodingBasePairsList = Encode.encodeToArrayList(oneLineBasePairs);// 放在第一位
						for (String encodingBasePair : oneLineEncodingBasePairsList) {
							encodingBasePairsList.add(encodingBasePair);
						}
						clientBasePairs += oneLineBasePairs;

					}

					Client.setBasePairs(clientBasePairs);
					Client.setEncodingBasePairsList(encodingBasePairsList);

					importedFileLable.setText(file.getAbsolutePath());
					fileEncodingStatusLabel.setText(ENCODED);
					TAEncodingStatusLabel.setText(UNENCODED);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (WrongGenomeDataException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Base pair file should only have A,T,G,C four situations!",
							ERROR_TITLE, ERROR_MESSAGE_JOPT);
				}
			}
		});
		btnImport.setBounds(555, 10, 115, 27);
		add(btnImport);

		JLabel lblFilePath = new JLabel("File Path:");
		lblFilePath.setBounds(10, 10, 100, 27);
		add(lblFilePath);

		JScrollPane contentSP = new JScrollPane();
		contentSP.setBounds(10, 141, 865, 191);
		add(contentSP);

		contentTA = new JTextArea();
		contentSP.setViewportView(contentTA);

		btnEncrypt = new JButton("Encrypt");
		btnEncrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> encodinglist = Client.getEncodingBasePairsList();
				if (encodinglist == null) {
					JOptionPane.showMessageDialog(null, "Data unencoded", WARNING_TITLE, WARNING_MESSAGE_JOPT);
					return;
				}

				BCPForClient bcp = Client.getBcp();
				if (bcp == null) {
					JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
					return;
				}
				PP pp = bcp.getPP();
				if (pp == null) {
					JOptionPane.showMessageDialog(null, PP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
					return;
				}
				BigInteger h = bcp.getH();
				if (h == null) {
					JOptionPane.showMessageDialog(null, "Public key " + UNSET_NOTIFICATION, ERROR_TITLE,
							ERROR_MESSAGE_JOPT);
					return;
				}
				ArrayList<BigInteger[]> cipherlist = new ArrayList<BigInteger[]>(encodinglist.size());

				for (String basepair : encodinglist) {
					cipherlist.add(BCPForClient.enc(pp, h, new BigInteger(basepair, 2)));
				}

				Client.setEncryptedEncodingBasePairs(cipherlist.toArray(new BigInteger[0][]));
			}
		});
		btnEncrypt.setBounds(10, 389, 113, 27);
		add(btnEncrypt);

		btnEncode = new JButton("Encode Text Aera");
		btnEncode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try (Scanner scanner = new Scanner(contentTA.getText())) {

					ArrayList<String> encodingBasePairsList = new ArrayList<String>();
					String clientBasePairs = "";

					while (scanner.hasNextLine()) {
						String line = scanner.nextLine().trim();
						if (CommonMethod.isFullEmpty(line)) {
							continue;
						}
						System.out.println(line);
						ArrayList<String> oneLineEncodingBasePairsList = Encode.encodeToArrayList(line);// 放在第一位
						for (String encodingBasePair : oneLineEncodingBasePairsList) {
							encodingBasePairsList.add(encodingBasePair);
						}
						clientBasePairs += line;
					}
					System.out.println(clientBasePairs);
					System.out.println(encodingBasePairsList);
					Client.setBasePairs(clientBasePairs);
					Client.setEncodingBasePairsList(encodingBasePairsList);

					TAEncodingStatusLabel.setText(ENCODED);
					fileEncodingStatusLabel.setText(UNENCODED);

				} catch (WrongGenomeDataException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Base pair file should only have A,T,G,C four situations!",
							ERROR_TITLE, ERROR_MESSAGE_JOPT);

				}
			}
		});
		btnEncode.setBounds(10, 342, 178, 27);
		add(btnEncode);

		btnImportToText = new JButton("Import To Text Aera");
		btnImportToText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = new File(filepathTF.getText());
				if (!file.exists() || file.isDirectory()) {
					JOptionPane.showMessageDialog(null, "The selected file is a directory or doesn't exist!",
							ERROR_TITLE, ERROR_MESSAGE_JOPT);
					return;
				}

				long filesizemb = file.length() / MB;
				if (filesizemb > FILE_WARNING_SIZE_MB) {
					int result = JOptionPane.showConfirmDialog(null,
							"This file's size is " + filesizemb + " MB and that value is above the recommended value "
									+ FILE_WARNING_SIZE_MB + " MB,do you really want to import it to the text aera?",
							WARNING_TITLE, JOptionPane.YES_NO_OPTION);
					if (result != JOptionPane.YES_OPTION) {
						return;
					}
				}

				try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
						BufferedReader reader = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET))) {

					contentTA.setText("");
					String oneLineBasePairs;
					while ((oneLineBasePairs = reader.readLine()) != null) {
						// 无需检测，Encode类中所有方法已经自带检测,readline后面不能trim，否则空指针
						contentTA.append(oneLineBasePairs.trim() + '\n');
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		btnImportToText.setBounds(680, 10, 195, 27);
		add(btnImportToText);

		rdbtnUseTextAera = new JRadioButton("Use Text Aera");
		rdbtnUseTextAera.setBounds(725, 47, 150, 27);
		add(rdbtnUseTextAera);

		lblImportedFile = new JLabel("Imported File:");
		lblImportedFile.setBounds(10, 94, 120, 27);
		add(lblImportedFile);

		importedFileLable = new JLabel("");
		importedFileLable.setBounds(140, 94, 400, 27);
		add(importedFileLable);

		lblEncodingStatus = new JLabel("Encoding Status:");
		lblEncodingStatus.setBounds(202, 342, 136, 27);
		add(lblEncodingStatus);

		TAEncodingStatusLabel = new JLabel("");
		TAEncodingStatusLabel.setBounds(352, 342, 318, 27);
		add(TAEncodingStatusLabel);

		lblFileEncodingStatus = new JLabel("File Encoding Status:");
		lblFileEncodingStatus.setBounds(550, 94, 175, 27);
		add(lblFileEncodingStatus);

		fileEncodingStatusLabel = new JLabel("");
		fileEncodingStatusLabel.setBounds(735, 94, 140, 27);
		add(fileEncodingStatusLabel);

		TAComponect = new JComponent[] { btnImportToText, contentTA, btnEncode, lblEncodingStatus,
				TAEncodingStatusLabel };
		fileComponect = new JComponent[] { btnImport, lblImportedFile, importedFileLable, fileEncodingStatusLabel,
				lblFileEncodingStatus };

		rdbtnUseFile = new JRadioButton("Use File");
		rdbtnUseFile.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (rdbtnUseFile.isSelected()) {
					CommonMethod.setComponentsEnable(fileComponect);
					CommonMethod.setComponentsDisable(TAComponect);
				} else {
					CommonMethod.setComponentsEnable(TAComponect);
					CommonMethod.setComponentsDisable(fileComponect);

				}
			}
		});
		rdbtnUseFile.setSelected(true);
		rdbtnUseFile.setBounds(10, 47, 150, 27);
		add(rdbtnUseFile);

		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnUseFile);
		group.add(rdbtnUseTextAera);

		JButton btnViewData = new JButton("View Data");
		btnViewData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new BasePairShowDialog(Client).setVisible(true);
			}
		});
		btnViewData.setBounds(137, 389, 113, 27);
		add(btnViewData);

	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.setColor(Color.BLACK);

		int useTAline = rdbtnUseTextAera.getHeight() + rdbtnUseTextAera.getY() + 10;
		g.drawLine(0, useTAline, getWidth(), useTAline);

		int selectedfileline = importedFileLable.getHeight() + importedFileLable.getY() + 10;
		g.drawLine(0, selectedfileline, getWidth(), selectedfileline);

		int encodeline = btnEncode.getHeight() + btnEncode.getY() + 10;
		g.drawLine(0, encodeline, getWidth(), encodeline);
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.setContentPane(new DataPanel(new Client()));
		jf.setBounds(10, 10, 1100, 700);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
}
