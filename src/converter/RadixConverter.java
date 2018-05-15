package converter;

import java.math.BigDecimal;

/**
 * 基数変換
 */
public class RadixConverter {

	/**
	 * n進数⇒10進数 (2 <= n <= 36)
	 * 小数点を含む場合、nは2の累乗数のみ
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

		// 整数部分
		if (chunk.length > 0) {
			if (chunk[0].length() == 0)
				throw new NumberFormatException("numberPart.length() == 0");

			// 計算
			char[] numberPart = chunk[0].toCharArray();
			for (int i = numberPart.length - 1, exponent = 0; i >= 0; i--, exponent++) {
				BigDecimal num1 = new BigDecimal(Integer.parseInt(String.valueOf(numberPart[i]), radix));
				BigDecimal num2 = new BigDecimal(Math.pow(radix, exponent));
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
				BigDecimal num1 = new BigDecimal(Integer.parseInt(String.valueOf(fractionalPart[i]), radix));
				BigDecimal num2 = new BigDecimal(Math.pow(radix, exponent));
				decimal = decimal.add(num1.divide(num2));
			}
		}

		return negative ? decimal.negate() : decimal;
	}

	/**
	 * 10進数⇒n進数
	 *
	 * @param decimal 10進数
	 * @param radix 基数
	 * @param n進数文字列
	 */
	public static String decimalToAnyDecimal(BigDecimal decimal, int radix) {
		return decimalToAnyDecimal(decimal, radix, 128);
	}

	/**
	 * 10進数⇒n進数
	 *
	 * @param decimal 10進数
	 * @param radix 基数
	 * @param scale 小数点以下の桁数
	 * @param n進数文字列
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

			result = result.insert(0, Integer.toString(Integer.parseInt(mod.toString()), radix));
		}
		mod = numberPart.remainder(base);
		result = result.insert(0, Integer.toString(Integer.parseInt(mod.toString()), radix));

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
			result.append(Integer.toString(n, radix));
		}

		if (negative) result.insert(0, "-");

		return result.toString();
	}

	/**
	 * 計算誤差をテストする
	 */
	public static void test() {
		// 許容誤差
		BigDecimal accuracy = new BigDecimal(0.000000000000001);

		// 2進数でのテスト
		for (double i = 0; i < 100; i += 0.1) {
			BigDecimal decimal = new BigDecimal(i);
			String anyDecimal = RadixConverter.decimalToAnyDecimal(new BigDecimal(i), 2);
			BigDecimal result = RadixConverter.anyDecimalToDecimal(anyDecimal, 2);
			if (decimal.subtract(result).abs().compareTo(accuracy) == 1) {
				throw new ArithmeticException();
			}
		}

		// 8進数でのテスト
		for (double i = 0; i < 100; i += 0.1) {
			BigDecimal decimal = new BigDecimal(i);
			String anyDecimal = RadixConverter.decimalToAnyDecimal(new BigDecimal(i), 8);
			BigDecimal result = RadixConverter.anyDecimalToDecimal(anyDecimal, 8);
			if (decimal.subtract(result).abs().compareTo(accuracy) == 1) {
				throw new ArithmeticException();
			}
		}

		// 16進数でのテスト
		for (double i = 0; i < 100; i += 0.1) {
			BigDecimal decimal = new BigDecimal(i);
			String anyDecimal = RadixConverter.decimalToAnyDecimal(new BigDecimal(i), 16);
			BigDecimal result = RadixConverter.anyDecimalToDecimal(anyDecimal, 16);
			if (decimal.subtract(result).abs().compareTo(accuracy) == 1) {
				throw new ArithmeticException();
			}
		}
	}

}
