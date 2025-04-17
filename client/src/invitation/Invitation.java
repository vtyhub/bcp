package invitation;

import java.io.Serializable;

public class Invitation implements Serializable {

	private static final long serialVersionUID = -5694415945309393886L;

	private String inviter;
	private int length;
	private String submissiontime;
	private String starttime;
	private String finishedtime;

	public Invitation(String inviter, int length, String submissiontime, String starttime, String finishedtime) {
		// TODO Auto-generated constructor stub
		this.inviter = inviter;
		this.length = length;
		this.submissiontime = submissiontime;
		this.starttime = starttime;
		this.finishedtime = finishedtime;
	}

	public String getInviter() {
		return inviter;
	}

	public void setInviter(String inviter) {
		this.inviter = inviter;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getSubmissiontime() {
		return submissiontime;
	}

	public void setSubmissiontime(String submissiontime) {
		this.submissiontime = submissiontime;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getFinishedtime() {
		return finishedtime;
	}

	public void setFinishedtime(String finishedtime) {
		this.finishedtime = finishedtime;
	}

}
