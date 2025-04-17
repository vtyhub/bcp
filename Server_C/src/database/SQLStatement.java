package database;

public interface SQLStatement extends DBConstant {

	// register SQL
	String CHECK_CLIENT_USERNAME_DUPLICATE_SQL = SELECT + " " + CLIENT_COLUMN_NAME_USERNAME + " " + FROM + " "
			+ CLIENT_TABLE_NAME + " " + WHERE + " " + CLIENT_COLUMN_NAME_USERNAME + "=?";
	String REGISTER_CLIENT_SQL = INSERT + " " + INTO + " " + CLIENT_TABLE_NAME + " (" + CLIENT_COLUMN_NAME_USERNAME
			+ "," + CLIENT_COLUMN_NAME_HASHED2SALTPWD + "," + CLIENT_COLUMN_NAME_SALT + ") " + VALUES + " (?,?,?)";

	// login SQL
	String LOGIN_CLIENT_SQL = SELECT + " " + CLIENT_COLUMN_NAME_USERNAME + "," + CLIENT_COLUMN_NAME_HASHED2SALTPWD + ","
			+ CLIENT_COLUMN_NAME_SALT + " " + FROM + " " + CLIENT_TABLE_NAME + " where " + CLIENT_COLUMN_NAME_USERNAME
			+ "=?";

	// modifyPWD SQL
	String CHECK_CLIENT_USEREXISTS_SQL = LOGIN_CLIENT_SQL;
	String UPDATE_client_PWD_SQL = UPDATE + " " + CLIENT_TABLE_NAME + " " + SET + " "
			+ CLIENT_COLUMN_NAME_HASHED2SALTPWD + "=?, " + CLIENT_COLUMN_NAME_SALT + "=? " + WHERE + " "
			+ CLIENT_COLUMN_NAME_USERNAME + "=?";

	// ----------------------------------------------------PP-------------------------------------------------------------
	// insert PP
	String INSERT_pp_SQL = INSERT + " " + INTO + " " + PP_TABLE_NAME + " (" + PP_COLUMN_NAME_N + "," + PP_COLUMN_NAME_K
			+ "," + PP_COLUMN_NAME_G + ") " + VALUES + " (?,?,?)";

	// select PP
	String SELECT_pp_NisEXISTED_SQL = SELECT + " " + PP_COLUMN_NAME_N + " " + FROM + " " + PP_TABLE_NAME + " " + WHERE
			+ " " + PP_COLUMN_NAME_N + "=?";
	String SELECT_PP_ALL_SQL = SELECT + " * " + FROM + " " + PP_TABLE_NAME + " " + WHERE + " " + PP_COLUMN_NAME_N
			+ "=?";

	// ----------------------------------------------------client-------------------------------------------------------------
	// update N,pk
	String UPDATE_client_N_SQL = UPDATE + " " + CLIENT_TABLE_NAME + " " + SET + " " + CLIENT_COLUMN_NAME_N + "=? "
			+ WHERE + " " + CLIENT_COLUMN_NAME_USERNAME + "=?";
	String UPDATE_CLIENT_h_SQL = UPDATE + " " + CLIENT_TABLE_NAME + " " + SET + " " + CLIENT_COLUMN_NAME_H + "=? "
			+ WHERE + " " + CLIENT_COLUMN_NAME_USERNAME + "=?";
	String UPDATE_CLIENT_PK_SQL = "update client set PK=? where username=?";

	// client get public parameters
	String SELECT_PP_CLIENT_PUBLICPARA_SQL = SELECT + " p." + PP_COLUMN_NAME_N + "," + PP_COLUMN_NAME_K + ","
			+ PP_COLUMN_NAME_G + " " + FROM + " " + PP_TABLE_NAME + " p," + CLIENT_TABLE_NAME + " c " + WHERE + " p."
			+ PP_COLUMN_NAME_N + "=c." + CLIENT_COLUMN_NAME_N + " " + AND + " " + CLIENT_COLUMN_NAME_USERNAME + "=?";

	// client get pk
	String SELECT_CLIENT_PK_SQL = SELECT + " " + CLIENT_COLUMN_NAME_H + " " + FROM + " " + CLIENT_TABLE_NAME + " "
			+ WHERE + " " + CLIENT_COLUMN_NAME_USERNAME + "=?";

	// client get client
	String SELECT_VALIDUSER_SQL = SELECT + " * " + FROM + " " + CLIENT_TABLE_NAME + " " + WHERE + " "
			+ CLIENT_COLUMN_NAME_USERNAME + " =?";

	// ----------------------------------------------------RESULT-ON-PK------------------------------------------------------------
	// insert result
	String INSERT_RESULTONPK = INSERT + " " + INTO + " " + resultonpk_TABLE_NAME + " (" + resultonpk_COLUMN_NAME_number
			+ "," + resultonpk_COLUMN_NAME_usernameA + "," + resultonpk_COLUMN_NAME_usernameB + ","
			+ resultonpk_COLUMN_NAME_addA + "," + resultonpk_COLUMN_NAME_addB + "," + resultonpk_COLUMN_NAME_multA + ","
			+ resultonpk_COLUMN_NAME_multB + "," + resultonpk_COLUMN_NAME_PK + ") " + VALUES + " (?,?,?,?,?,?,?,?)";

	// select result
	String SELECT_RESULTONPK = SELECT + " " + resultonpk_COLUMN_NAME_addA + "," + resultonpk_COLUMN_NAME_addB + ","
			+ resultonpk_COLUMN_NAME_multA + "," + resultonpk_COLUMN_NAME_multB + " " + FROM + " "
			+ resultonpk_TABLE_NAME + " " + WHERE + " (" + resultonpk_COLUMN_NAME_usernameA + "=? " + OR + " "
			+ resultonpk_COLUMN_NAME_usernameB + "=?) " + AND + " " + resultonpk_COLUMN_NAME_usernameA + "<>"
			+ resultonpk_COLUMN_NAME_usernameB;// 两个问号相同就是查一个用户和其他用户所有结果，不同查出的记录往往不是想要的，尽量避免

	String SELECT_RESULTONPK_OR = SELECT + " " + resultonpk_COLUMN_NAME_addA + "," + resultonpk_COLUMN_NAME_addB + ","
			+ resultonpk_COLUMN_NAME_multA + "," + resultonpk_COLUMN_NAME_multB + " " + FROM + " "
			+ resultonpk_TABLE_NAME + " " + WHERE + " ((" + resultonpk_COLUMN_NAME_usernameA + "=? " + AND + " "
			+ resultonpk_COLUMN_NAME_usernameB + "=?) " + OR + " (" + resultonpk_COLUMN_NAME_usernameB + "=? " + AND
			+ " " + resultonpk_COLUMN_NAME_usernameA + "=?)) " + AND + " " + resultonpk_COLUMN_NAME_usernameA + "<>"
			+ resultonpk_COLUMN_NAME_usernameB;

	String SELECT_RESULTONPK_ACCURATE = SELECT + " " + resultonpk_COLUMN_NAME_addA + "," + resultonpk_COLUMN_NAME_addB
			+ "," + resultonpk_COLUMN_NAME_multA + "," + resultonpk_COLUMN_NAME_multB + " " + FROM + " "
			+ resultonpk_TABLE_NAME + " " + WHERE + " " + resultonpk_COLUMN_NAME_usernameA + "=? " + AND + " "
			+ resultonpk_COLUMN_NAME_usernameB + "=? " + AND + " " + resultonpk_COLUMN_NAME_usernameA + "<>"
			+ resultonpk_COLUMN_NAME_usernameB;// 两个问号不能相同，精确查两个用户的计算

	// 查找指定一次计算的所有数据
	String SELECT_RESULTONPK_ALL_ACCURATE = SELECT + " * " + FROM + " " + resultonpk_TABLE_NAME + " " + WHERE + " "
			+ resultonpk_COLUMN_NAME_usernameA + "=? " + AND + " " + resultonpk_COLUMN_NAME_usernameB + "=? " + AND
			+ " " + resultonpk_COLUMN_NAME_usernameA + "<>" + resultonpk_COLUMN_NAME_usernameB;

	String DELETE_RESULT_ACCURATE = DELETE + " " + FROM + " " + resultonpk_TABLE_NAME + " " + WHERE + " "
			+ resultonpk_COLUMN_NAME_usernameA + "=? " + OR + " " + resultonpk_COLUMN_NAME_usernameB + "=?";// 把和该用户相关的记录全部删除，会影响其他用户

	// 未计算出结果的用户
	String SELECT_RESULTONPK_UNDECUSER = "SELECT DISTINCT pk.usernamea,pk.usernameb FROM resultonpk pk WHERE pk.usernamea<>pk.usernameb AND NOT (EXISTS (SELECT * FROM resultonh h WHERE pk.usernamea=h.usernamea AND pk.usernameb=h.usernameb) AND EXISTS (SELECT * FROM resultonh h2 WHERE (pk.usernamea=h2.usernameb AND pk.usernameb=h2.usernamea)))";

	String SELECT_RESULTONPK_UNDECUSERONGIVENPK = "SELECT DISTINCT pk.usernamea,pk.usernameb FROM resultonpk pk WHERE pk.usernamea<>pk.usernameb AND "
			+ resultonpk_COLUMN_NAME_PK
			+ "=? AND NOT (EXISTS (SELECT * FROM resultonh h WHERE pk.usernamea=h.usernamea AND pk.usernameb=h.usernameb) AND EXISTS (SELECT * FROM resultonh h2 WHERE (pk.usernamea=h2.usernameb AND pk.usernameb=h2.usernamea)))";

	//
	String SELECT_RESULTONPK_ALL = SELECT + " * " + FROM + " " + resultonpk_TABLE_NAME + " " + WHERE + " ("
			+ resultonpk_COLUMN_NAME_usernameA + "=? " + OR + " " + resultonpk_COLUMN_NAME_usernameB + "=?) " + AND
			+ " " + resultonpk_COLUMN_NAME_usernameA + "<>" + resultonpk_COLUMN_NAME_usernameB;

	// 查找所有PK
	String SELECT_RESULTONPK_ALLPK = SELECT + " " + DISTINCT + " " + resultonpk_COLUMN_NAME_PK + " " + FROM + " "
			+ resultonpk_TABLE_NAME;

	// 查找使用指定PK计算的用户二元组
	String SELECT_RESULTONPK_USERONPK = SELECT + " " + DISTINCT + " " + resultonpk_COLUMN_NAME_usernameA + ","
			+ resultonpk_COLUMN_NAME_usernameB + " " + FROM + " " + resultonpk_TABLE_NAME + " " + WHERE + " "
			+ resultonpk_COLUMN_NAME_PK + "=?";

	// 删除指定PK的全部记录
	String DELETE_RESULTONPK_DELETEBYPK = DELETE + " " + FROM + " " + resultonpk_TABLE_NAME + " " + WHERE + " "
			+ resultonpk_COLUMN_NAME_PK + "=?";
	// ------------------------------------------onpkshuffle----------------------------------------------------
	/*
	 * 仅仅更改onpk中的表面就可以使用
	 */
	// String INSERT_ONPKSHUFFLE = INSERT + " " + INTO + " " +
	// onpkshuffle_TABLE_NAME + " ("
	// + resultonpk_COLUMN_NAME_number + "," + resultonpk_COLUMN_NAME_usernameA +
	// ","
	// + resultonpk_COLUMN_NAME_usernameB + "," + resultonpk_COLUMN_NAME_addA + ","
	// + resultonpk_COLUMN_NAME_addB
	// + "," + resultonpk_COLUMN_NAME_multA + "," + resultonpk_COLUMN_NAME_multB +
	// "," + resultonpk_COLUMN_NAME_PK
	// + ") " + VALUES + " (?,?,?,?,?,?,?,?)";

	// -------------------------------------------RESULT-ON-H------------------------------------------------------------------------------------------
	String INSERT_RESULTONH = INSERT + " " + INTO + " " + RESULTONH_TABLE_NAME + " (" + RESULTONH_COLUMN_NAME_NUMBER
			+ "," + RESULTONH_COLUMN_NAME_USERNAMEA + "," + RESULTONH_COLUMN_NAME_USERNAMEB + ","
			+ RESULTONH_COLUMN_NAME_ADDA + "," + RESULTONH_COLUMN_NAME_ADDB + "," + RESULTONH_COLUMN_NAME_MULTA + ","
			+ RESULTONH_COLUMN_NAME_MULTB + ") " + VALUES + " (?,?,?,?,?,?,?)";

	String SELECT_RESULTONH_PEERUSER = SELECT + " " + DISTINCT + " " + RESULTONH_COLUMN_NAME_USERNAMEB + " " + FROM
			+ " " + RESULTONH_TABLE_NAME + " " + WHERE + " " + RESULTONH_COLUMN_NAME_USERNAMEA + "=? " + AND + " "
			+ RESULTONH_COLUMN_NAME_USERNAMEA + "<>" + RESULTONH_COLUMN_NAME_USERNAMEB;

	// select RESULTONH
	String SELECT_RESULTONH = SELECT + " " + RESULTONH_COLUMN_NAME_USERNAMEB + "," + RESULTONH_COLUMN_NAME_ADDA + ","
			+ RESULTONH_COLUMN_NAME_ADDB + "," + RESULTONH_COLUMN_NAME_MULTA + "," + RESULTONH_COLUMN_NAME_MULTB + " "
			+ FROM + " " + RESULTONH_TABLE_NAME + " " + WHERE + " " + RESULTONH_COLUMN_NAME_USERNAMEA + "=? " + AND
			+ " " + RESULTONH_COLUMN_NAME_USERNAMEA + "<>" + RESULTONH_COLUMN_NAME_USERNAMEB;// 两个问号相同就是查一个用户和其他用户所有结果，不同查出的记录往往不是想要的，尽量避免
	// 通过括号避免AB相同的情况
	String SELECT_RESULTONH_ACCURATE = SELECT + " " + RESULTONH_COLUMN_NAME_ADDA + "," + RESULTONH_COLUMN_NAME_ADDB
			+ "," + RESULTONH_COLUMN_NAME_MULTA + "," + RESULTONH_COLUMN_NAME_MULTB + " " + FROM + " "
			+ RESULTONH_TABLE_NAME + " " + WHERE + " " + RESULTONH_COLUMN_NAME_USERNAMEA + "=? " + AND + " "
			+ RESULTONH_COLUMN_NAME_USERNAMEB + "=? " + AND + " " + RESULTONH_COLUMN_NAME_USERNAMEA + "<>"
			+ RESULTONH_COLUMN_NAME_USERNAMEB;// 两个问号不能相同，精确查两个用户的计算

	String SELECT_RESULTONH_COMPUTEDUSER = SELECT + " " + DISTINCT + " " + RESULTONH_COLUMN_NAME_USERNAMEA + ","
			+ RESULTONH_COLUMN_NAME_USERNAMEB + " " + FROM + " " + RESULTONH_TABLE_NAME + " " + WHERE + " "
			+ RESULTONH_COLUMN_NAME_USERNAMEA + "<>" + RESULTONH_COLUMN_NAME_USERNAMEB;

	String DELETE_RESULTONH_ACCURATE = DELETE + " " + FROM + " " + RESULTONH_TABLE_NAME + " " + WHERE + " "
			+ RESULTONH_COLUMN_NAME_USERNAMEA + "=? " + OR + " " + RESULTONH_COLUMN_NAME_USERNAMEB + "=?";

	// ----------------------------------------------------ciphertext-------------------------------------------------------------

	// insert/update ciphertext
	String INSERT_ciphertext_cipherab_SQL = INSERT + " " + INTO + " " + ciphertext_TABLE_NAME + " ("
			+ ciphertext_COLUMN_NAME_NUMBER + "," + ciphertext_COLUMN_NAME_ciphersequence + ","
			+ ciphertext_COLUMN_NAME_username + "," + ciphertext_COLUMN_NAME_cipher_a + ","
			+ ciphertext_COLUMN_NAME_cipher_b + ") " + VALUES + " (?,?,?,?,?)";

	String UPDATE_ciphertext_cipherab_SQL = UPDATE + " " + ciphertext_TABLE_NAME + " " + SET + " "
			+ ciphertext_COLUMN_NAME_cipher_a + "=?," + ciphertext_COLUMN_NAME_cipher_b + "=? " + WHERE + " "
			+ ciphertext_COLUMN_NAME_username + "=?";

	// count
	String SELECT_CIPHERTEXT_COUNT_CIPHERONH = SELECT + " count(*) " + FROM + " " + ciphertext_TABLE_NAME + " " + WHERE
			+ " " + ciphertext_COLUMN_NAME_username + "=?";
	String SELECT_CIPHERTEXT_COUNT_CIPHERONPK = "SELECT COUNT(cipher_a_PK) FROM ciphertext WHERE cipher_a_PK IS NOT NULL AND cipher_b_PK IS NOT NULL AND username=?";

	String SELECT_CIPHERTEXT_UNPK = SELECT + " " + DISTINCT + " " + ciphertext_COLUMN_NAME_username + " " + FROM + " "
			+ ciphertext_TABLE_NAME + " " + WHERE + " " + ciphertext_COLUMN_NAME_cipher_a_PK + " is " + NULL + " " + OR
			+ " " + ciphertext_COLUMN_NAME_cipher_b_PK + " is " + NULL;

	String UPDATE_CIPHERTEXT_PK = UPDATE + " " + ciphertext_TABLE_NAME + " " + SET + " "
			+ ciphertext_COLUMN_NAME_cipher_a_PK + "=?," + ciphertext_COLUMN_NAME_cipher_b_PK + "=? " + WHERE + " "
			+ ciphertext_COLUMN_NAME_username + "=? " + AND + " " + ciphertext_COLUMN_NAME_ciphersequence + "=?";

	// select ciphertext,all,RESULTONH
	String SELECT_ciphertext_cipherab_SQL = SELECT + " " + ciphertext_COLUMN_NAME_cipher_a + ","
			+ ciphertext_COLUMN_NAME_cipher_b + " " + FROM + " " + ciphertext_TABLE_NAME + " " + WHERE + " "
			+ ciphertext_COLUMN_NAME_username + "=?";
	// 检测还没 计算出结果的，有有效PP和h,cipher的
	// 用户,不需要增加cipher不为null的条件，cipher非null，连接操作如果能成功cipher表中必然有记录
	// 连接操作自动保证了只能查出已经提交密文的用户
	String SELECT_ciphertext_client_validuser_SQL = SELECT + " distinct u." + CLIENT_COLUMN_NAME_USERNAME + " " + FROM
			+ " " + CLIENT_TABLE_NAME + " u," + ciphertext_TABLE_NAME + " c " + WHERE + " u."
			+ CLIENT_COLUMN_NAME_USERNAME + "=" + "c." + ciphertext_COLUMN_NAME_username + " " + AND + " u."
			+ CLIENT_COLUMN_NAME_N + " is " + NOT + " " + NULL + " " + AND + " u." + CLIENT_COLUMN_NAME_H + " is " + NOT
			+ " " + NULL + " " + AND + " " + NOT + " " + EXISTS + " (" + SELECT + " " + RESULTONH_COLUMN_NAME_ADDA + ","
			+ RESULTONH_COLUMN_NAME_ADDB + "," + RESULTONH_COLUMN_NAME_MULTA + "," + RESULTONH_COLUMN_NAME_MULTB + " "
			+ FROM + " " + RESULTONH_TABLE_NAME + " " + WHERE + " u." + CLIENT_COLUMN_NAME_USERNAME + "="
			+ RESULTONH_COLUMN_NAME_USERNAMEA + " " + OR + " u." + CLIENT_COLUMN_NAME_USERNAME + "="
			+ RESULTONH_COLUMN_NAME_USERNAMEB + ") " + AND + " " + NOT + " " + EXISTS + " (" + SELECT + " "
			+ resultonpk_COLUMN_NAME_addA + "," + resultonpk_COLUMN_NAME_addB + "," + resultonpk_COLUMN_NAME_multA + ","
			+ resultonpk_COLUMN_NAME_multB + " " + FROM + " " + resultonpk_TABLE_NAME + " " + WHERE + " u."
			+ CLIENT_COLUMN_NAME_USERNAME + "=" + resultonpk_COLUMN_NAME_usernameA + " " + OR + " u."
			+ CLIENT_COLUMN_NAME_USERNAME + "=" + resultonpk_COLUMN_NAME_usernameB + ")";// 没有检查字符串是否为空串或者有空格，这一点只能在insert的时候限制了

	String DELETE_CIPHERTEXT_USER = DELETE + " " + FROM + " " + ciphertext_TABLE_NAME + " " + WHERE + " "
			+ ciphertext_COLUMN_NAME_username + "=?";

	// ----------------------------------------------------invitation-------------------------------------------------------------
	String INSERT_RANDOM_COMPUTATION_SQL = INSERT + " " + INTO + " " + INVITATION_TABLE_NAME + " ("
			+ INVITATION_COLUMN_NAME_INVITER + "," + INVITATION_COLUMN_NAME_LENGTH + ","
			+ INVITATION_COLUMN_NAME_SUBMISSIONTIME + ") " + VALUES + " (?,?,?)";

	String INSERT_SPECIFIC_COMPUTATION_SQL = INSERT + " " + INTO + " " + INVITATION_TABLE_NAME + " ("
			+ INVITATION_COLUMN_NAME_INVITER + "," + INVITATION_COLUMN_NAME_LENGTH + ","
			+ INVITATION_COLUMN_NAME_INVITEES + "," + INVITATION_COLUMN_NAME_SUBMISSIONTIME + ") " + VALUES
			+ " (?,?,?,?)";

	// 查询用户是否已经提交,有结果集则提交
	String SELECT_IFSUBMIT_SQL = SELECT + " * " + FROM + " " + INVITATION_TABLE_NAME + " " + WHERE + " "
			+ INVITATION_COLUMN_NAME_INVITER + "=? ";

	// 查询指定用户计算是否开始，若开始则不能删除。若返回有结果集则已开始，若无结果集则未开始
	String SELECT_IFSTART_SQL = SELECT + " * " + FROM + " " + INVITATION_TABLE_NAME + " " + WHERE + " "
			+ INVITATION_COLUMN_NAME_INVITER + "=? " + AND + " " + INVITATION_COLUMN_NAME_PUTTIME + " is " + NOT + " "
			+ NULL;

	/**
	 * 通过number设置put后的时间
	 */
	String UPDATE_PUTTIME_BY_NUMBER_SQL = "UPDATE invitation SET puttime=? WHERE number=?";
	/**
	 * 通过用户名设置put时间
	 */
	String UPDATE_PUTTIME_BY_INVITER_SQL = "UPDATE " + INVITATION_TABLE_NAME + " SET " + INVITATION_COLUMN_NAME_PUTTIME
			+ "=? WHERE inviter=?";

	/**
	 * 通过number设置take后的时间
	 */
	String UPDATE_TAKETIME_BY_NUMBER_SQL = UPDATE + " " + INVITATION_TABLE_NAME + " " + SET + " "
			+ INVITATION_COLUMN_NAME_TAKETIME + "=? " + WHERE + " " + INVITATION_COLUMN_NAME_NUMBER + "=?";
	/**
	 * 通过inviter设置take后的时间
	 */
	String UPDATE_TAKETIME_BY_INVITER_SQL = UPDATE + " " + INVITATION_TABLE_NAME + " " + SET + " "
			+ INVITATION_COLUMN_NAME_TAKETIME + "=? " + WHERE + " " + INVITATION_COLUMN_NAME_INVITER + "=?";

	/**
	 * 通过number设置keyprod完成的时间
	 */
	String UPDATE_KEYPRODTIME_BY_NUMBER_SQL = UPDATE + " " + INVITATION_TABLE_NAME + " " + SET + " "
			+ INVITATION_COLUMN_NAME_KEYPRODTIME + "=? " + WHERE + " " + INVITATION_COLUMN_NAME_NUMBER + "=?";
	/**
	 * 通过inviter设置keyprod完成的时间
	 */
	String UPDATE_KEYPRODTIME_BY_INVITER_SQL = UPDATE + " " + INVITATION_TABLE_NAME + " " + SET + " "
			+ INVITATION_COLUMN_NAME_KEYPRODTIME + "=? " + WHERE + " " + INVITATION_COLUMN_NAME_INVITER + "=?";

	/**
	 * 设置完成resultonpk的时间
	 */
	String UPDATE_RESULTONPKTIME_NUMBER_SQL = UPDATE + " " + INVITATION_TABLE_NAME + " " + SET + " "
			+ INVITATION_COLUMN_NAME_RESULTONPKTIME + "=? WHERE " + INVITATION_COLUMN_NAME_NUMBER + "=?";
	/**
	 * 通过inviter设置完成onpk的时间
	 */
	String UPDATE_RESULTONPKTIME_INVITER_SQL = UPDATE + " " + INVITATION_TABLE_NAME + " " + SET + " "
			+ INVITATION_COLUMN_NAME_RESULTONPKTIME + "=? WHERE " + INVITATION_COLUMN_NAME_INVITER + "=?";

	// 设置结束时间
	String UPDATE_FINISH_BY_NUMBER_SQL = "UPDATE invitation SET finishedtime=? where number=?";
	String UPDATE_FINISH_BY_INVITER_SQL = "UPDATE " + INVITATION_TABLE_NAME + " SET "
			+ INVITATION_COLUMN_NAME_FINISHEDTIME + "=? " + WHERE + " " + INVITATION_COLUMN_NAME_INVITER + "=?";

	// 撤销请求需要删除computation以及ciphertext表中的该用户记录
	String DELETE_COMPUTATION_SQL = DELETE + " " + FROM + " " + INVITATION_TABLE_NAME + " " + WHERE + " "
			+ INVITATION_COLUMN_NAME_INVITER + "=?";

	String SELECT_INVITEE_LENGTH_SQL = SELECT + " " + INVITATION_COLUMN_NAME_LENGTH + " " + FROM + " "
			+ INVITATION_TABLE_NAME + " " + WHERE + " " + INVITATION_COLUMN_NAME_INVITER + "=?";

	/**
	 * 查询出所有invitee都没有null的订单，且是指定计算而不是随机计算，并且没有开始的订单
	 */
	String SELECT_READY_SQL = "SELECT DISTINCT number FROM invitation WHERE number NOT IN (SELECT DISTINCT number FROM invitee WHERE inviteelen IS NULL) and invitees is not null and puttime is null";

	/**
	 * 对于每一个Number，计算该邀请号的邀请可计算者
	 */
	String SELECT_READY_INVITEE_SQL = "SELECT DISTINCT invitee FROM invitee WHERE number=? AND inviteelen IS NOT NULL AND inviterlen=inviteelen";

	// 根据number查inviter
	String SELECT_INVITER_BY_NUMBER_SQL = SELECT + " " + INVITATION_COLUMN_NAME_INVITER + " " + FROM + " "
			+ INVITATION_TABLE_NAME + " " + WHERE + " " + INVITATION_COLUMN_NAME_NUMBER + "=?";

	/**
	 * 根据inviter查number
	 */
	String SELECT_NUMBER_BY_INVITER_SQL = SELECT + " " + DISTINCT + " " + INVITATION_COLUMN_NAME_NUMBER + " " + FROM
			+ " " + INVITATION_TABLE_NAME + " " + WHERE + " " + INVITATION_COLUMN_NAME_INVITER + "=?";

	// 查询该用户发出的请求
	String SELECT_YOUR_REQUEST_SQL = "SELECT * FROM invitation WHERE inviter=?";

	// 占位符使用方式setString(1,"% username %");
	String SELECT_INVITATIONS_TOYOU_SQL = "SELECT * FROM invitation WHERE invitees LIKE ?";

	// 若有结果说明spec，若无说明rand
	String SELECT_COMPUTATION_TYPE_SQL = SELECT + " * " + FROM + " " + INVITATION_TABLE_NAME + " " + WHERE + " "
			+ INVITATION_COLUMN_NAME_INVITEES + " is not " + NULL + " " + AND + " " + INVITATION_COLUMN_NAME_NUMBER
			+ "=?";

	// ------------------------------------------invitee-------------------------------------------------
	String INSERT_SPECIFIC_SQL = INSERT + " " + INTO + " " + SPECIFIC_TABLE_NAME + " (" + INVITATION_COLUMN_NAME_NUMBER
			+ "," + SPECIFIC_COLUMN_NAME_INVITER + "," + SPECIFIC_COLUMN_NAME_INVITERLENGTH + ","
			+ SPECIFIC_COLUMN_NAME_INVITEE + ") " + VALUES + " (?,?,?,?)";

	// 以invitee的身份更新所有和inviter长度不同的
	String UPDATE_INVITEE_LENGTH_SQL = UPDATE + " " + SPECIFIC_TABLE_NAME + " " + SET + " "
			+ SPECIFIC_COLUMN_NAME_INVITEELENGTH + "=? " + WHERE + " " + SPECIFIC_COLUMN_NAME_INVITEE + "=?";

	String UPDATE_INVITER_INVITEE_LENGTH_SQL = UPDATE + " " + SPECIFIC_TABLE_NAME + " " + SET + " "
			+ SPECIFIC_COLUMN_NAME_INVITEELENGTH + "=? " + WHERE + " " + SPECIFIC_COLUMN_NAME_INVITEE + "=? " + AND
			+ " " + SPECIFIC_COLUMN_NAME_INVITER + "=?";// 更新提交者记录时用这个提高效率

	// 通过number号查询
	String SELECT_INVITEES_BY_NUMBER_SQL = SELECT + " " + SPECIFIC_COLUMN_NAME_INVITEE + ","
			+ SPECIFIC_COLUMN_NAME_INVITEELENGTH + " " + FROM + " " + SPECIFIC_TABLE_NAME + " " + WHERE + " "
			+ SPECIFIC_COLUMN_NAME_NUMBER + "=?";

}
