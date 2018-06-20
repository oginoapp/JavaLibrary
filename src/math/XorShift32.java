package math;

import interfaces.IntPRNG;

/**
 * 疑似乱数を生成する
 * IntPRNG prng = new XorShift(12345678);
 * int randomInteger = prng.nextInt(123);
 */
public class XorShift32 implements IntPRNG {

	private int seed = 0;

	/**
	 * コンストラクタでシード値をセットする
	 * @param seed シード値(int)
	 */
	public XorShift32(int seed){
		this.seed = seed;
	}

	/**
	 * xorshiftのアルゴリズムで疑似乱数を生成する
	 * @param n 最大値+1
	 * @return 0から指定した最大値までの疑似乱数
	 */
	@Override
	public synchronized int nextInt(int n){
		int num = this.seed;
		num = num ^ (num << 13);
		num = num ^ (num >> 17);
		num = num ^ (num << 15);
		this.seed = num;

		num %= n;

		return Math.abs(num);
	}
}
