package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @クラス説明：生成時にディレクトリを指定する。ファイルをメソッドで指定してからデータの入出力を行う。
 * @使い方：TextLogManager log = new TextLogManager("C:\\test");
 *          log.setLogFile("test.txt");
 *          log.println("Hello Log");
 *          String data = log.getData();
 *          System.out.println(data);
 * @更新履歴：
 * 20160613 - 新規作成
 * 20160619 - getRowCountメソッド、getLengthメソッド、ファイル名指定時のエスケープ処理、ファイル書き込みメソッド
 * 20160731 - lastFileNameメソッド、existsメソッドの追加
 * 20160818 - パス名調整部分を削除
 */
public class TextLogManager {

	private String dirPath = null;
	private File logFile = null;
	private String lastFileName = null;

	/**
	 * @機能概要：扱うディレクトリを指定
	 * @引数１：扱うディレクトリのパス
	 * @注意事項：ディレクトリが存在しない場合はFileNotFoundExceptionを投げる
	 */
	public TextLogManager(String dirPath) throws FileNotFoundException{
		//ディレクトリが存在しない場合にエラー
		File dir = new File(dirPath);
		if(!dir.exists() || !dir.isDirectory()){
			throw new FileNotFoundException("dir is not exists. path: " + dirPath);
		}

		this.dirPath = dirPath;
	}

	/**
	 * @機能概要：ログファイルを指定（ファイルの入出力が可能になる）
	 * @引数１ = 使用するファイル名（拡張子含む）
	 * @引数２ = create：ファイルが存在しなかったら新しく生成
	 */
	public void setLogFile(String fileName){setLogFile(fileName, false);}
	public void setLogFile(String fileName, boolean create){
		logFile = new File(dirPath + fileName);
		if(create && !logFile.exists()){
			try{
				logFile.createNewFile();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		this.lastFileName = fileName;
	}

	/**
	 * @機能概要：最後に使ったファイル名を戻す
	 * @戻り値：最後に使ったファイル名（拡張子含む）
	 */
	public String lastFileName(){
		return this.lastFileName;
	}

	/**
	 * @機能概要：ファイルの存在確認
	 * @戻り値：ファイルの存在（boolean）
	 */
	public boolean exists(){
		return logFile.exists();
	}

	/**
	 * @機能概要：ファイルのサイズを取得する
	 * @戻り値：ファイルのサイズ(バイト数):long
	 */
	public long getLength(){
		return logFile.length();
	}

	/**
	 * @機能概要：ファイルの行数を取得する
	 * @戻り値：ファイルの行数
	 */
	public int getRowCount(){
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
	 * @機能概要：ログファイルの全データを読み取る
	 * @戻り値：ログファイルのデータ（String）
	 */
	public String getData(){
		StringBuilder data = new StringBuilder();
		FileInputStream fileIn = null;
		try{
			fileIn = new FileInputStream(logFile);
			byte[] buf = new byte[1];
			while(fileIn.read(buf) != -1){
				data.append(new String(buf, "UTF-8"));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				fileIn.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return data.toString();
	}

	/**
	 * @機能概要：ファイルに文字列を追加する
	 * @引数：追加する文字列
	 */
	public synchronized void println(Object str){
		print(str + "\n");
	}
	public void print(Object str){
		BufferedWriter fileOut = null;

		try{
			fileOut = new BufferedWriter(new FileWriter(logFile, true));
			fileOut.write(String.valueOf(str));
			fileOut.flush();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				fileOut.close();
			}catch(IOException ex){}
		}
	}

	/**
	 * @機能概要：ファイル名をエスケープ
	 * @引数１：エスケープが必要なファイル名
	 * @戻り値：使用不可文字を"_"にエスケープされたファイル名:String
	 */
	public String escapedFileName(String fileName){
		return escapedFileName(fileName, "_");
	}

	/**
	 * @機能概要：ファイル名をエスケープ
	 * @引数１：エスケープが必要なファイル名
	 * @引数２：エスケープ後の文字
	 * @戻り値：使用不可文字を指定した文字にエスケープされたファイル名:String
	 */
	public String escapedFileName(String fileName, String replacement){
		String escapedName = null;

		Pattern pattern = Pattern.compile("[(\\|/|:|\\*|?|\"|<|>|\\\\|)]");
		escapedName = pattern.matcher(fileName).replaceAll(replacement);

		return escapedName;
	}



}
