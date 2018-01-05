package lang;

/**
 * 文字列の比較を、与えられた辞書によって行うクラス
 */
public class StringComparator{
	private String[] ascendingArray = null;

	/**
	 * コンストラクタ
	 * @param ascendingArray 昇順の配列
	 */
	public StringComparator(String[] ascendingArray){
		this.ascendingArray = ascendingArray;
	}

	/**
	 * 与えられた辞書をもとにstr1とstr2を比較する
	 * @param str1 文字列１
	 * @param str2 辞書に登録された文字列
	 * @return str1 > str2 の結果
	 */
	public boolean greaterThan(String str1, String str2){
		boolean result = false;

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
		result = idx1 > idx2;

		return result;
	}
}
