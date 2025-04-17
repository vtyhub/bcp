package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import client.Client;
import constant.NetConstant;
import constant.ThreadConstant;
import constant.ViewConstant;
import constant.CommonClass.StopableThread;
import view.login.LoginDialog;

public class HeartbeatThread extends StopableThread implements NetConstant, ThreadConstant, ViewConstant {

	private volatile int errortimes = 0;

	private Client client;
	private Socket socket;
	private ObjectOutputStream out;

	private int sendout;
	private int errorbound;
	private Object heartbeatPacket = DEFAULT_HEARTBEAT_PACKET;

	public HeartbeatThread(Client client) throws IOException {
		// TODO Auto-generated constructor stub
		this(client, DEFAULT_HEARTBEAT_PACKET, DEFAULT_HEARTBEAT_SENDING_INTERVAL, DEFAULT_HEARTBEAT_ERRORBOUND);
	}

	public HeartbeatThread(Client client, Object heartbeatPacket, int sendout, int bounds) throws IOException {
		// TODO Auto-generated constructor stub
		this.client = client;
		this.socket = client.getSocket();
		out = client.getObtoc();
		this.heartbeatPacket = heartbeatPacket;
		this.sendout = sendout;
		this.errorbound = bounds;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (!end) {

			while (!pause && !end) {
				try {
					Thread.sleep(sendout);// 必须放在可能抛出异常的send前面，否则send抛出异常无法休眠，会很快循环三次立即断开服务器连接
					if (pause) {
						break;
					}
					out.writeObject(heartbeatPacket);
					errortimes = 0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if (errortimes++ > errorbound) {
						JOptionPane.showMessageDialog(client.getMainui(), "Network connection interrupt!",
								client.getUsername(), ERROR_MESSAGE_JOPT);
						client.getMainui().dispose();
						this.setEnd(true);
						try {
							socket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						LoginDialog login = new LoginDialog(new Client());
						login.getUsernameTF().setText(client.getUsername());
						
						login.setVisible(true);
						return;
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (pause) {
				try {
					Thread.sleep(DEFAULT_SLEEP_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

}
