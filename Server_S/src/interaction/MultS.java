package interaction;

import java.math.BigInteger;

import cryptography.BCP;
import cryptography.BCP.MK;
import cryptography.PP;

public class MultS {

	public static BigInteger[] mult(BigInteger N, BigInteger k, BigInteger g, BigInteger PK, BigInteger mp,
			BigInteger mq, BigInteger[][] twoclientmult) {
		BigInteger client1 = BCP.mDec(N, k, g, PK, mp, mq, twoclientmult[0]);
		BigInteger client2 = BCP.mDec(N, k, g, PK, mp, mq, twoclientmult[1]);
		return BCP.enc(N, g, PK, client1.multiply(client2).mod(N));// (Z1,Z2)
	}

	public static BigInteger[] mult(PP pp, BigInteger PK, MK mk, BigInteger[][] twoclientmult) {
		return mult(pp.getN(), pp.getK(), pp.getG(), PK, mk.getMp(), mk.getMq(), twoclientmult);
	}

}
