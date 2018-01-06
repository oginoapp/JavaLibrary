package lang;

import java.util.Comparator;

import util.ArrayUtility;

/**
 * 文字列の比較を、与えられた辞書によって行うクラス
 *
 * //使い方
 * ArrayList<String> list = new ArrayList<>();
 * list.add("a");
 * list.add("b");
 * list.add("c");
 * list.add("d");
 * String[] ascendingArray = {"a", "d", "b", "c"};
 * Collections.sort(list, new StringComparator(ascendingArray));
 * System.out.println(list);
 */
public class StringComparator implements Comparator<String>{

	//辞書データ
	private String[] ascendingArray = null;

	/**
	 * 引数なしコンストラクタ
	 */
	public StringComparator(){
	}

	/**
	 * コンストラクタ
	 * @param ascendingArray 昇順の配列
	 */
	public StringComparator(String[] ascendingArray){
		this.ascendingArray = ascendingArray;
	}

	/**
	 * 辞書をセット
	 * @param ascendingArray 昇順の配列
	 */
	public void setAscendingArray(String[] ascendingArray){
		this.ascendingArray = ascendingArray;
	}

	/**
	 * 与えられた辞書をもとにstr1とstr2を比較する
	 * @param str1 文字列１
	 * @param str2 辞書に登録された文字列
	 * @return str1 > str2 ⇒ 1, str1 < str2 ⇒ -1, str1 == str2 ⇒ 0
	 */
	public int compareTo(String str1, String str2){
		int result = 0;

		if(str1 == null){
			throw new NullPointerException("str1 == null");
		}

		if(str2 == null){
			throw new NullPointerException("str2 == null");
		}

		if(ascendingArray == null){
			throw new NullPointerException("ascendingArray == null");
		}

		//インデックス抽出
		int idx1 = -1;
		int idx2 = -1;
		for(int i = 0; i < ascendingArray.length; i++){
			if(idx1 == -1 && str1.equals(ascendingArray[i])){
				idx1 = i;
			}
			if(idx2 == -1 && str2.equals(ascendingArray[i])){
				idx2 = i;
			}
			if(idx1 != -1 && idx2 != -1){
				break;
			}
		}

		//比較
		if(idx1 > idx2){
			result = 1;
		}else if(idx1 < idx2){
			result = -1;
		}

		return result;
	}

	/**
	 * Collections.sortの時に呼ばれるメソッド
	 */
	@Override
	public int compare(String s1, String s2) {
		int result = 0;

		if(ArrayUtility.contains(ascendingArray, s1)
		|| ArrayUtility.contains(ascendingArray, s2)){
			result = this.compareTo(s1, s2);
		}else{
			result = s1.compareTo(s2);
		}

		return result;
	}

}
