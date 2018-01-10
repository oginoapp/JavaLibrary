package util;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayUtility{

	/**
	 * 配列の連結を行う
	 * @param arr1 配列１
	 * @param arr2 配列２
	 * @return 連結された配列
	 */
	public static <T> T[] concat(T[] arr1, T[] arr2){
		@SuppressWarnings("unchecked")
		T[] result = (T[])Array.newInstance(arr1.getClass().getComponentType(), arr1.length + arr2.length);

		System.arraycopy(arr1, 0, result, 0, arr1.length);
		System.arraycopy(arr2, 0, result, arr1.length, arr2.length);

		return result;
	}

	/**
	 * 配列の中に指定した要素が存在するかどうかを確認する
	 * @param arr 配列
	 * @param value 指定した要素
	 * @return equalsを使って比較して存在した場合
	 */
	public static <T> boolean contains(T[] arr, T value){
		boolean result = false;

		for(T t : arr){
			if(t == null){
				continue;
			}
			if(t.equals(value)){
				result = true;
				break;
			}
		}

		return result;
	}

	/**
	 * 配列の要素数を1つ増やす
	 * @param arr 配列
	 * @return 要素数を増やした配列
	 */
	public static <T> T[] expansion(T[] arr){
		return expansion(arr, 1);
	}

	/**
	 * 配列の要素数を増やす
	 * @param arr 配列
	 * @param increment 増やす要素数
	 * @return 要素数を増やした配列
	 */
	public static <T> T[] expansion(T[] arr, int increment){
		@SuppressWarnings("unchecked")
		T[] result = (T[])Array.newInstance(arr.getClass().getComponentType(), arr.length + increment);

		System.arraycopy(arr, 0, result, 0, arr.length);

		return result;
	}

	/**
	 * 配列を等分に分割
	 * @param arr 配列
	 * @param chunkSize 固まりのサイズ
	 * @return 分割された2次元配列
	 */
	public static <T> T[][] divideArray(T[] arr, int chunkSize){
		int len = (int)Math.ceil(arr.length / (double)chunkSize);
		@SuppressWarnings("unchecked")
		T[][] result = (T[][])Array.newInstance(arr.getClass().getComponentType(), len, chunkSize);

		for(int i = 0, start = 0; i < result.length; i++, start += chunkSize) {
			result[i] = Arrays.copyOfRange(arr, start, start + chunkSize);
		}

		return result;
	}

}
