package net;

import java.net.Socket;

import interfaces.ByteArrayEncryptor;

/**
 * @使用方法:PacketClient cl = new PacketClient{}.start();
 *           cl.sendData("test");
 * @更新履歴:20160730 - 新規作成
 *           20160730 - コンストラクタ引数にクライアントIDを追加
 *           20160808 - SocketをPacketSocketに委譲
 */
public abstract class PacketClient extends Thread{
	private String svhost;		//リモートホスト
	private int svport;			//リモートポート
	private String clientID;	//クライアントID
	private ByteArrayEncryptor encryptor;		//暗号オブジェクト

	private PacketSocket packetSock = null;	//ソケット

	/**
	 * @機能概要：コンストラクタ
	 * @引数１：リモートIPアドレス
	 * @引数２：リモートポート
	 * @引数３：クライアントID（クライアント同士で重複しない任意値）
	 */
	public PacketClient(String svhost, int svport, String clientID, ByteArrayEncryptor encryptor){
		this.svhost = svhost;
		this.svport = svport;
		this.clientID = clientID;
		this.encryptor = encryptor;
	}

	/**
	 * @機能概要：サーバーに対して文字列データを送信する
	 *           バイナリデータに変換してから送信する
	 * @引数１：送信する文字列データ
	 */
	public void sendData(String str){
		try{
			packetSock.writeString(str);
			onSend(str);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * @機能概要：専用スレッドでサーバーに接続と同時に受信待機
	 */
	@Override
	final public void run(){
		try{
			packetSock = new PacketSocket(new Socket(svhost,svport), this.encryptor);
			sendData(clientID);
			onConnect();

			boolean flg = true;
			while(flg){
				try{
					//受信
					String data = packetSock.readString();
					onReceive(data);
				}catch(NumberFormatException ex){
					continue;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			this.close();
		}
	}

	/**
	 * 機能概要：サーバーから切断して終了する
	 */
	public void close(){
		try{
			packetSock.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			packetSock = null;
			onClose();
		}
	}

	/**
	 * 継承して使うリスナーメソッド
	 */
	protected abstract void onConnect();
	protected abstract void onSend(String smsg);
	protected abstract void onReceive(String rmsg);
	protected abstract void onClose();
	protected abstract void updateLog(String log);
}
