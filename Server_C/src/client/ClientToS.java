package client;

import java.math.BigInteger;

public class ClientToS extends Client {

	private static final long serialVersionUID = 6813962853025319813L;

	private BigInteger[][] blindedCiphertext;// 单条数据，不分片，因为分片后计算会失去意义

	public BigInteger[][] getBlindedCiphertext() {
		return blindedCiphertext;
	}

	public void setBlindedCiphertext(BigInteger[][] blindOriginalCiphertext) {
		this.blindedCiphertext = blindOriginalCiphertext;
	}

}
