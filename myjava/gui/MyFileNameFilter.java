/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.io.*;
import myjava.util.*;

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
			if (dir.isDirectory()) return true;
			else for (String format: TextFileFormat.getFormatList())
			{
				if (file.endsWith("."+format)) return true;
			}
			return false;
			
			default: //FILTER_USER_DEFINED
			if (dir.isDirectory()) return true;
			else for (String format: filterFormat)
			{
				if (file.endsWith("."+format)) return true;
			}
			return false;
		}											
	}
}
