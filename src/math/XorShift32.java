package math;

import interfaces.IntPRNG;

/**
 * @クラス説明：疑似乱数を生成する
 * @使い方：XorShift x = new XorShift(12345678);
 */
public class XorShift32 implements IntPRNG{
	private int seed = 0;

	/**
	 * @deprecated
	 */
	private Integer increment = null;

	/**
	 * @deprecated
	 */
	private static final int max_seed = Integer.MAX_VALUE;

	/**
	 * @機能概要：コンストラクタでシード値をセットする
	 * @引数１：シード値(int)
	 */
	public XorShift32(int seed){
		this.seed = seed;
	}

	/**
	 * @機能概要：コンストラクタでシード値と増加量をセットする
	 * @引数１：シード値(int)
	 * @引数２：シード値の増加量(int)
	 *
	 * @deprecated 引数1つのコンストラクタを推奨
	 */
	public XorShift32(int seed, int increment){
		this.seed = seed;
		this.increment = increment;
	}

	/**
	 * @機能概要：xorshiftのアルゴリズムで疑似乱数を生成する
	 * @引数１：最大値
	 * @戻り値：0から指定した最大値までの疑似乱数
	 */
	@Override
	public synchronized int nextInt(int max){
		if(increment != null) {
			if(max_seed - this.seed > this.increment){
				this.seed += this.increment;
			}else{
				this.seed = 1;
			}
		}

		int num = this.seed;
		num = num ^ (num << 13);
		num = num ^ (num << 17);
		num = num ^ (num << 15);
		if(increment == null) this.seed = num;
		num %= (max + 1);

		return num >= 0 ? num : -num;
	}
}
