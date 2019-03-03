package util;

import java.util.Comparator;
import java.util.List;

public class WhyDoesJavaNotHaveThese {
	
	public static enum ComparatorResult {
		SECOND_GREATER, EQUAL, FIRST_GREATER;
		
		public int returnValue() {
			switch (this) {
			case FIRST_GREATER: return 1;
			case EQUAL: return 0;
			case SECOND_GREATER: return -1;
			default: return 0;
			}
		}
	}
	
	public static final Comparator<Integer> ascendingIntegerComparator = new Comparator<Integer>() {
		@Override
		public int compare(Integer arg0, Integer arg1) {
			return arg0 > arg1 ? ComparatorResult.FIRST_GREATER.returnValue() : (arg0 == arg1 ? ComparatorResult.EQUAL.returnValue() : ComparatorResult.SECOND_GREATER.returnValue());
		}
	};

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
	
	public static Boolean byteArrayHasPrefix(byte[] array, byte[] prefix) {
		if (prefix.length > array.length) { return false; }
		for (int i = 0; i < prefix.length; i++) {
			if (prefix[i] != array[i]) { return false; }
		}
		
		return true;
	}
	
	public static Boolean byteArrayMatchesFormat(byte[] array, List<Byte> format) {
		if (format.size() > array.length) { return false; }
		for (int i = 0; i < format.size(); i++) {
			Byte currentFormatByte = format.get(i);
			if (currentFormatByte == null) { continue; }
			byte formatByteValue = currentFormatByte.byteValue();
			if (array[i] != formatByteValue) { return false; }
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
	
	public static byte[] gbaAddressFromOffset(long offset) {
		byte[] result = new byte[4];
		long actualOffset = offset + 0x08000000;
		
		result[0] = (byte)(actualOffset & 0xFF);
		result[1] = (byte)((actualOffset >> 8) & 0xFF);
		result[2] = (byte)((actualOffset >> 16) & 0xFF);
		result[3] = (byte)((actualOffset >> 24) & 0xFF);
		
		return result;
	}
	
	public static void copyBytesIntoByteArrayAtIndex(byte[] source, byte[] destination, int offset, int copyLength) {
		assert destination.length >= copyLength + offset : "Attempted to copy source into destination with insufficient space";
		assert copyLength <= source.length : "Copy length is too large for source array";
		
		for (int i = 0; i < copyLength; i++) {
			destination[offset + i] = source[i];
		}
	}
}
