package converter;

/**
 * キーと値をペアで保持する構造
 */
public class KVS {

	/**
	 * サンプル
	 */
	public static void main(String[] args) {
		KVS kvs = new KVS(null,
			new KVS("a1", "a1_data"),
			new KVS("a2",
				new KVS("b1", "b1_data"),
				new KVS(null, "b2_data")
			)
		);

		//{"a1":"a1_data","a2":{"b1":"b1_data","b2_data"}}
		System.out.println(kvs.toJson());
	}

	/* キー */
	private String key;
	/* 値 */
	private Object value;

	public KVS(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public KVS(String key, KVS... value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Jsonの文字列に変換
	 */
	public String toJson() {
		StringBuilder json = new StringBuilder();

		if(key != null) {
			json.append("\"");
			json.append(escape(key));
			json.append("\":");
		}

		if(value == null) {
			json.append("null");
		} else if(value instanceof String) {
			json.append("\"");
			json.append(escape(value.toString()));
			json.append("\"");
		} else if(value instanceof KVS[]) {
			KVS[] values = (KVS[])value;

			json.append("{");
			for(int i = 0; i < values.length; i++) {
				json.append(values[i].toJson());
				if(i < values.length - 1)
					json.append(",");
			}
			json.append("}");
		}

		return json.toString();
	}

	/**
	 * 値をエスケープ
	 */
	private String escape(String str) {
		StringBuilder sb = new StringBuilder();

		for(char c : str.toCharArray()) {
			switch(c) {
			case '"':
				sb.append("\\\\\"");
			default:
				sb.append(c);
			}
		}

		return sb.toString();
	}

}



