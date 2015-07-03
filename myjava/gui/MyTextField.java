package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import myjava.gui.common.*;

public class MyTextField extends JTextField implements MouseListener, Resources, ColorConstants
{
	private int x;
	public MyTextField(int size, int x)
	{
		super(size);
		this.x = x;
		this.setFont(f13);
		this.setForeground(Color.BLACK);
		this.setBackground(vlgray);
		/*
		 * not using bord1 in Resources for Windows Look and Feel
		 */
		this.setBorder(bord1);
		this.setDragEnabled(true);
		this.addMouseListener(this);
		this.setPreferredSize(new Dimension(this.getSize().width,23));
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
