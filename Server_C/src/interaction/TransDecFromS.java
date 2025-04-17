package interaction;

import java.math.BigInteger;

import cryptography.BCPForC;

public class TransDecFromS {

	// 返回一个包含了依此用所有用户密钥加密结果的二维数组,因为是密文（A，B），所以用二维数组存储所有用户的
	public static BigInteger[][] finallyDectoClient(BigInteger N, BigInteger g, BigInteger[] pk, BigInteger[][] WiZi,
			BigInteger tinverse) {
		
		BigInteger[][] AiBi = new BigInteger[WiZi.length][];
		for (int i = 0; i < WiZi.length; i++) {
			AiBi[i] = CInteract.add(N, WiZi[i], BCPForC.enc(N, g, pk[i], tinverse));
		}
		return AiBi;
	}
}
