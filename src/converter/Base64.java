package converter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import util.StopWatch;
import util.StringUtility;

public class Base64 {

	/* [A-Z] [a-z] [0-9] [+] [/] の順に並ぶ */
	private static final char[] BASE64_CHARS = {
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
	};

	/* 文字列を変換する時に使う文字コード */
	private static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	/**
	 * エンコードする
	 *
	 * @param str エンコードしたい文字列
	 * @return base64文字列
	 */
	public static String encodeString(String str) {
		return encode(str.getBytes(DEFAULT_CHARSET));
	}

	/**
	 * エンコードする
	 *
	 * @param str エンコードしたい文字列
	 * @param charset 文字列変換に使う文字コード
	 * @return base64文字列
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeString(String str, String charset) throws UnsupportedEncodingException {
		return encode(str.getBytes(charset));
	}

	/**
	 * エンコードする
	 *
	 * @param bytes エンコードしたいバイト配列
	 * @return base64文字列
	 */
	public static String encode(byte[] bytes) {
		// 2進数文字列に変換
		StringBuilder binaryString = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			int b = bytes[i];
			if (b < 0)
				b += 256;
			String bits = Integer.toBinaryString(b);
			// 0埋め
			int paddingLen = 8 - bits.length();
			while (paddingLen-- > 0) {
				binaryString.append('0');
			}
			binaryString.append(bits);
		}

		// 6の倍数になるように0埋め
		while (binaryString.length() % 6 > 0) {
			binaryString.append('0');
		}

		// 6桁ずつに分割
		String[] arr = StringUtility.divideString(binaryString.toString(), 6);

		// base64文字列に変換
		StringBuilder base64 = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			String bits = arr[i];
			int b = Integer.parseInt(bits, 2);
			base64.append(BASE64_CHARS[b]);
		}

		// パディング
		while (base64.length() % 4 > 0) {
			base64.append('=');
		}

		return base64.toString();
	}

	/**
	 * デコードする
	 *
	 * @param base64 base64文字列
	 * @return デコードされた文字列
	 */
	public static String decodeString(String base64) {
		return new String(decode(base64), DEFAULT_CHARSET);
	}

	/**
	 * デコードする
	 *
	 * @param base64 base64文字列
	 * @return デコードされた文字列
	 * @param charset 文字列変換に使う文字コード
	 * @throws UnsupportedEncodingException
	 */
	public static String decodeString(String base64, String charset) throws UnsupportedEncodingException {
		return new String(decode(base64), charset);
	}

	/**
	 * デコードする
	 *
	 * @param base64 base64文字列
	 * @return デコードされたバイト配列
	 */
	public static byte[] decode(String base64) {
		// パディングをはずす
		base64 = base64.replace("=", "");

		// 2進数の文字列に変換
		char[] chars = base64.toCharArray();
		StringBuilder binaryString = new StringBuilder();
		for (char c : chars) {
			for (int i = 0; i < BASE64_CHARS.length; i++) {
				if (c == BASE64_CHARS[i]) {
					// 6桁の2進数に変換
					String bits = Integer.toBinaryString(i);
					// 0埋め
					int paddingLen = 6 - bits.length();
					while (paddingLen-- > 0) {
						binaryString.append('0');
					}
					binaryString.append(bits);
					break;
				}
			}
		}

		// 8桁ずつに分割
		int len = binaryString.length();
		String str = binaryString.substring(0, len - len % 8);
		String[] strBytes = StringUtility.divideString(str, 8);

		// バイト配列に変換する
		byte[] bytes = new byte[strBytes.length];
		for (int i = 0; i < bytes.length; i++) {
			String bits = strBytes[i];
			int b = Integer.parseInt(bits, 2);
			bytes[i] = (byte)b;
		}

		return bytes;
	}

	/**
	 * エンコードとデコードのテストをする
	 */
	public static void test(String... args) {
		if (args == null)
			args = new String[] {"abcdefg"};

		StopWatch sw = new StopWatch();
		for (String str : args) {
			sw.start();
			System.out.println("before: " + str);
			String base64 = Base64.encodeString(str);
			System.out.println("base64: " + base64);
			String result = Base64.decodeString(base64);
			System.out.println("after:  " + result);
			sw.stop();
			System.out.println("elapsed: " + sw.elapsed() + "ms");
			if (!str.equals(result))
				throw new AssertionError("Base64テスト失敗");
		}
	}

}
