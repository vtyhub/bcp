package compute;

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

import javax.naming.OperationNotSupportedException;

import client.ComputeClient;
import constant.ThreadConstant;
import constant.ViewConstant;
import constant.interaction.InteractWithSConstant;
import cryptography.PP;
import database.SQLStatement;
import genome.ComputeResult;
import genome.SingleBasePairComputeResult;
import method.CommonMethod;
import network.withs.Communicate;
import server_c.ServerC;

/**
 * 
 *
 */
public class TakeTask implements Runnable, ThreadConstant, InteractWithSConstant, ViewConstant, SQLStatement {

	protected volatile boolean end = false;
	protected volatile boolean pause = false;

	private BlockingQueue<Computation> blockQueue;
	private long blockTime;
	private ServerC c;
	private ObjectOutputStream obtos;
	private ObjectInputStream obfroms;
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

	public TakeTask(ServerC c, BlockingQueue<Computation> blockQueue) {
		// TODO Auto-generated constructor stub
		this(c, blockQueue, DEFAULT_TAKE_BLOCK_TIME);
	}

	public TakeTask(ServerC c, BlockingQueue<Computation> blockQueue, long blockTime) {
		// TODO Auto-generated constructor stub

		this.blockQueue = blockQueue;
		this.blockTime = blockTime;

		this.c = c;
		obtos = this.c.getObToS();
		obfroms = c.getObFromS();
		dbconn = c.getDbConnection();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!end) {
			while (!end && !pause) {
				try {
					Thread.sleep(blockTime);
					Computation computation = blockQueue.take();

					// 本次计算使用变量
					// int number = computation.getNumber();
					boolean specific = computation.isSpecific();
					long number = computation.getNumber();
					BigInteger PK = computation.getPK();
					PP pp = computation.getPp();// rand模式会遇到pp为null的情况，已解决

					BigInteger N = pp.getN();
					BigInteger g = pp.getG();

					// spec专用，自取
					ComputeClient inviter = computation.getInviter();
					HashMap<String, ComputeClient> inviteeMap = computation.getInviteeMap();

					// rand专用，自取
					HashMap<Order, ComputeClient> computeClientMap = computation.getComputeClientMap();
					Order[] orderArray = null;
					String[] usernameArray = null;
					long[] numberArray = null;
					if (computeClientMap != null) {
						// 若不检测，则在spec模式会抛出npe
						orderArray = computeClientMap.keySet().toArray(new Order[0]);// 更新提交时间用
						usernameArray = new String[orderArray.length];
						numberArray = new long[orderArray.length];
						for (int i = 0; i < usernameArray.length; i++) {
							usernameArray[i] = orderArray[i].getUsername();
							numberArray[i] = orderArray[i].getNumber();
						}

					}

					// 公共combine
					String[][] combine = null;// 局部变量必须初始化，即使知道不初始化为null也必须
					if (specific) {
						inviteeMap.remove(inviter.getUsername());
						combine = CommonMethod.combine(inviter.getUsername(),
								inviteeMap.keySet().toArray(new String[0]));
					} else {
						combine = CommonMethod.combine(usernameArray);
					}

					// 取出时间，其实更适合称之为开始时间，开始keyprod之前的时间
					if (specific) {
						PreparedStatement taketime = dbconn.prepareStatement(UPDATE_TAKETIME_BY_NUMBER_SQL);
						taketime.setString(1, CommonMethod.getTimeNow());
						taketime.setLong(2, number);
						taketime.executeUpdate();
					} else {
						PreparedStatement taketime = dbconn.prepareStatement(UPDATE_TAKETIME_BY_NUMBER_SQL);
						String time = CommonMethod.getTimeNow();
						for (int i = 0; i < orderArray.length; i++) {
							taketime.setString(1, time);
							taketime.setLong(2, orderArray[i].getNumber());
							taketime.executeUpdate();
						}
					}

					// 1. key prod ciphertext: on h -> ciphertext on PK
					try {
						obtos.writeObject(KEYPROD);
						Object response = obfroms.readObject();
						if (!response.equals(KEYPROD_PERMIT)) {
							return;
						} else {
							if (computation.isSpecific()) {
								Communicate.keyProdSpecific(obfroms, obtos, dbconn, computation);
							} else {
								Communicate.keyProdRandom(obfroms, obtos, dbconn, computation);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					// 更新keyprod完成的时间，rand使用inviter，spec使用number
					if (specific) {
						PreparedStatement keyprodtime = dbconn.prepareStatement(UPDATE_KEYPRODTIME_BY_NUMBER_SQL);
						keyprodtime.setString(1, CommonMethod.getTimeNow());
						keyprodtime.setLong(2, number);
						keyprodtime.executeUpdate();
					} else {
						PreparedStatement keyprodtime = dbconn.prepareStatement(UPDATE_KEYPRODTIME_BY_NUMBER_SQL);
						String time = CommonMethod.getTimeNow();
						for (int i = 0; i < orderArray.length; i++) {
							keyprodtime.setString(1, time);
							keyprodtime.setLong(2, orderArray[i].getNumber());
							keyprodtime.executeUpdate();
						}
					}

					// 2. add and mult to get result: ciphertext on PK->result on PK
					// 除了存储正常结果外，再增加一个shuffle表存储打乱结果，第3步也改为从打乱结果中来
					// 代价过大，先尝试直接插入打乱
					//
					try {
						obtos.writeObject(COMPUTE);
						Object response = obfroms.readObject();
						if (!response.equals(COMPUTE_PERMIT)) {
							return;
						}
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
						return;
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					}

					if (specific) {
						// spec专用
						try {
							PreparedStatement insertOnPK = dbconn.prepareStatement(INSERT_RESULTONPK);

							// 通过inviter和每个invitee组合得到计算顺序

							ComputeResult[] computeResult = new ComputeResult[combine.length];
							// 开始对每个组合计算出ComputeResult
							for (int i = 0; i < combine.length; i++) {
								// A为inviter,B为invitee之中的一个
								String inviterName = combine[i][0], inviteeName = combine[i][1];
								ComputeClient inviterClient = inviter, inviteeClient = inviteeMap.get(inviteeName);// 根据用户名取出两个计算用户

								// 计算出两个用户结果
								computeResult[i] = ComputeMethod.compute(N, g, computation.getPK(),
										inviterClient.getOriginalEncryptedOnPK(),
										inviteeClient.getOriginalEncryptedOnPK(), inviterName, inviteeName, obfroms,
										obtos);

								// 两个用户的结果也是数组，长度为碱基数量，由于数据库存储单条记录，将之循环存于数据库
								SingleBasePairComputeResult[] result = computeResult[i].getResult();

								// ** 增添的打乱部分 **
								// 打乱两个用户计算结果的顺序，防止用户通过自己碱基对序列破解出另一方的，保证只能计算出相似率
								CommonMethod.shuffle(result);

								for (int j = 0; j < result.length; j++) {
									// 循环设置单行，这个循环跑完了，两个用户的计算结果全部存入数据库
									BigInteger[] addResult = result[j].getAddResult();
									BigInteger[] multResult = result[j].getMultResult();
									insertOnPK.setLong(1, number);// 添加number字段
									insertOnPK.setString(2, inviterName);
									insertOnPK.setString(3, inviteeName);
									insertOnPK.setString(4, addResult[0].toString());
									insertOnPK.setString(5, addResult[1].toString());
									insertOnPK.setString(6, multResult[0].toString());
									insertOnPK.setString(7, multResult[1].toString());
									insertOnPK.setString(8, computation.getPK().toString());// pk

									insertOnPK.executeUpdate();
								}
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						} catch (OperationNotSupportedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						} finally {
							try {
								obtos.writeObject(MULT_END);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

						// 将完成resultonpk的时间写入resultonpktime字段
						// 代表可以进行transdec
						PreparedStatement resultonpktime = dbconn.prepareStatement(UPDATE_RESULTONPKTIME_NUMBER_SQL);
						resultonpktime.setString(1, CommonMethod.getTimeNow());
						resultonpktime.setLong(2, computation.getNumber());
						resultonpktime.executeUpdate();
					} else {
						// rand专用
						// String[] usernamearray = computeClientMap.keySet().toArray(new String[0]);
						// ComputeClient client = computeClientMap.get(usernamearray[0]);// 随便举出一个例子来

						try {
							PreparedStatement insertOnPK = dbconn.prepareStatement(INSERT_RESULTONPK);
							// 通过用户名组合得到一个不重复的计算顺序

							// 每个元素为两两计算结果，长度为需要计算的组数
							ComputeResult[] computeResult = new ComputeResult[combine.length];

							// 开始对每个组合计算出ComputeResult
							for (int i = 0; i < combine.length; i++) {
								String usernameA = combine[i][0], usernameB = combine[i][1];
								Order ordera = CommonMethod.getOrderByUsername(orderArray, usernameA);
								Order orderb = CommonMethod.getOrderByUsername(orderArray, usernameB);

								ComputeClient clientA = computeClientMap.get(ordera),
										clientB = computeClientMap.get(orderb);// 根据用户名取出两个计算用户

								// 计算出两个用户结果
								computeResult[i] = ComputeMethod.compute(N, g, computation.getPK(),
										clientA.getOriginalEncryptedOnPK(), clientB.getOriginalEncryptedOnPK(),
										usernameA, usernameB, obfroms, obtos);
								// 两个用户的结果也是数组，长度为碱基数量，由于数据库存储单条记录，将之循环存于数据库
								SingleBasePairComputeResult[] result = computeResult[i].getResult();
								
								// ** 增添的打乱部分 **
								// 打乱两个用户计算结果的顺序，防止用户通过自己碱基对序列破解出另一方的，保证只能计算出相似率
								CommonMethod.shuffle(result);
								
								for (int j = 0; j < result.length; j++) {
									// 循环设置单行，这个循环跑完了，两个用户的计算结果全部存入数据库
									BigInteger[] addResult = result[j].getAddResult();
									BigInteger[] multResult = result[j].getMultResult();
									insertOnPK.setLong(1, ordera.getNumber());
									insertOnPK.setString(2, usernameA);
									insertOnPK.setString(3, usernameB);
									insertOnPK.setString(4, addResult[0].toString());
									insertOnPK.setString(5, addResult[1].toString());
									insertOnPK.setString(6, multResult[0].toString());
									insertOnPK.setString(7, multResult[1].toString());
									insertOnPK.setString(8, computation.getPK().toString());// pk

									insertOnPK.executeUpdate();
								}

							}

						} catch (SQLException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
							return;
						} catch (OperationNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							return;
						} finally {
							try {
								obtos.writeObject(MULT_END);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

						// 将完成resultonpk的时间写入resultonpktime字段
						// 代表可以进行transdec
						PreparedStatement resultonpktime = dbconn.prepareStatement(UPDATE_RESULTONPKTIME_NUMBER_SQL);
						String time = CommonMethod.getTimeNow();
						for (int i = 0; i < orderArray.length; i++) {
							resultonpktime.setString(1, time);
							resultonpktime.setLong(2, orderArray[i].getNumber());
							resultonpktime.executeUpdate();
						}
					}

					/**
					 * 原版不打乱直接写入resultonpk代码
					 */
					// if (specific) {
					// // spec专用
					// try {
					// PreparedStatement insertOnPK = dbconn.prepareStatement(INSERT_RESULTONPK);
					//
					// // 通过inviter和每个invitee组合得到计算顺序
					//
					// ComputeResult[] computeResult = new ComputeResult[combine.length];
					// // 开始对每个组合计算出ComputeResult
					// for (int i = 0; i < combine.length; i++) {
					// // A为inviter,B为invitee之中的一个
					// String inviterName = combine[i][0], inviteeName = combine[i][1];
					// ComputeClient inviterClient = inviter, inviteeClient =
					// inviteeMap.get(inviteeName);// 根据用户名取出两个计算用户
					//
					// // 计算出两个用户结果
					// computeResult[i] = ComputeMethod.compute(N, g, computation.getPK(),
					// inviterClient.getOriginalEncryptedOnPK(),
					// inviteeClient.getOriginalEncryptedOnPK(), inviterName, inviteeName, obfroms,
					// obtos);
					// // 两个用户的结果也是数组，长度为碱基数量，由于数据库存储单条记录，将之循环存于数据库
					// SingleBasePairComputeResult[] result = computeResult[i].getResult();
					// for (int j = 0; j < result.length; j++) {
					// // 循环设置单行，这个循环跑完了，两个用户的计算结果全部存入数据库
					// BigInteger[] addResult = result[j].getAddResult();
					// BigInteger[] multResult = result[j].getMultResult();
					// insertOnPK.setLong(1, number);// 添加number字段
					// insertOnPK.setString(2, inviterName);
					// insertOnPK.setString(3, inviteeName);
					// insertOnPK.setString(4, addResult[0].toString());
					// insertOnPK.setString(5, addResult[1].toString());
					// insertOnPK.setString(6, multResult[0].toString());
					// insertOnPK.setString(7, multResult[1].toString());
					// insertOnPK.setString(8, computation.getPK().toString());// pk
					//
					// insertOnPK.executeUpdate();
					// }
					// }
					// } catch (SQLException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// return;
					// } catch (OperationNotSupportedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// return;
					// } finally {
					// try {
					// obtos.writeObject(MULT_END);
					// } catch (IOException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }
					// }
					//
					// // 将完成resultonpk的时间写入resultonpktime字段
					// // 代表可以进行transdec
					// PreparedStatement resultonpktime =
					// dbconn.prepareStatement(UPDATE_RESULTONPKTIME_NUMBER_SQL);
					// resultonpktime.setString(1, CommonMethod.getTimeNow());
					// resultonpktime.setLong(2, computation.getNumber());
					// resultonpktime.executeUpdate();
					// } else {
					// // rand专用
					// // String[] usernamearray = computeClientMap.keySet().toArray(new String[0]);
					// // ComputeClient client = computeClientMap.get(usernamearray[0]);// 随便举出一个例子来
					//
					// try {
					// PreparedStatement insertOnPK = dbconn.prepareStatement(INSERT_RESULTONPK);
					// // 通过用户名组合得到一个不重复的计算顺序
					//
					// // 每个元素为两两计算结果，长度为需要计算的组数
					// ComputeResult[] computeResult = new ComputeResult[combine.length];
					//
					// // 开始对每个组合计算出ComputeResult
					// for (int i = 0; i < combine.length; i++) {
					// String usernameA = combine[i][0], usernameB = combine[i][1];
					// Order ordera = CommonMethod.getOrderByUsername(orderArray, usernameA);
					// Order orderb = CommonMethod.getOrderByUsername(orderArray, usernameB);
					//
					// ComputeClient clientA = computeClientMap.get(ordera),
					// clientB = computeClientMap.get(orderb);// 根据用户名取出两个计算用户
					//
					// // 计算出两个用户结果
					// computeResult[i] = ComputeMethod.compute(N, g, computation.getPK(),
					// clientA.getOriginalEncryptedOnPK(), clientB.getOriginalEncryptedOnPK(),
					// usernameA, usernameB, obfroms, obtos);
					// // 两个用户的结果也是数组，长度为碱基数量，由于数据库存储单条记录，将之循环存于数据库
					// SingleBasePairComputeResult[] result = computeResult[i].getResult();
					// for (int j = 0; j < result.length; j++) {
					// // 循环设置单行，这个循环跑完了，两个用户的计算结果全部存入数据库
					// BigInteger[] addResult = result[j].getAddResult();
					// BigInteger[] multResult = result[j].getMultResult();
					// insertOnPK.setLong(1, ordera.getNumber());
					// insertOnPK.setString(2, usernameA);
					// insertOnPK.setString(3, usernameB);
					// insertOnPK.setString(4, addResult[0].toString());
					// insertOnPK.setString(5, addResult[1].toString());
					// insertOnPK.setString(6, multResult[0].toString());
					// insertOnPK.setString(7, multResult[1].toString());
					// insertOnPK.setString(8, computation.getPK().toString());// pk
					//
					// insertOnPK.executeUpdate();
					// }
					//
					// }
					//
					// } catch (SQLException e2) {
					// // TODO Auto-generated catch block
					// e2.printStackTrace();
					// return;
					// } catch (OperationNotSupportedException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// return;
					// } finally {
					// try {
					// obtos.writeObject(MULT_END);
					// } catch (IOException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }
					// }
					//
					// // 将完成resultonpk的时间写入resultonpktime字段
					// // 代表可以进行transdec
					// PreparedStatement resultonpktime =
					// dbconn.prepareStatement(UPDATE_RESULTONPKTIME_NUMBER_SQL);
					// String time = CommonMethod.getTimeNow();
					// for (int i = 0; i < orderArray.length; i++) {
					// resultonpktime.setString(1, time);
					// resultonpktime.setLong(2, orderArray[i].getNumber());
					// resultonpktime.executeUpdate();
					// }
					// }

					// 3. trans dec result on PK -> result on h
					// 只解密本次计算
					if (specific) {
						for (int j = 0; j < combine.length; j++) {

							String[] thisarray = combine[j];
							String usernamea = thisarray[0], usernameb = thisarray[1];

							// 仅需要A用户的公钥，a代表是属于a的结果
							BigInteger hfora = inviter.getH();

							// 两个用户的所有未解密密文,因为两个用户之间计算结果相同并且都是用PK加密所以只存一份记录
							// 这就是查两个用户共同记录，然后给需要计算的过去
							try {
								// spec模式这里应使用accurate，or是给rand使用的
								PreparedStatement singleacisql = dbconn.prepareStatement(SELECT_RESULTONPK_ACCURATE);
								singleacisql.setString(1, usernamea);
								singleacisql.setString(2, usernameb);
								ResultSet ciphertextset = singleacisql.executeQuery();
								ArrayList<SingleBasePairComputeResult> selectDecResult = new ArrayList<SingleBasePairComputeResult>();
								// 结果为需要解密的密文
								while (ciphertextset.next()) {
									String addA = ciphertextset.getString(resultonpk_COLUMN_NAME_addA);
									String addB = ciphertextset.getString(resultonpk_COLUMN_NAME_addB);
									String multA = ciphertextset.getString(resultonpk_COLUMN_NAME_multA);
									String multB = ciphertextset.getString(resultonpk_COLUMN_NAME_multB);

									BigInteger[] add = { new BigInteger(addA), new BigInteger(addB) };
									BigInteger[] mult = { new BigInteger(multA), new BigInteger(multB) };
									SingleBasePairComputeResult record = new SingleBasePairComputeResult(add, mult);
									selectDecResult.add(record);
								}

								SingleBasePairComputeResult[] afterTransDec = Communicate.transDec(pp.getN(), pp.getG(),
										computation.getPK(), hfora,
										selectDecResult.toArray(new SingleBasePairComputeResult[0]), obfroms, obtos);

								PreparedStatement insertOnH = dbconn.prepareStatement(INSERT_RESULTONH);
								for (int l = 0; l < afterTransDec.length; l++) {
									insertOnH.setLong(1, number);
									insertOnH.setString(2, usernamea);
									insertOnH.setString(3, usernameb);
									insertOnH.setString(4, afterTransDec[l].getAddResult()[0].toString());
									insertOnH.setString(5, afterTransDec[l].getAddResult()[1].toString());
									insertOnH.setString(6, afterTransDec[l].getMultResult()[0].toString());
									insertOnH.setString(7, afterTransDec[l].getMultResult()[1].toString());
									insertOnH.executeUpdate();
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (OperationNotSupportedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								try {
									obtos.writeObject(TRANSDEC_END);
									obtos.writeObject(COMPUTE_END);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}

						PreparedStatement resultonhtime = dbconn.prepareStatement(UPDATE_FINISH_BY_NUMBER_SQL);
						resultonhtime.setString(1, CommonMethod.getTimeNow());
						resultonhtime.setLong(2, computation.getNumber());
						resultonhtime.executeUpdate();
					} else {
						try {
							// Order[] allusername = computeClientMap.keySet().toArray(new Order[0]);

							String[][] permutate = CommonMethod.permutate(usernameArray, 2);

							// PreparedStatement pksql = dbconn.prepareStatement(SELECT_CLIENT_PK_SQL);

							for (int j = 0; j < permutate.length; j++) {

								String[] thisarray = permutate[j];
								String usernamea = thisarray[0], usernameb = thisarray[1];

								Order ordera = CommonMethod.getOrderByUsername(orderArray, usernamea);

								// 仅需要A用户的公钥，a代表是属于a的结果
								ComputeClient clientA = computeClientMap.get(ordera);
								BigInteger hfora = clientA.getH();

								// pksql.setString(1, usernamea);
								// ResultSet pkaset = pksql.executeQuery();
								// if (pkaset.next()) {
								// hfora = new BigInteger(pkaset.getString(CLIENT_COLUMN_NAME_H));
								// }

								// 两个用户的所有未解密密文,因为两个用户之间计算结果相同并且都是用PK加密所以只存一份记录
								// 这就是查两个用户共同记录，然后给需要计算的过去
								PreparedStatement singleacisql = dbconn.prepareStatement(SELECT_RESULTONPK_OR);
								singleacisql.setString(1, usernamea);
								singleacisql.setString(2, usernameb);
								singleacisql.setString(3, usernamea);
								singleacisql.setString(4, usernameb);
								ResultSet ciphertextset = singleacisql.executeQuery();
								ArrayList<SingleBasePairComputeResult> selectDecResult = new ArrayList<SingleBasePairComputeResult>();
								// 结果为需要解密的密文
								while (ciphertextset.next()) {
									String addA = ciphertextset.getString(resultonpk_COLUMN_NAME_addA);
									String addB = ciphertextset.getString(resultonpk_COLUMN_NAME_addB);
									String multA = ciphertextset.getString(resultonpk_COLUMN_NAME_multA);
									String multB = ciphertextset.getString(resultonpk_COLUMN_NAME_multB);

									BigInteger[] add = { new BigInteger(addA), new BigInteger(addB) };
									BigInteger[] mult = { new BigInteger(multA), new BigInteger(multB) };
									SingleBasePairComputeResult record = new SingleBasePairComputeResult(add, mult);
									selectDecResult.add(record);
								}

								SingleBasePairComputeResult[] afterTransDec = Communicate.transDec(pp.getN(), pp.getG(),
										PK, hfora, selectDecResult.toArray(new SingleBasePairComputeResult[0]), obfroms,
										obtos);

								PreparedStatement insertOnH = dbconn.prepareStatement(INSERT_RESULTONH);
								for (int l = 0; l < afterTransDec.length; l++) {
									insertOnH.setLong(1, ordera.getNumber());
									insertOnH.setString(2, usernamea);
									insertOnH.setString(3, usernameb);
									insertOnH.setString(4, afterTransDec[l].getAddResult()[0].toString());
									insertOnH.setString(5, afterTransDec[l].getAddResult()[1].toString());
									insertOnH.setString(6, afterTransDec[l].getMultResult()[0].toString());
									insertOnH.setString(7, afterTransDec[l].getMultResult()[1].toString());
									insertOnH.executeUpdate();
								}

							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (OperationNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} finally {
							try {
								obtos.writeObject(TRANSDEC_END);
								obtos.writeObject(COMPUTE_END);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

						// 终于完成，将完成transDec的时间写入finishedtime时间，其实也是resultonh时间
						PreparedStatement resultonhtime = dbconn.prepareStatement(UPDATE_FINISH_BY_NUMBER_SQL);
						String time = CommonMethod.getTimeNow();
						for (int i = 0; i < orderArray.length; i++) {
							resultonhtime.setString(1, time);
							resultonhtime.setLong(2, orderArray[i].getNumber());
							resultonhtime.executeUpdate();
						}
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

}
