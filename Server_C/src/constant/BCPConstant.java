package constant;

import java.util.Arrays;
import java.util.HashSet;

public interface BCPConstant {
	
	String PP_EXTENSION = "pp";

	String N = "N";
	String k = "k";
	String g = "g";
	String[] PP_NAME = { N, k, g };
	HashSet<String> PP_NAME_SET = new HashSet<>(Arrays.asList(PP_NAME));
	
	String KEY_PAIR_EXTENSION = "keypair";
	String PUBLIC_KEY = "PublicKey";
	String SECRET_KEY = "SecretKey";
	String[] KEY_PAIR_NAME = { PUBLIC_KEY, SECRET_KEY };
	HashSet<String> KEY_PAIR_NAME_SET = new HashSet<>(Arrays.asList(KEY_PAIR_NAME));
}
