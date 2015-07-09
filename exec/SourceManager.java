/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package exec;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.net.*;

public final class SourceManager
{
	private static final File settingFile = new File(getSettingFilePath(), "REFLUXEDITPREF.PROPERTIES");
	private static final Properties prop = new Properties();
	private static boolean isUpToDate = false;
	public static void initialize()
	{
		//size
		if (!settingFile.exists())
		{
			try
			{
				PrintWriter writer = new PrintWriter(settingFile, "UTF-8");
				writer.close();
				setConfig("Size.x", "550");
				setConfig("Size.y", "540");
				setConfig("Location.x", "10");
				setConfig("Location.y", "10");
				setConfig("isEditable", "true");
				setConfig("LineWrap", "true");
				setConfig("WrapStyleWord", "true");
				setConfig("Encoding", "default1");
				setConfig("ChooserStyle", "Java");
				setConfig("OnTop", "false");
				setConfig("TabSize", "4");
				setConfig("SelectionColor.r", "244");
				setConfig("SelectionColor.g", "223");
				setConfig("SelectionColor.b", "255");
				setConfig("LAF", "Default");
				setConfig("isPanel", "no");
				setConfig("ToolBar.new", "true");
				setConfig("ToolBar.open", "true");
				setConfig("ToolBar.save", "true");
				setConfig("ToolBar.export", "false");
				setConfig("ToolBar.print", "true");
				setConfig("ToolBar.undo", "true");
				setConfig("ToolBar.redo", "false");
				setConfig("ToolBar.cut", "true");
				setConfig("ToolBar.copy", "true");
				setConfig("ToolBar.paste", "true");
				setConfig("ToolBar.selectAll", "true");
				setConfig("ToolBar.selectAllAndCopy", "false");
				setConfig("ToolBar.delete", "false");
				setConfig("ToolBar.search", "true");
				setConfig("TextAreaFont.fontName", "Microsoft Jhenghei");
				setConfig("TextAreaFont.fontStyle", "0");
				setConfig("TextAreaFont.fontSize", "15");
				setConfig("isUseNewMenuBar", "true");
				setConfig("isUseNarrowEdge", "true");
				setConfig("lineSeparator", "\\n");
				setConfig("isRibbon", "true");
				setConfig("showCount", "false");
				setConfig("autoIndent", "true");
				setConfig("showUmbrella", "false");
				setConfig("Umbrella.alpha", "60");
				setConfig("Compile.command", "javac -classpath \"%p\" %f");
				setConfig("Compile.runCommand", "cd %p%njava -classpath %p %a%nPAUSE%ndel \"%~f0\"");
				setConfig("Compile.runCommandFileName","run.bat");
				setConfig("Compile.removeOriginal", "false");
				setConfig("Compile.regex", ".*\\.class");
				setConfig("Compile.useGlobal", "false");
				setConfig("Compile.command.default.c", "gcc -o %a %f");
				setConfig("Compile.command.default.cpp", "g++ -o %a %f");
				setConfig("Compile.command.default.java", "javac -classpath %p %f");
				setConfig("Compile.runCommand.default.c", "cd %p%n%a%nPAUSE%ndel \"%~f0\"");
				setConfig("Compile.runCommand.default.cpp", "cd %p%n%a%nPAUSE%ndel \"%~f0\"");
				setConfig("Compile.runCommand.default.java", "cd %p%njava -classpath %p %a%nPAUSE%ndel \"%~f0\"");
				setConfig("Compile.runCommand.default.pl", "cd %p%nperl %f%nPAUSE%ndel \"%~f0\"");
				setConfig("Compile.runCommand.default.plx", "cd %p%nperl %f%nPAUSE%ndel \"%~f0\"");
				setConfig("Compile.runCommand.default.py", "cd %p%npython %f%nPAUSE%ndel \"%~f0\"");
				setConfig("Caret.save", "true");
				setConfig("CheckUpdate", "true");
				setConfig("ConfirmDrag", "false");
				setConfig("useTray", "true");
				setConfig("CloseToTray", "false");
				setConfig("showHint", "false");
				setConfig("showLineCounter", "false");
				saveConfig();
			}
			catch (IOException ex)
			{
			}
		}
		else
		{
			loadConfig();
		}
	}
	
	public static URL get(String url)
	{
		return SourceManager.class.getResource(url);
	}
	
	public static ImageIcon icon(String name)
	{
		try
		{
			return new ImageIcon(get("/SRC/" + name + ".PNG"));
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	public static ImageIcon createQuestionMessageIcon()
	{
		try
		{
			return icon("QUESTION_ICON48");
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	public static String getSettingFilePath()
	{
		try
		{			
			return (new File(SourceManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getPath();
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	/*
	 * Preferences:
	 */
	public static boolean isUpToDate()
	{
		return SourceManager.isUpToDate;
	}
	
	public static void loadConfig()
	{
		try
		{
			if (!isUpToDate)
			{
				prop.load(new BufferedInputStream(new FileInputStream(settingFile)));
				isUpToDate = true;
			}
		}
		catch (Exception ex)
		{
		}
	}
	
	public static String getConfig(String name)
	{
		loadConfig();
		return getConfig0(name);
	}
	
	public static String getConfig0(String name)
	{
		return prop.getProperty(name);
	}
	
	public static boolean getBoolean(String name)
	{
		loadConfig();
		return getBoolean0(name);
	}
	
	public static boolean getBoolean0(String str)
	{
		return ("true").equals(prop.getProperty(str));
	}
		
	public static void saveConfig()
	{
		try
		{
			prop.store(new BufferedOutputStream(new FileOutputStream(settingFile)), null);
			isUpToDate = true;
		}
		catch (Exception ex)
		{
		}
	}
	
	public static void setConfig(String key, String value)
	{
		if (value != null)
		{
			prop.setProperty(key, value);
		}
		else
		{
			prop.remove(key);
		}
		isUpToDate = false;
	}
	
	public static void removeConfig0(String key)
	{
		prop.remove(key);
		isUpToDate = false;
	}
	
	public static void writeConfig(String key, String value)
	{
		prop.setProperty(key, value);
		saveConfig();
	}
	
	public static void setCaret(File f, int pos)
	{
		if (getBoolean0("Caret.save")&&(f != null))
		{
			setConfig("Caret."+f.getPath(), pos+"");
			isUpToDate = false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> propertyNames()
	{
		return (ArrayList<String>)Collections.list(prop.propertyNames());
	}
	
	public static Set<String> keys()
	{
		return prop.stringPropertyNames();
	}
}
