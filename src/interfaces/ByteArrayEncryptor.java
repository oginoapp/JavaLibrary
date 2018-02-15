package interfaces;

/**
 * 暗号化オブジェクトのインターフェース
 */
public interface ByteArrayEncryptor{

	public <T> void setEncryptIv(T iv);

	/**
	 * テキストの暗号化(バイト配列)
	 */
	public void encrypt(byte[] data);

	/**
	 * テキストの復号化(バイト配列)
	 */
	public void decrypt(byte[] data);

}
