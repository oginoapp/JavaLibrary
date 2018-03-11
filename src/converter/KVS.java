package converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
	public KVS(Object value) {
		this(null, value);
		isValueOnly = true;
	}

	/**
	 * キーと値のペア
	 */
	public KVS(String key, Object value) {
		this(key, value instanceof String ? ValueType.String : ValueType.Value, value);
	}

	/**
	 * キーと値の種類と値
	 */
	public KVS(String key, ValueType type, Object value) {
		this.key = key;
		this.type = type;
		this.value = value;
	}

	/**
	 * キーと値のペア
	 */
	public KVS(String key, KVS value) {
		this(key, ValueType.Map, new KVS[]{value});
	}

	/**
	 * 値が複数の場合
	 */
	public KVS(ValueType type, KVS... value) {
		this(null, type, value);
		isValueOnly = true;
	}

	/**
	 * 値が複数の場合
	 */
	public KVS(String key, ValueType type, KVS... value) {
		this.key = key;
		this.type = type;
		if(value != null) this.value = new ArrayList<>(Arrays.asList(value));
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

	/**
	 * 指定したノードの値にノードを追加する
	 * @param positions ノードの位置
	 */
	@SuppressWarnings("unchecked")
	public void addNodeAt(KVS newNode, String... positions) {
		KVS node = this;
		List<KVS> list = null;

		//場所を指定した場合
		if(positions != null) {
			node = getNodeAt(positions);
		}

		//値がリスト以外の場合はリストを作成
		if(node.value == null) {
			list = new ArrayList<KVS>();
		} else if(!(node.value instanceof List)) {
			list = new ArrayList<KVS>();
			list.add(new KVS(node.value));
		} else {
			list = (List<KVS>)node.value;
		}

		//ノードを追加
		list.add(newNode);

		node.value = list;
	}

	/**
	 * 指定した位置にあるノードを取得
	 * @param positions ノードの位置
	 */
	@SuppressWarnings("unchecked")
	public KVS getNodeAt(String... positions) {
		KVS node = this;

		if(positions == null) return key == null ? this : null;

		for(String key : positions) {
			if(key.equals(node.getKey())) {
				return node;
			} else if(node.getValue() instanceof List) {
				for(KVS child : ((List<KVS>)node.getValue())) {
					if (key.equals(child.getKey())) {
						node = child;
					}
				}
			} else {
				//取得できない場合
				return null;
			}
		}

		return node;
	}

}
