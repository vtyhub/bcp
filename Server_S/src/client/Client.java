package client;

import java.io.Serializable;
import java.math.BigInteger;

public abstract class Client implements Serializable {

	private static final long serialVersionUID = 6205341170763866393L;
	protected String username;
	protected BigInteger h;

	public BigInteger getH() {
		return h;
	}

	public void setH(BigInteger h) {
		this.h = h;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
