package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbexception.NoSuchUserException;

public class ModifyPassword implements DBConstant{

	public static void checkPwd(Connection conn, String username, String oldhashed1_pwd) throws SQLException, NoSuchUserException {
		PreparedStatement statement = conn.prepareStatement(SQLStatement.CHECK_CLIENT_USEREXISTS_SQL);
		statement.setString(1, username);
		ResultSet result = statement.executeQuery();
		if(result.next()) {
//			result.getString(USERNAME)
		}else {
			throw new NoSuchUserException("user:" + username + " doesn't exist");
		}
	}

	// 参数为客户端提交过的原密码哈希一次的哈希值,生成新的salt，加到这个值的末尾进行哈希并把结果转为字符串存入数据库
	public static void modifyPwd(Connection conn, String username, String newhashed1_pwd) {

	}

}
