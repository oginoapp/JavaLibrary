package converter;

import java.util.List;

import converter.KVS.ValueType;

/**
 * JSONを扱うクラス
 */
public class JSON {

	/**
	 * サンプル
	 */
	public static void test() {
		KVS kvs = new KVS(ValueType.Map,
			new KVS("a1", "a1_data"),
			new KVS("a2", ValueType.List,
				new KVS("b1", "b1_data"),
				new KVS("\",\\,\b,\f,\n,\r,\t,/,\u3042")
			)
		);

		//{"a1":"a1_data","a2":[{"b1":"b1_data"},"\",\\,\b,\f,\n,\r,\t,\/,あ"]}
		JSON json = new JSON(kvs);
		System.out.println(json);
	}

	/**
	 * エスケープする文字の種類
	 */
	public static class EscapeOption {
		public boolean quotationMark = true;
		public boolean reverseSolidus = true;
		public boolean backSpace = true;
		public boolean formFeed = true;
		public boolean newLine = true;
		public boolean carriageReturn = true;
		public boolean horizontalTab = true;
		public boolean slash = true;
	}

	/* KeyValue形式のデータ */
	private KVS kvs;
	/* エスケープ設定 */
	private EscapeOption escape;

	/**
	 * コンストラクタ
	 * @param kvs KeyValue形式のデータ
	 */
	public JSON(KVS kvs) {
		this(kvs, new EscapeOption());
	}

	public JSON(KVS kvs, EscapeOption escape) {
		this.kvs = kvs;
		this.escape = escape;
	}

	/**
	 * JSONの文字列を取得
	 */
	@Override
	public String toString() {
		StringBuilder json = new StringBuilder();

		if(!kvs.isValueOnly()) {
			json.append("\"");
			json.append(escape(kvs.getKey()));
			json.append("\":");
		}

		switch(kvs.getType()) {
		case Map:
			json.append("{");
			break;
		case List:
			json.append("[");
			break;
		default:
			break;
		}

		if(kvs.getValue() instanceof String) {
			if(kvs.getType() == ValueType.String) json.append("\"");
			json.append(escape(kvs.getValue().toString()));
			if(kvs.getType() == ValueType.String) json.append("\"");
		} else if(kvs.getValue() instanceof List) {
			@SuppressWarnings("unchecked")
			List<KVS> values = (List<KVS>)kvs.getValue();

			for(int i = 0; i < values.size(); i++) {
				boolean quot = kvs.getType() != ValueType.Map
						&& !values.get(i).isValueOnly();

				if(quot) json.append("{");
				json.append(new JSON(values.get(i)).toString());
				if(quot) json.append("}");

				if(i < values.size() - 1) json.append(",");
			}
		}

		switch(kvs.getType()) {
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
			if(escape.quotationMark && c == '"') {
				sb.append("\\\"");
			} else if(escape.reverseSolidus && c == '\\') {
				sb.append("\\\\");
			} else if(escape.backSpace && c == '\b') {
				sb.append("\\b");
			} else if(escape.formFeed && c == '\f') {
				sb.append("\\f");
			} else if(escape.newLine && c == '\n') {
				sb.append("\\n");
			} else if(escape.carriageReturn && c == '\r') {
				sb.append("\\r");
			} else if(escape.horizontalTab && c == '\t') {
				sb.append("\\t");
			} else if(escape.slash && c == '/') {
				sb.append("\\/");
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

}
