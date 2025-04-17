package view;

import javax.swing.JPanel;


import constant.BCPConstant;
import constant.CommonClass;
import constant.RegExConstant;
import constant.SysConstant;
import constant.ViewConstant;
import constant.interaction.InteractWithSConstant;
import cryptography.BCPForC;
import cryptography.PP;
import method.CommonMethod;
import server_c.ServerC;

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
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

@SuppressWarnings("unused")
public class BCPPanel extends JPanel
		implements SysConstant, BCPConstant, CommonClass, RegExConstant, ViewConstant, InteractWithSConstant {

	private static final long serialVersionUID = 1L;

	private ServerC C;

	private JButton btnGetPp;

	private JButton btnN;
	private JButton btnK;
	private JButton btnG;
	private JButton btnInputPp;
	private JButton btnOutputPp;
	private JButton btnValidatePp;

	private JButton[] btnGroupPPAll;
	private JButton[] btnGroupPPGen;
	private JButton[] btnGroupPPDisplay;

	private JButton[] btnGroupNetwork;

	private JButton btnResetBcp;

	private JLabel BCPStatusLabel;
	private JLabel lblPpStatus;
	private JLabel PPStatusLabel;
	private JLabel lblKeyPairStatus;
	private JLabel keyPairStatusLabel;

	/**
	 * Create the panel.
	 */
	public BCPPanel(ServerC c) {
		this.C = c;
		setLayout(null);

		btnGetPp = new JButton("Get PP");
		btnGetPp.setEnabled(false);
		btnGetPp.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				BCPForC bcp = c.getBcp();
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
					ObjectOutputStream obtos = C.getObToS();
					ObjectInputStream obfroms = C.getObFromS();
					if (obtos == null || obfroms == null) {
						JOptionPane.showMessageDialog(null, "Unconnected to the server S!", WARNING_TITLE,
								WARNING_MESSAGE_JOPT);
						return;
					}
					obtos.writeObject(GETPP);
					Object response = obfroms.readObject();
					if (response.equals(InteractWithSConstant.BCP_NOTSET)) {
						JOptionPane.showMessageDialog(null, "Server's BCP was not set! ", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					} else if (response.equals(PP_NOTSET)) {
						JOptionPane.showMessageDialog(null, "Server's PP was not set! ", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					} else if (response.equals(GETPP_DENY)) {
						JOptionPane.showMessageDialog(null, "Server didn't supprot this operation now! ", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					} else if (response.equals(GETPP_PERMIT)) {
						Object pp = obfroms.readObject();
						if (pp instanceof PP) {
							bcp.setPp((PP) pp);
						} else {
							JOptionPane.showMessageDialog(null, "Illegal pp: " + pp, ERROR_TITLE, ERROR_MESSAGE_JOPT);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Error response: " + response, ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				CommonMethod.setComponentsEnable(btnGroupPPAll);
				PPStatusLabel.setText(BCP_SET);
			}
		});
		btnGetPp.setBounds(10, 238, 120, 23);

		add(btnGetPp);

		btnN = new JButton("N");
		btnN.addActionListener((e) -> {
			BCPForC bcp = c.getBcp();
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
			BCPForC bcp = c.getBcp();
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
			BCPForC bcp = c.getBcp();
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

		btnInputPp = new JButton("Input PP");
		btnInputPp.setEnabled(false);
		btnInputPp.addActionListener((e) -> {

			BCPForC bcp = c.getBcp();
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

			bcp.setPp(new PP(map));

			CommonMethod.setComponentsEnable(btnGroupPPAll);
			PPStatusLabel.setText(BCP_SET);
		});
		btnInputPp.setBounds(10, 86, 110, 23);
		add(btnInputPp);

		btnOutputPp = new JButton("Output PP");
		btnOutputPp.setEnabled(false);
		btnOutputPp.addActionListener((e) -> {

			BCPForC bcp = c.getBcp();
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
				BCPForC bcp = c.getBcp();
				if (bcp == null) {
					JOptionPane.showMessageDialog(null, BCP_UNSET_NOTIFICATION, ERROR_TITLE, ERROR_MESSAGE_JOPT);
					return;
				}

				PP pp = bcp.getPP();
				if (pp == null) {
					JOptionPane.showMessageDialog(null, PP_UNSET_NOTIFICATION, WARNING_TITLE, WARNING_MESSAGE_JOPT);
					return;
				}

				try {
					ObjectOutputStream obtos = C.getObToS();
					ObjectInputStream obfroms = C.getObFromS();
					if (obtos == null || obfroms == null) {
						JOptionPane.showMessageDialog(null, "Unconnected to the server S!", WARNING_TITLE,
								WARNING_MESSAGE_JOPT);
						return;
					}
					obtos.writeObject(GETPP);
					Object response = obfroms.readObject();
					if (response.equals(InteractWithSConstant.BCP_NOTSET)) {
						JOptionPane.showMessageDialog(null, "Server's BCP was not set! ", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					} else if (response.equals(PP_NOTSET)) {
						JOptionPane.showMessageDialog(null, "Server's PP was not set! ", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					} else if (response.equals(GETPP_DENY)) {
						JOptionPane.showMessageDialog(null, "Server didn't supprot this operation now! ", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					} else if (response.equals(GETPP_PERMIT)) {
						Object Ob = obfroms.readObject();
						if (Ob instanceof PP) {
							PP pp2 = (PP) Ob;
							if (pp.equals(pp2)) {
								JOptionPane.showMessageDialog(null, "The set PP is right!", SUCCESS_TITLE,
										INFO_MESSAGE_JOPT);
							} else {
								JOptionPane.showMessageDialog(null, "The set PP is not equals to the S's one!",
										ERROR_TITLE, ERROR_MESSAGE_JOPT);
							}
						} else {
							JOptionPane.showMessageDialog(null, "Illegal pp: " + Ob, ERROR_TITLE, ERROR_MESSAGE_JOPT);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Error response: " + response, ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				CommonMethod.setComponentsEnable(btnGroupPPAll);
				PPStatusLabel.setText(BCP_SET);
			}
		});
		btnValidatePp.setEnabled(false);
		btnValidatePp.setBounds(10, 274, 120, 23);
		add(btnValidatePp);

		JLabel lblPpPart = new JLabel("PP part");
		lblPpPart.setBounds(530, 86, 72, 18);
		add(lblPpPart);

		JLabel lblNetworkPart = new JLabel("Network part");
		lblNetworkPart.setBounds(530, 238, 100, 18);
		add(lblNetworkPart);

		JButton btnInitializeBcp = new JButton("Initialize BCP");
		btnInitializeBcp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (c.isBCPSet()) {
					int confirm = JOptionPane.showConfirmDialog(null,
							"BCP's instance has already been set,do you want to override it?", QUESTION_TITLE,
							JOptionPane.YES_NO_OPTION, QUESTION_MESSAGE_JOPT);
					if (confirm != JOptionPane.OK_OPTION) {
						return;
					}
				}
				c.newBCP();
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
				if (c.isBCPSet()) {
					int confirm = JOptionPane.showConfirmDialog(null,
							"BCP's instance has been set,do you want to reset it?", QUESTION_TITLE,
							JOptionPane.YES_NO_OPTION, QUESTION_MESSAGE_JOPT);
					if (confirm != JOptionPane.OK_OPTION) {
						return;
					}
				}
				BCPStatusLabel.setText(BCP_UNSET);
				CommonMethod.setComponentsDisable(btnGroupNetwork);
				CommonMethod.setComponentsDisable(btnGroupPPAll);
				btnResetBcp.setEnabled(false);
				c.resetBCP();
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

		btnGroupPPAll = new JButton[] { btnInputPp, btnOutputPp, btnValidatePp, btnN, btnK, btnG };
		btnGroupPPDisplay = new JButton[] { btnN, btnK, btnG };
		btnGroupPPGen = new JButton[] { btnGetPp, btnInputPp };

		btnGroupNetwork = new JButton[] { btnGetPp, btnValidatePp };
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

		int forthline = btnGetPp.getY() + btnGetPp.getHeight() + 10;
		g.drawLine(0, forthline, getWidth(), forthline);

	}

	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.setSize(900, 900);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setContentPane(new BCPPanel(new ServerC()));
		jf.setVisible(true);
	}

	// --------------------------------------------------------
	public static void readBCPFromFile() {

	}
}
