package util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * new ImageMatcher();
 * imageMatch(screen,img);
 */
public class ImageMatcher{
	public static final int POS_ST_X = 0;
	public static final int POS_ST_Y = 1;
	public static final int POS_ED_X = 2;
	public static final int POS_ED_Y = 3;

	public ImageMatcher(){
	}

	/**
	 * 画像のテンプレートマッチング
	 * 入力:スクリーンショット,対象画像,全てか
	 * 出力:発見場所の座標の配列
	 */
	public ArrayList<int[]> imageMatch(BufferedImage screen, BufferedImage img, boolean searchAll){
		//結果の座標を格納する配列を格納するリスト
		ArrayList<int[]> results
			= new ArrayList<int[]>();

		int[][] screenGray=imgToGray(screen);
		int[][] imgGray=imgToGray(img);

		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		//画像探索
		root:for(i=0;i<screenGray.length;i++){
			compare:for(j=0;j<screenGray[0].length;j++){
				if(screenGray[i][j] == imgGray[0][0]){

					//一致判定済みの領域かどうかチェック
					if(results.size()>0){
						int[] tmp;
						for(int m=0;m<results.size();m++){
							tmp = results.get(m);
							if(i>=tmp[ImageMatcher.POS_ST_X] && i<=tmp[ImageMatcher.POS_ED_X] && j>=ImageMatcher.POS_ST_Y && j<=ImageMatcher.POS_ED_Y){
								continue compare;
							}
						}
					}

					//画像比較
					try{
						for(k=i;k<imgGray.length;k++){
							for(l=j;l<imgGray[0].length;l++){
								if(imgGray[k-i][l-j] != screenGray[k][l]){
									continue compare;
								}
							}
						}
						//結果の座標を格納する配列
						int[] result = new int[4];
						result[POS_ST_X] = j;
						result[POS_ST_Y] = i;
						result[POS_ED_X] = l;
						result[POS_ED_Y] = k;
						results.add(result);
						if(!searchAll){
							break root;
						}
					}catch(Exception ex){
						continue compare;
					}

				}
			}
		}

		return results;
	}

	/**
	 * グレースケールに変換
	 * 入力:BufferedImage
	 * 出力:グレースケールのint[][]
	 */
	public int[][] imgToGray(BufferedImage img){
		int width = img.getWidth();
		int height = img.getHeight();
		int[][] gray_img = new int[height][width];

		for(int y=0;y<height;y++){
			for(int x=0;x<width;x++){
				int rgb = img.getRGB(x,y);

				rgb -= 0xFF000000;
				int r=(rgb & 0xFF0000)>>16;
				int g=(rgb & 0xFF00)>>8;
				int b=rgb & 0xFF;
				int gray=(b + g + r)/3;

				gray_img[y][x] = gray;
			}
		}

		return gray_img;
	}
}














