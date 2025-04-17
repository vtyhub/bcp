package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

import client.ClientToS;
import constant.NetConstant;
import cryptography.BCP;
import cryptography.BCP.MK;
import cryptography.PP;
import interaction.GenPK;
import server_s.ServerS;

public class Communicate implements NetConstant {

	// 1
	public static void keyProd(BlockingQueue<Object> readQueue, ObjectOutputStream obtoc, ServerS serverS)
			throws ClassNotFoundException, IOException, InterruptedException {
		// 获取BCP实例
		BCP bcp = serverS.getBcp();
		PP pp = bcp.getPP();
		MK mk = bcp.getMK();

		// 1:保存C发送来的用户并计算PK
		Object rb = readQueue.take();
		System.out.println(rb);
		ClientToS[] computeClients = (ClientToS[]) rb;

		BigInteger PK = GenPK.genPK(pp.getN(), computeClients);
		serverS.setPK(PK);

		// 1.2 将用户密文换成用PK加密并发送给C
		ClientToS[] convertedClients = new ClientToS[computeClients.length];

		// 对于每一个用户
		for (int a = 0; a < computeClients.length; a++) {
			// 发送给C的无内容用户
			ClientToS clientToC = new ClientToS();

			// 从C获取的有内容的用户
			ClientToS computeClient = computeClients[a];

			// 获取公钥和加盲密文，没有获取用户名
			BigInteger h = computeClient.getH();
			BigInteger[][] blindcipher = computeClient.getBlindedCiphertext();

			// 转换后的以PK为底的加盲密文
			BigInteger[][] encryptedOnPK = new BigInteger[blindcipher.length][];

			// 1.2.0 把C发过来的加盲密文clientTos[]用mk解密出Zi[]
			for (int j = 0; j < blindcipher.length; j++) {
				// 把加盲密文用mk解密
				BigInteger zi = BCP.mDec(pp, h, mk, blindcipher[j]);

				// 把解密出的加盲密文用PK加密，成功实现转化
				encryptedOnPK[j] = BCP.enc(pp, PK, zi);
			}
			// 发送给C的仅设置了必需的转化密文，没有设置用户名以及公钥
			clientToC.setBlindedCiphertext(encryptedOnPK);

			convertedClients[a] = clientToC;
		}

		obtoc.writeObject(convertedClients);
	}

	// 3
	public static void mult(PP pp, MK mk, BigInteger PK, BlockingQueue<Object> readQueue, ObjectOutputStream obtoc) {

		try {
			Object response = readQueue.take();
			BigInteger[][] ComputeClient = (BigInteger[][]) response;

			BigInteger[] clientAcipher = ComputeClient[0];
			BigInteger[] clientBcipher = ComputeClient[1];

			BigInteger z1 = BCP.mDec(pp, PK, mk, clientAcipher);
			BigInteger z2 = BCP.mDec(pp, PK, mk, clientBcipher);
			BigInteger[] enc = BCP.enc(pp, PK, z1.multiply(z2));

			obtoc.writeObject(enc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 4 把用PK加密的解密成用指定h加密的
	// 接收singbasepair数组，这个为两个用户之间的结果，把里面内容全部解密成用h加密的
	public static void transDec(PP pp, MK mk, BlockingQueue<Object> readQueue, ObjectOutputStream obtoc) {
		try {
			// 接收公钥以及加乘结果
			Object response = readQueue.take();
			BigInteger h = (BigInteger) response;
			BigInteger PK = (BigInteger) readQueue.take();
			BigInteger[][] addUnderBlind = (BigInteger[][]) readQueue.take();
			BigInteger[][] multUnderBlind = (BigInteger[][]) readQueue.take();

			// 生成WiZi
			BigInteger[][] addWiZi = new BigInteger[addUnderBlind.length][];
			BigInteger[][] multWiZi = new BigInteger[multUnderBlind.length][];

			for (int i = 0; i < addUnderBlind.length; i++) {
				BigInteger addZ = BCP.mDec(pp, PK, mk, addUnderBlind[i]);
				BigInteger multZ = BCP.mDec(pp, PK, mk, multUnderBlind[i]);

				addWiZi[i] = BCP.enc(pp, h, addZ);
				multWiZi[i] = BCP.enc(pp, h, multZ);
			}

			// 发送转换公钥结果
			obtoc.writeObject(addWiZi);
			obtoc.writeObject(multWiZi);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
