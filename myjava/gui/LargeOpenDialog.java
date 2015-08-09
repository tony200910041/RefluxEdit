/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
		super(parent,"",true);
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
		//extra tooltip
		quick.setToolTipText("Load text from a file path or a URL");
	}
	
	public static void showOpenDialog(Frame parent)
	{
		LargeOpenDialog dialog = new LargeOpenDialog(parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
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
