/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.statusbar;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.*;
import myjava.gui.*;
import myjava.util.*;

public class CountComponent extends StatusComponent
{
	private boolean isWordCount = true;
	private JLabel countLabel = new MyLabel();
	private volatile WordCounter counter = null;
	private final Object lock = new Object();
	private int cachedWordCount = -1;
	private int cachedCharCount = -1;
	public CountComponent(Tab tab)
	{
		super(tab);
		this.add(countLabel);
		this.update();
	}
	
	@Override
	public void update()
	{
		JTextArea textArea = this.tab.getTextArea();
		String selectedText = textArea.getSelectedText();
		final String text = selectedText==null?(textArea.getText()):selectedText;
		if (this.isWordCount)
		{
			synchronized(lock)
			{
				if (counter != null)
				{
					counter.stop();
				}
				counter = new WordCounter(text);
			}
			Thread thread = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					final int result = counter.count();
					if (result != -1)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								CountComponent.this.cachedWordCount = result;
								countLabel.setText(result+" word"+(result==1?"":"s"));
							}
						});
					}
				}
			});
			thread.start();
		}
		else
		{
			this.cachedCharCount = text.replace("\n","").length();
			countLabel.setText(cachedCharCount+" character"+(cachedCharCount==1?"":"s"));
		}
	}
	
	@Override
	public void postCaretUpdate()
	{
		this.cachedWordCount = -1;
		this.cachedCharCount = -1;
		this.update();
	}
	
	@Override
	public void postDocumentUpdate()
	{
		this.cachedWordCount = -1;
		this.cachedCharCount = -1;
		this.update();
	}
	
	@Override
	public void mouseReleased(MouseEvent ev)
	{
		if (isWordCount&&(cachedCharCount != -1))
		{
			countLabel.setText(cachedCharCount+" character"+(cachedCharCount==1?"":"s"));
			isWordCount = false;
		}
		else if ((!isWordCount)&&(cachedWordCount != -1))
		{
			countLabel.setText(cachedWordCount+" word"+(cachedWordCount==1?"":"s"));
			isWordCount = true;
		}
		else
		{
			isWordCount = !isWordCount;
			this.update();
		}
	}
}
