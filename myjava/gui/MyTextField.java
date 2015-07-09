/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import myjava.gui.common.*;

public class MyTextField extends JTextField implements MouseListener, Resources, ColorConstants
{
	private int x;	
	public MyTextField()
	{
		super();
		this.setFont(f13);
		this.setForeground(Color.BLACK);
		this.setBackground(vlgray);
		/*
		 * not using bord1 in Resources for Windows Look and Feel
		 */
		if (!LAF.contains("windows"))
		{
			this.setBorder(bord1);
		}
		this.setDragEnabled(true);
		this.addMouseListener(this);
		this.setPreferredSize(new Dimension(this.getSize().width,23));
	}
	
	public MyTextField(int size, int x)
	{
		this();
		this.setColumns(size);
		this.x = x;
	}
	
	public MyTextField(int size)
	{
		this(size,0);
	}
	
	public int getIndex()
	{
		return this.x;
	}
	
	@Override
	public void mouseEntered(MouseEvent ev)
	{
		this.setBackground(Color.WHITE);
	}
	
	@Override
	public void mouseExited(MouseEvent ev)
	{
		this.setBackground(vlgray);
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
	}
	
	@Override
	public void mousePressed(MouseEvent ev)
	{
	}
	
	@Override
	public void mouseReleased(MouseEvent ev)
	{
	}
}
