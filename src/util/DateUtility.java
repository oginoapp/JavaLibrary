package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtility{

	/**
	 * 日付の差分の種類を指定する列挙型
	 */
	public static enum TimeDiffType{
		DAY,
		HOUR,
		MINUTE,
		SECOND,
		MILLI_SECOND;
	}

	/**
	 * 曜日計算
	 * @param date 日付の文字列
	 * @param separator 年月日のセパレータ
	 *
	 * @deprecated weekOfDay(Date date)メソッドを推奨
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
	 * 日付から曜日を求める
	 * @param date 日付のオブジェクト
	 * @return 曜日の値
	 * SUNDAY = 1    //日曜日
	 * MONDAY = 2    //月曜日
	 * TUESDAY = 3   //火曜日
	 * WEDNESDAY = 4 //水曜日
	 * THURSDAY = 5  //木曜日
	 * FRIDAY = 6    //金曜日
	 * SATURDAY = 7  //土曜日
	 */
	public static int weekOfDay(Date date){
		int dayOfWeek = 0;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		return dayOfWeek;
	}

	/**
	 * @機能概要：文字列をyyyy-mm-ddの形式にする
	 * @引数１：変換する文字列
	 * @戻り値：変換された文字列
	 *
	 * @deprecated reFormatメソッドを推奨
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

	/**
	 * 日付のフォーマットを変更
	 * yyyyMMddでフォーマットされている文字列を指定したフォーマットに変換
	 * @param strDate 日付の文字列
	 * @param afterFormat 変更後のフォーマット
	 * @return フォーマットが変更された日付の文字列
	 *
	 * @throws ParseException 解析中の想定外のエラー
	 */
	public static String reFormat(String strDate, String afterFormat) throws ParseException{
		return reFormat(strDate, "yyyyMMdd", afterFormat);
	}

	/**
	 * 日付のフォーマットを変更
	 * @param strDate 日付の文字列
	 * @param beforeFormat 変更前のフォーマット
	 * @param afterFormat 変更後のフォーマット
	 * @return フォーマットが変更された日付の文字列
	 *
	 * @throws ParseException 解析中の想定外のエラー
	 */
	public static String reFormat(String strDate, String beforeFormat, String afterFormat) throws ParseException{
		String result = "";

		Date date = new SimpleDateFormat(beforeFormat).parse(strDate);
		result = new SimpleDateFormat(afterFormat).format(date);

		return result;
	}

	/**
	 * dateTo - dateFrom の日付の差分を返す
	 *
	 * @param dateFrom 比較対象の日付
	 * @param dateTo 比較対象の日付
	 * @param type 日付の種類
	 * @return 指定した日付の種類に応じた差分
	 *
	 * @throws IllegalArgumentException DateDiffTypeの指定が間違っている
	 */
	public static double dateDiff(Date dateFrom, Date dateTo, TimeDiffType type) throws IllegalArgumentException{
		double diff = dateTo.getTime() - dateFrom.getTime();

		switch(type){
		case DAY:
			diff /= 24D;
		case HOUR:
			diff /= 60D;
		case MINUTE:
			diff /= 60D;
		case SECOND:
			diff /= 1000D;
		case MILLI_SECOND:
			break;
		default:
			throw new IllegalArgumentException("DateDiffTypeが間違っています。");
		}

		return diff;
	}

}
