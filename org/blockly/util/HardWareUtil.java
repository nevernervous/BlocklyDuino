package org.blockly.util;

import javafx.scene.web.WebEngine;

public class HardWareUtil {
	public static int getNumOfDigitalPin(WebEngine webEngine) {
		String js = "profile.default.digital.length";
		int len = ((Integer) webEngine.executeScript(js)).intValue();
		return len;
	}

	public static int getNumOfAnalogPin(WebEngine webEngine) {
		String js = "profile.default.analog.length";
		int len = ((Integer) webEngine.executeScript(js)).intValue();
		return len;
	}

	public static String getHardWareTestCode(int digitalNum, int analogNum) {
		StringBuilder code = new StringBuilder("");
		StringBuilder str_d = new StringBuilder("");
		StringBuilder str_a = new StringBuilder("");
		for (int i = 0; i < digitalNum; i++) {
			str_d.append(i);
			if (i != digitalNum - 1) {
				str_d.append(",");
			}
		}
		for (int i = 0; i < analogNum; i++) {
			str_a.append("A" + i);
			if (i != analogNum - 1) {
				str_a.append(",");
			}
		}

		code.append("int list_d[]={").append(str_d).append("};\n").append("int list_a[]={").append(str_a).append("};\n")
				.append("int leng_d=0,leng_a=0;\n\n").append("void setup()\n{\n")
				.append("  leng_d = sizeof(list_d)/sizeof(list_d[0]);\n")
				.append("  leng_a = sizeof(list_a)/sizeof(list_a[0]);\n").append("  Serial.begin(9600);\n}\n\n")
				.append("void loop()\n{\n").append("  Serial.print(\"digital\");\n")
				.append("  for (int i = 0; i < leng_d; i++) {\n").append("    pinMode(list_d[i], INPUT);\n")
				.append("    Serial.print(digitalRead(list_d[i]));\n").append("    Serial.print(\" \");\n  }\n")
				.append("  Serial.println(\"\");\n").append("  Serial.print(\"analog\");\n")
				.append("  for (int i = 0; i < leng_a; i++) {\n").append("    Serial.print(analogRead(list_a[i]));\n")
				.append("    Serial.print(\" \");\n  }\n").append("  Serial.println(\"\");\n")
				.append("  delay(100);\n}\n");
		return new String(code);
	}
}