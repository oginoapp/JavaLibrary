package net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * スレッドプールを使った非同期のポートスキャンを行う
 */
public class PortScanner {

	private int timeout = 1000;

	private boolean run = false;

	/**
	 * ポートスキャンを開始する
	 * 0～1023のウェルノウンポートのみをスキャンする
	 * @param host 対象
	 * @return 開いているポートのリスト
	 */
	public List<Integer> scan(final String host) {
		return scan(host, 0, 1023);
	}

	/**
	 * ポートスキャンを開始する
	 * @param host 対象
	 * @param start 開始ポート
	 * @param end 終了ポート
	 * @return 開いているポートのリスト
	 */
	public List<Integer> scan(final String host, int start, int end) {
		if (start < 0 || end > 65535) {
			System.err.println("start < 0 || end > 65535");
			return null;
		}

		// 結果
		final List<Integer> portList = Collections.synchronizedList(new ArrayList<Integer>());

		// スキャンするポート一覧
		final Deque<Integer> ports = new ArrayDeque<>();
		for (int i = start; i <= end; i++) {
			ports.add(i);
		}

		// スレッドプールの生成
		int threads = end - start + 1;
		final ExecutorService executor = Executors.newFixedThreadPool(threads);

		run = true;
		try {
			while (run) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						Integer port = null;

						// キューからポート番号を取り出す
						synchronized (ports) {
							port = ports.poll();
							// キューがなくなったら終了
							if(port == null) {
								run = false;
								return;
							}
						}

						// スキャン
						SocketAddress address = new InetSocketAddress(host, port);
						try (Socket sock = new Socket()) {
							sock.connect(address, timeout);
							// open
							portList.add(port);
						} catch (IOException ex) {
							// close
						}
					}
				});
			}
		} finally {
			// 全タスク終了まで待機
			try {
				executor.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			executor.shutdown();
		}

		return portList;
	}

}
