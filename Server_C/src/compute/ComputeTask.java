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

import javax.naming.OperationNotSupportedException;

import client.ComputeClient;
import constant.ViewConstant;
import constant.interaction.InteractWithSConstant;
import cryptography.PP;
import database.SQLStatement;
import genome.ComputeResult;
import genome.SingleBasePairComputeResult;
import method.CommonMethod;
import network.withs.Communicate;
import server_c.ServerC;
@Deprecated
public class ComputeTask implements Runnable, InteractWithSConstant, ViewConstant, SQLStatement {

	private ServerC c;
	private ObjectOutputStream obtos;
	private ObjectInputStream obfroms;
	private Connection dbconn;
	private final boolean specific;

	private Computation computation;
	private BigInteger PK;
	private String[][] combine;
	private PP pp;

	public ComputeTask(ServerC C, Computation computation, boolean computationType) {
		// TODO Auto-generated constructor stub
		this.c = C;
		dbconn = c.getDbConnection();
		obtos = c.getObToS();
		obfroms = c.getObFromS();
		this.specific = computationType;

		this.computation = computation;
		PK = computation.getPK();
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void run() {
		c.getHeartbeatTask().setPause(true);

		// key prod ciphertext: on h -> ciphertext on PK
		// 这一步rand和spec应该没有区别
		try {
			obtos.writeObject(KEYPROD);
			Object response = obfroms.readObject();
			if (!response.equals(KEYPROD_PERMIT)) {
				c.getHeartbeatTask().setPause(false);
				return;
			} else {
				if (specific) {
					Communicate.keyProdSpecific(obfroms, obtos, dbconn, computation);
				} else {
					Communicate.keyProdRandom(obfroms, obtos, dbconn, computation);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.getHeartbeatTask().setPause(false);
			return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.getHeartbeatTask().setPause(false);
			return;
		}

		// compute result: ciphertext on PK->result on PK
		try {
			obtos.writeObject(COMPUTE);
			Object response = obfroms.readObject();
			if (!response.equals(COMPUTE_PERMIT)) {
				c.getHeartbeatTask().setPause(false);
				return;
			}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			c.getHeartbeatTask().setPause(false);
			return;
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			c.getHeartbeatTask().setPause(false);
			return;
		}

		if (specific) {
			ComputeClient inviter = computation.getInviter();
			HashMap<String, ComputeClient> inviteeMap = computation.getInviteeMap();

			pp = inviter.getPp();
			BigInteger N = pp.getN();
			BigInteger g = pp.getG();

			try {
				PreparedStatement insertOnPK = dbconn.prepareStatement(INSERT_RESULTONPK);

				// 通过inviter和每个invitee组合得到计算顺序
				combine = CommonMethod.combine(inviter.getUsername(), inviteeMap.keySet().toArray(new String[0]));

				ComputeResult[] computeResult = new ComputeResult[combine.length];
				// 开始对每个组合计算出ComputeResult
				for (int i = 0; i < combine.length; i++) {
					// A为inviter,B为invitee之中的一个
					String inviterName = combine[i][0], inviteeName = combine[i][1];
					ComputeClient inviterClient = inviter, inviteeClient = inviteeMap.get(inviteeName);// 根据用户名取出两个计算用户

					// 计算出两个用户结果
					computeResult[i] = ComputeMethod.compute(N, g, PK, inviterClient.getOriginalEncryptedOnPK(),
							inviteeClient.getOriginalEncryptedOnPK(), inviterName, inviteeName, obfroms, obtos);
					// 两个用户的结果也是数组，长度为碱基数量，由于数据库存储单条记录，将之循环存于数据库
					SingleBasePairComputeResult[] result = computeResult[i].getResult();
					for (int j = 0; j < result.length; j++) {
						// 循环设置单行，这个循环跑完了，两个用户的计算结果全部存入数据库
						BigInteger[] addResult = result[j].getAddResult();
						BigInteger[] multResult = result[j].getMultResult();
//						insertOnPK.setLong(1, number);
						insertOnPK.setString(1, inviterName);
						insertOnPK.setString(2, inviteeName);
						insertOnPK.setString(3, addResult[0].toString());
						insertOnPK.setString(4, addResult[1].toString());
						insertOnPK.setString(5, multResult[0].toString());
						insertOnPK.setString(6, multResult[1].toString());
						insertOnPK.setString(7, PK.toString());// pk

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

		} else {
			HashMap<Order, ComputeClient> computeClientMap = computation.getComputeClientMap();

			String[] usernamearray = computeClientMap.keySet().toArray(new String[0]);
			ComputeClient client = computeClientMap.get(usernamearray[0]);// 随便举出一个例子来
			pp = client.getPp();
			BigInteger N = pp.getN();
			BigInteger g = pp.getG();

			try {
				PreparedStatement insertOnPK = dbconn.prepareStatement(INSERT_RESULTONPK);
				// 通过用户名组合得到一个不重复的计算顺序
				combine = CommonMethod.combine(usernamearray);
				// 每个元素为两两计算结果，长度为需要计算的组数
				ComputeResult[] computeResult = new ComputeResult[combine.length];

				// 开始对每个组合计算出ComputeResult
				for (int i = 0; i < combine.length; i++) {
					String usernameA = combine[i][0], usernameB = combine[i][1];
					ComputeClient clientA = computeClientMap.get(usernameA), clientB = computeClientMap.get(usernameB);// 根据用户名取出两个计算用户

					// 计算出两个用户结果
					computeResult[i] = ComputeMethod.compute(N, g, PK, clientA.getOriginalEncryptedOnPK(),
							clientB.getOriginalEncryptedOnPK(), usernameA, usernameB, obfroms, obtos);
					// 两个用户的结果也是数组，长度为碱基数量，由于数据库存储单条记录，将之循环存于数据库
					SingleBasePairComputeResult[] result = computeResult[i].getResult();
					for (int j = 0; j < result.length; j++) {
						// 循环设置单行，这个循环跑完了，两个用户的计算结果全部存入数据库
						BigInteger[] addResult = result[j].getAddResult();
						BigInteger[] multResult = result[j].getMultResult();

						insertOnPK.setString(1, usernameA);
						insertOnPK.setString(2, usernameB);
						insertOnPK.setString(3, addResult[0].toString());
						insertOnPK.setString(4, addResult[1].toString());
						insertOnPK.setString(5, multResult[0].toString());
						insertOnPK.setString(6, multResult[1].toString());
						insertOnPK.setString(7, PK.toString());// pk

						insertOnPK.executeUpdate();
					}

				}

			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				c.getHeartbeatTask().setPause(false);
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
		}
		// 代表可以进行transdec
		try {
			PreparedStatement resultonpktime = dbconn.prepareStatement(UPDATE_RESULTONPKTIME_NUMBER_SQL);
			resultonpktime.setString(1, CommonMethod.getTimeNow());
			resultonpktime.setLong(2, computation.getNumber());
			resultonpktime.executeUpdate();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// trans dec result on PK -> result on h
		// 只解密本次计算
		if (specific) {

			ComputeClient inviter = computation.getInviter();
			// HashMap<String, ComputeClient> inviteeMap = computation.getInviteeMap();

			for (int j = 0; j < combine.length; j++) {

				String[] thisarray = combine[j];
				String usernamea = thisarray[0], usernameb = thisarray[1];

				// 仅需要A用户的公钥，a代表是属于a的结果
				BigInteger hfora = inviter.getH();

				// 两个用户的所有未解密密文,因为两个用户之间计算结果相同并且都是用PK加密所以只存一份记录
				// 这就是查两个用户共同记录，然后给需要计算的过去
				try {
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

					SingleBasePairComputeResult[] afterTransDec = Communicate.transDec(pp.getN(), pp.getG(), PK, hfora,
							selectDecResult.toArray(new SingleBasePairComputeResult[0]), obfroms, obtos);

					PreparedStatement insertOnH = dbconn.prepareStatement(INSERT_RESULTONH);
					for (int l = 0; l < afterTransDec.length; l++) {
						insertOnH.setString(1, usernamea);
						insertOnH.setString(2, usernameb);
						insertOnH.setString(3, afterTransDec[l].getAddResult()[0].toString());
						insertOnH.setString(4, afterTransDec[l].getAddResult()[1].toString());
						insertOnH.setString(5, afterTransDec[l].getMultResult()[0].toString());
						insertOnH.setString(6, afterTransDec[l].getMultResult()[1].toString());
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
					c.getHeartbeatTask().setPause(false);// 执行完毕，恢复心跳
				}

			}
		} else {
			try {
				HashMap<Order, ComputeClient> computeClientMap = computation.getComputeClientMap();
				Order[] allOrder = computeClientMap.keySet().toArray(new Order[0]);
				String[] allUsername = new String[allOrder.length];
				for (int i = 0; i < allOrder.length; i++) {
					allUsername[i] = allOrder[i].getUsername();
				}

				String[][] permutate = CommonMethod.permutate(allUsername, 2);

				// PreparedStatement pksql = dbconn.prepareStatement(SELECT_CLIENT_PK_SQL);

				for (int j = 0; j < permutate.length; j++) {

					String[] thisarray = permutate[j];
					String usernamea = thisarray[0], usernameb = thisarray[1];
					Order ordera = CommonMethod.getOrderByUsername(allOrder, usernamea);

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

					SingleBasePairComputeResult[] afterTransDec = Communicate.transDec(pp.getN(), pp.getG(), PK, hfora,
							selectDecResult.toArray(new SingleBasePairComputeResult[0]), obfroms, obtos);

					PreparedStatement insertOnH = dbconn.prepareStatement(INSERT_RESULTONH);
					for (int l = 0; l < afterTransDec.length; l++) {
						insertOnH.setLong(1, ordera.getNumber());//根据usernamea获取到的number，写入数据库
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
				c.getHeartbeatTask().setPause(false);// 执行完毕，恢复心跳
			}
		}

	}

}
