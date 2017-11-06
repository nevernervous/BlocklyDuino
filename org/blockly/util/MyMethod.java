package org.blockly.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyMethod {
	public static String cofig = "config.properties";
	public static Hashtable<String, String> boardName_Path = new Hashtable<String, String>();

	public static String getProp(String filePath, String key) {
		Properties props = new Properties();
		try {
			
			InputStream in = ClassLoader.getSystemResourceAsStream(filePath);
			props.load(in);
			in.close();
			return props.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static synchronized void changeCurrentBoard(String filePath, String key, String value) {
		Properties prop = new Properties();
		try {
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
			InputStream fis = new FileInputStream(file);
			prop.load(fis);
			fis.close();
			prop.setProperty(key, value);
			java.io.OutputStream fos = new FileOutputStream(filePath);
			prop.store(fos, "Update '" + key + "' value");
			fos.close();
		} catch (Exception localException) {
		}
	}

	public static void getKeysAndValues(String filePath) {
		OrderedProperties properties = new OrderedProperties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			properties.load(in);
			Iterator<Object> it = properties.keySet().iterator();
			while (it.hasNext()) {
				Object entry = it.next();
				String key = (String) entry;
				if (key.endsWith(".name")) {
					System.out.println(key + "=" + properties.getProperty(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getBoards(String filePath) {
		ArrayList<String> list = new ArrayList<String>();
		OrderedProperties properties = new OrderedProperties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			properties.load(in);
			Iterator<Object> it = properties.keySet().iterator();
			while (it.hasNext()) {
				Object entry = it.next();
				String key = (String) entry;
				if (key.endsWith(".name")) {
					String value = properties.getProperty(key);
					ArrayList<String> cpu = getCpuByBoard(filePath, value);

					if ((cpu != null) && (cpu.size() > 0)) {
						for (int i = 0; i < cpu.size(); i++) {
							list.add(value + "[" + (String) cpu.get(i) + "]");

							boardName_Path.put(value + "[" + (String) cpu.get(i) + "]", filePath);
						}
					} else {
						list.add(value);

						boardName_Path.put(value, filePath);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String getCompileKeyByBoards(String filePath, String board) {
		String re = "";
		OrderedProperties properties = new OrderedProperties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			properties.load(in);
			Iterator<Object> it = properties.keySet().iterator();
			while (it.hasNext()) {
				Object entry = it.next();
				String key = (String) entry;
				if (key.endsWith(".name")) {
					String value = properties.getProperty(key);
					if (value.equals(board)) {
						re = key.substring(0, key.indexOf(".name"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}

	public static ArrayList<String> getCpuByBoard(String filePath, String board) {
		ArrayList<String> re = new ArrayList<String>();
		String regex = getCompileKeyByBoards(filePath, board) + ".menu.cpu.(\\w)+";
		OrderedProperties properties = new OrderedProperties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			properties.load(in);
			Iterator<Object> it = properties.keySet().iterator();
			while (it.hasNext()) {
				Object entry = it.next();
				String key = (String) entry;
				if (key.matches(regex)) {
					re.add(key.substring(key.lastIndexOf(".") + 1, key.length()));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}

	public static void saveYuanma(String yuanma) {
		String filepath = "testArduino/testArduino.ino";
		try {
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWritter = new FileWriter(file);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(yuanma);
			bufferWritter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveCode(String yuanma, String filepath) {
		try {
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWritter = new FileWriter(file);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(yuanma);
			bufferWritter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveLanguage(String value, String filepath) {
		try {
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}

			OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			BufferedWriter bufferWritter = new BufferedWriter(writerStream);
			bufferWritter.write(value);
			bufferWritter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveDigitalPinToInterrupt(String str) {
		String filepath = "blockly/apps/mixly/digitalPinToInterrupt.js";
		try {
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWritter = new FileWriter(file);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(str);
			bufferWritter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean saveXML(String xml, String filepath) {
		try {
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(filepath);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			BufferedWriter bufferWritter = new BufferedWriter(osw);
			bufferWritter.write(xml);
			bufferWritter.close();
			osw.close();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String readXml(File file) {
		if (!file.exists()) {
			System.err.println("Can't Find file!");
			return null;
		}
		String s = "";
		try {
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader in = new BufferedReader(isr);
			String str;
			while ((str = in.readLine()) != null) {
				s = s + str.trim();
			}

			in.close();
			isr.close();
			fis.close();
			return s;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}

	public static String readFile(File file) {
		if (!file.exists()) {
			System.err.println("Can't Find file!");
			return null;
		}
		StringBuilder s = new StringBuilder("");
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader in = new BufferedReader(read);
			String str;
			while ((str = in.readLine()) != null) {
				s.append(str).append("\n");
			}

			in.close();
			return s.toString();
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}

	public static String readBoardNameFromXml(File file) {
		if (!file.exists()) {
			System.err.println("Can't Find file!");
			return null;
		}
		String s = "";
		try {
			BufferedReader in = new BufferedReader(new java.io.FileReader(file));
			String str;
			while ((str = in.readLine()) != null) {
				s = s + str;
			}

			in.close();
			Pattern p = Pattern.compile("<xml board=\"(.+?)\"");
			Matcher matcher = p.matcher(s);
			if (matcher.find()) {
				return matcher.group(1);
			}
			return null;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}

	public static boolean compareStrArray(String[] pre, String[] cur) {
		if ((pre == null) && (cur == null))
			return true;
		if ((pre != null) && (cur != null)) {
			if (pre.length != cur.length) {
				return false;
			}
			for (int i = 0; i < pre.length; i++) {
				if (!pre[i].equals(cur[i])) {

					return false;
				}
			}
			return true;
		}

		return false;
	}

	public static boolean deleteFile(String f) {
		boolean flag = false;
		File file = new File(f);

		if ((file.isFile()) && (file.exists())) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public static boolean deleteDirectory(String sPath) {
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);

		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
			return false;
		}
		boolean flag = true;

		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			} else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag) {
			return false;
		}
		if (dirFile.delete()) {
			return true;
		}
		return false;
	}

	public static int renameFile(String path, String oldname, String newname) {
		if (!oldname.equals(newname)) {
			File oldfile = new File(path + "/" + oldname);
			File newfile = new File(path + "/" + newname);
			if (!oldfile.exists()) {
				return -1;
			}
			if (newfile.exists()) {
				return 1;
			}
			oldfile.renameTo(newfile);
			return 2;
		}

		return -1;
	}

	public static List<String> getXmlMilList(String path) {
		File file = new File(path);
		List<String> namelist = new ArrayList<String>();
		if (file.isDirectory()) {
			File[] dirFile = file.listFiles();
			File[] arrayOfFile1;
			int j = (arrayOfFile1 = dirFile).length;
			for (int i = 0; i < j; i++) {
				File f = arrayOfFile1[i];
				if (!f.isDirectory()) {

					if ((f.getName().endsWith(".xml")) || (f.getName().endsWith(".XML"))
							|| (f.getName().endsWith(".mil")) || (f.getName().endsWith(".MIL"))) {
						namelist.add(f.getAbsolutePath());
					}
				}
			}
		}
		return namelist;
	}

	public static List<String> getBoardsList(String path) {
		File file = new File(path);
		List<String> namelist = new ArrayList<String>();
		if (file.isDirectory()) {
			File[] dirFile = file.listFiles();
			File[] arrayOfFile1;
			int j = (arrayOfFile1 = dirFile).length;
			for (int i = 0; i < j; i++) {
				File f = arrayOfFile1[i];
				if (f.isDirectory()) {
					namelist.addAll(getBoardsList(f.getAbsolutePath()));
				} else if (f.getName().equals("boards.txt")) {
					namelist.add(f.getAbsolutePath());
				}
			}
		}

		return namelist;
	}

	public static List<String> getJSList(String path) {
		File file = new File(path);
		List<String> namelist = new ArrayList<String>();
		if (file.isDirectory()) {
			File[] dirFile = file.listFiles();
			File[] arrayOfFile1;
			int j = (arrayOfFile1 = dirFile).length;
			for (int i = 0; i < j; i++) {
				File f = arrayOfFile1[i];
				if (f.isDirectory()) {
					namelist.addAll(getJSList(f.getAbsolutePath()));
				} else if ((f.getName().endsWith(".js")) || (f.getName().endsWith(".JS"))) {
					namelist.add(f.getAbsolutePath());
				}
			}
		}

		return namelist;
	}

	public static void setCustom() {
		List<String> xmlList = getXmlMilList("mylib");
		List<String> companyList = getXmlMilList("company");
		String customblock = "null";
		String company_block = "null";
		if ((xmlList != null) && (xmlList.size() > 0)) {
			customblock = "<sep></sep>";
			for (int i = 0; i < xmlList.size(); i++) {
				String xmlname = (String) xmlList.get(i);
				String temp = readXml(new File(xmlname)).replaceAll("<xml board=\"(.+?)\">|<xml>|</xml>", "");
				customblock = customblock + "<category colour=\"290\" name=\"";

				customblock = customblock + xmlname.substring(
						xmlname.lastIndexOf(System.getProperty("file.separator")) + 1, xmlname.lastIndexOf("."));
				customblock = customblock + "\">";
				customblock = customblock + temp;
				customblock = customblock + "</category>";
			}
		}
		if ((companyList != null) && (companyList.size() > 0)) {
			company_block = "<sep></sep>";
			for (int i = 0; i < companyList.size(); i++) {
				String xmlname = (String) companyList.get(i);
				String temp = readXml(new File(xmlname));
				company_block = company_block + temp;
			}
		}
		saveMyBlock("var myblock='" + customblock + "';\n" + "var company_block='" + company_block + "';\n");
	}

	public static void saveMyBlock(String str) {
		String filepath = "blockly/apps/mixly/myblock.js";
		try {
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fileWritter = new FileOutputStream(file);
			BufferedWriter bufferWritter = new BufferedWriter(new OutputStreamWriter(fileWritter, "UTF-8"));
			bufferWritter.write(str);
			bufferWritter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setLanguage() {
		java.util.Locale locale = java.util.Locale.getDefault();
		String lang = locale.getLanguage();

		changeCurrentBoard(cofig, "language", lang.contains("zh") ? "zh-hans" : "en");
	}

	public static void setCompanyLanguage() {
		List<String> ls = getJSList("blockly/msg/js/company/language");
		StringBuilder en = new StringBuilder("");
		StringBuilder spa = new StringBuilder("");
		StringBuilder zhs = new StringBuilder("");
		StringBuilder zht = new StringBuilder("");
		for (int i = 0; i < ls.size(); i++) {
			String value = readFile(new File((String) ls.get(i))) + "\n";
			if (value != null) {
				if (((String) ls.get(i)).endsWith("en.js")) {
					en.append(value);
				} else if (((String) ls.get(i)).endsWith("spa.js")) {
					spa.append(value);
				} else if (((String) ls.get(i)).endsWith("zh-hans.js")) {
					zhs.append(value);
				} else if (((String) ls.get(i)).endsWith("zh-hant.js")) {
					zht.append(value);
				}
			}
		}
		saveLanguage(en.toString(), "blockly/msg/js/company/en.js");
		saveLanguage(spa.toString(), "blockly/msg/js/company/spa.js");
		saveLanguage(zhs.toString(), "blockly/msg/js/company/zh-hans.js");
		saveLanguage(zht.toString(), "blockly/msg/js/company/zh-hant.js");
	}

	public static void setCompanyPin() {
		List<String> ls = getJSList("blockly/apps/mixly/companypin");
		StringBuilder str = new StringBuilder("");
		for (int i = 0; i < ls.size(); i++) {
			String value = readFile(new File((String) ls.get(i))) + "\n";
			if (value != null) {
				str.append(value);
			}
		}
		saveLanguage(str.toString(), "blockly/apps/mixly/company_pin.js");
	}

	public static String getOsName() {
		Properties props = System.getProperties();
		String osName = props.getProperty("os.name");

		return osName;
	}

	public static void createSomeFolder() {
		FileUtil.createEmptyFolder("sample");
		FileUtil.createEmptyFolder("company");
		FileUtil.createEmptyFolder("blockly/blocks/company");
		FileUtil.createEmptyFolder("blockly/generators/arduino/company");
		FileUtil.createEmptyFolder("blockly/msg/js/company/language");
		FileUtil.createEmptyFolder("blockly/apps/mixly/companypin");

		String arduinoPath = getProp(cofig, "arduinoPath");
		FileUtil.createEmptyFolder(arduinoPath + "mixlyBuild");
	}

	public static void main(String[] args) {
		System.out.println(getOsName());
	}
}