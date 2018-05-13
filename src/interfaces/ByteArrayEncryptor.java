package interfaces;

/**
 * 暗号化オブジェクトのインターフェース
 */
public interface ByteArrayEncryptor{

	public <T> void setEncryptIv(T iv);

	/**
	 * バイト配列の暗号化
	 */
	public void encrypt(byte[] data);

	/**
	 * バイト配列の復号化
	 */
	public void decrypt(byte[] data);

}
