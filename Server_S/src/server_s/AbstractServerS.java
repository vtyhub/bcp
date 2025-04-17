package server_s;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import constant.CommonClass;
import cryptography.BCP;
import heartbeat.HeartbeatDetection;
import network.ForwardlyConnectToC;
import network.PutMessageTask;
import network.TakeMessageTask;

public abstract class AbstractServerS implements CommonClass {

	// C's info
	protected RemoteHostNetworkInfo infoC;

	// connection mode
	protected ConnectionMode connectionMode = ConnectionMode.ACTIVE;
	protected UseWhatToConnect forwardlyUseWhat = UseWhatToConnect.IP;

	// 主动连接C 需停止
	protected Socket forwardlySocket;//
	protected ObjectInputStream forwardlyObFromC;//
	protected ObjectOutputStream forwardlyObToC;//

	// socket 消息队列 需清空
	protected BlockingQueue<Object> socketQueue;

	// socket消息队列 需停止
	protected PutMessageTask putTask;
	protected TakeMessageTask takeTask;

	// 被动接收C连接 需停止
	protected ServerSocket passivelyServerSocket;
	protected Socket passivelySocket;
	protected ObjectInputStream passivelyObFromC;
	protected ObjectOutputStream passivelyObToC;

	// 主动通信线程 未启用 需停止
	protected ForwardlyConnectToC forwardlyCommunicateTask;

	// 被动通信线程 未启用 需停止
	// protected PassivelyConnectedByC passivelyCommunicateTask;

	// 检测来自C的心跳包线程 需停止
	protected HeartbeatDetection heartbeatDetectionTask;

	protected BCP bcp;
	protected BigInteger PK;// 本次计算使用的公共密钥

	// ------------get set-----------------------------------------
	public RemoteHostNetworkInfo getInfoC() {
		return infoC;
	}

	public void setInfoC(RemoteHostNetworkInfo infoC) {
		this.infoC = infoC;
	}

	public ConnectionMode getConnectionMode() {
		return connectionMode;
	}

	public void setConnectionMode(ConnectionMode connectionMode) {
		this.connectionMode = connectionMode;
	}

	public Socket getForwardlySocket() {
		return forwardlySocket;
	}

	public void setForwardlySocket(Socket forwardlySocket) {
		this.forwardlySocket = forwardlySocket;
	}

	public ServerSocket getPassivelyServerSocket() {
		return passivelyServerSocket;
	}

	public void setPassivelyServerSocket(ServerSocket passivelyServerSocket) {
		this.passivelyServerSocket = passivelyServerSocket;
	}

	public Socket getPassivelySocket() {
		return passivelySocket;
	}

	public void setPassivelySocket(Socket passivelySocket) {
		this.passivelySocket = passivelySocket;
	}

	public ForwardlyConnectToC getForwardlyCommunicateTask() {
		return forwardlyCommunicateTask;
	}

	public void setForwardlyCommunicateTask(ForwardlyConnectToC forwardlyCommunicateTask) {
		this.forwardlyCommunicateTask = forwardlyCommunicateTask;
	}

	public ObjectInputStream getForwardlyObFromC() {
		return forwardlyObFromC;
	}

	public void setForwardlyObFromC(ObjectInputStream forwardlyObFromC) {
		this.forwardlyObFromC = forwardlyObFromC;
	}

	public ObjectOutputStream getForwardlyObToC() {
		return forwardlyObToC;
	}

	public void setForwardlyObToC(ObjectOutputStream forwardlyObToC) {
		this.forwardlyObToC = forwardlyObToC;
	}

	public ObjectInputStream getPassivelyObFromC() {
		return passivelyObFromC;
	}

	public void setPassivelyObFromC(ObjectInputStream passivelyObFromC) {
		this.passivelyObFromC = passivelyObFromC;
	}

	public ObjectOutputStream getPassivelyObToC() {
		return passivelyObToC;
	}

	public void setPassivelyObToC(ObjectOutputStream passivelyObToC) {
		this.passivelyObToC = passivelyObToC;
	}

	public UseWhatToConnect getForwardlyUseWhat() {
		return forwardlyUseWhat;
	}

	public void setForwardlyUseWhat(UseWhatToConnect forwardlyConnectUseWhat) {
		this.forwardlyUseWhat = forwardlyConnectUseWhat;
	}

	public HeartbeatDetection getHeartbeatDetectionTask() {
		return heartbeatDetectionTask;
	}

	public void setHeartbeatDetectionTask(HeartbeatDetection heartbeatDetectionTask) {
		this.heartbeatDetectionTask = heartbeatDetectionTask;
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

	public BlockingQueue<Object> getSocketQueue() {
		return socketQueue;
	}

	public void setSocketQueue(BlockingQueue<Object> socketQueue) {
		this.socketQueue = socketQueue;
	}

	public PutMessageTask getPutTask() {
		return putTask;
	}

	public void setPutTask(PutMessageTask putTask) {
		this.putTask = putTask;
	}

	public TakeMessageTask getTakeTask() {
		return takeTask;
	}

	public void setTakeTask(TakeMessageTask takeTask) {
		this.takeTask = takeTask;
	}

	// ------------get set-----------------------------------------
	public static enum ConnectionMode {
		PASSIVE, ACTIVE
	}

	public static enum LogType {
		bcpLog, interactLog, networkLog
	}

	public static enum UseWhatToConnect {
		IP, Hostname, DomainName
	}

}
