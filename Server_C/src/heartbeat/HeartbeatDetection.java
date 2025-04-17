package heartbeat;

import java.io.IOException;

import client.OnlineClient;
import constant.NetConstant;
import constant.ThreadConstant;

public class HeartbeatDetection implements Runnable, NetConstant, ThreadConstant {

	private volatile boolean online;
	private volatile boolean end = false;
	private volatile boolean pause = false;

	private OnlineClient client;

	private int sendingInterval;
	private long timeout;

	public HeartbeatDetection(OnlineClient client) {
		// TODO Auto-generated constructor stub
		this(client, true, DEFAULT_HEARTBEAT_TIMEOUT);
	}

	public HeartbeatDetection(OnlineClient client, boolean initialStatus, long timeout) {
		// TODO Auto-generated constructor stub
		this.online = initialStatus;
		this.timeout = timeout;
		this.client = client;
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
					try {
						client.offline();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // 主动抛出异常，让底下异常处理部分被触发
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
