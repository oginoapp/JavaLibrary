package security;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import math.XorShift128;

/**
 * @see 128bit暗号キーによる暗号化、復号化
 * @author ogino
 * @version 20161230
 */
public class MyCrypt128{

	private int keyToken1 = -1;
	private int keyToken2 = -1;
	private int keyToken3 = -1;
	private int keyToken4 = -1;
	private int iv = -1;

	private final int IV_MIN = Integer.parseInt("10000000", 16);
	private final int IV_MAX = Integer.parseInt("7FFFFFFF", 16);
	private final int IV_LEN = 8;

	private Random rand = new Random();

	/**
	 * @機能概要：コンストラクタ
	 * @param 暗号キー(32桁までの16進数文字列)
	 */
	public MyCrypt128(String cipherKey) throws NumberFormatException, NullPointerException{
		if(cipherKey == null || cipherKey.isEmpty()){
            throw new NullPointerException();
        }

		String tmp;
		long tmpNum;
		for(int i = 0, sliceLen = 0, cnt = 0; i < cipherKey.length(); i += 8, cnt++){
			sliceLen = cipherKey.length() - i;
			sliceLen = sliceLen > 8 ? 8 : sliceLen;

			tmp = cipherKey.substring(i, i + sliceLen);
			tmpNum = Long.parseLong(tmp, 16);
			tmpNum -= Integer.MAX_VALUE - 1;

			switch(cnt){
			case 0:
				this.keyToken1 = (int)tmpNum;
				break;
			case 1:
				this.keyToken2 = (int)tmpNum;
				break;
			case 2:
				this.keyToken3 = (int)tmpNum;
				break;
			case 3:
				this.keyToken4 = (int)tmpNum;
				break;
			}
		}
	}

	/**
	 * @see コンストラクタ
     * @param 暗号キー(int)４つ
	 */
	public MyCrypt128(int keyToken1, int keyToken2, int keyToken3, int keyToken4){
		this.keyToken1 = keyToken1;
        this.keyToken2 = keyToken2;
		this.keyToken3 = keyToken3;
		this.keyToken4 = keyToken4;
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
	public String encryptWithIV(String strData){
		int iv = rand.nextInt(IV_MAX - IV_MIN) + IV_MIN;
		return Integer.toHexString(iv) + encrypt(strData, iv);
	}

	/**
	 * @機能概要：文字列を暗号化する
	 * @引数１：暗号化する文字列
	 * @戻り値：暗号化された16進数文字列
	 */
	public String encrypt(String strData){
		return encrypt(strData, this.iv);
	}
	public String encrypt(String strData, int iv){
		byte[] data = strData.getBytes(StandardCharsets.UTF_8);
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
		XorShift128 rand = new XorShift128(
				this.keyToken1,
				this.keyToken2,
				this.keyToken3,
				this.keyToken4);
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
	public String decryptWithIV(String strData){
		int iv = Integer.parseInt(strData.substring(0, IV_LEN), 16);
		strData = strData.substring(IV_LEN, strData.length());
		return decrypt(strData, iv);
	}

	/**
	 * @機能概要：16進数文字列を復号化する
	 * @引数１：暗号化された16進数文字列
	 * @戻り値：復号化された文字列
	 */
	public String decrypt(String strData){
		return decrypt(strData, this.iv);
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
		decrypt(data, this.iv);
	}
	public void decrypt(byte[] data, int iv){
		XorShift128 rand = new XorShift128(
				this.keyToken1,
				this.keyToken2,
				this.keyToken3,
				this.keyToken4);
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
