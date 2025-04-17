package interactconstant;

public interface InteractWithCConstant {

	int GETPP = 0;
	int GETPP_PERMIT = 00;
	int GETPP_DENY = 01;

	int KEYPROD = 1;
	int KEYPROD_PERMIT = 10;
	int KEYPROD_DENY = 11;

	int COMPUTE = 2;
	int COMPUTE_PERMIT = 20;
	int COMPUTE_DENY = 21;
	int COMPUTE_END = 22;

	int MULT = 3;
	int MULT_PERMIT = 30;
	int MULT_DENY = 31;
	int MULT_END = 32;

	int TRANSDEC = 4;
	int TRANSDEC_PERMIT = 40;
	int TRANSDEC_DENY = 41;
	int TRANSDEC_END = 42;

	int END = 9;

	// exception
	int BCP_NOTSET = 100000;
	int PP_NOTSET = 100001;
}
