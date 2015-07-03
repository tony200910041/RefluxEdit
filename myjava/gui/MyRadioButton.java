package myjava.gui; //version: 1.1

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.Resources;

public class MyRadioButton extends JRadioButton implements Resources
{
	private int x;
	public MyRadioButton(String str, boolean isSelected, int x)
	{
		super(str, isSelected);
		this.setFont(f13);
		this.setOpaque(false);
		this.setFocusPainted(false);
		this.x = x;
	}
	
	public int getIndex()
	{
		return this.x;
	}
}
