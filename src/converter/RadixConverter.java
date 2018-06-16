package converter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 基数変換
 */
public class RadixConverter {

	/* [0-9] [a-z] [A-Z] の順に並ぶ */
	private static final char[] RADIX_TABLE = new char[]{
			'0','1','2','3','4','5','6','7','8','9',
			'a','b','c','d','e','f','g','h','i','j',
			'k','l','m','n','o','p','q','r','s','t',
			'u','v','w','x','y','z',
			'A','B','C','D','E','F','G','H','I','J',
			'K','L','M','N','O','P','Q','R','S','T',
			'U','V','W','X','Y','Z'
	};

	/**
	 * n進数⇒n進数変換
	 * 非常に大きな桁数と小数に対応
	 * 62進数まで対応
	 *
	 * 例：
	 * ("3.141592", 10, 16)   ⇒ 3.243f5f91600f345069a4df47f993d5347...
	 * ("3.141592", 10, 2)    ⇒ 11.00100100001111110101111110010001...
	 * ("11111111.11", 2, 10) ⇒ 255.75
	 * ("abcd.efgh", 32, 16)  ⇒ 52d8d.73e11
	 *
	 * @param anyDecimal 変換したい数
	 * @param 基数（変換元）
	 * @param 基数（変換先）
	 */
	public static String anyDecimalToAnyDecimal(String anyDecimal, int radixFrom, int radixTo) {
		return decimalToAnyDecimal(anyDecimalToDecimal(anyDecimal, radixFrom), radixTo);
	}

	/**
	 * n進数⇒10進数 (2 <= n <= 36)
	 *
	 * @param anyDecimal n進数文字列
	 * @param radix 基数
	 * @return 10進数:BigDecimal
	 */
	public static BigDecimal anyDecimalToDecimal(String anyDecimal, int radix) {
		if (anyDecimal == null || anyDecimal.isEmpty())
			throw new NullPointerException("anyDecimal == null || anyDecimal.isEmpty()");

		// 負の数かどうか
		boolean negative = false;
		if (anyDecimal.charAt(0) == '-') {
			negative = true;
			anyDecimal = anyDecimal.substring(1);
		}

		// 整数部分と小数部分を分解
		String[] chunk = anyDecimal.split("\\.");

		BigDecimal decimal = BigDecimal.ZERO;
		BigDecimal bdRadix = new BigDecimal(radix);

		// 整数部分
		if (chunk.length > 0) {
			if (chunk[0].length() == 0)
				throw new NumberFormatException("numberPart.length() == 0");

			// 計算
			char[] numberPart = chunk[0].toCharArray();
			for (int i = numberPart.length - 1, exponent = 0; i >= 0; i--, exponent++) {
				BigDecimal num1 = new BigDecimal(integerParseInt(String.valueOf(numberPart[i]), radix));
				BigDecimal num2 = bdRadix.pow(exponent);
				decimal = decimal.add(num1.multiply(num2));
			}
		}

		// 小数部分
		if (chunk.length > 1) {
			if (chunk[1].length() == 0)
				throw new NumberFormatException("fractionalPart.length() == 0");

			// 計算
			char[] fractionalPart = chunk[1].toCharArray();
			for (int i = 0, exponent = 1; i < fractionalPart.length; i++, exponent++) {
				BigDecimal num1 = new BigDecimal(integerParseInt(String.valueOf(fractionalPart[i]), radix));
				BigDecimal num2 = bdRadix.pow(exponent);
				decimal = decimal.add(num1.divide(num2, 1024, RoundingMode.HALF_UP));
			}
		}

		return negative ? decimal.negate() : decimal;
	}

	/**
	 * 10進数⇒n進数
	 *
	 * @param decimal 10進数
	 * @param radix 基数
	 * @return n進数文字列
	 */
	public static String decimalToAnyDecimal(BigDecimal decimal, int radix) {
		return decimalToAnyDecimal(decimal, radix, 1024);
	}

	/**
	 * 10進数⇒n進数
	 *
	 * @param decimal 10進数
	 * @param radix 基数
	 * @param scale 小数点以下の桁数
	 * @return n進数文字列
	 */
	public static String decimalToAnyDecimal(BigDecimal decimal, int radix, int scale) {
		if (decimal == null)
			throw new NullPointerException("decimal == null");

		// 結果格納変数
		StringBuilder result = new StringBuilder();

		// 基数
		BigDecimal base = new BigDecimal(radix);

		// 負の数かどうか
		boolean negative = false;
		if (decimal.compareTo(BigDecimal.ZERO) == -1) {
			negative = true;
			decimal = decimal.negate();
		}

		// 整数部分
		BigDecimal numberPart = decimal.setScale(0, BigDecimal.ROUND_DOWN);
		BigDecimal mod = BigDecimal.ZERO;
		while(numberPart.compareTo(base.subtract(BigDecimal.ONE)) == 1){
			mod = numberPart.remainder(base);
			numberPart = numberPart.divide(base).setScale(0, BigDecimal.ROUND_DOWN);

			result = result.insert(0, integerToString(Integer.parseInt(mod.toString()), radix));
		}
		mod = numberPart.remainder(base);
		result = result.insert(0, integerToString(Integer.parseInt(mod.toString()), radix));

		// 小数部分
		BigDecimal fractionalPart = decimal.remainder(BigDecimal.ONE);
		if (fractionalPart.compareTo(BigDecimal.ZERO) != 0) {
			result.append(".");
		}
		for (int i = 0; i < scale; i ++) {
			// 小数部分をr倍したものを代入
			fractionalPart = fractionalPart.remainder(BigDecimal.ONE).multiply(base);

			// 小数部分が0の場合
			if (fractionalPart.compareTo(BigDecimal.ZERO) == 0)
				break;

			// 小数点以下を切り捨てた値を取得
			int n = Integer.parseInt(fractionalPart.setScale(0, BigDecimal.ROUND_DOWN).toString());
			result.append(integerToString(n, radix));
		}

		if (negative) result.insert(0, "-");

		return result.toString();
	}

	/**
	 * int型数値⇒n進数文字列
	 * @param num int型数値
	 */
	private static String integerToString(int num, int radix){
		if (radix > RADIX_TABLE.length)
			throw new IllegalArgumentException("radix > RADIX_TABLE.length");

		StringBuilder result = new StringBuilder();

		int tmp = num;
		int mod = 0;
		while(tmp >= radix){
			mod = tmp % radix;
			tmp = tmp / radix;

			result = result.insert(0, RADIX_TABLE[(int)mod]);
		}
		mod = tmp % radix;
		result = result.insert(0, RADIX_TABLE[(int)mod]);

		return result.toString();
	}

	/**
	 * n進数文字列⇒int型の数値
	 * @param str n進数文字列
	 */
	private static long integerParseInt(String str, int radix){
		if (radix > RADIX_TABLE.length)
			throw new IllegalArgumentException("radix > RADIX_TABLE.length");

		int result = 0;

		char[] chars = str.toCharArray();
		int power = 1;
		for(int i = chars.length - 1; i >= 0; i--, power *= radix){
			int base = -1;
			for(int j = 0; i < radix; j++){
				if(chars[i] == RADIX_TABLE[j]){
					base = j;
					break;
				}
			}
			result += base * power;
		}

		return result;
	}

	/**
	 * 計算誤差をテストする
	 */
	public static void test() {
		// 許容誤差
		BigDecimal accuracy = new BigDecimal(0.000000000000001);

		// 2進数での誤差テスト
		for (double i = 0; i < 100; i += 0.1) {
			BigDecimal decimal = new BigDecimal(i);
			String anyDecimal = RadixConverter.decimalToAnyDecimal(decimal, 2);
			BigDecimal result = RadixConverter.anyDecimalToDecimal(anyDecimal, 2);
			if (decimal.subtract(result).abs().compareTo(accuracy) == 1) {
				throw new ArithmeticException();
			}
		}

		// 8進数での誤差テスト
		for (double i = 0; i < 100; i += 0.1) {
			BigDecimal decimal = new BigDecimal(i);
			String anyDecimal = RadixConverter.decimalToAnyDecimal(decimal, 8);
			BigDecimal result = RadixConverter.anyDecimalToDecimal(anyDecimal, 8);
			if (decimal.subtract(result).abs().compareTo(accuracy) == 1) {
				throw new ArithmeticException();
			}
		}

		// 16進数での誤差テスト
		for (double i = 0; i < 100; i += 0.1) {
			BigDecimal decimal = new BigDecimal(i);
			String anyDecimal = RadixConverter.decimalToAnyDecimal(decimal, 16);
			BigDecimal result = RadixConverter.anyDecimalToDecimal(anyDecimal, 16);
			if (decimal.subtract(result).abs().compareTo(accuracy) == 1) {
				throw new ArithmeticException();
			}
		}

		// 変換が正しいかどうかのテスト
		String str = null;
		System.out.println(str = RadixConverter.anyDecimalToAnyDecimal("3.141592", 10, 16));
		if (!str.startsWith("3.243f5f91600f345069a4df47f993d5347")) {
			throw new AssertionError();
		}
		System.out.println(str = RadixConverter.anyDecimalToAnyDecimal("3.141592", 10, 2));
		if (!str.startsWith("11.00100100001111110101111110010001")) {
			throw new AssertionError();
		}
		System.out.println(str = RadixConverter.anyDecimalToAnyDecimal("11111111.11", 2, 10));
		if (!str.equals("255.75")) {
			throw new AssertionError();
		}
		System.out.println(str = RadixConverter.anyDecimalToAnyDecimal("abcd.efgh", 32, 16));
		if (!str.equals("52d8d.73e11")) {
			throw new AssertionError();
		}
		System.out.println(str = RadixConverter.anyDecimalToAnyDecimal("xyz.XYZ", 62, 10));
		if (!str.startsWith("128995.96747759390419925480849921117115")) {
			throw new AssertionError();
		}

		System.out.println("テスト完了");
	}

}
