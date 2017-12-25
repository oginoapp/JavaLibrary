package util;

/**
 * @更新履歴：
 * 20160926 - 新規作成
 */
public class HTMLUtility{

	/**
	 * @機能概要：出力用HTMLのエスケープ
	 * @引数１：エスケープする文字列
	 * @戻り値：エスケープされたHTML
	 */
	public static String escapeHtml(String str){
        if(str == null){
			str = "";
		}

		StringBuilder result = new StringBuilder();
		for(char c : str.toCharArray()){
			switch (c){
			case '&' :
				result.append("&amp;");
				break;
			case '<' :
				result.append("&lt;");
				break;
			case '>' :
				result.append("&gt;");
				break;
			case '"' :
				result.append("&quot;");
				break;
			case '\'' :
				result.append("&#39;");
				break;
			case ' ' :
				result.append("&nbsp;");
				break;
			default :
				result.append(c);
				break;
			}
		}
		return result.toString();
	}

	/**
	 * @機能概要：HTMLタグ<select>を作成する
	 * @引数１：name属性
	 * @引数２：style属性
	 * @引数３：選択肢の値の配列
	 * @引数４：選択中の値
	 * @戻り値：完成したHTML
	 */
	public static String createSelectTag(String nameAttr, String style, String[] options, String selectedValue, String onChange){
		StringBuilder html = new StringBuilder();

		//nullチェック
		if(nameAttr == null){
			nameAttr = "";
		}
		if(style == null){
			style = "";
		}
		if(selectedValue == null){
			selectedValue = "";
		}
		if(onChange == null){
			onChange = "";
		}

		//エスケープ処理
		char quot = '\'';
		char[] chars = onChange.toCharArray();
		boolean replaceFlg = false;
		for(int i=0; i<chars.length; i++){
			if(chars[i] == '\'' || chars[i] == '\"'){
				if(!replaceFlg){
					if(chars[i] == quot){
						replaceFlg = true;
					}else{
						break;
					}
				}
				if(replaceFlg){
					if(chars[i] == '\''){
						chars[i] = '\"';
					}else if(chars[i] == '\"'){
						chars[i] = '\'';
					}
				}
			}
		}
		onChange = new String(chars);

		//html組み立て - select
		html
		.append("<select name='")
		.append(nameAttr)
		.append("' style='")
		.append(style)
		.append("' onchange='")
		.append(onChange)
		.append("'>");

		//html組み立て - options
		for(int i=0; i<options.length; i++){
			html
			.append("<option value='")
			.append(options[i])
			.append("' ")
			.append(options[i].equals(selectedValue) ? "selected" : "")
			.append(">")
			.append(options[i])
			.append("</option>");
		}

		//html組み立て - select
		html.append("</select>");

		return html.toString();
	}

	/**
	 * @機能概要：HTMLタグ<input>を作成する
	 * @引数１：name属性
	 * @引数２：style属性
	 * @引数３：type属性
	 * @引数４：初期値
	 * @戻り値：完成したHTML
	 */
	public static String createInputTag(String nameAttr, String style, String type, String value){
		StringBuilder html = new StringBuilder();

		if(nameAttr == null){
			nameAttr = "";
		}
		if(style == null){
			style = "";
		}
		if(type == null){
			type = "";
		}
		if(value == null){
			value = "";
		}

		html
		.append("<input name='")
		.append(nameAttr)
		.append("' style='")
		.append(style)
		.append("' type='")
		.append(type)
		.append("' value='")
		.append(value)
		.append("'/>");

		return html.toString();
	}

	/**
	 * @機能概要：HTMLタグ<textarea>を作成する
	 * @引数１：name属性
	 * @引数２：style属性
	 * @引数３：初期値
	 * @引数４：行数
	 * @戻り値：完成したHTML
	 */
	public static String createTextAreaTag(String nameAttr, String style, String value, int rows){
		StringBuilder html = new StringBuilder();

		if(nameAttr == null){
			nameAttr = "";
		}
		if(style == null){
			style = "";
		}
		if(value == null){
			value = "";
		}

		html
		.append("<textarea spellcheck='false' name='")
		.append(nameAttr)
		.append("' style='")
		.append(style)
		.append("' rows='")
		.append(rows)
		.append("'>")
		.append(value)
		.append("</textarea>");

		return html.toString();
	}

    /**
	 * @機能概要：外側を囲っているクォーテーションと競合しないようにエスケープ
	 * @引数１：文字列
	 * @引数２：外側を囲っているクォーテーション
	 * @戻り値：エスケープされた文字列
	 */
	public static String quotEscape(String str, char outerQuot){
		char[] chars = str.toCharArray();
		boolean replaceFlg = false;
		for(int i=0; i<chars.length; i++){
			if(chars[i] == '\'' || chars[i] == '\"'){
				if(!replaceFlg){
					if(chars[i] == outerQuot){
						replaceFlg = true;
					}else{
						break;
					}
				}
				if(replaceFlg){
					if(chars[i] == '\''){
						chars[i] = '\"';
					}else if(chars[i] == '\"'){
						chars[i] = '\'';
					}
				}
			}
		}
		return new String(chars);
	}

	/**
	 * @機能概要：toHtmlLineの引数1つのオーバーロード
	 * @引数１：文字列
	 */
	public static String toHtmlLine(Object value){
		return toHtmlLine(value, "black");
	}

	/**
	 * @機能概要：渡された文字列を色付きのHTMLに変換する
	 * @引数１：文字列
	 * @引数２：色コードもしくは名前
	 * @戻り値：色のついたHTML
	 */
	public static String toHtmlLine(Object value, String color){
		String html = "";
		color = HTMLUtility.escapeHtml(color);

		if(value != null){
			html = HTMLUtility.escapeHtml(String.valueOf(value));
		}

		return "<span style='color: " + color + "'>" + html + "</span><br/>";
	}

}

