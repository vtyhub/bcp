package database;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import constant.SysConstant;
import dbexception.UserAlreadyExistedException;
import method.CommonMethod;

public class Register implements DBConstant, SysConstant {

	public static void checkDuplicate(Connection connection, String username)
			throws SQLException, UserAlreadyExistedException {
		PreparedStatement statement = connection.prepareStatement(SQLStatement.CHECK_CLIENT_USERNAME_DUPLICATE_SQL);
		statement.setString(1, username);
		ResultSet result = statement.executeQuery();
		if (result.next())
			throw new UserAlreadyExistedException(CLIENT_COLUMN_NAME_USERNAME + ":" + username + " already existed");
	}

	// 
	public static void register(Connection connection, String username, String password) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(SQLStatement.REGISTER_CLIENT_SQL);
		statement.setString(1, username);

		int salt = new Random().nextInt(Integer.MAX_VALUE);
		String stringSalt = String.valueOf(salt);
		try {
			MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
			byte[] hashBytes = digest.digest((password + stringSalt).getBytes(DEFAULT_DB_CHARSET));
			String hash = CommonMethod.byteToHexStr(hashBytes);
			statement.setString(2, hash);
			statement.setString(3, stringSalt);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		statement.executeUpdate();
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		MessageDigest i = MessageDigest.getInstance("SHA-256");

		// int salt = new Random().nextInt(Integer.MAX_VALUE);
		// System.out.println(salt);

		byte[] digest = i.digest("123123".getBytes());
		String s = CommonMethod.byteToHexStr(digest);
		System.out.println(s);
	}
}
