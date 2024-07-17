package me.kimovoid.betaqol.rcon;

public class BufferHelper {
	public static char[] HEX_CHARS_LOOKUP = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	public BufferHelper() {
	}

	public static String getString(byte[] buf, int pos, int max) {
		int var3 = max - 1;

		int var4;
		for(var4 = pos > var3 ? var3 : pos; 0 != buf[var4] && var4 < var3; ++var4) {
		}

		return new String(buf, pos, var4 - pos);
	}

	public static int getIntLE(byte[] buf, int start) {
		return getIntLE(buf, start, buf.length);
	}

	public static int getIntLE(byte[] buf, int start, int limit) {
		return 0 > limit - start - 4 ? 0 : buf[start + 3] << 24 | (buf[start + 2] & 255) << 16 | (buf[start + 1] & 255) << 8 | buf[start] & 255;
	}

	public static int getIntBE(byte[] buf, int start, int limit) {
		return 0 > limit - start - 4 ? 0 : buf[start] << 24 | (buf[start + 1] & 255) << 16 | (buf[start + 2] & 255) << 8 | buf[start + 3] & 255;
	}

	public static String toHex(byte b) {
		return "" + HEX_CHARS_LOOKUP[(b & 240) >>> 4] + HEX_CHARS_LOOKUP[b & 15];
	}
}
