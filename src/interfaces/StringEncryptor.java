package interfaces;

/**
 * 暗号化オブジェクトのインターフェース
 */
public interface StringEncryptor{

	public <T> void setEncryptIv(T iv);

	/**
	 * テキストの暗号化
	 */
	public String encrypt(String data);

	/**
	 * テキストの復号化
	 */
	public String decrypt(String data);

	/**
	 * IVを自動生成、付加してテキストの暗号化
	 */
	public String encryptWithIv(String data);

	/**
	 * encryptWithIVメソッドで暗号化したテキストを復号化
	 */
	public String decryptWithIv(String data);

}
