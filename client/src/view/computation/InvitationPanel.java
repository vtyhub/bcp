package view.computation;

import javax.swing.JPanel;

import client.Client;
import constant.ViewConstant;
import constant.interaction.LoggedConstant;
import invitation.Invitation;
import invitation.Invitee;
import invitation.Request;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InvitationPanel extends JPanel implements LoggedConstant, ViewConstant {

	// private ArrayList<LinkedHashMap<String, Integer>> inviteesList;

	private static final long serialVersionUID = 1L;

	ArrayList<Request> requestList;

	private JLabel lblComputationRequest;
	private Client client;
	private JTable requestTable;

	private static String[] requestTableColumn = { "Number", "Invitees", "Base pairs' length", "Submission time",
			"Start time", "Finished time" };
	private static String[] inviteTableColumn = { "Inviter", "Length", "Submission Time" };
	private JTable invitationTable;

	public InvitationPanel(Client Client) {
		setSize(994, 650);// panel大小超过JTabbedPane大小，无法加载 893 650
		setLayout(null);

		client = Client;

		lblComputationRequest = new JLabel("Your request for computation");
		lblComputationRequest.setBounds(10, 10, 312, 20);
		add(lblComputationRequest);

		JLabel invitationLabel = new JLabel("Invitations to you");
		invitationLabel.setHorizontalAlignment(SwingConstants.LEFT);
		invitationLabel.setBounds(693, 10, 144, 20);
		add(invitationLabel);

		// -----------------------------------------------------------------------------------
		JScrollPane requestSP = new JScrollPane();
		requestSP.setBounds(10, 40, 673, 374);// 比列长度加起来大3正好没有滚轮
		add(requestSP);

		DefaultTableModel requestTableModel = new DefaultTableModel(requestTableColumn, 0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		// requestTableModel.addRow(new Object[0]);// 调试用

		requestTable = new JTable(requestTableModel);
		requestTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int selectedRow = requestTable.getSelectedRow();
					if (requestList != null && requestList.size() > selectedRow
							&& requestList.get(selectedRow) != null) {
						Request request = requestList.get(selectedRow);
						ArrayList<Invitee> invitees = request.getInvitees();
						new InviteeDialog(invitees).setVisible(true);
					}
				}
			}
		});
		requestTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		requestTable.getTableHeader().setReorderingAllowed(false);
		requestTable.getTableHeader().setResizingAllowed(false);

		TableColumnModel requestColumnModel = requestTable.getColumnModel();
		// number
		requestColumnModel.getColumn(0).setMaxWidth(60);// 55
		requestColumnModel.getColumn(0).setMinWidth(60);
		// invitees
		requestColumnModel.getColumn(1).setMaxWidth(125);// 90
		requestColumnModel.getColumn(1).setMinWidth(125);
		// length
		requestColumnModel.getColumn(2).setMaxWidth(110);
		requestColumnModel.getColumn(2).setMinWidth(110);
		// submission 去掉秒及毫秒后时间正合适
		requestColumnModel.getColumn(3).setMaxWidth(125);
		requestColumnModel.getColumn(3).setMinWidth(125);
		// start
		requestColumnModel.getColumn(4).setMaxWidth(125);
		requestColumnModel.getColumn(4).setMinWidth(125);
		// finished
		requestColumnModel.getColumn(5).setMaxWidth(125);
		requestColumnModel.getColumn(5).setMinWidth(125);

		requestSP.setViewportView(requestTable);// total 630 670 增加40
		// ---------------------------------------------------
		DefaultTableModel invitationTableModel = new DefaultTableModel(inviteTableColumn, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		// invitationTableModel.addRow(new Object[0]);

		invitationTable = new JTable(invitationTableModel);
		invitationTable.getTableHeader().setReorderingAllowed(false);
		invitationTable.getTableHeader().setResizingAllowed(false);
		invitationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		TableColumnModel inviteColumnModel = invitationTable.getColumnModel();
		// inviter's name
		inviteColumnModel.getColumn(0).setMaxWidth(95);// 10 D O C 的最小容忍长度
		inviteColumnModel.getColumn(0).setMinWidth(95);
		// length
		inviteColumnModel.getColumn(1).setMaxWidth(60);// 60支持1-1e的长度
		inviteColumnModel.getColumn(1).setMinWidth(60);
		// submissiontime
		inviteColumnModel.getColumn(2).setMaxWidth(125);
		inviteColumnModel.getColumn(2).setMinWidth(125);

		JScrollPane invitationSP = new JScrollPane();// total 231 280 增加49
		invitationSP.setBounds(693, 40, 283, 374);// 234 374 比所有列宽加起来大3
		invitationSP.setViewportView(invitationTable);
		add(invitationSP);

		JButton btnGetRequest = new JButton("Get request");
		btnGetRequest.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				ObjectInputStream obfromc = client.getObfromc();
				ObjectOutputStream obtoc = client.getObtoc();

				try {
					obtoc.writeObject(INVITATION_OPER);
					obtoc.writeObject(GET_REQUEST);
					Object response = obfromc.readObject();
					if (!response.equals(GET_REQUEST_PERMIT)) {
						JOptionPane.showMessageDialog(null, "Server doesn't support get your request operation now!",
								ERROR_TITLE, ERROR_MESSAGE_JOPT);
					} else {
						requestList = (ArrayList<Request>) obfromc.readObject();

						// 清除已有
						while (requestTableModel.getRowCount() > 0) {
							requestTableModel.removeRow(0);
						}

						// 更新结果
						for (int i = 0; i < requestList.size(); i++) {
							Request request = requestList.get(i);
							// "Number", "Invitees", "Base pairs' length","Submission time", "Finished time"
							ArrayList<Invitee> invitees = request.getInvitees();
							String inviteeColumn;
							if (invitees == null) {
								inviteeColumn = "Nobody";
							} else if (invitees.size() == 1) {
								inviteeColumn = invitees.get(0).getInviteeName();
							} else {
								inviteeColumn = "Click twice for more ...";
							}

							// 根据数据库内容版本不同保持兼容，若数据库未存毫秒则保持原长，若数据库存储毫秒则只取.之前的
							String submissiontime = request.getSubmissiontime();
							String starttime = request.getStarttime();
							String finishedtime = request.getFinishedtime();
							Object[] newRow = new Object[] { request.getNumber(), inviteeColumn, request.getLength(),
									submissiontime != null ? submissiontime.substring(0,
											submissiontime.lastIndexOf(".") != -1 ? submissiontime.lastIndexOf(".")
													: submissiontime.length())
											: submissiontime,
									starttime != null ? starttime.substring(0,
											starttime.lastIndexOf(".") != -1 ? starttime.lastIndexOf(".")
													: starttime.length())
											: starttime,
									finishedtime != null ? finishedtime.substring(0,
											finishedtime.lastIndexOf(".") != -1 ? finishedtime.lastIndexOf(".")
													: finishedtime.length())
											: finishedtime };
							requestTableModel.addRow(newRow);
						}
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
		btnGetRequest.setBounds(10, 424, 144, 27);
		add(btnGetRequest);

		JButton btnGetInvitations = new JButton("Get invitations");
		btnGetInvitations.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ObjectInputStream obfromc = client.getObfromc();
				ObjectOutputStream obtoc = client.getObtoc();

				try {
					obtoc.writeObject(INVITATION_OPER);
					obtoc.writeObject(GET_INVITATION);

					Object response = obfromc.readObject();
					if (!response.equals(GET_INVITATION_PERMIT)) {
						JOptionPane.showMessageDialog(null, "Server doesn't support get your request operation now!",
								ERROR_TITLE, ERROR_MESSAGE_JOPT);
					} else {
						@SuppressWarnings("unchecked")
						ArrayList<Invitation> invitationList = (ArrayList<Invitation>) obfromc.readObject();
						while (invitationTableModel.getRowCount() > 0) {
							invitationTableModel.removeRow(0);
						}

						for (int i = 0; i < invitationList.size(); i++) {
							Invitation invitation = invitationList.get(i);
							String inviter = invitation.getInviter();
							int length = invitation.getLength();
							String submissiontime = invitation.getSubmissiontime();
							Object[] newRow = new Object[] { inviter, length,
									submissiontime.substring(0,
											submissiontime.lastIndexOf(".") != -1 ? submissiontime.lastIndexOf(".")
													: submissiontime.length()) };
							invitationTableModel.addRow(newRow);
						}
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
		btnGetInvitations.setBounds(693, 424, 153, 27);
		add(btnGetInvitations);
		// ------------------------------------------------
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.setBounds(300, 100, 1000, 600);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		InvitationPanel ip = new InvitationPanel(new Client());
		jf.setContentPane(ip);
		jf.setVisible(true);

	}
}
