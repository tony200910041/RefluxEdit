/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
	
	@Override
	public synchronized void dragEnter(DropTargetDragEvent dtde)
	{
		Transferable trans = dtde.getTransferable();
		DataFlavor[] flavors = trans.getTransferDataFlavors();
		for (DataFlavor flavor: flavors)
		{
			if (DataFlavor.javaFileListFlavor.equals(flavor))
			{
				tab.getLayerUI().setDrop(true);
				tab.getLayer().repaint();
				return;
			}
		}
	}
	
	@Override
	public synchronized void dragExit(DropTargetEvent dtde)
	{
		this.resetLayer();
	}
	
	@Override
	public synchronized void drop(DropTargetDropEvent dtde)
	{
		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		try
		{
			SourceManager.loadConfig();
			SourceManager.setCaret(this.tab.getFile(), this.tab.getTextArea().getCaretPosition());
			SourceManager.saveConfig();
			Transferable trans = dtde.getTransferable();
			DataFlavor[] flavors = trans.getTransferDataFlavors();
			java.util.List<File> files = null;
			String text = null;
			for (DataFlavor flavor: flavors)
			{
				if (DataFlavor.javaFileListFlavor.equals(flavor))
				{
					@SuppressWarnings("unchecked")
					java.util.List<File> received = (java.util.List<File>)(trans.getTransferData(DataFlavor.javaFileListFlavor));
					files = received;
					break;
				}
				else if (DataFlavor.stringFlavor.equals(flavor))
				{
					String s = (String)(trans.getTransferData(DataFlavor.stringFlavor));
					if (s != null)
					{
						File received = new File(s);
						if (received.exists()&&received.isFile())
						{
							files = Arrays.asList(received.getAbsoluteFile());
						}
						else
						{
							text = s;
						}
						break;
					}
				}
			}
			if (files != null)
			{
				int option;
				if (SourceManager.getBoolean0("ConfirmDrag"))
				{
					option = JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(tab),"Open file " + files.toString() + "?", "Confirm Dialog", JOptionPane.YES_NO_OPTION);
				}
				else option = JOptionPane.YES_OPTION;
				if (option == JOptionPane.YES_OPTION)
				{
					outFor:
					for (File file: files)
					{
						MainPanel.openFileAndWait(file);
					}
				}
			}
			else if (text != null)
			{
				MyTextArea textArea = tab.getTextArea();
				textArea.insert(text, textArea.getCaretPosition());
			}
		}
		catch (Exception ex)
		{
			ExceptionDialog.exception(ex);
		}
		finally
		{
			this.resetLayer();
		}
	}
	
	private void resetLayer()
	{
		tab.getLayerUI().setDrop(false);
		tab.getLayer().repaint();
	}
}
