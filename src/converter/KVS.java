package converter;

/**
 * キーと値をペアで保持する構造
 */
public class KVS {

	/**
	 * 値の種類
	 */
	public static enum ValueType {
		Value,
		String,
		List,
		Map
	}

	/**
	 * サンプル
	 */
	public static void main(String[] args) {
		KVS kvs = new KVS(ValueType.Map,
			new KVS("a1", "a1_data"),
			new KVS("a2", ValueType.List,
				new KVS("b1", "b1_data"),
				new KVS("data")
			)
		);

		//{"a1":"a1_data","a2":[{"b1":"b1_data"},"data"]}
		System.out.println(kvs.toJson());
	}

	/* キー */
	private String key;
	/* 値 */
	private Object value;
	/* 値の種類 */
	private ValueType type = ValueType.String;
	/* 値のみであるか */
	private boolean isValueOnly;

	/**
	 * 値のみ
	 */
	public KVS(String value) {
		this(null, value);
		isValueOnly = true;
	}

	/**
	 * キーと値のペア
	 */
	public KVS(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * 値がMapかListの場合
	 */
	public KVS(ValueType type, KVS... value) {
		this(null, type, value);
		isValueOnly = true;
	}

	/**
	 * 値がMapかListの場合
	 */
	public KVS(String key, ValueType type, KVS... value) {
		this.key = key;
		this.type = type;
		this.value = value;
	}

	/**
	 * Jsonの文字列に変換
	 */
	public String toJson() {
		StringBuilder json = new StringBuilder();

		if(!isValueOnly) {
			json.append("\"");
			json.append(escape(key));
			json.append("\":");
		}

		switch(type) {
		case Map:
			json.append("{");
			break;
		case List:
			json.append("[");
			break;
		default:
			break;
		}

		if(value instanceof String) {
			if(type == ValueType.String) json.append("\"");
			json.append(escape(value.toString()));
			if(type == ValueType.String) json.append("\"");
		} else if(value instanceof KVS[]) {
			KVS[] values = (KVS[])value;

			for(int i = 0; i < values.length; i++) {
				boolean quot = type != ValueType.Map && !values[i].isValueOnly;
				if(quot)
					json.append("{");
				json.append(values[i].toJson());
				if(quot)
					json.append("}");
				if(i < values.length - 1)
					json.append(",");
			}
		}

		switch(type) {
		case Map:
			json.append("}");
			break;
		case List:
			json.append("]");
			break;
		default:
			break;
		}

		return json.toString();
	}

	/**
	 * 値をエスケープ
	 */
	private String escape(String str) {
		if(str == null) return str;
		StringBuilder sb = new StringBuilder();

		for(char c : str.toCharArray()) {
			switch(c) {
			case '"':
				sb.append("\\\"");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(c);
				break;
			}
		}

		return sb.toString();
	}

}

