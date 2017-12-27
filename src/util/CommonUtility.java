package util;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

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
	 * @see ファイルパス表現を統一する
	 * @param oldPath 修正するファイルパス(String)
	 * @param separator セパレータ(char)
	 * @param lastSeparator 最後のセパレータを有りにするかどうか(boolean)
	 */
	public static String convertFilePath(String oldPath, char separator, boolean lastSeparator){
		String newPath = oldPath;

		try{
			//セパレータの統一
			newPath = newPath.replace('\\', separator);
			newPath = newPath.replace('/', separator);

			//最後のセパレータ
			if(lastSeparator){
				if(newPath.charAt(newPath.length() - 1) != separator){
					newPath += separator;
				}
			}else{
				if(newPath.charAt(newPath.length() - 1) == separator){
					newPath = newPath.substring(0, newPath.length() - 1);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return newPath;
	}

	/**
	 * @see ファイルのサイズを取得する
	 * @see ディレクトリの場合は、再帰で取得したサイズの合計を戻す
	 * @param ファイルオブジェクト
	 */
	public static long allFileSize(File file){
		long size = 0L;

		if (file == null){
			return size;
		}

		if (file.isDirectory()){
			File files[] = file.listFiles();
			if (files != null) {
				for (int i=0; i < files.length; i++){
					size += allFileSize(files[i]);
				}
			}
		}else{
			size = file.length();
		}

		return size;
	}

	/**
	 * @機能概要：ファイル名の拡張子を取得する
	 * @引数１：ファイル名
	 * @戻り値：ファイルの拡張子
	 * @戻り値_ファイル名が存在しない：空文字
	 * @戻り値_拡張子が存在しない：空文字
	 */
	public static String getExtension(String fileName){
		String extension = "";

		//ファイル名が存在しない
		if(fileName == null || fileName.isEmpty()){
			return extension;
		}

		//拡張子が存在しない
		if(fileName.indexOf(".") == -1){
			return extension;
		}

		//拡張子を取得
		extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

		return extension;
	}

	/**
	 * @機能概要：曜日計算
	 * @引数１：日付の文字列
	 * @引数２：年月日のセパレータ
	 */
	public static String weekOfDay(String date, String separator){
		String weekOfDay = null;
		String[] strYmd = new String[3];

		//年月日の取得
		if(date == null || date.isEmpty()){
			strYmd[0] = "0";
			strYmd[1] = "0";
			strYmd[2] = "0";
		}else{
			strYmd = date.split(separator);
		}

		//年月日の数値化
		int[] ymd = new int[strYmd.length];
		for(int i=0; i<strYmd.length; i++){
			ymd[i] = Integer.parseInt(strYmd[i]);
		}

		//カレンダーを使って曜日を計算
		Locale.setDefault(Locale.JAPANESE);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, ymd[0]);
		cal.set(Calendar.MONTH, ymd[1] - 1);
		cal.set(Calendar.DATE, ymd[2]);
		weekOfDay = String.format("%1$tA", cal);

		return weekOfDay;
	}

	/**
	 * @機能概要：文字列をyyyy-mm-ddの形式にする
	 * @引数１：変換する文字列
	 * @戻り値：変換された文字列
	 */
	public static String toDateString(String strDate){
		if(strDate == null){
			strDate = "";
		}

		char[] chars = strDate.toCharArray();
		StringBuilder datetime = new StringBuilder();

		int numCount = 0;
		for(int i=0; i<chars.length && numCount < 8; i++){
			if(isNumber(String.valueOf(chars[i]))){
				datetime.append(chars[i]);
				numCount++;
				if(numCount == 4 || numCount == 6){
					datetime.append('-');
				}
			}
		}

		return datetime.toString();
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
