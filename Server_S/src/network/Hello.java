package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import constant.NetConstant;

public class Hello implements NetConstant {

	public static void forwardlyAloha(ObjectInputStream in, ObjectOutputStream out)
			throws IOException, ClassNotFoundException, ErrorResponseException {
		out.writeObject(ALOHA);
		String ok = (String) in.readObject();
		if (!OK.equals(ok)) {
			throw new ErrorResponseException(ok);
		}
	}
 
	public static void passivelyOk(ObjectInputStream in, ObjectOutputStream out)
			throws ClassNotFoundException, IOException, ErrorResponseException {
		String aloha = (String) in.readObject();
		if (ALOHA.equals(aloha)) {
			out.writeObject(OK);
		} else {
			throw new ErrorResponseException(aloha);
		}
	}

}
