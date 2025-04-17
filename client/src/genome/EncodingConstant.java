package genome;

public interface EncodingConstant {

	char A = 'A';
	char T = 'T';
	char G = 'G';
	char C = 'C';

	String ENCODING_A = "00";
	String ENCODING_T = "01";
	String ENCODING_G = "10";
	String ENCODING_C = "11";

	int MANGIFICATION = ENCODING_A.length();

	String BASEPAIR_REGEX = "^[ATCGatcg]+$";
	String ENCODING_BASEPAIR_REGEX = "^[0-1]+$";

}
