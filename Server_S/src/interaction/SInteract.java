package interaction;

import java.math.BigInteger;

public class SInteract {

	public static BigInteger additiveInverse(BigInteger m, BigInteger N) {
		return N.subtract(m);
	}
	
	//S不需要做加法，仅在测试使用
	public static BigInteger[] add(BigInteger N, BigInteger[] a, BigInteger[] b) {
		BigInteger N2 = N.multiply(N);
		BigInteger newA = a[0].multiply(b[0]).mod(N2);
		BigInteger newB = a[1].multiply(b[1]).mod(N2);
		BigInteger[] newcipher = new BigInteger[] { newA, newB };
		return newcipher;
	}
}
