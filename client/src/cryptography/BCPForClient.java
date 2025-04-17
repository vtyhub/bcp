package cryptography;

import java.math.BigInteger;
import java.util.Random;

import constant.BCPConstant;

public class BCPForClient implements BCPConstant {

	// -------------------field---------------------------
	private PP pp;
	private BigInteger h;
	private BigInteger a;

	// --------------------constructor-----------------------
	public BCPForClient(PP pp) {
		// TODO Auto-generated constructor stub
		this.pp = pp;
	}

	public BCPForClient() {
	}

	// -----------------------get-----------------------
	public PP getPP() {
		return pp;
	}

	public BigInteger getH() {
		return h;
	}

	public BigInteger getA() {
		return a;
	}

	// ------------------------set-------------------------
	public void setPP(PP pp) {
		this.pp = pp;
	}

	public void setH(BigInteger h) {
		this.h = h;
	}

	public void setA(BigInteger a) {
		this.a = a;
	}

	// -----------------------other methods--------------------
	public final boolean isPPSet() {
		return getPP() != null;
	}

	public final boolean isPkSet() {
		return getH() != null;
	}

	public final boolean isSkSet() {
		return getA() != null;
	}

	public final boolean isKeyPairSet() {
		return isPkSet() && isSkSet();
	}

	// ----------------------generate pk and sk---------------------------------
	private static BigInteger[] keyGen(BigInteger N, BigInteger g, BigInteger a) {
		// 原始KeyGen，所有参数都已知
		BigInteger h = g.modPow(a, N.pow(2));
		BigInteger[] keypair = { h, a };
		return keypair;
	}

	public static BigInteger[] keyGen(PP pp) {
		return keyGen(pp.getN(), pp.getG());
	}

	public static BigInteger[] keyGen(BigInteger N, BigInteger g) {
		// 指定N的比特长度，随机生成a的keyGE
		// 确定a小于N2
		BigInteger N2 = N.multiply(N);
		BigInteger a = new BigInteger(N2.bitLength(), new Random());
		while (a.compareTo(N2) != -1 || a.compareTo(BigInteger.ZERO) == -1) {
			a = new BigInteger(N2.bitLength(), new Random());
		}
		return keyGen(N, g, a);
	}

	// ------------------------enc dec------------------------------
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

}
