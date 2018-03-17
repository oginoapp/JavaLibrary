package example;

import gui.Vertex;
import gui.jogl.GraphDrawer3D;
import gui.jogl.GraphDrawer3D.DrawType;

/**
 * 2次元波動方程式
 * 例：Wave2DSimulator.main(BoundaryOption.FreeEndReflection, DrawType.Grid);
 *
 * 必要なライブラリ
 * WindowsでIntelのCPUを使っている場合：
 *   jogl-all.jar
 *   jogl-all-natives-windows-i586.jar
 *   gluegen-rt.jar
 *   gluegen-rt-natives-windows-i586.jar
 */
public class Wave2DSimulator {

	/**
	 * 境界の反射のオプション
	 */
	public static enum BoundaryOption {
		/* 自由端反射 */
		FreeEndReflection,
		/* 固定端反射 */
		FixedEndReflection,
		/* 無反射 */
		NonReflective,
	}

	/**
	 * シミュレーション
	 */
	public static void main(BoundaryOption option, DrawType type) {
		// 格子数の定義
		final int ISIZE = 103;
		final int JSIZE = 103;

		int imax = ISIZE - 2;
		int jmax = JSIZE - 2;
		float dx = 1f / (float)(imax - 1); // x軸方向の刻み幅
		float dy = 1f / (float)(jmax - 1); // y軸方向の刻み幅

		float dt = 0.002f; // 時間の刻み幅
		int interval = (int)(dt * 10000); // 計算間隔(ms)

		float c = 1f;
		float nu_x = c * (dt / dx); // クーラン数_x
		float nu_y = c * (dt / dy); // クーラン数_y

		float[][] up = new float[ISIZE][JSIZE];
		float[][] u = new float[ISIZE][JSIZE];
		float[][] uq = new float[ISIZE][JSIZE];

		Vertex[][] vertices = new Vertex[imax][jmax];

		// 画面
		GraphDrawer3D screen = new GraphDrawer3D(type, 1280, 768);

		// 初期条件
		for (int i = 1; i <= imax; i++) {
			for (int j = 1; j <= jmax; j++) {
				float x = (i - 1) * dx * 2 - 1;
				float y = (j - 1) * dy * 2 - 1;
				vertices[i - 1][j - 1] = new Vertex(x, y, 0f);

				u[i][j] = up[i][j] = uq[i][j] = (float)Math.exp((-100) *
						(Math.pow(i * dx - 0.52f, 2) + Math.pow(j * dy - 0.52f, 2)));
			}
		}

		// 時間発展
		while (true) {

			// 座標の計算
			for (int i = 2; i <= imax - 1; i++) {
				for (int j = 2; j <= jmax - 1; j++) {
					up[i][j] = 2f * u[i][j] - uq[i][j]
							+ (nu_x * nu_x) * (u[i + 1][j] - 2f * u[i][j] + u[i - 1][j])
							+ (nu_y * nu_y) * (u[i][j + 1] - 2f * u[i][j] + u[i][j - 1]);
				}
			}

			// 境界の計算
			switch(option) {
				case FreeEndReflection:
					// 自由端反射
					for (int i = 1; i <= imax; i++) {
						u[i][1] = u[i][2];
						u[i][jmax] = u[i][jmax - 1];
						up[i][1] = up[i][2];
						up[i][jmax] = up[i][jmax - 1];
						uq[i][1] = uq[i][2];
						uq[i][jmax] = uq[i][jmax - 1];
					}
					for (int j = 1; j <= jmax; j++) {
						u[1][j] = u[2][j];
						u[imax][j] = u[imax - 1][j];
						up[1][j] = up[2][j];
						up[imax][j] = up[imax - 1][j];
						uq[1][j] = uq[2][j];
						uq[imax][j] = uq[imax - 1][j];
					}
					break;
				case FixedEndReflection:
					// 固定端反射
					for (int i = 1; i <= imax; i++) {
						u[i][1] = 0f;
						u[i][jmax] = 0f;
						up[i][1] = 0f;
						up[i][jmax] = 0f;
						uq[i][1] = 0f;
						uq[i][jmax] = 0f;
					}
					for (int j = 1; j <= jmax; j++) {
						u[1][j] = 0f;
						u[imax][j] = 0f;
						up[1][j] = 0f;
						up[imax][j] = 0f;
						uq[1][j] = 0f;
						uq[imax][j] = 0f;
					}
					break;
				case NonReflective:
					// 無反射
					for (int i = 1; i <= imax; i++) {
						up[i][1] = u[i][1] + nu_y * (u[i][2] - u[i][1]);
						up[i][jmax] = u[i][jmax] - nu_y * (u[i][jmax] - u[i][jmax - 1]);
					}
					for (int j = 1; j <= jmax; j++) {
						up[1][j] = u[1][j] + nu_x * (u[2][j] - u[1][j]);
						up[imax][j] = u[imax][j] - nu_x * (u[imax][j] - u[imax - 1][j]);
					}
					break;
				default:
					break;
			}

			// 更新
			for (int i = 1; i <= imax; i++) {
				for (int j = 1; j <= jmax; j++) {
					uq[i][j] = u[i][j];
					u[i][j] = up[i][j];
				}
			}

			// 出力
			for (int i = 1; i <= imax; i++) {
				for(int j = 1; j <= jmax; j++){
					vertices[i - 1][j - 1].z = -u[i][j];
				}
			}
			try {
				Thread.sleep(interval);
			} catch(InterruptedException e){ }
			screen.draw(vertices);

		}

	}
}

