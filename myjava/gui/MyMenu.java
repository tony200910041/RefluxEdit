package myjava.gui;

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.*;

public class MyMenu extends JMenu implements Resources
{
	public MyMenu(String str)
	{
		super(str);
		this.setFont(f13);
		this.setForeground(Color.BLACK);
		this.setOpaque(false);
		this.setBackground(Color.WHITE);
	}
}
