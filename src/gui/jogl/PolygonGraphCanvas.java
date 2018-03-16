package gui.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import gui.Vertex;

/**
 * 3Dグラフをポリゴンで描画するCanvas
 */
public class PolygonGraphCanvas extends GLCanvas3D {

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// 背景をクリア
		gl.glClearColor(0f, 0f, 0f, 0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

		// 色を設定
		gl.glColor3f(0.8f, 0.8f, 0.8f);

		// ポリゴンを描画する
		Vertex from = null;
		Vertex to = null;
		if(vertices != null) {
			for(int i = 0; i < vertices.length - 1; i++) {
				for(int j = 0; j < vertices[i].length - 1; j++) {
					from = vertices[i][j];
					gl.glColor3f(0.8f, 0.8f, 0.8f);
					gl.glBegin(GL2.GL_POLYGON);
					gl.glVertex3f(from.x, from.y, from.z);
					to = vertices[i + 1][j];
					gl.glVertex3f(to.x, to.y, to.z);
					to = vertices[i + 1][j + 1];
					gl.glVertex3f(to.x, to.y, to.z);
					to = vertices[i][j + 1];
					gl.glVertex3f(to.x, to.y, to.z);
					gl.glEnd();
				}
			}
		}

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		// x軸回転
		gl.glRotatef(-130f, 1f, 0f, 0f);
		// y軸回転
		gl.glRotatef(10f, 0f, -1f, 0f);
		// z軸回転
		gl.glRotatef(20f, 0f, 0f, 1f);

		// 縮小
		gl.glScalef(0.8f, 0.8f, 0.8f);

		// ライティング処理有効化
		gl.glEnable(GL2.GL_LIGHTING);

		// 0番目の光源を設定
		float[] light0Pos = {-0.8f, -0.8f, 0.5f, 1f};
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0Pos, 0);
		gl.glEnable(GL2.GL_LIGHT0);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

}
