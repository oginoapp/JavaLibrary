package security;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;

import interfaces.ByteArrayEncryptor;
import interfaces.StringEncryptor;
import math.XorShift32;

/**
 * 32bitキーの暗号アルゴリズム
 */
public class MyCrypt32 implements ByteArrayEncryptor, StringEncryptor{
	private int cipherKey;
	private int encryptIv;
	private int iv_min = Integer.parseInt("100000", 16);
	private int iv_max = Integer.parseInt("FFFFFF", 16);
	private SecureRandom rand = new SecureRandom();

	/**
	 * コンストラクタ
	 *
	 * @param cipherKey int型の暗号化キー（8桁推奨）
	 */
	public MyCrypt32(int cipherKey){
		this(cipherKey, 0);
	}

	/**
	 * コンストラクタ
	 *
	 * @param cipherKey int型の暗号化キー（8桁推奨）
	 * @param iv int型の初期化ベクトル(乱数推奨)
	 */
	public MyCrypt32(int cipherKey, int iv){
		this.cipherKey = cipherKey;
		this.encryptIv = iv;
	}

	/**
	 * 初期化ベクトルをセットする
	 *
	 * @param encryptIv int型の値(乱数推奨)
	 */
	@Override
	public <T> void setEncryptIv(T encryptIv) {
		this.encryptIv = (int)encryptIv;
	}

	/**
	 * IVを生成して文字列を暗号化する
	 *
	 * @param strData 暗号化する文字列
	 * @return 暗号化された16進数文字列（IVを含む）
	 */
	@Override
	public String encryptWithIv(String strData){
		int iv = rand.nextInt(iv_max - iv_min) + iv_min;
		return Integer.toHexString(iv) + encrypt(strData, iv);
	}

	/**
	 * 文字列を暗号化する
	 *
	 * @param strData 暗号化する文字列
	 * @return 暗号化された16進数文字列
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
	 * バイト配列を暗号化する
	 *
	 * @param data 暗号化するバイト配列
	 */
	public void encrypt(byte[] data){
		encrypt(data, this.encryptIv);
	}
	public void encrypt(byte[] data, int iv){
		//XorShift32 rand = new XorShift32(this.cipherKey, 3);
		XorShift32 rand = new XorShift32(this.cipherKey);
		for(int i = 0; i < data.length; i++){
			if(i == 0){
				data[i] ^= iv;
			}else{
				data[i] ^= data[i - 1];
			}
		}
		for(int i = 0; i < data.length; i++){
			data[i] ^= rand.nextInt(256);
		}
	}

	/**
	 * IVを含む16進数文字列を復号化する
	 *
	 * @param strData 暗号化された16進数文字列（IVを含む）
	 * @return 復号化された文字列
	 */
	@Override
	public String decryptWithIv(String strData){
		int iv = Integer.parseInt(strData.substring(0, 6), 16);
		strData = strData.substring(6, strData.length());
		return decrypt(strData, iv);
	}

	/**
	 * 16進数文字列を復号化する
	 *
	 * @param strData 暗号化された16進数文字列
	 * @return 復号化された文字列
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
	 * バイト配列を復号化する
	 *
	 * @param data 復号化するバイト配列
	 */
	public void decrypt(byte[] data){
		decrypt(data, this.encryptIv);
	}
	public void decrypt(byte[] data, int iv){
		//XorShift32 rand = new XorShift32(this.cipherKey, 3);
		XorShift32 rand = new XorShift32(this.cipherKey);
		for(int i = 0; i < data.length; i++){
			data[i] ^= rand.nextInt(256);
		}
		for(int i = data.length - 1; i >= 0; i--){
			if(i == 0){
				data[i] ^= iv;
			}else{
				data[i] ^= data[i - 1];
			}
		}
	}

}
