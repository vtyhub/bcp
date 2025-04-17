package heartbeat;

import constant.NetConstant;
import constant.ThreadConstant;
import server_s.ServerS;

public class HeartbeatDetection implements Runnable, NetConstant, ThreadConstant {

	private volatile boolean online;
	private volatile boolean end = false;
	private volatile boolean pause = false;

	private ServerS S;

	private int sendingInterval;
	private long timeout;

	public HeartbeatDetection(ServerS S) {
		// TODO Auto-generated constructor stub
		this(S, true, DEFAULT_HEARTBEAT_TIMEOUT);
	}

	public HeartbeatDetection(ServerS S, boolean initialStatus, long timeout) {
		// TODO Auto-generated constructor stub
		this.S = S;
		this.online = initialStatus;
		this.timeout = timeout;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!end) {

			while (!pause && !end) {
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (end || pause) {
					break;// 若连接关闭，则不主动关闭socket
				}
				if (online) {
					online = false;
				} else {
					// 意味着客户端中断连接
					setEnd(true);
					S.offline();// 调用S的offline方法
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

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public int getSendingInterval() {
		return sendingInterval;
	}

	public void setSendingInterval(int sendingInterval) {
		this.sendingInterval = sendingInterval;
	}

}
