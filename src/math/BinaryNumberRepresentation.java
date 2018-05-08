package math;

/**
 * コンピュータによる数値表現関連
 */
public class BinaryNumberRepresentation {

	/**
	 * 仮数部nビットの浮動小数点数表現の有効桁数を求める
	 * n: 23 ⇒ 有効桁数: 6
	 *
	 * @param n 仮数部のビット数
	 * @return 有効桁数
	 */
	public static int significantDigits(int n) {
		double significantDigits = Math.pow(2, n);

		double tmp = 0;
		double exp = 0;

		double increment = 1;

		while (true) {
			tmp = Math.pow(10, exp);
			if (increment <= 0.000000000000001) {
				break;
			}

			if (tmp >= significantDigits) {
				exp -= increment;
				increment /= 2;
			}
			exp += increment;
		}

		return (int)Math.floor(exp);
	}

}
