package server_s;

import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedHashMap;

import cryptography.BCP;

public class ServerS extends AbstractServerS {

	// other method

	public void initializebcp(int kappa, int certainty) {
		bcp = new BCP(kappa, certainty);
	}

	public void initializebcp() {
		initializebcp(BCP.DEFAULTKAPPA, BCP.DEFAULTCERTAINTY);
	}

	public void startbcp(int kappa, int certainty, BigInteger N, BigInteger k, BigInteger g, BigInteger mp,
			BigInteger mq) {
		bcp = new BCP(kappa, certainty, N, k, g, mp, mq);
	}

	public boolean startbcp(LinkedHashMap<String, String> map) {
		if (map == null) {
			return false;
		}
		String[] ay = map.values().toArray(new String[] {});
		if (ay.length < 7) {
			return false;
		}
		try {
			startbcp(new Integer(ay[0]), Integer.valueOf(ay[1]), new BigInteger(ay[2]), new BigInteger(ay[3]),
					new BigInteger(ay[4]), new BigInteger(ay[5]), new BigInteger(ay[6]));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 关闭与C的连接需要执行的代码 避免空指针
	 */
	public void offline() {
		// 停止心跳
		if (heartbeatDetectionTask != null) {
			heartbeatDetectionTask.setEnd(true);
			setHeartbeatDetectionTask(null);
		}

		// 生产者
		if (putTask != null) {
			putTask.setEnd(true);
			putTask = null;
		}

		// 消费者
		if (takeTask != null) {
			takeTask.setEnd(true);
			takeTask = null;
		}

		if (forwardlySocket != null) {
			try {
				forwardlySocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setForwardlySocket(null);
		}

		setForwardlyObFromC(null);
		setForwardlyObToC(null);

		// 清空
		// setForwardlyCommunicateTask(null);
		setSocketQueue(null);
	}

	// -----------------------------------------------------------------------------------

	public ServerS() {

	}

	public boolean isBCPSet() {
		return bcp != null;
	}

	// 被动连接模式下，断开和C的连接，清除断开模式所用的一些成员域
	public static void passiveModeDisconnect() {

	}

	// 主动连接模式下，断开和C的连接，清除主动模式变量
	public static void forwardlyModeDisconnect() {

	}

	public static void main(String[] args) {
	}

	// --------------------------------------------------------------

}
