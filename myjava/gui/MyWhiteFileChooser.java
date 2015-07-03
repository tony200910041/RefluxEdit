/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import myjava.gui.common.*;

public class MyWhiteFileChooser extends JFileChooser implements Resources
{
	private static final MyWhiteFileChooser INSTANCE = new MyWhiteFileChooser();
	private MyWhiteFileChooser()
	{
		super();
		this.decorate();
	}
	
	public static MyWhiteFileChooser getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public void addChoosableFileFilter(FileFilter f)
	{
		super.addChoosableFileFilter(f);
		this.setFileFilter(f);
	}
	
	private void decorate()
	{
		MyWhiteFileChooser.decorate(this);
		this.setBackground(Color.WHITE);
		if (isWindows)
		{
			this.setPreferredSize(new Dimension(561,388));
		}
		else
		{
			this.setPreferredSize(new Dimension(506,405));
		}
	}
	
	private static void decorate(Container container)
	{
		for (Component c: container.getComponents())
		{
			if (!(c instanceof JPanel))
			{
				c.setFont(f13);
				if (!isWindows) c.setBackground(Color.WHITE);
				c.setForeground(Color.BLACK);
			}
			if (c instanceof Container) decorate((Container)c);
		}
	}
}
