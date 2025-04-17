package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import constant.NetConstant;

public class Hello implements NetConstant {

	public static void forwardlyHello(ObjectInputStream in, ObjectOutputStream out)
			throws IOException, ClassNotFoundException, ErrorResponseException {
		out.writeObject(ClIENT_HELLO);
		String hi = (String) in.readObject();
		if (!SERVER_HI.equals(hi)) {
			throw new ErrorResponseException(hi);
		}
	}
}
