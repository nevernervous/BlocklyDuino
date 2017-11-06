package org.blockly.util;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coder {
	private static String getURLEncoder(String s, String enc) {
		String str = null;
		try {
			str = URLEncoder.encode(s, enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	private static String getURLDecoder(String s, String enc) {
		String str = null;
		try {
			str = URLDecoder.decode(s, enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String getChineseString(String s) {
		String result = s;
		String reg = "((_[A-F0-9][A-F0-9]){3})+";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(s);
		ArrayList<String> arrayList = new ArrayList();
		while (m.find()) {
			String tmp = m.group();

			arrayList.add(tmp);
		}

		Iterator<String> it = arrayList.iterator();
		while (it.hasNext()) {
			String tmp = (String) it.next();

			result = result.replaceFirst(tmp, getURLDecoder(tmp.replaceAll("_", "%"), "utf-8"));
		}
		return result;
	}

}