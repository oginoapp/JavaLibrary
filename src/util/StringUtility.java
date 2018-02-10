package util;

import java.util.regex.Pattern;

public class StringUtility{

	/**
	 * 文字列の数値判定
	 * @param str 文字列
	 * @return 数値かどうか
	 */
	public static boolean isNumeric(String str){
		boolean result = false;

		try{
			if(str != null){
				Double.parseDouble(str);
				result = true;
			}
		}catch(NumberFormatException ex){}

		return result;
	}

	/**
	 * 文字列の数値判定
	 * @param str 文字列
	 * @return 整数かどうか
	 */
	public static boolean isNumber(String str){
		boolean result = false;

		try{
			if(str != null){
				Long.parseLong(str);
				result = true;
			}
		}catch(NumberFormatException ex){}

		return result;
	}

	/**
	 * Nullか空文字のチェック
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}

	/**
	 * 半角英数字だけかどうか
	 */
	public static boolean isHalfAlphaNumeric(String str) {
		Pattern pattern = Pattern.compile("^[0-9a-zA-Z]*$");
		return pattern.matcher(str).find();
	}

	/**
	 * 半角カナだけかどうか
	 * @param str チェックする文字列
	 */
	public static boolean isHankakuKana(String str) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			//半角カナ以外
			if (c < '\uff61' || c > '\uff9f') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 全角文字だけかどうか
	 * @param str チェックする文字列
	 * @param allowHankakuKana 半角カナ全角文字として扱う
	 */
	public static boolean isZenkaku(String str, boolean allowHankakuKana) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if ((c <= '\u007e') || // 英数字
					(c == '\u00a5') || // \記号
					(c == '\u203e') || // ~記号
					(!allowHankakuKana &&
							(c >= '\uff61' && c <= '\uff9f')) // 半角カナ
			) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 配列の文字列データを1つの文字列に結合
	 * @param arr 結合したい配列
	 * @param separator セパレータ
	 */
	public static <T> String join(T[] arr, String separator){
		StringBuilder sb = new StringBuilder();

		//配列がnullの場合は空文字を返す
		if(arr == null){
			return sb.toString();
		}

		//セパレータがnullの場合は空文字を設定する
		if(separator == null){
			separator = "";
		}

		//結合
		for(int i = 0; i < arr.length; i++){
			sb.append(String.valueOf(arr[i]));

			if(i < arr.length - 1){
				sb.append(separator);
			}
		}

		return sb.toString();
	}

	/**
	 * @機能概要：文字列の前後のスペースを削除する（全角スペース含む）
	 * @引数１：文字列
	 * @戻り値：前後のスペースを削除された文字列
	 * @戻り値_文字列がnull：空文字
	 */
	public static String trim(String str){
		if(str == null){
			str = "";
		}
		char[] value = str.toCharArray();
		int len = value.length;
		int st = 0;
		char[] val = value;

		while ((st < len) && (val[st] <= ' ' || val[st] == '\u3000')) {
			st++;
		}
		while ((st < len) && (val[len - 1] <= ' ' || val[len - 1] == '\u3000')) {
			len--;
		}

		return ((st > 0) || (len < value.length)) ? str.substring(st, len) : str;
	}

	/**
	 * VB6,VBA互換
	 * 文字列の指定した位置から指定した長さを取得する
	 */
	public static String mid(String str, int start, int len){
		if (start <= 0){
			throw new IllegalArgumentException("引数'start'は1以上でなければなりません。");
		}
		if (len < 0){
			throw new IllegalArgumentException("引数'len'は0以上でなければなりません。");
		}
		if (str == null || str.length() < start){
			return "";
		}
		if (str.length() < (start + len)){
			return str.substring(start - 1);
		}
		return str.substring(start - 1);
	}

	/**
	 * VB6,VBA互換
	 * 文字列の指定した位置から末尾までを取得する
	 */
	public static String mid(String str, int start){
		return mid(str, start, str.length());
	}

	/**
	 * VB6,VBA互換
	 * 文字列の先頭から指定した長さの文字列を取得する
	 */
	public static String left(String str, int len){
		if (len < 0){
			throw new IllegalArgumentException("引数'len'は0以上でなければなりません。");
		}
		if (str == null){
			return "";
		}
		if (str.length() <= len){
			return str;
		}
		return str.substring(0, len);
	}

	/**
	 * VB6,VBA互換
	 * 文字列の末尾から指定した長さの文字列を取得する
	 */
	public static String right(String str, int len){
		if (len < 0){
			throw new IllegalArgumentException("引数'len'は0以上でなければなりません。");
		}
		if (str == null){
			return "";
		}
		if (str.length() <= len){
			return str;
		}
		return str.substring(str.length() - len);
	}

	/**
	 * 文字列を等分に分割
	 * @param str 文字列
	 * @param chunkSize 何文字ごとに分割するか
	 * @return 分割された文字列の配列
	 */
	public static String[] divideString(String str, int chunkSize){
		int strLen = str.length();
		String[] result = new String[(int)Math.ceil(strLen / (double)chunkSize)];

		for(int i = 0, sliceLen = 0, cnt = 0; i < strLen; i += chunkSize, cnt++){
			sliceLen = strLen - i;
			sliceLen = sliceLen > chunkSize ? chunkSize : sliceLen;

			result[cnt] = str.substring(i, i + sliceLen);
		}

		return result;
	}

}
