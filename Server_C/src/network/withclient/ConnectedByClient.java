package network.withclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;

import client.OnlineClient;
import constant.CryptoConstant;
import constant.NetConstant;
import database.Login;
import database.Register;
import dbexception.NoSuchUserException;
import dbexception.UserAlreadyExistedException;
import dbexception.WrongPasswordException;
import heartbeat.HeartbeatDetection;
import method.CommonMethod;
import network.ErrorResponseException;
import network.Hello;
import server_c.ServerC;

/**
 * 在这里改为生产消费者队列
 */
public class ConnectedByClient implements Runnable, NetConstant, CryptoConstant {

	private ServerC c;
	// private ServerSocket clientServerSocket;
	private Socket singleClientSocket;
	private Connection conn;

	private InputStream fromClient;
	private OutputStream toClient;

	// 假定调用时已经监听过端口,并且有socket和 ServerSocket
	public ConnectedByClient(ServerC c, Socket singleClient) {
		// TODO Auto-generated constructor stub
		this.c = c;
		this.singleClientSocket = singleClient;
		// this.clientServerSocket = c.getClientServerSocket();
		this.conn = c.getDbConnection();

		try {
			fromClient = this.singleClientSocket.getInputStream();
			toClient = this.singleClientSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ObjectInputStream obFromClient = null;
		ObjectOutputStream obToClient = null;
		try {
			obFromClient = new ObjectInputStream(fromClient);
			obToClient = new ObjectOutputStream(toClient);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		try {
			// 无论什么情况，客户端和服务器连接都要先打招呼
			Hello.passivelyHi(obFromClient, obToClient);
			if (conn == null) {
				// 若数据库还未连接，则读取操作符并丢弃，并向该客户端发送数据库未连接异常
				obFromClient.readInt();
				// 测试的话因为客户端此时已经关闭socket，再调用输出操作会IO异常
				obToClient.writeInt(DB_UNCONNECTED);
				CommonMethod.batchClose(obToClient, obFromClient, singleClientSocket);// 先关闭输出流，保证消息被flush推送
				// 为什么只有字符串推送正常，而Int不被推送？难道是因为内容太小的缘故？
				// 抛出异常可能是因为客户端已经关闭socket，而服务器还在推送
				return;
			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (ErrorResponseException e) {
			// TODO Auto-generated catch block
			System.out.println("Client's hello is " + e.getMessage());// 多个线程同时监听该端口
			try {
				obToClient.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}

		// 进行到此处，数据库保证处于连接状态
		try {
			// 开始业务流程
			int operationType = obFromClient.readInt();
			switch (operationType) {
			case CLIENT_TEST:
				obToClient.writeInt(SUCCESSFULLY_TEST);// 先关闭的Input流，没有推送output！
				CommonMethod.batchClose(obToClient, obFromClient, singleClientSocket);
				return;// 仅仅是测试则可以停止
			case CLIENT_REGISTER:
				obToClient.writeInt(REGISTER_PERMITTED);
				obToClient.flush();
				String[] user = (String[]) obFromClient.readObject();// 假定user一共两个元素，分别是username和hashedpwd
				if (user == null || user.length != 2) {
					obToClient.writeInt(REGISTER_FAILED);
					CommonMethod.batchClose(obToClient, obFromClient, singleClientSocket);
					return;
				}

				String username = user[0], hashed1pwd = user[1];

				// 注册
				Register.checkDuplicate(conn, username);
				Register.register(conn, username, hashed1pwd);

				obToClient.writeInt(REGISTER_SUCCEEDED);
				CommonMethod.batchClose(obToClient, obFromClient, singleClientSocket);
				break;
			case CLIENT_LOGIN:
				obToClient.writeInt(LOGIN_PERMITTED);
				obToClient.flush();

				String[] userlogin = (String[]) obFromClient.readObject();
				if (userlogin == null || userlogin.length != 2) {
					obToClient.writeInt(LOGIN_FAILED);
					obToClient.close();
					return;
				}
				String loginusername = userlogin[0];

				// 没有抛出异常说明登陆验证成功
				Login.login(conn, loginusername, userlogin[1], DEFAULT_HASH_ALGORITHM, DEFAULT_CHARSET);

				// 写入user类的实例

				singleClientSocket.setKeepAlive(true);
				// 在线client实例
				OnlineClient clientForC = new OnlineClient(c, loginusername, singleClientSocket, obToClient,
						obFromClient);

				// 心跳检测线程
				HeartbeatDetection detection = new HeartbeatDetection(clientForC);
				clientForC.setDetectHeartBeat(detection);

				// 消息队列
				PutMessageTask putMessageTask = new PutMessageTask(obFromClient, clientForC.getSocketQueue(),
						detection);
				TakeMessageTask takeMessageTask = new TakeMessageTask(obFromClient, obToClient,
						clientForC.getSocketQueue(), c, clientForC);
				clientForC.setPutMessageTask(putMessageTask);
				clientForC.setTakeMessageTask(takeMessageTask);

				// 线程启动
				new Thread(putMessageTask).start();
				new Thread(takeMessageTask).start();
				new Thread(detection).start();

				c.addClient(loginusername, clientForC);

				obToClient.writeInt(LOGIN_SUCCEED);// 该socket不关闭
				obToClient.flush();
				break;
			default:
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				obToClient.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				obToClient.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				obToClient.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		} catch (UserAlreadyExistedException e) {
			// TODO Auto-generated catch block
			try {
				obToClient.writeInt(REGISTER_USEREXISTED);
				obToClient.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		} catch (NoSuchUserException e) {
			// TODO Auto-generated catch block
			try {
				obToClient.writeInt(LOGIN_NOSUCHUSER);
				obToClient.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		} catch (WrongPasswordException e) {
			// TODO Auto-generated catch block
			try {
				obToClient.writeInt(LOGIN_NOSUCHUSER);
				obToClient.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}

	}

}
