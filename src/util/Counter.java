package util;

/**
 * 更新履歴：
 * 20160619 - 初期化メソッドの追加、コンストラクタから初期化メソッドを呼び出すようにする。
 * 20160622 - 待機・再開・停止メソッドの追加。クロック処理内に制御文を追加。
 */
public abstract class Counter extends Thread{

	/* カウント回数の無限指定（-1以下の定数のみ代入可） */
	public static final int INFINITY = -1;// <= -1

	private long max;
	private long interval;
	private boolean countDown;
	private boolean err_flg;
	private boolean loop;
	private boolean count_infinity;
	private boolean wait;
	private long count;
	private StopWatch sw;

	/* コンストラクタ */
	public Counter(){
		initialize(100, 1000, true);
	}

	/* コンストラクタ */
	public Counter(long max){
		initialize(max, 1000, true);
	}

	/* コンストラクタ */
	public Counter(long max, long interval){
		initialize(max, interval, true);
	}

	/* コンストラクタ */
	public Counter(long max, long interval, boolean countDown){
		initialize(max, interval, countDown);
	}

	/**
	 * 機能概要：コンストラクタから呼ばれる初期化メソッド
	 * 引数１：クロック回数
	 * 引数２：クロック間隔（ミリ秒）
	 * 引数３：カウントダウンかどうか
	 */
	public void initialize(long max, long interval, boolean countDown){
		this.max = max;
		this.interval = interval;
		this.countDown = countDown;
		this.count = (countDown ? max : 0);
		this.err_flg = false;
		this.loop = true;
		this.count_infinity = (max == Counter.INFINITY ? true : false);
		this.wait = false;
		this.sw = new StopWatch();
	}

	/**
	 * 機能概要：クロックを一時停止する
	 */
	public void clockWait(){
		this.wait = true;
	}

	/**
	 * 機能概要：一時停止したクロックを再開させる
	 */
	public void clockResume(){
		this.wait = false;
	}

	/**
	 * 機能概要：クロックを停止する
	 */
	public void clockStop(){
		this.loop = false;
		this.wait = false;
	}

	/* クロック処理 */
	@Override
	public void run(){
		onStart();
		clock: while(loop){
			try{
				sw.start();
				onCount(count);
				sw.stop();

				//オーバーヘッド計算
				long sleep = interval - sw.elapsed();
				Thread.sleep(sleep >= 0 ? sleep : 0);

				//待機処理
				while(wait){
					Thread.sleep(1);
				}

				//停止
				if(!loop){
					break;
				}

				if(!count_infinity){
					if(countDown){
						if(count <= 0){
							loop = false;
						}else{
							count--;
						}
					}else{
						if(count >= max){
							loop = false;
						}else{
							count++;
						}
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
				err_flg = true;
				loop = false;
				break clock;
			}
		}
		onStop(err_flg);
	}

	/**
	 * 機能概要：クロック開始時に呼ばれるリスナーメソッド
	 */
	protected abstract void onStart();

	/**
	 * 機能概要：クロックするたびに毎回呼ばれるリスナーメソッド
	 * 引数１：カウントアップ=現在のクロック回数、カウントダウン=残りクロック回数
	 */
	protected abstract void onCount(long count);

	/**
	 * 機能概要：クロック終了時に呼ばれるリスナーメソッド
	 * 引数１：エラーによる終了かどうか
	 */
	protected abstract void onStop(boolean err_flg);
}
