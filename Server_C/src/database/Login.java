package database;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbexception.WrongPasswordException;
import method.CommonMethod;
import dbexception.NoSuchUserException;

public class Login implements DBConstant {

	public static void login(Connection connection, String username, String hashedpwd, String algorithm, String charset)
			throws SQLException, NoSuchUserException, WrongPasswordException {
		PreparedStatement statement = connection.prepareStatement(SQLStatement.LOGIN_CLIENT_SQL);
		statement.setString(1, username);
		ResultSet result = statement.executeQuery();
		
		if (result.next()) {
			@SuppressWarnings("unused")
			String username1 = result.getString(CLIENT_COLUMN_NAME_USERNAME);
			String hashed2pwd = result.getString(CLIENT_COLUMN_NAME_HASHED2SALTPWD);
			String salt = result.getString(CLIENT_COLUMN_NAME_SALT);
//			System.out.println(username1);
//			System.out.println(hashed2pwd);
//			System.out.println(salt);
			try {
				MessageDigest digest = MessageDigest.getInstance(algorithm);
				byte[] hashed2 = digest.digest((hashedpwd + salt).getBytes(charset));
				String userhashed2pwd = CommonMethod.byteToHexStr(hashed2);
				if (!hashed2pwd.equals(userhashed2pwd)) {
					throw new WrongPasswordException("password:" + userhashed2pwd + " is wrong");
				}
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			throw new NoSuchUserException("user:" + username + " doesn't exist");			
		}
	}
	
	public static void main(String[] args) {
		
	}
}
