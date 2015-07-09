/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import myjava.gui.common.*;
import exec.*;

public class MyMenuItem extends JMenuItem implements Resources
{
	public MyMenuItem(String str, String icon, int x)
	{
		super(str);
		this.setFont(f13);
		this.setBackground(Color.WHITE);
		this.setForeground(Color.BLACK);
		this.addMouseListener(new MyListener(x));
		try
		{
			this.setIcon(SourceManager.icon(icon));
		}
		catch (Exception ex)
		{
		}
	}
	
	public MyMenuItem(String str, String icon, int x, int keyEvent)
	{
		this(str, icon, x, keyEvent, OS_CTRL_MASK);
	}
	
	public MyMenuItem(String str, String icon, int x, int keyEvent, int actionEvent)
	{
		this(str, icon, x);
		this.setAccelerator(KeyStroke.getKeyStroke(keyEvent, actionEvent));
	}
	
	@Deprecated
	public MyMenuItem setAccelerator(int keyEvent, int actionEvent)
	{
		this.setAccelerator(KeyStroke.getKeyStroke(keyEvent, actionEvent));
		return this;
	}
}
