package util;

import java.io.File;

public class FileUtility{

	/**
	 * ファイルのパスを受け取り、ファイル名を返す。
	 * ファイル名にディレクトリ名が含まれないようにする
	 * ディレクトリトラバーサル対策
	 * @param path ファイルのパス
	 * @return
	 */
	public static String getFileBaseName(String path){
		String result = null;
		if(path != null){
			File file = new File(path);
			result = file.getName();
		}
		return result;
	}

	/**
	 * @see ファイルパス表現を統一する
	 * @param oldPath 修正するファイルパス(String)
	 * @param separator セパレータ(char)
	 * @param lastSeparator 最後のセパレータを有りにするかどうか(boolean)
	 */
	public static String convertFilePath(String oldPath, char separator, boolean lastSeparator){
		String newPath = oldPath;

		try{
			//セパレータの統一
			newPath = newPath.replace('\\', separator);
			newPath = newPath.replace('/', separator);

			//最後のセパレータ
			if(lastSeparator){
				if(newPath.charAt(newPath.length() - 1) != separator){
					newPath += separator;
				}
			}else{
				if(newPath.charAt(newPath.length() - 1) == separator){
					newPath = newPath.substring(0, newPath.length() - 1);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return newPath;
	}

	/**
	 * @see ファイルのサイズを取得する
	 * @see ディレクトリの場合は、再帰で取得したサイズの合計を戻す
	 * @param ファイルオブジェクト
	 * @return 再帰で取得したサイズの合計(バイト)
	 */
	public static long getAllFileSize(File file){
		long size = 0L;

		if (file == null){
			return size;
		}

		if (file.isDirectory()){
			File files[] = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++){
					size += getAllFileSize(files[i]);
				}
			}
		}else{
			size = file.length();
		}

		return size;
	}

	/**
	 * @機能概要：ファイル名の拡張子を取得する
	 * @引数１：ファイル名
	 * @戻り値：ファイルの拡張子
	 * @戻り値_ファイル名が存在しない：空文字
	 * @戻り値_拡張子が存在しない：空文字
	 */
	public static String getExtension(String fileName){
		String extension = "";

		//ファイル名を正規化
		fileName = getFileBaseName(fileName);

		//ファイル名が存在しない
		if(fileName == null || fileName.isEmpty()){
			return extension;
		}

		//拡張子が存在しない
		if(fileName.indexOf(".") == -1){
			return extension;
		}

		//拡張子を取得
		extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

		return extension;
	}

}
