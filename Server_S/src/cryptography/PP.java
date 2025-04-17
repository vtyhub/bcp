package cryptography;

import java.io.Serializable;
import java.math.BigInteger;

public class PP implements Serializable {
	private static final long serialVersionUID = -3920544574872572608L;
	private BigInteger N;
	private BigInteger k;
	private BigInteger g;

	public PP(BigInteger N, BigInteger k, BigInteger g) {
		this.N = N;
		this.k = k;
		this.g = g;
	}

	public BigInteger getN() {
		return N;
	}

	public BigInteger getK() {
		return k;
	}

	public BigInteger getG() {
		return g;
	}

	public BigInteger[] getAll() {
		return new BigInteger[] { N, k, g };
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof PP) {
			PP pp2 = (PP) obj;
			return N.equals(pp2.N) && k.equals(pp2.k) && g.equals(pp2.g);
		} else {
			return false;
		}
	}
}
