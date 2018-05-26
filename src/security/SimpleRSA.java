package security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import math.Functions;

/**
 * 簡単な公開鍵暗号アルゴリズム
 *
 * @param generateKey 公開鍵と秘密鍵のキーペアを生成する
 * @param crypt 暗号化または復号化をする
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
	 */
	public static BigInteger crypt(BigInteger data, KeyPair.Key key) {
		BigInteger e = new BigInteger(key.exponent);
		BigInteger m = new BigInteger(key.modules);
		return data.modPow(e, m);
	}

	/**
	 * テストする
	 */
	public static void test() {
		KeyPair keyPair = generateKey(1024);

		BigInteger e = new BigInteger(keyPair.publicKey.exponent);
		BigInteger d = new BigInteger(keyPair.privateKey.exponent);
		BigInteger n = new BigInteger(keyPair.privateKey.modules);

		for (BigInteger i = BigInteger.ZERO;
				i.compareTo(n) <= -1 && i.compareTo(new BigInteger("100")) <= -1;
				i = i.add(BigInteger.ONE)) {
			BigInteger data = i;

			// 暗号化
			BigInteger enc = data.modPow(e, n);
			// 復号化
			BigInteger dec = enc.modPow(d, n);

			// 平文⇒ 暗号データ⇒ 復号データ を出力
			System.out.println(data + "⇒\t" + enc + "⇒\t" + dec);
			if (!data.equals(dec))
				throw new IllegalStateException("復号化エラー");
		}
	}

}
