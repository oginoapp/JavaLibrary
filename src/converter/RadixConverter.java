package converter;

import java.math.BigDecimal;

/**
 * 基数変換
 */
public class RadixConverter {

	/**
	 * n進数⇒10進数
	 * 小数点込みでも計算できる
	 * @param n進数文字列
	 * @param 基数
	 * @return 10進数:BigDecimal
	 */
	public static BigDecimal anyDecimalToDecimal(String anyDecimal, int radix) {
		if (anyDecimal == null || anyDecimal.isEmpty())
			throw new NullPointerException("anyDecimal == null || anyDecimal.isEmpty()");

		// 負の数かどうか
		boolean minus = false;
		if (anyDecimal.charAt(0) == '-') {
			minus = true;
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

		return minus ? decimal.negate() : decimal;
	}

}
