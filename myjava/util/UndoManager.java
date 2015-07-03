/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.util;

import java.util.*;
import javax.swing.*;
import exec.*;

public class UndoManager
{
	public ArrayDeque<String> undoList = new ArrayDeque<>();
	public ArrayDeque<String> redoList = new ArrayDeque<>();
	private JTextArea textArea;
	public UndoManager(JTextArea textArea)
	{
		super();
		this.textArea = textArea;
	}
	
	public void backup()
	{
		undoList.addFirst(textArea.getText());
	}
	
	public void backup(String str)
	{
		undoList.addFirst(str);
	}
	
	public ArrayDeque<String> getUndoList()
	{
		return undoList.clone();
	}
	
	public ArrayDeque<String> getRedoList()
	{
		return redoList.clone();
	}
	
	public void undo()
	{
		String s = undoList.peek();
		if (s == null)
		{
			JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(textArea),"Reached undo limit!","Error",JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			redoList.addFirst(textArea.getText());
			textArea.setText(undoList.pop());
		}
	}
	
	public void redo()
	{
		String s = redoList.peek();
		if (s == null)
		{
			JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(textArea),"Reached redo limit!","Error",JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			undoList.addFirst(textArea.getText());
			textArea.setText(redoList.pop());
		}
	}
	
	public void undo(int time)
	{
		if (time > undoList.size())
		{
			throw new IllegalArgumentException("undo time " + time + " > " + undoList.size());
		}
		else
		{
			redoList.addFirst(textArea.getText());
			for (int i=0; i<time-1; i++)
			{
				redoList.addFirst(undoList.pop());
			}
			textArea.setText(undoList.pop());
		}
	}
	
	public void redo(int time)
	{
		if (time > redoList.size())
		{
			throw new IllegalArgumentException("redo time " + time + " > " + undoList.size());
		}
		else
		{
			undoList.addFirst(textArea.getText());
			for (int i=0; i<time-1; i++)
			{
				undoList.addFirst(redoList.pop());
			}
			textArea.setText(redoList.pop());
		}
	}
	
	public void clearRedoList()
	{
		redoList.clear();
	}
}
