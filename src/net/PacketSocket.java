package net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import interfaces.Encryptor;
import security.CaesarCipher;

/**
 * @クラス説明：パケットを送受信する
 * @使い方：PacketSocket ps = new PacketSocket(sock, 12345678);
 *          ps.writeData("test");
 *          System.out.println(ps.readData());
 * @更新履歴：
 * 20160730 - 新規作成
 * 20160808 - sockの委譲、暗号化ライブラリの変更、
 * 文字列とバイト配列の両方の送受信に対応(それぞれ排他処理)
 * 20160917 - 文字コードを"UTF-8"に設定
 */
public class PacketSocket {
	/* 設定値 */
	public static final int L_LENGTH = 6;
	public static final int L_CHECKSUM = 3;
	public static final int L_IV = 6;

	private Socket sock = null;
	private BufferedInputStream sockin = null;
	private BufferedOutputStream sockout = null;
	private Encryptor enctyptor = null;

	private final Object sendLock = new Object();
	private final Object reveiveLock = new Object();

	/**
	 * @機能概要：コンストラクタ
	 * @引数１：Socketのインスタンス
	 * @引数２：暗号化キー
	 * @see CaesarCipher
	 */
	public PacketSocket(Socket sock, Encryptor enctyptor){
		try{
			this.sock = sock;
			this.sockin = new BufferedInputStream(sock.getInputStream());
			this.sockout = new BufferedOutputStream(sock.getOutputStream());
		}catch(IOException ex){
			ex.printStackTrace();
		}
		this.enctyptor = enctyptor;
	}

	/**
	 * @機能概要：送信用メソッド
	 * @引数１：送信する文字列
	 */
	public void writeString(String str)throws Exception{
		synchronized(sendLock){
			writeData(str.getBytes("UTF-8"));
		}
	}

	/**
	 * @機能概要：送信用メソッド
	 * @引数１：送信するバイト配列
	 */
	public void writeData(byte[] data) throws Exception{
		synchronized(sendLock){
			byte[] b_length = null;
			byte[] b_checksum = null;
			byte[] b_iv = null;

			//初期化ベクトル生成
			int iv = new Random().nextInt(899999) + 100000;
			enctyptor.setEncryptIv(iv);

			//暗号化
			data = enctyptor.encrypt(data);

			//チェックサム
			int checksum = 0;
			for(int i=0; i<data.length; i++){
				checksum = checksum & 0xFF;
				checksum += data[i];
			}

			//ヘッダーの作成
			b_length = (fill(String.valueOf(data.length), L_LENGTH)).getBytes();
			b_checksum = (fill(String.valueOf(checksum), L_CHECKSUM)).getBytes();
			b_iv = (fill(String.valueOf(iv), L_CHECKSUM)).getBytes();

			//ヘッダーとデータの連結
			byte[] packet = new byte[b_length.length + b_checksum.length + b_iv.length + data.length];
			int pos = 0;
			System.arraycopy(b_length, 0, packet, pos, b_length.length);
			System.arraycopy(b_checksum, 0, packet, pos += b_length.length, b_checksum.length);
			System.arraycopy(b_iv,0, packet, pos += b_checksum.length, b_iv.length);
			System.arraycopy(data,0, packet, pos += b_iv.length, data.length);

			//送信
			sockout.write(packet);
			sockout.flush();
		}
	}

	/**
	 * @throws Exception
	 * @機能概要：受信用メソッド
	 * @戻り値：受信した文字列
	 */
	public String readString() throws Exception{
		synchronized(reveiveLock){
			return new String(readData(), "UTF-8");
		}
	}

	/**
	 * @機能概要：受信用メソッド
	 * @戻り値：受信したバイト配列
	 */
	public byte[] readData() throws Exception{
		synchronized(reveiveLock){
			byte[] b_length = new byte[L_LENGTH];
			byte[] b_checksum = new byte[L_CHECKSUM];
			byte[] b_iv = new byte[L_IV];
			byte[] buf;
			int size = 0;
			int checksum = 0;
			int iv = 0;
			int pos;

			//データの長さの取得
			while(sockin.available()<b_length.length);
			sockin.read(b_length);
			size = Integer.parseInt(new String(b_length,"UTF-8").trim());

			//チェックサムの値を取得
			while(sockin.available()<b_checksum.length);
			sockin.read(b_checksum);
			checksum = Integer.parseInt(new String(b_checksum,"UTF-8").trim());

			//初期化ベクトルを取得
			while(sockin.available()<b_iv.length);
			sockin.read(b_iv);
			iv = Integer.parseInt(new String(b_iv,"UTF-8").trim());

			//長さの分だけデータを読み込む
			buf = new byte[size];
			pos = 0;
			while(true){
				if(size - pos > sockin.available()){
					pos += sockin.read(buf,pos,sockin.available());
				}else{
					pos += sockin.read(buf,pos,size-pos);
					break;
				}
				try{Thread.sleep(1);}catch(InterruptedException e){}
			}

			//チェックサム
			int c = 0;
			for(int i=0; i<buf.length; i++){
				c = c & 0xFF;
				c += buf[i];
			}
			if(c != checksum){
				throw new IOException("checksum error");
			}

			//文字列に変換して復号化
			enctyptor.setEncryptIv(iv);
			buf = enctyptor.decrypt(buf);

			return buf;
		}
	}

	/**
	 * @機能概要：ソケットとストリームを閉じる
	 */
	public void close() throws IOException{
		if(this.sock != null) this.sock.close();
		if(this.sockin != null) this.sockin.close();
		if(this.sockout != null) this.sockout.close();
	}

	/**
	 * @機能概要：指定した長さになるまで空白で埋めて固定長にする
	 *            既に超えている場合はそのまま返す
	 * @引数１：文字列
	 * @引数２：指定した長さ
	 */
	private String fill(String val, int len){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i<len - val.length(); i++){
			sb.append(" ");
		}
		return sb.append(val).toString();
	}

}
