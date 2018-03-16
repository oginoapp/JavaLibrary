package gui.jogl;

import javax.swing.JFrame;

import gui.Vertex;

/**
 * 3Dグラフ描画用のウィンドウ
 */
public class GraphDrawer3D extends JFrame {

	/* 描画用エリア */
	private GraphDrawer3DCanvas canvas;

	/**
	 * 初期化
	 */
	public GraphDrawer3D(int width, int height) {
		canvas = new GraphDrawer3DCanvas();

		canvas.setSize(width, height);
		add(canvas);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();

		setVisible(true);
	}

	/**
	 * Canvasを再描画
	 */
	public void draw(Vertex[][] vertices) {
		canvas.setVertices(vertices);
		canvas.repaint();
	}

}
