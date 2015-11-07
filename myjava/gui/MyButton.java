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
	public MyButton()
	{
		this.setFont(Resources.f13);
		if (isMetal)
		{
			this.setBorder(bord1);
			this.setFocusPainted(false);
			this.setBackground(Color.WHITE);
			this.setPreferredSize(new Dimension(50,28));
		}
		else if (isNimbus)
		{
			this.setBackground(Color.WHITE);
		}
		else if (isWindows)
		{
			this.setOpaque(false);
		}
		this.addMouseListener(this);
		this.addActionListener(this);
	}
	
	public MyButton(String text)
	{
		this();
		this.setText(text);
	}
	
	public MyButton(Icon icon)
	{
		this();
		this.setIcon(icon);
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
		if (this.isEnabled()&&isMetal)
		{
			this.setBorder(bord2);
		}
	}
	
	@Override
	public void mouseExited(MouseEvent ev)
	{
		if (this.isEnabled()&&isMetal)
		{
			this.setBorder(bord1);
		}
	}
}
