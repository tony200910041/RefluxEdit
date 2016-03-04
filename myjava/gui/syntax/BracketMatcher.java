/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.syntax;

import javax.swing.*;
import javax.swing.text.*;
import myjava.util.*;
import myjava.gui.*;
import myjava.gui.syntax.*;
import static myjava.gui.syntax.Token.Type.*;

public class BracketMatcher
{
	private JTextArea textArea;
	private Parser parser;
	private volatile StringView view;
	private volatile int off = -1;
	private volatile int effective = -1;
	private volatile int result = -1;
	private volatile boolean isMatching = false;
	public BracketMatcher(JTextArea textArea, Parser parser)
	{
		super();
		this.textArea = textArea;
		this.parser = parser;
	}
	
	public void matchBracketOnNewThread(final int newOff)
	{
		this.isMatching = true;
		final StringView newView = new StringView(this.textArea.getText());
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					synchronized(BracketMatcher.this)
					{
						BracketMatcher.this.off = newOff;
						BracketMatcher.this.view = newView;
						matchBracket0();
					}
				}
				catch (BadLocationException ex)
				{
					throw new InternalError(ex.getMessage());
				}
				finally
				{
					BracketMatcher.this.isMatching = false;
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							MainPanel.getSelectedTab().getLayer().repaint();
						}
					});
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	private void matchBracket0() throws BadLocationException
	{
		this.effective = getEffectiveBracketPosition();
		if (this.effective != -1)
		{
			Bracket bracket = Bracket.of(view.getText(effective,1).toString());
			Bracket opp = bracket.opposite();
			int same = 1;
			if (bracket.isOpen())
			{
				//find "forward"
				for (int i=this.effective+1; i<this.view.length(); i++)
				{
					if (isValid(i))
					{
						Bracket b = Bracket.of(view.getText(i,1));
						if (b != null)
						{
							if (b == bracket)
							{
								same++;
							}
							else if (b == opp)
							{
								same--;
							}
							if (same == 0)
							{
								this.result = i;
								return;
							}
						}
					}
				}
			}
			else //bracket is closed
			{
				//find "backward"
				for (int i=this.effective-1; i>=0; i--)
				{
					if (isValid(i))
					{
						Bracket b = Bracket.of(view.getText(i,1));
						if (b != null)
						{
							if (b == bracket)
							{
								same++;
							}
							else if (b == opp)
							{
								same--;
							}
							if (same == 0)
							{
								this.result = i;
								return;
							}
						}
					}
				}
			}
			this.result = -1;
			return;
		}
		else
		{
			this.reset();
		}
	}
	
	private int getEffectiveBracketPosition()
	{
		int rawPosition = getEffectiveBracketPosition0();
		if (rawPosition == -1) return -1;
		else return isValid(rawPosition)?rawPosition:(-1);
	}
	
	private int getEffectiveBracketPosition0()
	{
		int viewLength = this.view.length();
		if (viewLength != 0)
		{
			if (this.off == 0)
			{
				return Bracket.isBracket(view.getText(0,1))?0:-1;
			}
			else if (this.off == viewLength)
			{
				return Bracket.isBracket(view.getText(viewLength-1,1))?(viewLength-1):(-1);
			}
			else
			{
				if (Bracket.isBracket(view.getText(this.off-1,1)))
				{
					return this.off-1;
				}
				else if (Bracket.isBracket(view.getText(this.off,1)))
				{
					return this.off;
				}
				else return -1;
			}
		}
		else
		{
			return -1;
		}
	}
	
	private boolean isValid(int off)
	{
		return parser.getTokenAt(off,STRING,CHARACTER,SINGLE_LINE_COMMENT,MULTI_LINE_COMMENT,PREPROCESS) == null;
	}
	
	public void reset()
	{
		this.off = -1;
		this.result = -1;
	}
	
	public int getOff()
	{
		return this.off;
	}
	
	public int getEffectiveBracketLocation()
	{
		return this.effective;
	}
	
	public int getResultLocation()
	{
		return this.result;
	}
	
	public boolean isMatching()
	{
		return this.isMatching;
	}
}
