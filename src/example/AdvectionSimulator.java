package example;

import gui.PlotGraphDrawer;

/**
 * 移流方程式
 */
public class AdvectionSimulator {

	public static void main() {
		final int JSIZE = 5000;

		int jmax = JSIZE / 10;
		double dz = 0.015; // 領域

		double dt = 0.001;

		double c = 1.0;
		double nu = c * (dt / dz); // courant数

		double[] u = new double[JSIZE];
		double[] up = new double[JSIZE];
		double[] x = new double[JSIZE];

		PlotGraphDrawer view = new PlotGraphDrawer(800, 300);

		boolean run = true;

		// 初期化
		for(int j = 0; j <= jmax; j++){
			x[j] = j * dz / (jmax * dz);
			u[j] = up[j] = Math.exp((-100) * (j * dz - 1.0) * (j * dz - 1.0));// 1.0
		}

		// 計算ループ
		while(run) {

			// 境界の計算
			u[0] = u[jmax];
			u[jmax + 1] = u[1];

			// 計算
			for(int j = 2; j <= jmax - 1; j++){
				up[j] = u[j] - 0.5 * nu * (u[j + 1] - u[j - 1])
					         + 0.5 * nu * nu * (u[j + 1] - 2.0 * u[j] + u[j - 1]);
			}

			// 書き換え
			for(int j = 1; j <= jmax - 1; j++){
				u[j] = up[j];
			}

			// 境界の計算
			u[0] = u[jmax];
			u[jmax + 1] = u[1];

			// 出力
			for(int j = 0; j <= jmax; j++){
				view.plot(x[j], u[j]);
			}
			view.draw();
			try {
				Thread.sleep((int)(dt * 1000));
			} catch(InterruptedException ex) {}

		}
	}

}
