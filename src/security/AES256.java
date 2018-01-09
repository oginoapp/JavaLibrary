package security;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import interfaces.Encryptor;

/**
 * AES256を使った暗号化、復号化を行う
 * AES256 aes = new AES256("1234567890123456", "abcdefghijklmnop");
 * String enctyptData = aes.encrypt("test");
 * String decryptData = aes.decrypt(encryptData);
 *
 * @see /Resources/commons-codec-1.10.jar
 * @see org.apache.commons.codec.binary.Base64
 */
public class AES256 implements Encryptor{
	private String encryptKey = "1234567890123456";
	private String encryptIv = "abcdefghijklmnop";
	private String charset = "UTF-8";

	//ランダムエンジン
	private SecureRandom random = new SecureRandom();

	//定数
	public static final int MAX_KEY_LENGTH = 16;

	//英数字一覧
	public static final char[] chars62 = new char[]{
		'0','1','2','3','4','5','6','7','8','9',
		'a','b','c','d','e','f','g','h','i','j',
		'k','l','m','n','o','p','q','r','s','t',
		'u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','I','J',
		'K','L','M','N','O','P','Q','R','S','T',
		'U','V','W','X','Y','Z'
	};

	/**
	 * コンストラクタ
	 * @param key 暗号キー
	 */
	public AES256(String key){
		this.encryptKey = key;
		//初期化ベクトルを自動生成
		generateNewIv();
	}

	/**
	 * コンストラクタ
	 * @param key 暗号キー
	 * @param iv 初期化ベクトル
	 */
	public AES256(String key, String iv){
		this.encryptKey = key;
		this.encryptIv = iv;
	}

	/**
	 * 暗号キーを設定
	 */
	public void setEncryptKey(String encryptKey){
		this.encryptKey = encryptKey;
	}

	/**
	 * 初期化ベクトルを設定
	 */
	@Override
	public <T> void setEncryptIv(T encryptIv) {
		this.encryptIv = (String)encryptIv;
	}

	/**
	 * 文字コードの設定
	 */
	public void setCharset(String charset){
		this.charset = charset;
	}

	/**
	 * ランダムな初期化ベクトルを生成
	 */
	public void generateNewIv(){
		this.encryptIv =  generateKey();
	}

	/**
	 * ランダムな暗号キーを生成
	 * @return 暗号キー
	 */
	public String generateKey(){
		StringBuilder key = new StringBuilder();

		for(int i = 0, idx = 0; i < MAX_KEY_LENGTH; i++){
			idx = random.nextInt(chars62.length);
			key.append(chars62[idx]);
		}

		return key.toString();
	}

	/**
	 * 暗号化メソッド
	 * @param text 暗号化する文字列
	 * @return 暗号化文字列
	 */
	@Override
	public String encrypt(String text){
		// 変数初期化
		String strResult = null;

		try{
			// 文字列をバイト配列へ変換
			byte[] byteText = text.getBytes(charset);

			// 暗号化キーと初期化ベクトルをバイト配列へ変換
			byte[] byteKey = encryptKey.getBytes(charset);
			byte[] byteIv = encryptIv.getBytes(charset);

			// 暗号化キーと初期化ベクトルのオブジェクト生成
			SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(byteIv);

			// Cipherオブジェクト生成
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// Cipherオブジェクトの初期化
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);

			// 暗号化の結果格納
			byte[] byteResult = cipher.doFinal(byteText);

			// Base64へエンコード
			strResult = Base64.encodeBase64String(byteResult);
		}catch(Exception ex){
			ex.printStackTrace();
		}

		// 暗号化文字列を返却
		return strResult;
	}

	/**
	 * 復号化メソッド
	 * @param text 復号化する文字列
	 * @return 復号化文字列
	 */
	@Override
	public String decrypt(String text){
		// 変数初期化
		String strResult = null;

		try{
			// Base64をデコード
			byte[] byteText = Base64.decodeBase64(text);

			// 暗号化キーと初期化ベクトルをバイト配列へ変換
			byte[] byteKey = encryptKey.getBytes(charset);
			byte[] byteIv = encryptIv.getBytes(charset);

			// 復号化キーと初期化ベクトルのオブジェクト生成
			SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(byteIv);

			// Cipherオブジェクト生成
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// Cipherオブジェクトの初期化
			cipher.init(Cipher.DECRYPT_MODE, key, iv);

			// 復号化の結果格納
			byte[] byteResult = cipher.doFinal(byteText);

			// バイト配列を文字列へ変換
			strResult = new String(byteResult, charset);
		}catch(Exception ex){
			ex.printStackTrace();
		}

		// 復号化文字列を返却
		return strResult;
	}

	/**
	 * ランダムなIVを使って暗号化
	 * @param text 暗号化したい文字列
	 * @return 暗号化された文字列
	 */
	@Override
	public String encryptWithIv(String text){
		String result = "";

		generateNewIv();
		result += this.encryptIv;
		result += encrypt(text);

		return result;
	}

	/**
	 * ランダムなIvを使って暗号化した文字列を復号化
	 * @param text encryptWithIvで暗号化した文字列
	 * @return 復号化した文字列
	 */
	@Override
	public String decryptWithIv(String text){
		String result = "";

		this.encryptIv = text.substring(0, 16);
		result = decrypt(text.substring(16));

		return result;
	}

	/**
	 * バイト配列を暗号化
	 * @param data 暗号化したいバイト配列
	 * @return 暗号化されたバイト配列
	 *
	 * @deprecated 文字列での暗号化を推奨
	 */
	public byte[] encrypt(byte[] data) {
		System.out.println(data.length);
		try{
			String str = new String(data, charset);
			str = encrypt(str);
			data = str.getBytes(charset);
			System.out.println(data.length);
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * バイト配列を復号化
	 * @param data 暗号化されたバイト配列
	 * @return 復号化されたバイト配列
	 *
	 * @deprecated 文字列での復号化を推奨
	 */
	public byte[] decrypt(byte[] data) {
		try{
			String str = new String(data, charset);
			str = decrypt(str);
			data = str.getBytes(charset);
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return data;
	}

}
