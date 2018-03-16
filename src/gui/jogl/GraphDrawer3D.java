package gui.jogl;

import javax.swing.JFrame;

import gui.Vertex;

/**
 * 3Dグラフ描画用のウィンドウ
 */
public class GraphDrawer3D extends JFrame {

	public static enum DrawType {
		Polygon,
		Grid
	}

	/* 描画用エリア */
	private GLCanvas3D canvas;

	/**
	 * 初期化
	 */
	public GraphDrawer3D(DrawType type, int width, int height) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("graph");

		if(type == DrawType.Polygon) {
			canvas = new PolygonGraphCanvas();
		} else {
			canvas = new GridGraphCanvas();
		}

		canvas.setSize(width, height);
		add(canvas);
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