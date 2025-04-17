package dbexception;

public class WrongPasswordException extends Exception {

	private static final long serialVersionUID = 1L;

	public WrongPasswordException(String description) {
		// TODO Auto-generated constructor stub
		super(description);
	}
	
	public WrongPasswordException() {
		// TODO Auto-generated constructor stub
		super();
	}
}
