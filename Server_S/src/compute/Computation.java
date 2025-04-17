package compute;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;

import constant.CommonClass.RemoteHostNetworkInfo;
import cryptography.BCP;
import heartbeat.HeartbeatDetection;
import network.ForwardlyConnectToC;
import server_s.AbstractServerS.ConnectionMode;
import server_s.AbstractServerS.UseWhatToConnect;

public class Computation {

	private long computationNumber;// S应当开启被动连接

	// 该次计算使用的bcp实例 ，内含PP和MK
	private BCP bcp;// 应该导出
	private BigInteger PK;// 所有客户端公钥相加得到的PK //应该导出
//	private ComputeClient[] computeClientArray;// 所有参与计算的客户端，需要保存他们的用户名和公钥，不需要保存密文//导出

	// 网络通信部分
	private Socket forwardlySocket;
	private RemoteHostNetworkInfo infoC;// 导出
	private ObjectInputStream obfromc;
	private ObjectOutputStream obtoc;
	private ForwardlyConnectToC forwardlyCommunicateTask;
	private HeartbeatDetection heartbeatDetectionTask;

	// 连接方式
	private ConnectionMode connectionMode = ConnectionMode.PASSIVE;// 导出
	private UseWhatToConnect forwardlyUseWhat = UseWhatToConnect.IP;// 导出

	public Computation() {
		// TODO Auto-generated constructor stub
	}

	public long getComputationNumber() {
		return computationNumber;
	}

	public void setComputationNumber(long computationNumber) {
		this.computationNumber = computationNumber;
	}



	public BCP getBcp() {
		return bcp;
	}

	public void setBcp(BCP bcp) {
		this.bcp = bcp;
	}

	public BigInteger getPK() {
		return PK;
	}

	public void setPK(BigInteger pK) {
		PK = pK;
	}

	public Socket getForwardlySocket() {
		return forwardlySocket;
	}

	public void setForwardlySocket(Socket forwardlySocket) {
		this.forwardlySocket = forwardlySocket;
	}

	public RemoteHostNetworkInfo getInfoC() {
		return infoC;
	}

	public void setInfoC(RemoteHostNetworkInfo infoC) {
		this.infoC = infoC;
	}

	public ObjectInputStream getObfromc() {
		return obfromc;
	}

	public void setObfromc(ObjectInputStream obfromc) {
		this.obfromc = obfromc;
	}

	public ObjectOutputStream getObtoc() {
		return obtoc;
	}

	public void setObtoc(ObjectOutputStream obtoc) {
		this.obtoc = obtoc;
	}

	public ForwardlyConnectToC getForwardlyCommunicateTask() {
		return forwardlyCommunicateTask;
	}

	public void setForwardlyCommunicateTask(ForwardlyConnectToC forwardlyCommunicateTask) {
		this.forwardlyCommunicateTask = forwardlyCommunicateTask;
	}

	public HeartbeatDetection getHeartbeatDetectionTask() {
		return heartbeatDetectionTask;
	}

	public void setHeartbeatDetectionTask(HeartbeatDetection heartbeatDetectionTask) {
		this.heartbeatDetectionTask = heartbeatDetectionTask;
	}

	public ConnectionMode getConnectionMode() {
		return connectionMode;
	}

	public void setConnectionMode(ConnectionMode connectionMode) {
		this.connectionMode = connectionMode;
	}

	public UseWhatToConnect getForwardlyUseWhat() {
		return forwardlyUseWhat;
	}

	public void setForwardlyUseWhat(UseWhatToConnect forwardlyUseWhat) {
		this.forwardlyUseWhat = forwardlyUseWhat;
	}


}
