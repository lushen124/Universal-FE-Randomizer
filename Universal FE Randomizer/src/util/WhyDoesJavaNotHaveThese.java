package util;

public class WhyDoesJavaNotHaveThese {

	public static int clamp(int value, int min, int max) {
		return Math.min(max, Math.max(min, value));
	}
	
	public static Boolean isValueBetween(int value, int min, int max) {
		if (value >= min && value <= max) {
			return true;
		}
		
		return false;
	}
	
	public static String displayStringForBytes(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		
		if (bytes != null) {
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toHexString((bytes[i] & 0xFF)) + " ");
			}
		}
		
		return sb.toString().toUpperCase();
	}
	
	public static Boolean byteArraysAreEqual(byte[] array1, byte[] array2) {
		if (array1.length != array2.length) {
			return false;
		}
		
		for (int i = 0; i < array1.length; i++) {
			if (array1[i] != array2[i]) {
				return false;
			}
		}
		
		return true;
	}
}
