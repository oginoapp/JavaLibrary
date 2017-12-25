package net;
import java.util.HashMap;

/**
 * @クラス説明：パケットを文字列で管理するクラス
 *              パケットの属性を追加、取得できる
 * @使い方：PacketBuilder pb = new PacketBuilder();
 *          pb.setPacket("a=1&B=2;3;4&c=5");
 *          pb.addAttribute("d", "6");
 *          System.out.println(pb.toString());
 * @更新履歴：20160730 - 新規作成
 *            20160731 - setAttributeメソッド、removeAttributeメソッドの追加
 *            20160801 - 値のない属性も反映するように変更
 */
public class PacketBuilder{
	public static final String SEPARATOR_ATTRIBUTES = "&";
	public static final String SEPARATOR_EQUAL = "=";
	public static final String SEPARATOR_VALUES = ";";

	/* パケットの属性を管理するマップ */
	private HashMap<String, String[]> attributes = null;

	/**
	 * @機能概要：引数なしのコンストラクタ
	 */
	public PacketBuilder(){
		this(null);
	}

	/**
	 * @機能概要：コンストラクタ
	 * @引数１：文字列のパケット
	 */
	public PacketBuilder(String packet){
		setPacket(packet);
	}

	/**
	 * @機能概要：パケットをこのオブジェクトにセットする
	 * @引数1:文字列のパケット
	 * @引数の例：packet = "  1=123;234&  2=abc&100=345;678&101=&102=;";
	 */
	public void setPacket(String packet){
		this.attributes = new HashMap<>();

		if(packet == null){
			return;
		}

		String[] entries = packet.split(SEPARATOR_ATTRIBUTES);

		for(int i=0; i<entries.length; i++){
			String[] tmp = entries[i].split(SEPARATOR_EQUAL);
			if(tmp.length == 2){
				attributes.put(tmp[0], tmp[1].split(SEPARATOR_VALUES));
			}else
			if(tmp.length == 1){
				attributes.put(tmp[0], new String[0]);
			}
		}
	}

	/**
	 * @機能概要：パケットに属性を追加する
	 * @引数１：属性の名前
	 * @引数２：属性値
	 */
	public void addAttribute(String name, String value){
		addAttribute(name, new String[]{value});
	}
	public void addAttribute(String name, String[] values){
		if(values != null){
			if(attributes.containsKey(name)){
				String[] oldValues = getAttribute(name);
				String[] newValues = new String[oldValues.length + values.length];

				System.arraycopy(oldValues,0,newValues,0,oldValues.length);
				System.arraycopy(values,0,newValues,oldValues.length, values.length);

				attributes.put(name, newValues);
			}else{
				attributes.put(name, values);
			}
		}
	}

	/**
	 * @機能概要：パケットの属性を変更する
	 * @引数１：属性の名前
	 * @引数２：属性値
	 */
	public void setAttribute(String name, String value){
		setAttribute(name,new String[]{value});
	}
	public void setAttribute(String name, String[] values){
		if(values != null){
			attributes.put(name, values);
		}
	}

	/**
	 * @機能概要：パケットから指定した属性を削除する
	 * @引数１：属性の名前
	 */
	public void removeAttribute(String name){
		attributes.remove(name);
	}

	/**
	 * @機能概要：パケットの属性値を属性の名前から取得する
	 * @引数１：属性の名前
	 */
	public String[] getAttribute(String name){
		return attributes.get(name);
	}

	/**
	 * @機能概要：パケットを文字列として取得する
	 */
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();

		String[] keyArray = attributes.keySet().toArray(new String[attributes.size()]);
		for(int i=0; i<keyArray.length; i++){
			//キーと連結用符号の追加
			sb.append(keyArray[i]);
			sb.append(SEPARATOR_EQUAL);

			//値の配列を追加
			String[] values = attributes.get(keyArray[i]);
			for(int j=0; j<values.length; j++){
				sb.append(values[j]);
				if(j < values.length - 1){
					sb.append(SEPARATOR_VALUES);
				}
			}

			//属性の分割用符号の追加
			if(i < keyArray.length - 1){
				sb.append(SEPARATOR_ATTRIBUTES);
			}
		}

		return sb.toString();
	}
}