package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Log {
	private ArrayList<String> log = new ArrayList<String>();// 组合的方式创建log
	private ArrayList<String> time = new ArrayList<String>();// 组合的方式创建log

	public boolean writeToLog(String s) {
		return (log.add(s) && time.add(LocalDateTime.now().toString()));
	}

	public String getLog(int i) {
		return log.get(i);
	}

	public String getTime(int i) {
		return time.get(i);
	}

	public void clear() {
		log.clear();
		time.clear();
	}

	public int size() {
		return log.size();
	}

	public String[] getAllLog() {
		String[] result = new String[log.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (i + 1) + " " + log.get(i);
		}
		return result;
	}

	public String[] getAllTime() {
		String[] result = new String[time.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (i + 1) + " " + time.get(i);
		}
		return result;
	}

	public String[] getAll() {
		String[] result = new String[log.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (i + 1) + " Content:" + log.get(i) + " Time:" + time.get(i);
		}
		return result;
	}

}
