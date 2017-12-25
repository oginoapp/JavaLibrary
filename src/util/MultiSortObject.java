package util;

import java.util.ArrayList;

/**
 * @クラス説明：ソートする配列に合わせて他の配列も一緒に並び替える
 * @使い方：
 * int[] params = new int[]{...};
 * String[] data = new String[]{...};
 * MultiSortObject mso = new MultiSortObject(params);
 * mso.addArray(data);
 * mso.sort();
 * System.out.println(Arrays.toString(data));
 * @更新履歴：20160827 - 新規作成
 */
public class MultiSortObject{
	private int[] keyArray = null;
	private ArrayList<Object[]> subArrays = new ArrayList<Object[]>();

	/**
	 * @機能概要：コンストラクタ
	 * @引数１：ソートするキー配列(int[])
	 */
	public MultiSortObject(int[] keyArray){
		this.keyArray = keyArray;
	}

	/**
	 * @機能概要：配列を追加する
	 * @引数１：一緒に並び替えるサブ配列
	 */
	public void addArray(Object[] subArray){
		subArrays.add(subArray);
	}

	/**
	 * @機能概要：ソート呼び出し
	 */
	public synchronized void sort() throws Exception{
		//キー配列がnullの場合はエラー
		if(keyArray == null){
			throw new Exception("keyArray == null");
		}

		//サブ配列がnullもしくは長さが一致していない場合はエラー
		for(Object[] subArray : subArrays){
			if(subArray == null){
				throw new Exception("subArray == null");
			}else
			if(subArray.length != keyArray.length){
				throw new Exception("subArray.length != keyArray.length");
			}
		}

		quickSort(keyArray, 0, keyArray.length - 1);
	}

	/* ソート処理（クイックソート_昇順） */
	public synchronized void quickSort(int[] arr, int left, int right){
		if (left <= right){
			int p = arr[(left + right) / 2];
			int l = left;
			int r = right;

			while(l <= r) {
				while(arr[l] < p){l++;}
				while(arr[r] > p){r--;}

				if (l <= r) {

					//スワップ処理
					int tmp = arr[l];
					arr[l] = arr[r];
					arr[r] = tmp;
					for(Object[] subArray : subArrays){
						Object tmpObj = subArray[l];
						subArray[l] = subArray[r];
						subArray[r] = tmpObj;
					}

					l++;
					r--;
				}
			}

			quickSort(arr, left, r);
			quickSort(arr, l, right);
		}
	}
}
