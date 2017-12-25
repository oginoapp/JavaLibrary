package gui;

import java.awt.Container;

import javax.swing.JFrame;

public class LogWindow extends JFrame{
	private Container ct;
	private LogPanel logPanel;

	public LogWindow(String title, int logSize, int x, int y, int width, int height, boolean topInsert, boolean visible){
		//ウィンドウの初期化
		setTitle(title);
		setBounds(x, y, width, height);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//構造の設定
		logPanel = new LogPanel(logSize, topInsert);
		ct = getContentPane();
		ct.add(logPanel);

		setVisible(visible);
	}

	public void updateLog(final Object log){
		logPanel.updateLog(log);
	}
}

