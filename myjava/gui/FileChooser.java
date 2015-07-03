package myjava.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.util.*;
import static exec.SourceManager.*;

public final class FileChooser
{
	//defaults
	private static final MyWhiteFileChooser JavaChooser = MyWhiteFileChooser.getInstance();
	private static FileDialog systemChooser;
	private static final FileNameExtensionFilter textFilter = new FileNameExtensionFilter("Text file", new String[]{"txt", "java", "py", "php", "html", "htm", "xml", "properties", "c", "cpp"});
	//
	public static final int OPEN = 0;
	public static final int SAVE = 1;
	//
	public static File showPreferredFileDialog(Frame parent, int mode, String... filterFormat)
	{
		File f = null;
		boolean save = false;
		// file: globar variable
		String chooserStyleName = getConfig("ChooserStyle");
		if (chooserStyleName == null)
		{
			chooserStyleName = "Java";
		}
		if (chooserStyleName.equals("Java"))	
		{	
			outdo1:
			do
			{
				JavaChooser.resetChoosableFileFilters();
				if (filterFormat.length == 0)
				{
					JavaChooser.addChoosableFileFilter(textFilter);
				}
				else
				{
					JavaChooser.addChoosableFileFilter(new FileNameExtensionFilter(Arrays.toString(filterFormat), filterFormat));
				}
				int option;
				if (mode == OPEN)
				{
					option = JavaChooser.showOpenDialog(parent);
				}
				else if (mode == SAVE)
				{
					option = JavaChooser.showSaveDialog(parent);
				}
				else
				{
					throw new IllegalArgumentException("can only be FileChooser.OPEN or FileChooser.SAVE");
				}
				if (option == JFileChooser.APPROVE_OPTION)
				{
					f = JavaChooser.getSelectedFile();
					String path = f.getPath();
					if (!path.contains("."))
					{
						if (filterFormat.length == 0)
						{
							f = new File(path+".txt");
						}
						else
						{
							f = new File(path+"."+filterFormat[0]);
						}
					}
					if (f.exists()&&(mode==FileChooser.SAVE))
					{
						save = (isOverride(parent) == JOptionPane.YES_OPTION);
					}
					else break outdo1;
				}
				else return null; //cancelled
			} while (!save);
		}
		else if (chooserStyleName.equals("System"))
		{
			MyFileNameFilter filter = new MyFileNameFilter(filterFormat);
			systemChooser = new FileDialog(parent, "Choose:");
			outdo2:
			do
			{
				systemChooser.setFilenameFilter(filter);
				if (mode == OPEN)
				{
					systemChooser.setMode(FileDialog.LOAD);
				}
				else if (mode == SAVE)
				{
					systemChooser.setMode(FileDialog.SAVE);
				}
				else
				{
					throw new IllegalArgumentException("can only be FileChooser.OPEN or FileChooser.SAVE");
				}
				systemChooser.setVisible(true);
				String child = systemChooser.getFile();				
				if (child != null)
				{
					if ((!child.toLowerCase().endsWith(".txt"))&&(!child.contains(".")))
					{
						if (filterFormat.length == 0)
						{
							f = new File(child + ".txt");
						}
						else
						{
							f = new File(child + "."+filterFormat[0]);
						}
					}
					f = new File(systemChooser.getDirectory(), child);	
					if (f.exists()&&(mode==FileChooser.SAVE))
					{
						save = (isOverride(parent) == JOptionPane.YES_OPTION);
					}
					else break outdo2;
				}
				else return null;
			} while (!save);
		}
		return f;
	}
	
	public static int isOverride(Frame parent)
	{
		return JOptionPane.showConfirmDialog(parent, "Override old file?", "Warning", JOptionPane.WARNING_MESSAGE);
	}
}
