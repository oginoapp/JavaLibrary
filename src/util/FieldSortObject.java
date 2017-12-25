package util;

/**
 * BeanやEntityなどの配列の中にある変数をもとにソートするクラス
 */
public class FieldSortObject{

	private Object[] arr = null;
	private String sortFieldName = "";

	/**
	 * @機能概要：コンストラクタ
	 */
	public FieldSortObject(Object[] arr, String sortFieldName){
		this.arr = arr;
		this.sortFieldName = sortFieldName;
	}

	/**
	 * @機能概要：ソート呼び出し
	 */
	public synchronized void sort() throws Exception{
		//配列がnullの場合はエラー
		if(arr == null){
			throw new Exception("keyArray == null");
		}

		quickSort(arr, 0, arr.length - 1);
	}

	/**
	 * ソート処理（クイックソート_昇順）
	 */
	private void quickSort(Object[] arr, int left, int right) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
		if (left <= right){
			int p = getSortVariable(arr[(left + right) / 2]);
			int l = left;
			int r = right;

			while(l <= r) {
				while(getSortVariable(arr[l]) < p){l++;}
				while(getSortVariable(arr[r]) > p){r--;}

				if (l <= r) {

					//スワップ処理
					Object tmp = arr[l];
					arr[l] = arr[r];
					arr[r] = tmp;

					l++;
					r--;
				}
			}

			quickSort(arr, left, r);
			quickSort(arr, l, right);
		}
	}

	/**
	 * 文字列をもとに変数名を指定して値を取得
	 */
	private int getSortVariable(Object obj) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
		return obj.getClass().getField(this.sortFieldName).getInt(obj);
	}
}

