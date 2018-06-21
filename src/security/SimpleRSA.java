package security;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

import converter.Base64;
import converter.RadixConverter;
import math.Functions;
import util.ArrayUtility;

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
		BigInteger m = new BigInteger(key.modulus);
		return data.modPow(e, m);
	}

	/**
	 * 暗号化をする
	 *
	 * @param data 暗号化したい文字列
	 * @return 暗号化された文字列
	 */
	public static String encrypt(String data, KeyPair.Key key) {
		String result;

		// パディング
		data = "0" + data;

		// 文字列 ⇒ byte配列
		byte[] byteData = data.getBytes(StandardCharsets.UTF_8);

		// データとキーの長さ取得
		int dataLenB = byteData.length;
		int keyLenB = RadixConverter.decimalToAnyDecimal(
				new BigDecimal(key.exponent), 2).length() / 8;

		// 暗号キーの長さが足りない場合
		if (dataLenB > keyLenB) {
			throw new IllegalArgumentException("dataLenB > keyLenB");
		}

		// byte配列 ⇒ 整数
		BigInteger intData = new BigInteger(byteData);

		// 暗号化
		BigInteger enc = crypt(intData, key);

		// 整数 ⇒ byte配列
		byte[] tmp = enc.toByteArray();

		// byte配列 ⇒ Base64に変換
		result = Base64.encode(tmp);

		return result;
	}

	/**
	 * 復号化をする
	 *
	 * @param base64 復号化したい文字列
	 * @return 復号化された文字列
	 */
	public static String decrypt(String base64, KeyPair.Key key) {
		String result;

		// base64 ⇒ byte配列
		byte[] byteData = Base64.decode(base64);

		// byte配列 ⇒ 整数
		BigInteger intData = new BigInteger(byteData);

		// 復号化
		BigInteger dec = crypt(intData, key);

		// 整数 ⇒ byte配列
		byteData = dec.toByteArray();

		// byte配列 ⇒ 文字列
		result = new String(byteData, StandardCharsets.UTF_8);

		// パディング解除
		result = result.substring(1);

		return result;
	}

	/**
	 * テストする
	 */
	public static void test(String... params) {
		System.out.println("-----テスト開始-----");

		// キー生成
		KeyPair keyPair = SimpleRSA.generateKey(1024);

		// テスト用の値
		String[] plainTexts = {"平文test!!,./\\-^", "あいうえおabc", "012345.67890"};
		if (params != null && params.length > 0) {
			plainTexts = ArrayUtility.concat(plainTexts, params);
		}

		for (String text : plainTexts) {
			System.out.println("平文データ: " + text);
			String enc = SimpleRSA.encrypt(text, keyPair.getPublicKey());
			System.out.println("暗号データ: " + enc);
			String dec = SimpleRSA.decrypt(enc, keyPair.getPrivateKey());
			System.out.println("復号データ: " + dec);

			if (!text.equals(dec)) {
				throw new AssertionError("[" + text + "] != [" + dec + "]");
			}
		}

		System.out.println("-----テスト完了-----");
	}

}