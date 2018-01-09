package interfaces;

public interface RandomIntGenerator{

	/**
	 * 0から指定した最大値までの疑似乱数生成
	 * @param max 最大値
	 */
	public int nextInt(int max);

}
