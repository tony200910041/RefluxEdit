/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import javax.swing.*;
import javax.swing.text.*;
import exec.*;

public class MyIndentFilter extends DocumentFilter
{
	private static boolean autoIndent = SourceManager.getBoolean0("autoIndent");
	private static String indentString = SourceManager.getConfig0("autoIndentString");
	static
	{
		if ((indentString==null)||((!indentString.equals("\t"))&&(!indentString.equals("    "))))
		{
			indentString = "\t";
		}
	}
	private MyTextArea textArea;
	public MyIndentFilter(MyTextArea textArea)
	{
		super();
		this.textArea = textArea;
	}
	
	public static void setAutoIndent(boolean autoIndent)
	{
		MyIndentFilter.autoIndent = autoIndent;
	}
	
	public static boolean isAutoIndent()
	{
		return MyIndentFilter.autoIndent;
	}
	
	public static void setIndentString(String indentString)
	{
		MyIndentFilter.indentString = indentString;
	}
	
	public static String getIndentString()
	{
		return MyIndentFilter.indentString;
	}
	
	@Override
	public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException
	{
		/*
		 * backup
		 */
		if (this.textArea.isOpening())
		{
			super.insertString(fb,offset,string,attr);
		}
		else if (this.textArea.isEditable())
		{
			if (this.textArea.isAutoBackup())
			{
				this.textArea.getUndoManager().backup();
			}
			if (("\n").equals(string)&&autoIndent)
			{
				String _char = getLastCharacter(fb, offset);
				if (_char.equals("{"))
				{
					string = string + indentString + getIndentOfLastLine(fb, offset); //increase indentation
				}
				else
				{
					string = string + getIndentOfLastLine(fb, offset); //no indentation
				}
				super.insertString(fb, offset, string, attr);
			}
			else if (("}").equals(string)&&autoIndent)
			{
				super.insertString(fb, offset, string, attr);
				AbstractDocument doc = (AbstractDocument)(fb.getDocument());
				int start = textArea.getLineStartOffset(textArea.getLineOfOffset(offset));				
				doc.replace(start,offset-start,removeOneTab(doc.getText(start,offset-start)),attr);
			}
			else super.insertString(fb, offset, string, attr);
		}
		else
		{
			cannotEdit();
		}
	}
	
	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException
	{
		if (this.textArea.isEditable())
		{
			if (this.textArea.isAutoBackup())
			{
				this.textArea.getUndoManager().backup();
			}
			if (("\n").equals(string)&&autoIndent)
			{
				String _char = getLastCharacter(fb, offset);
				if (_char.equals("{"))
				{
					string = string + indentString + getIndentOfLastLine(fb, offset);
				}
				else
				{
					string = string + getIndentOfLastLine(fb, offset);
				}
				super.replace(fb, offset, length, string, attr);
			}
			else if (("}").equals(string)&&autoIndent)
			{
				super.replace(fb, offset, length, string, attr);
				if (containsTabAndSpaceOnly(offset))
				{
					AbstractDocument doc = (AbstractDocument)(fb.getDocument());
					int start = textArea.getLineStartOffset(textArea.getLineOfOffset(offset));				
					doc.replace(start,offset-start,removeOneTab(doc.getText(start,offset-start)),attr);
				}
			}
			else super.replace(fb, offset, length, string, attr);
		}
		else
		{
			cannotEdit();
		}
	}
	
	@Override
	public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException
	{
		if (this.textArea.isEditable())
		{
			if (this.textArea.isAutoBackup())
			{
				this.textArea.getUndoManager().backup();
			}
			super.remove(fb,offset,length);
		}
		else
		{
			cannotEdit();
		}
	}
	
	private String getIndentOfLastLine(DocumentFilter.FilterBypass fb, int offset) throws BadLocationException
	{
		Document doc = fb.getDocument();
		int line = textArea.getLineOfOffset(offset);
		if (line != 0)
		{
			int start = textArea.getLineStartOffset(line);
			int end = textArea.getLineEndOffset(line);
			String str = "";
			for (int i=start; i<end; i++)
			{
				String character = doc.getText(i,1);
				if (character.equals("\t")) str += "\t";
				else if (character.equals(" ")) str += " ";
				else break;
			}
			return str;
		}
		else return "";
	}
	
	private String getLastCharacter(DocumentFilter.FilterBypass fb, int offset) throws BadLocationException
	{
		/*
		 * return the "last" character (not \n or " ")
		 */
		Document doc = fb.getDocument();
		for (int i=offset; i>textArea.getLineStartOffset(textArea.getLineOfOffset(offset)); i--)
		{
			String _char = doc.getText(i-1,1);
			if ((!_char.equals("\n"))&&(!_char.equals(" "))) return _char;
		}
		return "1";
	}
	
	private String removeOneTab(String text)
	{
		if (text.contains(indentString))
		{
			int index = text.lastIndexOf(indentString);
			text = text.substring(0,index)+text.substring(index+indentString.length(),text.length());
		}
		return text;
	}
	
	private boolean containsTabAndSpaceOnly(int offset) throws BadLocationException
	{
		int line = textArea.getLineOfOffset(offset);
		int start = textArea.getLineStartOffset(line);
		String text = textArea.getDocument().getText(start, offset-start);
		for (int i=0; i<text.length(); i++)
		{
			char c = text.charAt(i);
			if ((c != '\t')&&(c != ' ')) return false;
		}
		return true;
	}
	
	public void cannotEdit()
	{
		JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(textArea), "Editing the text is DISABLED!\nPlease enable editing!", "Error", JOptionPane.WARNING_MESSAGE);
	}
}
