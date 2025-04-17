package interaction;

import java.math.BigInteger;
import cryptography.BCPForC;

public class KeyProdFromS {

	public static BigInteger[] removeBlind(BigInteger N, BigInteger g, BigInteger PK, BigInteger blindinverse,
			BigInteger[] c) {
		BigInteger[] enblind = BCPForC.enc(N, g, PK, blindinverse);
		return CInteract.add(N, c, enblind);
	}

}
