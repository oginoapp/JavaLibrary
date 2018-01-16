package util;

/**
 * @varsion 20161227
 * @author ogino
 * @see 共通処理のユーティリティ
 */
public class CommonUtility{

	/**
	 * @機能概要：文字列の数値判定
	 * @引数１：文字列
	 * @戻り値：数値かどうか
	 */
	public static boolean isNumber(String str){
		boolean result = false;

		try{
			Long.parseLong(str);
			result = true;
		}catch(NumberFormatException ex){
			result = false;
		}

		return result;
	}

	/**
     * 戻り値の型に合わせてキャスト
     */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
	    T castObj = (T) obj;
	    return castObj;
	}

}
