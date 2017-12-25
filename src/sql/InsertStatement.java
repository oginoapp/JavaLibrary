package sql;
import java.util.ArrayList;

/**
 * @クラス説明：INSERT文を生成する
 */
public class InsertStatement{
	/* 例："table_name" */
	private String tableName = null;

	/* ペアになっている */
	private ArrayList<String> cols = null;
	private ArrayList<Object> vals = null;

	/**
	 * @機能概要：コンストラクタでテーブル名をセットする
	 * @引数１：テーブル名
	 */
	public InsertStatement(String tableName){
		//エラーチェック
		if(tableName == null || tableName.isEmpty()){
			System.err.println("tableName == null || tableName.isEmpty()");
		}
		this.tableName = tableName;
		cols = new ArrayList<String>();
		vals = new ArrayList<Object>();
	}

	/**
	 * @機能概要：挿入する項目名と値を追加する
	 * @引数１：col(String)
	 * @引数２：val(Object)
	 */
	public void addAttribute(String col, Object val){
		//エラーチェック
		if(col == null || col.isEmpty()){
			System.err.println("col == null || col.isEmpty()");
		}

		String value = "";
		String quot;

		if(isNumber(val)){
			quot = "";
		}else{
			quot = "'";
		}

		value += quot;
		value += String.valueOf(val);
		value += quot;

		cols.add(col);
		vals.add(value);
	}

	/**
	 * @機能概要：クエリを生成する
	 * @戻り値：完成したINSERT文
	 */
	public String toString(){
		//エラーチェック
		if(cols.size() != vals.size()){
			System.err.println("cols.size() != vals.size()");
		}

		//クエリの準備
		StringBuilder sql = new StringBuilder("INSERT INTO ");

		sql.append(tableName);
		sql.append("(");

		//項目名を追加
		for(int i=0; i<cols.size(); i++){
			sql.append(cols.get(i));
			if(i != cols.size() - 1){
				sql.append(",");
			}
		}

		sql.append(") VALUES (");

		//値を追加
		for(int i=0; i<vals.size(); i++){
			String quot = "";
			if(isNumber(vals.get(i))){
				quot = "";
			}else{
				quot = "'";
			}

			sql.append(quot);
			sql.append(String.valueOf(vals.get(i)));
			sql.append(quot);

			if(i < vals.size() - 1){
				sql.append(",");
			}
		}

		sql.append(");");

		return sql.toString();
	}

	/**
	 * @機能概要：項目名を指定しないINSERT文を生成する
	 * @引数１：１行に挿入する値の全て
	 * @戻り値：完成したINSERT文
	 */
	public String toString(Object... values){
		StringBuilder sql = new StringBuilder("INSERT INTO ");

		sql.append(tableName);
		sql.append(" VALUES (");

		//値を追加
		for(int i=0; i<values.length; i++){
			String quot = "";
			if(isNumber(values[i])){
				quot = "";
			}else{
				quot = "'";
			}

			sql.append(quot);
			sql.append(String.valueOf(values[i]));
			sql.append(quot);

			if(i < vals.size() - 1){
				sql.append(",");
			}
		}

		sql.append(");");

		return sql.toString();
	}

	/**
	 * @機能概要：数値かどうかを判定する
	 * @引数１：判定する値(Object)
	 * @戻り値：数値かどうか
	 */
	private boolean isNumber(Object val){
		boolean result = true;
		try{
			Long.parseLong(String.valueOf(val));
		}catch(NumberFormatException ex){
			result = false;
		}
		return result;
	}
}
