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

public class LargeNewDialog extends JDialog implements Resources, ColorConstants
{
	private LargeNewDialog(Frame parent)
	{
		/*
		 * open window in ribbon UI:
		 * new file(1), new file from clipboard(56), new Java file(57)
		 */
		super(parent,"New file...",true);
		this.setUndecorated(true);
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.getContentPane().setBackground(gray);
		this.getRootPane().setBorder(raisedBorder);
		NewButton open = new NewButton("New file","NEW80",1);
		NewButton quick = new NewButton("New file (clipboard)","NEWCLIPBOARD80",56);
		NewButton charset = new NewButton("New Java class","NEWJAVA80",57);
		NewButton cancel = new NewButton("Cancel",null,-1);
		this.add(open);
		this.add(quick);
		this.add(charset);
		this.add(cancel);
	}
	
	public static void showNewDialog()
	{
		LargeNewDialog dialog = new LargeNewDialog(RefluxEdit.getInstance());
		dialog.pack();
		dialog.setLocationRelativeTo(RefluxEdit.getInstance());
		//
		GrayGlassPane.getInstance().setTransparent(false);
		dialog.setVisible(true);
	}
	
	class NewButton extends MyButton
	{
		private int x;
		NewButton(String text, String icon, int x)
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
			LargeNewDialog.this.dispose();
			GrayGlassPane.getInstance().setTransparent(true);
			if (x >= 1)
			{
				(new MyListener(x)).mouseReleased(ev);
			}
		}
	}
}
