package gui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

/**
 * 描画用の画面
 */
public class PlotGraphDrawer extends JFrame {

	private Image image = null; //画像バッファ
	private Canvas canvas = null; //描画エリア
	private List<Point> plotList = new ArrayList<>();

	/**
	 * 初期化
	 */
	public PlotGraphDrawer(int width, int height) {
		setBounds(0, 0, width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		canvas = new Canvas() {
			@Override
			public void update(Graphics g) {
				paint(g);
			}
			@Override
			public void paint(Graphics g)
			{
			    g.drawImage(image, 0, 0, this);
				image.flush();
			}
		};

		getContentPane().add(canvas);

		setVisible(true);
	}

	/**
	 * 描画させる
	 */
	public void draw() {
		//画像バッファに描画
		image = createImage(canvas.getWidth(), canvas.getHeight());
		Graphics g = image.getGraphics();
		for(int i = 0; i < plotList.size() - 1; i++) {
			Point newP = plotList.get(i + 1);
			Point oldP = plotList.get(i);
			g.drawLine(oldP.x, oldP.y, newP.x, newP.y);
		}
		plotList.clear();

		//canvasに画像バッファの内容を描画させる
		canvas.repaint();
	}

	/**
	 * プロットを追加
	 * @param x 位置 0～1
	 * @param y 位置 -1～1
	 */
	public void plot(double x, double y) {
		Point p = new Point(
				(int)(x * canvas.getWidth()),
				canvas.getHeight() / 2 - (int)Math.ceil(y * canvas.getHeight()) / 2);
		plotList.add(p);
	}

}
