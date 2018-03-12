package test;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 波動方程式
 */
public class WaveSimulator {

	public static void main(){
		final int JSIZE = 53;

		int jmax = JSIZE - 2;
		double dz = 1.0 / (double)(jmax - 1);//0.02;

		double dt = dz / 10.0; //0.002;

		double c = 1.0;
		double nu = c * (dt / dz);//courant数

		double[] x = new double[JSIZE];
		double[] up = new double[JSIZE];
		double[] u = new double[JSIZE];
		double[] uq = new double[JSIZE];

		GraphDrawer view = new GraphDrawer(800, 300);

		boolean run = true;

		//初期化
		for(int j = 1; j <= jmax; j++){
			x[j - 1] = (j - 1) * dz;
			u[j] = up[j] = uq[j] = Math.exp((-100) * (j * dz - 0.52) * (j * dz - 0.52));//1.0;
		}

		// 計算ループ
		while(run){

			// 計算
			for(int j = 2; j <= jmax-1; j++){
				up[j] = 2.0 * u[j] - uq[j] + nu * nu * (u[j + 1] - 2.0 * u[j] + u[j - 1]);
			}

			// 境界の計算
			u [1] = u [2];
			up[1] = up[2];
			uq[1] = uq[2];
			u [jmax] = u [jmax - 1];
			up[jmax] = up[jmax - 1];
			uq[jmax] = uq[jmax - 1];

			// 更新
			for(int j = 1; j <= jmax; j++){
				uq[j] = u[j];
				u[j] = up[j];
			}

			// 出力
			for(int j = 1; j <= jmax; j++){
				view.addDrawPosition(x[j - 1], u[j]);
			}
			view.reDraw();
			try {
				Thread.sleep((int)(dz * 1000));
			} catch (Exception ex) {
			}
		}

	}

}

/**
 * 描画用の画面
 */
class GraphDrawer extends JFrame {

	private Canvas canvas = null;
	private List<Point> pointList = new ArrayList<>();

	public GraphDrawer(int width, int height) {
		setBounds(0, 0, width + 50, height + 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		add(panel);
		panel.setBounds(0, 0, width, height);

		canvas = new Canvas() {
			@Override
			public void paint(Graphics g)
			{
				if(pointList != null) {
					for(int i = 0; i < pointList.size() - 1; i++) {
						Point newP = pointList.get(i + 1);
						Point oldP = pointList.get(i);
						g.drawLine(oldP.x, oldP.y, newP.x, newP.y);
					}
					pointList.clear();
				}
			}
		};
		panel.add(canvas);
		canvas.setBounds(0, 0, width, height);
		canvas.setBackground(Color.WHITE);

		setVisible(true);
	}

	public void reDraw() {
		if(canvas != null) canvas.repaint();
	}

	public void addDrawPosition(double x, double y) {
		Point p = new Point(
				(int)(x * canvas.getWidth()),
				canvas.getHeight() / 2 - (int)Math.ceil(y * canvas.getHeight()) / 2);
		pointList.add(p);
	}

}
