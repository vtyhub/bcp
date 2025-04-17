package interaction;

import java.math.BigInteger;
import java.util.List;

import client.Client;

public class GenPK {

	public static BigInteger genPK(BigInteger N, Client[] clients) {
		BigInteger product = BigInteger.ONE;
		for (int i = 0; i < clients.length; i++) {
			product = product.multiply(clients[i].getH());
		}
		return product.mod(N.multiply(N));
	}

	public static BigInteger genPK(BigInteger N, List<? extends Client> clientListC) {
		return genPK(N, clientListC.toArray(new Client[] {}));
	}
}
