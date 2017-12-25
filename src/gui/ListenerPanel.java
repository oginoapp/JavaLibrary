package gui;

import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public abstract class ListenerPanel extends JPanel{

	/**
	 * コンストラクタ
	 * 引数 - interval
	 */
	public ListenerPanel(){
		//マウスイベント
		this.addMouseListener(new MouseListener(){
			@Override
			public void mouseReleased(MouseEvent e){
				onMouseRelease(e);
			}

			@Override
			public void mousePressed(MouseEvent e){
				onMousePress(e);
			}

			@Override
			public void mouseExited(MouseEvent e){}

			@Override
			public void mouseEntered(MouseEvent e){}

			@Override
			public void mouseClicked(MouseEvent e){}
		});

		//キーイベント
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e){}

			@Override
			public void keyReleased(KeyEvent e){
				onKeyRelease(e);
			}

			@Override
			public void keyPressed(KeyEvent e){
				onKeyPress(e);
			}
		});
	}

	/**
	 * 引数 - 描画する画像:BufferedImage
	 * 結果 - 画像を描画する
	 */
	final public void drawImage(final BufferedImage img){
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){

			}
		});
	}

	protected abstract void onMouseRelease(MouseEvent e);
	protected abstract void onMousePress(MouseEvent e);
	protected abstract void onKeyRelease(KeyEvent e);
	protected abstract void onKeyPress(KeyEvent e);
}

