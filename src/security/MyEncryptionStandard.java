package security;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;

import interfaces.ByteArrayEncryptor;
import interfaces.StringEncryptor;
import math.XorShiftVariable;

/**
 * 自分専用の共通鍵暗号化アルゴリズム
 * 16進数の任意の長さの暗号キーを使い
 * CBCモードで暗号化する
 *
 * @see math.XorShiftVariable 疑似乱数生成に使う
 */
public class MyEncryptionStandard implements ByteArrayEncryptor, StringEncryptor{
	private int[] keyTokens = null;
	private int encryptIv = -1;
	private Charset charset = StandardCharsets.UTF_8;

	private final int IV_MIN = Integer.parseInt("10000000", 16);
	private final int IV_MAX = Integer.parseInt("7FFFFFFF", 16);
	private final int IV_LEN = 8;

	private SecureRandom rand = new SecureRandom();

	/**
	 * コンストラクタ
	 *
	 * @param cipherKey 暗号キー(任意の桁数の16進数文字列)
	 */
	public MyEncryptionStandard(String cipherKey) throws NumberFormatException, NullPointerException{
		if(cipherKey == null || cipherKey.isEmpty()){
			throw new NullPointerException();
		}

		this.keyTokens = new int[(int)Math.ceil(cipherKey.length() / 8.0)];
		String tmp;
		long tmpNum;
		for(int i = 0, sliceLen = 0, cnt = 0; i < cipherKey.length(); i += 8, cnt++){
			sliceLen = cipherKey.length() - i;
			sliceLen = sliceLen > 8 ? 8 : sliceLen;

			tmp = cipherKey.substring(i, i + sliceLen);
			tmpNum = Long.parseLong(tmp, 16);
			tmpNum -= Integer.MAX_VALUE - 1;

			keyTokens[cnt] = (int)tmpNum;
		}
	}

	/**
	 * コンストラクタ
	 *
     * @param keyTokens 暗号キー(32ビット整数の配列)
	 */
	public MyEncryptionStandard(int[] keyTokens){
		this.keyTokens = keyTokens.clone();
	}

	/**
	 * 文字コードの設定
	 *
	 * @param charset 文字コード
	 */
	public void setCharset(Charset charset){
		this.charset = charset;
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
		int iv = rand.nextInt(IV_MAX - IV_MIN) + IV_MIN;
		return (Integer.toHexString(iv) + encrypt(strData, iv)).toUpperCase();
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

	/**
	 * 文字列を暗号化する
	 *
	 * @param strData 暗号化する文字列
	 * @param iv 初期化ベクトル
	 * @return 暗号化された16進数文字列
	 */
	public String encrypt(String strData, int iv){
		byte[] data = strData.getBytes(charset);
		encrypt(data, iv);
		return (DatatypeConverter.printHexBinary(data)).toUpperCase();
	}

	/**
	 * バイト配列を暗号化する
	 *
	 * @param data 暗号化するバイト配列
	 */
	public void encrypt(byte[] data){
		encrypt(data, this.encryptIv);
	}

	/**
	 * バイト配列を暗号化する
	 *
	 * @param data 暗号化するバイト配列
	 * @param iv 初期化ベクトル
	 */
	public void encrypt(byte[] data, int iv){
		XorShiftVariable rand = new XorShiftVariable(this.keyTokens);
		for(int i = 0; i < data.length; i++){
			if(i == 0){
				data[i] ^= iv;
			}else{
				data[i] ^= data[i - 1];
			}
		}
		for(int i = 0; i < data.length; i++){
			data[i] ^= rand.nextInt(255);
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
		int iv = Integer.parseInt(strData.substring(0, IV_LEN), 16);
		strData = strData.substring(IV_LEN, strData.length());
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

	/**
	 * 16進数文字列を復号化する
	 *
	 * @param strData 暗号化された16進数文字列
	 * @param iv 初期化ベクトル
	 * @return 復号化された文字列
	 */
	public String decrypt(String strData, int iv){
		byte[] data = DatatypeConverter.parseHexBinary(strData);
		decrypt(data, iv);
		return new String(data, StandardCharsets.UTF_8);
	}

	/**
	 * バイト配列を復号化する
	 *
	 * @param data 復号化するバイト配列
	 */
	public void decrypt(byte[] data){
		decrypt(data, this.encryptIv);
	}

	/**
	 * バイト配列を復号化する
	 *
	 * @param data 復号化するバイト配列
	 * @param iv 初期化ベクトル
	 */
	public void decrypt(byte[] data, int iv){
		XorShiftVariable rand = new XorShiftVariable(this.keyTokens);
		for(int i = 0; i < data.length; i++){
			data[i] ^= rand.nextInt(255);
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
