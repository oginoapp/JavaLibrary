package security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import converter.Base64;
import util.StringUtility;

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

	/**
	 * ファイルにキーの情報を書き込む
	 *
	 * @param path ファイルのパス
	 * @throws FileNotFoundException
	 */
	public void writeToFile(String path) throws FileNotFoundException {
		File file = new File(path);

		try (PrintWriter writer = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8)))) {

			// modulus
			if (publicKey != null) {
				byte[] byteArray = new BigInteger(publicKey.modulus).toByteArray();
				String[] tmp = StringUtility.divideString(Base64.encode(byteArray), 64);

				writer.println("-----BEGIN RSA MODULUS-----");
				for (String str : tmp) {
					writer.println(str);
				}
				writer.println("-----END RSA MODULUS-----");
			}

			writer.println();

			// publicExponent
			if (publicKey != null) {
				byte[] byteArray = new BigInteger(publicKey.exponent).toByteArray();
				String[] tmp = StringUtility.divideString(Base64.encode(byteArray), 64);

				writer.println("-----BEGIN RSA PUBLIC EXPONENT-----");
				for (String str : tmp) {
					writer.println(str);
				}
				writer.println("-----END RSA PUBLIC EXPONENT-----");
			}

			writer.println();

			// privateExponent
			if (privateKey != null) {
				byte[] byteArray = new BigInteger(privateKey.exponent).toByteArray();
				String[] tmp = StringUtility.divideString(Base64.encode(byteArray), 64);

				writer.println("-----BEGIN RSA PRIVATE EXPONENT-----");
				for (String str : tmp) {
					writer.println(str);
				}
				writer.println("-----END RSA PRIVATE EXPONENT-----");
			}

		}
	}

	/**
	 * ファイルからキーの情報を読み込む
	 *
	 * @param path ファイルのパス
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static KeyPair loadFromFile(String path) throws FileNotFoundException, IOException {
		File file = new File(path);

		String modulus = "";
		String publicExponent = "";
		String privateExponent = "";
		int region = 0;
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));){

			String line;
			while((line = reader.readLine()) != null){
				switch (line) {
				case "-----BEGIN RSA MODULUS-----":
					region = 1;
					break;
				case "-----END RSA MODULUS-----":
					region = 0;
					break;
				case "-----BEGIN RSA PUBLIC EXPONENT-----":
					region = 2;
					break;
				case "-----END RSA PUBLIC EXPONENT-----":
					region = 0;
					break;
				case "-----BEGIN RSA PRIVATE EXPONENT-----":
					region = 3;
					break;
				case "-----END RSA PRIVATE EXPONENT-----":
					region = 0;
					break;
				default:
					switch (region) {
					case 1:
						modulus += line;
						break;
					case 2:
						publicExponent += line;
						break;
					case 3:
						privateExponent += line;
						break;
					}
				}
			}

		}

		// Base64からBigIntegerに変換
		modulus = new BigInteger(Base64.decode(modulus)).toString();
		publicExponent = new BigInteger(Base64.decode(publicExponent)).toString();
		privateExponent = new BigInteger(Base64.decode(privateExponent)).toString();

		KeyPair keyPair = new KeyPair(
				new Key(publicExponent, modulus),
				new Key(privateExponent, modulus));

		return keyPair;
	}

}
