package net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 使用方法：匿名クラス{}.start();
 * 更新履歴：20160611 - 新規作成
 *           20160709 - 文字列送信メソッドの追加
 */
public abstract class BufferedStreamClient extends Thread{
	private String svhost;
	private int svport;

	private Socket sock;
	private BufferedInputStream sockin;
	private BufferedOutputStream sockout;
	private volatile boolean lock=false;

	private BytePacket b_Packet=new BytePacket();

	/**
	 * 機能概要：コンストラクタ
	 *           IPとポート番号を指定してサーバーに接続
	 */
	public BufferedStreamClient(String svhost,int svport){
		this.svhost=svhost;
		this.svport=svport;
	}

	/**
	 * 機能概要：サーバーに対して文字列データを送信する
	 *           バイナリデータに変換してから送信する
	 * 引数１：送信する文字列データ
	 */
	public void sendData(String str){
		sendData(str.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 機能概要：サーバーに対してバイナリデータを送信する
	 * 引数１：送信するバイナリデータ
	 */
	public void sendData(byte[] data){
		try{
			sockout.write(b_Packet.makePacket(data));
			sockout.flush();
			onSend(data);
		}catch(IOException ex){
			updateLog(ex.getStackTrace().toString());
		}
	}

	/**
	 * 専用スレッドでサーバーに接続と同時に受信待機
	 */
	@Override
	public void run(){
		try{
			sock=new Socket(svhost,svport);
			sockin=new BufferedInputStream(sock.getInputStream());
			sockout=new BufferedOutputStream(sock.getOutputStream());
			onConnect();

			byte[] bound=new byte[BytePacket.headBounds];
			byte[] buf;
			int size=0;
			int pos=0;

			boolean flg = true;
			while(flg){
				while(lock);
				lock=true;
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
						Thread.sleep(1);
					}

					onReceive(buf);
				}catch(Exception ex){
					lock=false;
					continue;
				}
				lock=false;
			}

			close();
		}catch(Exception ex){
			updateLog(ex.getStackTrace().toString());
		}
	}

	/**
	 * 機能概要：サーバーから切断して終了する
	 */
	public void close(){
		try{
			sock.close();
			sockin.close();
			sockout.close();
		}catch(IOException ex){
			updateLog(ex.getStackTrace().toString());
		}finally{
			onClose();
		}
	}

	/**
	 * 継承して使うリスナーメソッド
	 */
	protected abstract void onConnect();
	protected abstract void onSend(byte[] smsg);
	protected abstract void onReceive(byte[] rmsg);
	protected abstract void onClose();
	protected abstract void updateLog(String log);
}
