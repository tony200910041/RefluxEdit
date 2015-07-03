package myjava.gui;

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.*;
import exec.*;

public class MyMenuItem extends JMenuItem implements Resources
{
	public MyMenuItem(String str, String icon, int x)
	{
		super(str);
		this.setFont(f13);
		this.setBackground(Color.WHITE);
		this.setForeground(Color.BLACK);
		this.addMouseListener(new MyListener(x));
		try
		{
			this.setIcon(SourceManager.icon(icon));
		}
		catch (Exception ex)
		{
		}
	}
	
	public MyMenuItem setAccelerator(int keyEvent, int actionEvent)
	{
		this.setAccelerator(KeyStroke.getKeyStroke(keyEvent, actionEvent));
		return this;
	}
}
