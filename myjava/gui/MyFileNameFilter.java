/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.io.*;

public class MyFileNameFilter implements FilenameFilter
{
	private String[] filterFormat;
	MyFileNameFilter(String... filterFormat)
	{
		super();
		this.filterFormat = filterFormat;
	}
	
	@Override
	public boolean accept(File dir, String name)
	{
		dir = new File(dir,name);
		String file = name.toLowerCase();
		switch (filterFormat.length)
		{
			case 0: //FILTER_TEXTFILE
			return (dir.isDirectory())||(file.endsWith("txt"))||(file.endsWith("java"))||(file.endsWith("py"))||(file.endsWith("php"))||(file.endsWith("html"))||(file.endsWith("htm"))||(file.endsWith("xml"))||(file.endsWith("properties")||(file.endsWith("c"))||(file.endsWith("cpp")));
			
			default: //FILTER_USER_DEFINED
			if (dir.isDirectory()) return true;
			else for (String format: filterFormat)
			{
				if (file.endsWith(format)) return true;
			}
			return false;
		}											
	}
}
