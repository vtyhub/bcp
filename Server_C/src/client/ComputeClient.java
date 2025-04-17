package client;

import java.math.BigInteger;

import cryptography.PP;

public class ComputeClient extends Client {
	// 该客户端实例可以离线，必须数据库中有pk，cipher，pp等参数才可能计算

	private static final long serialVersionUID = 1L;

	private PP pp;
	
	private BigInteger[][] originalCiphertext;// 原始密文,全局唯一

	// 加盲后原始密文不会再被使用到，只需存储盲即可
	private BigInteger[] blindness;// 对原始密文使用的盲

	private BigInteger[][] originalEncryptedOnPK;// 以PK为底加密后的原始密文

	public PP getPp() {
		return pp;
	}

	public void setPp(PP pp) {
		this.pp = pp;
	}

	public BigInteger[][] getOriginalCiphertext() {
		return originalCiphertext;
	}

	public void setOriginalCiphertext(BigInteger[][] originalCiphertext) {
		this.originalCiphertext = originalCiphertext;
	}

	public BigInteger[][] getOriginalEncryptedOnPK() {
		return originalEncryptedOnPK;
	}

	public void setOriginalEncryptedOnPK(BigInteger[][] originalEncryptedOnPK) {
		this.originalEncryptedOnPK = originalEncryptedOnPK;
	}

	public BigInteger[] getBlindness() {
		return blindness;
	}

	public void setBlindness(BigInteger[] blindness) {
		this.blindness = blindness;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof ComputeClient) {
			ComputeClient comp = (ComputeClient) obj;
			// 两者有共同PP且碱基长度相同才能归为一类
			return pp.equals(comp.getPp()) && originalCiphertext.length == comp.originalCiphertext.length;
		}
		return false;
	}

}
