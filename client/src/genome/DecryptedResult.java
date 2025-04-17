package genome;

public class DecryptedResult {

	private String usernamea;
	private String usernameb;
	private SingleDecryption[] result;

	public DecryptedResult() {
		// TODO Auto-generated constructor stub
	}

	public DecryptedResult(String usernamea, String usernameb, SingleDecryption[] result) {
		// TODO Auto-generated constructor stub
		this.usernamea = usernamea;
		this.usernameb = usernameb;
		this.result = result;
	}

	public String getUsernamea() {
		return usernamea;
	}

	public void setUsernamea(String usernamea) {
		this.usernamea = usernamea;
	}

	public String getUsernameb() {
		return usernameb;
	}

	public void setUsernameb(String usernameb) {
		this.usernameb = usernameb;
	}

	public SingleDecryption[] getResult() {
		return result;
	}

	public void setResult(SingleDecryption[] result) {
		this.result = result;
	}

}
