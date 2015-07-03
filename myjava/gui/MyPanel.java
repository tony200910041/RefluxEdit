package myjava.gui;

import java.awt.*;
import javax.swing.*;

public class MyPanel extends JPanel
{
	public static final int LEFT = FlowLayout.LEFT;
	public static final int CENTER = FlowLayout.CENTER;
	public static final int RIGHT = FlowLayout.RIGHT;
	public MyPanel(int x)
	{
		super();
		this.setLayout(new FlowLayout(x));
		this.setBackground(Color.WHITE);
	}
}
