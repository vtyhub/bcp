package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import constant.CommonClass;
import constant.NetConstant;
import model.Log;
import server_s.ServerS;

@Deprecated
public class PassivelyConnectedByC implements Runnable, NetConstant, CommonClass {

	// private ServerS serverS;

	private ServerSocket S;
	private Socket C;

	private InputStream fromC;
	private OutputStream toC;

	// private int portC;
	// private String ipC;
	// private String hostnameC;

	// private BigInteger sessionID;

	public PassivelyConnectedByC(ServerS serverS, Log networkLog) {
		// TODO Auto-generated constructor stub

		this.S = serverS.getPassivelyServerSocket();
		this.C = serverS.getPassivelySocket();
		try {
			toC = C.getOutputStream();
			fromC = C.getInputStream();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// 若出现空指针或IO异常，就没有必要继续下去
			e.printStackTrace();
			return;
		}
		// this.serverS = serverS;

		try {
			// 设置长连接，并取消缓冲区
			C.setKeepAlive(true);
			C.setTcpNoDelay(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// 本来打算使用Buffered装饰，但担心出现推送不及时的情况，不使用缓冲区
			ObjectOutputStream obtoc = new ObjectOutputStream(toC);
			ObjectInputStream obfromc = new ObjectInputStream(fromC);

			// 使用readObject()去读取一个对方的Ob流输出的int,float等基本类型时,会抛出java.io.OptionalDataException
			// 0.0进行基本验证，等待C发送ALOHA验证身份
			if (!ALOHA.equals(obfromc.readObject())) {
				// networkLog.writeToLog("Hostname: " + serverS.getServerSInfo().getHostname() +
				// "IP: "
				// + serverS.getClientCInfo().getIp() + "Port: " +
				// serverS.getClientCInfo().getUsingPort()
				// + " authentiaction failure");
				C.close();
				S.close();
				return;
			}
			obtoc.writeObject(OK);

			// 1 keyProd
			// Communicate.keyProd(obfromc, obtoc, serverS);

			// 3: mult

			// 4:TransDec

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void interactWithC(ServerSocket S, Socket C) throws IOException {
		if (S == null || C == null) {
			return;
		}
	}

	public static void interactWithC(Socket S) {

	}

}
