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

	/* getter */
	public boolean isValueOnly() {
		return isValueOnly;
	}
	public String getKey() {
		return key;
	}
	public Object getValue() {
		return value;
	}
	public ValueType getType() {
		return type;
	}

}

