package interaction;

import java.math.BigInteger;

import cryptography.BCPForC;

public class MultFromS {

	public static BigInteger[] genRemoveBlind(BigInteger N, BigInteger g, BigInteger h, BigInteger blindA,
			BigInteger blindB) {
		return BCPForC.enc(N, g, h, CInteract.additiveInverse(blindA, N).multiply(blindB).mod(N));
	}

	public static BigInteger[] mult(BigInteger N, BigInteger Z1, BigInteger T1, BigInteger A1, BigInteger B1,
			BigInteger blindA, BigInteger Z2, BigInteger T2, BigInteger A2, BigInteger B2, BigInteger blindB) {
		BigInteger N2 = N.multiply(N);

		BigInteger Z1modN2 = Z1.mod(N2);
		BigInteger A1Powt2modN2 = A1.modPow(blindB, N2);
		BigInteger A2Powt1modN2 = A2.modPow(blindA, N2);
		BigInteger T1modN2 = T1.mod(N2);
		BigInteger A3 = Z1modN2.multiply(A1Powt2modN2).multiply(A2Powt1modN2).multiply(T1modN2).mod(N2);

		BigInteger Z2modN2 = Z2.mod(N2);
		BigInteger B1Powt2modN2 = B1.modPow(blindB, N2);
		BigInteger B2Powt1modN2 = B2.modPow(blindA, N2);
		BigInteger T2modN2 = T2.mod(N2);
		BigInteger B3 = Z2modN2.multiply(B1Powt2modN2).multiply(B2Powt1modN2).multiply(T2modN2).mod(N2);

		BigInteger[] newcipher = { A3, B3 };
		return newcipher;
	}

	public static BigInteger[] multi(BigInteger N, BigInteger g, BigInteger h, BigInteger Z1, BigInteger blindA,
			BigInteger A1, BigInteger B1, BigInteger Z2, BigInteger blindB, BigInteger A2, BigInteger B2) {
		BigInteger[] T12 = genRemoveBlind(N, g, h, blindA, blindB);
		return mult(N, Z1, T12[0], A1, B1, blindA, Z2, T12[1], A2, B2, blindB);
	}
	
	public static void main(String[] args) {
		BigInteger m=new BigInteger("1234567");
		BigInteger N = new BigInteger("7654321");
		BigInteger inverse = CInteract.additiveInverse(m,N);
		System.out.println(inverse);
		BigInteger m2 = CInteract.additiveInverse(inverse, N);
		System.out.println(m2);
	}
}
