package security;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;

import interfaces.ByteArrayEncryptor;
import interfaces.StringEncryptor;
import math.XorShiftVariable;

/**
 * 自分専用の標準暗号化アルゴリズム
 * 16進数の任意の長さの暗号キーを使い
 * CBCモードで暗号化する
 * @see 依存クラス: XorShiftVariable
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
	 * @機能概要：コンストラクタ
	 * @param 暗号キー(任意の桁数の16進数文字列)
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
     * @param 暗号キー(32ビット整数の配列)
	 */
	public MyEncryptionStandard(int[] keyTokens){
		this.keyTokens = keyTokens.clone();
	}

	/**
	 * 文字コードの設定
	 * @param charset 文字コード
	 */
	public void setCharset(Charset charset){
		this.charset = charset;
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
		int iv = rand.nextInt(IV_MAX - IV_MIN) + IV_MIN;
		return (Integer.toHexString(iv) + encrypt(strData, iv)).toUpperCase();
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
		byte[] data = strData.getBytes(charset);
		encrypt(data, iv);
		return (DatatypeConverter.printHexBinary(data)).toUpperCase();
	}

	/**
	 * @機能概要：バイト配列を暗号化する
	 * @引数１：暗号化するバイト配列
	 */
	public void encrypt(byte[] data){
		encrypt(data, this.encryptIv);
	}
	public void encrypt(byte[] data, int iv){
		XorShiftVariable rand = new XorShiftVariable(this.keyTokens);
		for(int i = 0; i < data.length; i++){
			if(i == 0){
				data[i] ^= iv;
			}else{
				data[i] ^= data[i-1];
			}
		}
		for(int i = 0; i < data.length; i++){
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
		int iv = Integer.parseInt(strData.substring(0, IV_LEN), 16);
		strData = strData.substring(IV_LEN, strData.length());
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
		byte[] data = DatatypeConverter.parseHexBinary(strData);
		decrypt(data, iv);
		return new String(data, StandardCharsets.UTF_8);
	}

	/**
	 * @機能概要：バイト配列を復号化する
	 * @引数１：復号化するバイト配列
	 */
	public void decrypt(byte[] data){
		decrypt(data, this.encryptIv);
	}
	public void decrypt(byte[] data, int iv){
		XorShiftVariable rand = new XorShiftVariable(this.keyTokens);
		for(int i = 0; i < data.length; i++){
			data[i] ^= rand.nextInt(255);
		}
		for(int i = data.length - 1; i >= 0; i--){
			if(i == 0){
				data[i] ^= iv;
			}else{
				data[i] ^= data[i-1];
			}
		}
	}
}
