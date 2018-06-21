package security;

/**
 * RSAのキーペア
 *
 * @param getPublicKey 公開鍵を取得
 * @param getPrivateKey 秘密鍵を取得
 */
public class KeyPair {

	/**
	 * 公開鍵または秘密鍵
	 */
	public static class Key {
		public String exponent;
		public String modulus;
		public Key(String exponent, String modulus) {
			this.exponent = exponent;
			this.modulus = modulus;
		}
	}

	/* 公開鍵 */
	private Key publicKey;

	/* 秘密鍵 */
	private Key privateKey;

	/**
	 * コンストラクタ
	 */
	public KeyPair(Key publicKey, Key privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	/**
	 * 公開鍵を取得
	 */
	public Key getPublicKey() {
		return publicKey;
	}

	/**
	 * 秘密鍵を取得
	 */
	public Key getPrivateKey() {
		return privateKey;
	}
}
