package genome;

import java.math.BigInteger;

import cryptography.PP;

public class DecResult {

	private PP pp;
	private BigInteger PK;

	private BigInteger hA;

	private String usernameA;
	private String usernameB;

	private SingleBasePairComputeResult[] result;

	public String getUsernameA() {
		return usernameA;
	}

	public void setUsernameA(String usernameA) {
		this.usernameA = usernameA;
	}

	public String getUsernameB() {
		return usernameB;
	}

	public void setUsernameB(String usernameB) {
		this.usernameB = usernameB;
	}

	public SingleBasePairComputeResult[] getResult() {
		return result;
	}

	public void setResult(SingleBasePairComputeResult[] result) {
		this.result = result;
	}

	public PP getPp() {
		return pp;
	}

	public void setPp(PP pp) {
		this.pp = pp;
	}

	public BigInteger getPK() {
		return PK;
	}

	public void setPK(BigInteger pK) {
		PK = pK;
	}

	public BigInteger gethA() {
		return hA;
	}

	public void sethA(BigInteger hA) {
		this.hA = hA;
	}

}
