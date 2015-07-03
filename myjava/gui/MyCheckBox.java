package myjava.gui; //version: 1.0

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.Resources;

public class MyCheckBox extends JCheckBox
{
	private static Color background = Color.WHITE;
	public MyCheckBox(String str, boolean isSelected)
	{
		super(str, isSelected);
		this.setFont(Resources.f13);
		this.setBackground(background);
		this.setFocusPainted(false);
		this.setOpaque(false);
	}
	
	public static void setDefaultBackground(Color c)
	{
		MyCheckBox.background = c;
	}
}
