package interaction;

import java.math.BigInteger;
import java.util.ArrayList;

public class CInteract {

	public static BigInteger[] add(BigInteger N, BigInteger[] a, BigInteger[] b) {
		BigInteger N2 = N.multiply(N);
		BigInteger newA = a[0].multiply(b[0]).mod(N2);
		BigInteger newB = a[1].multiply(b[1]).mod(N2);
		BigInteger[] newcipher = new BigInteger[] { newA, newB };
		return newcipher;
	}

	public static boolean check(BigInteger[] a) {
		if (a.length != 2) {
			return false;
		}
		return true;
	}

	// deep copy
	public static ArrayList<BigInteger[][][]> copyList(ArrayList<BigInteger[][][]> c) {
		ArrayList<BigInteger[][][]> copylist = new ArrayList<BigInteger[][][]>(c.size());
		for (BigInteger[][][] b : c) {
			copylist.add(b.clone());
		}
		return copylist;
	}

	public static BigInteger additiveInverse(BigInteger m, BigInteger N) {
		return N.subtract(m);
	}

}
