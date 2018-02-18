package test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import interfaces.StringEncryptor;
import security.MyCrypt128;
import security.MyCrypt32;

public class Test {

	/**
	 * ピラミッドを出力
	 * @params borderOnly 枠線のみ
	 */
	public static void printPyramid(int height, boolean borderOnly) {
		for(int i = 1; i <= height; i++) {
			for(int j = 1; j < height + i; j++) {
				boolean empty = borderOnly && j > height - i + 1 &&
						j != height + i - 1 && i != height;
				if(j <= height - i || empty) {
					System.out.print(" ");
				} else {
					System.out.print("*");
				}
			}
			System.out.println();
		}
	}

	/**
	 * ひし形を出力
	 * @params borderOnly 枠線のみ
	 */
	public static void printDiamond(int size, boolean borderOnly) {
		final int half = (int)Math.ceil(size / 2D);
		int i = 1;
		int increase = 1;
		while(i > 0) {
			for(int j = 0; j < half + i - 1; j++) {
				boolean empty = borderOnly && j != half - i && j != half + i - 2;
				if(j < half - i || empty) {
					System.out.print(" ");
				} else {
					System.out.print("*");
				}
			}
			System.out.println();

			i += increase;
			if(i >= half) increase = -increase;
		}
	}

	/**
	 * 楕円を出力
	 * @param raduis 半径
	 * @param horizontalRatio 横の倍率
	 * @param verticalRatio 縦の倍率
	 */
	public static void printEllipse(int radius, double horizontalRatio, double verticalRatio) {
		double step = Math.PI / radius / 180;

		char dot = '■';
		char space = '　';

		//円周の座標リスト
		List<Point> pointList = new ArrayList<>();
		for(double radian = 0; radian < Math.PI * 2; radian += step) {
			double x = radius * Math.cos(radian) * horizontalRatio;
			double y = radius * Math.sin(radian) * verticalRatio;
			pointList.add(new Point((int)Math.round(x), (int)Math.round(y)));
		}

		//楕円向けのサイズ調整
		double ratio = verticalRatio > horizontalRatio ? verticalRatio : horizontalRatio;
		radius = (int)Math.ceil(radius * ratio);

		//テーブルに書き込み
		int size = (radius + 1) * 2 - 1;
		boolean[][] table = new boolean[size][size];
		for(int i = 0; i < pointList.size(); i++) {
			Point p = pointList.get(i);
			table[p.y + radius][p.x + radius] = true;
		}

		//出力
		for(int i = 0; i < table.length; i++) {
			for(int j = 0; j < table[i].length; j++) {
				if(table[i][j]) {
					System.out.print(dot);
				} else {
					System.out.print(space);
				}
			}
			System.out.println();
		}
	}

	/**
	 * 32bit暗号攻撃テスト
	 */
	public static void cipherAttackTest32(){
		StringEncryptor encryptor = null;

		//平文と暗号文のペアを作成
		Random rand = new Random();
		encryptor = new MyCrypt32(rand.nextInt(Integer.MAX_VALUE));
		String plainText = "testテストデータ";
		String enctyptText = encryptor.encryptWithIv(plainText);
		System.out.println("平文：" + plainText);
		System.out.println("暗号文：" + plainText);

		//32ビット回のループ
		for(int i = 0; i <= Integer.MAX_VALUE; i++) {
			encryptor = new MyCrypt32(i);
			String str = encryptor.decryptWithIv(enctyptText);//暗号データ
			if(str.equals(plainText)){//暗号データに対応する復号データ
				System.out.println("complete.");
				System.out.println("cipherKey=" + i + ". data=" + str);
				break;
			}

			//100万回ごとに出力
			if(i % 1000000 == 0){
				System.out.println("cipherKey=" + i + ". data=" + str);
			}
		}
		System.out.println("end.");
	}

	/**
	 * 128bit暗号攻撃テスト
	 */
	public static void cipherAttackTest128(){
		StringEncryptor encryptor = null;
		int count = 0;

		//平文と暗号文のペアを作成
		Random rand = new Random();
		encryptor = new MyCrypt128(
				rand.nextInt(Integer.MAX_VALUE),
				rand.nextInt(Integer.MAX_VALUE),
				rand.nextInt(Integer.MAX_VALUE),
				rand.nextInt(Integer.MAX_VALUE));
		String plainText = "testテストデータ";
		String enctyptText = encryptor.encryptWithIv(plainText);
		System.out.println("平文：" + plainText);
		System.out.println("暗号文：" + plainText);

		//カウンタ
		int i = Integer.MIN_VALUE;
		int j = Integer.MIN_VALUE;
		int k = Integer.MIN_VALUE;
		int l = Integer.MIN_VALUE;

		//128ビット回のループ
		while(l < Integer.MAX_VALUE){
			encryptor = new MyCrypt128(i, j, k, l);
			String str = encryptor.decryptWithIv(enctyptText);//暗号データ
			if(str.equals(plainText)){//暗号データに対応する復号データ
				System.out.println("complete.");
				System.out.println("cipherKey=" + i + "," + j + "," + k + "," + l + ". data=" + str);
				break;
			}

			if(i < Integer.MAX_VALUE){
				i++;
			}else if(j < Integer.MAX_VALUE){
				j++;
				if(i == Integer.MAX_VALUE){
					i = Integer.MIN_VALUE;
				}
			}else if(k < Integer.MAX_VALUE){
				k++;
				if(j == Integer.MAX_VALUE){
					j = Integer.MIN_VALUE;
				}
			}else if(l < Integer.MAX_VALUE){
				l++;
				if(k == Integer.MAX_VALUE){
					k = Integer.MIN_VALUE;
				}
			}else{
				System.out.println("end.");
				break;
			}

			//100万回ごとに出力
			if(count++ == 1000000){
				count = 0;
				System.out.println("cipherKey=" + i + "," + j + "," + k + "," + l + ". data=" + str);
			}
		}
		System.out.println("end.");
	}

}
