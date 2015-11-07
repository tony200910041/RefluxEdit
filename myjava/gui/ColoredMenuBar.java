/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import exec.*;
import myjava.gui.common.*;

public class ColoredMenuBar extends JMenuBar implements Resources
{
	/*
	 * instance:
	 */
	private static final ColoredMenuBar INSTANCE = new ColoredMenuBar(ColoredMenuBar.NONE);
	/*
	 * mode:
	 */
	public static final int NONE = -1;
	public static final int WHITE = 0;
	public static final int BLUE = 1;
	public static final int MODERN = 2;
	/*
	 * menus
	 */
	private MyMenu menu1 = new MyMenu("File");
	private MyMenu menu2 = new MyMenu("Edit");
	private MyMenu menu3 = new MyMenu("View");
	private MyMenu menu4 = new MyMenu("Tools");
	private MyMenu menu5 = new MyMenu("Insert");
	private MyMenu menu6 = new MyMenu("Help");
	/*
	 * submenus
	 */
	private MyMenu menu1_2 = new MyMenu("Open");
	private int mode;
	private ColoredMenuBar(int mode)
	{
		super();
		this.setBorderPainted(false);
		this.setBackground(Color.WHITE);
		this.mode = mode;
		/*
		 * JMenus
		 */
		this.add(new JLabel(" "));
		this.add(menu1);
		this.add(new JLabel(" "));
		this.add(menu2);
		this.add(new JLabel(" "));
		this.add(menu3);
		this.add(new JLabel(" "));
		this.add(menu4);
		this.add(new JLabel(" "));
		this.add(menu5);
		this.add(new JLabel(" "));
		this.add(menu6);
		/*
		 * JMenuItems
		 */
		MyMenu menu1_1 = new MyMenu("New");
		menu1_1.setIcon(SourceManager.icon("NEW"));
		menu1.add(menu1_1);
		menu1_1.add(new MyMenuItem("New file", "NEW", 1));
		menu1_1.add(new MyMenuItem("New file from clipboard", "NEWCLIPBOARD16", 56));
		menu1_1.add(new MyMenuItem("New Java class", "NEWJAVA16", 57));
		menu1_2.setIcon(SourceManager.icon("OPEN"));
		menu1.add(menu1_2);
		menu1_2.addMenuListener(new MenuListener()
		{
			@Override
			public void menuSelected(MenuEvent ev)
			{
				menu1_2.removeAll();
				menu1_2.add(new MyMenuItem("Open file", "OPEN", 2, KeyEvent.VK_O));
				menu1_2.add(new MyMenuItem("Open file (quick)", null, 3));
				menu1_2.add(new MyMenuItem("Open file (charset)",null, 51));
				if (SourceManager.getBoolean0("rememberRecentFiles"))
				{
					menu1_2.add(new JSeparator());
					menu1_2.add(RecentMenu.getInstance());
				}
			}
			
			@Override
			public void menuDeselected(MenuEvent ev)
			{
			}
			
			@Override
			public void menuCanceled(MenuEvent ev)
			{
			}
		});
		menu1.add(new JSeparator());
		menu1.add(new MyMenuItem("Save as", "SAVE", 4, KeyEvent.VK_S));
		menu1.add(new MyMenuItem("Save", null, 5));
		menu1.add(new MyMenuItem("Export as image", "EXPORT16", 43, KeyEvent.VK_E));
		menu1.add(new JSeparator());
		menu1.add(new MyMenuItem("Print", "PRINT16", 38, KeyEvent.VK_P));
		menu1.add(new MyMenuItem("Close", "CLOSE", 6));
		//
		menu2.add(new MyMenuItem("Undo", "UNDO", 7, KeyEvent.VK_Z));
		menu2.add(new MyMenuItem("Redo", "REDO", 8, KeyEvent.VK_Y));
		menu2.add(new JSeparator());
		menu2.add(new MyMenuItem("Select all", null, 9, KeyEvent.VK_A));
		menu2.add(new MyMenuItem("Select all and copy", null, 10));
		menu2.add(new JSeparator());
		menu2.add(new MyMenuItem("Cut", "CUT", 11, KeyEvent.VK_X));
		menu2.add(new MyMenuItem("Copy", "COPY", 12, KeyEvent.VK_C));
		menu2.add(new MyMenuItem("Paste", "PASTE", 13, KeyEvent.VK_V));
		menu2.add(new MyMenuItem("Paste on next line", null, 14));
		menu2.add(new MyMenuItem("Delete", "DELETE16", 15, KeyEvent.VK_DELETE, 0));
		menu2.add(new JSeparator());
		menu2.add(new MyMenuItem("Increase indentation", "INDENT+", 18, KeyEvent.VK_I));
		menu2.add(new MyMenuItem("Decrease indentation", "INDENT-", 19, KeyEvent.VK_U));
		//
		menu3.add(new MyMenuItem("Enable/disable editing", "EDIT16", 17));
		menu3.add(new MyMenuItem("Enable/disable always on top", "ONTOP16", 21));
		menu3.add(new MyMenuItem("Undo record dialog", "UNDODIALOG16", 52));
		menu3.add(new MyMenuItem("Clipboard listener", "PASTE", 29));
		menu3.add(new MyMenuItem("File browser", "FILEBROWSER16", 59));
		menu3.add(new JSeparator());
		menu3.add(new MyMenuItem("Goto line", "GOTOLINE16", 47));
		//
		menu4.add(new MyMenuItem("Options", "OPTIONS16", 39));
		menu4.add(new JSeparator());
		menu4.add(new MyMenuItem("Compile code", "COMPILE16", 53, isMac?KeyEvent.VK_8:KeyEvent.VK_F8, isMac?InputEvent.META_MASK:0));
		menu4.add(new MyMenuItem("Word count", null, 22, isMac?KeyEvent.VK_2:KeyEvent.VK_F2));
		menu4.add(new MyMenuItem("Character count", null, 44, isMac?KeyEvent.VK_3:KeyEvent.VK_F3));
		menu4.add(new MyMenuItem("Delete blank lines", null, 35));
		menu4.add(new MyMenuItem("Reverse text", null, 50));
		//
		MyMenu menu4_1 = new MyMenu("Case conversion");
		menu4.add(menu4_1);
		menu4_1.setIcon(EmptyIcon.getInstance());
		menu4_1.add(new MyMenuItem("Convert to upper case", "UPPERCASE", 26));
		menu4_1.add(new MyMenuItem("Convert to lower case", "LOWERCASE", 27));
		menu4_1.add(new MyMenuItem("Convert to invert case", "REVERSECASE", 28));
		//
		MyMenu menu4_2 = new MyMenu("Escape character");
		menu4.add(menu4_2);
		menu4_2.setIcon(EmptyIcon.getInstance());
		menu4_2.add(new MyMenuItem("Escape string", "TRANSPARENT16", 54));
		menu4_2.add(new MyMenuItem("Unescape string", null, 55));
		//
		menu4.add(new JSeparator());
		menu4.add(new MyMenuItem("Search and replace", "SEARCH16", 24, KeyEvent.VK_F));
		menu4.add(new MyMenuItem("Color chooser", "COLORCHOOSER16", 40));
		menu4.add(new MyMenuItem("Base converter (2-36)", "BASE16", 41));
		menu4.add(new MyMenuItem("Regex matcher", "REGEX16", 23));
		//
		menu5.add(new MyMenuItem("Insert ten equal signs", null, 30));
		menu5.add(new MyMenuItem("Insert four spaces", null, 31));
		menu5.add(new MyMenuItem("Insert spaces between characters", null, 49));
		menu5.add(new MyMenuItem("Generate random words", null, 32));
		menu5.add(new JSeparator());
		menu5.add(new MyMenuItem("Insert key words (Java)", "KEYWORDJAVA16", 33));
		menu5.add(new MyMenuItem("Insert key words (html)", "KEYWORDHTML16", 34));
		menu5.add(new MyMenuItem("Insert unicode character", null, 45));
		menu5.add(new MyMenuItem("Insert unicode value", null, 46));
		//
		menu6.add(new MyMenuItem("About RefluxEdit", "APPICON16", 16, isMac?KeyEvent.VK_1:KeyEvent.VK_F1));
		menu6.add(new MyMenuItem("About MPL 2.0", null, 58));
		menu6.add(new MyMenuItem("Visit GitHub page", "VISIT16", 48));
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D)(g.create());
		int w = this.getWidth();
		int h = this.getHeight();
		switch (mode)
		{
			case -1:
			break;
			
			case 0: //white
			g2D.setColor(Color.WHITE);
			g2D.fillRect(0, 0, w, h);
			break;
			
			case 1: //blue
			g2D.setColor(new Color(242,254,255));
			g2D.fillRect(0, 0, w, h);
			break;
			
			case 2: //modern			
			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)0.5));
			g2D.setPaint(new GradientPaint(0, 0, new Color(242,254,255), w, 0, new Color(255,250,217)));
			g2D.fillRect(0, 0, w, h);
			break;
		}
		g2D.dispose();
	}
	
	public void setStyle(int mode)
	{
		this.mode = mode;
		this.repaint();
	}
	
	public static ColoredMenuBar getInstance()
	{
		return INSTANCE;
	}
}
