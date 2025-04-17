package interaction;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import client.Client;
import cryptography.BCPForC;
import cryptography.PP;

public class KeyProdToS {

	static BigInteger generateBlind(BigInteger N) {// multToS会调用该方法
		BigInteger blind;
		do {
			blind = new BigInteger(N.bitLength(), new Random());
		} while (blind.compareTo(BigInteger.ZERO) < 0 || blind.compareTo(N) >= 0);
		return blind;
	}

	public static BigInteger[] blind(BigInteger N, BigInteger g, BigInteger h, BigInteger blindness, BigInteger[] c) {
		BigInteger[] encblind = BCPForC.enc(N, g, h, blindness);
		BigInteger[] blindcipher = CInteract.add(N, c, encblind);
		BigInteger[] result = new BigInteger[] { blindcipher[0], blindcipher[1], blindness,
				CInteract.additiveInverse(blindness, N) };
		return result;
	}

	public static BigInteger[] blind(BigInteger N, BigInteger g, BigInteger h, BigInteger[] c) {
		return blind(N, g, h, generateBlind(N), c);
	}

	public static BigInteger[] blind(PP pp, BigInteger h, BigInteger[] c) {
		return blind(pp.getN(), pp.getG(), h, c);
	}

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
