package net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 使用方法:匿名クラス{}.start();
 * 更新履歴:20160611 - 新規作成
 *          20160709 - セッションIDの生成方法の変更、文字列送信メソッドの追加
 */
public abstract class BufferedStreamServer extends Thread{
	private static List<Session> sessionList = null;
	private ServerSocket svSock = null;

	/**
	 * 機能概要：コンストラクタ（バインドするサーバーポート）
	 *           セッションリストの準備、サーバーソケットの生成
	 */
	public BufferedStreamServer(int serverPort){
		try{
			sessionList = Collections.synchronizedList(new ArrayList<Session>());
			svSock=new ServerSocket();
			svSock.bind(new InetSocketAddress("0.0.0.0",serverPort));
			onBind(serverPort);
		}catch(Exception ex){
			updateLog(ex.getStackTrace().toString());
		}
	}

	/**
	 * 機能概要：セッションIDから指定したセッションオブジェクトを取得
	 * 引数１：セッションID文字列
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
	 * 機能概要：専用スレッドで、ユーザーの接続待機をする
	 *           接続ごとにセッションオブジェクトを生成する
	 */
	@Override
	final public void run(){
		try{
			while(true){
				try{
					Socket sock=svSock.accept();
					Session session=new Session(sock);
					sessionList.add(session);
				}catch(Exception ex){
					updateLog(ex.getStackTrace().toString());
					break;
				}
			}

			svSock.close();
		}catch(Exception ex){
			updateLog(ex.getStackTrace().toString());
		}
	}

	/**
	 * 機能概要：セッションクラス
	 *           クライアントの接続ごとに生成される
	 *           クライアント情報、クライアントとの送受信を管理
	 *           セッションIDはコンストラクタで作成される
	 */
	final public class Session extends Thread{
		private Socket sock;
		private BufferedInputStream sockin;
		private BufferedOutputStream sockout;
		private String id=null;

		private BytePacket b_Packet=new BytePacket();

		public String getIP(){//外部から呼び出せる
			return sock.getInetAddress().toString().split("/")[1];
		}
		public String getHost(){//外部から呼び出せる
			return sock.getInetAddress().getHostName().toString();
		}
		public String getID(){//外部から呼び出せる
			return this.id;
		}

		/**
		 * 機能概要：セッションの生成、初期化
		 */
		public Session(Socket sock) throws UnsupportedEncodingException, IOException{
			this.sock=sock;
			this.id=String.valueOf((sock.getInetAddress().toString()+sock.getInetAddress().getHostName().toString()).hashCode());
			sockin=new BufferedInputStream(sock.getInputStream());
			sockout=new BufferedOutputStream(sock.getOutputStream());
			Session.this.start();
		}

		/**
		 * 機能概要：文字列データを送信
		 *           バイナリに変換されて送信される
		 * 引数１：送信する文字列データ
		 */
		public void sendData(String str){
			sendData(str.getBytes(StandardCharsets.UTF_8));
		}

		/**
		 * 機能概要：バイナリデータを送信
		 * 引数１：送信するバイナリデータ
		 */
		public void sendData(byte[] smsg){//外部から呼び出せる
			try{
				sockout.write(b_Packet.makePacket(smsg));
				sockout.flush();
				onSend(smsg,Session.this);
			}catch(IOException ex){
				updateLog(ex.getStackTrace().toString());
			}
		}

		@Override
		public void run(){
			try{
				onConnect(Session.this);

				byte[] bound=new byte[BytePacket.headBounds];
				byte[] buf;
				int size=0;
				int pos=0;

				boolean flg = true;
				while(flg){
					try{
						while(sockin.available()<bound.length);

						sockin.read(bound);
						buf=new byte[size=Integer.parseInt(new String(bound,"UTF-8").split(":")[1].trim())];

						pos=0;
						while(true){
							if(size-pos>sockin.available()){
								pos+=sockin.read(buf,pos,sockin.available());
							}else{
								pos+=sockin.read(buf,pos,size-pos);
								break;
							}
							Thread.sleep(1);//これがないとかたまる
						}

						onReceive(buf,Session.this);
					}catch(Exception ex){
						continue;
					}
				}

			}catch(Exception ex){
				updateLog(ex.getStackTrace().toString());
			}finally{
				Session.this.close();
			}
		}

		/**
		 * 機能概要：セッションを閉じる
		 *           セッションリストからも削除される
		 */
		public void close(){
			try{
				sock.close();
				sockin.close();
				sockout.close();
			}catch(IOException ex){
				updateLog(ex.getStackTrace().toString());
			}finally{
				sessionList.remove(Session.this);
				onClose(Session.this);
			}
		}
	}

	/**
	 * 機能概要：継承して使うリスナーメソッド
	 *           サーバーのイベント発生時に呼ばれる
	 */
	protected abstract void onBind(int port);
	protected abstract void onConnect(Session session);
	protected abstract void onSend(byte[] smsg,Session session);
	protected abstract void onReceive(byte[] rmsg,Session session);
	protected abstract void onClose(Session session);
	protected abstract void updateLog(String log);
}
