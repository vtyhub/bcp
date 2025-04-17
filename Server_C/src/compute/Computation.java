package compute;

import java.math.BigInteger;
import java.util.HashMap;

import client.ComputeClient;
import cryptography.PP;

public class Computation {

	// 公有部分
	private BigInteger PK;
	private PP pp;
	private boolean specific;// 是否是指定计算，默认为false

	// spec专有部分
	private long number;
	private ComputeClient inviter;
	private HashMap<String, ComputeClient> inviteeMap;

	// random专有部分 参与计算的用户 order中保存一个用户的用户名与其number
	// 由于和spec模式一主多次不同，rand模式所有计算者等级平等，这就要求所有参与者的number都要获取，用于甄别不同次计算的密文
	private HashMap<Order, ComputeClient> computeClientMap;

	public Computation(HashMap<Order, ComputeClient> computeClientMap, BigInteger PK) {
		// rand模式构造方法
		this.specific = false;
		this.PK = PK;
		this.computeClientMap = computeClientMap;
	}

	public Computation(ComputeClient inviter, HashMap<String, ComputeClient> inviteeMap, BigInteger PK) {
		// spec模式构造方法
		this(inviter, inviteeMap, PK, null);
	}

	public Computation(ComputeClient inviter, HashMap<String, ComputeClient> inviteeMap, BigInteger PK, PP pp) {
		// TODO Auto-generated constructor stub
		this.specific = true;
		this.PK = PK;
		this.inviter = inviter;
		this.inviteeMap = inviteeMap;
		this.pp = pp;
	}

	public Computation() {
		// TODO Auto-generated constructor stub

	}

	public long getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public HashMap<Order, ComputeClient> getComputeClientMap() {
		return computeClientMap;
	}

	public void setComputeClientMap(HashMap<Order, ComputeClient> computeClientMap) {
		this.computeClientMap = computeClientMap;
	}

	public BigInteger getPK() {
		return PK;
	}

	public void setPK(BigInteger pK) {
		PK = pK;
	}

	public boolean isSpecific() {
		return specific;
	}

	public void setSpecific(boolean specific) {
		this.specific = specific;
	}

	public ComputeClient getInviter() {
		return inviter;
	}

	public void setInviter(ComputeClient inviter) {
		this.inviter = inviter;
	}

	public HashMap<String, ComputeClient> getInviteeMap() {
		return inviteeMap;
	}

	public void setInviteeMap(HashMap<String, ComputeClient> inviteeMap) {
		this.inviteeMap = inviteeMap;
	}

	public PP getPp() {
		return pp;
	}

	public void setPp(PP pp) {
		this.pp = pp;
	}

}
