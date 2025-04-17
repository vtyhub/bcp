package constant.interaction;

public interface BCPOperConstant {
	
	// from server
	int BCP_PERMIT = 10;//ser to cli
	int BCP_DNEY = 11;//ser to cli

	int BCP_GETPP = 100;//client to server
	int BCP_GETPP_PERMIT = 101;//ser to cli
	int BCP_GETPP_DENY = 102;//ser to cli

	int BCP_GETPP_PERMIT_RIGHTUSERNAME = 1001;
	int BCP_GETPP_PERMIT_WRONGUSERNAME = 1002;

	int BCP_VALIDATEPP = 111;
}
