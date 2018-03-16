package gui.jogl;

import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;

import gui.Vertex;

public abstract class GLCanvas3D extends GLCanvas implements GLEventListener {

	protected Vertex[][] vertices = null;

	public GLCanvas3D() {
		super();
		addGLEventListener(this);
	}

	/**
	 * 頂点座標の一覧をセット
	 */
	public void setVertices(Vertex[][] vertices) {
		this.vertices = vertices;
	}

}
