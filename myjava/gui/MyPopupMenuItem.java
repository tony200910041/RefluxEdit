/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import exec.*;
import myjava.gui.common.*;
import static exec.SourceManager.*;

public class MyPopupMenuItem extends JMenuItem implements ActionListener, Resources
{
	private int x;
	public MyPopupMenuItem(String text, String icon, int x)
	{
		super(text);
		this.setFont(f13);
		this.setBackground(Color.WHITE);
		this.setForeground(Color.BLACK);
		if (icon != null)
		{
			this.setIcon(icon(icon));
		}
		if (x >= 0)
		{
			this.addActionListener(new MyListener(x));
		}
		this.addActionListener(this);
		this.x = x;
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		/*
		 * >0 for MyListener, <0 for implementing here
		 */
		RefluxEdit w = RefluxEdit.getInstance();
		if (this.x == -1)
		{
			//show/hide
			w.setVisible(!w.isVisible());
			return;
		}
		else
		{
			//all operation
			w.setVisible(true);
		}
	}
}
