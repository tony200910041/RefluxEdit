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
	public ArrayDeque<Data> undoList = new ArrayDeque<>();
	public ArrayDeque<Data> redoList = new ArrayDeque<>();
	private JTextArea textArea;
	public UndoManager(JTextArea textArea)
	{
		super();
		this.textArea = textArea;
	}
	
	private Data getData()
	{
		String text = textArea.getText();
		int selectionStart = textArea.getSelectionStart();
		int selectionEnd = textArea.getSelectionEnd();
		return new Data(textArea.getText(),selectionStart,selectionEnd);
	}
	
	private void restore(Data data)
	{
		this.textArea.setText(data.getText());
		int start = data.getSelectionStart();
		int end = data.getSelectionEnd();
		if (start != end)
		{
			this.textArea.select(start,end);
		}
		else
		{
			this.textArea.setCaretPosition(end);
		}
	}
	
	public void backup()
	{
		undoList.addFirst(getData());
	}
	
	@Deprecated
	public void backup(String str)
	{
		undoList.addFirst(new Data(str,textArea.getCaretPosition()));
	}
	
	public Deque<Data> getUndoList()
	{
		return undoList.clone();
	}
	
	public Deque<Data> getRedoList()
	{
		return redoList.clone();
	}
	
	public void undo()
	{
		Data data = undoList.peek();
		if (data == null)
		{
			JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(textArea),"Reached undo limit!","Error",JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			redoList.addFirst(getData());
			restore(undoList.pop());
		}
	}
	
	public void redo()
	{
		Data data = redoList.peek();
		if (data == null)
		{
			JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(textArea),"Reached redo limit!","Error",JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			undoList.addFirst(getData());
			restore(redoList.pop());
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
			redoList.addFirst(getData());
			for (int i=0; i<time-1; i++)
			{
				redoList.addFirst(undoList.pop());
			}
			restore(undoList.pop());
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
			undoList.addFirst(getData());
			for (int i=0; i<time-1; i++)
			{
				undoList.addFirst(redoList.pop());
			}
			restore(redoList.pop());
		}
	}
	
	public void clearRedoList()
	{
		redoList.clear();
	}
	
	public static class Data
	{
		private String str;
		private int selectionStart,selectionEnd;
		public Data(String str, int selectionStart, int selectionEnd)
		{
			super();
			this.str = str;
			this.selectionStart = selectionStart;
			this.selectionEnd = selectionEnd;
		}
		
		Data(String str, int caret)
		{
			this(str,caret,caret);
		}
		
		String getText()
		{
			return this.str;
		}
		
		int getSelectionStart()
		{
			return this.selectionStart;
		}
		
		int getSelectionEnd()
		{
			return this.selectionEnd;
		}
		
		@Override
		public String toString()
		{
			int length = str.length();
			return length>25?(str.substring(0,25)+"..."):(length>1?(str+" ("+length+" characters)"):(str+" ("+length+" character)"));
		}
	}
}
