package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import constant.CommonClass.RemoteHostNetworkInfo;
import cryptography.BCPForClient;
import cryptography.PP;

public class Client extends AbstractClient {

	public boolean isBCPSet() {
		return getBcp() != null;
	}

	public void connectToServer(String host, int port) throws UnknownHostException, IOException {
		this.setSocket(new Socket(host, port));
		this.setServerInfo(new RemoteHostNetworkInfo(socket, serverDomainName));
	}

	public void initServerInfo(Socket socket, String domainName) {
		this.setServerInfo(new RemoteHostNetworkInfo(socket, domainName));
	}

	public void newBCP() {
		this.setBcp(new BCPForClient());
	}

	public void newBCP(PP pp) {
		this.setBcp(new BCPForClient(pp));
	}

	public void resetBCP() {
		this.setBcp(null);
	}
}
