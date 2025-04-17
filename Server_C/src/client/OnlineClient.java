package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import constant.CommonClass;
import heartbeat.HeartbeatDetection;
import network.withclient.PutMessageTask;
import network.withclient.TakeMessageTask;
import server_c.ServerC;

public class OnlineClient extends Client implements CommonClass {

	private static final long serialVersionUID = 3344508505485194401L;

	private ServerC C;
	private long ID;
	private Socket clientSocket;

	// socket queue
	private BlockingQueue<Object> socketQueue = new LinkedBlockingQueue<>();
	protected PutMessageTask putMessageTask;
	protected TakeMessageTask takeMessageTask;

	private RemoteHostNetworkInfo clientNetInfo;
	// private CommunicateWithLoggedClient loggedThread;
	private HeartbeatDetection detectHeartBeat;

	private ObjectInputStream obfromclient;
	private ObjectOutputStream obtoclient;

	public OnlineClient(ServerC c, String username, Socket clientSocket) throws IOException {
		// TODO Auto-generated constructor stub
		this(c, username, clientSocket, new ObjectOutputStream(clientSocket.getOutputStream()),
				new ObjectInputStream(clientSocket.getInputStream()));
	}

	public OnlineClient(ServerC c, String username, Socket clientSocket, ObjectOutputStream obtoclient,
			ObjectInputStream obfromclient) {
		// TODO Auto-generated constructor stub
		this.C = c;
		this.username = username;
		this.clientSocket = clientSocket;
		this.clientNetInfo = new RemoteHostNetworkInfo(clientSocket);

		this.obfromclient = obfromclient;
		this.obtoclient = obtoclient;
	}

	/**
	 * 停止与该客户端相关的所有线程，关闭socket
	 * 
	 * @throws IOException
	 */
	public void offline() throws IOException {
		putMessageTask.setEnd(true);
		takeMessageTask.setEnd(true);
		detectHeartBeat.setEnd(true);
		clientSocket.close();
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public RemoteHostNetworkInfo getClientNetInfo() {
		return clientNetInfo;
	}

	public void setClientNetInfo(RemoteHostNetworkInfo clientNetInfo) {
		this.clientNetInfo = clientNetInfo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ServerC getC() {
		return C;
	}

	public void setC(ServerC c) {
		C = c;
	}

	public ObjectInputStream getObfromclient() {
		return obfromclient;
	}

	public void setObfromclient(ObjectInputStream obfromclient) {
		this.obfromclient = obfromclient;
	}

	public ObjectOutputStream getObtoclient() {
		return obtoclient;
	}

	public void setObtoclient(ObjectOutputStream obtoclient) {
		this.obtoclient = obtoclient;
	}

	public HeartbeatDetection getDetectHeartBeat() {
		return detectHeartBeat;
	}

	public void setDetectHeartBeat(HeartbeatDetection detectHeartBeat) {
		this.detectHeartBeat = detectHeartBeat;
	}

	public BlockingQueue<Object> getSocketQueue() {
		return socketQueue;
	}

	public void setSocketQueue(BlockingQueue<Object> socketQueue) {
		this.socketQueue = socketQueue;
	}

	public PutMessageTask getPutMessageTask() {
		return putMessageTask;
	}

	public void setPutMessageTask(PutMessageTask putMessageTask) {
		this.putMessageTask = putMessageTask;
	}

	public TakeMessageTask getTakeMessageTask() {
		return takeMessageTask;
	}

	public void setTakeMessageTask(TakeMessageTask takeMessageTask) {
		this.takeMessageTask = takeMessageTask;
	}

}
