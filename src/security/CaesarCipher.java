package security;

import math.XorShift32;

/**
 * @使用方法：CaesarCipher cc = new CaesarCipher(12345678);
 *            String encryptData = cc.encrypt("testdata");
 *            String decryptData = cc.decrypt(encryptData);
 * @更新履歴：20160713 - 新規作成
 *            20160730 - 暗号キー変更メソッドの追加
 */
public class CaesarCipher{
	private int cipherKey;

	/**
	 * @機能概要：コンストラクタ
	 * @引数１：int型の暗号化キー（8桁推奨）
	 */
	public CaesarCipher(int cipherKey){
		setCipherKey(cipherKey);
	}

	/**
	 * @機能概要：暗号キーの変更
	 * @引数１：int型の暗号化キー（8桁推奨）
	 */
	public void setCipherKey(int cipherKey){
		this.cipherKey = cipherKey;
	}

	/**
	 * @機能概要：文字列を暗号化する
	 * @引数１：暗号化する文字列
	 * @戻り値：暗号化された文字列
	 */
	public synchronized String encrypt(String str){
		StringBuilder sb = new StringBuilder();
		XorShift32 x = new XorShift32(cipherKey, 3);

		for(int i=0; i<str.length(); i++){
	         sb.append((char)(str.charAt(i) + x.nextInt(1000)));
		}

		return sb.toString();
	}

	/**
	 * @機能概要：文字列を復号化する
	 * @引数１：暗号化された文字列
	 * @戻り値：復号化された文字列
	 */
	public synchronized String decrypt(String str){
		StringBuilder sb = new StringBuilder();
		XorShift32 x = new XorShift32(cipherKey, 3);

		for(int i=0; i<str.length(); i++){
			sb.append((char)(str.charAt(i) - x.nextInt(1000)));
		}

		return sb.toString();
	}

}
