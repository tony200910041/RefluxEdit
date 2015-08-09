/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.util;

import java.io.*;
import java.util.*;
import exec.*;

public final class TextFileFormat
{
	public static final String[] TEXT_FILE = new String[]{"txt", "properties", "xml", "ini", "reg", "mf", "log"};
	public static final String[] PROGRAMMING = new String[]{"java", "c", "cpp", "h", "m", "pl", "plx", "pm", "py", "rb", "f90", "f95", "f03", "f", "for", "lua", "groovy", "js", "bat", "cmd", "nsh", "cs", "pas", "hs", "lhs", "ahk"};
	public static final String[] NETWORK = new String[]{"htm", "html", "php", "css"};
	public static final Set<String> PRE_DEFINED = new TreeSet<>();
	public static final Set<String> USER_DEFINED = new TreeSet<>();
	static
	{
		//pre-defined
		Collections.addAll(PRE_DEFINED, TEXT_FILE);
		Collections.addAll(PRE_DEFINED, PROGRAMMING);
		Collections.addAll(PRE_DEFINED, NETWORK);
		reloadUserDefinedFormat();
	}
	
	public static String[] getFormats()
	{
		return getFormatList().toArray(new String[0]);
	}
	
	public static Set<String> getFormatList()
	{
		Set<String> formats = new TreeSet<>(PRE_DEFINED);
		formats.addAll(USER_DEFINED);
		return formats;
	}
	
	public static FileFilter getFormatFilter()
	{
		return new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				if (file.isDirectory())
				{
					return true;
				}
				else
				{
					Set<String> formats = getFormatList();
					String path = file.getPath().toLowerCase();
					for (String format: formats)
					{
						if (path.endsWith("."+format)) return true;
					}
					return false;
				}
			}
		};
	}
	
	public static void reloadUserDefinedFormat()
	{
		//user-defined
		String definedFormat = SourceManager.getConfig("userDefinedTextFormats");
		if ((definedFormat != null)&&(!definedFormat.isEmpty()))
		{
			String[] extensions = definedFormat.split(" ",0);
			for (String ext: extensions)
			{
				USER_DEFINED.add(ext.toLowerCase());
			}
		}
	}
	
	public static String getUserFormatString()
	{
		StringBuilder builder = new StringBuilder();
		for (String ext: USER_DEFINED)
		{
			builder.append(ext+" ");
		}
		return builder.toString().trim();
	}
	
	private TextFileFormat()
	{
		throw new InternalError();
	}
}
