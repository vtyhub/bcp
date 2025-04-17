package operatestring;

import java.util.ArrayList;

public class EstimateString {
	

	/**
	 * 查看src中有多少个loopstring中的子串，去除重复子串
	 * @param src
	 * @param loopstring
	 * @return
	 */
	public static ArrayList<String> getSubStringsRemoval(String src, String loopstring) {
		ArrayList<String> list = new ArrayList<String>();
		for (int l = 1; l <= src.length(); l++) {
			for (int i = 0; i < src.length() + 1 - l; i++) {
				String sub = src.substring(i, i + l);
				if (loopstring.contains(sub) && !list.contains(sub)) {
					list.add(sub);
				}
			}
		}
		return list;
	}

	/**
	 * 查看src中有多少个loopstring中的子串
	 * @param src
	 * @param loopstring
	 * @return
	 */
	public static ArrayList<String> getSubStrings(String src, String loopstring) {
		ArrayList<String> list = new ArrayList<String>();
		for (int l = 1; l <= src.length(); l++) {
			for (int i = 0; i < src.length() + 1 - l; i++) {
				System.out.println("l =" + l + "  i =" + i);
				String sub = src.substring(i, i + l);
				if (loopstring.contains(sub)) {
					list.add(sub);
				}
			}
		}
		return list;
	}
}
