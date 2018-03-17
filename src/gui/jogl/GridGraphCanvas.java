package gui.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import gui.Vertex;

/**
 * 3Dグラフを格子状に描画するCanvas
 */
public class GridGraphCanvas extends GLCanvas3D {

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// 背景をクリア（R, G, B, A）
		gl.glClearColor(1f, 1f, 1f, 0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

		// 線の色を設定
		gl.glColor3f(0f, 0f, 0f);

		// 格子状に線を引く
		Vertex from = null;
		Vertex to = null;
		if(vertices != null) {
			for(int i = 0; i < vertices.length; i++) {
				for(int j = 0; j < vertices[i].length; j++) {
					from = vertices[i][j];
					if(i < vertices.length - 1) {
						to = vertices[i + 1][j];
						gl.glBegin(GL2.GL_LINES);
						gl.glVertex3f(from.x, from.y, from.z);
						gl.glVertex3f(to.x, to.y, to.z);
						gl.glEnd();
					}
					if(j < vertices.length - 1) {
						to = vertices[i][j + 1];
						gl.glBegin(GL2.GL_LINES);
						gl.glVertex3f(from.x, from.y, from.z);
						gl.glVertex3f(to.x, to.y, to.z);
						gl.glEnd();
					}
				}
			}
		}

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
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

}
