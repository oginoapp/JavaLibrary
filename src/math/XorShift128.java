package math;

import java.util.Random;

import interfaces.IntPRNG;

/**
 * 128bitのXorShiftによる疑似乱数の生成クラス
 * @author ogino
 * @version 20161230
 */
public class XorShift128 implements IntPRNG{
    private int x = -1;
    private int y = -1;
    private int z = -1;
    private int w = -1;

	/**
	 * 引数なしコンストラクタ
	 * seed値に乱数が設定される
	 */
	public XorShift128(){
	    Random rand = new Random();
	    this.x = rand.nextInt(Integer.MAX_VALUE);
	    this.y = rand.nextInt(Integer.MAX_VALUE);
	    this.z = rand.nextInt(Integer.MAX_VALUE);
	    this.w = rand.nextInt(Integer.MAX_VALUE);
	}

	/**
	 * コンストラクタ
	 * @param シード値1
	 * @param シード値2
	 * @param シード値3
	 * @param シード値4
	 */
	public XorShift128(int seed1, int seed2, int seed3, int seed4){
		this.x = seed1;
	    this.y = seed2;
	    this.z = seed3;
	    this.w = seed4;
	}

	/**
	 * 0から最大値までの疑似乱数生成
	 * @param max 最大値
	 * @return 生成された乱数
	 */
	@Override
	public synchronized int nextInt(int max){
		int t = x ^ (x << 11);

		x = y; y = z; z = w;
		w = (w ^ (w >> 19)) ^ (t ^ (t >> 8));

		return Math.abs(w) % (max + 1);
	}
}
