/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import exec.*;
import myjava.gui.syntax.*;
import myjava.util.*;

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
	private Parser parser;
	private BracketMatcher bracketMatcher;
	private String[] commentStarts;
	private String[] commentEnds;
	private List<String> commentDelimiters = new ArrayList<>();
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
	
	public void setParser(Parser parser)
	{
		this.parser = parser;
		if (parser != null)
		{
			commentDelimiters.clear();
			commentStarts = parser.getLanguage().getCommentStart();
			commentEnds = parser.getLanguage().getCommentEnd();
			Collections.addAll(commentDelimiters,commentStarts);
			Collections.addAll(commentDelimiters,commentEnds);
		}
	}
	
	public Parser getParser()
	{
		return this.parser;
	}
	
	public void updateBracketMatcher()
	{
		this.bracketMatcher = this.parser==null?null:new BracketMatcher(this.textArea,this.parser);
	}
	
	public BracketMatcher getBracketMatcher()
	{
		return this.bracketMatcher;
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
				parse(offset,0,string,fb);
			}
			else if (("}").equals(string)&&autoIndent)
			{
				super.insertString(fb, offset, string, attr);
				parse(offset,0,string,fb);
				AbstractDocument doc = (AbstractDocument)(fb.getDocument());
				int start = textArea.getLineStartOffset(textArea.getLineOfOffset(offset));
				String removedTab = removeOneTab(doc.getText(start,offset-start));
				doc.replace(start,offset-start,removedTab,attr);
				parse(start,offset-start,removedTab,fb);
			}
			else
			{
				super.insertString(fb, offset, string, attr);	
				parse(offset,0,string,fb);		
			}
		}
		else
		{
			cannotEdit();
		}
	}
	
	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException
	{
		AbstractDocument doc = (AbstractDocument)(fb.getDocument());
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
				parse(offset,length,string,fb);
			}
			else if (("}").equals(string)&&autoIndent)
			{
				super.replace(fb, offset, length, string, attr);
				parse(offset,length,string,fb);
				if (containsTabAndSpaceOnly(offset))
				{
					int start = textArea.getLineStartOffset(textArea.getLineOfOffset(offset));
					String removedTab = removeOneTab(doc.getText(start,offset-start));
					//doc.replace(start,offset-start,removedTab,attr);
					//parse(start,offset-start,removedTab,fb);
					this.replace(fb,start,offset-start,removedTab,attr);
				}
			}
			else
			{
				super.replace(fb, offset, length, string, attr);
				parse(offset,length,string,fb);
			}
		}
		else
		{
			cannotEdit();
		}
	}
	
	@Override
	public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException
	{
		Document doc = fb.getDocument();
		if (this.textArea.isEditable())
		{
			if (this.textArea.isAutoBackup())
			{
				this.textArea.getUndoManager().backup();
			}
			super.remove(fb,offset,length);
			parse(offset,length,"",fb);
		}
		else
		{
			cannotEdit();
		}
	}
	
	private void parse(int start, int oldLength, String newString, DocumentFilter.FilterBypass fb)
	{
		this.parse(start,oldLength,newString,fb.getDocument());
	}
	
	private synchronized void parse(int start, int oldLength, String newString, Document doc)
	{
		if ((parser != null)&&MyUmbrellaLayerUI.isSyntaxHighlightingEnabled())
		{
			try
			{
				/*
				 * we now want to replace a section of text so that "start" is at line start and "end" at line end
				 * called AFTER actual insertion/deletion/replacement
				 */			
				int realStart = textArea.getLineStartOffset(textArea.getLineOfOffset(start));
				int realEnd = textArea.getLineEndOffset(textArea.getLineOfOffset(start+newString.length()));
				/*
				 * now find out the smallest off and largest off+len, if comment regions are amended
				 */
				for (int i=start; i<=start+oldLength; i++)
				{
					Token commentToken = parser.getCommentAt(i);
					if (commentToken != null)
					{
						int commentOff = commentToken.off();
						int commentLength = commentToken.length();
						realStart = Math.min(realStart,commentOff);
						realEnd = Math.max(realEnd,commentOff+commentLength+newString.length()-oldLength);
					}
				}
				/*
				 * e.g. from ABCD to AXD
				 * oldLength=2; newString.length()=1
				 * realStart=0; realEnd=3
				 * 
				 * e.g. from ABC to AC
				 * oldLength=1; newString.length()=0
				 * realStart=0; realEnd=2
				 */
				final int[] par = new int[]{realStart,realEnd,oldLength,newString.length()};
				/*
				 * now replace:
				 */
				final javax.swing.Timer[] repaintTimer = new javax.swing.Timer[1];
				parser.addParseListener(new Parser.ParseListener()
				{
					@Override
					public void parseFinished()
					{
						repaintTimer[0].stop();
						MyIndentFilter.this.matchBracket();
					}
				});
				ActionListener actionListener = new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						MainPanel.getSelectedTab().getLayer().repaint();
					}
				};
				repaintTimer[0] = new javax.swing.Timer(1000,actionListener);
				parser.replaceOnNewThread(realStart,realEnd-realStart+oldLength-newString.length(),realEnd-realStart,new StringView(textArea.getText()));
				repaintTimer[0].start();
			}
			catch (BadLocationException ex)
			{
				throw new InternalError(ex.getMessage());
			}
		}
	}
	
	public void matchBracket()
	{
		if ((parser != null)&&(MyUmbrellaLayerUI.isBracketMatchingEnabled()))
		{
			this.bracketMatcher.matchBracketOnNewThread(MyIndentFilter.this.textArea.getCaretPosition());
			MainPanel.getSelectedTab().getLayer().repaint();
		}
	}
	
	public void reparse(Document doc)
	{
		try
		{
			int len = doc.getLength();
			this.parse(0,len,doc.getText(0,len),doc);
		}
		catch (BadLocationException ex)
		{
			//cannot happen
			throw new InternalError();
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
