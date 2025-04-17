package genome;

import java.util.HashMap;

public final class TruthTable {

	private final static HashMap<String, Boolean> map = new HashMap<>();

	static {
		map.put("00", true);
		map.put("101", true);
		map.put("100100", true);
		map.put("1101001", true);
	}

	public final static boolean getTruth(String PT) {
		return map.get(PT) == null ? false : true;
	}

}
