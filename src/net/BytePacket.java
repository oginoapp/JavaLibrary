package net;

public class BytePacket{
	final public static String title="Length:";
	final public static int headBounds=new String(title+"111111").getBytes().length;

	public byte[] makePacket(byte[] data){
		//データの長さをヘッダーに格納
		byte[] header=(title+fill(String.valueOf(data.length))).getBytes();

		//ヘッダーとデータの連結
		byte[] result=new byte[header.length+data.length];
		System.arraycopy(header,0,result,0,header.length);
		System.arraycopy(data,0,result,header.length, data.length);

		return result;
	}

	private String fill(String val){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<6-val.length();i++){
			sb.append(" ");
		}
		return sb.append(val).toString();
	}
}
