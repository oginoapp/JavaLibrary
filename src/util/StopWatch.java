package util;

/**
 * ストップウォッチ
 */
public class StopWatch{

	private long start = 0;
	private long elapsed = 0;
	private boolean run = false;

	/**
	 * 状態をリセットして計測を開始する
	 */
	public void start(){
		start = System.nanoTime();
		elapsed = 0;
		run = true;
	}

	/**
	 * 経過時間を記録して計測を停止する
	 */
	public void stop(){
		if(!run) return;
		elapsed += System.nanoTime() - start;
		run = false;
	}

	/**
	 * 状態をリセットせずに計測を再開する
	 */
	public void resume(){
		if(run) return;
		start = System.nanoTime();
		run = true;
	}

	/**
	 * 経過時間(ミリ秒)を取得する
	 */
	public long getElapsedMillis(){
		return elapsed / 1000000;
	}

}
