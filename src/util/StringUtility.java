package util;

public class StringUtility{

	/**
	 * @see 配列のデータを1つの文字列に結合
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

		return ((st>0) || (len<value.length)) ? str.substring(st,len) : str;
	}

}
