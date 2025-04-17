package view.bcp;

import javax.swing.JPanel;

import client.Client;
import constant.BCPConstant;
import constant.CommonClass;
import constant.RegExConstant;
import constant.SysConstant;
import constant.ViewConstant;
import constant.interaction.LoggedConstant;
import cryptography.BCPForClient;
import cryptography.PP;
import method.CommonMethod;
import view.BCPParameterDialog;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

@SuppressWarnings("unused")
public class BCPPanel extends JPanel
		implements SysConstant, BCPConstant, CommonClass, RegExConstant, ViewConstant, LoggedConstant {

	private static final long serialVersionUID = 1L;

	private Client client;

	private JButton btnGetPp;
	private JButton btnGenerateKeyPair;

	private JButton btnN;
	private JButton btnK;
	private JButton btnG;

	private JButton btnPublicKey;
	private JButton btnSecretKey;
	private JButton btnInputPp;
	private JButton btnOutputPp;
	private JButton btnValidatePp;
	private JButton btnInputKeyPair;
	private JButton btnOutputKeyPair;
	private JButton btnValidatePublicKey;

	private JButton[] btnGroupPPAll;
	private JButton[] btnGroupPPGen;
	private JButton[] btnGroupPPDisplay;

	private JButton[] btnGroupKeyPairAll;
	private JButton[] btnGroupKeyPairDisplay;
	private JButton[] btnGroupKeyPairGen;

	private JButton[] btnGroupNetwork;

	private JButton btnResetBcp;

	private JLabel BCPStatusLabel;
	private JLabel lblPpStatus;
	private JLabel PPStatusLabel;
	private JLabel lblKeyPairStatus;
	private JLabel keyPairStatusLabel;

	private String username;

	private Socket mainsocket;

	private ObjectInputStream obfromc;

	private ObjectOutputStream obtoc;

	/**
	 * Create the panel.
	 */
	public BCPPanel(Client client) {
		this.client = client;
		username = client.getUsername();
		mainsocket = client.getSocket();
		obfromc = client.getObfromc();
		obtoc = client.getObtoc();
		setLayout(null);

		btnGetPp = new JButton("Get PP");
		btnGetPp.setEnabled(false);
		btnGetPp.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				BCPForClient bcp = client.getBcp();
				if (bcp == null) {
					JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
					return;
				}
				if (bcp.isPPSet()) {
					int result = JOptionPane.showConfirmDialog(null,
							"Public parameters have already been set,do you rellay want to override them ?",
							QUESTION_TITLE, JOptionPane.YES_NO_OPTION, QUESTION_MESSAGE_JOPT);
					if (result != JOptionPane.YES_OPTION) {
						return;
					}
				}
				try {
					obtoc.writeObject(BCP_OPER);
					Object response = obfromc.readObject();
					if (response.equals(BCP_PERMIT)) {
						obtoc.writeObject(BCP_GETPP);

						Object getresponse = obfromc.readObject();
						if (getresponse.equals(BCP_GETPP_PERMIT)) {

							Object pp = obfromc.readObject();
							if (pp instanceof PP) {
								bcp.setPP((PP) pp);
							} else {
								JOptionPane.showMessageDialog(null, "Data format error!", ERROR_TITLE,
										ERROR_MESSAGE_JOPT);
								return;
							}

						} else if (getresponse.equals(BCP_GETPP_DENY)) {
							JOptionPane.showMessageDialog(null, "Server doesn't support get PP operation now!",
									ERROR_TITLE, ERROR_MESSAGE_JOPT);
							return;
						}
					} else if (response.equals(BCP_DNEY)) {
						JOptionPane.showMessageDialog(null, "Server doesn't support BCP operation now!", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
						return;
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
				CommonMethod.setComponentsEnable(btnGroupKeyPairGen);
				CommonMethod.setComponentsEnable(btnGroupPPAll);
				PPStatusLabel.setText(BCP_SET);
			}
		});
		btnGetPp.setBounds(10, 238, 120, 23);

		add(btnGetPp);

		btnN = new JButton("N");
		btnN.addActionListener((e) -> {
			BCPForClient bcp = client.getBcp();
			if (bcp == null) {
				JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			PP pp = bcp.getPP();
			if (pp == null) {
				JOptionPane.showMessageDialog(null, PP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			BigInteger NN = pp.getN();
			if (NN == null) {
				JOptionPane.showMessageDialog(null, N + " " + UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			new BCPParameterDialog(N, NN.toString(), NN.bitLength()).setVisible(true);
		});
		btnN.setEnabled(false);
		btnN.setBounds(10, 119, 90, 23);
		add(btnN);

		btnK = new JButton("k");
		btnK.addActionListener((e) -> {
			BCPForClient bcp = client.getBcp();
			if (bcp == null) {
				JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			PP pp = bcp.getPP();
			if (pp == null) {
				JOptionPane.showMessageDialog(null, PP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			BigInteger kk = pp.getK();
			if (kk == null) {
				JOptionPane.showMessageDialog(null, k + " " + UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			new BCPParameterDialog(k, kk.toString(), kk.bitLength()).setVisible(true);
		});
		btnK.setEnabled(false);
		btnK.setBounds(110, 119, 90, 23);
		add(btnK);

		btnG = new JButton("g");
		btnG.addActionListener((e) -> {
			BCPForClient bcp = client.getBcp();
			if (bcp == null) {
				JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			PP pp = bcp.getPP();
			if (pp == null) {
				JOptionPane.showMessageDialog(null, PP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			BigInteger gg = pp.getG();
			if (gg == null) {
				JOptionPane.showMessageDialog(null, g + " " + UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			new BCPParameterDialog(g, gg.toString(), gg.bitLength()).setVisible(true);

		});
		btnG.setEnabled(false);
		btnG.setBounds(210, 119, 90, 23);
		add(btnG);

		btnGenerateKeyPair = new JButton("Generate key pair");
		btnGenerateKeyPair.setEnabled(false);
		btnGenerateKeyPair.addActionListener((e) -> {

			BCPForClient bcp = client.getBcp();
			if (bcp == null) {
				JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}

			PP pp = bcp.getPP();
			if (pp == null) {
				JOptionPane.showMessageDialog(null, PP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}

			if (bcp.isKeyPairSet()) {
				int result = JOptionPane.showConfirmDialog(null,
						"Key pair has already been set,do you rellay want to override it ?", QUESTION_TITLE,
						JOptionPane.YES_NO_OPTION, QUESTION_MESSAGE_JOPT);
				if (result != JOptionPane.YES_OPTION) {
					return;
				}
			}

			BigInteger[] keypair = BCPForClient.keyGen(pp.getN(), pp.getG());
			bcp.setH(keypair[0]);
			bcp.setA(keypair[1]);

			CommonMethod.setComponentsEnable(btnGroupKeyPairAll);
			keyPairStatusLabel.setText(BCP_SET);
		});
		btnGenerateKeyPair.setBounds(10, 162, 170, 23);
		add(btnGenerateKeyPair);

		btnPublicKey = new JButton("Public key");
		btnPublicKey.addActionListener((e) -> {
			BCPForClient bcp = client.getBcp();
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
			new BCPParameterDialog("Public key", h.toString(), h.bitLength()).setVisible(true);
		});
		btnPublicKey.setEnabled(false);
		btnPublicKey.setBounds(10, 195, 120, 23);
		add(btnPublicKey);

		btnSecretKey = new JButton("Secret key");
		btnSecretKey.addActionListener((e) -> {
			BCPForClient bcp = client.getBcp();
			if (bcp == null) {
				JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			PP pp = bcp.getPP();
			if (pp == null) {
				JOptionPane.showMessageDialog(null, PP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			BigInteger a = bcp.getA();
			if (a == null) {
				JOptionPane.showMessageDialog(null, "Secret key " + UNSET_NOTIFICATION, ERROR_TITLE,
						ERROR_MESSAGE_JOPT);
				return;
			}
			new BCPParameterDialog("Secret key", a.toString(), a.bitLength()).setVisible(true);
		});
		btnSecretKey.setEnabled(false);
		btnSecretKey.setBounds(146, 195, 120, 23);
		add(btnSecretKey);

		btnInputPp = new JButton("Input PP");
		btnInputPp.setEnabled(false);
		btnInputPp.addActionListener((e) -> {

			BCPForClient bcp = client.getBcp();
			if (bcp == null) {
				JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			if (bcp.isPPSet()) {
				int result = JOptionPane.showConfirmDialog(null,
						"Public parameters have already been set,do you rellay want to override them ?", QUESTION_TITLE,
						JOptionPane.YES_NO_OPTION, QUESTION_MESSAGE_JOPT);
				if (result != JOptionPane.YES_OPTION) {
					return;
				}
			}

			JFileChooser jfc = new JFileChooser(DESKTOP_PATH);
			jfc.setFileFilter(new SingleClassFileFilter(PP_EXTENSION));
			jfc.setAcceptAllFileFilterUsed(false);// 禁止所有文件
			jfc.showOpenDialog(new JPanel());
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			File file = jfc.getSelectedFile();
			if (file == null) {
				return;
			}

			LinkedHashMap<String, String> map = new LinkedHashMap<>();
			try (Scanner scanner = new Scanner(file)) {
				while (scanner.hasNext()) {
					String k = scanner.next();
					if (!PP_NAME_SET.contains(k)) {
						JOptionPane.showMessageDialog(null, "Key is illegal!", ERROR_TITLE, JOptionPane.ERROR_MESSAGE,
								null);
						return;
					}
					if (!"=".equals(scanner.next())) {
						JOptionPane.showMessageDialog(null, "Format error", ERROR_TITLE, JOptionPane.ERROR_MESSAGE,
								null);
						return;
					}
					String v = scanner.next();
					if (!v.matches(DIGIT_REGEX)) {
						JOptionPane.showMessageDialog(null, "Value is illegal!", ERROR_TITLE, JOptionPane.ERROR_MESSAGE,
								null);
						return;
					}
					map.put(k, v);
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}

			try {
				bcp.setPP(new PP(map));
			} catch (NullPointerException e2) {
				JOptionPane.showMessageDialog(null, "Format error", ERROR_TITLE, JOptionPane.ERROR_MESSAGE, null);
				return;
			}

			CommonMethod.setComponentsEnable(btnGroupPPAll);
			CommonMethod.setComponentsEnable(btnGroupKeyPairGen);
			PPStatusLabel.setText(BCP_SET);
		});
		btnInputPp.setBounds(10, 86, 110, 23);
		add(btnInputPp);

		btnOutputPp = new JButton("Output PP");
		btnOutputPp.setEnabled(false);
		btnOutputPp.addActionListener((e) -> {

			BCPForClient bcp = client.getBcp();
			if (bcp == null) {
				JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			PP pp = bcp.getPP();
			if (pp == null) {
				JOptionPane.showMessageDialog(null, PP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}

			JFileChooser jfc = new JFileChooser(DESKTOP_PATH);
			jfc.setFileFilter(new SingleClassFileSaveFilter("pp"));
			jfc.setAcceptAllFileFilterUsed(false);
			// jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//设置不设置都不影响savaDialog
			jfc.showSaveDialog(new JPanel());
			File file = jfc.getSelectedFile();
			if (file == null) {
				return;
			}
			String path = file.getAbsolutePath() + "." + PP_EXTENSION;
			try (PrintWriter pw = new PrintWriter(path)) {
				pw.println(N + " = " + pp.getN());
				pw.println(k + " = " + pp.getK());
				pw.println(g + " = " + pp.getG());
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		});
		btnOutputPp.setBounds(130, 86, 110, 23);
		add(btnOutputPp);

		btnValidatePp = new JButton("Validate PP");// 向服务器验证PP是否正确
		btnValidatePp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BCPForClient bcp = client.getBcp();
				if (bcp == null) {
					JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
					return;
				}
				PP pp = bcp.getPP();
				if (pp == null) {
					JOptionPane.showMessageDialog(null, PP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
					return;
				}
			}
		});
		btnValidatePp.setEnabled(false);
		btnValidatePp.setBounds(10, 284, 120, 23);
		add(btnValidatePp);

		btnInputKeyPair = new JButton("Input key pair");
		btnInputKeyPair.setEnabled(false);
		btnInputKeyPair.addActionListener((e) -> {

			BCPForClient bcp = client.getBcp();
			if (bcp == null) {
				JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			if (!bcp.isPPSet()) {
				JOptionPane.showMessageDialog(null, PP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			if (bcp.isKeyPairSet()) {
				int result = JOptionPane.showConfirmDialog(null,
						"Key pair has already been set,do you rellay want to override it ?", QUESTION_TITLE,
						JOptionPane.YES_NO_OPTION, QUESTION_MESSAGE_JOPT);
				if (result != JOptionPane.YES_OPTION) {
					return;
				}
			}

			JFileChooser jfc = new JFileChooser(DESKTOP_PATH);
			jfc.setFileFilter(new SingleClassFileFilter(KEY_PAIR_EXTENSION));
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.showOpenDialog(new JPanel());
			File file = jfc.getSelectedFile();
			if (file == null) {
				return;
			}

			try (Scanner scanner = new Scanner(file)) {
				String pk = scanner.next();
				if (!KEY_PAIR_NAME_SET.contains(pk)) {
					return;
				}
				if (!"=".equals(scanner.next())) {
					return;
				}
				BigInteger h = new BigInteger(scanner.next());
				bcp.setH(h);

				String sk = scanner.next();
				if (!KEY_PAIR_NAME_SET.contains(sk)) {
					return;
				}
				if (!"=".equals(scanner.next())) {
					return;
				}
				BigInteger a = new BigInteger(scanner.next());
				bcp.setA(a);

				CommonMethod.setComponentsEnable(btnGroupKeyPairAll);
				keyPairStatusLabel.setText(BCP_SET);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		});
		btnInputKeyPair.setBounds(190, 162, 150, 23);
		add(btnInputKeyPair);

		btnOutputKeyPair = new JButton("Output key pair");
		btnOutputKeyPair.setEnabled(false);
		btnOutputKeyPair.addActionListener((e) -> {
			BCPForClient bcp = client.getBcp();
			if (bcp == null) {
				JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}

			if (!bcp.isKeyPairSet()) {
				JOptionPane.showMessageDialog(null, KEYPAIR_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			BigInteger h = bcp.getH();
			BigInteger a = bcp.getA();

			JFileChooser jfc = new JFileChooser(DESKTOP_PATH);
			jfc.setFileFilter(new SingleClassFileSaveFilter(KEY_PAIR_EXTENSION));
			jfc.setAcceptAllFileFilterUsed(false);
			// jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);// 对save来说，设置不设置不影响
			jfc.showSaveDialog(new JPanel());
			// jfc.showOpenDialog(null);
			File file = jfc.getSelectedFile();
			if (file == null) {
				return;
			}
			String path = file.getAbsolutePath() + "." + KEY_PAIR_EXTENSION;
			try (PrintWriter pw = new PrintWriter(path)) {
				pw.println(PUBLIC_KEY + " = " + h);
				pw.println(SECRET_KEY + " = " + a);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnOutputKeyPair.setBounds(350, 162, 160, 23);
		add(btnOutputKeyPair);

		btnValidatePublicKey = new JButton("Validate public key");
		btnValidatePublicKey.setEnabled(false);
		btnValidatePublicKey.addActionListener((e) -> {
			BCPForClient bcp = client.getBcp();
			if (bcp == null) {
				JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
			PP pp = bcp.getPP();
			if (pp == null) {
				JOptionPane.showMessageDialog(null, PP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
				return;
			}
		});
		btnValidatePublicKey.setBounds(146, 284, 211, 23);
		add(btnValidatePublicKey);

		JLabel lblKeyPairPart = new JLabel("Key pair part");
		lblKeyPairPart.setBounds(530, 164, 120, 18);
		add(lblKeyPairPart);

		JLabel lblPpPart = new JLabel("PP part");
		lblPpPart.setBounds(530, 86, 72, 18);
		add(lblPpPart);

		JLabel lblNetworkPart = new JLabel("Network part");
		lblNetworkPart.setBounds(530, 238, 100, 18);
		add(lblNetworkPart);

		JButton btnInitializeBcp = new JButton("Initialize BCP");
		btnInitializeBcp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (client.isBCPSet()) {
					int confirm = JOptionPane.showConfirmDialog(null,
							"BCP's instance has already been set,do you want to override it?", QUESTION_TITLE,
							JOptionPane.YES_NO_OPTION, QUESTION_MESSAGE_JOPT);
					if (confirm != JOptionPane.OK_OPTION) {
						return;
					}
				}
				client.newBCP();
				BCPStatusLabel.setText(BCP_SET);
				CommonMethod.setComponentsEnable(btnGroupPPGen);
				btnResetBcp.setEnabled(true);
			}
		});
		btnInitializeBcp.setBounds(10, 10, 150, 23);
		add(btnInitializeBcp);

		JLabel lblBcpStatus = new JLabel("BCP status:");
		lblBcpStatus.setBounds(10, 43, 100, 23);
		add(lblBcpStatus);

		BCPStatusLabel = new JLabel(BCP_UNSET);
		BCPStatusLabel.setBounds(120, 43, 60, 23);
		add(BCPStatusLabel);

		JLabel lblBcpPart = new JLabel("BCP part");
		lblBcpPart.setBounds(530, 10, 86, 18);
		add(lblBcpPart);

		btnResetBcp = new JButton("Reset BCP");
		btnResetBcp.setEnabled(false);
		btnResetBcp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (client.isBCPSet()) {
					int confirm = JOptionPane.showConfirmDialog(null,
							"BCP's instance has been set,do you want to reset it?", QUESTION_TITLE,
							JOptionPane.YES_NO_OPTION, QUESTION_MESSAGE_JOPT);
					if (confirm != JOptionPane.OK_OPTION) {
						return;
					}
				}
				BCPStatusLabel.setText(BCP_UNSET);
				CommonMethod.setComponentsDisable(btnGroupKeyPairAll);
				CommonMethod.setComponentsDisable(btnGroupNetwork);
				CommonMethod.setComponentsDisable(btnGroupPPAll);
				btnResetBcp.setEnabled(false);
				client.resetBCP();
			}
		});
		btnResetBcp.setBounds(170, 10, 113, 23);
		add(btnResetBcp);

		lblPpStatus = new JLabel("PP status:");
		lblPpStatus.setBounds(190, 45, 100, 18);
		add(lblPpStatus);

		PPStatusLabel = new JLabel(BCP_UNSET);
		PPStatusLabel.setBounds(300, 45, 60, 18);
		add(PPStatusLabel);

		lblKeyPairStatus = new JLabel("Key pair status:");
		lblKeyPairStatus.setBounds(370, 45, 130, 18);
		add(lblKeyPairStatus);

		keyPairStatusLabel = new JLabel(BCP_UNSET);
		keyPairStatusLabel.setBounds(510, 45, 60, 18);
		add(keyPairStatusLabel);

		JButton btnGetPublicKey = new JButton("Get Public Key");
		btnGetPublicKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnGetPublicKey.setEnabled(false);
		btnGetPublicKey.setBounds(146, 238, 211, 23);
		add(btnGetPublicKey);

		btnGroupPPAll = new JButton[] { btnInputPp, btnOutputPp, btnValidatePp, btnN, btnK, btnG };
		btnGroupPPDisplay = new JButton[] { btnN, btnK, btnG };
		btnGroupPPGen = new JButton[] { btnGetPp, btnInputPp };

		btnGroupKeyPairAll = new JButton[] { btnGenerateKeyPair, btnInputKeyPair, btnOutputKeyPair, btnPublicKey,
				btnSecretKey, btnValidatePublicKey };
		btnGroupKeyPairGen = new JButton[] { btnGenerateKeyPair, btnInputKeyPair };
		btnGroupKeyPairDisplay = new JButton[] { btnPublicKey, btnSecretKey };

		btnGroupNetwork = new JButton[] { btnGetPp, btnValidatePp, btnValidatePublicKey };
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.setColor(Color.BLACK);

		int firstline = BCPStatusLabel.getY() + BCPStatusLabel.getHeight() + 10;
		g.drawLine(0, firstline, this.getWidth(), firstline);

		int secondline = btnN.getY() + btnN.getHeight() + 10;
		g.drawLine(0, secondline, this.getWidth(), secondline);

		int thirdline = btnPublicKey.getY() + btnPublicKey.getHeight() + 10;
		g.drawLine(0, thirdline, getWidth(), thirdline);

		int forthline = btnGetPp.getY() + btnGetPp.getHeight() + 10;
		g.drawLine(0, forthline, getWidth(), forthline);

		int fifthline = btnValidatePp.getY() + btnValidatePp.getHeight() + 10;
		g.drawLine(0, fifthline, getWidth(), fifthline);
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.setSize(900, 900);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setContentPane(new BCPPanel(new Client()));
		jf.setVisible(true);
	}

	// --------------------------------------------------------
	public static void readBCPFromFile() {

	}
}
