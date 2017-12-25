package gui;

import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class LogPanel extends JScrollPane{
	private JPanel jp;
	private JLabel[] labels;
	private boolean topInsert;

	public LogPanel(){
		this(50, false);
	}

	public LogPanel(int logSize, boolean topInsert){
		//構造の設定
		jp = new JPanel();
		setViewportView(jp);
		jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));

		//ラベルの生成
		labels = new JLabel[logSize];
		for(int i=0; i<logSize; i++){
			labels[i] = new JLabel("");
			jp.add(labels[i]);
		}

		this.topInsert = topInsert;
	}

	final public void updateLog(final Object log){
		EventQueue.invokeLater(new Runnable(){
			private JScrollBar scrollBar = getVerticalScrollBar();
			@Override
			public void run(){
				if(topInsert){
					//最上行にログ追加
					for(int i=labels.length-2; i>=0; i--){
						labels[i+1].setText(labels[i].getText());
					}
					labels[0].setText(String.valueOf(log));
				}else{
					//最下行にログ追加
					for(int i=0; i<labels.length-1; i++){
						labels[i].setText(labels[i+1].getText());
					}
					labels[labels.length-1].setText(String.valueOf(log));

					//一番下にスクロール
					if(scrollBar.getValue() >= (scrollBar.getMaximum()-scrollBar.getVisibleAmount())){
						scrollBar.setValue(0);
						getViewport().scrollRectToVisible(new Rectangle(0, Integer.MAX_VALUE - 1, 1, 1));
					}
				}
			}
		});
	}
}
