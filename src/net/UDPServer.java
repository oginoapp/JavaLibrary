package net;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

public abstract class UDPServer extends Thread{
	private int localPort = 55560;
	private int bufferSize = 1024;

	//受信
	private DatagramSocket recSock = null;
	private byte[] recBuf = new byte[UDPServer.this.bufferSize];
	private DatagramPacket recPacket = new DatagramPacket(recBuf, recBuf.length);

	/**
	 * コンストラクタ
	 * 送受信用オブジェクトの初期化
	 */
	public UDPServer(int localPort, int bufferSize){
		//初期化
		if(localPort <= 0 || localPort >= 65535){
			printLog("alert:localPort <= 0 || localPort >= 65535");
		}else
		if(bufferSize <= 0 || bufferSize >= Integer.MAX_VALUE){
			printLog("alert:bufferSize <= 0 || bufferSize >= Integer.MAX_VALUE");
		}else{
			this.localPort = localPort;
			this.bufferSize = bufferSize;
		}

		//送受信オブジェクトの初期化
		try{
			recSock = new DatagramSocket(UDPServer.this.localPort);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * サーバースレッドの起動
	 */
	@Override
	public void run(){
		try{
			onStart(recSock.getLocalPort());

			//受信処理
			while(true){
				recSock.receive(recPacket);
				byte[] recData = Arrays.copyOfRange(recBuf, 0, recPacket.getLength());
				onReceive(recData, (InetSocketAddress)recPacket.getSocketAddress());
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//プログラム終了時にクローズ
			if(recSock != null){
				recSock.close();
			}
		}
	}

	/**
	 * String型のデータを指定したソケットに送信する
	 * sendData(byte[])を内部で呼び出す
	 */
	public void sendData(String data, InetSocketAddress remoteAddress){
		try{
			sendData(data.getBytes("UTF-8"), remoteAddress);
		}catch(UnsupportedEncodingException ex){
			ex.printStackTrace();
		}
	}

	/**
	 * バイナリデータを指定したソケットに送信する
	 */
	public void sendData(byte[] data, InetSocketAddress remoteAddress){
		DatagramSocket sendSock = null;//後でフィールドに移植？
		try{
			sendSock = new DatagramSocket();//後でフィールドに移植？
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, remoteAddress);
			sendSock.send(sendPacket);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * リスナー
	 * onStart：受信スレッドの開始時に呼び出される(使用したローカルポート番号)
	 * onReceive：受信時に受信したバイナリを取得できる(受信したバイナリデータ)
	 * printLog：ログ出力
	 * 継承してオーバーライドして使う
	 */
	protected abstract void onStart(int localPort);
	protected abstract void onReceive(byte[] data, InetSocketAddress senderAddress);
	protected abstract void printLog(Object log);
}
