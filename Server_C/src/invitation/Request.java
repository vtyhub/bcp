package invitation;

import java.io.Serializable;
import java.util.ArrayList;

public class Request implements Serializable {

	private static final long serialVersionUID = -1316833800483904907L;

	private int number;
	private String inviter;
	private int length;
	private ArrayList<Invitee> invitees;
	private String submissiontime;
	private String starttime;
	private String finishedtime;

	public Request(int number, String inviter, int length, ArrayList<Invitee> invitees, String submissiontime,
			String starttime, String finishedtime) {
		// TODO Auto-generated constructor stub
		this.number = number;
		this.invitees = invitees;
		this.inviter = inviter;
		this.length = length;
		this.submissiontime = submissiontime;
		this.starttime = starttime;
		this.finishedtime = finishedtime;

	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
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

	public ArrayList<Invitee> getInvitees() {
		return invitees;
	}

	public void setInvitees(ArrayList<Invitee> invitees) {
		this.invitees = invitees;
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

	public boolean isSpecific() {
		return invitees != null;
	}
}
