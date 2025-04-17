package view;

import javax.swing.JPanel;

import constant.CommonClass;
import constant.SysConstant;
import constant.CommonClass.tfFocusListener;
import cryptography.BCP;
import method.CommonMethod;
import server_s.ServerS;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class BCPPanel extends JPanel implements SysConstant {

	private static final long serialVersionUID = -9141469658919544244L;

	private ServerS S;

	// BCPTypeCB;
	private String[] BCPTypeCBArray = { "Initialize", "Input" };
	private DefaultComboBoxModel<String> BCPTypeCBModel;
	private JComboBox<String> BCPTypeCB;

	private JButton btnN;

	private JButton btnk;

	private JButton btng;
	private JLabel lblPp;
	private JButton btnStart;
	private JLabel lblMk;
	private JButton btnMp;
	private JButton btnMq;
	private JButton btnP;
	private JButton btnQ;
	private JTextField kappaTF;
	private JTextField certaintyTF;

	public final static String KAPPA = "kappa";
	public final static String CERTAINTY = "certainty";

	private tfFocusListener kappatfFocusListener;

	private tfFocusListener certaintytfFocusListener;

	private JButton[] BCPButtons;
	private JLabel lblBasicParameters;
	private JButton btnBitlength;
	private JButton btnCertainty;

	private JButton btnOutput;

	/**
	 * Create the panel.
	 */
	public BCPPanel(ServerS s) {
		this.S = s;

		// BCPPanel
		setLayout(null);
		this.setBounds(10, 10, 742, 600);

		// BCPComboBox Model
		BCPTypeCBModel = new DefaultComboBoxModel<>(BCPTypeCBArray);

		// BCPComboBox
		BCPTypeCB = new JComboBox<String>();
		BCPTypeCB.setModel(BCPTypeCBModel);
		BCPTypeCB.addItemListener((e) -> {
			if (BCPTypeCBArray[0].equals(BCPTypeCB.getSelectedItem())) {
				// kappaTF.addFocusListener(kappatfFocusListener);
				// certaintyTF.addFocusListener(certaintytfFocusListener);
				kappaTF.setEnabled(true);
				certaintyTF.setEnabled(true);
				btnStart.setText("Initialize");
			} else {
				// kappaTF.removeFocusListener(kappatfFocusListener);
				// certaintyTF.removeFocusListener(certaintytfFocusListener);
				kappaTF.setEnabled(false);
				certaintyTF.setEnabled(false);
				btnStart.setText("Input");
			}
		});
		BCPTypeCB.setBounds(134, 10, 93, 23);
		add(BCPTypeCB);

		//
		JLabel lblBcpInitialMode = new JLabel("BCP Initial Mode:");
		lblBcpInitialMode.setBounds(10, 10, 114, 23);
		add(lblBcpInitialMode);

		// N JButton
		btnN = new JButton("N");
		btnN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BigInteger N = BCPPanel.this.S.getBcp().getPP().getN();
				new BCPParameterDialog("N", N.toString(), N.bitLength()).setVisible(true);
			}
		});
		btnN.setEnabled(false);
		btnN.setBounds(134, 96, 93, 23);
		add(btnN);

		// K JButton
		btnk = new JButton("k");
		btnk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BigInteger k = BCPPanel.this.S.getBcp().getPP().getK();
				new BCPParameterDialog("k", k.toString(), k.bitLength()).setVisible(true);
			}
		});
		btnk.setEnabled(false);
		btnk.setBounds(237, 96, 93, 23);
		add(btnk);

		//
		btng = new JButton("g");
		btng.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BigInteger g = BCPPanel.this.S.getBcp().getPP().getG();
				new BCPParameterDialog("g", g.toString(), g.bitLength()).setVisible(true);
			}
		});
		btng.setEnabled(false);
		btng.setBounds(340, 96, 93, 23);
		add(btng);

		lblPp = new JLabel("Public Parameters:");
		lblPp.setBounds(10, 96, 114, 23);
		add(lblPp);

		btnStart = new JButton("Initialize");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (BCPTypeCBArray[0].equals(BCPTypeCB.getSelectedItem())) {
					int kappa = Integer.valueOf(kappaTF.getText());
					int certainty = Integer.valueOf(certaintyTF.getText());
					if (kappa > 2048 || kappa < 128) {
						JOptionPane.showMessageDialog(BCPPanel.this, "kappa is smaller than 128 or bigger than 2048");
						return;
					}
					if (certainty < 100 || certainty > 1000) {
						JOptionPane.showMessageDialog(BCPPanel.this,
								"certainty is smaller than 100 or bigger than 300");
						return;
					}
					new Thread(() -> {
						CommonMethod.setComponentsDisable(BCPButtons);
						S.initializebcp(kappa, certainty);
						if (BCPPanel.this.S.isBCPSet()) {
							CommonMethod.setComponentsEnable(BCPButtons);
						}
					}).start();

				} else {
					// set
					JFileChooser jfc = new JFileChooser(DESKTOP_PATH);
					jfc.setFileFilter(new CommonClass.SingleClassFileFilter(BCP.BCP_FILE_EXTENSION));
					jfc.setAcceptAllFileFilterUsed(false);// 禁止所有文件
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					jfc.showOpenDialog(new JPanel());
					File file = jfc.getSelectedFile();
					if (file == null || file.isDirectory()) {
						return;
					}
					LinkedHashMap<String, String> map = new LinkedHashMap<>();
					try (Scanner scan = new Scanner(file)) {
						while (scan.hasNext()) {
							String key = scan.next();
							if (!BCP.membersnameset.contains(key)) {
								JOptionPane.showMessageDialog(null, "There's no such key like " + key + " !");
								return;
							}
							if (!"=".equals(scan.next())) {
								JOptionPane.showMessageDialog(null, "equality sign's position error!");
								return;
							}
							String value = scan.next();
							map.put(key, value);
						}
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (map.size() < BCP.membersnameset.size() - 2) {
						JOptionPane.showMessageDialog(null,
								"The number of entries should at least be " + BCP.membersnameset.size());
						return;
					}
					if (!BCPPanel.this.S.startbcp(map)) {
						JOptionPane.showMessageDialog(null, "BCP file format error!");
						return;
					}
					CommonMethod.setComponentsEnable(BCPButtons);
				}
			}
		});
		btnStart.setBounds(649, 10, 93, 23);
		add(btnStart);

		// MK Label
		lblMk = new JLabel("Master Secret:");
		lblMk.setBounds(10, 139, 114, 23);
		add(lblMk);

		btnMp = new JButton("p'");
		btnMp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BigInteger mp = BCPPanel.this.S.getBcp().getMK().getMp();
				new BCPParameterDialog("p'", mp.toString(), mp.bitLength()).setVisible(true);
			}
		});
		btnMp.setEnabled(false);
		btnMp.setBounds(237, 139, 93, 23);
		add(btnMp);

		btnMq = new JButton("q'");
		btnMq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BigInteger mq = BCPPanel.this.S.getBcp().getMK().getMq();
				new BCPParameterDialog("q'", mq.toString(), mq.bitLength()).setVisible(true);
			}
		});
		btnMq.setEnabled(false);
		btnMq.setBounds(443, 139, 93, 23);
		add(btnMq);

		btnP = new JButton("p");
		btnP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BigInteger p = BCPPanel.this.S.getBcp().getMK().getP();
				new BCPParameterDialog("p", p.toString(), p.bitLength()).setVisible(true);
			}
		});
		btnP.setEnabled(false);
		btnP.setBounds(134, 139, 93, 23);
		add(btnP);

		btnQ = new JButton("q");
		btnQ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BigInteger q = BCPPanel.this.S.getBcp().getMK().getQ();
				new BCPParameterDialog("q", q.toString(), q.bitLength()).setVisible(true);
			}
		});
		btnQ.setEnabled(false);
		btnQ.setBounds(340, 139, 93, 23);
		add(btnQ);

		// kappa TextField
		kappaTF = new JTextField();
		kappaTF.setDocument(new CommonClass.PortTFDocument(kappaTF));
		kappaTF.setText(BCP.DEFAULTKAPPASTR);// 这一步必须在设置doc之前，设置操作会清空文本内容
		kappatfFocusListener = new CommonClass.tfFocusListener(BCP.DEFAULTKAPPASTR, kappaTF);
		kappaTF.addFocusListener(kappatfFocusListener);
		kappaTF.setBounds(340, 10, 93, 23);
		kappaTF.setColumns(10);
		add(kappaTF);

		// certainty TextField
		certaintyTF = new JTextField();
		certaintyTF.setDocument(new CommonClass.PortTFDocument(certaintyTF));
		certaintyTF.setText(BCP.DEFAULTCERTAINTYSTR);
		certaintytfFocusListener = new CommonClass.tfFocusListener(BCP.DEFAULTCERTAINTYSTR, certaintyTF);
		certaintyTF.addFocusListener(certaintytfFocusListener);
		certaintyTF.setBounds(546, 10, 93, 23);
		certaintyTF.setColumns(10);
		add(certaintyTF);

		JLabel lblBitlength = new JLabel("Bitlength:");
		lblBitlength.setBounds(237, 10, 93, 23);
		add(lblBitlength);

		JLabel lblCertainty = new JLabel("Certainty:");
		lblCertainty.setBounds(443, 10, 93, 23);
		add(lblCertainty);

		JLabel lblBcpOutput = new JLabel("BCP Output:");
		lblBcpOutput.setBounds(10, 182, 114, 23);
		add(lblBcpOutput);

		// Output Button
		btnOutput = new JButton("Output");
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BCP bcp = BCPPanel.this.S.getBcp();
				if (bcp == null) {
					return;
				}

				JFileChooser jfc = new JFileChooser(DESKTOP_PATH);
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setFileFilter(new CommonClass.SingleClassFileSaveFilter(BCP.BCP_FILE_EXTENSION));
				jfc.showSaveDialog(null);
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只允许打开目录
				// jfc.setSelectedFile(new File(".bcp"));// 所有在文件名栏中打入的自动加上该扩展名，但也会强制文件名只能为.bcp
				File file = jfc.getSelectedFile();
				if (file == null) {
					return;
				}
				String path = file.getAbsolutePath() + "." + BCP.BCP_FILE_EXTENSION;
				try (PrintWriter pr = new PrintWriter(path);) {
					Iterator<Entry<String, String>> iterator = BCPPanel.this.S.getBcp().getMembers().entrySet()
							.iterator();
					while (iterator.hasNext()) {
						Entry<String, String> member = iterator.next();
						pr.println(member.getKey() + " = " + member.getValue());
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnOutput.setEnabled(false);
		btnOutput.setBounds(134, 182, 93, 23);
		add(btnOutput);

		lblBasicParameters = new JLabel("Basic Parameters:");
		lblBasicParameters.setBounds(10, 53, 114, 23);
		add(lblBasicParameters);

		btnBitlength = new JButton("Bitlength");
		btnBitlength.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, s.getBcp().getKappa(), "Bitlength", 1, null);
			}
		});
		btnBitlength.setEnabled(false);
		btnBitlength.setBounds(134, 53, 93, 23);
		add(btnBitlength);

		btnCertainty = new JButton("Certainty");
		btnCertainty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, s.getBcp().getCertainty(), "Certainty", 1, null);
			}
		});
		btnCertainty.setEnabled(false);
		btnCertainty.setBounds(237, 53, 93, 23);
		add(btnCertainty);

		// button array
		BCPButtons = new JButton[] { btnN, btnk, btng, btnP, btnMp, btnQ, btnMq, btnOutput, btnBitlength,
				btnCertainty };
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);// 不知道作用如何,去掉也没有区别
		g.setColor(Color.black);
		int firstline = BCPTypeCB.getY() + BCPTypeCB.getHeight() + 10;
		g.drawLine(0, firstline, this.getWidth(), firstline);// (x1,y1)(x2,y2)两点唯一确定一条线

		int secondline = btnBitlength.getY() + btnBitlength.getHeight() + 10;
		g.drawLine(0, secondline, this.getWidth(), secondline);

		int thirdline = btnN.getY() + btnN.getHeight() + 10;
		g.drawLine(0, thirdline, this.getWidth(), thirdline);

		int forthline = btnMp.getY() + btnMp.getHeight() + 10;
		g.drawLine(0, forthline, getWidth(), forthline);

		int fifthline = btnOutput.getY() + btnOutput.getHeight() + 10;
		g.drawLine(0, fifthline, getWidth(), fifthline);
	}


}
