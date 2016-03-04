/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.statusbar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import myjava.gui.*;

public class FileComponent extends StatusComponent
{
	private static final String FILE_INDICATOR = "Current file: ";
	private JLabel fileLabel = new MyLabel();
	public FileComponent(Tab tab)
	{
		super(tab);
		this.setComponent(fileLabel);
		this.update();
		this.addComponentListener(new ResizeListener());
	}
	
	@Override
	public void update()
	{
		File file = this.tab.getFile();
		if (file != null)
		{
			String path = file.getPath();
			String shortened = shorten(path);
			if (path.startsWith(File.separator)&&(!shortened.startsWith(File.separator)))
			{
				shortened = File.separator + shortened;
			}
			fileLabel.setText(FILE_INDICATOR + shortened);
			fileLabel.setToolTipText(path);
		}
	}
	
	@Override
	public boolean fillHorizontal()
	{
		return true;
	}
	
	class ResizeListener extends ComponentAdapter
	{
		public ResizeListener()
		{
			super();
		}
		
		@Override
		public void componentResized(ComponentEvent ev)
		{
			FileComponent.this.update();
		}
	}
	
	private String shorten(String path)
	{
		int width = this.getWidth()-10; //-10 for spaces
		String[] pathElements = path.split(File.separator,0);
		if (pathElements.length > 0)
		{
			String first = pathElements[0];
			if (pathElements.length > 1)
			{
				if (first.isEmpty())
				{
					pathElements = Arrays.copyOfRange(pathElements,1,pathElements.length);
					if (pathElements.length != 0)
					{
						first = pathElements[0];
					}
					else
					{
						return path;
					}
				}
				String last = pathElements[pathElements.length-1];
				if (calculateWidth(FILE_INDICATOR+path) > width)
				{
					if (calculateWidth(FILE_INDICATOR+first+last) > width)
					{
						//force shorten
						if (calculateWidth(FILE_INDICATOR+File.separator+"..."+File.separator+last) > width)
						{
							for (int i=1; i<=last.length(); i++)
							{
								if (calculateWidth(FILE_INDICATOR+File.separator+"..."+File.separator+last.substring(i)) <= width)
								{
									return "..."+File.separator+last.substring(i);
								}
							}
							return "";
						}
						else
						{
							for (int i=first.length(); i>=1; i--)
							{
								if (calculateWidth(FILE_INDICATOR+File.separator+first.substring(0,i)+"..."+File.separator+last) <= width)
								{
									return first.substring(0,i)+"..."+File.separator+last;
								}
							}
							return "..."+File.separator+last;
						}
					}
					else
					{
						//add some part
						for (int i=pathElements.length-2; i>=0; i--)
						{
							if (calculateWidth(FILE_INDICATOR+concatPathElements(pathElements,0,i)+"..."+File.separator+last) <= width)
							{
								return concatPathElements(pathElements,0,i)+"..."+File.separator+last;
							}
						}
						return "..."+File.separator+last;
					}
				}
				else
				{
					return path;
				}
			}
			else
			{
				return path;
			}
		}
		return path;
	}
	
	private int calculateWidth(String s)
	{
		FontMetrics fontMetrics = this.fileLabel.getFontMetrics(this.fileLabel.getFont());
		return fontMetrics.stringWidth(s);
	}
	
	private String concatPathElements(String[] strs, int from, int to)
	{
		StringBuilder s = new StringBuilder();
		for (int i=from; i<=to; i++)
		{
			s.append(strs[i]+File.separator);
		}
		return s.toString();
	}
}
