package cryptography;

import java.math.BigInteger;
import java.util.Random;

import constant.BCPConstant;

public class BCPForC implements BCPConstant {

	// -------------------field---------------------------
	private PP pp;

	// -----------------------get-----------------------
	public PP getPP() {
		return pp;
	}

	// ------------------------set-------------------------
	public void setPp(PP pp) {
		this.pp = pp;
	}

	// ---------------------constructor---------------------
	public BCPForC(PP pp) {
		// TODO Auto-generated constructor stub
		this.pp = pp;
	}

	public BCPForC() {
		// TODO Auto-generated constructor stub
	}

	// ------------------------other method------------------------
	public static BigInteger[] enc(BigInteger N, BigInteger g, BigInteger h, BigInteger m) {
		BigInteger N2 = N.pow(2);

		BigInteger r = new BigInteger(N2.bitLength(), new Random());
		while (r.compareTo(N2) != -1 || r.compareTo(BigInteger.ZERO) == -1) {
			r = new BigInteger(N2.bitLength(), new Random());
		}

		BigInteger A = g.modPow(r, N2);

		BigInteger B1 = h.modPow(r, N2);
		BigInteger B2 = m.multiply(N).add(BigInteger.ONE).mod(N2);
		BigInteger B = B1.multiply(B2).mod(N2);

		BigInteger[] c = { A, B };
		return c;
	}

	public static BigInteger[] enc(PP pp, BigInteger h, BigInteger m) {
		return enc(pp.getN(), pp.getG(), h, m);
	}

	public boolean isPPSet() {
		return pp != null;
	}
	
	public static BigInteger dec(BigInteger N, BigInteger a, BigInteger[] c) {
		if (c.length != 2) {
			throw new RuntimeException("Wrong length of ciphertext");
		}
		BigInteger N2 = N.multiply(N), A = c[0], B = c[1];
		BigInteger InverseA = A.modInverse(N2);// 是否是A模N2的逆元论文中并未说明

		BigInteger tempA = InverseA.modPow(a, N2);
		BigInteger tempB = B.mod(N2);
		BigInteger tempC = tempA.multiply(tempB).mod(N2);
		BigInteger tempD = tempC.subtract(BigInteger.ONE.mod(N2)).mod(N2);
		return tempD.divide(N);
	}

	// ----------static nested classes------------------
}
