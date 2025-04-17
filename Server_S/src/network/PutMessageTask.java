package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

import constant.NetConstant;
import constant.ThreadConstant;
import heartbeat.HeartbeatDetection;

/**
 * 完成过滤心跳包，心跳检测，并仅在这个线程里更新online，take中不再更新，有效降低耦合度
 * 
 * @author Jotaro
 *
 */
public class PutMessageTask implements Runnable, ThreadConstant, NetConstant {

	protected volatile boolean end = false;
	protected volatile boolean pause = false;

	private BlockingQueue<Object> blockQueue;
	private ObjectInputStream obfrom;

	private Object heartbeatPacket;
	private HeartbeatDetection hbd;

	public PutMessageTask(ObjectInputStream obfrom, BlockingQueue<Object> queue,
			HeartbeatDetection heartbeatDetection) {
		this(obfrom, queue, DEFAULT_HEARTBEAT_PACKET, heartbeatDetection);
	}

	public PutMessageTask(ObjectInputStream obfrom, BlockingQueue<Object> queue, Object heartbeat,
			HeartbeatDetection heartbeatDetection) {
		// TODO Auto-generated constructor stub
		this.obfrom = obfrom;
		this.blockQueue = queue;
		this.heartbeatPacket = heartbeat;
		this.hbd = heartbeatDetection;
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

	public ObjectInputStream getObfrom() {
		return obfrom;
	}

	public void setObfrom(ObjectInputStream obfrom) {
		this.obfrom = obfrom;
	}

	public BlockingQueue<Object> getBlockQueue() {
		return blockQueue;
	}

	public void setBlockQueue(BlockingQueue<Object> blockQueue) {
		this.blockQueue = blockQueue;
	}

	@Override
	public void run() {

		while (!end) {
			while (!end && !pause) {
				try {
					Object message = obfrom.readObject();
					hbd.setOnline(true);
					if (message.equals(heartbeatPacket)) {
						continue;
					}
					blockQueue.put(message);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					setEnd(true);
					e.printStackTrace();
					break;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					setEnd(true);
					e.printStackTrace();
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					setEnd(true);
					e.printStackTrace();
					break;
				}
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
