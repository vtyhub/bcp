package dbexception;

public class NoSuchUserException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public NoSuchUserException() {
		// TODO Auto-generated constructor stub
		super();
	}

	public NoSuchUserException(String description) {
		super(description);
	}
}
