package network.withclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import client.OnlineClient;
import constant.NetConstant;
import constant.ThreadConstant;
import constant.interaction.BusinessConstant;
import constant.interaction.LoggedConstant;
import cryptography.BCPForC;
import cryptography.PP;
import database.SQLStatement;
import genome.ComputeResult;
import genome.SingleBasePairComputeResult;
import invitation.Invitation;
import invitation.Invitee;
import invitation.Request;
import method.CommonMethod;
import server_c.ServerC;

public class TakeMessageTask
		implements NetConstant, ThreadConstant, LoggedConstant, SQLStatement, BusinessConstant, Runnable {

	protected volatile boolean end = false;
	protected volatile boolean pause = false;

	private ObjectInputStream obfrom;
	private ObjectOutputStream obto;
	private BlockingQueue<Object> blockQueue;
	private ServerC c;
	private OnlineClient client;
	private String username;
	private Connection dbconn;

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public BlockingQueue<Object> getBlockQueue() {
		return blockQueue;
	}

	public void setBlockQueue(BlockingQueue<Object> blockQueue) {
		this.blockQueue = blockQueue;
	}

	public ObjectInputStream getObfrom() {
		return obfrom;
	}

	public void setObfrom(ObjectInputStream obfrom) {
		this.obfrom = obfrom;
	}

	public ObjectOutputStream getObto() {
		return obto;
	}

	public void setObto(ObjectOutputStream obto) {
		this.obto = obto;
	}

	public TakeMessageTask(ObjectInputStream obfrom, ObjectOutputStream obto, BlockingQueue<Object> queue, ServerC c,
			OnlineClient client) {
		// TODO Auto-generated constructor stub
		this.obfrom = obfrom;
		this.obto = obto;
		this.blockQueue = queue;
		this.c = c;
		this.client = client;
		this.username = client.getUsername();
		this.dbconn = c.getDbConnection();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!end) {
			while (!end && !pause) {
				try {
					Object oper = blockQueue.take();
					if (pause) {
						break;
					}
					if (end) {
						throw new IOException();
					}

					if (oper.equals(BCP_OPER)) {
						// BCP操作
						BCPForC bcp = c.getBcp();
						if (bcp != null) {
							obto.writeObject(BCP_PERMIT);
						} else {
							obto.writeObject(BCP_DNEY);
							continue;
						}

						Object bcpOper = blockQueue.take();
						if (bcpOper.equals(BCP_GETPP)) {
							// 先检测该用户数据库是否有记录，若没有记录检测服务器内存并将之存入数据库，若服务器没有就拒绝
							try {
								Connection dbconn = c.getDbConnection();
								if (dbconn == null) {
									obto.writeObject(BCP_GETPP_DENY);
									return;
								}
								try {
									PreparedStatement selectPP = dbconn
											.prepareStatement(SELECT_PP_CLIENT_PUBLICPARA_SQL);
									selectPP.setString(1, username);
									ResultSet ppresult = selectPP.executeQuery();
									if (ppresult.next()) {
										String N = ppresult.getString(PP_COLUMN_NAME_N);
										String k = ppresult.getString(PP_COLUMN_NAME_K);
										String g = ppresult.getString(PP_COLUMN_NAME_G);
										obto.writeObject(BCP_GETPP_PERMIT);
										obto.writeObject(
												new PP(new BigInteger(N), new BigInteger(k), new BigInteger(g)));//
									} else {
										// 若客户端表内不存在PP，则把服务器内存的PP插入用户表，先检测PP表内是否已经存在服务器的PP，若不存在写入
										PP serverpp = bcp.getPP();
										if (serverpp == null) {
											obto.writeObject(BCP_GETPP_DENY);
											continue;
										}
										obto.writeObject(BCP_GETPP_PERMIT);
										BigInteger N = serverpp.getN();
										PreparedStatement isppexisted = dbconn
												.prepareStatement(SELECT_pp_NisEXISTED_SQL);
										isppexisted.setString(1, N.toString());
										ResultSet isexisted = isppexisted.executeQuery();
										if (!isexisted.next()) {
											// C内存中的N并不在数据库里存在，先写入数据库
											BigInteger k = serverpp.getK();
											BigInteger g = serverpp.getG();
											PreparedStatement insertpp = dbconn.prepareStatement(INSERT_pp_SQL);
											insertpp.setString(1, N.toString());
											insertpp.setString(2, k.toString());
											insertpp.setString(3, g.toString());
											insertpp.executeUpdate();
											// 写入完毕后，将该N写入客户端
										}
										// 此时内存的N已经在数据库里存在，可以直接把该N更新到clint表
										PreparedStatement updateN = dbconn.prepareStatement(UPDATE_client_N_SQL);
										updateN.setString(1, N.toString());
										updateN.setString(2, username);
										updateN.executeUpdate();
										obto.writeObject(serverpp);
									}
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									continue;
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								continue;
							}
						}

					} else if (oper.equals(DATA_OPER)) {
						// 数据提交等操作
						Object dataoper = blockQueue.take();

						if (dataoper.equals(SUBMIT_RANDOM)) {

							// 检测是否提交过，若已提交则无法提交
							Connection dbconn = c.getDbConnection();
							try {
								PreparedStatement ifexist = dbconn.prepareStatement(SELECT_IFSUBMIT_SQL);
								ifexist.setString(1, username);
								ResultSet existresult = ifexist.executeQuery();
								if (existresult.next()) {
									// 若存在，说明提交过
									obto.writeObject(ALREDAY_SUBMIT);
									continue;
								}
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							obto.writeObject(SUBMIT_RANDOM_PERMIT);// 允许

							BigInteger[][] ciphertext = (BigInteger[][]) blockQueue.take();
							BigInteger h = (BigInteger) blockQueue.take();

							// 生成邀请记录
							try {
								PreparedStatement randomsql = dbconn.prepareStatement(INSERT_RANDOM_COMPUTATION_SQL);
								randomsql.setString(1, username);
								randomsql.setInt(2, ciphertext.length);
								randomsql.setString(3, CommonMethod.getTimeNow());
								randomsql.executeUpdate();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								obto.writeObject(SUBMIT_RANDOM_FAILED);
								continue;
							}

							// 查询上一步邀请记录中自动生成的number，用于ciphertext，resultonpk/h中添加
							// 因为random不需要更新invitee记录，所以之前没有获取number的代码，而spec中有
							long number = 0;// 数据库中为20位，这里使用8位有溢出可能
							try {
								PreparedStatement getNumberByInviterSQL = dbconn
										.prepareStatement(SELECT_NUMBER_BY_INVITER_SQL);
								getNumberByInviterSQL.setString(1, username);
								ResultSet numberByInviter = getNumberByInviterSQL.executeQuery();
								if (numberByInviter.next()) {
									number = numberByInviter.getLong(INVITATION_COLUMN_NAME_NUMBER);
								}
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							// 更新邀请人在其他邀请中的记录
							try {
								PreparedStatement updateineelensql = dbconn.prepareStatement(UPDATE_INVITEE_LENGTH_SQL);
								updateineelensql.setInt(1, ciphertext.length);
								updateineelensql.setString(2, username);
								updateineelensql.executeUpdate();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// 更新公钥
							try {
								PreparedStatement updatepk = dbconn.prepareStatement(UPDATE_CLIENT_h_SQL);
								updatepk.setString(1, h.toString());
								updatepk.setString(2, username);
								updatepk.executeUpdate();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								obto.writeObject(SUBMIT_RANDOM_FAILED);
								continue;
							}

							// 插入密文
							try {
								PreparedStatement insertcipher = dbconn
										.prepareStatement(INSERT_ciphertext_cipherab_SQL);
								for (int i = 0; i < ciphertext.length; i++) {
									insertcipher.setLong(1, number);
									insertcipher.setInt(2, i + 1);
									insertcipher.setString(3, username);
									insertcipher.setString(4, ciphertext[i][0].toString());
									insertcipher.setString(5, ciphertext[i][1].toString());
									insertcipher.executeUpdate();
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								obto.writeObject(SUBMIT_RANDOM_FAILED);
								continue;
							}

							obto.writeObject(SUBMIT_RANDOM_SUCCESS);
						} else if (dataoper.equals(CHECK_SPECIFIC)) {
							// 检测有效用户名
							obto.writeObject(CHECK_SPECIFIC_PERMIT);

							@SuppressWarnings("unchecked")
							ArrayList<String> userlist = (ArrayList<String>) blockQueue.take();

							if (userlist.size() == 0) {
								obto.writeObject(new ArrayList<String>());
								continue;
							} else {
								ArrayList<String> validlist = new ArrayList<String>();
								Connection conn = c.getDbConnection();
								try {
									PreparedStatement validuser = conn.prepareStatement(SELECT_VALIDUSER_SQL);
									for (int i = 0; i < userlist.size(); i++) {
										String username = userlist.get(i);
										validuser.setString(1, username);
										ResultSet query = validuser.executeQuery();
										if (query.next()) {
											if (!username.equals(client.getUsername())) {
												validlist.add(userlist.get(i));
											}
										}
									}
									obto.writeObject(validlist);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

						} else if (dataoper.equals(SUBMIT_SPECIFIC)) {
							Connection dbconn = c.getDbConnection();
							try {
								PreparedStatement ifexist = dbconn.prepareStatement(SELECT_IFSUBMIT_SQL);
								ifexist.setString(1, username);
								ResultSet existresult = ifexist.executeQuery();
								if (existresult.next()) {
									// 若存在，说明提交过
									obto.writeObject(ALREDAY_SUBMIT);
									continue;
								}
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								continue;
							}
							obto.writeObject(SUBMIT_SPECIFIC_PERMIT);// 允许

							BigInteger[][] ciphertext = (BigInteger[][]) blockQueue.take();
							BigInteger h = (BigInteger) blockQueue.take();
							@SuppressWarnings("unchecked")
							ArrayList<String> validlist = (ArrayList<String>) blockQueue.take();
							// 这里不考虑破解客户端导致用户名不准确的的可能性，效率起见不进行验证了,仅简单验证一下validlist长度是否超过100，防止过长insert邀请失败

							// 插入邀请记录
							validlist = validlist.size() <= MAXIMUM_USERNUMBER ? validlist
									: (ArrayList<String>) validlist.subList(0, MAXIMUM_USERNUMBER);

							StringBuilder sbinvitees = new StringBuilder(validlist.size() * MAXIMUM_LENGTH_USERNAME);
							sbinvitees.append(' ');// 不光不trim，还要在前面+一个空格，为了使'% username %'查询顺利
							for (int i = 0; i < validlist.size(); i++) {
								sbinvitees.append(validlist.get(i) + ' ');
							}
							try {
								PreparedStatement insertspecsql = dbconn
										.prepareStatement(INSERT_SPECIFIC_COMPUTATION_SQL);
								insertspecsql.setString(1, username);
								insertspecsql.setInt(2, ciphertext.length);
								insertspecsql.setString(3, sbinvitees.toString());
								insertspecsql.setString(4, CommonMethod.getTimeNow());
								insertspecsql.executeUpdate();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								obto.writeObject(SUBMIT_RANDOM_FAILED);
								continue;
							}

							// 查询上一步邀请记录中自动生成的number，用于ciphertext，resultonpk/h中添加
							long number = 0;// 数据库中为20位，这里使用8位有溢出可能
							try {
								PreparedStatement getNumberByInviterSQL = dbconn
										.prepareStatement(SELECT_NUMBER_BY_INVITER_SQL);
								getNumberByInviterSQL.setString(1, username);
								ResultSet numberByInviter = getNumberByInviterSQL.executeQuery();
								if (numberByInviter.next()) {
									number = numberByInviter.getLong(INVITATION_COLUMN_NAME_NUMBER);
								}
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							// 创建specific表中的记录项 for，为每个invitee插入一次
							try {
								// 首先查询刚刚插入邀请记录自动生成的number

								// 创建specific表中的记录项
								PreparedStatement insertspecsql = dbconn.prepareStatement(INSERT_SPECIFIC_SQL);
								for (int i = 0; i < validlist.size(); i++) {
									insertspecsql.setLong(1, number);
									insertspecsql.setString(2, username);
									insertspecsql.setInt(3, ciphertext.length);
									insertspecsql.setString(4, validlist.get(i));
									insertspecsql.executeUpdate();
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								obto.writeObject(SUBMIT_RANDOM_FAILED);
								continue;
							}

							// 更新邀请人在其他邀请中的记录
							try {
								PreparedStatement updateineelensql = dbconn.prepareStatement(UPDATE_INVITEE_LENGTH_SQL);
								updateineelensql.setInt(1, ciphertext.length);
								updateineelensql.setString(2, username);
								updateineelensql.executeUpdate();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// 万一被邀请者已经提交过，把他们的数据更新到创建的记录中
							// 遍历被邀请者，查询
							try {
								PreparedStatement inviteesql = dbconn.prepareStatement(SELECT_INVITEE_LENGTH_SQL);
								PreparedStatement updateineelensql = dbconn
										.prepareStatement(UPDATE_INVITER_INVITEE_LENGTH_SQL);
								for (int i = 0; i < validlist.size(); i++) {
									String ineename = validlist.get(i);
									inviteesql.setString(1, ineename);
									ResultSet ineelenresult = inviteesql.executeQuery();
									if (ineelenresult.next()) {
										int length = ineelenresult.getInt(1);
										updateineelensql.setInt(1, length);
										updateineelensql.setString(2, ineename);
										updateineelensql.setString(3, username);
										updateineelensql.executeUpdate();
									} else {
										// 该用户尚未提交数据

									}
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// 更新公钥
							try {
								PreparedStatement updatepk = dbconn.prepareStatement(UPDATE_CLIENT_h_SQL);
								updatepk.setString(1, h.toString());
								updatepk.setString(2, username);
								updatepk.executeUpdate();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								obto.writeObject(SUBMIT_SPECIFIC_FAILED);
								continue;
							}

							// 插入密文
							try {
								PreparedStatement insertcipher = dbconn
										.prepareStatement(INSERT_ciphertext_cipherab_SQL);
								for (int i = 0; i < ciphertext.length; i++) {
									insertcipher.setLong(1, number);
									insertcipher.setInt(2, i + 1);
									insertcipher.setString(3, username);
									insertcipher.setString(4, ciphertext[i][0].toString());
									insertcipher.setString(5, ciphertext[i][1].toString());
									insertcipher.executeUpdate();
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								obto.writeObject(SUBMIT_RANDOM_FAILED);
								continue;
							}

							// 清理specific表的工作移交到了定期扫描数据库的puttask线程中
							obto.writeObject(SUBMIT_SPECIFIC_SUCCESS);
						}

					} else if (oper.equals(RESULT_OPER)) {
						Object resultoper = blockQueue.take();
						if (resultoper.equals(GETRESULT)) {
							Connection dbconn = c.getDbConnection();
							if (dbconn == null) {
								obto.writeObject(GETRESULT_DENY);
								continue;
							}

							try {
								PreparedStatement ifSubmittedSQL = dbconn.prepareStatement(SELECT_IFSUBMIT_SQL);
								ifSubmittedSQL.setString(1, username);
								ResultSet ifSubmittedSet = ifSubmittedSQL.executeQuery();
								if (!ifSubmittedSet.next()) {
									// 说明未提交
									obto.writeObject(GETRESULT_UNSUBMITTED);
									continue;
								}
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							try {
								PreparedStatement getComputedUserSQL = dbconn
										.prepareStatement(SELECT_RESULTONH_PEERUSER);
								getComputedUserSQL.setString(1, username);
								ResultSet computedUser = getComputedUserSQL.executeQuery();

								HashMap<String, ComputeResult> resultMap = new HashMap<String, ComputeResult>();
								while (computedUser.next()) {
									String usernameb = computedUser.getString(RESULTONH_COLUMN_NAME_USERNAMEB);
									ComputeResult computeResult = new ComputeResult();
									computeResult.setUsernameA(username);
									computeResult.setUsernameB(usernameb);
									resultMap.put(usernameb, computeResult);
								}
								// 结果为0，未完成
								if (resultMap.size() == 0) {
									obto.writeObject(GETRESULT_UNFINISHED);
									continue;
								} else {
									obto.writeObject(GETRESULT_PERMIT);
								}

								PreparedStatement getResultOnHSQL = dbconn.prepareStatement(SELECT_RESULTONH);
								getResultOnHSQL.setString(1, client.getUsername());
								ResultSet resultOnH = getResultOnHSQL.executeQuery();

								while (resultOnH.next()) {
									String usernameb = resultOnH.getString(RESULTONH_COLUMN_NAME_USERNAMEB);
									BigInteger addA = new BigInteger(resultOnH.getString(RESULTONH_COLUMN_NAME_ADDA));
									BigInteger addB = new BigInteger(resultOnH.getString(RESULTONH_COLUMN_NAME_ADDB));
									BigInteger multA = new BigInteger(resultOnH.getString(RESULTONH_COLUMN_NAME_MULTA));
									BigInteger multB = new BigInteger(resultOnH.getString(RESULTONH_COLUMN_NAME_MULTB));

									ComputeResult computeResult = resultMap.get(usernameb);
									ArrayList<SingleBasePairComputeResult> resultList = computeResult.getResultList();
									resultList.add(new SingleBasePairComputeResult(new BigInteger[] { addA, addB },
											new BigInteger[] { multA, multB }));
								}
								ComputeResult[] computeResult = resultMap.values().toArray(new ComputeResult[0]);
								for (ComputeResult twoResult : computeResult) {
									twoResult.setResult(
											twoResult.getResultList().toArray(new SingleBasePairComputeResult[0]));
									// twoResult.setResultList(null);//节省网络带宽，测试完毕后加上
								}

								obto.writeObject(computeResult);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else
							continue;

					} else if (oper.equals(INVITATION_OPER)) {
						Object invitationoper = blockQueue.take();
						if (invitationoper.equals(GET_REQUEST)) {
							obto.writeObject(GET_REQUEST_PERMIT);

							try {
								PreparedStatement requestsql = dbconn.prepareStatement(SELECT_YOUR_REQUEST_SQL);
								PreparedStatement inviteesql = dbconn.prepareStatement(SELECT_INVITEES_BY_NUMBER_SQL);
								requestsql.setString(1, username);
								ResultSet requestSet = requestsql.executeQuery();
								ArrayList<Request> requestList = new ArrayList<Request>();
								while (requestSet.next()) {
									// number
									int number = requestSet.getInt(INVITATION_COLUMN_NAME_NUMBER);

									// invitee
									inviteesql.setInt(1, number);
									ResultSet inviteeSet = inviteesql.executeQuery();
									ArrayList<Invitee> inviteeList = new ArrayList<Invitee>();
									while (inviteeSet.next()) {
										Invitee invitee = new Invitee(
												inviteeSet.getString(SPECIFIC_COLUMN_NAME_INVITEE),
												inviteeSet.getInt(SPECIFIC_COLUMN_NAME_INVITEELENGTH));
										inviteeList.add(invitee);
									}

									// inviter
									String inviter = requestSet.getString(INVITATION_COLUMN_NAME_INVITER);

									// length
									int length = requestSet.getInt(INVITATION_COLUMN_NAME_LENGTH);

									// submission
									String submissiontime = requestSet.getString(INVITATION_COLUMN_NAME_SUBMISSIONTIME);

									// start(take)time
									String starttime = requestSet.getString(INVITATION_COLUMN_NAME_TAKETIME);

									// finishtime
									String finishedtime = requestSet.getString(INVITATION_COLUMN_NAME_FINISHEDTIME);

									Request request;
									if (inviteeList.size() == 0) {
										// random
										request = new Request(number, inviter, length, null, submissiontime, starttime,
												finishedtime);
									} else {
										// spec
										request = new Request(number, inviter, length, inviteeList, submissiontime,
												starttime, finishedtime);
									}
									requestList.add(request);
								}
								obto.writeObject(requestList);

							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else if (invitationoper.equals(GET_INVITATION)) {
							obto.writeObject(GET_INVITATION_PERMIT);
							try {
								PreparedStatement invitationsql = dbconn.prepareStatement(SELECT_INVITATIONS_TOYOU_SQL);
								invitationsql.setString(1, "% " + username + " %");
								ResultSet invitationSet = invitationsql.executeQuery();
								ArrayList<Invitation> invitationList = new ArrayList<Invitation>();
								while (invitationSet.next()) {
									int length = invitationSet.getInt(INVITATION_COLUMN_NAME_LENGTH);
									String inviter = invitationSet.getString(INVITATION_COLUMN_NAME_INVITER);
									String submissiontime = invitationSet
											.getString(INVITATION_COLUMN_NAME_SUBMISSIONTIME);
									String starttime = invitationSet.getString(INVITATION_COLUMN_NAME_TAKETIME);
									String finishtime = invitationSet.getString(INVITATION_COLUMN_NAME_FINISHEDTIME);

									Invitation invitation = new Invitation(inviter, length, submissiontime, starttime,
											finishtime);
									invitationList.add(invitation);
								}
								obto.writeObject(invitationList);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					this.setEnd(true);
					break;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (pause) {
			try {
				Thread.sleep(DEFAULT_SLEEP_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
