package network.listen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import constant.ThreadConstant;
import network.withclient.ConnectedByClient;
import server_c.ServerC;

public class ListenToClientDaemon implements Runnable, ThreadConstant {

	private ServerC c;
	private ServerSocket clientServerSocket;
	private volatile boolean end = false;
	private volatile boolean pause = false;

	// 默认调用时已经开始监听，并且有serverSocket
	public ListenToClientDaemon(ServerC c) {
		// TODO Auto-generated constructor stub
		this.c = c;
		this.clientServerSocket = c.getClientServerSocket();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!end) {
			while (!pause && !end) {
				try {
					Socket singleClient = clientServerSocket.accept();
					if (!pause && !end) {
						Thread connectThread = new Thread(new ConnectedByClient(this.c, singleClient));
						connectThread.start();
					}
				} catch (IOException e) {
					// accept会在serverSocket被关闭的时候抛出IO异常
					end();
					break;
				}
			}
			if (pause && !end) {
				try {
					Thread.sleep(DEFAULT_SLEEP_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		c.setListenToClientTask(null);
		c.setClientServerSocket(null);
		try {
			clientServerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void pause() {
		this.pause = true;
	}

	public void resume() {
		this.pause = false;
	}

	public void end() {
		this.end = true;
	}

}
