package lang;

/**
 * 62進数の変換を行うクラス
 */
public class Base62{

	private static final char[] chars62 = new char[]{
		'0','1','2','3','4','5','6','7','8','9',
		'a','b','c','d','e','f','g','h','i','j',
		'k','l','m','n','o','p','q','r','s','t',
		'u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','I','J',
		'K','L','M','N','O','P','Q','R','S','T',
		'U','V','W','X','Y','Z'
	};

	/**
	 * long型数値⇒62進数文字列
	 * @param num long型数値
	 */
	public static String encodeLong(long num){
		StringBuilder base62 = new StringBuilder();

		long tmp = num;
		long mod = 0;
		while(tmp >= 62){
			mod = tmp % 62;
			tmp = tmp / 62;

			base62 = base62.insert(0, chars62[(int)mod]);
		}
		mod = tmp % 62;
		base62 = base62.insert(0, chars62[(int)mod]);

		return base62.toString();
	}

	/**
	 * 62進数文字列⇒long型数値
	 * @param decimal62 62進数文字列
	 */
	public static long decodeToLong(String decimal62){
		long result = 0;

		char[] chars = decimal62.toCharArray();
		int power = 1;
		for(int i = chars.length - 1; i >= 0; i--, power *= 62 ){
			int base = -1;
			for(int j = 0; i < chars62.length; j++){
				if(chars[i] == chars62[j]){
					base = j;
					break;
				}
			}
			result += base * power;
		}

		return result;
	}

}
