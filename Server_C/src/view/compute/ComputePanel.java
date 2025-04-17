package view.compute;

import javax.swing.JPanel;

import client.ComputeClient;
import compute.Computation;
import compute.Order;
import constant.ViewConstant;
import constant.interaction.InteractWithSConstant;
import cryptography.PP;
import database.SQLStatement;
import interaction.KeyProdToS;
import method.CommonMethod;
import server_c.ServerC;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComputePanel extends JPanel implements ViewConstant, InteractWithSConstant, SQLStatement {

	private static final long serialVersionUID = 1L;
	private ServerC C;
	private JButton btnStartaRand;
	// private JButton btnKeyProd;
	private JTable computeClientTable;

	private String[] computeClientColumn = { "Sequence", "Username", "pk", "N", "k", "g", "Change key" };
	private DefaultTableModel computeTableModel;
	// private JButton btnCompute;
	// private JButton btnTransDec;

	public ComputePanel(ServerC c) {
		C = c;
		setLayout(null);
		setBounds(10, 10, 870, 589);

		btnStartaRand = new JButton("Start a random conputation");
		btnStartaRand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Connection dbconn = C.getDbConnection();
				if (dbconn == null) {
					JOptionPane.showMessageDialog(null, "Database should be connected!", WARNING_TITLE,
							WARNING_MESSAGE_JOPT);
					return;
				}

				try {
					LinkedBlockingQueue<Computation> computationQueue = C.getComputationQueue();

					Computation computation = new Computation();// 最终put进队列的，clientmap也只是它的成员变量
					HashMap<Order, ComputeClient> computeClientMap = new HashMap<Order, ComputeClient>();

					PreparedStatement clientcipherab = dbconn.prepareStatement(SELECT_ciphertext_cipherab_SQL);
					PreparedStatement clientpk = dbconn.prepareStatement(SELECT_CLIENT_PK_SQL);
					PreparedStatement clientPP = dbconn.prepareStatement(SELECT_PP_CLIENT_PUBLICPARA_SQL);

					// 将PP不同的用户分别存在不同的线性表中，挑选最长的线性表计算
					ArrayList<ArrayList<ComputeClient>> compareClient = new ArrayList<ArrayList<ComputeClient>>();

					ResultSet validuser = dbconn.createStatement().executeQuery(SELECT_ciphertext_client_validuser_SQL);
					int count = 0;
					// 针对一个用户的
					while (validuser.next()) {
						String username = validuser.getString(CLIENT_COLUMN_NAME_USERNAME);
						ComputeClient singleclient = new ComputeClient();

						clientcipherab.setString(1, username);
						clientpk.setString(1, username);
						clientPP.setString(1, username);

						ResultSet cipherabset = clientcipherab.executeQuery();
						ResultSet pkset = clientpk.executeQuery();
						ResultSet PPset = clientPP.executeQuery();

						// 添加密文到内存
						ArrayList<BigInteger[]> cipherlist = new ArrayList<BigInteger[]>();
						while (cipherabset.next()) {
							String cipherA = cipherabset.getString(ciphertext_COLUMN_NAME_cipher_a);
							String cipherB = cipherabset.getString(ciphertext_COLUMN_NAME_cipher_b);
							BigInteger[] cipher = new BigInteger[] { new BigInteger(cipherA), new BigInteger(cipherB) };
							cipherlist.add(cipher);
						}
						BigInteger[][] originalCiphertext = cipherlist.toArray(new BigInteger[0][0]);
						singleclient.setOriginalCiphertext(originalCiphertext);

						// 设置公钥
						if (pkset.next()) {
							String pk = pkset.getString(CLIENT_COLUMN_NAME_H);
							singleclient.setH(new BigInteger(pk));
						}

						// 设置PP
						if (PPset.next()) {
							String N = PPset.getString(PP_COLUMN_NAME_N);
							String k = PPset.getString(PP_COLUMN_NAME_K);
							String g = PPset.getString(PP_COLUMN_NAME_G);
							singleclient.setPp(new PP(new BigInteger(N), new BigInteger(k), new BigInteger(g)));
						}

						// 设置用户名
						singleclient.setUsername(username);

						CommonMethod.cluster(compareClient, singleclient);// 运行完成后，会初始化一个聚类后的线性表
						count++;
					}

					if (count <= 0) {
						JOptionPane.showMessageDialog(null, "No user submits the necessary data!", WARNING_TITLE,
								WARNING_MESSAGE_JOPT);
						return;
					}

					int maxIndex = CommonMethod.maxIndex(compareClient);// 得出了一个从前往后，相同PP最多的用户列表,若为1则弹出只有一个用户，不计算
					ArrayList<ComputeClient> computeclientlist = compareClient.get(maxIndex);

					int usernumber = computeclientlist.size();
					if (usernumber == 1) {
						JOptionPane.showMessageDialog(null,
								"Only one client can join the computation now,please wait for other clients to submit the data",
								PROMPT_TITLE, INFO_MESSAGE_JOPT);
						return;
					}
					int result = JOptionPane.showConfirmDialog(null,
							"The count of computable clients is " + usernumber
									+ " ,are you sure to start computing and first generate the PK?",
							QUESTION_TITLE, YES_NO_OPT);
					if (result != YES_RESULT) {
						return;
					}

					ComputeClient sample = computeclientlist.get(0);
					PP pp = sample.getPp();
					BigInteger N = pp.getN();
					BigInteger k = pp.getK();
					BigInteger g = pp.getG();

					computeClientMap.clear();
					// for (int i = 0; i < computeClientTable.getRowCount(); i++) {
					// // 删除所有行
					// computeTableModel.removeRow(0);
					// }

					// 更新视图
					for (int i = 0; i < computeclientlist.size(); i++) {
						ComputeClient client = computeclientlist.get(i);
						computeTableModel
								.addRow(new Object[] { i + 1, client.getUsername(), client.getH(), N, k, g, "no" });
					}

					// 提交
					

					BigInteger PK = KeyProdToS.genPK(N, computeclientlist);
					computation.setPK(PK);
					computation.setPp(pp);// 设置pp

					// 完成username，number均有的order
					try {
						PreparedStatement getNumberByInviterSQL = dbconn.prepareStatement(SELECT_NUMBER_BY_INVITER_SQL);
						for (int i = 0; i < computeclientlist.size(); i++) {
							ComputeClient client = computeclientlist.get(i);
							String username = client.getUsername();

							getNumberByInviterSQL.setString(1, username);
							ResultSet numberByInviter = getNumberByInviterSQL.executeQuery();
							long number = 0;
							if (numberByInviter.next()) {
								number = numberByInviter.getLong(INVITATION_COLUMN_NAME_NUMBER);
							} // 若获取不到，数据库写入会出现异常，不存在外键0对应number insert会失败
							Order order = new Order(username, number);
							
							computeClientMap.put(order, client);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					computation.setComputeClientMap(computeClientMap);
					computationQueue.put(computation);// 在这一步之后应该更新puttime

					//新版启用number后的代码
					PreparedStatement puttimeByNum = dbconn.prepareStatement(UPDATE_PUTTIME_BY_NUMBER_SQL);
					String timeNow = CommonMethod.getTimeNow();// 参与同一次rand计算的用户提交时间应设置为相同
					Order[] orderArray = computeClientMap.keySet().toArray(new Order[0]);
					for (int i = 0; i < orderArray.length; i++) {
						puttimeByNum.setString(1, timeNow);
						puttimeByNum.setLong(2, orderArray[i].getNumber());
						puttimeByNum.executeUpdate();
					}
//					PreparedStatement puttime = dbconn.prepareStatement(UPDATE_PUTTIME_BY_INVITER_SQL);
//					for (int i = 0; i < computeclientlist.size(); i++) {
//						puttime.setString(1, timeNow);
//						puttime.setString(2, computeclientlist.get(i).getUsername());
//						puttime.executeUpdate();
//					}

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		btnStartaRand.setBounds(14, 353, 246, 27);
		add(btnStartaRand);

		computeTableModel = new DefaultTableModel(computeClientColumn, 0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}

		};

		computeClientTable = new JTable();
		computeClientTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// if (e.getClickCount() == 2 && computeClientTable.getSelectedRowCount() == 1)
				// {
				// String username = (String)
				// computeClientTable.getValueAt(computeClientTable.getSelectedRow(), 1);
				// HashMap<String, ComputeClient> computeClientMap = C.getComputeClientMap();
				// ComputeClient selectedClient = computeClientMap.get(username);
				// if (selectedClient == null) {
				// JOptionPane.showMessageDialog(null, "Selected client didn't exist!",
				// ERROR_TITLE,
				// ERROR_MESSAGE_JOPT);
				// return;
				// }
				// new ClientCiphertextDialog(C, selectedClient).setVisible(true);
				// }
			}
		});
		computeClientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		computeClientTable.setModel(computeTableModel);

		JScrollPane computeSP = new JScrollPane();
		computeSP.setViewportView(computeClientTable);
		computeSP.setBounds(10, 10, 846, 330);
		add(computeSP);

		// btnCompute = new JButton("Compute Genome Data");
		// btnCompute.setEnabled(false);
		// btnCompute.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// // ObjectInputStream obfroms = C.getObFromS();
		// // ObjectOutputStream obtos = C.getObToS();
		// //
		// // try {
		// // obtos.writeObject(COMPUTE);
		// // Object response = obfroms.readObject();
		// // if (!response.equals(COMPUTE_PERMIT)) {
		// // JOptionPane.showMessageDialog(null, "Server doesn't compute now!",
		// // ERROR_TITLE,
		// // ERROR_MESSAGE_JOPT);
		// // return;
		// // }
		// // } catch (IOException e2) {
		// // // TODO Auto-generated catch block
		// // e2.printStackTrace();
		// // JOptionPane.showMessageDialog(null, "Network error!", ERROR_TITLE,
		// // ERROR_MESSAGE_JOPT);
		// // return;
		// // } catch (ClassNotFoundException e1) {
		// // // TODO Auto-generated catch block
		// // e1.printStackTrace();
		// // JOptionPane.showMessageDialog(null, "Class error!", ERROR_TITLE,
		// // ERROR_MESSAGE_JOPT);
		// // return;
		// // }
		// //
		// // C.getHeartbeatTask().setPause(true);
		// //
		// // HashMap<String, ComputeClient> computeClientMap = C.getComputeClientMap();
		// //
		// // String[] usernamearray = computeClientMap.keySet().toArray(new String[0]);
		// // ComputeClient client = computeClientMap.get(usernamearray[0]);// 随便举出一个例子来
		// // BigInteger PK = C.getPK();
		// // PP pp = client.getPp();
		// // BigInteger N = pp.getN();
		// // BigInteger g = pp.getG();
		// //
		// // Connection conn = C.getDbConnection();
		// // if (conn == null) {
		// // JOptionPane.showMessageDialog(null, "Database should be connected!",
		// // WARNING_TITLE,
		// // WARNING_MESSAGE_JOPT);
		// // return;
		// // }
		// // try {
		// // PreparedStatement insertOnPK = conn.prepareStatement(INSERT_RESULTONPK);
		// // // 通过用户名组合得到一个不重复的计算顺序
		// // String[][] combine = CommonMethod.combine(usernamearray);
		// // // 每个元素为两两计算结果，长度为需要计算的组数
		// // ComputeResult[] computeResult = new ComputeResult[combine.length];
		// //
		// // // 开始对每个组合计算出ComputeResult
		// // for (int i = 0; i < combine.length; i++) {
		// // String usernameA = combine[i][0], usernameB = combine[i][1];
		// // ComputeClient clientA = computeClientMap.get(usernameA),
		// // clientB = computeClientMap.get(usernameB);// 根据用户名取出两个计算用户
		// //
		// // // 计算出两个用户结果
		// // computeResult[i] = ComputeMethod.compute(N, g, PK,
		// // clientA.getOriginalEncryptedOnPK(),
		// // clientB.getOriginalEncryptedOnPK(), usernameA, usernameB, obfroms, obtos);
		// // // 两个用户的结果也是数组，长度为碱基数量，由于数据库存储单条记录，将之循环存于数据库
		// // SingleBasePairComputeResult[] result = computeResult[i].getResult();
		// // for (int j = 0; j < result.length; j++) {
		// // // 循环设置单行，这个循环跑完了，两个用户的计算结果全部存入数据库
		// // BigInteger[] addResult = result[j].getAddResult();
		// // BigInteger[] multResult = result[j].getMultResult();
		// //
		// // insertOnPK.setString(1, usernameA);
		// // insertOnPK.setString(2, usernameB);
		// // insertOnPK.setString(3, addResult[0].toString());
		// // insertOnPK.setString(4, addResult[1].toString());
		// // insertOnPK.setString(5, multResult[0].toString());
		// // insertOnPK.setString(6, multResult[1].toString());
		// // insertOnPK.setString(7, PK.toString());// pk
		// //
		// // insertOnPK.executeUpdate();
		// // }
		// //
		// // }
		// //
		// // C.setComputeResult(computeResult);
		// // } catch (SQLException e2) {
		// // // TODO Auto-generated catch block
		// // e2.printStackTrace();
		// // return;
		// // } catch (OperationNotSupportedException e1) {
		// // // TODO Auto-generated catch block
		// // e1.printStackTrace();
		// // JOptionPane.showMessageDialog(null, "S doesn't support computation now!",
		// // ERROR_TITLE,
		// // ERROR_MESSAGE_JOPT);
		// // return;
		// // } finally {
		// // try {
		// // obtos.writeObject(MULT_END);
		// // } catch (IOException e1) {
		// // // TODO Auto-generated catch block
		// // e1.printStackTrace();
		// // }
		// // C.getHeartbeatTask().setPause(false);
		// // }
		// //
		// // JOptionPane.showMessageDialog(null, "Successfully computed!",
		// SUCCESS_TITLE,
		// // INFO_MESSAGE_JOPT);
		// // btnTransDec.setEnabled(true);
		// }
		// });
		// btnCompute.setBounds(10, 431, 210, 27);
		// add(btnCompute);

		// btnTransDec = new JButton("Trans Dec");
		// btnTransDec.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// // ObjectInputStream obFromS = C.getObFromS();
		// // ObjectOutputStream obToS = C.getObToS();
		// // if (obFromS == null || obToS == null) {
		// // JOptionPane.showMessageDialog(null, "Unconnected to server S!",
		// ERROR_TITLE,
		// // ERROR_MESSAGE_JOPT);
		// // return;
		// // }
		// // Connection dbconn = C.getDbConnection();
		// // if (dbconn == null) {
		// // JOptionPane.showMessageDialog(null, "Database should be connected!",
		// // WARNING_TITLE,
		// // WARNING_MESSAGE_JOPT);
		// // return;
		// // }
		// //
		// // c.getHeartbeatTask().setPause(true);
		// //
		// // try {
		// // // 1，从数据库查出所有可用PK
		// // PreparedStatement allPKSQL =
		// // dbconn.prepareStatement(SELECT_RESULTONPK_ALLPK);
		// // ResultSet pk = allPKSQL.executeQuery();
		// // ArrayList<BigInteger> PKList = new ArrayList<>();
		// // while (pk.next()) {
		// // String PK = pk.getString(resultonpk_COLUMN_NAME_PK);
		// // PKList.add(new BigInteger(PK));
		// // }
		// // if (PKList.size() == 0) {
		// // JOptionPane.showMessageDialog(null, "There is no ciphertext waiting for
		// // decryption!",
		// // WARNING_TITLE, WARNING_MESSAGE_JOPT);
		// // return;
		// // }
		// //
		// // PreparedStatement unDecUserOnGivenPKSQL = dbconn
		// // .prepareStatement(SELECT_RESULTONPK_UNDECUSERONGIVENPK);
		// //
		// // // 2 大循环，将所有PK全部解除，最后删掉对应PK的所有记录
		// // // 检测在该PK下还未完成最终解密的用户，解密完成后记录删除，查出来的都是ab或ba完成的，没有ab ba都完成的
		// //
		// // for (int i = 0; i < PKList.size(); i++) {
		// //
		// // // 本次计算使用的PK
		// // BigInteger PK = PKList.get(i);
		// //
		// // unDecUserOnGivenPKSQL.setString(1, PK.toString());
		// // ResultSet unDecUserOnGivenPK = unDecUserOnGivenPKSQL.executeQuery();
		// //
		// // // 将所有未解密用户选出
		// // ArrayList<String[]> unDecUsername = new ArrayList<String[]>();
		// // while (unDecUserOnGivenPK.next()) {
		// // String usernamea =
		// // unDecUserOnGivenPK.getString(resultonpk_COLUMN_NAME_usernameA);
		// // String usernameb =
		// // unDecUserOnGivenPK.getString(resultonpk_COLUMN_NAME_usernameB);
		// //
		// // unDecUsername.add(new String[] { usernamea, usernameb });
		// // }
		// // // 所有参与计算用户,无重复
		// // // 若所有用户都已经结束计算
		// // if (unDecUsername.size() == 0) {
		// // continue;
		// // }
		// // String[][] userDecArray = unDecUsername.toArray(new String[0][]);
		// // LinkedHashSet<String> extract = CommonMethod.extract(userDecArray);
		// // String[] allusername = extract.toArray(new String[0]);
		// //
		// // String sqlIn = CommonMethod.genJDBCIn(allusername.length);
		// // String userinsql = "select distinct usernamea,usernameb from resultonh
		// where
		// // usernamea in "
		// // + sqlIn + " or usernameb in " + sqlIn;
		// //
		// // PreparedStatement userinstate = dbconn.prepareStatement(userinsql);
		// // for (int j = 0; j < allusername.length; j++) {
		// // userinstate.setString(j + 1, allusername[j]);
		// // userinstate.setString(j + 1 + allusername.length, allusername[j]);
		// // }
		// // ResultSet onhuser = userinstate.executeQuery();
		// // ArrayList<String[]> useronhlist = new ArrayList<String[]>();
		// // while (onhuser.next()) {
		// // String usernamea = onhuser.getString(RESULTONH_COLUMN_NAME_USERNAMEA);
		// // String usernameb = onhuser.getString(RESULTONH_COLUMN_NAME_USERNAMEB);
		// //
		// // useronhlist.add(new String[] { usernamea, usernameb });
		// // }
		// // // 用排列减去onh表中不需要计算的得出需要计算的组数
		// // ArrayList<String[]> needDecList;
		// // String[][] needDecUser = null;
		// // // 解密结果
		// //
		// // if (useronhlist.size() != 0) {
		// // // 已经有部分计算出了结果，需要计算
		// // String[][] useronharray = useronhlist.toArray(new String[0][]);
		// // String[][] permutatation = CommonMethod.permutate(allusername, 2);
		// //
		// // needDecList = CommonMethod.removeSrc(permutatation, useronharray);
		// // needDecUser = needDecList.toArray(new String[0][]);
		// // } else {
		// // // 全都没有进行计算，需求计算的是排列
		// // needDecUser = CommonMethod.permutate(allusername, 2);
		// // }
		// //
		// // PreparedStatement getPPsql =
		// // dbconn.prepareStatement(SELECT_PP_CLIENT_PUBLICPARA_SQL);
		// // PreparedStatement pksql = dbconn.prepareStatement(SELECT_CLIENT_PK_SQL);
		// //
		// // for (int j = 0; j < needDecUser.length; j++) {
		// //
		// // String[] thisarray = needDecUser[j];
		// // String usernamea = thisarray[0], usernameb = thisarray[1];
		// //
		// // // PP
		// // getPPsql.setString(1, usernamea);
		// // ResultSet PPset = getPPsql.executeQuery();
		// // BigInteger N = null, g = null;
		// // if (PPset.next()) {
		// // String Ns = PPset.getString(PP_COLUMN_NAME_N);
		// // String gs = PPset.getString(PP_COLUMN_NAME_G);
		// // if (Ns != null) {
		// // N = new BigInteger(Ns);
		// // }
		// // if (gs != null) {
		// // g = new BigInteger(gs);
		// // }
		// // }
		// // // ----------
		// //
		// // // 仅需要A用户的公钥，a代表是属于a的结果
		// // BigInteger hfora = null;
		// //
		// // pksql.setString(1, usernamea);
		// // ResultSet pkaset = pksql.executeQuery();
		// // if (pkaset.next()) {
		// // hfora = new BigInteger(pkaset.getString(CLIENT_COLUMN_NAME_H));
		// // }
		// //
		// // // 两个用户的所有未解密密文,因为两个用户之间计算结果相同并且都是用PK加密所以只存一份记录
		// // // 这就是查两个用户共同记录，然后给需要计算的过去
		// // PreparedStatement singleacisql =
		// // dbconn.prepareStatement(SELECT_RESULTONPK_OR);
		// // singleacisql.setString(1, usernamea);
		// // singleacisql.setString(2, usernameb);
		// // singleacisql.setString(3, usernamea);
		// // singleacisql.setString(4, usernameb);
		// // ResultSet ciphertextset = singleacisql.executeQuery();
		// // ArrayList<SingleBasePairComputeResult> selectDecResult = new
		// // ArrayList<SingleBasePairComputeResult>();
		// // // 结果为需要解密的密文
		// // while (ciphertextset.next()) {
		// // String addA = ciphertextset.getString(resultonpk_COLUMN_NAME_addA);
		// // String addB = ciphertextset.getString(resultonpk_COLUMN_NAME_addB);
		// // String multA = ciphertextset.getString(resultonpk_COLUMN_NAME_multA);
		// // String multB = ciphertextset.getString(resultonpk_COLUMN_NAME_multB);
		// //
		// // BigInteger[] add = { new BigInteger(addA), new BigInteger(addB) };
		// // BigInteger[] mult = { new BigInteger(multA), new BigInteger(multB) };
		// // SingleBasePairComputeResult record = new SingleBasePairComputeResult(add,
		// // mult);
		// // selectDecResult.add(record);
		// // }
		// //
		// // SingleBasePairComputeResult[] afterTransDec = Communicate.transDec(N, g,
		// PK,
		// // hfora,
		// // selectDecResult.toArray(new SingleBasePairComputeResult[0]), obFromS,
		// obToS);
		// //
		// // PreparedStatement insertOnH = dbconn.prepareStatement(INSERT_RESULTONH);
		// // for (int l = 0; l < afterTransDec.length; l++) {
		// // insertOnH.setString(1, usernamea);
		// // insertOnH.setString(2, usernameb);
		// // insertOnH.setString(3, afterTransDec[l].getAddResult()[0].toString());
		// // insertOnH.setString(4, afterTransDec[l].getAddResult()[1].toString());
		// // insertOnH.setString(5, afterTransDec[l].getMultResult()[0].toString());
		// // insertOnH.setString(6, afterTransDec[l].getMultResult()[1].toString());
		// // insertOnH.executeUpdate();
		// // }
		// //
		// // }
		// // }
		// //
		// // } catch (SQLException e1) {
		// // // TODO Auto-generated catch block
		// // e1.printStackTrace();
		// // return;
		// // } catch (OperationNotSupportedException e1) {
		// // // TODO Auto-generated catch block
		// // e1.printStackTrace();
		// // return;
		// // } finally {
		// // try {
		// // obToS.writeObject(TRANSDEC_END);
		// // obToS.writeObject(COMPUTE_END);
		// // } catch (IOException e1) {
		// // // TODO Auto-generated catch block
		// // e1.printStackTrace();
		// // }
		// // c.getHeartbeatTask().setPause(false);// 执行完毕，恢复心跳
		// // }
		// //
		// // JOptionPane.showMessageDialog(null, "Successfully decrypted!",
		// SUCCESS_TITLE,
		// // INFO_MESSAGE_JOPT);
		// }
		// });
		// btnTransDec.setBounds(10, 511, 113, 27);
		// add(btnTransDec);

	}

	// @Override
	// protected void paintComponent(Graphics g) {
	// // TODO Auto-generated method stub
	// super.paintComponent(g);
	// int firstline = btnPk.getY() + btnPk.getHeight() + 10;
	// g.drawLine(0, firstline, getWidth(), firstline);
	//
	// int secondline = btnKeyProd.getY() + btnKeyProd.getHeight() + 10;
	// g.drawLine(0, secondline, getWidth(), secondline);
	//
	// int thirdline = btnCompute.getY() + btnCompute.getHeight() + 10;
	// g.drawLine(0, thirdline, getWidth(), thirdline);
	//
	// int forthline = btnTransDec.getY() + btnTransDec.getHeight() + 10;
	// g.drawLine(0, forthline, getWidth(), forthline);
	//
	// }

	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.setBounds(10, 10, 500, 500);
		jf.setContentPane(new ComputePanel(new ServerC()));
		jf.setVisible(true);

	}
}
