package util;

import java.util.List;

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
				String hexComponent = Integer.toHexString((bytes[i] & 0xFF));
				if (hexComponent.length() == 1) {
					sb.append('0');
				}
				sb.append(hexComponent + " ");
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
	
	public static byte[] bytesFromAddress(long address) {
		if (address <= 0x8000000) {
			address += 0x8000000;
		}
		
		return new byte[] {(byte)(address & 0xFF), (byte)((address & 0xFF00) >> 8), (byte)((address & 0xFF0000) >> 16), (byte)((address & 0xFF000000) >> 24)};
	}
	
	public static byte[] byteArrayFromByteList(List<Byte> byteList) {
		byte[] byteArray = new byte[byteList.size()];
		for (int i = 0; i < byteList.size(); i++) {
			byteArray[i] = byteList.get(i);
		}
		
		return byteArray;
	}
	
	public static String stringByCapitalizingFirstLetter(String input) {
		String firstLetter = input.substring(0, 1);
		String remainder = input.substring(1).replace('_', ' ');
		return firstLetter.toUpperCase() + remainder.toLowerCase();
	}
}
