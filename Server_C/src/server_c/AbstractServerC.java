package server_c;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import client.OnlineClient;
import compute.Computation;
import compute.PutTask;
import compute.TakeTask;
import constant.CommonClass.RemoteHostNetworkInfo;
import cryptography.BCPForC;
import genome.ComputeResult;
import heartbeat.HeartbeatToS;
import network.listen.ListenToClientDaemon;
import network.listen.ListenToSDaemon;
import view.MainUI;

public abstract class AbstractServerC {

	// ----------------field-----------------------------
	// UI
	protected MainUI mainui;

	// BCP instance
	protected BCPForC bcp;

	// database
	protected Connection dbConnection;

	// Forwardly communicate task with S
	protected Socket forwardlySocket;

	// Passively communicate with s
	protected ListenToSDaemon listenToSTask;
	protected ServerSocket passivelyServerSocket;
	protected Socket passivelySocket;

	// ob
	protected ObjectInputStream ObFromS;
	protected ObjectOutputStream ObToS;

	// heaetbeat to s/detect
	protected HeartbeatToS heartbeatTask;

	// online client map
	protected HashMap<String, OnlineClient> onlineClientMap = new HashMap<>();

	// computation
	protected LinkedBlockingQueue<Computation> computationQueue = new LinkedBlockingQueue<>();
	protected TakeTask takeTask;// 将计算订单(hashmap)从阻塞队头取出并依次为每一个开子线程计算的线程，结果将由子线程存入数据库
	protected PutTask putTask;// 每隔一段时间检测可以执行的指定计算，将之压入队尾

	// serversocket for client
	protected ServerSocket clientServerSocket;
	protected ListenToClientDaemon listenToClientTask;

	// remote info
	protected RemoteHostNetworkInfo sInfo;// 客户端的信息在clientforc类中存储

	protected ComputeResult[] computeResult;// 所有客户端两两之间的计算结果
	protected ComputeResult[] resultEncryptedOnH;// 所有客户端两两之间的计算结果

	// ---------------get set---------------------------

	public BCPForC getBcp() {
		return bcp;
	}

	public void setBcp(BCPForC bcp) {
		this.bcp = bcp;
	}

	public Connection getDbConnection() {
		return dbConnection;
	}

	public void setDbConnection(Connection dbConnection) {
		this.dbConnection = dbConnection;
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

	public ServerSocket getClientServerSocket() {
		return clientServerSocket;
	}

	public void setClientServerSocket(ServerSocket clientsServerSocket) {
		this.clientServerSocket = clientsServerSocket;
	}

	public ListenToClientDaemon getListenToClientTask() {
		return listenToClientTask;
	}

	public void setListenToClientTask(ListenToClientDaemon listenToClientsTask) {
		this.listenToClientTask = listenToClientsTask;
	}

	public HashMap<String, OnlineClient> getOnlineClientMap() {
		return onlineClientMap;
	}

	public void setOnlineClientMap(HashMap<String, OnlineClient> onlineClientMap) {
		this.onlineClientMap = onlineClientMap;
	}

	public ListenToSDaemon getListenToSTask() {
		return listenToSTask;
	}

	public void setListenToSTask(ListenToSDaemon listenToSTask) {
		this.listenToSTask = listenToSTask;
	}

	public ObjectInputStream getObFromS() {
		return ObFromS;
	}

	public void setObFromS(ObjectInputStream passivelyObFromS) {
		this.ObFromS = passivelyObFromS;
	}

	public ObjectOutputStream getObToS() {
		return ObToS;
	}

	public void setObToS(ObjectOutputStream passivelyObToS) {
		this.ObToS = passivelyObToS;
	}

	public HeartbeatToS getHeartbeatTask() {
		return heartbeatTask;
	}

	public void setHeartbeatTask(HeartbeatToS heartbeatTask) {
		this.heartbeatTask = heartbeatTask;
	}

	public RemoteHostNetworkInfo getsInfo() {
		return sInfo;
	}

	public void setsInfo(RemoteHostNetworkInfo sInfo) {
		this.sInfo = sInfo;
	}

	public ComputeResult[] getComputeResult() {
		return computeResult;
	}

	public void setComputeResult(ComputeResult[] computeResult) {
		this.computeResult = computeResult;
	}

	public MainUI getMainui() {
		return mainui;
	}

	public void setMainui(MainUI mainui) {
		this.mainui = mainui;
	}

	public LinkedBlockingQueue<Computation> getComputationQueue() {
		return computationQueue;
	}

	public void setComputationQueue(LinkedBlockingQueue<Computation> computationQueue) {
		this.computationQueue = computationQueue;
	}

	public TakeTask getTakeTask() {
		return takeTask;
	}

	public void setTakeTask(TakeTask takeTask) {
		this.takeTask = takeTask;
	}

	public PutTask getPutTask() {
		return putTask;
	}

	public void setPutTask(PutTask putTask) {
		this.putTask = putTask;
	}

}
