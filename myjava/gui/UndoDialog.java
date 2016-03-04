/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import myjava.util.*;
import myjava.gui.common.*;
import exec.*;

public class UndoDialog extends JDialog implements Resources
{
	private JList<UndoManager.Data> undoJList = createUndoRecordList();
	private JList<UndoManager.Data> redoJList = createUndoRecordList();
	private Tab tab;
	public UndoDialog(final Tab tab)
	{
		super(RefluxEdit.getInstance(),"Undo and Redo record",false);
		this.setLayout(new BorderLayout());
		this.getContentPane().setBackground(Color.WHITE);
		this.tab = tab;
		/*
		 * center
		 */
		final JPanel center = new JPanel(new GridLayout(1,2,0,0));
		center.add(new JScrollPane(undoJList));
		center.add(new JScrollPane(redoJList));
		this.add(center, BorderLayout.CENTER);
		/*
		 * bottom
		 */
		MyPanel bottom = new MyPanel(MyPanel.CENTER);
		MyButton undo_b = new MyButton("Use undo list item")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				int index = undoJList.getSelectedIndex();
				MyTextArea textArea = tab.getTextArea();
				if (textArea.isEditable())
				{
					textArea.setAutoBackup(false);
					textArea.getUndoManager().undo(index+1); //e.g. index=0 means undo once
					textArea.setAutoBackup(false);
					resetUndoDialogList();
				}
				else
				{
					MyListener.cannotEdit();
				}
			}
		};
		MyButton redo_b = new MyButton("Use redo list item")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				int index = redoJList.getSelectedIndex();
				MyTextArea textArea = tab.getTextArea();
				if (textArea.isEditable())
				{
					textArea.setAutoBackup(false);
					textArea.getUndoManager().redo(index+1);
					textArea.setAutoBackup(false);
					resetUndoDialogList();
				}
				else
				{
					MyListener.cannotEdit();
				}
			}
		};
		if (!isWindows)
		{
			undo_b.setPreferredSize(new Dimension(125,28));
			redo_b.setPreferredSize(new Dimension(125,28));
		}
		bottom.add(undo_b);
		bottom.add(redo_b);
		bottom.add(new MyButton("Reload")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				resetUndoDialogList();
			}
		});
		this.add(bottom, BorderLayout.PAGE_END);
		this.setSize(400,400);
	}
	
	public void resetUndoDialogList()
	{
		//reset list
		DefaultListModel<UndoManager.Data> undo_m = (DefaultListModel<UndoManager.Data>)(undoJList.getModel());
		undo_m.removeAllElements();
		for (UndoManager.Data _undo: tab.getTextArea().getUndoManager().getUndoList())
		{
			/*
			 * first one in list = peek
			 */
			undo_m.addElement(_undo);
		}
		//
		DefaultListModel<UndoManager.Data> redo_m = (DefaultListModel<UndoManager.Data>)(redoJList.getModel());
		redo_m.removeAllElements();
		for (UndoManager.Data _redo: tab.getTextArea().getUndoManager().getRedoList())
		{
			redo_m.addElement(_redo);
		}
	}
	
	public static JList<UndoManager.Data> createUndoRecordList()
	{
		//return a formatted JList
		DefaultListModel<UndoManager.Data> lm = new DefaultListModel<>();
		JList<UndoManager.Data> list = new JList<>(lm);
		list.setFont(f13);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return list;
	}
}
