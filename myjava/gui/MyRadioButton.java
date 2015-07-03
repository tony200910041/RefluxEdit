package myjava.gui; //version: 1.0

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.Resources;

public class MyRadioButton extends JRadioButton implements Resources
{
	private static Color background = Color.WHITE;
	private int x;
	public MyRadioButton(String str, boolean isSelected, int x)
	{
		super(str, isSelected);
		this.setFont(f13);
		this.setBackground(background);
		this.setFocusPainted(false);
		this.x = x;
	}
	
	public int getIndex()
	{
		return this.x;
	}
	
	public static void setDefaultBackground(Color c)
	{
		MyRadioButton.background = c;
	}
}
