package heartbeat;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import constant.NetConstant;
import constant.ThreadConstant;
import constant.ViewConstant;
import server_c.ServerC;
import view.ConnectWithSPanel;

public class HeartbeatToS implements Runnable, NetConstant, ViewConstant, ThreadConstant {

	private ServerC C;
	private Socket socket;
	private ObjectOutputStream obtos;
	private volatile boolean pause = false;
	private volatile boolean end = false;

	private Object heartbeatPacket;
	private int sendingInterval;
	private int errorBound;

	private int errorTime = 0;

	public HeartbeatToS(ServerC c, Socket socket, ObjectOutputStream obtoc, Object heartbeatPacket, int sendingInterval,
			int errorBound) {
		// TODO Auto-generated constructor stub
		C = c;
		this.socket = socket;
		this.obtos = obtoc;

		this.heartbeatPacket = heartbeatPacket;
		this.sendingInterval = sendingInterval;
		this.errorBound = errorBound;
	}

	public HeartbeatToS(ServerC c, Socket socket, ObjectOutputStream obtoc) {
		// TODO Auto-generated constructor stub
		this(c, socket, obtoc, DEFAULT_HEARTBEAT_PACKET, DEFAULT_HEARTBEAT_SENDING_INTERVAL,
				DEFAULT_HEARTBEAT_ERRORBOUND);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!end) {
			while (!pause && !end) {

				try {
					Thread.sleep(sendingInterval);// 必须放在可能抛出异常的send前面，否则send抛出异常无法休眠，会很快循环三次立即断开服务器连接
					if (pause) {
						break;
					}
					obtos.writeObject(heartbeatPacket);
					errorTime = 0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if (errorTime++ > errorBound) {
						JOptionPane.showMessageDialog(null, "Connection between C and S has been interrupted!",
								ERROR_TITLE, ERROR_MESSAGE_JOPT);
						this.setEnd(true);
						C.setsInfo(null);
						C.setObFromS(null);
						C.setObToS(null);
						C.setPassivelySocket(null);
						C.setHeartbeatTask(null);
						ConnectWithSPanel consPanel = C.getMainui().getMainTabbed().getNetworkTabbed()
								.getConnectWithSPanel();
						consPanel.getConnStatusLabel().setText("Disconnected");
						//7.24新增，put和take任务结束
						C.setComputationQueue(null);
						C.getTakeTask().setEnd(true);
						C.setTakeTask(null);
						C.getPutTask().setEnd(true);
						C.setPutTask(null);
						try {
							socket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
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

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

}
