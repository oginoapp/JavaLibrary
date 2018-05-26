package math;

import java.math.BigInteger;

/**
 * 標準ライブラリ（java.lang.Math）などにありそうでない関数
 */
public class Functions {

	/**
	 * 最大公約数
	 */
	public static int gcd(int... params) {
		BigInteger[] args = new BigInteger[params.length];
		for (int i = 0; i < params.length; i++) {
			args[i] = BigInteger.valueOf(params[i]);
		}
		return gcd(args).intValue();
	}

	/**
	 * 最大公約数
	 * 非常に大きな桁数と任意の個数に対応
	 *
	 * @throws IllegalArgumentException 引数がおかしい場合
	 */
	public static BigInteger gcd(BigInteger... params) {
		// パラメータチェック
		if (params == null || params.length < 2) {
			throw new IllegalArgumentException("args == null || args.length < 2");
		}

		// 計算
		BigInteger m = params[0];
		BigInteger n = null;
		BigInteger r = null;
		for (int i = 1; i < params.length; i++) {
			n = params[i];

			do {
				r = m.remainder(n);
				m = n;
				n = r;
			} while (r.compareTo(BigInteger.ZERO) != 0);
		}

		return m;
	}

	/**
	 * 最小公倍数
	 */
	public static int lcm(int... params) {
		BigInteger[] args = new BigInteger[params.length];
		for (int i = 0; i < params.length; i++) {
			args[i] = BigInteger.valueOf(params[i]);
		}
		return lcm(args).intValue();
	}

	/**
	 * 最小公倍数
	 * 非常に大きな桁数と任意の個数に対応
	 */
	public static BigInteger lcm(BigInteger... params) {
		BigInteger gcd = gcd(params);
		BigInteger result = gcd;

		for (int i = 0; i < params.length; i++) {
			result = result.multiply(params[i].divide(gcd));
		}

		return result;
	}

}
