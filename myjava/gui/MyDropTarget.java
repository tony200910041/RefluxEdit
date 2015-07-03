package myjava.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import exec.*;

public class MyDropTarget extends DropTarget
{
	private Tab tab;
	public MyDropTarget(Tab tab)
	{
		super();
		this.tab = tab;
	}
		
	public synchronized void drop(DropTargetDropEvent dtde)
	{
		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		try
		{
			SourceManager.loadConfig();
			SourceManager.setCaret(this.tab.getFile(), this.tab.getTextArea().getCaretPosition());
			SourceManager.saveConfig();
			File file = (File)(((java.util.List)(dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor))).get(0));
			int option;
			if (SourceManager.getBoolean0("ConfirmDrag"))
			{
				option = JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(tab),"Open file " + file.getPath() + "?", "Confirm Dialog", JOptionPane.YES_NO_OPTION);
			}
			else option = JOptionPane.YES_OPTION;
			if (option == JOptionPane.YES_OPTION)
			{
				Tab newTab = Tab.getNewTab();
				MainPanel.getInstance().addTab(newTab);
				newTab.open(file);
			}
		}
		catch (Exception ex)
		{
			ExceptionDialog.exception(ex);
		}
	}
}
