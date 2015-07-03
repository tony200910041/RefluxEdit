package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ColoredMenuBar extends JMenuBar
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
		MyMenu menu1 = new MyMenu("File");
		MyMenu menu2 = new MyMenu("Edit");
		MyMenu menu3 = new MyMenu("View");
		MyMenu menu4 = new MyMenu("Tools");
		MyMenu menu5 = new MyMenu("Insert");
		MyMenu menu6 = new MyMenu("Help");
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
		menu1.add(new MyMenuItem("New file", "NEW", 1));
		menu1.add(new MyMenuItem("Open file", "OPEN", 2).setAccelerator(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menu1.add(new MyMenuItem("Open file (quick)", null, 3));
		menu1.add(new MyMenuItem("Open file (charset)",null, 51));
		menu1.add(new JSeparator());
		menu1.add(new MyMenuItem("Save as", "SAVE", 4).setAccelerator(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu1.add(new MyMenuItem("Save", null, 5));
		menu1.add(new MyMenuItem("Export to image", "EXPORT16", 43).setAccelerator(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		menu1.add(new JSeparator());
		menu1.add(new MyMenuItem("Print", "PRINT16", 38).setAccelerator(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		menu1.add(new MyMenuItem("Close", "CLOSE", 6));
		//
		menu2.add(new MyMenuItem("Undo", "UNDO", 7).setAccelerator(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		menu2.add(new MyMenuItem("Redo", "REDO", 8).setAccelerator(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		menu2.add(new JSeparator());
		menu2.add(new MyMenuItem("Select all", null, 9).setAccelerator(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menu2.add(new MyMenuItem("Select all and copy", null, 10));
		menu2.add(new JSeparator());
		menu2.add(new MyMenuItem("Cut", "CUT", 11).setAccelerator(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		menu2.add(new MyMenuItem("Copy", "COPY", 12).setAccelerator(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		menu2.add(new MyMenuItem("Paste", "PASTE", 13).setAccelerator(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		menu2.add(new MyMenuItem("Paste on next line", null, 14));
		menu2.add(new MyMenuItem("Delete", "DELETE16", 15));
		menu2.add(new JSeparator());
		menu2.add(new MyMenuItem("Increase indentation", "INDENT+", 18).setAccelerator(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		menu2.add(new MyMenuItem("Decrease indentation", "INDENT-", 19).setAccelerator(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
		//
		menu3.add(new MyMenuItem("Enable/disable editing", "EDIT16", 17));
		menu3.add(new MyMenuItem("Enable/disable always on top", "ONTOP16", 21));
		menu3.add(new MyMenuItem("Undo record dialog", null, 52));
		//
		menu4.add(new MyMenuItem("Options", "OPTIONS16", 39));
		menu4.add(new JSeparator());
		menu4.add(new MyMenuItem("Compile code", "COMPILE16", 53));
		menu4.add(new MyMenuItem("Word count", null, 22).setAccelerator(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));
		menu4.add(new MyMenuItem("Character count", null, 44).setAccelerator(KeyEvent.VK_F3, ActionEvent.CTRL_MASK));
		menu4.add(new MyMenuItem("Delete blank lines", null, 35));
		menu4.add(new MyMenuItem("Reverse text", null, 50));
		//
		MyMenu menu4_1 = new MyMenu("Case conversion");
		menu4.add(menu4_1);
		menu4_1.add(new MyMenuItem("Convert to upper case", "UPPERCASE", 26));
		menu4_1.add(new MyMenuItem("Convert to lower case", "LOWERCASE", 27));
		menu4_1.add(new MyMenuItem("Convert to invert case", "INVERTCASE", 28));
		//
		MyMenu menu4_2 = new MyMenu("Escape character");
		menu4.add(menu4_2);
		menu4_2.add(new MyMenuItem("Escape string", "TRANSPARENT16", 54));
		menu4_2.add(new MyMenuItem("Unescape string", null, 55));
		//
		menu4.add(new JSeparator());
		menu4.add(new MyMenuItem("Search and replace", "SEARCH16", 24).setAccelerator(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		menu4.add(new MyMenuItem("Show JColorChooser", "COLORCHOOSER16", 40));
		menu4.add(new MyMenuItem("Base converter (2-36)", "BASE16", 41));
		menu4.add(new MyMenuItem("Regex matcher", "REGEX16", 23));
		//
		menu5.add(new MyMenuItem("Insert ten equal signs", null, 30));
		menu5.add(new MyMenuItem("Insert four spaces", null, 31));
		menu5.add(new MyMenuItem("Insert spaces between characters", null, 49));
		menu5.add(new MyMenuItem("Generate random words", null, 32));
		menu5.add(new JSeparator());
		menu5.add(new MyMenuItem("Insert key words (Java)", "KEYWORDJAVA", 33));
		menu5.add(new MyMenuItem("Insert key words (html)", "KEYWORDHTML", 34));
		menu5.add(new MyMenuItem("Insert unicode character", null, 45));
		menu5.add(new MyMenuItem("Insert unicode value", null, 46));
		//
		menu6.add(new MyMenuItem("About RefluxEdit", "APPICON16", 16).setAccelerator(KeyEvent.VK_F1, ActionEvent.CTRL_MASK));
		menu6.add(new MyMenuItem("Visit SourceForge page", "VISIT16", 48));
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
