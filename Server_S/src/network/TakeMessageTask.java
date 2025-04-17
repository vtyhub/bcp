package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

import constant.NetConstant;
import cryptography.BCP;
import cryptography.PP;
import cryptography.BCP.MK;
import interactconstant.InteractWithCConstant;
import server_s.ServerS;

public class TakeMessageTask implements Runnable, InteractWithCConstant, NetConstant {

	protected volatile boolean end = false;
	// protected volatile boolean pause = false;

	private ServerS S;
	private ObjectOutputStream obto;
	private BlockingQueue<Object> readQueue;

	public TakeMessageTask(ServerS S) {
		// TODO Auto-generated constructor stub
		this.S = S;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	// public boolean isPause() {
	// return pause;
	// }
	//
	// public void setPause(boolean pause) {
	// this.pause = pause;
	// }

	public BlockingQueue<Object> getBlockQueue() {
		return readQueue;
	}

	public void setBlockQueue(BlockingQueue<Object> blockQueue) {
		this.readQueue = blockQueue;
	}

	public ObjectOutputStream getObto() {
		return obto;
	}

	public void setObto(ObjectOutputStream obto) {
		this.obto = obto;
	}

	public TakeMessageTask(ServerS S, ObjectOutputStream obto, BlockingQueue<Object> queue) {
		// TODO Auto-generated constructor stub
		this.S = S;
		this.obto = obto;
		this.readQueue = queue;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!end) {

			try {
				Object oper = readQueue.take();

				if (end) {
					throw new IOException();
				}

				if (oper.equals(END)) {
					break;
				} else if (oper.equals(GETPP)) {
					BCP bcp = S.getBcp();
					if (bcp == null) {
						obto.writeObject(BCP_NOTSET);
						continue;
					}

					PP pp = bcp.getPP();
					if (pp == null) {
						obto.writeObject(PP_NOTSET);
						continue;
					}

					obto.writeObject(GETPP_PERMIT);
					obto.writeObject(pp);
				} else if (oper.equals(KEYPROD)) {
					S.getHeartbeatDetectionTask().setPause(true);
					BCP bcp = S.getBcp();
					if (bcp == null) {
						obto.writeObject(BCP_NOTSET);
						continue;
					}

					PP pp = bcp.getPP();
					if (pp == null) {
						obto.writeObject(PP_NOTSET);
						continue;
					}

					obto.writeObject(KEYPROD_PERMIT);

					Communicate.keyProd(readQueue, obto, S);
					S.getHeartbeatDetectionTask().setPause(false);
				} else if (oper.equals(COMPUTE)) {
					BCP bcp = S.getBcp();
					if (bcp == null) {
						obto.writeObject(BCP_NOTSET);
						continue;
					}

					PP pp = bcp.getPP();
					if (pp == null) {
						obto.writeObject(PP_NOTSET);
						continue;
					}

					obto.writeObject(COMPUTE_PERMIT);
					S.getHeartbeatDetectionTask().setPause(true);
				} else if (oper.equals(COMPUTE_END)) {
					S.getHeartbeatDetectionTask().setPause(false);
				} else if (oper.equals(MULT)) {
					S.getHeartbeatDetectionTask().setPause(true);
					BCP bcp = S.getBcp();
					if (bcp == null) {
						obto.writeObject(BCP_NOTSET);
						continue;
					}

					PP pp = bcp.getPP();
					if (pp == null) {
						obto.writeObject(PP_NOTSET);
						continue;
					}

					MK mk = bcp.getMK();
					BigInteger PK = S.getPK();
					if (mk == null || PK == null) {
						obto.writeObject(MULT_DENY);
						continue;
					}

					obto.writeObject(MULT_PERMIT);

					Communicate.mult(pp, mk, PK, readQueue, obto);
				} else if (oper.equals(MULT_END)) {
					S.getHeartbeatDetectionTask().setPause(false);
				} else if (oper.equals(TRANSDEC)) {
					S.getHeartbeatDetectionTask().setPause(true);
					BCP bcp = S.getBcp();
					if (bcp == null) {
						obto.writeObject(BCP_NOTSET);
						continue;
					}

					PP pp = bcp.getPP();
					if (pp == null) {
						obto.writeObject(PP_NOTSET);
						continue;
					}

					MK mk = bcp.getMK();
					if (mk == null) {
						obto.writeObject(TRANSDEC_DENY);
						continue;
					}

					obto.writeObject(TRANSDEC_PERMIT);
					Communicate.transDec(pp, mk, readQueue, obto);
				} else if (oper.equals(TRANSDEC_END)) {
					S.getHeartbeatDetectionTask().setPause(false);
				}

			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}

		}

		// 切断与C连接执行的代码
		S.offline();

	}
}
