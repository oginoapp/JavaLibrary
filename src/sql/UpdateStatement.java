package sql;

import java.util.ArrayList;

/**
 * @クラス説明：UPDATE文を生成する
 */
public class UpdateStatement{
	/* 例："table_name" */
	private String tableName = null;

	/* 例：add("col=val") */
	private ArrayList<String> set_attributes = null;

	/* 例：add("col=val") */
	private ArrayList<String> where_attributes = null;

	/**
	 * @機能概要：コンストラクタでテーブル名をセットする
	 * @引数１：テーブル名
	 */
	public UpdateStatement(String tableName){
		if(tableName == null || tableName.isEmpty()){
			System.err.println("tableName == null || tableName.isEmpty()");
		}
		this.tableName = tableName;
		set_attributes = new ArrayList<String>();
		where_attributes = new ArrayList<String>();
	}

	/**
	 * @機能概要：セットする項目と値の指定のための属性を追加する
	 * @引数１：col(String)
	 * @引数２：val(Object)
	 */
	public void addSetAttribute(String col, Object val){
		StringBuilder attribute = new StringBuilder(col);
		String quot;

		if(isNumber(val)){
			quot = "";
		}else{
			quot = "'";
		}
		attribute.append("=");
		attribute.append(quot);
		attribute.append(String.valueOf(val));
		attribute.append(quot);

		set_attributes.add(attribute.toString());
	}

	/**
	 * @機能概要：WHEREの条件指定のための属性を追加する
	 * @引数１：col(String)
	 * @引数２：val(Object)
	 */
	public void addWhereAttribute(String col, Object val){
		StringBuilder attribute = new StringBuilder(col);
		String quot;

		if(isNumber(val)){
			quot = "";
		}else{
			quot = "'";
		}
		attribute.append("=");
		attribute.append(quot);
		attribute.append(String.valueOf(val));
		attribute.append(quot);

		where_attributes.add(attribute.toString());
	}

	/**
	 * @機能概要：UPDATE文を生成する
	 * @戻り値：完成したUPDATE文
	 */
	public String toString(){
		//クエリの準備
		StringBuilder sql = new StringBuilder("UPDATE ");

		sql.append(tableName);

		sql.append(" SET ");

		//セットする項目と値の指定を追加
		if(set_attributes == null || set_attributes.size() == 0){
			System.err.println("set_attributes == null || set_attributes.size() == 0");
		}else{
			for(int i=0; i<set_attributes.size(); i++){
				sql.append(set_attributes.get(i));
				if(i < set_attributes.size() - 1){
					sql.append(",");
				}
			}
		}

		sql.append(" WHERE ");

		//指定する条件を追加
		if(where_attributes == null || where_attributes.size() == 0){
			sql.append("1");
		}else{
			for(int i=0; i<where_attributes.size(); i++){
				sql.append(where_attributes.get(i));
				if(i < where_attributes.size() - 1){
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
