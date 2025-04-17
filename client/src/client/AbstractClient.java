package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;

import constant.CommonClass.RemoteHostNetworkInfo;
import constant.NetConstantClient;
import cryptography.BCPForClient;
import genome.ComputeResult;
import genome.DecryptedResult;
import genome.Result;
import network.HeartbeatThread;
import view.MainUI;

public abstract class AbstractClient {

	protected String username;

	// 连接使用
	protected String serverIP;// 连接使用的参数，不能集成
	protected String serverHostname;
	protected String serverDomainName;
	protected UseWhatToConnect useWhat = UseWhatToConnect.IP;
	protected int serverPort = NetConstantClient.DEFAULT_SERVER_UNSET_PORT;

	protected Socket socket;// 常用，不集成在远端信息中
	protected ObjectInputStream obfromc;
	protected ObjectOutputStream obtoc;
	protected RemoteHostNetworkInfo serverInfo;
	protected HeartbeatThread heartbeat;

	protected MainUI mainui;

	protected BCPForClient bcp;

	protected ArrayList<String> encodingBasePairsList;// 编码后的碱基对，每个碱基都是一个元素
	protected String BasePairs;// 原始碱基对
	protected BigInteger[][] encryptedEncodingBasePairs;// 发送给服务器的变量

	protected ComputeResult[] comptuteResult;
	protected DecryptedResult[] decResult;
	protected Result[] finalResult;// 格式为 用户名String 概率double
	// protected

	// ----------连接属性使用的参数-------------------
	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverCIP) {
		this.serverIP = serverCIP;
	}

	public int getServerPort() {
		return serverPort;
	}

	public DecryptedResult[] getDecResult() {
		return decResult;
	}

	public void setDecResult(DecryptedResult[] decResult) {
		this.decResult = decResult;
	}

	public void setServerPort(int serverCPort) {
		this.serverPort = serverCPort;
	}

	public String getServerHostname() {
		return serverHostname;
	}

	public void setServerHostname(String serverHostname) {
		this.serverHostname = serverHostname;
	}

	public String getServerDomainName() {
		return serverDomainName;
	}

	public void setServerDomainName(String serverDomainName) {
		this.serverDomainName = serverDomainName;
	}

	public UseWhatToConnect getUseWhat() {
		return useWhat;
	}

	public void setUseWhat(UseWhatToConnect useWhat) {
		this.useWhat = useWhat;
	}

	// -----------------------------------------------

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public RemoteHostNetworkInfo getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(RemoteHostNetworkInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	public BCPForClient getBcp() {
		return bcp;
	}

	public void setBcp(BCPForClient bcpForClient) {
		this.bcp = bcpForClient;
	}
	
	public Result[] getFinalResult() {
		return finalResult;
	}

	public void setFinalResult(Result[] finalResult) {
		this.finalResult = finalResult;
	}

	public MainUI getMainui() {
		return mainui;
	}

	public void setMainui(MainUI mainui) {
		this.mainui = mainui;
	}

	public HeartbeatThread getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(HeartbeatThread heartbeat) {
		this.heartbeat = heartbeat;
	}

	public String getBasePairs() {
		return BasePairs;
	}

	public void setBasePairs(String basePairs) {
		BasePairs = basePairs;
	}

	public ArrayList<String> getEncodingBasePairsList() {
		return encodingBasePairsList;
	}

	public void setEncodingBasePairsList(ArrayList<String> encodingBasePairsList) {
		this.encodingBasePairsList = encodingBasePairsList;
	}

	public BigInteger[][] getEncryptedEncodingBasePairs() {
		return encryptedEncodingBasePairs;
	}

	public void setEncryptedEncodingBasePairs(BigInteger[][] encryptedEncodingBasePairs) {
		this.encryptedEncodingBasePairs = encryptedEncodingBasePairs;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public ComputeResult[] getComptuteResult() {
		return comptuteResult;
	}

	public void setComptuteResult(ComputeResult[] comptuteResult) {
		this.comptuteResult = comptuteResult;
	}

	// ==========================================
	public static enum UseWhatToConnect {
		IP, Hostname, DomainName
	}

}
