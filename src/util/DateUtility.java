package util;

import java.util.Calendar;
import java.util.Locale;

public class DateUtility{

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
			if(CommonUtility.isNumber(String.valueOf(chars[i]))){
				datetime.append(chars[i]);
				numCount++;
				if(numCount == 4 || numCount == 6){
					datetime.append('-');
				}
			}
		}

		return datetime.toString();
	}

}
