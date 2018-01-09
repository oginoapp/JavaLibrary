package math;


import java.util.Random;

import interfaces.RandomIntGenerator;

/**
 * @version 20161230
 * @author ogino
 * 疑似乱数をXorShiftによって生成し、同じseed値を与えられた場合に同じ乱数列を生成する
 * seed値を32bit整数の任意の長さの配列で渡す
 * seed値を渡さない場合、ランダムな128bitのseed値が生成される
 */
public class XorShiftVariable implements RandomIntGenerator{
	private int[] seed = null;

	/**
	 * 引数なしコンストラクタ
	 * seed値に乱数が設定され、生成する疑似乱数は128ビット周期になる
	 */
	public XorShiftVariable(){
	    Random rand = new Random();
	    this.seed = new int[4];
	    this.seed[0] = rand.nextInt(Integer.MAX_VALUE);
	    this.seed[1] = rand.nextInt(Integer.MAX_VALUE);
	    this.seed[2] = rand.nextInt(Integer.MAX_VALUE);
	    this.seed[3] = rand.nextInt(Integer.MAX_VALUE);
	}

	/**
	 * コンストラクタ
	 * @param シード値配列
	 */
	public XorShiftVariable(int[] seed){
		this.seed = seed.clone();
	}

	/**
	 * 0から最大値までの疑似乱数生成
	 * @param max 最大値
	 * @return 生成された乱数
	 */
	@Override
	public synchronized int nextInt(int max){
		int result = 0;

		for(int i = 0, t = seed[0] ^ (seed[0] << 11); i < seed.length; i++){
			if(i < seed.length - 1){
				seed[i] = seed[i + 1];
			}else{
				seed[i] = (seed[i] ^ (seed[i] >> 19)) ^ (t ^ (t >> 8));
				result = Math.abs(seed[i]) % (max + 1);
			}
		}

		return result;
	}
}
