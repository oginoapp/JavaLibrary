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

		// 背景の枠線を引く
		float[][] cube = {
			{-1.0f, -1.0f, 1.0f},
			{1.0f, -1.0f, 1.0f},
			{1.0f, 1.0f, 1.0f},
			{-1.0f, 1.0f, 1.0f},
			{-1.0f, -1.0f, 1.0f},
			{-1.0f, 1.0f, 1.0f},
			{-1.0f, 1.0f, -1.0f},
			{-1.0f, -1.0f, -1.0f},
			{-1.0f, -1.0f, 1.0f},
			{-1.0f, -1.0f, -1.0f},
			{1.0f, -1.0f, -1.0f},
			{1.0f, -1.0f, 1.0f},
		};
		gl.glBegin(GL2.GL_LINE_LOOP);
		for(int i = 0; i < cube.length; i++) {
			gl.glVertex3f(cube[i][0], cube[i][1], cube[i][2]);
		}
		gl.glEnd();

		// ポリゴンを描画する
		Vertex from = null;
		Vertex to = null;
		if(vertices != null) {
			for(int i = 0; i < vertices.length - 1; i++) {
				for(int j = 0; j < vertices[i].length - 1; j++) {
					from = vertices[i][j];
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

		// 背景の枠線を引く
		gl.glBegin(GL2.GL_LINE_LOOP);
		for(int i = 0; i < cube.length; i++) {
			gl.glVertex3f(-cube[i][0], -cube[i][1], -cube[i][2]);
		}
		gl.glEnd();

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		// x軸回転
		float rotateX = 50f;
		gl.glRotatef(rotateX, 1f, 0f, 0f);
		// z軸回転
		float rotateZ = 195f;
		gl.glRotatef(rotateZ, 0f, 0f, 1f);

		// 角が消えるのを防ぐため縮小
		float ratio = (float)Math.cos((rotateX % 90) * (float)Math.PI / 180f)
				* (float)Math.cos((rotateZ % 90) * (float)Math.PI / 180f);
		gl.glScalef(ratio, ratio, ratio);

		// ライティング処理有効化
		gl.glEnable(GL2.GL_LIGHTING);

		float[] red = {0.5f, 0.5f, 0.5f, 0f};
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, red, 0);

		// 環境光を設定
		float[] lightAmbient = { 0.5f, 0.5f, 0.5f, 0.0f };
	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient, 0);

		// 0番目の光源を設定
		float[] light1Pos = {10f, -10f, 1f, 10f};
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light1Pos, 0);
		gl.glEnable(GL2.GL_LIGHT0);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

}
