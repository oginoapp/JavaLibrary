package env;

import java.awt.AWTException;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class Environment {

	/**
	 * 画面のサイズを取得
	 */
	public static Rectangle getSereenRect() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		DisplayMode displayMode = env.getDefaultScreenDevice().getDisplayMode();
		int width = displayMode.getWidth();
		int height = displayMode.getHeight();
		return new Rectangle(0, 0, width, height);
	}

	/**
	 * スクリーンショットを取得
	 */
	public static BufferedImage getScreenShot() {
		BufferedImage image = null;
		try {
			Robot robot = new Robot();
			image = robot.createScreenCapture(getSereenRect());
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return image;
	}

}
