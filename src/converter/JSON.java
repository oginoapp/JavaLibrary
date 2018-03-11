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
		//ノードの生成
		KVS kvs = new KVS(ValueType.Map,
			new KVS("a1", true),
			new KVS("a2", ValueType.List,
				new KVS("b1", 1234),
				new KVS("b2\",\\,\b,\f,\n,\r,\t,/,\u3042")
			)
		);

		//a1.b1のノードに値を追加
		KVS child1 = new KVS("c1", new KVS("d1", "d1data"));
		kvs.addNodeAt(child1, "a2", "b1");

		//{"a1":true,"a2":[{"b1":1234,{"c1":{"d1":"d1data"}}},"b2\",\\,\b,\f,\n,\r,\t,\/,あ"]}
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

		if(kvs.getValue() instanceof List) {
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
		} else {
			if(kvs.getType() == ValueType.String) json.append("\"");
			json.append(escape(kvs.getValue().toString()));
			if(kvs.getType() == ValueType.String) json.append("\"");
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
	 * JSONを整形する
	 */
	public static String format(String json) {
		if(json == null) return json;
		StringBuilder sb = new StringBuilder();

		//状態フラグ
		boolean quotSeq = false;
		boolean escapeSeq = false;
		int indent = 0;

		for(char c : json.toCharArray()) {
			//エスケープと囲いの判定
			if(!escapeSeq && c == '\\') {
				escapeSeq = true;
			} else if(escapeSeq) {
				escapeSeq = false;
			} else if(!quotSeq && !escapeSeq && c == '"') {
				quotSeq = true;
			} else if(quotSeq && !escapeSeq && c == '"') {
				quotSeq = false;
			}

			//連結
			if(!quotSeq && (
					c == ' ' ||
					c == '\t' ||
					c == '\r' ||
					c == '\n'
					)) {
				continue;
			} else {
				sb.append(c);
			}
		}

		json = sb.toString();
		sb = new StringBuilder();
		quotSeq = false;
		escapeSeq = false;
		indent = 0;

		for(char c : json.toCharArray()) {
			//文字列操作フラグ
			boolean beforeNewLine = false;
			boolean afterNewLine = false;
			boolean beforeSpace = false;
			boolean afterSpace = false;
			String strIndent = null;

			//エスケープと囲いの判定
			if(!escapeSeq && c == '\\') {
				escapeSeq = true;
			} else if(escapeSeq) {
				escapeSeq = false;
			} else if(!quotSeq && !escapeSeq && c == '"') {
				quotSeq = true;
			} else if(quotSeq && !escapeSeq && c == '"') {
				quotSeq = false;
			}

			//改行とインデントの判定
			if(!quotSeq && (c == '{' || c == '[')) {
				afterNewLine = true;
				indent += 1;
			} else if(!quotSeq && (c == '}' || c == ']')) {
				beforeNewLine = true;
				indent -= 1;
			} else if(!quotSeq && c == ':') {
				beforeSpace = true;
				afterSpace = true;
			} else if(!quotSeq && c == ',') {
				afterNewLine = true;
			}

			//連結
			if(indent > 0 && (beforeNewLine || afterNewLine))
				strIndent = String.format("%0" + indent + "d", 0).replace('0', '\t');
			if(beforeNewLine) sb.append('\n');
			if(indent > 0 && beforeNewLine) sb.append(strIndent);
			if(beforeSpace) sb.append(' ');
			sb.append(c);
			if(afterSpace) sb.append(' ');
			if(afterNewLine) sb.append('\n');
			if(indent > 0 && afterNewLine) sb.append(strIndent);
		}

		return sb.toString();
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
