package security;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import interfaces.Encryptor;
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
public class MyCrypt32 implements Encryptor{
	private int cipherKey;
	private int encryptIv;
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
		this.encryptIv = iv;
	}

	/**
	 * @機能概要：初期化ベクトルをセットする
	 * @引数１：int型の値(乱数推奨)
	 */
	@Override
	public <T> void setEncryptIv(T encryptIv) {
		this.encryptIv = (int)encryptIv;
	}

	/**
	 * @機能概要：IVを生成して文字列を暗号化する
	 * @引数１：暗号化する文字列
	 * @戻り値：暗号化された16進数文字列（IVを含む）
	 */
	@Override
	public String encryptWithIv(String strData){
		int iv = rand.nextInt(iv_max - iv_min) + iv_min;
		return Integer.toHexString(iv) + encrypt(strData, iv);
	}

	/**
	 * @機能概要：文字列を暗号化する
	 * @引数１：暗号化する文字列
	 * @戻り値：暗号化された16進数文字列
	 */
	@Override
	public String encrypt(String strData){
		return encrypt(strData, this.encryptIv);
	}
	public String encrypt(String strData, int iv){
		byte[] data = null;
		try{
			data = strData.getBytes("UTF-8");
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		encrypt(data, iv);
		return DatatypeConverter.printHexBinary(data);
	}

	/**
	 * @機能概要：バイト配列を暗号化する
	 * @引数１：暗号化するバイト配列
	 */
	public byte[] encrypt(byte[] data){
		encrypt(data, this.encryptIv);
		return data;
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
	@Override
	public String decryptWithIv(String strData){
		int iv = Integer.parseInt(strData.substring(0, 6), 16);
		strData = strData.substring(6, strData.length());
		return decrypt(strData, iv);
	}

	/**
	 * @機能概要：16進数文字列を復号化する
	 * @引数１：暗号化された16進数文字列
	 * @戻り値：復号化された文字列
	 */
	@Override
	public String decrypt(String strData){
		return decrypt(strData, this.encryptIv);
	}
	public String decrypt(String strData, int iv){
		String result = null;
		byte[] data = DatatypeConverter.parseHexBinary(strData);
		decrypt(data, iv);
		try{
			result = new String(data, "UTF-8");
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @機能概要：バイト配列を復号化する
	 * @引数１：復号化するバイト配列
	 */
	public byte[] decrypt(byte[] data){
		decrypt(data, this.encryptIv);
		return data;
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
