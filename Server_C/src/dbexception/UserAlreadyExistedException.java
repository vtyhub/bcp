package dbexception;

public class UserAlreadyExistedException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserAlreadyExistedException() {
		// TODO Auto-generated constructor stub
		super();
	}

	public UserAlreadyExistedException(String description) {
		super(description);
	}

}
