package example;

import gui.PlotGraphDrawer;

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

		PlotGraphDrawer view = new PlotGraphDrawer(800, 300);

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
				view.plot(x[j - 1], u[j]);
			}
			view.draw();
			try {
				Thread.sleep((int)(dz * 1000));
			} catch (Exception ex) {
			}
		}

	}

}