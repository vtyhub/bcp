package cryptography;

import java.math.BigInteger;

public class BlindCiphertext {

	private BigInteger[][] ciphertextOnBlind;
	private BigInteger[] blindness;
	
	public BigInteger[][] getCiphertextOnBlind() {
		return ciphertextOnBlind;
	}
	public void setCiphertextOnBlind(BigInteger[][] ciphertextOnBlind) {
		this.ciphertextOnBlind = ciphertextOnBlind;
	}
	public BigInteger[] getBlindness() {
		return blindness;
	}
	public void setBlindness(BigInteger[] blindness) {
		this.blindness = blindness;
	}
	
	
}
