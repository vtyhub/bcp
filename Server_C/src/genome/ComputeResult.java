package genome;

import java.io.Serializable;
import java.util.ArrayList;

public class ComputeResult implements Serializable {

	private static final long serialVersionUID = -4234064384928367453L;

	private String usernameA;
	private String usernameB;

	private SingleBasePairComputeResult[] result;
	private ArrayList<SingleBasePairComputeResult> resultList = new ArrayList<>();

	public String getUsernameA() {
		return usernameA;
	}

	public void setUsernameA(String usernameA) {
		this.usernameA = usernameA;
	}

	public String getUsernameB() {
		return usernameB;
	}

	public void setUsernameB(String usernameB) {
		this.usernameB = usernameB;
	}

	public SingleBasePairComputeResult[] getResult() {
		return result;
	}

	public void setResult(SingleBasePairComputeResult[] result) {
		this.result = result;
	}

	public ArrayList<SingleBasePairComputeResult> getResultList() {
		return resultList;
	}

	public void setResultList(ArrayList<SingleBasePairComputeResult> resultList) {
		this.resultList = resultList;
	}

}
