package security;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import math.XorShift32;

/**
 * @クラス説明：バーナム暗号
 * @使用方法：
 * MyCrypt mc = new MyCrypt(12345678, random.nextInt(999999));
 * mc.encrypt(data);
 * mc.decrypt(data);
 * @更新履歴：
 * 20160803 - 新規作成
 * 20160901 - IVを含む暗号化、複合化
 */
public class MyCrypt32 {
	private int cipherKey;
	private int iv;
	private int iv_min = Integer.parseInt("100000", 16);
	private int iv_max = Integer.parseInt("FFFFFF", 16);
	private Random rand = new Random();

	/**
	 * @機能概要：コンストラクタ
	 * @引数１：int型の暗号化キー（8桁推奨）
	 */
	public MyCrypt32(int cipherKey){
		this(cipherKey, 0);
	}

	/**
	 * @機能概要：コンストラクタ
	 * @引数１：int型の暗号化キー（8桁推奨）
	 * @引数２：int型の初期化ベクトル(乱数推奨)
	 */
	public MyCrypt32(int cipherKey, int iv){
		this.cipherKey = cipherKey;
		this.iv = iv;
	}

	/**
	 * @機能概要：初期化ベクトルをセットする
	 * @引数１：int型の値(乱数推奨)
	 */
	public void setIV(int iv){
		this.iv = iv;
	}

	/**
	 * @機能概要：IVを生成して文字列を暗号化する
	 * @引数１：暗号化する文字列
	 * @戻り値：暗号化された16進数文字列（IVを含む）
	 */
	public String encryptWithIV(String strData) throws UnsupportedEncodingException{
		int iv = rand.nextInt(iv_max - iv_min) + iv_min;
		return Integer.toHexString(iv) + encrypt(strData, iv);
	}

	/**
	 * @機能概要：文字列を暗号化する
	 * @引数１：暗号化する文字列
	 * @戻り値：暗号化された16進数文字列
	 */
	public String encrypt(String strData) throws UnsupportedEncodingException{
		return encrypt(strData, this.iv);
	}
	public String encrypt(String strData, int iv) throws UnsupportedEncodingException{
		byte[] data = strData.getBytes("UTF-8");
		encrypt(data, iv);
		return DatatypeConverter.printHexBinary(data);
	}

	/**
	 * @機能概要：バイト配列を暗号化する
	 * @引数１：暗号化するバイト配列
	 */
	public void encrypt(byte[] data){
		encrypt(data, this.iv);
	}
	public void encrypt(byte[] data, int iv){
		XorShift32 rand = new XorShift32(this.cipherKey, 3);
		for(int i=0; i<data.length; i++){
			if(i == 0){
				data[i] ^= iv;
			}else{
				data[i] ^= data[i-1];
			}
		}
		for(int i=0; i<data.length; i++){
			data[i] ^= rand.nextInt(255);
		}
	}

	/**
	 * @機能概要：IVを含む16進数文字列を復号化する
	 * @引数１：暗号化された16進数文字列（IVを含む）
	 * @戻り値：復号化された文字列
	 */
	public String decryptWithIV(String strData) throws UnsupportedEncodingException{
		int iv = Integer.parseInt(strData.substring(0, 6), 16);
		strData = strData.substring(6, strData.length());
		return decrypt(strData, iv);
	}

	/**
	 * @機能概要：16進数文字列を復号化する
	 * @引数１：暗号化された16進数文字列
	 * @戻り値：復号化された文字列
	 */
	public String decrypt(String strData) throws UnsupportedEncodingException{
		return decrypt(strData, this.iv);
	}
	public String decrypt(String strData, int iv) throws UnsupportedEncodingException{
		byte[] data = DatatypeConverter.parseHexBinary(strData);
		decrypt(data, iv);
		return new String(data, "UTF-8");
	}

	/**
	 * @機能概要：バイト配列を復号化する
	 * @引数１：復号化するバイト配列
	 */
	public void decrypt(byte[] data){
		decrypt(data, this.iv);
	}
	public void decrypt(byte[] data, int iv){
		XorShift32 rand = new XorShift32(this.cipherKey, 3);
		for(int i=0; i<data.length; i++){
			data[i] ^= rand.nextInt(255);
		}
		for(int i=data.length-1; i>=0; i--){
			if(i == 0){
				data[i] ^= iv;
			}else{
				data[i] ^= data[i-1];
			}
		}
	}

}
