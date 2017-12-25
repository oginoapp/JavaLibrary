package sql;

import java.util.ArrayList;

/**
 * @クラス説明：DELETE文を生成する
 */
public class DeleteStatement{
	/* 例："table_name" */
	private String tableName = null;

	/* 例：add("col=val") */
	private ArrayList<String> attributes = null;

	/**
	 * @機能概要：コンストラクタでテーブル名をセットする
	 * @引数１：テーブル名
	 */
	public DeleteStatement(String tableName){
		if(tableName == null || tableName.isEmpty()){
			System.err.println("tableName == null || tableName.isEmpty()");
		}
		this.tableName = tableName;
		attributes = new ArrayList<String>();
	}

	/**
	 * @機能概要：WHERE指定のための属性を追加する
	 * @引数１：col(String)
	 * @引数２：val(Object)
	 */
	public void addAttribute(String col, Object val){
		String attribute = "";
		String quot;

		if(isNumber(val)){
			quot = "";
		}else{
			quot = "'";
		}

		attribute += col;
		attribute += "=";
		attribute += quot;
		attribute += String.valueOf(val);
		attribute += quot;

		attributes.add(attribute);
	}

	/**
	 * @機能概要：クエリを生成する
	 * @戻り値：完成したDELETE文
	 */
	public String toString(){
		StringBuilder sql = new StringBuilder("DELETE FROM ");

		sql.append(tableName);
		sql.append(" WHERE ");

		//指定する条件を追加
		if(attributes == null || attributes.size() == 0){
			System.err.println("attributes == null || attributes.size() == 0");
			sql.append("0");
		}else{
			for(int i=0; i<attributes.size(); i++){
				sql.append(attributes.get(i));
				if(i < attributes.size() - 1){
					sql.append(" AND ");
				}
			}
		}

		sql.append(";");

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
