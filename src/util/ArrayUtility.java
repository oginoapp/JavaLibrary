package util;

import java.lang.reflect.Array;

public class ArrayUtility{

	/**
	 * @機能概要：配列の連結を行う
	 * @引数１：配列１
	 * @引数２：配列２
	 * @戻り値：連結された配列
	 */
	public static <T> T[] concat(T[] arr1, T[] arr2){
		@SuppressWarnings("unchecked")
		T[] result = (T[])Array.newInstance(arr1.getClass().getComponentType(), arr1.length + arr2.length);

		System.arraycopy(arr1, 0, result, 0, arr1.length);
		System.arraycopy(arr2, 0, result, arr1.length, arr2.length);

		return result;
	}

	/**
	 * @機能概要：配列の要素数を1つ増やす
	 * @引数１：配列
	 */
	public static <T> T[] expansion(T[] arr){
		return expansion(arr, 1);
	}

	/**
	 * @機能概要：配列の要素数を増やす
	 * @引数１：配列
	 * @引数２：増やす要素数
	 */
	public static <T> T[] expansion(T[] arr, int increment){
		@SuppressWarnings("unchecked")
		T[] result = (T[])Array.newInstance(arr.getClass().getComponentType(), arr.length + increment);;

		System.arraycopy(arr, 0, result, 0, arr.length);

		return result;
	}

}
