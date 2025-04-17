package network.withs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import compute.PutTask;
import compute.TakeTask;
import constant.CommonClass.RemoteHostNetworkInfo;
import heartbeat.HeartbeatToS;
import network.ErrorResponseException;
import network.Hello;
import server_c.ServerC;
import view.ConnectWithSPanel;

public class PassivelyConnectedByS implements Runnable {

	private ServerC C;
	private Socket socketS;

	private ObjectInputStream obfroms;
	private ObjectOutputStream obtos;

	public PassivelyConnectedByS(ServerC c, ServerSocket serverSocketS, Socket socketS) {
		// TODO Auto-generated constructor stub
		C = c;
		this.socketS = socketS;

		try {
			obfroms = new ObjectInputStream(socketS.getInputStream());
			obtos = new ObjectOutputStream(socketS.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Hello.passivelyOk(obfroms, obtos);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				socketS.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				socketS.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		} catch (ErrorResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				socketS.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}


		if (C.getPassivelySocket() != null) {
			//只接收一个实例
			try {
				socketS.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		C.setPassivelySocket(socketS);
		C.setObFromS(obfroms);
		C.setObToS(obtos);
		C.setsInfo(new RemoteHostNetworkInfo(socketS));
		
		ConnectWithSPanel consPanel = C.getMainui().getMainTabbed().getNetworkTabbed().getConnectWithSPanel();
		consPanel.getConnStatusLabel().setText("Connected");
		
		HeartbeatToS heartbeatToS = new HeartbeatToS(C, socketS, obtos);
		C.setHeartbeatTask(heartbeatToS);
		new Thread(heartbeatToS).start();
		
		TakeTask takeComputation = new TakeTask(C, C.getComputationQueue());
		C.setTakeTask(takeComputation);
		new Thread(takeComputation).start();
		
		PutTask putTask = new PutTask(C, C.getComputationQueue());
		C.setPutTask(putTask);
		new Thread(putTask).start();
	}

}
