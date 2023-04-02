package util;

public class PaletteUtil {
	public static byte[] getByteArrayFromString(String palette) {
		int len = palette.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(palette.charAt(i), 16) << 4)
					+ Character.digit(palette.charAt(i + 1), 16));
		}
		return data;
	}
}