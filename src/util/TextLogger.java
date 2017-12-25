package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 更新履歴：
 * 20160612 - fileInのクローズ
 */
public class TextLogger {
	private File logFile = null;
	/**
	 * コンストラクタ
	 * ログファイル初期化
	 */
	public TextLogger(String filePath){
		setLogFile(filePath);
	}

	/**
	 * ログファイルを指定
	 */
	public void setLogFile(String filePath){
		logFile = new File(filePath);
	}

	/**
	 * ファイルの容量を取得する
	 * 戻り値 = ファイルの容量:long
	 */
	public long length(){
		return logFile.length();
	}

	/**
	 * ファイルの行数を取得する
	 * 戻り値 = ファイルの行数
	 */
	public int rowCount(){
		int rowCount = 0;
		BufferedReader fileIn = null;
		try{
			fileIn = new BufferedReader(new FileReader(logFile));
			while(fileIn.readLine() != null){
				rowCount++;
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				fileIn.close();
			}catch(IOException ex){}
		}

		return rowCount;
	}

	/**
	 * ファイルのデータを取得する
	 * 戻り値 = ファイルの全データ:String
	 */
	public String getData(){
		StringBuilder sb = new StringBuilder();
		BufferedReader fileIn = null;
		try{
			fileIn = new BufferedReader(new FileReader(logFile));
			for(String row; (row = fileIn.readLine()) != null;){
				sb.append(row);
				sb.append("\n");
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				fileIn.close();
			}catch(IOException ex){}
		}

		return sb.toString();
	}

	/**
	 * ファイルに文字列を追加する
	 */
	public void println(String str){
		print(str + "\n");
	}
	public void print(String str){
		BufferedWriter fileOut = null;

		try{
			fileOut = new BufferedWriter(new FileWriter(logFile, true));
			fileOut.write(str);
			fileOut.flush();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				fileOut.close();
			}catch(IOException ex){}
		}
	}


}
