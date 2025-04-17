package method;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.JComponent;

import compute.Order;

public class CommonMethod {

	public static String byteToHexStr(byte[] b) // 二行制转字符串
	{
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

	public static void batchClose(Closeable... streams) throws IOException {
		for (Closeable stream : streams) {
			stream.close();
		}
	}

	public static void setComponentsEnable(JComponent... Components) {
		for (JComponent b : Components) {
			if (b != null) {
				b.setEnabled(true);
			}
		}
	}

	public static void setComponentsDisable(JComponent... Components) {
		for (JComponent b : Components) {
			if (b != null) {
				b.setEnabled(false);
			}
		}
	}

	// ---------------cluster------------------------------------
	// 根据N是否相同以及碱基对长度是否相同聚类，相同PP并且碱基长度相同的聚在一类,euqals新增密文长度必须相同的条件，就不必再增加参数
	public static <E> void cluster(ArrayList<ArrayList<E>> srclist, E element) {
		for (int i = 0; i <= srclist.size(); i++) {
			if (i == srclist.size()) {
				srclist.add(new ArrayList<E>());
			}
			ArrayList<E> list = srclist.get(i);
			if (list.isEmpty()) {
				list.add(element);
				return;
			}
			if (!element.equals(list.get(0))) {
				continue;
			} else {
				list.add(element);
				return;
			}
		}
	}

	public static <E> int maxIndex(ArrayList<ArrayList<E>> srclist) {
		int maxindex = 0;
		int peak = 0;
		for (int i = 0; i < srclist.size(); i++) {
			if (srclist.get(i).size() > peak) {
				peak = srclist.get(i).size();
				maxindex = i;
			}
		}
		return maxindex;
	}

	// -------------------permutation-and-combination--------------------------------------------

	// 从输入的二维数组中提取出所有不重复的元素
	public static <E> LinkedHashSet<E> extract(E[][] a) {
		if (a == null) {
			throw new IllegalArgumentException();
		}
		LinkedHashSet<E> result = new LinkedHashSet<>();
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				if (a[i][j] != null && !"".equals(a[i][j]))
					result.add(a[i][j]);
			}
		}
		return result;
	}

	/**
	 * @param a
	 * @return a的组合
	 */
	public static String[][] combine(String[] a) {
		int cl = (a.length * (a.length - 1)) / 2;
		int clcpy = cl;
		String x[][] = new String[cl][1];
		String tem[] = new String[2];

		for (int i = 0; i < a.length; i++)
			for (int j = a.length - 1; j > 0 + i - 1; j--) {
				if (a[j] != a[i]) {
					tem[1] = a[j];
					tem[0] = a[i];
					x[clcpy - 1] = tem.clone();
					clcpy--;
				}
			}
		return x;
	}

	/**
	 * 将inviter和每个invitee组合起来
	 * @param inviterName
	 * @param inviteeArray
	 * @return
	 */
	public static String[][] combine(String inviterName, String[] inviteeArray) {
		String[][] result = new String[inviteeArray.length][2];
		for (int i = 0; i < result.length; i++) {
			result[i][0] = inviterName;
			result[i][1] = inviteeArray[i];
		}
		return result;
	}

	public static String[][] permutate(String[] a, int take) {
		// 从a中取出take个元素排列
		if (a == null || a.length < take) {
			throw new IllegalArgumentException(take + ">" + a.length);
		}
		ArrayList<String> tmp = new ArrayList<String>();
		// 可能的情况数
		// result为Object数组，返回时会将其强制转换为实际类型，这是问题
		// T[][] result = (T[][]) new Object[factorial(a.length) / factorial(a.length -
		// take)][take];
		ArrayList<String[]> relist = new ArrayList<String[]>();
		return permutate(take, a, tmp, relist);
	}

	public static String[][] permutate(int k, String[] arr, ArrayList<String> tmpArr, ArrayList<String[]> relist) {
		if (k == 1) {
			for (int i = 0; i < arr.length; i++) {
				tmpArr.add(arr[i]);
				// 此时数组内就是完整结果
				relist.add(tmpArr.toArray(new String[0]));
				tmpArr.remove(arr[i]);
			}
		} else if (k > 1) {

			for (int i = 0; i < arr.length; i++) { // 按顺序挑选一个元素
				tmpArr.add(arr[i]); // 添加选到的元素
				permutate(k - 1, removeArrayElements(arr, tmpArr.toArray(new String[0])), tmpArr, relist); // 没有取过的元素，继续挑选
				tmpArr.remove(arr[i]);
			}
		}
		return relist.toArray(new String[0][]);
	}

	// 从数组中删除elements元素,不改变原数组
	public static String[] removeArrayElements(String[] arr, String... elements) {
		List<String> remainList = new ArrayList<>(arr.length);
		for (int i = 0; i < arr.length; i++) {
			boolean find = false;
			for (int j = 0; j < elements.length; j++) {
				if (arr[i].equals(elements[j])) {
					find = true;
					break;
				}
			}
			if (!find) { // 没有找到的元素保留下来
				remainList.add(arr[i]);
			}
		}
		String[] remainArray = new String[remainList.size()];
		for (int i = 0; i < remainList.size(); i++) {
			remainArray[i] = remainList.get(i);
		}
		return remainArray;
	}

	// 返回数组第一个非NULL元素索引
	public static <T> int notNull(T[][] a) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] == null) {
				return i;
			}
		}
		return -1;
	}

	// -----------------------------------------------------------------
	// 从src数组中，把dst中已经存在的内容完全相同的一维数组去掉，src应该是dst的超集，若dst是src的超集，则返回null？
	public static <E> ArrayList<E[]> removeSrc(E[][] src, E[][] dst) {
		if (src == null || dst == null) {
			throw new IllegalArgumentException();
		}

		ArrayList<E[]> result = new ArrayList<E[]>(src.length);
		e: for (int i = 0; i < src.length; i++) {
			E[] singlesrc = src[i];
			for (int j = 0; j < dst.length; j++) {
				E[] singledst = dst[j];
				if (singlesrc.length != singledst.length) {
					continue;
				}
				int count = 0;
				while (count < singlesrc.length) {
					if (!singlesrc[count].equals(singledst[count])) {
						// 若不相等，则不需要再比此列，继续比下一数组即可
						break;
					}
					// 对应相等，count++
					count++;
				}
				if (count >= singlesrc.length) {
					// 若正常比完了都没有break，则说明这个在里面有重复，从线性表删除该元素并continue e;
					continue e;
				}
			}
			// 正常执行到这里还没有触发过continue e，说明全都相等
			result.add(singlesrc);
		}
		if (result.size() == 0) {
			return null;
		}
		return result;
	}

	// -----------------------------------------------------------------
	public static int factorial(int n) {
		if (n == 0) {
			return 1;
		}
		return n * factorial(n - 1);
	}

	// -----------------------------------------------------------------
	public static String genJDBCIn(int mount) {
		String para = "(";
		for (int j = 0; j < mount; j++) {
			if (j != mount - 1) {
				para += "?,";
			} else {
				para += "?)";
			}
		}
		return para;
	}

	// ----------------------------------------------------------
	// public static String getTimeNow() {
	// String now = LocalDateTime.now().toString();
	// return now.substring(0, now.lastIndexOf("."));// 最后一个点之后的要删掉
	// }

	public static String getTimeNow() {
		return LocalDateTime.now().toString();// 最后一个点之后的要删掉
	}

	// ------------------------------------------------------------
	/**
	 * 若无，则返回null
	 * 
	 * @param orderArray
	 * @param username
	 * @return
	 */
	public static Order getOrderByUsername(Order[] orderArray, String username) {
		for (int i = 0; i < orderArray.length; i++) {
			if (username.equals(orderArray[i].getUsername())) {
				return orderArray[i];
			}
		}
		return null;
	}

	/**
	 * 打乱一个数组
	 * 
	 * @param array
	 */
	public static <T> void shuffle(T[] array) {
		for (int i = array.length - 1; i > 0; i--) {
			int j = (int) (Math.random() * (i + 1));
			T temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}

	public static void main(String[] args) {
		System.out.println(LocalDateTime.now());
		System.out.println(getTimeNow());
	}
}
