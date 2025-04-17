package constant;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;

import cryptography.BCP;

public interface BCPConstant {

	public final static int DEFAULTKAPPA = 256;
	public final static String DEFAULTKAPPASTR = String.valueOf(BCP.DEFAULTKAPPA);
	public final static int DEFAULTCERTAINTY = 100;
	public final static String DEFAULTCERTAINTYSTR = String.valueOf(BCP.DEFAULTCERTAINTY);

	public final static String BCP_FILE_EXTENSION = "bcp";

	public final static String DEFAULTCHARSET = Charset.defaultCharset().toString();

	public final static String[] membersname = { "kappa", "certainty", "N", "k", "g", "mp", "mq", "p", "q" };

	public final static HashSet<String> membersnameset = new HashSet<>(Arrays.asList(membersname));

}
