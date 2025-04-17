package view.compare;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;

import client.Client;
import constant.ViewConstant;
import constant.interaction.BusinessConstant;
import constant.interaction.LoggedConstant;
import cryptography.BCPForClient;
import cryptography.PP;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.event.ActionEvent;

public class SubmitPanel extends JPanel implements ViewConstant, LoggedConstant, BusinessConstant {

	private static final long serialVersionUID = 1L;
	
	private Client client;
	private ArrayList<String> validlist;

	private JLabel promptLabel;
	private JLabel firstLabel;
	private JLabel secondLabel;
	private SubmitDialog subdia;
	private JLabel lblComputeWithRandom;
	private JLabel lblComputeWithSpecific;
	private JTextArea userTA;
	private JLabel lblNewLabel;
	private JButton btnCheck;
	private JScrollPane validSP;
	private JTextArea validTA;
	private JButton submitSpec;
	private JLabel validLabel;

	public SubmitPanel(Client Client) {
		// TODO Auto-generated constructor stub
		setSize(625, 465);
		setLayout(null);
		client = Client;

		promptLabel = new JLabel("Which mode of computation do you want to choose?");
		promptLabel.setFont(new java.awt.Font("Dialog", 1, 20));
		promptLabel.setBounds(10, 10, 584, 55);
		add(promptLabel);

		firstLabel = new JLabel("1");
		firstLabel.setFont(new java.awt.Font("Dialog", 1, 15));
		firstLabel.setBounds(10, 85, 20, 20);
		add(firstLabel);

		secondLabel = new JLabel("2");
		secondLabel.setBounds(270, 85, 20, 20);
		secondLabel.setFont(new java.awt.Font("Dialog", 1, 15));
		add(secondLabel);

		lblComputeWithRandom = new JLabel("Compute with random users");
		lblComputeWithRandom.setBounds(40, 85, 210, 18);
		add(lblComputeWithRandom);

		lblComputeWithSpecific = new JLabel("Compute with specific users");
		lblComputeWithSpecific.setBounds(300, 85, 228, 18);
		add(lblComputeWithSpecific);

		JButton submitRandom = new JButton("Submit");
		submitRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
				BigInteger[][] encryptedEncodingBasePairs = Client.getEncryptedEncodingBasePairs();
				if (encryptedEncodingBasePairs == null) {
					JOptionPane.showMessageDialog(null, "Encrypted data has not set!", WARNING_TITLE,
							WARNING_MESSAGE_JOPT);
					return;
				}

				ObjectOutputStream obtoc = Client.getObtoc();
				ObjectInputStream obfromc = Client.getObfromc();

				try {
					obtoc.writeObject(DATA_OPER);
					obtoc.writeObject(SUBMIT_RANDOM);
					Object response = obfromc.readObject();
					if (response.equals(SUBMIT_RANDOM_DENY)) {
						JOptionPane.showMessageDialog(null, "Server doesn't support get PP operation now!", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					} else if (response.equals(ALREDAY_SUBMIT)) {
						JOptionPane.showMessageDialog(null,
								"You have already submitted computation request,if you want to submit again, please withdraw the request",
								ERROR_TITLE, ERROR_MESSAGE_JOPT);
					} else if (response.equals(SUBMIT_RANDOM_PERMIT)) {
						obtoc.writeObject(encryptedEncodingBasePairs);
						obtoc.writeObject(h);
						Object result = obfromc.readObject();
						if (result.equals(SUBMIT_RANDOM_SUCCESS)) {
							JOptionPane.showMessageDialog(null, "Successfully submited!", SUCCESS_TITLE,
									INFO_MESSAGE_JOPT);
						} else {
							JOptionPane.showMessageDialog(null, "Submit failed!", ERROR_TITLE, ERROR_MESSAGE_JOPT);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Error response: " + response, ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					}

				} catch (IOException | ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		submitRandom.setBounds(49, 242, 113, 49);
		add(submitRandom);

		JScrollPane userSP = new JScrollPane();
		userSP.setBounds(270, 115, 340, 100);
		add(userSP);

		userTA = new JTextArea();
		userSP.setViewportView(userTA);

		lblNewLabel = new JLabel("Use space to separate different usernames");
		lblNewLabel.setBounds(270, 225, 340, 20);
		add(lblNewLabel);

		btnCheck = new JButton("Check");
		btnCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// 过滤数据，获取用户名
				ArrayList<String> userlist = new ArrayList<>();
				String raw = userTA.getText().trim();
				try (Scanner scanner = new Scanner(raw)) {
					while (scanner.hasNext()) {
						String username = scanner.next();
						userlist.add(username);
					}
				}
				if (userlist.size() == 0) {
					validTA.setText("");
					submitSpec.setEnabled(false);
					validlist = null;
					return;
				}

				// 向服务器验证userlist中的有效用户名
				ObjectInputStream obfromc = client.getObfromc();
				ObjectOutputStream obtoc = client.getObtoc();

				try {
					client.getHeartbeat().setPause(true);

					obtoc.writeObject(DATA_OPER);
					obtoc.writeObject(CHECK_SPECIFIC);

					Object response = obfromc.readObject();
					if (response.equals(CHECK_SPECIFIC_PERMIT))
						;
					else {
						JOptionPane.showMessageDialog(null, "Server doesn't support check data operation now!",
								ERROR_TITLE, ERROR_MESSAGE_JOPT);
						System.out.println(response);
						return;
					}

					// 发送过滤后的列表，拿到有效用户列表
					obtoc.writeObject(userlist);
					@SuppressWarnings("unchecked")
					ArrayList<String> existentlist = (ArrayList<String>) obfromc.readObject();

					validTA.setText("");
					int number = Math.min(existentlist.size(), MAXIMUM_USERNUMBER);
					for (int i = 0; i < number; i++) {
						validTA.append(existentlist.get(i) + ' ');
					}

					if (existentlist.size() != 0 && existentlist.size() <= MAXIMUM_USERNUMBER) {
						submitSpec.setEnabled(true);
						validlist = existentlist;
					} else if (existentlist.size() > MAXIMUM_USERNUMBER) {
						submitSpec.setEnabled(true);
						validlist = (ArrayList<String>) existentlist.subList(0, MAXIMUM_USERNUMBER);
						JOptionPane.showMessageDialog(null, "The number of users you choose is more than "
								+ MAXIMUM_USERNUMBER + ",more than 100 will be abandoned", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					} else {
						submitSpec.setEnabled(false);
						validlist = null;
					}

				} catch (IOException | ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					client.getHeartbeat().setPause(false);
				}

			}
		});
		btnCheck.setBounds(270, 255, 113, 23);
		add(btnCheck);

		validSP = new JScrollPane();
		validSP.setBounds(270, 288, 340, 100);
		add(validSP);

		validTA = new JTextArea();
		validTA.setEditable(false);
		validSP.setViewportView(validTA);

		submitSpec = new JButton("Submit");
		submitSpec.setEnabled(false);
		submitSpec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
				BigInteger[][] encryptedEncodingBasePairs = Client.getEncryptedEncodingBasePairs();
				if (encryptedEncodingBasePairs == null) {
					JOptionPane.showMessageDialog(null, "Encrypted data has not set!", WARNING_TITLE,
							WARNING_MESSAGE_JOPT);
					return;
				}

				ObjectInputStream obfromc = client.getObfromc();
				ObjectOutputStream obtoc = client.getObtoc();

				try {
					obtoc.writeObject(DATA_OPER);
					obtoc.writeObject(SUBMIT_SPECIFIC);

					Object response = obfromc.readObject();

					if (response.equals(SUBMIT_SPECIFIC_DENY)) {
						JOptionPane.showMessageDialog(null, "Server doesn't support submit operation now!", ERROR_TITLE,
								ERROR_MESSAGE_JOPT);
					} else if (response.equals(ALREDAY_SUBMIT)) {
						JOptionPane.showMessageDialog(null,
								"You have already submitted computation request,if you want to submit again, please withdraw the request",
								WARNING_TITLE, ERROR_MESSAGE_JOPT);
					} else if (response.equals(SUBMIT_SPECIFIC_PERMIT)) {
						obtoc.writeObject(encryptedEncodingBasePairs);
						obtoc.writeObject(h);
						obtoc.writeObject(validlist);// 邀请者
						Object result = obfromc.readObject();
						if (result.equals(SUBMIT_SPECIFIC_SUCCESS)) {
							JOptionPane.showMessageDialog(null, "Successfully submited!", SUCCESS_TITLE,
									INFO_MESSAGE_JOPT);
						} else {
							JOptionPane.showMessageDialog(null, "Submit failed!", ERROR_TITLE, ERROR_MESSAGE_JOPT);
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

			}
		});
		submitSpec.setBounds(270, 428, 113, 23);
		add(submitSpec);

		validLabel = new JLabel("Existent users");
		validLabel.setBounds(270, 398, 121, 20);
		add(validLabel);

		JButton btnSelectIn = new JButton("Select Inviters");
		btnSelectIn.setBounds(438, 255, 172, 23);
		add(btnSelectIn);
	}

	@Override
	public void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponents(g);
		g.setColor(Color.BLACK);

		int firstline = promptLabel.getY() + promptLabel.getHeight() + 10;
		g.drawLine(0, firstline, getWidth(), firstline);

		// int secondline = firstLabel.getY() - 10;
		// g.drawLine(0, secondline, getWidth(), secondline);

		// int secondvertical = firstLabel.getX() - 10;
		g.drawLine(secondLabel.getX() - 10, firstLabel.getY() - 10, secondLabel.getX() - 10, getHeight());

	}

	public SubmitDialog getSubdia() {
		return subdia;
	}

	public void setSubdia(SubmitDialog subdia) {
		this.subdia = subdia;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.setSize(646, 500);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setContentPane(new SubmitPanel(new Client()));
		jf.setVisible(true);
	}
}
