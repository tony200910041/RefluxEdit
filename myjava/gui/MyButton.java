package myjava.gui;

/**
 * Requires myjava.gui.common.Resources to work
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import myjava.gui.common.*;

public class MyButton extends JButton implements ActionListener, MouseListener, Resources
{
	private static final String LAF = UIManager.getLookAndFeel().getName().toLowerCase();
	private static final boolean isWindows = LAF.contains("windows");
	private static final boolean isNimbus = LAF.contains("nimbus");
	private static Color background = Color.WHITE;
	private static Color foreground = Color.BLACK;
	private static Dimension dimension = new Dimension(50,28);	
	public MyButton(String text)
	{
		super(text);
		this.setFont(Resources.f13);
		if ((!isWindows)&&(!isNimbus))
		{
			this.setBorder(bord1);
		}
		else if (isNimbus)
		{
			this.setBorder(null);
		}
		if (!isWindows)
		{
			this.setBackground(background);
			this.setForeground(foreground);
			this.setPreferredSize(dimension);
			this.setFocusPainted(false);
		}
		else
		{
			this.setOpaque(false);
		}
		this.addMouseListener(this);
		this.addActionListener(this);		
	}
	
	public MyButton()
	{
		this("");
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
	}
	
	@Override
	public void mouseReleased(MouseEvent ev)
	{
	}
	
	@Override
	public void mousePressed(MouseEvent ev)
	{
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
	}
	
	@Override
	public void mouseEntered(MouseEvent ev)
	{
		if (this.isEnabled()&&(!isWindows)&&(!isNimbus))
		{
			this.setBorder(bord2);
		}
	}
	
	@Override
	public void mouseExited(MouseEvent ev)
	{
		if (this.isEnabled()&&(!isWindows)&&(!isNimbus))
		{
			this.setBorder(bord1);
		}
	}
}
