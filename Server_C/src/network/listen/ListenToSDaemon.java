package network.listen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import constant.NetConstant;
import constant.ThreadConstant;
import network.withs.PassivelyConnectedByS;
import server_c.ServerC;

public class ListenToSDaemon implements Runnable, NetConstant, ThreadConstant {

	private ServerC C;
	private ServerSocket serversocket;

	private volatile boolean end = false;
	private volatile boolean pause = false;

	public ListenToSDaemon(ServerC c, ServerSocket serversocket) {
		// TODO Auto-generated constructor stub
		C = c;
		this.serversocket = serversocket;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!end) {
			while (!pause && !end) {
				try {
					Socket socketS = serversocket.accept();
					if (!pause && !end) {
						Thread connectThread = new Thread(new PassivelyConnectedByS(C, serversocket, socketS));
						connectThread.start();
					}
				} catch (IOException e) {
					// accept会在serverSocket被关闭的时候抛出IO异常
					setEnd(true);
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

		C.setListenToSTask(null);
		C.setPassivelyServerSocket(null);
		try {
			serversocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

}
