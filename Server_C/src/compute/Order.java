package compute;

public class Order {

	private String username;
	private long number;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return username.hashCode() + (int) number;
	}
	

	public Order(String username, long number) {
		// TODO Auto-generated constructor stub
		this.username = username;
		this.number = number;
	}
}
