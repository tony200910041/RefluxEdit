/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import java.text.*;
import exec.*;
import static exec.SourceManager.*;

public class MyToolBar extends JToolBar
{
	private static final MyToolBar INSTANCE = new MyToolBar();
	private MyToolBar()
	{
		super("ToolBar");
		this.update();
	}
	
	public static MyToolBar getInstance()
	{
		return INSTANCE;
	}
	
	public void update()
	{
		this.loadButtons();
		this.revalidate();
		this.repaint();
	}
	
	public void loadButtons()
	{
		try
		{
			this.removeAll();
		}
		catch (Exception ex)
		{
		}
		String data = getConfig0("ToolBar.buttons");
		if (data != null)
		{
			for (Indexable indexable: toComponents(tokenize(data)))
			{
				this.add((JComponent)indexable);
			}
		}
	}
	
	static java.util.List<String> tokenize(String data)
	{
		java.util.List<String> list = new ArrayList<>();
		int i=0;
		while (i<data.length())
		{
			list.add(data.substring(i,Math.min(i+3, data.length())));
			i += 3;
		}
		return list;
	}
	
	static java.util.List<Indexable> toComponents(java.util.List<String> tokens)
	{
		java.util.List<Indexable> c = new ArrayList<>(tokens.size());
		for (String s: tokens)
		{
			if (s.equals("000")) c.add(new MySeparator());
			else
			{
				while (s.startsWith("0")) s = s.substring(1,s.length());
				try
				{
					c.add(MyToolBarButton.fromIndex(Integer.parseInt(s)));
				}
				catch (Exception ex)
				{
					//pass
				}
			}
		}
		return c;
	}
	
	public static interface Indexable
	{
		String getIndexString();
		int getIndex();
		Icon getIcon();
		String toString();
	}
	
	public static class MyToolBarButton extends JButton implements Indexable
	{
		private static final NumberFormat stringFormatter = new DecimalFormat("000");
		private static final Set<MyToolBarButton> buttonSet = new LinkedHashSet<>();
		static
		{
			buttonSet.add(new MyToolBarButton("NEW32", "New file", 1));
			buttonSet.add(new MyToolBarButton("OPEN32", "Open file", 2));
			buttonSet.add(new MyToolBarButton("SAVE32", "Save as", 4));
			buttonSet.add(new MyToolBarButton("EXPORT32", "Export", 43));
			buttonSet.add(new MyToolBarButton("PRINT32", "Print", 38));
			buttonSet.add(new MyToolBarButton("UNDO32", "Undo", 7));
			buttonSet.add(new MyToolBarButton("REDO32", "Redo", 8));
			buttonSet.add(new MyToolBarButton("CUT32", "Cut selection", 11));
			buttonSet.add(new MyToolBarButton("COPY32", "Copy selection", 12));
			buttonSet.add(new MyToolBarButton("PASTE32", "Paste", 13));
			buttonSet.add(new MyToolBarButton("DELETE32", "Delete selection", 15));
			buttonSet.add(new MyToolBarButton("INDENT+32", "Increase indentation", 18));
			buttonSet.add(new MyToolBarButton("INDENT-32", "Decrease indentation", 19));
			buttonSet.add(new MyToolBarButton("SELECT32", "Select all", 9));
			buttonSet.add(new MyToolBarButton("SELECT32", "Select all and copy", 10));
			buttonSet.add(new MyToolBarButton("REPLACE32", "Search/Replace", 24));
			buttonSet.add(new MyToolBarButton("BASE32", "Base converter", 41));
			buttonSet.add(new MyToolBarButton("COLORCHOOSER32", "Color chooser", 40));
		}
		private String des;
		private int x;
		private MyToolBarButton(String icon, String tooltip, int x)
		{
			super();
			this.setToolTipText(tooltip);
			this.setFocusPainted(false);
			this.setBackground(new Color(224,223,227));
			this.setIcon(icon(icon));
			this.addActionListener(new MyListener(x));
			this.x = x;
			this.des = tooltip;
		}
		
		public static MyToolBarButton fromIndex(int index)
		{
			for (MyToolBarButton button: buttonSet)
			{
				if (button.x == index) return button;
			}
			return null;
		}
		
		public static Set<MyToolBarButton> all()
		{
			return buttonSet;
		}
		
		@Override
		public int getIndex()
		{
			return this.x;
		}
		
		@Override
		public String getIndexString()
		{
			return stringFormatter.format(this.x);
		}
		
		@Override
		public String toString()
		{
			return this.des;
		}
	}
	
	public static class MySeparator extends JToolBar.Separator implements Indexable
	{
		public MySeparator()
		{
			super();
		}
		
		@Override
		public int getIndex()
		{
			return 0;
		}
		
		@Override
		public String getIndexString()
		{
			return "000";
		}
		
		@Override
		public Icon getIcon()
		{
			return icon("SEPARATOR32");
		}
		
		@Override
		public String toString()
		{
			return "Separator";
		}
	}
}
