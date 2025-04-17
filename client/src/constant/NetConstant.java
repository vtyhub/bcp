package constant;

public interface NetConstant {
	// ------------With Client----------------------
	String ClIENT_HELLO = "Client hello";
	String SERVER_HI = "Server hello";

	// client to c
	int CLIENT_TEST = 0;
	int CLIENT_REGISTER = 1;
	int CLIENT_LOGIN = 2;

	// server to client
	int SUCCESSFULLY_TEST = 0;

	int REGISTER_PERMITTED = 100;
	int REGISTER_DENIED = 101;
	int REGISTER_SUCCEEDED = 102;
	int REGISTER_FAILED = 103;
	int REGISTER_USEREXISTED = 104;

	int LOGIN_PERMITTED = 200;
	int LOGIN_DENIED = 201;
	int LOGIN_SUCCEED = 202;
	int LOGIN_FAILED = 203;
	int LOGIN_NOSUCHUSER = 204;

	int DB_UNCONNECTED = 10001;

	// heart beat
	int DEFAULT_HEARTBEAT_PACKET = 8864;
	int DEFAULT_HEARTBEAT_ERRORBOUND = 3;
	int DEFAULT_HEARTBEAT_SENDING_INTERVAL = 1000;//应为 1000
	int DEFAULT_HEARTBEAT_TIMEOUT = 5000;// 单位ms 应为5000

	int DEFAULT_TIMEOUT = DEFAULT_HEARTBEAT_TIMEOUT;

	// -----------------With S-----------------------
	static final int MULT = 0;
	static final int END = 1;
	String ALOHA = "Bonjour";
	String OK = "Ok";
	String SERVER_NAME = "C";

	// -----------------Network--------------------
	int MAXIMUM_PORT = 65535;
	int MINIMUM_PORT = 0;

}
