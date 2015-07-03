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
import static exec.SourceManager.*;

public class MyTextArea extends JTextArea
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
	private boolean isSaved = true;
	MyTextArea()
	{
		//autoIndent
		this.indentFilter = new MyIndentFilter(this);
		((AbstractDocument)(this.getDocument())).setDocumentFilter(indentFilter);
		//
		this.undoManager = new UndoManager(this);
		//general
		this.setDragEnabled(true);
		this.setText("");
		this.setSelectedTextColor(Color.BLACK);
		this.update();
		//
		this.defaultDropTarget = this.getDropTarget();
		TextAreaListener taListener = new TextAreaListener();
		this.addMouseListener(taListener);
		this.addKeyListener(taListener);
		//build popup
		this.popup.add(new MyMenuItem("Cut", "CUT", 11));
		this.popup.add(new MyMenuItem("Copy", "COPY", 12));
		this.popup.add(new MyMenuItem("Paste", "PASTE", 13));
		this.popup.add(new MyMenuItem("Delete", "DELETE16", 15));
		this.popup.add(new JSeparator());
		this.popup.add(new MyMenuItem("Select all", "SELECT", 9));
		this.popup.add(new MyMenuItem("Select all and copy", null, 10));
		this.popup.add(new JSeparator());
		this.popup.add(new MyMenuItem("Increase indentation", "INDENT+", 18).setAccelerator(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		this.popup.add(new MyMenuItem("Decrease indentation", "INDENT-", 19).setAccelerator(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
	}
	
	public static void setGlobalProperties(boolean isEditable, boolean isLineWrap, boolean isWrapStyleWord, int tabSize, Color selectionColor, Font font, boolean autoIndent)
	{
		MyTextArea.isEditable = isEditable;
		MyTextArea.isLineWrap = isLineWrap;
		MyTextArea.isWrapStyleWord = isWrapStyleWord;
		MyTextArea.tabSize = tabSize;
		MyTextArea.selectionColor = selectionColor;
		MyTextArea.font = font;
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
	
	class TextAreaListener implements KeyListener, MouseListener
	{
		TextAreaListener()
		{
			super();
		}
		
		@Override
		public void keyTyped(KeyEvent ev)
		{
			if (!ev.isControlDown())
			{
				undoManager.clearRedoList();
			}
		}
		
		@Override
		public void keyPressed(KeyEvent ev)
		{
			//handle shortcut			
			if (ev.isControlDown())
			{				
				MyMenuItem menuItem = null;
				int code = ev.getKeyCode();
				if (code == KeyEvent.VK_Z)
				{
					menuItem = new MyMenuItem(null, null, 7);
				}
				else if (code == KeyEvent.VK_Y)
				{
					menuItem = new MyMenuItem(null, null, 8);
				}
				else if (code == KeyEvent.VK_S)
				{
					menuItem = new MyMenuItem(null, null, 4);
				}
				else if (code == KeyEvent.VK_F1)
				{
					menuItem = new MyMenuItem(null, null, 16);
				}
				else if (code == KeyEvent.VK_F)
				{
					menuItem = new MyMenuItem(null, null, 24);
				}
				else if (code == KeyEvent.VK_O)
				{
					menuItem = new MyMenuItem(null, null, 2);
				}
				else if (code == KeyEvent.VK_F2)
				{
					menuItem = new MyMenuItem(null, null, 22);
				}
				else if (code == KeyEvent.VK_F3)
				{
					menuItem = new MyMenuItem(null, null, 44);
				}
				else if (code == KeyEvent.VK_P)
				{
					menuItem = new MyMenuItem(null, null, 38);
				}
				else if (code == KeyEvent.VK_I)
				{
					menuItem = new MyMenuItem(null, null, 18);
				}
				else if (code == KeyEvent.VK_U)
				{
					menuItem = new MyMenuItem(null, null, 19);
				}
				else if (code == KeyEvent.VK_E)
				{
					menuItem = new MyMenuItem(null, null, 43);
				}
				try
				{
					menuItem.dispatchEvent(new MouseEvent(menuItem, MouseEvent.MOUSE_RELEASED, 1, MouseEvent.NOBUTTON, 0, 0, 1, false));
				}
				catch (Exception ex)
				{
				}
			}
		}
		
		@Override
		public void keyReleased(KeyEvent ev)
		{
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
	
	public UndoManager getUndoManager()
	{
		return this.undoManager;
	}
	
	public boolean isSaved()
	{
		return this.isSaved;
	}
	
	public void setSaved(boolean isSaved)
	{
		this.isSaved = isSaved;
	}
	
	public MyIndentFilter getFilter()
	{
		return this.indentFilter;
	}
}
