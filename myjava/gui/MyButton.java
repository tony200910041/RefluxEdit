/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
			this.setFocusPainted(false);
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
