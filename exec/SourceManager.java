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
import myjava.util.*;

public final class SourceManager
{
	public static final String OS_NAME = System.getProperty("os.name").toLowerCase();
	public static final boolean IS_MAC = OS_NAME.contains("mac");
	public static final boolean IS_WINDOWS = OS_NAME.contains("windows");
	private static final File settingFile = new File(getSettingFilePath(), "REFLUXEDITPREF.PROPERTIES");
	private static final Properties prop = new Properties();
	private static final Map<String,String> OLD_TO_NEW = getKeyConversionMap();
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
				setConfig("frame.size.width", "560");
				setConfig("frame.size.height", "540");
				setConfig("frame.location.x", "10");
				setConfig("frame.location.y", "10");
				setConfig("textArea.isEditable", "true");
				setConfig("textArea.isLineWrap", "true");
				setConfig("textArea.isWrapStyleWord", "true");
				setConfig("output.encoding", "default1");
				setConfig("lookAndFeel.chooserStyle", IS_MAC?"System":"Java");
				setConfig("frame.onTop", "false");
				setConfig("textArea.tabSize", "4");
				setConfig("textArea.selectionColor", "-729089");
				setConfig("lookAndFeel.value", UIManager.getCrossPlatformLookAndFeelClassName());
				setConfig("frame.isPanel", "no");
				setConfig("toolbar.Buttons", "001002004038000007008012013015024");
				setConfig("textArea.font.name", "Microsoft Jhenghei");
				setConfig("textArea.font.style", "0");
				setConfig("textArea.font.size", "15");
				setConfig("frame.newMenuBar", "true");
				setConfig("frame.narrowerEdge", "true");
				setConfig("output.lineSeparator", "\\n");
				setConfig("frame.isRibbon", "true");
				setConfig("statusBar.showCount", "false");
				setConfig("statusBar.showCaretLocation", "true");
				setConfig("textArea.autoIndent", "true");
				setConfig("textArea.autoIndentString", "\t");
				setConfig("textArea.umbrella.show", "false");
				setConfig("textArea.umbrella.alpha", "60");
				setConfig("compile.runCommandFileName", "run"+(IS_MAC?".command":(IS_WINDOWS?".bat":"")));
				setConfig("compile.removeOriginalFiles", "false");
				setConfig("compile.regex", ".*\\.class");
				setConfig("compile.useGlobalCommand", "false");
				setConfig("compile.command.default.c", "gcc -o %a %f");
				setConfig("compile.command.default.cpp", "g++ -o %a %f");
				setConfig("compile.command.default.java", "javac -classpath %p %f");
				setConfig("compile.runCommand.default.c", "cd %p%n" + (IS_MAC?"open %a":"%a%nPAUSE%ndel \"%~f0\""));
				setConfig("compile.runCommand.default.cpp", "cd %p%n" + (IS_MAC?"open %a":"%a%nPAUSE%ndel \"%~f0\""));
				setConfig("compile.runCommand.default.java", "cd %p%njava -classpath %p %a" + (IS_MAC?"":"%nPAUSE%ndel \"%~f0\""));
				setConfig("compile.runCommand.default.pl", "cd %p%nperl %f" + (IS_MAC?"":"%nPAUSE%ndel \"%~f0\""));
				setConfig("compile.runCommand.default.plx", "cd %p%nperl %f" + (IS_MAC?"":"%nPAUSE%ndel \"%~f0\""));
				setConfig("compile.runCommand.default.py", "cd %p%npython %f" + (IS_MAC?"":"%nPAUSE%ndel \"%~f0\""));
				setConfig("compile.end.beep", "false");
				setConfig("compile.pathQuote", IS_MAC?"no":"straight");
				setConfig("compile.escapeSpace", IS_MAC+"");
				setConfig("caret.save", "true");
				setConfig("textArea.confirmDrag", "false");
				setConfig("systemTray.use", "true");
				setConfig("systemTray.closeToTray", "false");
				setConfig("hint.showAtStartUp", "false");
				setConfig("textArea.showLineCounter", "false");
				setConfig("recentFiles.remember", "true");
				setConfig("apple.laf.useScreenMenuBar", "true");
				setConfig("syntax.highlight", "true");
				setConfig("syntax.matchBrackets", "true");
				setConfig("syntax.selectedPainter", "Default");
				setConfig("painter.userDefined.Pastel", "1344303407:1352217344:-8847239:-155745:-6684775:-3355393:-11665:-11665:-13434829:-13434829:-5122092");
				saveConfig();
			}
			catch (IOException ex)
			{
			}
		}
		else
		{
			loadConfig();
			convert(prop);
		}
	}
	
	private static Map<String,String> getKeyConversionMap()
	{
		//from old to new
		Map<String,String> map = new TreeMap<>();
		map.put("Size.x", "frame.size.width");
		map.put("Size.y", "frame.size.height");
		map.put("Location.x", "frame.location.x");
		map.put("Location.y", "frame.location.y");
		map.put("isEditable", "textArea.isEditable");
		map.put("LineWrap", "textArea.isLineWrap");
		map.put("WrapStyleWord", "textArea.isWrapStyleWord");
		map.put("Encoding", "output.encoding");
		map.put("ChooserStyle", "lookAndFeel.chooserStyle");
		map.put("OnTop", "frame.onTop");
		map.put("TabSize", "textArea.tabSize");
		map.put("LAF", "lookAndFeel.value");
		map.put("isPanel", "frame.isPanel");
		map.put("ToolBar.buttons", "toolbar.Buttons");
		map.put("TextAreaFont.fontName", "textArea.font.name");
		map.put("TextAreaFont.fontStyle", "textArea.font.style");
		map.put("TextAreaFont.fontSize", "textArea.font.size");
		map.put("isUseNewMenuBar", "frame.newMenuBar");
		map.put("isUseNarrowEdge", "frame.narrowerEdge");
		map.put("lineSeparator", "output.lineSeparator");
		map.put("isRibbon", "frame.isRibbon");
		map.put("showCount", "statusBar.showCount");
		map.put("autoIndent", "textArea.autoIndent");
		map.put("autoIndentString", "textArea.autoIndentString");
		map.put("showUmbrella", "textArea.umbrella.show");
		map.put("Umbrella.alpha", "textArea.umbrella.alpha");
		map.put("Compile.runCommandFileName", "compile.runCommandFileName");
		map.put("Compile.removeOriginal", "compile.removeOriginalFiles");
		map.put("Compile.regex", "compile.regex");
		map.put("Compile.useGlobal", "compile.useGlobalCommand");
		map.put("Compile.command.default.c", "compile.command.default.c");
		map.put("Compile.command.default.cpp", "compile.command.default.cpp");
		map.put("Compile.command.default.java", "compile.command.default.java");
		map.put("Compile.runCommand.default.c", "compile.runCommand.default.c");
		map.put("Compile.runCommand.default.cpp", "compile.runCommand.default.cpp");
		map.put("Compile.runCommand.default.java", "compile.runCommand.default.java");
		map.put("Compile.runCommand.default.pl", "compile.runCommand.default.pl");
		map.put("Compile.runCommand.default.plx", "compile.runCommand.default.plx");
		map.put("Compile.runCommand.default.py", "compile.runCommand.default.py");
		map.put("Compile.end.beep", "compile.end.beep");
		map.put("Compile.pathQuote", "compile.pathQuote");
		map.put("Compile.escapeSpace", "compile.escapeSpace");
		map.put("Caret.save", "caret.save");
		map.put("ConfirmDrag", "textArea.confirmDrag");
		map.put("useTray", "systemTray.use");
		map.put("CloseToTray", "systemTray.closeToTray");
		map.put("showHint", "hint.showAtStartUp");
		map.put("showLineCounter", "textArea.showLineCounter");
		map.put("rememberRecentFiles", "recentFiles.remember");
		map.put("isMaxmized","frame.isMaxmized");
		return map;
	}
	
	public static void convert(Properties old)
	{
		boolean hasChanged = false;
		for (Map.Entry<Object,Object> entry: new HashSet<>(old.entrySet()))
		{
			String key = (String)(entry.getKey());
			String value = (String)(entry.getValue());
			if (OLD_TO_NEW.containsKey(key))
			{
				old.remove(key);
				old.setProperty(OLD_TO_NEW.get(key),value);
				hasChanged = true;
			}
			if (key.startsWith("Caret."))
			{
				old.remove(key);
				old.setProperty("caret."+key.substring(6),value);
				hasChanged = true;
			}
			else if (key.startsWith("Compile."))
			{
				old.remove(key);
				old.setProperty("compile."+key.substring(8),value);
				hasChanged = true;
			}
		}
		//special conversion
		try
		{
			if (old.containsKey("SelectionColor.r"))
			{
				int r = Integer.parseInt(old.getProperty("SelectionColor.r"));
				int g = Integer.parseInt(old.getProperty("SelectionColor.g"));
				int b = Integer.parseInt(old.getProperty("SelectionColor.b"));
				old.remove("SelectionColor.r");
				old.remove("SelectionColor.g");
				old.remove("SelectionColor.b");
				old.put("textArea.selectionColor",Integer.toString(new Color(r,g,b).getRGB()));
				hasChanged = true;
			}
		}
		catch (Exception ex)
		{
			//pass
		}
		if (old.containsKey("CheckUpdate"))
		{
			old.remove("CheckUpdate");
			hasChanged = true;
		}
		if (hasChanged)
		{
			saveConfig();
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
		if (name == null)
		{
			return null;
		}
		else if (prop.containsKey(name))
		{
			return prop.getProperty(name);
		}
		else if (OLD_TO_NEW.containsKey(name))
		{
			return prop.getProperty(OLD_TO_NEW.get(name));
		}
		else return null;
	}
	
	public static boolean getBoolean(String name)
	{
		loadConfig();
		return getBoolean0(name);
	}
	
	public static boolean getBoolean0(String str)
	{
		return ("true").equals(getConfig0(str));
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
		if (key != null)
		{
			if (OLD_TO_NEW.containsKey(key))
			{
				key = OLD_TO_NEW.get(key);
			}
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
	}
	
	public static void removeConfig0(String key)
	{
		if (OLD_TO_NEW.containsKey(key))
		{
			key = OLD_TO_NEW.get(key);
		}
		prop.remove(key);
		isUpToDate = false;
	}
	
	public static void writeConfig(String key, String value)
	{
		setConfig(key, value);
		saveConfig();
	}
	
	public static void setCaret(File f, int pos)
	{
		if (getBoolean0("caret.save")&&(f != null))
		{
			setConfig("caret."+f.getPath(), pos+"");
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
