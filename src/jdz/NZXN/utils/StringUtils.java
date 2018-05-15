
package jdz.NZXN.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {
	public static String mergeList(List<String> list, String separator) {
		if (list.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder(list.size() * 32);
		for (String string : list)
			sb.append(string+separator);
		return sb.substring(0, sb.length()-separator.length()).toString();
	}
	
	public static List<String> parseList(String s, String separator) {
		if (s.equals(""))
			return new ArrayList<String>();
		List<String> retList = new ArrayList<String>(Arrays.asList(s.split(separator)));
		for (String str : retList)
			str = str.trim();
		return new ArrayList<String>(retList);
	}
}
