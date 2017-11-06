package org.blockly.util;

import java.io.PrintStream;
import java.util.regex.Pattern;
import jssc.SerialPortList;

public class MyCom {
	public static String[] getSerialNames() {
		int osType = jssc.SerialNativeInterface.getOsType();
		if (osType == 3) {
			return SerialPortList.getPortNames("/dev/", Pattern.compile("tty."));
		}
		return SerialPortList.getPortNames();
	}

	public static void main(String[] args) {
		new Thread() {
			public void run() {
				while (true) {
					String[] list = MyCom.getSerialNames();
					if ((list != null) && (list.length > 0)) {
						for (int i = 0; i < list.length; i++) {
							System.out.print(list[i]);
						}
						System.out.println("");
					}
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
}