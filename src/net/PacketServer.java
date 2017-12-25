package net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @使用方法:PacketServer sv = new PacketServer{}.start();
 *           sv.getSessionByID(sessionID).sendData("test");
 * @更新履歴:20160730 - 新規作成
 *           20160808 - SocketをPacketSocketに委譲
 */
public abstract class PacketServer extends Thread{
	private static String[] clientIDArray = null;

	private static List<Session> sessionList = null;	//クライアント一覧
	private ServerSocket svSock = null;					//ソケット
	private int cipherKey;		//暗号キー

	/**
	 * @機能概要：コンストラクタ
	 *           セッションリストの準備、サーバーソケットの生成
	 * @引数１：バインドするサーバーのローカルポート
	 */
	public PacketServer(int serverPort, int cipherKey){
		try{
			this.cipherKey = cipherKey;

			sessionList = Collections.synchronizedList(new ArrayList<Session>());
			svSock = new ServerSocket();
			svSock.bind(new InetSocketAddress("0.0.0.0", serverPort));
			onBind(serverPort);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * @機能概要：クライアントID配列をセットする
	 * @引数１：アクセス許可をするクライアントIDの配列
	 */
	public static void setClientIDArray(String[] clientIDArray){
		PacketServer.clientIDArray = clientIDArray;
	}

	/**
	 * @機能概要：セッションIDから指定したセッションオブジェクトを取得
	 * @引数１：セッションID文字列
	 */
	public Session getSessionByID(String sessionID){
		for(Session session : sessionList){
			if(session.getID().equals(sessionID)){
				return session;
			}
		}
		return null;
	}

	/**
	 * @機能概要：セッションIDの一覧を取得
	 * @戻り値：セッションIDの配列（String[]）
	 */
	public String[] getSessionIDArray(){
		String[] array = new String[sessionList.size()];
		for(int i=0; i<sessionList.size(); i++){
			array[i] = sessionList.get(i).getID();
		}
		return array;
	}

	/**
	 * @機能概要：専用スレッドで、ユーザーの接続待機をする
	 *           接続ごとにセッションオブジェクトを生成する
	 */
	@Override
	final public void run(){
		try{
			while(true){
				try{
					//接続待機
					Socket sock = svSock.accept();

					//接続発生
					Session session = new Session(sock);

					//認証
					if(session.authentication()){
						session.start();
						sessionList.add(session);
					}else{
						System.err.println("deny: " + sock.getInetAddress().toString());
					}
				}catch(Exception ex){
					ex.printStackTrace();
					break;
				}
			}

			svSock.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * @機能概要：セッションクラス
	 *           クライアントの接続ごとに生成される
	 *           クライアント情報、クライアントとの送受信を管理
	 *           セッションIDはコンストラクタで作成される
	 */
	final public class Session extends Thread{
		private Socket sock = null;
		private String sessionID = null;

		private PacketSocket packetSock = null;

		public String getIP(){//外部から呼び出せる
			return sock.getInetAddress().toString().split("/")[1];
		}
		public String getHost(){//外部から呼び出せる
			return sock.getInetAddress().getHostName().toString();
		}
		public String getID(){//外部から呼び出せる
			return this.sessionID;
		}

		/**
		 * @機能概要：セッションの初期化
		 */
		public Session(Socket sock){
			this.sock = sock;
			this.packetSock = new PacketSocket(sock, PacketServer.this.cipherKey);
		}

		/**
		 * @機能概要：クライアントIDの認証
		 */
		public boolean authentication(){
			boolean allow = false;

			try{
				this.sessionID = packetSock.readString();

				//使用可能かのチェック
				for(int i=0; i<PacketServer.clientIDArray.length; i++){
					if(PacketServer.clientIDArray[i].equals(this.sessionID)){
						allow = true;
					}
				}

				//重複のチェック
				for(int i=0; i<sessionList.size(); i++){
					if(sessionList.get(i).getID().equals(this.sessionID)){
						allow = false;
					}
				}

			}catch(Exception ex){
				ex.printStackTrace();
			}

			return allow;
		}

		/**
		 * @機能概要：文字列データを送信
		 *           バイナリに変換されて送信される
		 * @引数１：送信する文字列データ
		 */
		public void sendData(String str){
			try{
				packetSock.writeString(str);
				onSend(str,Session.this);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}

		@Override
		public void run(){
			try{
				onConnect(Session.this);

				boolean flg = true;
				while(flg){
					try{
						//受信
						String data = packetSock.readString();
						onReceive(data,Session.this);
					}catch(NumberFormatException ex){
						continue;
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}

			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				Session.this.close();
			}
		}

		/**
		 * @機能概要：セッションを閉じる
		 *           セッションリストからも削除される
		 */
		public void close(){
			try{
				packetSock.close();
			}catch(IOException ex){
				ex.printStackTrace();
			}finally{
				sessionList.remove(Session.this);
				onClose(Session.this);
			}
		}
	}

	/**
	 * @機能概要：継承して使うリスナーメソッド
	 *           サーバーのイベント発生時に呼ばれる
	 */
	protected abstract void onBind(int port);
	protected abstract void onConnect(Session session);
	protected abstract void onSend(String smsg,Session session);
	protected abstract void onReceive(String rmsg,Session session);
	protected abstract void onClose(Session session);
	protected abstract void updateLog(String log);
}
