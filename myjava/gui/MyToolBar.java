/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import exec.*;
import static exec.SourceManager.*;

public class MyToolBar extends JToolBar
{
	//section 1:
	private MyToolBarButton buttonNew = new MyToolBarButton("NEW32", "New file", 1);
	private MyToolBarButton buttonOpen = new MyToolBarButton("OPEN32", "Open file", 2);
	private MyToolBarButton buttonSave = new MyToolBarButton("SAVE32", "Save as", 4);
	private MyToolBarButton buttonExport = new MyToolBarButton("EXPORT32", "Export", 43);
	private MyToolBarButton buttonPrint = new MyToolBarButton("PRINT32", "Print", 38);
	//section 2:
	private MyToolBarButton buttonUndo = new MyToolBarButton("UNDO32", "Undo", 7);
	private MyToolBarButton buttonRedo = new MyToolBarButton("REDO32", "Redo", 8);
	private MyToolBarButton buttonCut = new MyToolBarButton("CUT32", "Cut selection", 11);
	private MyToolBarButton buttonCopy = new MyToolBarButton("COPY32", "Copy selection", 12);
	private MyToolBarButton buttonPaste = new MyToolBarButton("PASTE32", "Paste", 13);
	private MyToolBarButton buttonDelete = new MyToolBarButton("DELETE32", "Delete selection", 15);
	private MyToolBarButton buttonSelectAll = new MyToolBarButton("SELECT32", "Select all", 9);
	private MyToolBarButton buttonSelectAllCopy = new MyToolBarButton("SELECT32", "Select all and copy", 10);
	//section 3:
	private MyToolBarButton buttonReplace = new MyToolBarButton("REPLACE32", "Search/Replace", 24);
	private MyToolBarButton buttonOptions = new MyToolBarButton("OPTIONS32", "Toolbar options", 0);
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
		loadConfig();
		// setup toolbar
		this.removeAll();
		// section 1
		boolean b1 = getBoolean0("ToolBar.new");
		boolean b2 = b1;
		if (b1) this.add(buttonNew);
		b1 = getBoolean0("ToolBar.open");
		b2 = b2||b1;
		if (b1) this.add(buttonOpen);
		b1 = getBoolean0("ToolBar.save");
		b2 = b2||b1;
		if (b1) this.add(buttonSave);
		b1 = getBoolean0("ToolBar.export");
		b2 = b2||b1;
		if (b1) this.add(buttonExport);
		b1 = getBoolean0("ToolBar.print");
		b2 = b2||b1;
		if (b1) this.add(buttonPrint);
		if (b2) this.addSeparator();
		// section 2
		b1 = getBoolean0("ToolBar.undo");
		b2 = b1;
		if (b1) this.add(buttonUndo);
		b1 = getBoolean0("ToolBar.redo");
		b2 = b1;
		if (b1) this.add(buttonRedo);
		b1 = getBoolean0("ToolBar.cut");
		b2 = b1;
		if (b1) this.add(buttonCut);
		b1 = getBoolean0("ToolBar.copy");
		b2 = b1||b2;
		if (b1) this.add(buttonCopy);
		b1 = getBoolean0("ToolBar.paste");
		b2 = b1||b2;
		if (b1) this.add(buttonPaste);
		b1 = getBoolean0("ToolBar.selectAll");
		b2 = b1||b2;
		if (b1) this.add(buttonSelectAll);
		b1 = getBoolean0("ToolBar.selectAllAndCopy");
		b2 = b1||b2;
		if (b1) this.add(buttonSelectAllCopy);
		b1 = getBoolean0("ToolBar.delete");
		b2 = b1||b2;
		if (b1) this.add(buttonDelete);
		if (b2) this.addSeparator();
		// section 3
		if (getBoolean0("ToolBar.replace")) this.add(buttonReplace);
		this.add(buttonOptions);
		// finally:
		this.revalidate();
		this.repaint();
	}
	
	static class MyToolBarButton extends JButton implements MouseListener
	{
		//MyToolBarButton: buttons on JToolBar
		private int x;
		MyToolBarButton(String icon, String tooltip, int x)
		{
			super();
			this.setToolTipText(tooltip);
			this.setFocusPainted(false);
			this.setBackground(new Color(224,223,227));
			if (x != 0)
			{
				/*
				 * add the general listener
				 */
				this.addMouseListener(new MyListener(x));
			}
			else
			{
				/*
				 * add this as listener (Option dialog)
				 */
				this.addMouseListener(this);
			}
			try
			{
				this.setIcon(icon(icon));
			}
			catch (Exception ex)
			{
			}
			this.x = x;
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
			if (this.x == 0)
			{
				loadConfig();
				/*
				 * option dialog:
				 * create dialog:
				 */
				JFrame w = RefluxEdit.getInstance();
				JDialog dialog = new JDialog(w, "Button selection", true);				
				MyCheckBox _new = new MyCheckBox("New", getBoolean0("ToolBar.new"));
				MyCheckBox open = new MyCheckBox("Open", getBoolean0("ToolBar.open"));
				MyCheckBox save = new MyCheckBox("Save", getBoolean0("ToolBar.save"));
				MyCheckBox export = new MyCheckBox("Export to image", getBoolean0("ToolBar.export"));
				MyCheckBox print = new MyCheckBox("Print", getBoolean0("ToolBar.print"));
				MyCheckBox undo = new MyCheckBox("Undo", getBoolean0("ToolBar.undo"));
				MyCheckBox redo = new MyCheckBox("Redo", getBoolean0("ToolBar.redo"));
				MyCheckBox cut = new MyCheckBox("Cut", getBoolean0("ToolBar.cut"));
				MyCheckBox copy = new MyCheckBox("Copy", getBoolean0("ToolBar.copy"));
				MyCheckBox paste = new MyCheckBox("Paste", getBoolean0("ToolBar.paste"));
				MyCheckBox delete = new MyCheckBox("Delete", getBoolean0("ToolBar.delete"));
				MyCheckBox selectAll = new MyCheckBox("Select all", getBoolean0("ToolBar.selectAll"));
				MyCheckBox selectAllCopy = new MyCheckBox("Select all and copy", getBoolean0("ToolBar.selectAllAndCopy"));
				MyCheckBox search = new MyCheckBox("Search/Replace", getBoolean0("ToolBar.search"));
				/*
				 * add checkboxes to dialog
				 */
				dialog.setLayout(new GridLayout(7,2,1,5));
				dialog.add(_new);
				dialog.add(open);
				dialog.add(save);
				dialog.add(export);
				dialog.add(print);
				dialog.add(undo);
				dialog.add(redo);
				dialog.add(cut);
				dialog.add(copy);
				dialog.add(paste);
				dialog.add(delete);
				dialog.add(selectAll);
				dialog.add(selectAllCopy);
				dialog.add(search);
				/*
				 * show the dialog
				 */
				dialog.pack();
				dialog.setLocationRelativeTo(w);
				dialog.setVisible(true);
				/*
				 * done:
				 */
				boolean isNew = _new.isSelected();
				boolean isOpen = open.isSelected();
				boolean isSave = save.isSelected();
				boolean isExport = export.isSelected();
				boolean isPrint = print.isSelected();
				boolean isUndo = undo.isSelected();
				boolean isRedo = redo.isSelected();
				boolean isCut = cut.isSelected();
				boolean isCopy = copy.isSelected();
				boolean isPaste = paste.isSelected();
				boolean isDelete = delete.isSelected();
				boolean isSelectAll = selectAll.isSelected();
				boolean isSelectAllCopy = selectAllCopy.isSelected();
				boolean isSearch = search.isSelected();
				dialog.dispose();
				setConfig("ToolBar.new", isNew + "");
				setConfig("ToolBar.open", isOpen + "");
				setConfig("ToolBar.save", isSave + "");
				setConfig("ToolBar.export", isExport + "");
				setConfig("ToolBar.print", isPrint + "");
				setConfig("ToolBar.undo", isUndo + "");
				setConfig("ToolBar.redo", isRedo + "");
				setConfig("ToolBar.cut", isCut + "");
				setConfig("ToolBar.copy", isCopy + "");
				setConfig("ToolBar.paste", isPaste + "");
				setConfig("ToolBar.delete", isDelete + "");
				setConfig("ToolBar.selectAll", isSelectAll + "");
				setConfig("ToolBar.selectAllAndCopy", isSelectAllCopy + "");
				setConfig("ToolBar.search", isSearch + "");
				saveConfig();
				MyToolBar.getInstance().removeAll();
				MyToolBar.getInstance().update();
			}
		}		
		@Override
		public void mouseEntered(MouseEvent ev) {}
		
		@Override
		public void mouseExited(MouseEvent ev) {}
		
		@Override
		public void mousePressed(MouseEvent ev) {}
		
		@Override
		public void mouseClicked(MouseEvent ev) {}
	}
}
