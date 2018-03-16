package gui.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;

import gui.Vertex;

/**
 * 3Dグラフを格子状に描画するCanvas
 */
public class GraphDrawer3DCanvas extends GLCanvas implements GLEventListener {

	private Vertex[][] vertices = null;

	public GraphDrawer3DCanvas() {
		super();
		addGLEventListener(this);
	}

	public void setVertices(Vertex[][] vertices) {
		this.vertices = vertices;
	}

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
		gl.glRotatef(50f, -1f, 0f, 0f);
		// y軸回転
		gl.glRotatef(10f, 0f, -1f, 0f);
		// z軸回転
		gl.glRotatef(20f, 0f, 0f, 1f);

		// 縮小
		gl.glScalef(0.8f, 0.8f, 0.8f);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

}
