package network.withs;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import constant.NetConstant;
import server_c.ServerC;

public class ForwardlyConnectToS implements Runnable, NetConstant {

	private Socket CClientSocket;
	@SuppressWarnings("unused")
	private ServerC C;
	private InputStream fromS;
	private OutputStream toS;

	public ForwardlyConnectToS(ServerC C, Socket CClientSocket) {
		// TODO Auto-generated constructor stub
		this.C = C;
		this.CClientSocket = CClientSocket;

		try {
			fromS = this.CClientSocket.getInputStream();
			toS = this.CClientSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ObjectOutputStream obtos = new ObjectOutputStream(toS);
			ObjectInputStream obfroms = new ObjectInputStream(fromS);
			
			// 0 ALOHA
			obtos.writeObject(ALOHA);
			if (!OK.equals(obfroms.readObject())) {
				return;
			}
			System.out.println("aloha success");

			// 1 init
			// 从S处接收PP
			// 将PP发送给每个用户
			// 从每个用户处接收h和原始密文，并存储起来
			System.out.println("init success");
			//2 keyProd
//			Communicate.keyProd(obfroms, obtos, C);
			System.out.println("keyprod success");
			//3 add and mult
				
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		ServerC C = new ServerC();
		Socket socket = new Socket("localhost", 8000);
		new Thread(new ForwardlyConnectToS(C, socket)).start();
	}
}
