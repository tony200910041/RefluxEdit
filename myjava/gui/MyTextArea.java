/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import exec.*;
import myjava.gui.*;
import myjava.util.*;
import myjava.gui.common.*;
import static exec.SourceManager.*;
import static myjava.util.StaticUtilities.*;

public class MyTextArea extends JTextArea implements MouseListener
{
	/*
	 * static properties, apply to all MyTextArea
	 */
	static
	{
		loadConfig();
	}
	private static boolean isEditable = getBoolean0("isEditable");
	private static boolean isLineWrap = getBoolean0("LineWrap");
	private static boolean isWrapStyleWord = getBoolean0("WrapStyleWord");
	private static boolean showLineCounter = getBoolean0("showLineCounter");
	private static int tabSize;
	private static Color selectionColor;
	private static Font font;
	static
	{
		//tab size
		try
		{
			tabSize = Integer.parseInt(getConfig0("TabSize"));
		}
		catch (Exception ex)
		{
			tabSize = 4;
		}
		//selection color
		try
		{
			selectionColor = new Color(Short.parseShort(getConfig0("SelectionColor.r")), Short.parseShort(getConfig0("SelectionColor.g")), Short.parseShort(getConfig0("SelectionColor.b")));
		}
		catch (Exception ex)
		{
			selectionColor = new Color(244,223,255);
		}
		//font
		String fontName = getConfig0("TextAreaFont.fontName");
		if (fontName == null)
		{
			fontName = "Microsoft Jhenghei";
		}
		int fontStyle, fontSize;
		try
		{
			fontStyle = Integer.parseInt(getConfig0("TextAreaFont.fontStyle"));
			if ((fontStyle>2)||(fontStyle<0))
			{
				fontStyle = 0;
			}
		}
		catch (Exception ex)
		{
			fontStyle = 0;
		}
		try
		{
			fontSize = Integer.parseInt(getConfig0("TextAreaFont.fontSize"));
			if ((fontSize>200)||(fontSize<1))
			{
				fontSize = 15;
			}
		}
		catch (Exception ex)
		{
			fontSize = 15;
		}
		font = new Font(fontName,fontStyle,fontSize);
	}
	/*
	 * instance properties/fields
	 */
	private JPopupMenu popup = new JPopupMenu();
	private MyIndentFilter indentFilter;
	private DropTarget defaultDropTarget;
	private UndoManager undoManager;
	private boolean autoBackupEnabled = true;
	private boolean isOpening = false;
	MyTextArea()
	{
		//autoIndent
		this.indentFilter = new MyIndentFilter(this);
		((AbstractDocument)(this.getDocument())).setDocumentFilter(indentFilter);
		//
		this.undoManager = new UndoManager(this);
		//general
		this.setDragEnabled(true);
		this.setSelectedTextColor(Color.BLACK);
		this.update();
		this.setBorder(new TextAreaBorder());
		//
		this.defaultDropTarget = this.getDropTarget();
		this.addMouseListener(this);
		this.setupInputMap();
		//build popup
		this.popup.add(new MyMenuItem("Cut", "CUT", 11, KeyEvent.VK_X));
		this.popup.add(new MyMenuItem("Copy", "COPY", 12, KeyEvent.VK_C));
		this.popup.add(new MyMenuItem("Paste", "PASTE", 13, KeyEvent.VK_V));
		this.popup.add(new MyMenuItem("Delete", "DELETE16", 15, KeyEvent.VK_DELETE, 0));
		this.popup.add(new JSeparator());
		this.popup.add(new MyMenuItem("Select all", "SELECT", 9, KeyEvent.VK_A));
		this.popup.add(new MyMenuItem("Select all and copy", null, 10));
		this.popup.add(new JSeparator());
		this.popup.add(new MyMenuItem("Increase indentation", "INDENT+", 18, KeyEvent.VK_I));
		this.popup.add(new MyMenuItem("Decrease indentation", "INDENT-", 19, KeyEvent.VK_U));
		this.popup.add(new JSeparator());
		this.popup.add(new MyMenuItem("Word count", null, 22, KeyEvent.VK_F2));
	}
	
	public static void setGlobalProperties(boolean isEditable, boolean isLineWrap, boolean isWrapStyleWord, int tabSize, Color selectionColor, Font font, boolean autoIndent, boolean showLineCounter)
	{
		MyTextArea.isEditable = isEditable;
		MyTextArea.isLineWrap = isLineWrap;
		MyTextArea.isWrapStyleWord = isWrapStyleWord;
		MyTextArea.tabSize = tabSize;
		MyTextArea.selectionColor = selectionColor;
		MyTextArea.font = font;
		MyTextArea.showLineCounter = showLineCounter;
		MyIndentFilter.setAutoIndent(autoIndent);
	}
	
	public static void setGlobalProperties(boolean isEditable)
	{
		MyTextArea.isEditable = isEditable;
	}
	
	public void update()
	{
		//setup according to static properties
		if (isEditable)
		{
			this.setBackground(Color.WHITE);
		}
		else
		{
			this.setBackground(new Color(245,245,245));
		}
		this.setEditable(isEditable);
		this.setLineWrap(isLineWrap);
		this.setWrapStyleWord(isWrapStyleWord);
		this.setTabSize(tabSize);
		this.setSelectionColor(selectionColor);
		this.setFont(font);
		if (showLineCounter)
		{
			this.setBorder(new TextAreaBorder());
		}
		else
		{
			this.setBorder(null);
		}
	}
	
	public void setAutoBackup(boolean autoBackupEnabled)
	{
		this.autoBackupEnabled = autoBackupEnabled;
	}
	
	public boolean isAutoBackup()
	{
		return this.autoBackupEnabled;
	}
	
	public void setOpening(boolean isOpening)
	{
		this.isOpening = isOpening;
	}
	
	public boolean isOpening()
	{
		return this.isOpening;
	}
	
	private void setupInputMap()
	{
		InputMap inputMap = this.getInputMap();
		ActionMap actionMap = this.getActionMap();
		/*
		 * InputMap
		 */
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, Resources.OS_CTRL_MASK), "export");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, Resources.OS_CTRL_MASK), "search");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, Resources.OS_CTRL_MASK), "about");		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, Resources.OS_CTRL_MASK), "wordcount");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, Resources.OS_CTRL_MASK), "charcount");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, Resources.OS_CTRL_MASK), "indent+");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, Resources.OS_CTRL_MASK), "open");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, Resources.OS_CTRL_MASK), "print");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Resources.OS_CTRL_MASK), "saveas");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, Resources.OS_CTRL_MASK), "indent-");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Resources.OS_CTRL_MASK), "redo");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Resources.OS_CTRL_MASK), "undo");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "compile");
		/*
		 * ActionMap
		 */
		actionMap.put("export", new MyListener(43));
		actionMap.put("search", new MyListener(24));
		actionMap.put("about", new MyListener(16));
		actionMap.put("wordcount", new MyListener(22));
		actionMap.put("charcount", new MyListener(44));
		actionMap.put("indent+", new MyListener(18));
		actionMap.put("open", new MyListener(2));
		actionMap.put("print", new MyListener(38));
		actionMap.put("saveas", new MyListener(4));
		actionMap.put("indent-", new MyListener(19));
		actionMap.put("redo", new MyListener(8));
		actionMap.put("undo", new MyListener(7));
		actionMap.put("compile", new MyListener(53));
	}
	
	public UndoManager getUndoManager()
	{
		return this.undoManager;
	}
	
	public MyIndentFilter getFilter()
	{
		return this.indentFilter;
	}
	
	@Override
	public void mouseReleased(MouseEvent ev)
	{
		if (ev.isPopupTrigger())
		{
			popup.show(MyTextArea.this, ev.getX(), ev.getY());
		}
	}
	
	@Override
	public void mousePressed(MouseEvent ev)
	{
		//reset MyTextArea.this drop target: remove receiving file info outside
		MyTextArea.this.setDropTarget(defaultDropTarget);
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
	}
	
	@Override
	public void mouseEntered(MouseEvent ev)
	{
	}
	
	@Override
	public void mouseExited(MouseEvent ev)
	{
	}
}
