package genome;

import java.util.ArrayList;
import java.util.HashMap;

public class Encode implements EncodingConstant {

	private static HashMap<Character, String> encodingMap = new HashMap<>(4);

	static {
		encodingMap.put(A, ENCODING_A);
		encodingMap.put(T, ENCODING_T);
		encodingMap.put(C, ENCODING_C);
		encodingMap.put(G, ENCODING_G);
	}

	public static ArrayList<String> encodeToArrayList(String basepairs) throws WrongGenomeDataException {
		checkBasePair(basepairs);

		String upper = basepairs.toUpperCase();
		ArrayList<String> list = new ArrayList<>(upper.length());
		for (int i = 0; i < upper.length(); i++) {
			list.add(encodingMap.get(upper.charAt(i)));
		}
		return list;
	}

	public static String[] encodeToArray(String basepairs) throws WrongGenomeDataException {
		return encodeToArrayList(basepairs).toArray(new String[0]);
	}

	public static String encodeToStr(String basepairs) throws WrongGenomeDataException {
		ArrayList<String> list = encodeToArrayList(basepairs);
		String result = "";
		for (String s : list) {
			result += s;
		}
		return result;
	}

	public static void checkBasePair(String basepairs) throws WrongGenomeDataException {
		if (basepairs == null || !basepairs.matches(BASEPAIR_REGEX)) {
			throw new WrongGenomeDataException(basepairs);
		}
	}

	public static void checkEncodingBasePair(String encodingBasePairs) throws WrongGenomeDataException {
		if (encodingBasePairs != null && encodingBasePairs.length() / MANGIFICATION == 0
				&& encodingBasePairs.matches(ENCODING_BASEPAIR_REGEX)) {
			return;
		}
		throw new WrongGenomeDataException(encodingBasePairs);
	}

	public static void main(String[] args) throws WrongGenomeDataException {

		String s = "at";

		System.out.println(encodeToStr(s));
		System.out.println(encodeToArrayList(s));
		String[] array = encodeToArray(s);
		for (String s1 : array) {
			System.out.println(s1);

		}
	}

}
