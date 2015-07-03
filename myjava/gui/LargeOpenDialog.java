package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import myjava.gui.*;
import myjava.gui.common.*;
import exec.*;

public class LargeOpenDialog extends JDialog implements Resources, ColorConstants
{
	private LargeOpenDialog(Frame parent)
	{
		//open window in ribbon UI: open(2), open_quick(3), open_charset(51)
		super(parent,"Open file...",true);
		this.setUndecorated(true);
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.getContentPane().setBackground(gray);
		this.getRootPane().setBorder(raisedBorder);
		OpenButton open = new OpenButton("Open file","OPEN80",2);
		OpenButton quick = new OpenButton("Open file (quick)","OPENQ80",3);
		OpenButton charset = new OpenButton("Open file (charset)","OPENC80",51);
		OpenButton cancel = new OpenButton("Cancel",null,-1);
		this.add(open);
		this.add(quick);
		this.add(charset);
		this.add(cancel);
	}
	
	public static void showOpenDialog()
	{
		LargeOpenDialog dialog = new LargeOpenDialog(RefluxEdit.getInstance());
		dialog.pack();
		dialog.setLocationRelativeTo(RefluxEdit.getInstance());
		//
		GrayGlassPane.getInstance().setTransparent(false);
		dialog.setVisible(true);
	}
	
	class OpenButton extends MyButton
	{
		private int x;
		OpenButton(String text, String icon, int x)
		{
			super(text);
			this.x = x;
			/*
			 * set icon if exists
			 */
			if (icon != null)
			{
				this.setPreferredSize(new Dimension(120,120));
				this.setVerticalTextPosition(SwingConstants.BOTTOM);
				this.setHorizontalTextPosition(SwingConstants.CENTER);
				try
				{
					this.setIcon(SourceManager.icon(icon));
				}
				catch (Exception ex)
				{
				}
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
			LargeOpenDialog.this.dispose();
			GrayGlassPane.getInstance().setTransparent(true);
			if (x >= 1)
			{
				(new MyListener(x)).mouseReleased(ev);
			}
		}
	}
}
