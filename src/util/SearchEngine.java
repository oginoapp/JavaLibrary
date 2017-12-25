package util;

/**
 * @クラス説明：類似検索をするためのクラス
 * @使い方：
 * String str = "HelloWorld";
 * String[] data = {"abc","hello","world!!"};
 * SearchEngine se = new SearchEngine();
 * se.sortSimilarity(str, data);
 * System.out.println(Arrays.toString(data));
 * @更新履歴：20160827 - 新規作成
 */
public class SearchEngine{

	/**
	 * @機能概要：データを検索文字列に対しての類似順にソートする
	 * @引数１：検索文字列
	 * @引数２：データ
	 */
	public synchronized void sortSimilarity(String str,String[] data) throws Exception{
		//検索文字列がnullの場合はエラー
		if(str == null){
			throw new NullPointerException("str == null");
		}

		//データがnullの場合はエラー
		if(data == null){
			throw new NullPointerException("data == null");
		}

		//類似度パラメータを取得
		int[] rateArray = getSimilarityParam(str, data);

		//ソート
		MultiSortObject mso = new MultiSortObject(rateArray);
		mso.addArray(data);
		mso.sort();
	}

	/**
	 * @機能概要：類似度パラメータを取得（低い方が類似）
	 * @引数１：検索文字列
	 * @引数２：データ
	 */
	public synchronized int[] getSimilarityParam(String str, String[] data){
		int[] rateArray = new int[data.length];

		char[] s_tokens = str.trim().toLowerCase().toCharArray();
		char[] d_tokens = null;
		for(int n=0; n<data.length; n++){
			d_tokens = data[n].trim().toLowerCase().toCharArray();

			//評価
			for(int i=0; i<s_tokens.length; i++){
				for(int j=0; j<d_tokens.length; j++){
					if(s_tokens[i] == d_tokens[j]){
						for(int k=i,l=j; k<s_tokens.length && l<d_tokens.length; k++,l++){
							if(s_tokens[i] != d_tokens[j]){
								break;
							}
							rateArray[n]++;
						}
					}
				}
			}

			rateArray[n] -= (int)Math.sqrt(Math.abs(s_tokens.length - d_tokens.length));
			rateArray[n] = -rateArray[n];
		}

		return rateArray;
	}
}
