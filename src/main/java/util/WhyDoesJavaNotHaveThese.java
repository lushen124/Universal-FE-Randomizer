package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
	
	public static byte[] bytesFromPointer(long pointer) {
		return new byte[] {(byte)((pointer >> 24) & 0xFF), (byte)((pointer >> 16) & 0xFF), (byte)((pointer >> 8 & 0xFF)), (byte)(pointer & 0xFF)};
	}
	
	public static byte[] bytesFromAddress(long address) {
		if (address <= 0x8000000) {
			address += 0x8000000;
		}
		
		return new byte[] {(byte)(address & 0xFF), (byte)((address & 0xFF00) >> 8), (byte)((address & 0xFF0000) >> 16), (byte)((address & 0xFF000000) >> 24)};
	}
	
	public static int intValueFromByteSubarray(byte[] data, int start, int length, boolean isLittleEndian) {
		byte[] effectiveData = new byte[length];
		for (int i = 0; i < length; i++) {
			effectiveData[i] = data[start + i];
		}
		
		long value = longValueFromByteArray(effectiveData, isLittleEndian);
		return (int)value;
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
	
	// Copies x bytes from source array into an offset destination array.
	public static void copyBytesIntoByteArrayAtIndex(byte[] source, byte[] destination, int destinationOffset, int copyLength) {
		assert destination.length >= copyLength + destinationOffset : "Attempted to copy source into destination with insufficient space";
		assert copyLength <= source.length : "Copy length is too large for source array";
		
		for (int i = 0; i < copyLength; i++) {
			destination[destinationOffset + i] = source[i];
		}
	}
	
	// Copies x bytes from an offset source array into the destination array.
	public static void copyBytesFromByteArray(byte[] source, byte[] destination, int sourceOffset, int copyLength) {
		assert source.length >= copyLength + sourceOffset : "Attempted to copy beyond the source array's bounds.";
		assert copyLength <= destination.length : "Copy length is too large for destination array";
		
		for (int i = 0; i < copyLength; i++) {
			destination[i] = source[sourceOffset + i];
		}
	}
	
	public static String stringFromAsciiBytes(byte[] input) {
		StringBuilder sb = new StringBuilder();
		for (byte currentByte : input) {
			if (currentByte == 0) { break; }
			sb.append((char)currentByte);
		}
		return sb.toString();
	}
	
	public static String stringFromShiftJIS(byte[] input) {
		StringBuilder sb = new StringBuilder();
		
		InputStream in = new ByteArrayInputStream(input);
		try {
			Reader reader = new InputStreamReader(in, Charset.forName("SJIS"));
			int read;
			while ((read = reader.read()) != -1) {
				if (read == 0) { break; }
				sb.append((char)read);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "<ShiftJIS Decoding Error>";
		}
		
		return sb.toString();
	}
	
	public static byte[] asciiBytesFromString(String string) {
		byte[] byteArray = new byte[string.length()];
		for (int i = 0; i < string.length(); i++) {
			byteArray[i] = (byte)string.charAt(i);
		}
		
		return byteArray;
	}
	
	public static byte[] shiftJISBytesFromString(String string) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(out, Charset.forName("SJIS"));
		try {
			writer.write(string);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return asciiBytesFromString(string);
		}
		
		return out.toByteArray();
	}
	
	public static long longValueFromByteArray(byte[] input, boolean isLittleEndian) {
		long offsetValue = 0;
		long mask = 0;
		
		if (isLittleEndian) {
			for (int i = input.length - 1; i >= 0; i--) {
				int nextValue = input[i];
				mask <<= 8;
				mask |= 255;
				// 0x12345678 <-> 78 56 34 12
				offsetValue <<= 8;
				offsetValue |= (nextValue & 0xFF);
			}
		} else {
			for (int i = 0; i < input.length; i++) {
				int nextValue = input[i];
				mask <<= 8;
				mask |= 255;
				// 0x12345678 <-> 12 34 56 78
				offsetValue <<= 8;
				offsetValue |= (nextValue & 0xFF);
			}
		}
		
		return (offsetValue & mask);
	}
	
	public static byte[] byteArrayFromLongValue(long input, boolean isLittleEndian, int numBytes) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		for (int i = 0; i < numBytes; i++) {
			builder.appendByte((byte)(input & 0xFF));
			input >>= 8;
		}
		if (!isLittleEndian) {
			return builder.reversedBytes();
		}
		
		return builder.toByteArray();
	}
	
	public static String inCamelCase(String baseString) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < baseString.length(); i++) {
			char c = baseString.charAt(i);
			if (i == 0 ||
					baseString.charAt(i - 1) == '_' ||
					Character.isWhitespace(baseString.charAt(i - 1))) {
				sb.append(Character.toUpperCase(c));
			} else  {
				sb.append(Character.toLowerCase(c));
			}
		}
		
		return sb.toString();
	}
	
	public static byte[] subArray(byte[] original, int start, int length) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = original[start + i];
		}
		
		return result;
	}
	
	public static <T> List<T> createMutableCopy(List<T> list) {
		List<T> copy = new ArrayList<T>();
		for(T item : list) {
			copy.add(item);
		}
		return copy;
	}
	
	public static List<Byte> byteArrayToByteList(byte[] byteArray) {
		List<Byte> list = new ArrayList<Byte>();
		for (byte current : byteArray) {
			list.add(current);
		}
		return list;
	}
	
	public static int firstIndexOfBytesInByteArray(byte[] byteArray, byte[] targetBytes, int startOffset, int maxOffset) {
		if (startOffset >= byteArray.length || maxOffset <= startOffset) { return -1; }
		for (int i = startOffset; i < Math.min(maxOffset, byteArray.length); i++) {
			if (byteArray[i] == targetBytes[0]) {
				boolean isMatch = true;
				for (int j = 0; j < targetBytes.length; j++) {
					if (byteArray[i + j] != targetBytes[j]) {
						isMatch = false;
						break;
					}
				}
				if (isMatch) {
					return i;
				}
			}
		}
		
		return -1;
	}
}
