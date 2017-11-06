package org.blockly.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import org.blockly.MyBrowser;

public class FileUtil {
	public static boolean copyFileTo(String file, String to) {
		File f = new File(file);
		File t = new File(to + "/" + f.getName());
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(f);
			fo = new FileOutputStream(t);
			in = fi.getChannel();
			out = fo.getChannel();
			in.transferTo(0L, in.size(), out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			fi.close();
			in.close();
			fo.close();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static void copyFolderTo(File src, File dest) {
		try {
			if (src.isDirectory()) {
				if (!dest.exists()) {
					dest.mkdir();
				}
				String[] files = src.list();
				String[] arrayOfString1;
				int j = (arrayOfString1 = files).length;
				for (int i = 0; i < j; i++) {
					String file = arrayOfString1[i];
					File srcFile = new File(src, file);
					File destFile = new File(dest, file);

					copyFolderTo(srcFile, destFile);
				}
			} else {
				copyFileTo(src.getAbsolutePath(), dest.getParent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createEmptyFolder(String path) {
		File dest = new File(path);
		if (!dest.exists()) {
			dest.mkdir();
			System.out.println("create：" + path);
		}
	}

	public static void main(String[] args) {
	}

	public static void searchAndExport(String lib, String path) {
		File searchPath = new File(MyBrowser.arduinoPath + "libraries");
		if (searchAndExportDetail(lib, searchPath, path)) {
			return;
		}

		searchPath = new File(MyBrowser.arduinoPath + "hardware/arduino/avr/libraries");
		searchAndExportDetail(lib, searchPath, path);
	}

	private static boolean searchAndExportDetail(String lib, File searchPath, String dest) {
		if (searchPath.isDirectory()) {
			String[] files = searchPath.list();
			String[] arrayOfString1;
			int j = (arrayOfString1 = files).length;
			for (int i = 0; i < j; i++) {
				String file = arrayOfString1[i];
				File src = new File(searchPath + "/" + file);
				if ((src.isDirectory()) && (cotainsFile(src, lib))) {
					String srcname = src.getName();
					copyFolderTo(src, new File(dest + "/" + srcname));

					return true;
				}
			}
		}

		return false;
	}

	private static boolean cotainsFile(File path, String name) {
		if (path.isDirectory()) {
			String[] files = path.list();
			String[] arrayOfString1;
			int j = (arrayOfString1 = files).length;
			for (int i = 0; i < j; i++) {
				String file = arrayOfString1[i];

				if (cotainsFile(new File(path + "/" + file), name)) {
					return true;
				}

			}
		} else if (path.getName().equals(name)) {
			return true;
		}

		return false;
	}
}