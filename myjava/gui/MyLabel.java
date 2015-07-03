package myjava.gui;

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.Resources;

public class MyLabel extends JLabel implements Resources
{
	private static Color defaultColor = Color.BLACK;
	public MyLabel()
	{
		super();
		this.setFont(f13);
		this.setForeground(defaultColor);
	}
	
	public MyLabel(String str)
	{
		super(str);
		this.setFont(f13);
		this.setForeground(defaultColor);
	}
	
	public static void setDefaultForeground(Color c)
	{
		MyLabel.defaultColor = c;
	}
}
