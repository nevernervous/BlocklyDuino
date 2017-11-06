package org.blockly.util;

import java.util.ResourceBundle;

public class LanguageProvider {
	private static String cofig = "config.properties";
	private static ResourceBundle bundle;

	public static String getLocalString(String name) {
		String lang = MyMethod.getProp(cofig, "language");
		bundle = ResourceBundle.getBundle(lang);
		return bundle.getString(name);
	}
}