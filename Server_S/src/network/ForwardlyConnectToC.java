package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;

import constant.NetConstant;
import cryptography.BCP;
import cryptography.BCP.MK;
import cryptography.PP;
import heartbeat.HeartbeatDetection;
import interactconstant.InteractWithCConstant;
import server_s.ServerS;

public class ForwardlyConnectToC implements Runnable, InteractWithCConstant,NetConstant {

	// private volatile boolean end;
	private ServerS S;
	private Socket socket;
	private ObjectInputStream obfromc;
	private ObjectOutputStream obtoc;
	private HeartbeatDetection heartbeatDetection;
	private Object heartbeatPacket;

	private volatile boolean end = false;

	public ForwardlyConnectToC(ServerS s, Socket socket, ObjectInputStream obfromc, ObjectOutputStream obtoc,
			HeartbeatDetection heartbeatDetection) {
		// TODO Auto-generated constructor stub
		S = s;
		this.socket = socket;
		this.obfromc = obfromc;
		this.obtoc = obtoc;
		this.heartbeatDetection = heartbeatDetection;
//		heartbeatPacket = heartbeatDetection.getHeartbeatPacket();
		heartbeatPacket=DEFAULT_HEARTBEAT_PACKET;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (!end) {
			try {
				Object oper = obfromc.readObject();
				if (end) {
					// 长期阻塞之后都应该检查一下状态
					break;
				}
				heartbeatDetection.setOnline(true);
				if (oper.equals(END)) {
					break;
				} else if (oper.equals(heartbeatPacket)) {
					continue;
				} else if (oper.equals(GETPP)) {
					BCP bcp = S.getBcp();
					if (bcp == null) {
						obtoc.writeObject(BCP_NOTSET);
						continue;
					}

					PP pp = bcp.getPP();
					if (pp == null) {
						obtoc.writeObject(PP_NOTSET);
						continue;
					}

					obtoc.writeObject(GETPP_PERMIT);
					obtoc.writeObject(pp);
				} else if (oper.equals(KEYPROD)) {
					S.getHeartbeatDetectionTask().setPause(true);
					BCP bcp = S.getBcp();
					if (bcp == null) {
						obtoc.writeObject(BCP_NOTSET);
						continue;
					}

					PP pp = bcp.getPP();
					if (pp == null) {
						obtoc.writeObject(PP_NOTSET);
						continue;
					}

					obtoc.writeObject(KEYPROD_PERMIT);

//					Communicate.keyProd(obfromc, obtoc, S);
					S.getHeartbeatDetectionTask().setPause(false);
				} else if (oper.equals(COMPUTE)) {
					BCP bcp = S.getBcp();
					if (bcp == null) {
						obtoc.writeObject(BCP_NOTSET);
						continue;
					}

					PP pp = bcp.getPP();
					if (pp == null) {
						obtoc.writeObject(PP_NOTSET);
						continue;
					}

					obtoc.writeObject(COMPUTE_PERMIT);
					S.getHeartbeatDetectionTask().setPause(true);
				} else if (oper.equals(COMPUTE_END)) {
					S.getHeartbeatDetectionTask().setPause(false);
				} else if (oper.equals(MULT)) {
					S.getHeartbeatDetectionTask().setPause(true);
					BCP bcp = S.getBcp();
					if (bcp == null) {
						obtoc.writeObject(BCP_NOTSET);
						continue;
					}

					PP pp = bcp.getPP();
					if (pp == null) {
						obtoc.writeObject(PP_NOTSET);
						continue;
					}

					MK mk = bcp.getMK();
					BigInteger PK = S.getPK();
					if (mk == null || PK == null) {
						obtoc.writeObject(MULT_DENY);
						continue;
					}

					obtoc.writeObject(MULT_PERMIT);

//					Communicate.mult(pp, mk, PK, obfromc, obtoc);
				} else if (oper.equals(MULT_END)) {
					S.getHeartbeatDetectionTask().setPause(false);
				} else if (oper.equals(TRANSDEC)) {
					S.getHeartbeatDetectionTask().setPause(true);
					BCP bcp = S.getBcp();
					if (bcp == null) {
						obtoc.writeObject(BCP_NOTSET);
						continue;
					}

					PP pp = bcp.getPP();
					if (pp == null) {
						obtoc.writeObject(PP_NOTSET);
						continue;
					}

					MK mk = bcp.getMK();
					if (mk == null) {
						obtoc.writeObject(TRANSDEC_DENY);
						continue;
					}
					
					
					obtoc.writeObject(TRANSDEC_PERMIT);
//					Communicate.transDec(pp, mk, obfromc, obtoc);
				} else if (oper.equals(TRANSDEC_END)) {
					S.getHeartbeatDetectionTask().setPause(false);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}

		}

		System.out.println("s close socket");
		// 循环跳出视为关闭该连接
		heartbeatDetection.setEnd(true);
		S.setForwardlyCommunicateTask(null);
		S.setForwardlySocket(null);
		S.setForwardlyObFromC(null);
		S.setForwardlyObToC(null);
		S.setHeartbeatDetectionTask(null);
		try {
			socket.close();
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

}
