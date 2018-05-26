package util;

/**
 * ストップウォッチ
 */
public class StopWatch {

	public enum TimeUnit {
		Nanos,
		Micros,
		Millis,
		Second,
		Minute,
		Hour,
		Day
	}

	private Object lock = new Object();
	private long start = 0;
	private long end = 0;
	private boolean run = false;

	/**
	 * 状態をリセットして計測を開始する
	 */
	public void start() {
		if (run)
			return;
		synchronized (lock) {
			start = System.nanoTime();
			run = true;
		}
	}

	/**
	 * 経過時間を記録して計測を停止する
	 */
	public void stop() {
		if (!run)
			return;
		synchronized (lock) {
			end = System.nanoTime();
			run = false;
		}
	}

	/**
	 * 状態をリセットせずに計測を再開する
	 */
	public void resume() {
		if (run)
			return;
		synchronized (lock) {
			start = System.nanoTime();
			run = true;
		}
	}

	/**
	 * 経過時間を取得する（ミリ秒）
	 */
	public long elapsed() {
		return elapsed(TimeUnit.Millis);
	}

	/**
	 * 経過時間を取得する
	 *
	 * @param timeUnit 時間の単位
	 */
	public long elapsed(TimeUnit timeUnit) {
		if (run)
			end = System.nanoTime();

		double elapsed = end - start;

		switch (timeUnit) {
		case Day:
			elapsed /= 24D;
		case Hour:
			elapsed /= 60D;
		case Minute:
			elapsed /= 60D;
		case Second:
			elapsed /= 1000D;
		case Millis:
			elapsed /= 1000D;
		case Micros:
			elapsed /= 1000D;
		case Nanos:
			break;
		}

		return (long)elapsed;
	}

}