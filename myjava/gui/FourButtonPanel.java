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

public class FourButtonPanel extends JPanel implements Resources
{
	private static final FourButtonPanel INSTANCE = new FourButtonPanel();
	private FourButtonPanel()
	{
		super();
		this.add(new MyShortcutButton("New", -1));
		this.add(new MyShortcutButton("Open", 2));
		this.add(new MyShortcutButton("Save as", 4));
		this.add(new MyShortcutButton("Save", 5));
	}
	
	public static FourButtonPanel getInstance()
	{
		return INSTANCE;
	}
	
	private static class MyShortcutButton extends JButton implements MouseListener
	{
		//"Four panel"
		public MyShortcutButton(String str, int x)
		{
			super(str);
			this.setBackground(Color.WHITE);
			this.setForeground(new Color(120,77,26));
			this.setPreferredSize(new Dimension(60,30));
			this.setFocusable(false);
			this.setFont(f13);
			this.setBorder(bord1);
			this.addMouseListener(new MyListener(x));
			this.addMouseListener(this);
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
			this.setBorder(bord2);
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
		{
			this.setBorder(bord1);
		}
		
		@Override
		public void mousePressed(MouseEvent ev) {}
		
		@Override
		public void mouseClicked(MouseEvent ev) {}
		
		@Override
		public void mouseReleased(MouseEvent ev) {}
	}
}
