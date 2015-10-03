package org.omg.bpmn.miwg.util;

public class StringUtil {

	public StringUtil() {
		// TODO Auto-generated constructor stub
	}

	public static String generateSpaces(int n) {
		return generateCharacters(n, ' ');
	}

	public static String generateCharacters(int n, char c) {
		String s = "";
		for (int i = 0; i < n; i++) {
			s += c;
		}
		return s;
	}

}
