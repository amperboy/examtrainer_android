package de.higger.examtrainer.tool;

import android.util.Base64;

public class StringBase64 {
	public static String encode(String in) {
		return new String(Base64.encode(in.getBytes(), Base64.NO_WRAP));
	}	
	
	public static String decode(String in) {
		return new String(Base64.decode(in, Base64.NO_WRAP));
	}
}
