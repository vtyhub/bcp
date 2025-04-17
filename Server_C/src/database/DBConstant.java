package database;

public interface DBConstant {

	// SQL keyword
	String SELECT = "select";
	String DISTINCT = "distinct";
	String FROM = "from";
	String WHERE = "where";

	String INSERT = "insert";
	String INTO = "into";
	String VALUES = "values";

	String UPDATE = "update";
	String SET = "set";
	String DELETE = "delete";

	String EXISTS = "exists";

	String AND = "and";
	String OR = "or";
	String NOT = "not";
	String IN = "in";
	String TRUE = "true";
	String FALSE = "false";
	String NULL = "null";

	// charset
	String DEFAULT_DB_CHARSET = "UTF-8";

	// hash algorithm
	String ALGORITHM = "SHA-256";

	// driver
	String MYSQL_DRIVER_NAME = "com.mysql.jdbc.Driver";
	String DATABASE_NAME = "serverc";
	String DEFAULT_URL_MYSQL = "jdbc:mysql://127.0.0.1:3306/" + DATABASE_NAME + "?useSSL=false&characterEncoding="
			+ DEFAULT_DB_CHARSET;

	// client table names
	String CLIENT_TABLE_NAME = "client";

	String CLIENT_COLUMN_NAME_USERNAME = "username";
	String CLIENT_COLUMN_NAME_HASHED2SALTPWD = "hashed2saltpwd";
	String CLIENT_COLUMN_NAME_SALT = "salt";
	String CLIENT_COLUMN_NAME_N = "N";
	String CLIENT_COLUMN_NAME_H = "h";
	String CLIENT_COLUMN_NAME_P = "PK";

	// String[] CLIENT_COLUMNS = { CLIENT_COLUMN_NAME_USERNAME,
	// CLIENT_COLUMN_NAME_HASHED2SALTPWD, CLIENT_COLUMN_NAME_SALT,
	// CLIENT_COLUMN_NAME_N, CLIENT_COLUMN_NAME_H, CLIENT_COLUMN_NAME_P };

	// invitation
	String INVITATION_TABLE_NAME = "invitation";

	String INVITATION_COLUMN_NAME_NUMBER = "number";
	String INVITATION_COLUMN_NAME_INVITER = "inviter";
	String INVITATION_COLUMN_NAME_LENGTH = "length";
	String INVITATION_COLUMN_NAME_INVITEES = "invitees";
	String INVITATION_COLUMN_NAME_SUBMISSIONTIME = "submissiontime";
	String INVITATION_COLUMN_NAME_PUTTIME = "puttime";
	String INVITATION_COLUMN_NAME_TAKETIME = "taketime";
	String INVITATION_COLUMN_NAME_KEYPRODTIME = "keyprodtime";
	String INVITATION_COLUMN_NAME_RESULTONPKTIME = "resultonpktime";
	String INVITATION_COLUMN_NAME_FINISHEDTIME = "finishedtime";

	// specific
	String SPECIFIC_TABLE_NAME = "invitee";

	String SPECIFIC_COLUMN_NAME_SEQUENCE = "sequence";
	String SPECIFIC_COLUMN_NAME_NUMBER = INVITATION_COLUMN_NAME_NUMBER;
	String SPECIFIC_COLUMN_NAME_INVITER = "inviter";
	String SPECIFIC_COLUMN_NAME_INVITERLENGTH = "inviterlen";
	String SPECIFIC_COLUMN_NAME_INVITEE = "invitee";
	String SPECIFIC_COLUMN_NAME_INVITEELENGTH = "inviteelen";

	// pp table
	String PP_TABLE_NAME = "pp";

	String PP_COLUMN_NAME_N = CLIENT_COLUMN_NAME_N;
	String PP_COLUMN_NAME_K = "k";
	String PP_COLUMN_NAME_G = "g";

	// String[] PP_COLUMNS = { PP_COLUMN_NAME_N, PP_COLUMN_NAME_K, PP_COLUMN_NAME_G
	// };

	// ciphertext table
	String ciphertext_TABLE_NAME = "ciphertext";

	String ciphertext_COLUMN_NAME_id = "sequence";
	String ciphertext_COLUMN_NAME_NUMBER = INVITATION_COLUMN_NAME_NUMBER;// 新增字段
	String ciphertext_COLUMN_NAME_ciphersequence = "ciphersequence";
	String ciphertext_COLUMN_NAME_username = CLIENT_COLUMN_NAME_USERNAME;
	String ciphertext_COLUMN_NAME_cipher_a = "cipher_a";
	String ciphertext_COLUMN_NAME_cipher_b = "cipher_b";
	String ciphertext_COLUMN_NAME_cipher_a_PK = "cipher_a_PK";
	String ciphertext_COLUMN_NAME_cipher_b_PK = "cipher_b_PK";

	// String[] ciphertext_COLUMNS = { ciphertext_COLUMN_NAME_id,
	// ciphertext_COLUMN_NAME_cipher_a,
	// ciphertext_COLUMN_NAME_cipher_b };

	// resultonpk table
	String resultonpk_TABLE_NAME = "resultonpk";

	String resultonpk_COLUMN_NAME_sequence = "sequence";
	String resultonpk_COLUMN_NAME_number = INVITATION_COLUMN_NAME_NUMBER;
	String resultonpk_COLUMN_NAME_usernameA = "usernamea";
	String resultonpk_COLUMN_NAME_usernameB = "usernameb";
	String resultonpk_COLUMN_NAME_addA = "adda";
	String resultonpk_COLUMN_NAME_addB = "addb";
	String resultonpk_COLUMN_NAME_multA = "multa";
	String resultonpk_COLUMN_NAME_multB = "multb";
	String resultonpk_COLUMN_NAME_PK = "PK";

	// String[] resultonpk_COLUMNS = { resultonpk_COLUMN_NAME_sequence,
	// resultonpk_COLUMN_NAME_usernameA,
	// resultonpk_COLUMN_NAME_usernameB, resultonpk_COLUMN_NAME_addA,
	// resultonpk_COLUMN_NAME_addB,
	// resultonpk_COLUMN_NAME_multA, resultonpk_COLUMN_NAME_multB,
	// resultonpk_COLUMN_NAME_PK };

	// onpkshuffle table
	// String onpkshuffle_TABLE_NAME = "onpkshuffle";
	//
	// String onpkshuffle_COLUMN_NAME_sequence = resultonpk_COLUMN_NAME_sequence;
	// String onpkshuffle_COLUMN_NAME_number = resultonpk_COLUMN_NAME_number;
	// String onpkshuffle_COLUMN_NAME_usernameA = resultonpk_COLUMN_NAME_usernameA;
	// String onpkshuffle_COLUMN_NAME_usernameB = resultonpk_COLUMN_NAME_usernameB;
	// String onpkshuffle_COLUMN_NAME_addA = resultonpk_COLUMN_NAME_addA;
	// String onpkshuffle_COLUMN_NAME_addB = resultonpk_COLUMN_NAME_addB;
	// String onpkshuffle_COLUMN_NAME_multA = resultonpk_COLUMN_NAME_multA;
	// String onpkshuffle_COLUMN_NAME_multB = resultonpk_COLUMN_NAME_multB;
	// String onpkshuffle_COLUMN_NAME_PK = resultonpk_COLUMN_NAME_PK;

	// result on h
	String RESULTONH_TABLE_NAME = "resultonh";

	String RESULTONH_COLUMN_NAME_SEQUENCE = resultonpk_COLUMN_NAME_sequence;
	String RESULTONH_COLUMN_NAME_NUMBER = INVITATION_COLUMN_NAME_NUMBER;
	String RESULTONH_COLUMN_NAME_USERNAMEA = resultonpk_COLUMN_NAME_usernameA;
	String RESULTONH_COLUMN_NAME_USERNAMEB = resultonpk_COLUMN_NAME_usernameB;
	String RESULTONH_COLUMN_NAME_ADDA = resultonpk_COLUMN_NAME_addA;
	String RESULTONH_COLUMN_NAME_ADDB = resultonpk_COLUMN_NAME_addB;
	String RESULTONH_COLUMN_NAME_MULTA = resultonpk_COLUMN_NAME_multA;
	String RESULTONH_COLUMN_NAME_MULTB = resultonpk_COLUMN_NAME_multB;

	// String[] RESULTONH_COLUMNS = { RESULTONH_COLUMN_NAME_SEQUENCE,
	// RESULTONH_COLUMN_NAME_USERNAMEA,
	// RESULTONH_COLUMN_NAME_USERNAMEB, RESULTONH_COLUMN_NAME_ADDA,
	// RESULTONH_COLUMN_NAME_ADDB,
	// RESULTONH_COLUMN_NAME_MULTA, RESULTONH_COLUMN_NAME_MULTB };//
	// 浅拷贝，开辟了新数组，里面的元素全都引用之前的数组元素所引用的实例

}
