package gui;

import java.io.Serializable;

/**
 * 3D描画に使う頂点
 */
public class Vertex implements Serializable, Cloneable{
	public float x;
	public float y;
	public float z;

	public Vertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public Vertex clone() {
		return new Vertex(x, y, z);
	}
}
