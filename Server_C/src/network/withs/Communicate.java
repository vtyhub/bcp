package network.withs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.OperationNotSupportedException;

import client.ClientToS;
import client.ComputeClient;
import compute.Computation;
import compute.Order;
import constant.NetConstant;
import constant.interaction.InteractWithSConstant;
import cryptography.BCPForC;
import cryptography.PP;
import database.SQLStatement;
import genome.SingleBasePairComputeResult;
import interaction.CInteract;
import interaction.KeyProdToS;
import interaction.MultFromS;

public class Communicate implements NetConstant, InteractWithSConstant, SQLStatement {

	public static void keyProdSpecific(ObjectInputStream obfroms, ObjectOutputStream obtos, Connection dbconn,
			Computation computation) {
		try {
			// 视为已经允许
			HashMap<String, ComputeClient> inviteeMap = computation.getInviteeMap();
			ComputeClient inviter = computation.getInviter();
			String invitername = inviter.getUsername();
			inviteeMap.put(invitername, inviter);

			BigInteger PK = computation.getPK();

			ComputeClient[] computeClientArray = inviteeMap.values().toArray(new ComputeClient[0]);

			PP pp = computeClientArray[0].getPp();
			BigInteger N = pp.getN();

			// 1
			ClientToS[] clientsToS = new ClientToS[computeClientArray.length];
			for (int i = 0; i < computeClientArray.length; i++) {
				// 循环内使用的参数
				ComputeClient computeClient = computeClientArray[i];
				BigInteger h = computeClient.getH();

				// 发送给S的客户端数组实例
				clientsToS[i] = new ClientToS();

				// 写入公钥，不写入用户名
				clientsToS[i].setH(h);

				// 得到从内存中取出的原始密文，为二维数组
				BigInteger[][] originalCiphertext = computeClient.getOriginalCiphertext();

				BigInteger[][] blindOriginalCiphertext = new BigInteger[originalCiphertext.length][];// 加盲密文
				BigInteger[] blindnessArray = new BigInteger[originalCiphertext.length];// 盲的数组

				for (int j = 0; j < originalCiphertext.length; j++) {
					BigInteger[] cipherAndBlindness = KeyProdToS.blind(pp, h, originalCiphertext[j]);

					blindOriginalCiphertext[j] = new BigInteger[] { cipherAndBlindness[0], cipherAndBlindness[1] };
					blindnessArray[j] = cipherAndBlindness[2];// 盲
				}
				clientsToS[i].setBlindedCiphertext(blindOriginalCiphertext);

				// 设置盲和加盲后的原始密文进入内存
				computeClient.setBlindness(blindnessArray);
			}
			obtos.writeObject(clientsToS);

			// 2
			PreparedStatement updateCipherOnPKSQL = dbconn.prepareStatement(UPDATE_CIPHERTEXT_PK);

			ClientToS[] clientsFromS = (ClientToS[]) obfroms.readObject();
			for (int i = 0; i < clientsFromS.length; i++) {
				// 用户名
				String username = computeClientArray[i].getUsername();

				// 从S发送过来里面取出转化为PK加密的加盲密文
				BigInteger[][] blindedEncryptedOnPK = clientsFromS[i].getBlindedCiphertext();

				// 用以存储去盲后以PK加密的密文的数组
				BigInteger[][] encryptedOnPK = new BigInteger[blindedEncryptedOnPK.length][];

				// 取出原始盲，加盲密文只有在clientsToS中才能取出
				BigInteger[] blindness = computeClientArray[i].getBlindness();

				for (int j = 0; j < blindedEncryptedOnPK.length; j++) {
					BigInteger[] blindInverseEncOnPK = BCPForC.enc(pp, PK, CInteract.additiveInverse(blindness[j], N));
					BigInteger[] encOnPK = CInteract.add(N, blindedEncryptedOnPK[j], blindInverseEncOnPK);
					encryptedOnPK[j] = encOnPK;

					updateCipherOnPKSQL.setString(1, encOnPK[0].toString());
					updateCipherOnPKSQL.setString(2, encOnPK[1].toString());
					updateCipherOnPKSQL.setString(3, username);
					updateCipherOnPKSQL.setInt(4, j + 1);

					updateCipherOnPKSQL.executeUpdate();
				}

				computeClientArray[i].setOriginalEncryptedOnPK(encryptedOnPK);
			}
			
			inviteeMap.remove(inviter.getUsername());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
		}
	}

	public static void keyProdRandom(ObjectInputStream obfroms, ObjectOutputStream obtos, Connection dbconn,
			Computation computation) {
		try {
			// 视为已经允许
			HashMap<Order, ComputeClient> computeClientMap = computation.getComputeClientMap();
			BigInteger PK = computation.getPK();

			ComputeClient[] computeClientArray = computeClientMap.values().toArray(new ComputeClient[0]);

			PP pp = computeClientArray[0].getPp();
			BigInteger N = pp.getN();

			// 1
			ClientToS[] clientsToS = new ClientToS[computeClientArray.length];
			for (int i = 0; i < computeClientArray.length; i++) {
				// 循环内使用的参数
				ComputeClient computeClient = computeClientArray[i];
				BigInteger h = computeClient.getH();

				// 发送给S的客户端数组实例
				clientsToS[i] = new ClientToS();

				// 写入公钥，不写入用户名
				clientsToS[i].setH(h);

				// 得到从内存中取出的原始密文，为二维数组
				BigInteger[][] originalCiphertext = computeClient.getOriginalCiphertext();

				BigInteger[][] blindOriginalCiphertext = new BigInteger[originalCiphertext.length][];// 加盲密文
				BigInteger[] blindnessArray = new BigInteger[originalCiphertext.length];// 盲的数组

				for (int j = 0; j < originalCiphertext.length; j++) {
					BigInteger[] cipherAndBlindness = KeyProdToS.blind(pp, h, originalCiphertext[j]);

					blindOriginalCiphertext[j] = new BigInteger[] { cipherAndBlindness[0], cipherAndBlindness[1] };
					blindnessArray[j] = cipherAndBlindness[2];// 盲
				}
				clientsToS[i].setBlindedCiphertext(blindOriginalCiphertext);

				// 设置盲和加盲后的原始密文进入内存
				computeClient.setBlindness(blindnessArray);
			}
			obtos.writeObject(clientsToS);

			// 2 ciphertext on h -> ciphertext on PK
			PreparedStatement updateCipherOnPKSQL = dbconn.prepareStatement(UPDATE_CIPHERTEXT_PK);

			ClientToS[] clientsFromS = (ClientToS[]) obfroms.readObject();
			for (int i = 0; i < clientsFromS.length; i++) {
				// 用户名
				String username = computeClientArray[i].getUsername();

				// 从S发送过来里面取出转化为PK加密的加盲密文
				BigInteger[][] blindedEncryptedOnPK = clientsFromS[i].getBlindedCiphertext();

				// 用以存储去盲后以PK加密的密文的数组
				BigInteger[][] encryptedOnPK = new BigInteger[blindedEncryptedOnPK.length][];

				// 取出原始盲，加盲密文只有在clientsToS中才能取出
				BigInteger[] blindness = computeClientArray[i].getBlindness();

				for (int j = 0; j < blindedEncryptedOnPK.length; j++) {
					BigInteger[] blindInverseEncOnPK = BCPForC.enc(pp, PK, CInteract.additiveInverse(blindness[j], N));
					BigInteger[] encOnPK = CInteract.add(N, blindedEncryptedOnPK[j], blindInverseEncOnPK);
					encryptedOnPK[j] = encOnPK;

					updateCipherOnPKSQL.setString(1, encOnPK[0].toString());
					updateCipherOnPKSQL.setString(2, encOnPK[1].toString());
					updateCipherOnPKSQL.setString(3, username);
					updateCipherOnPKSQL.setInt(4, j + 1);

					updateCipherOnPKSQL.executeUpdate();
				}

				computeClientArray[i].setOriginalEncryptedOnPK(encryptedOnPK);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static BigInteger[] mult(BigInteger N, BigInteger g, BigInteger PK, BigInteger[] A1B1, BigInteger[] A2B2,
			ObjectInputStream obfroms, ObjectOutputStream obtos) throws OperationNotSupportedException {

		try {
			// 因为有多步，所以请求在方法内执行
			obtos.writeObject(MULT);
			Object response = obfroms.readObject();
			if (!response.equals(MULT_PERMIT)) {
				throw new OperationNotSupportedException("mult denied");
			}

			// 应使用盲的加法逆元，但这里使用了盲，所以盲视为盲的加法逆元，盲的加法逆元视为盲
			// 加盲密文和盲
			BigInteger[] cipherAndBlindnessA = KeyProdToS.blind(N, g, PK, A1B1);
			BigInteger[] cipherAndBlindnessB = KeyProdToS.blind(N, g, PK, A2B2);

			// 加盲密文
			BigInteger[] blindCipherA = new BigInteger[] { cipherAndBlindnessA[0], cipherAndBlindnessA[1] };
			BigInteger[] blindCipherB = new BigInteger[] { cipherAndBlindnessB[0], cipherAndBlindnessB[1] };

			// 盲
			BigInteger blindnessA = cipherAndBlindnessA[3];
			BigInteger blindnessB = cipherAndBlindnessB[3];

			obtos.writeObject(new BigInteger[][] { blindCipherA, blindCipherB });

			BigInteger[] multResult = (BigInteger[]) obfroms.readObject();

			BigInteger[] multi = MultFromS.multi(N, g, PK, multResult[0], blindnessA, A1B1[0], A1B1[1], multResult[1],
					blindnessB, A2B2[0], A2B2[1]);

			return multi;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	// 将一段用PK加密的密文解密成用指定h加密的
	public static SingleBasePairComputeResult[] transDec(BigInteger N, BigInteger g, BigInteger PK, BigInteger h,
			SingleBasePairComputeResult[] twoClientsResult, ObjectInputStream obfroms, ObjectOutputStream obtos)
			throws OperationNotSupportedException {
		try {
			obtos.writeObject(TRANSDEC);
			Object response = obfroms.readObject();
			if (!response.equals(TRANSDEC_PERMIT)) {
				throw new OperationNotSupportedException("mult denied");
			}
			// 提取结果到两个二维数组中
			BigInteger[][] addResult = new BigInteger[twoClientsResult.length][];
			BigInteger[][] multResult = new BigInteger[twoClientsResult.length][];
			for (int i = 0; i < twoClientsResult.length; i++) {
				addResult[i] = twoClientsResult[i].getAddResult();
				multResult[i] = twoClientsResult[i].getMultResult();
			}

			// 对每个数据依次加盲，结果为包含盲和密文的数组
			BigInteger[][] addAndBlind = new BigInteger[addResult.length][];
			BigInteger[][] multAndBlind = new BigInteger[multResult.length][];

			for (int i = 0; i < addAndBlind.length; i++) {
				addAndBlind[i] = KeyProdToS.blind(N, g, PK, addResult[i]);
				multAndBlind[i] = KeyProdToS.blind(N, g, PK, multResult[i]);
			}

			// 提取密文和盲
			BigInteger[][] addUnderBlind = new BigInteger[addAndBlind.length][];
			BigInteger[][] blindnessAdd = new BigInteger[addUnderBlind.length][];

			BigInteger[][] multUnderBlind = new BigInteger[multAndBlind.length][];
			BigInteger[][] blindnessMult = new BigInteger[multUnderBlind.length][];

			for (int i = 0; i < addUnderBlind.length; i++) {
				addUnderBlind[i] = new BigInteger[] { addAndBlind[i][0], addAndBlind[i][1] };
				blindnessAdd[i] = new BigInteger[] { addAndBlind[i][2], addAndBlind[i][3] };

				multUnderBlind[i] = new BigInteger[] { multAndBlind[i][0], multAndBlind[i][1] };
				blindnessMult[i] = new BigInteger[] { multAndBlind[i][2], multAndBlind[i][3] };
			}

			// 发送数据等待计算
			obtos.writeObject(h);
			obtos.writeObject(PK);
			obtos.writeObject(addUnderBlind);
			obtos.writeObject(multUnderBlind);

			// 接收到的第一个两个用户加盲以h为底的密文
			BigInteger[][] addWiZi = (BigInteger[][]) obfroms.readObject();
			BigInteger[][] multWiZi = (BigInteger[][]) obfroms.readObject();

			// 去盲后的以h为底密文
			BigInteger[][] addResultOnH = new BigInteger[addWiZi.length][];
			BigInteger[][] multResultOnH = new BigInteger[multWiZi.length][];

			for (int i = 0; i < addWiZi.length; i++) {
				addResultOnH[i] = CInteract.add(N, addWiZi[i], BCPForC.enc(N, g, h, blindnessAdd[i][1]));
				multResultOnH[i] = CInteract.add(N, multWiZi[i], BCPForC.enc(N, g, h, blindnessMult[i][1]));
			}

			SingleBasePairComputeResult[] resultOnH = new SingleBasePairComputeResult[addResultOnH.length];
			for (int i = 0; i < resultOnH.length; i++) {
				SingleBasePairComputeResult singleBasePairComputeResult = new SingleBasePairComputeResult();

				singleBasePairComputeResult.setAddResult(addResultOnH[i]);
				singleBasePairComputeResult.setMultResult(multResultOnH[i]);

				resultOnH[i] = singleBasePairComputeResult;
			}
			return resultOnH;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {

	}

}
