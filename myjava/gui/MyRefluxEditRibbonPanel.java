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
import myjava.util.*;
import exec.*;

public class MyRefluxEditRibbonPanel extends MyRibbonPanel
{
	private static final String OS_MASK_STRING = Utilities.getMenuShortcutKeyMaskString();
	private static final MyRefluxEditRibbonPanel INSTANCE = new MyRefluxEditRibbonPanel();
	private MyRefluxEditRibbonPanel()
	{
		super();
		this.addAsFirstComponent(MyRibbonFirst.getInstance());
		MyRibbonTab tab2 = new MyRibbonTab("EDIT");
		MyRibbonTab tab3 = new MyRibbonTab("VIEW");
		MyRibbonTab tab4 = new MyRibbonTab("TOOLS");
		MyRibbonTab tab5 = new MyRibbonTab("INSERT");
		MyRibbonTab tab6 = new MyRibbonTab("HELP");
		this.addTab(tab2);
		this.addTab(tab3);
		this.addTab(tab4);
		this.addTab(tab5);
		this.addTab(tab6);
		// Edit
		tab2.add(new MyRibbonButton("Undo", "UNDO32", "<html><font size=\"4\"><b>Undo&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+Z</b></font><br>Undo the last amendment.</font></html>", false, 7));
		tab2.add(new MyRibbonButton("Redo", "REDO32", "<html><font size=\"4\"><b>Redo&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+Y</b></font><br>Redo the undo amendment.</html>", false, 8));
		JPanel tab2_1 = new JPanel(new GridLayout(2,1,10,10));
		tab2_1.setOpaque(false);
		tab2_1.add(new MyRibbonButton("Select all", "SELECT", "<html><font size=\"4\"><b>Select all&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+A</b></font><br>Select all text in the text area.</html>", true, 9));
		tab2_1.add(new MyRibbonButton("Select all and copy", "SELECT", "<html><font size=\"4\"><b>Select all and copy</b></font><br>Select all text in the text area and<br>copy to the system clipboard.</html>", true, 10));
		tab2.add(tab2_1);
		tab2.add(createSeparator());
		tab2.add(new MyRibbonButton("Cut", "CUT32", "<html><font size=\"4\"><b>Cut selected text&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+X</b></font><br>The selected text will be moved<br>to the system clipboard.</html>", false, 11));
		tab2.add(new MyRibbonButton("Copy", "COPY32", "<html><font size=\"4\"><b>Copy selected text&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+C</b></font><br>The selected text will be copied<br>to the system clipboard.</html>", false, 12));
		JPanel tab2_2 = new JPanel(new GridLayout(3,1,3,3));
		tab2_2.setOpaque(false);
		tab2_2.add(new MyRibbonButton("Paste", "PASTE", "<html><font size=\"4\"><b>Paste text&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+V</b></font><br>Paste the text in the system clipboard<br>to the text area.</html>", true, 13));
		tab2_2.add(new MyRibbonButton("Paste on next line", "PASTE", "<html><font size=\"4\"><b>Paste text on next line</b></font><br>Insert the text in the system clipboard<br>on the next line.</html>", true, 14));
		tab2_2.add(new MyRibbonButton("Delete", "DELETE16", "<html><font size=\"4\"><b>Delete selected text&nbsp;&nbsp;&nbsp;Delete</b></font><br>The selected text will be deleted.</html>", true, 15));
		tab2.add(tab2_2);
		tab2.add(createSeparator());
		JPanel tab2_3 = new JPanel(new GridLayout(2,1,10,10));
		tab2_3.setOpaque(false);
		tab2_3.add(new MyRibbonButton("Indent\u2191", "INDENT+", "<html><font size=\"4\"><b>Increase indentation&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+I</b></font><br>Increase the indentation of the selected text by 1</html>", true, 18));
		tab2_3.add(new MyRibbonButton("Indent\u2193", "INDENT-", "<html><font size=\"4\"><b>Decrease indentation&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+U</b></font><br>Decrease the indentation of the selected text by 1</html>", true, 19));
		tab2.add(tab2_3);
		// View
		tab3.add(new MyRibbonButton("<html><center>Editing/<br>viewing</center></html>", "EDIT32", "<html><font size=\"4\"><b>Enable/disable editing</b></font><br>Click here to disable/re-enable editing.<br></html>", false, 17));
		tab3.add(new MyRibbonButton("<html><center>Always<br>on top</center></html>", "ONTOP", "<html><font size=\"4\"><b>Enable/disable always on top</b></font><br>Click here to enable/disable RefluxEdit always staying on top.</html>", false, 21));
		tab3.add(new MyRibbonButton("<html><center><font color=\"green\">Undo</font><br>dialog</center></htnml>", "UNDODIALOG32", "<html><font size=\"4\"><b>Undo record dialog</b></font><br>Show the undo record dialog.</html>", false, 52));
		tab3.add(new MyRibbonButton("<html><center>Clipboard<br>listener</center></html>", "PASTE32", "<html><font size=\"4\"><b>Clipboard listener</b></font><br>Show the clipboard listener dialog, which monitors clipboard changes.</html>", false, 29));
		tab3.add(new MyRibbonButton("<html><center>File<br>browser</center></html>", "FILEBROWSER32", "<html><font size=\"4\"><b>File browser</b></font><br>Convenient dialog to browse and open files.</html>", false, 59));
		tab3.add(createSeparator());
		tab3.add(new MyRibbonButton("<html><center>Goto<br>line</center></html>", "GOTOLINE32", "<html><font size=\"4\"><b>Goto line</b></font><br>Goto specific line immediately.</html>", false, 47));
		MyPanel panel1 = new MyPanel(MyPanel.CENTER);
		panel1.setPreferredSize(new Dimension(1,85));
		tab3.add(panel1);
		// Tools
		tab4.add(new MyRibbonButton("<html><center>Compile<br>code</center></html>", "COMPILE32", "<html><font size=\"4\"><b>Compile code&nbsp;&nbsp;&nbsp;F8</b></font><br>Compile the code.</html>", false, 53));
		tab4.add(createSeparator());
		JPanel tab4_1 = new JPanel(new GridLayout(3,1,3,3));
		tab4_1.setOpaque(false);
		tab4_1.add(new MyRibbonButton("Word count", "WORDCOUNT", "<html><font size=\"4\"><b>Word count&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+F2</b></font><br>Count the number of words that are in the selected text,<br>or all words if no text is selected.</html>", true, 22));
		tab4_1.add(new MyRibbonButton("Character count", "CHARACTERCOUNT", "<html><font size=\"4\"><b>Character count&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+F3</b></font><br>Count the number of characters that are in the selected text,<br>or all characters if no text is selected.</html>", true, 44));
		tab4_1.add(new MyRibbonButton("Reverse text", null, "<html><font size=\"4\"><b>Reverse characters</b></font><br>Reverse all characters!</html>", true, 50));			
		tab4.add(tab4_1);
		tab4.add(createSeparator());
		tab4.add(new MyRibbonButton("<html><center><font color=\"red\">Delete</font><br>blank<br>lines</center></html>", null, "<html><font size=\"4\"><b>Delete blank lines</b></font><br>ALL blank lines will be deleted.</html>", false, 35));
		JPanel tab4_2 = new JPanel(new GridLayout(3,1,3,3));
		tab4_2.setOpaque(false);
		tab4_2.add(new MyRibbonButton("Uppercase", "UPPERCASE", "<html><font size=\"4\"><b>Convert to uppercase</b></font><br>Convert the selected text to uppercase,<br>or all characters if no text is selected.</html>", true, 26));
		tab4_2.add(new MyRibbonButton("Lowercase", "LOWERCASE", "<html><font size=\"4\"><b>Convert to lowercase</b></font><br>Convert the selected text to lowercase,<br>or all characters if no text is selected.</html>", true, 27));
		tab4_2.add(new MyRibbonButton("Invert case", "REVERSECASE", "<html><font size=\"4\"><b>Convert to invert case</b></font><br>Convert the selected text to invert case<br>(uppercase to lowercase, and lowercase to uppercase),<br>or all characters if no text is selected.</html>", true, 28));
		tab4.add(tab4_2);
		tab4.add(createSeparator());
		JPanel tab4_3 = new JPanel(new GridLayout(3,1,3,3));
		tab4_3.setOpaque(false);
		tab4_3.add(new MyRibbonButton("<html>Search/<br>Replace</html>", "SEARCH16", "<html><font size=\"4\"><b>Search and Replace&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+F</b></font><br>Integrated search and replace functions,<br>supporting regex and case-sensitivity.</html>", true, 24));
		tab4_3.add(new MyRibbonButton("Escape", null, "<html><font size=\"4\"><b>Escape text</b></font><br>Convert all unescaped characters to escaped characters.<br>For example, a new line break will be converted to \\n.</html>", true, 54));
		tab4_3.add(new MyRibbonButton("Unescape", null, "<html><font size=\"4\"><b>Unescape text</b></font><br>Convert all escaped characters to unescaped characters.<br>For example, \\t will be converted to a tab.</html>", true, 55));
		tab4.add(tab4_3);
		tab4.add(createSeparator());
		JPanel tab4_4 = new JPanel(new GridLayout(3,1,3,3));
		tab4_4.setOpaque(false);
		tab4_4.add(new MyRibbonButton("Color chooser", "COLORCHOOSER16", "<html><font size=\"4\"><b>Show color chooser</b></font><br>A color chooser will be shown which allows you to choose<br>a color and insert REB, HSV or HEX code.</html>", true, 40));
		tab4_4.add(new MyRibbonButton("Base converter", "BASE16", "<html><font size=\"4\"><b>Base converter</b></font><br>Convert numbers between base 2, 8, 10 and 16.</html>", true, 41));
		tab4_4.add(new MyRibbonButton("Regex matcher", "REGEX16", "<html><font size=\"4\"><b>Regex matcher</b></font><br>Test if a string matches a regular expression.</html>", true, 23));
		tab4.add(tab4_4);
		// Insert
		JPanel tab5_1 = new JPanel(new GridLayout(3,1,3,3));
		tab5_1.setOpaque(false);
		tab5_1.add(new MyRibbonButton("10 \"=\"", null, "<html><font size=\"4\"><b>Insert 10 \"=\"</b></font><br>Insert ten equal signs</html>", true, 30));
		tab5_1.add(new MyRibbonButton("Four \" \"", null, "<html><font size=\"4\"><b>Insert four spaces</b></font><br>Insert four spaces. Useful for programmers.</html>", true, 31));
		tab5_1.add(new MyRibbonButton("Spaces", null, "<html><font size=\"4\"><b>Spaces</b></font><br>Insert spaces between characters.</html>", true, 49));
		tab5.add(tab5_1);
		tab5.add(createSeparator());
		tab5.add(new MyRibbonButton("<html><center>Random<br>words</center></html>", "RANDOM", "<html><font size=\"4\"><b>Generate random words</b></font><br>Generate specified number of \"words\" randomly.<br>The words will be between 1 and 10 character(s) long.<br>Note that performing this action may take a long time.</html>", false, 32));
		tab5.add(new MyRibbonButton("<html><center>Java<br>keywords</center></html>", "KEYWORDJAVA32", "<html><font size=\"4\"><b>Insert Java keywords</b></font><br>Insert Java keywords. Useful for Java developers.<br>More will be introduced in later versions.</html>", false, 33));
		tab5.add(new MyRibbonButton("<html><center>HTML<br>keywords</center></html>", "KEYWORDHTML32", "<html><font size=\"4\"><b>Insert HTML keywords</b></font><br>Insert HTML keywords. Useful for web developers.<br>More will be introduced in later versions.</html>", false, 34));
		tab5.add(new MyRibbonButton("<html><center>Unicode<br>character</center></html>", "UNICODE32", "<html><font size=\"4\"><b>Insert unicode character</b></font><br>Insert unicode character by given code value.</html>", false, 45));
		tab5.add(new MyRibbonButton("<html><center>Unicode<br>value</center></html>", "UNICODE32", "<html><font size=\"4\"><b>Insert unicode value</b></font><br>Insert unicode value by given character.</html>", false, 46));
		// About
		MyPanel panel2 = new MyPanel(MyPanel.CENTER);
		panel2.setPreferredSize(new Dimension(5,85));
		tab6.add(panel2);
		tab6.add(new MyRibbonButton("<html><center>About<br>RefluxEdit</center></html>", "APPICON32", "<html><font size=\"4\"><b>About RefluxEdit&nbsp;&nbsp;&nbsp;"+OS_MASK_STRING+"+F1</b></font><br>RefluxEdit is a lightweight plain text editor written in Java by tony200910041.<br>GitHub page: https://github.com/tony200910041/RefluxEdit</html>", false, 16));
		tab6.add(new MyRibbonButton("<html><center>About<br>MPL 2.0</center></html>", "LICENSE32", "<html><font size=\"4\"><b>About Mozilla Public License 2.0</b></font><br>RefluxEdit is released under MPL 2.0. You are welcome to learn more about this license.</html>", false, 58));
		tab6.add(new MyRibbonButton("<html><center>Visit<br>GitHub</center></html>", "VISIT32", "<html><font size=\"4\"><b>Visit GitHub homepage</b></font><br>https://github.com/tony200910041/RefluxEdit/releases</html>", false, 48));
		tab6.add(createSeparator());
		tab6.add(new MyRibbonButton("<html>Options</html>", "OPTIONS32", "<html><font size=\"4\"><b>Options</b></font><br>Miscellaneous options</html>", false, 39));
		//"<html><font size=\"4\"><b></b></font><br></html>"
	}
	
	public static MyRefluxEditRibbonPanel getInstance()
	{
		return INSTANCE;
	}
	
	void addTab(MyRibbonTab tab)
	{
		super.addTab(tab.getName(), tab);
	}
	
	static class MyRibbonTab extends JPanel
	{
		private String name;
		public MyRibbonTab(String name)
		{
			super();
			this.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
			this.setBackground(Color.WHITE);
			this.setPreferredSize(new Dimension(0,85));
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
	}
	
	static class MyRibbonButton extends JPanel implements MouseListener, Resources
	{
		private JLabel label;
		private String icon;
		private boolean isHorizontal;
		private int x;
		MyRibbonButton(String text, String icon, String tooltip, boolean isHorizontal, int x)
		{
			this(text,icon,isHorizontal,x);
			this.setToolTipText(tooltip);
		}
		
		MyRibbonButton(String text, String icon, boolean isHorizontal, int x)
		{
			super();
			label = new MyLabel(text);
			if (isHorizontal)
			{
				this.setLayout(new FlowLayout(FlowLayout.LEFT));
				this.add(new MyLabel("    "));
				this.add(label);
				this.label.setFont(new Font(f13.getName(), Font.PLAIN, 12));
				this.setSize(this.getPreferredSize()); // pack()
				this.setPreferredSize(new Dimension(this.getWidth(),22));
			}
			else
			{
				this.setLayout(new BorderLayout());
				MyPanel panel = new MyPanel(MyPanel.CENTER);
				panel.add(label);
				panel.setOpaque(false);
				this.add(panel, BorderLayout.SOUTH);
				this.setSize(this.getPreferredSize());
				this.setPreferredSize(new Dimension(this.getWidth()+8,75));
			}
			this.isHorizontal = isHorizontal;
			this.icon = icon;
			this.setBorder(null);
			this.setBackground(Color.WHITE);
			this.addMouseListener(this);
			this.addMouseListener(new MyListener(x));
		}
		
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if (this.icon != null)
			{
				ImageIcon image = null;
				try
				{
					image = SourceManager.icon(this.icon);
					if (this.isHorizontal)
					{
						g.drawImage(image.getImage(),2,(this.getHeight()-image.getIconHeight())/2,null);
					}
					else
					{
						g.drawImage(image.getImage(),(this.getWidth()-image.getIconWidth())/2,1,null);
					}
				}
				catch (Exception ex)
				{
				}
			}
		}
		
		/*
		 * button effect: become black when clicked
		 */
		@Override
		public void mousePressed(MouseEvent ev)
		{
			this.setBackground(new Color(230,230,230));
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
			this.setBackground(new Color(240,240,240));
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
		{
			this.setBackground(Color.WHITE);
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
			this.setBackground(Color.WHITE);
		}
		
		@Override
		public void mouseClicked(MouseEvent ev)
		{
		}
	}
	
	private static JSeparator createSeparator()
	{
		JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
		sep.setPreferredSize(new Dimension(2,85));
		return sep;
	}
}
