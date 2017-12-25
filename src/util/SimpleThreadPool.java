package util;

/**
 * @see スレッドプール
 * @see ブロッキングされるような処理の連続呼び出しを高速化する目的で使う
 * @author ogino
 * @version 20170413
 */
public abstract class SimpleThreadPool{

	/* スレッドクラス */
    public class ManagedThread extends Thread{

        private int index = -1;
        private boolean running = false;

        public boolean keepRunning = false;

        public int getIndex(){
        	return this.index;
        }

    }

    /* スレッドプール */
    private ManagedThread[] pool = null;

    /**
     * @see コンストラクタ - オーバーロード
     */
    public SimpleThreadPool(){
    	this(4);
    }

    /**
     * @see コンストラクタ
     * @param size:生成するスレッド数
     */
    public SimpleThreadPool(int size){
    	pool = new ManagedThread[size];
    	for(int i = 0; i < pool.length; i++){
    		final int index = i;

    		//スレッドを生成
    		pool[index] = new ManagedThread(){
    			@Override
    			public void run(){
    				SimpleThreadPool.this.run(pool[index]);
    				pool[index].running = pool[index].keepRunning | false;
    			}
    		};

    		pool[index].index = index;
    	}
    }

    /**
     * @see スレッド処理を開始する - オーバーロード
     */
    public void execute(){
    	execute(-1);
    }

    /**
     * @see スレッド処理を開始する
     * @param timeOutMillis: ブロッキング処理のタイムアウトをミリ秒で設定する
     *                      （0未満の場合は処理が終わるまでブロッキングする）
     */
    public void execute(int timeOutMillis){

    	//設定時間が0未満の場合は終わるまで待つ
    	boolean await = timeOutMillis < 0;

    	//各スレッドを開始する
    	for(int i = 0; i < pool.length; i++){
    		pool[i].running = true;
    		pool[i].start();
    	}

    	//ブロッキング処理
    	long time = System.currentTimeMillis();
    	while(true){
    		//スレッド処理がすべて終了した場合は抜ける
    		boolean running = false;
    		for(int i = 0; i < pool.length; i++){
    			running |= pool[i].running;
    		}
    		if(!running) break;

    		//時間オーバーした場合は抜ける
    		long now = System.currentTimeMillis();
    		if((now - time) > timeOutMillis && !await) break;
    	}

    }

    /**
     * @see 各スレッド処理のコールバック
     * @param index: スレッドのインデックス
     */
    public abstract void run(ManagedThread thread);
}
