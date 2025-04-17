package invitation;

import java.io.Serializable;

public class Invitee implements Serializable{

	private static final long serialVersionUID = -6917080722930013280L;
	
	private String inviteeName;
	private int length;

	public Invitee(String inviteeName, int length) {
		// TODO Auto-generated constructor stub
		this.inviteeName = inviteeName;
		this.length = length;
	}

	public String getInviteeName() {
		return inviteeName;
	}

	public void setInviteeName(String inviteeName) {
		this.inviteeName = inviteeName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
