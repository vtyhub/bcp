package operatestring;

import java.util.ArrayList;
import java.util.ListIterator;

public interface GeneralOperation {
	public static final String DIGIT = "0123456789";

	public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	public static final String DIGITLOWERCASE = DIGIT + LOWERCASE;
	public static final String LOWERCASEDIGIT = LOWERCASE + DIGIT;

	public static final String UPPERCASE = LOWERCASE.toUpperCase();
	public static final String DIGITUPPERCAST = DIGIT + UPPERCASE;
	public static final String UPPERCASTDIGIT = UPPERCASE + DIGIT;

	public static final String LOWERUPPERCASE = LOWERCASE + UPPERCASE;
	public static final String UPPERLOWERCASE = UPPERCASE + LOWERCASE;

	/**
	 *  二行制转字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToHexStr(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			// if (n < b.length - 1)
			// hs = hs + ":";
		}
		return hs.toUpperCase();
	}

	/**
	 * 检测字符串是否为""或" "
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isFullEmpty(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) != ' ') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 字符串去重
	 * 
	 * @param mould
	 * @return
	 */
	public static String duplicateRemoval(String mould) {
		ArrayList<Character> list = new ArrayList<>();
		for (int i = 0; i < mould.length(); i++) {
			if (!list.contains(mould.charAt(i))) {
				list.add(mould.charAt(i));
			}
		}
		StringBuilder builder = new StringBuilder(list.size());
		ListIterator<Character> listIterator = list.listIterator();
		while (listIterator.hasNext()) {
			builder.append(listIterator.next().charValue());
		}
		return builder.toString();
	}

	/**
	 * 字符串循环左移
	 * 
	 * @param mould
	 * @param n
	 * @return
	 */
	public static String ringShiftLeft(String mould, int n) {
		int m = Math.abs(n % mould.length());
		String left = mould.substring(0, m), right = mould.substring(m, mould.length());
		return right + left;
	}

	/**
	 * 字符串循环右移
	 * 
	 * @param mould
	 * @param n
	 * @return
	 */
	public static String ringShiftRight(String mould, int n) {
		int len = mould.length(), m = Math.abs(n % len);
		String left = mould.substring(0, len - m), right = mould.substring(len - m, len);
		return right + left;
	}

	/**
	 * 打乱一个字符串 char[]和Character[]不互通，需要循环两次来改变
	 * 
	 * @param s
	 * @return
	 */
	public static String shuffle(String s) {
		char[] charArray = s.toCharArray();
		Character[] array = new Character[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			array[i] = Character.valueOf(charArray[i]);
		}
		shufflea(array);
		for (int i = 0; i < charArray.length; i++) {
			charArray[i] = array[i].charValue();
		}
		return new String(charArray);
	}

	/**
	 * 打乱一个数组
	 * 
	 * @param array
	 */
	public static <T> void shufflea(T[] array) {
		for (int i = array.length - 1; i > 0; i--) {
			int j = (int) (Math.random() * (i + 1));
			T temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}
	
	public static void main(String[] args) {
	}
}
