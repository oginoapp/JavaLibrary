package security;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import converter.Base64;
import converter.RadixConverter;
import math.Functions;

/**
 * 簡単な公開鍵暗号アルゴリズム
 *
 * @param generateKey 公開鍵と秘密鍵のキーペアを生成する
 * @param crypt 整数データの暗号化または復号化をする
 * @param encrypt 文字列を暗号化する
 * @param decrypt 文字列を復号化する
 */
public class SimpleRSA {

	/**
	 * キーペアのクラス
	 *
	 * @param getPublicKey 公開鍵を取得
	 * @param getPrivateKey 秘密鍵を取得
	 */
	public static class KeyPair {
		public static class Key {
			public String exponent;
			public String modules;
			public Key(String exponent, String modules) {
				this.exponent = exponent;
				this.modules = modules;
			}
		}

		private Key publicKey;
		private Key privateKey;

		public KeyPair(Key publicKey, Key privateKey) {
			this.publicKey = publicKey;
			this.privateKey = privateKey;
		}

		public Key getPublicKey() {
			return publicKey;
		}
		public Key getPrivateKey() {
			return privateKey;
		}
	}

	/**
	 * 公開鍵と秘密鍵のキーペアを生成する
	 *
	 * @param bits 生成するキーのビット数
	 * @return 生成されたキーペア
	 */
	public static KeyPair generateKey(int bits) {
		// 生成する数値が素数である確率は(1 - 1/2^certainty)より大きい
		int certainty = 8192;
		Random prng = new SecureRandom();

		// ランダムな素数１
		BigInteger p = new BigInteger(bits, certainty, prng);
		// ランダムな素数２
		BigInteger q = null;
		do {
			q = new BigInteger(bits, certainty, prng);
		} while (p.compareTo(q) == 0);

		// 法
		BigInteger n = p.multiply(q);
		// (p-1)と(q-1)の最小公倍数
		BigInteger l = Functions.lcm(
				p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));

		// lと互いに素な数
		BigInteger e = null;
		do {
			e = new BigInteger(bits, certainty, prng);
		} while (Functions.gcd(e, l).compareTo(BigInteger.ONE) != 0
				|| e.compareTo(l) == 0
				|| e.compareTo(p) == 0
				|| e.compareTo(q) == 0);

		// lを法としたeのモジュラ逆数
		BigInteger d = e.modInverse(l);

		// 公開鍵
		KeyPair.Key publicKey = new KeyPair.Key(e.toString(), n.toString());
		// 秘密鍵
		KeyPair.Key privateKey = new KeyPair.Key(d.toString(), n.toString());

		return new KeyPair(publicKey, privateKey);
	}

	/**
	 * 暗号化または復号化をする
	 *
	 * @param data 暗号化前または暗号化後の整数データ
	 * @return 暗号データまたは復号データ
	 */
	public static BigInteger crypt(BigInteger data, KeyPair.Key key) {
		BigInteger e = new BigInteger(key.exponent);
		BigInteger m = new BigInteger(key.modules);
		return data.modPow(e, m);
	}

	/**
	 * 暗号化をする
	 *
	 * @param data 暗号化したい文字列
	 * @return 暗号化された文字列
	 */
	public static String encrypt(String data, KeyPair.Key key) {
		StringBuilder result = new StringBuilder();

		// 文字列から整数に変換
		byte[] byteData = data.getBytes(StandardCharsets.UTF_8);

		// データとキーの長さ取得
		int dataLenB = byteData.length;
		int keyLenB = RadixConverter.decimalToAnyDecimal(
				new BigDecimal(key.exponent), 2).length() / 8;

		// 暗号キーの長さが足りない場合
		if (dataLenB > keyLenB) {
			throw new IllegalArgumentException("dataLenB > keyLenB");
		}

		// 16進数に変換
		String hexData = DatatypeConverter.printHexBinary(byteData);

		// 整数に変換
		BigInteger intData = new BigInteger(RadixConverter.anyDecimalToDecimal(hexData, 16).toString());

		// 暗号化
		BigInteger enc = crypt(intData, key);

		// 16進数に変換
		hexData = RadixConverter.decimalToAnyDecimal(new BigDecimal(enc), 16).toString();

		// byte配列に変換
		if (hexData.length() % 2 != 0)
			hexData = "0" + hexData;
		byte[] tmp = DatatypeConverter.parseHexBinary(hexData);
		// Base64に変換
		result.append(Base64.encode(tmp));

		return result.toString();
	}

	/**
	 * 復号化をする
	 *
	 * @param data 復号化したい文字列
	 * @return 復号化された文字列
	 */
	public static String decrypt(String data, KeyPair.Key key) {
		String result;

		// byte配列に変換
		byte[] byteData = Base64.decode(data);

		// 16進数に変換
		String hexData = DatatypeConverter.printHexBinary(byteData);

		// 整数に変換
		BigInteger intData = new BigInteger(RadixConverter.anyDecimalToDecimal(hexData, 16).toString());

		// 復号化
		BigInteger enc = crypt(intData, key);

		// 整数から文字列に変換
		hexData = RadixConverter.decimalToAnyDecimal(new BigDecimal(enc), 16).toString();

		// byte配列に変換
		if (hexData.length() % 2 != 0)
			hexData = "0" + hexData;
		byteData = DatatypeConverter.parseHexBinary(hexData);

		// 文字列に変換
		result = new String(byteData, StandardCharsets.UTF_8);

		return result;
	}

	/**
	 * テストする
	 */
	public static void test() {
		KeyPair keyPair = generateKey(1024);

		BigInteger n = new BigInteger(keyPair.privateKey.modules);

		for (BigInteger i = BigInteger.ONE;
				i.compareTo(n) <= -1 && i.compareTo(new BigInteger("3")) <= -1;
				i = i.add(BigInteger.ONE)) {
			BigInteger data = i;

			// 暗号化
			BigInteger enc = crypt(data, keyPair.publicKey);
			// 復号化
			BigInteger dec = crypt(enc, keyPair.privateKey);

			// 平文⇒ 暗号データ⇒ 復号データ を出力
			System.out.println(data + "⇒\t" + enc + "⇒\t" + dec);
			if (!data.equals(dec))
				throw new IllegalStateException("復号化エラー");
		}
	}

}