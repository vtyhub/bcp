package compute;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import client.ComputeClient;
import cryptography.PP;
import database.SQLStatement;
import genome.ComputeResult;
import genome.SingleBasePairComputeResult;
import interaction.CInteract;
import interaction.KeyProdToS;
import network.withs.Communicate;

public class ComputeMethod implements SQLStatement {

	/**
	 * 两个碱基计算
	 * 
	 * @param N
	 * @param g
	 * @param PK
	 * @param a
	 * @param b
	 * @param obfroms
	 * @param obtos
	 * @return
	 * @throws OperationNotSupportedException
	 */
	public static SingleBasePairComputeResult compute(BigInteger N, BigInteger g, BigInteger PK, BigInteger[] a,
			BigInteger[] b, ObjectInputStream obfroms, ObjectOutputStream obtos) throws OperationNotSupportedException {

		SingleBasePairComputeResult computeResult = new SingleBasePairComputeResult();

		// 1加法
		BigInteger[] addResult = CInteract.add(N, a, b);
		computeResult.setAddResult(addResult);

		// 2乘法
		BigInteger[] multResult = Communicate.mult(N, g, PK, a, b, obfroms, obtos);
		computeResult.setMultResult(multResult);

		return computeResult;
	}

	/**
	 * 两个用户之间全部碱基计算
	 * 
	 * @param N
	 * @param g
	 * @param PK
	 * @param A
	 * @param B
	 * @param usernameA
	 * @param usernameB
	 * @param obfroms
	 * @param obtos
	 * @return
	 * @throws OperationNotSupportedException
	 */
	public static ComputeResult compute(BigInteger N, BigInteger g, BigInteger PK, BigInteger[][] A, BigInteger[][] B,
			String usernameA, String usernameB, ObjectInputStream obfroms, ObjectOutputStream obtos)
			throws OperationNotSupportedException {

		ComputeResult computeResult = new ComputeResult();
		computeResult.setUsernameA(usernameA);
		computeResult.setUsernameB(usernameB);

		SingleBasePairComputeResult[] result = new SingleBasePairComputeResult[A.length];// 假定两者长度相同

		for (int i = 0; i < result.length; i++) {
			result[i] = compute(N, g, PK, A[i], B[i], obfroms, obtos);
		}

		computeResult.setResult(result);
		return computeResult;
	}

	// specific模式专用生成方法
	// 虽然调用底下的可以减少代码复杂度，但是会导致预编译sql语句带来的效率提升消失，决定用空间换时间增大代码量
	public static HashMap<String, ComputeClient> getSpecInviteesMap(Connection dbconn, List<String> inviteeList)
			throws SQLException {

		HashMap<String, ComputeClient> inviteesMap = new HashMap<String, ComputeClient>();

		PreparedStatement clientcipherab = dbconn.prepareStatement(SELECT_ciphertext_cipherab_SQL);
		PreparedStatement clientpk = dbconn.prepareStatement(SELECT_CLIENT_PK_SQL);
		PreparedStatement clientPP = dbconn.prepareStatement(SELECT_PP_CLIENT_PUBLICPARA_SQL);

		for (int i = 0; i < inviteeList.size(); i++) {
			// 针对一个用户的
			String username = inviteeList.get(i);
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

			inviteesMap.put(username, singleclient);
		}
		return inviteesMap;
	}

	// 一般用于在specific模式下获取inviter的信息
	public static ComputeClient getComputeClient(Connection dbconn, String username) throws SQLException {
		PreparedStatement clientcipherab = dbconn.prepareStatement(SELECT_ciphertext_cipherab_SQL);
		PreparedStatement clientpk = dbconn.prepareStatement(SELECT_CLIENT_PK_SQL);
		PreparedStatement clientPP = dbconn.prepareStatement(SELECT_PP_CLIENT_PUBLICPARA_SQL);

		// 针对一个用户的
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
		return singleclient;
	}

	// generate PK
	public static BigInteger generatePK(Connection dbconn, ComputeClient[] computeArray) throws SQLException {

		if (computeArray.length == 0) {
			return null;
		}

		ComputeClient sample = computeArray[0];
		PP pp = sample.getPp();
		BigInteger N = pp.getN();

		return KeyProdToS.genPK(N, computeArray);
	}

	public static Computation getSpecComputation(Connection dbconn, String inviter, List<String> inviteeList)
			throws SQLException {
		// 两次API调用
		HashMap<String, ComputeClient> computeMap = getSpecInviteesMap(dbconn, inviteeList);
		ComputeClient inviterClient = getComputeClient(dbconn, inviter);

		// 计算PK并将之更新入数据库
		computeMap.put(inviter, inviterClient);
		ComputeClient[] inviterAndees = computeMap.values().toArray(new ComputeClient[0]);
		BigInteger PK = generatePK(dbconn, inviterAndees);// 未参与进入PK计算导致错误解密？
		PreparedStatement updatePK = dbconn.prepareStatement(UPDATE_CLIENT_PK_SQL);
		for (int i = 0; i < inviterAndees.length; i++) {
			updatePK.setString(1, PK.toString());
			updatePK.setString(2, inviterAndees[i].getUsername());
			updatePK.executeUpdate();
		}
		computeMap.remove(inviter);

		return new Computation(inviterClient, computeMap, PK, inviterClient.getPp());
	}

}
