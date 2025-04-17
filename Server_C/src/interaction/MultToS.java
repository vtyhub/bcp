package interaction;

import java.math.BigInteger;

import cryptography.BCPForC;
 
public class MultToS {

	// 这里使用加法逆元加盲，假定传进来的参数都是加法逆元，和keyProd中有所不同
	public static BigInteger[] blind(BigInteger N, BigInteger g, BigInteger h, BigInteger blindinverse,
			BigInteger[] c) {
		
		BigInteger[] encblind = BCPForC.enc(N, g, h, blindinverse);
		BigInteger[] blindcipher = CInteract.add(N, c, encblind);
		BigInteger blind = CInteract.additiveInverse(blindinverse, N);//设定使用了盲的加法逆元来加盲，盲的加法逆元的加法逆元就是原盲
		BigInteger[] result = new BigInteger[] { blindcipher[0], blindcipher[1], blind, blindinverse };// 区别在这里，result[2]是加法逆元，result[3]是原盲
		return result;
	}

	// 这里的blind本身b1[2]是加法逆元，它的加法逆元b1[3]是原盲，和keyProd反了过来，记住
	public static BigInteger[][] multBlind(BigInteger N, BigInteger g, BigInteger h, BigInteger[] c1, BigInteger[] c2) {
		BigInteger[] blindinverse = new BigInteger[2];
		for (int i = 0; i < blindinverse.length; i++) {
			blindinverse[i] = CInteract.additiveInverse(KeyProdToS.generateBlind(N), N);
		}
		BigInteger[] b1 = blind(N, g, h, blindinverse[0], c1);
		BigInteger[] b2 = blind(N, g, h, blindinverse[1], c2);

		BigInteger[][] withoutdivide = { b1, b2 };
		return withoutdivide;
	}

	// 第一个三维数组是发送的A和B的盲化密文，第二个是A和B的盲和加法逆元
	public static BigInteger[][][] multDivideBlind(BigInteger[][] withoutdivide) {
		if (withoutdivide.length != 2 || withoutdivide[1].length != 4) {
			throw new RuntimeException("wrong length");
		}
		BigInteger[][][] divide = new BigInteger[2][2][2];// 2*4->2*2*2依然8个元素，换成二叉树一样的存储
		for (int i = 0; i < withoutdivide.length; i++) {
			System.arraycopy(withoutdivide[i], 0, divide[0][i], 0, divide[0][i].length);
			System.arraycopy(withoutdivide[i], divide[0][i].length, divide[1][i], 0, divide[1][i].length);
		}
		return divide;
	}

}
