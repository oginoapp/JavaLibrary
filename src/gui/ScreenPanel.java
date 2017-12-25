package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ScreenPanel extends JPanel{
	private BufferedImage image = null;

	//画像更新
	public void updateImage(BufferedImage image){
		this.image = image;
		repaint();
	}

	//描画メソッドへの介入
	@Override
	public void paintComponent(Graphics g){
		if(image == null){
			super.paintComponent(g);
			return;
		}

		Graphics2D g2D = (Graphics2D)g;

		double imageWidth = image.getWidth();
		double imageHeight = image.getHeight();
		double panelWidth = this.getWidth();
		double panelHeight = this.getHeight();

		//画面対画像のサイズ比率の計算
		double sx = (panelWidth / imageWidth);
		double sy = (panelHeight / imageHeight);

		AffineTransform af = AffineTransform.getScaleInstance(sx, sy);
		g2D.drawImage(image, af, this);
	}
}
