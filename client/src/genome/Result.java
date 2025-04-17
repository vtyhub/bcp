package genome;

public class Result {

	private String usernamea;
	private String usernameb;
	private boolean[] result;// 长度应等同于提交碱基长度
	private double rate;

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

	public boolean[] getResult() {
		return result;
	}

	public void setResult(boolean[] result) {
		this.result = result;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}
	
}
