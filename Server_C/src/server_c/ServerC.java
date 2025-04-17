package server_c;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import client.OnlineClient;
import cryptography.BCPForC;
import cryptography.PP;
import network.listen.ListenToClientDaemon;

public class ServerC extends AbstractServerC {

	// --------------other methods---------------------------

	public void newBCP(PP pp) {
		setBcp(new BCPForC(pp));
	}

	public void newBCP() {
		setBcp(new BCPForC());
	}

	public void resetBCP() {
		setBcp(null);
	}

	public final boolean isBCPSet() {
		return getBcp() != null;
	}

	public void listenToClient(int port) throws IOException {
		// 不应该在这里关闭serversocket，应该先结束监听线程再关闭，这个方法存在的意义已经不太大了
		this.setClientServerSocket(new ServerSocket(port));
	}

	public void stopListeningToClient() throws IOException {
		ListenToClientDaemon listenTask = getListenToClientTask();
		if (listenTask != null) {
			listenTask.end();
			setListenToClientTask(null);
		}

		ServerSocket clientServerSocket = getClientServerSocket();
		if (clientServerSocket != null) {
			clientServerSocket.close();
			setClientServerSocket(null);
		}
	}

	public int clientNum() {
		return onlineClientMap.size();
	}

	public void closeDBConnection() throws SQLException {
		this.dbConnection.close();
		this.dbConnection = null;
	}

	public void listenToS(int port) throws IOException {
		this.passivelyServerSocket = new ServerSocket(port);
	}

	public void connectToS(String IP, int port) throws UnknownHostException, IOException {
		this.forwardlySocket = new Socket(IP, port);
	}

	public void addClient(String username, OnlineClient client) {
		getOnlineClientMap().put(username, client);
	}

	public OnlineClient removeClient(String username) {
		OnlineClient clientForC = onlineClientMap.get(username);
		try {
			clientForC.offline();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return onlineClientMap.remove(username);
	}

	public Set<String> onlineUsernameSet() {
		return onlineClientMap.keySet();
	}

	public Collection<OnlineClient> onlineClientCollection() {
		return onlineClientMap.values();
	}

}
