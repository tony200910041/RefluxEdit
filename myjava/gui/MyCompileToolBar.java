package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.*;
import java.io.*;
import java.util.regex.*;
import myjava.gui.common.*;
import static exec.SourceManager.*;
import static myjava.gui.ExceptionDialog.*;
import static myjava.util.StaticUtilities.*;

public class MyCompileToolBar extends JToolBar implements Resources
{
	private static final MyCompileToolBar INSTANCE = new MyCompileToolBar();
	private DefaultListModel<String> lm = new DefaultListModel<>();
	private JList<String> compileList = new JList<>(lm);
	private JPanel panel = new JPanel(new BorderLayout());
	/*
	 * currentTab: will be assigned each click
	 */
	private Tab currentTab;
	/*
	 * 
	 */
	MyToolBarUI toolBarUI = new MyToolBarUI();
	/*
	 * private constructor
	 */
	private MyCompileToolBar()
	{
		super("Compile dialog");
		this.add(panel);
		panel.add(new JScrollPane(compileList), BorderLayout.CENTER);
		JPanel bottom = new JPanel(new BorderLayout(10,10));
		panel.add(bottom, BorderLayout.LINE_END);
		/*
		 * set compileList:
		 */
		compileList.setCellRenderer(new DefaultListCellRenderer()
		{
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				JLabel label = (JLabel)(super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus));
				String str = value.toString();
				switch (stringType(str))
				{
					case 0:
					label.setForeground(Color.RED);
					break;
					
					case 1:
					default:
					label.setForeground(Color.BLACK);
					break;
				}
				return label;
			}
			
			private int stringType(String value)
			{
				if (value.startsWith(currentTab.getFile().getPath()))
				{
					return 0;
				}
				else return 1;
			}
		});
		compileList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent ev)
			{
				String value = (String)(compileList.getSelectedValue());
				int line = lineno(value);
				File file = getFile(value);
				if ((file != null)&&(line >= 0))
				{
					try
					{
						int start = currentTab.getTextArea().getLineStartOffset(line-1);
						int end = currentTab.getTextArea().getLineEndOffset(line-1);
						for (Tab tab: MainPanel.getAllTab())
						{
							if (file.equals(tab.getFile()))
							{
								MainPanel.setSelectedComponent(tab);
								tab.getTextArea().select(start, end);
								tab.getTextArea().requestFocus();
							}
						}
					}
					catch (BadLocationException ex)
					{
						exception(ex);
					}
				}
			}
			
			private File getFile(String value)
			{
				try
				{
					Matcher matcher = Pattern.compile(":([0-9]+):").matcher(value);
					if (matcher.find())
					{
						String s = value.substring(0,matcher.start());
						File file = new File(s);
						if (file.exists())
						{
							return file;
						}
						else
						{
							return null;
						}
					}
					else return null;
				}
				catch (Exception ex)
				{
					return null;
				}
			}
		});
		MyButton run = new MyButton("Run")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				(new Thread()
				{
					@Override
					public void run()
					{
						loadConfig();
						boolean isGlobal = getBoolean0("Compile.useGlobal");
						String command = null;
						if ((!isGlobal)&&(currentTab.getFile() != null))
						{
							command = getConfig0("Compile.runCommand."+currentTab.getFile().getPath());
						}
						command = (command==null)?getConfig0("Compile.runCommand"):command;
						if (command == null)
						{
							command = "java -classpath " + currentTab.getFile().getParent() + " " + getFileName(currentTab.getFile()) + "\nPAUSE";
						}
						else
						{
							command = replaceExpressions(command);
						}
						String cmdFileName = null;
						if ((!isGlobal)&&(currentTab.getFile() != null))
						{
							cmdFileName = getConfig0("Compile.runCommandFileName."+currentTab.getFile().getPath());
						}
						cmdFileName = (cmdFileName==null)?getConfig0("Compile.runCommandFileName"):cmdFileName;
						if (cmdFileName == null)
						{
							cmdFileName = "CMD.BAT";
						}
						else
						{
							command = replaceExpressions(command);
						}
						File cmdfile = new File(currentTab.getFile().getParent(),cmdFileName);
						try
						{
							PrintWriter writer = new PrintWriter(cmdfile,"UTF-8");
							writer.write(command);
							writer.close();
							Desktop.getDesktop().open(cmdfile);
						}
						catch (Exception ex)
						{
						}
					}
					
					String replaceExpressions(String command)
					{
						return command.replace("%f", currentTab.getFile().getPath()).replace("%p", currentTab.getFile().getParent()).replace("%n",System.getProperty("line.separator")).replace("%s",getFileName(currentTab.getFile()));
					}
				}).start();
			}
		};
		MyButton hide = new MyButton("Hide")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				MyCompileToolBar.this.toolBarUI.stopFloating();
				MyCompileToolBar.this.updateUI();
				MainPanel.getInstance().remove(MyCompileToolBar.this);
			}
		};
		JPanel p0 = new JPanel();
		p0.setLayout(new GridLayout(2,1,5,15));
		p0.add(run);
		p0.add(hide);
		bottom.add(p0);
		compileList.setFont(f13);
		compileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public static MyCompileToolBar getInstance()
	{
		return INSTANCE;
	}
	
	public void startCompile()
	{
		MainPanel main = MainPanel.getInstance();
		currentTab = MainPanel.getSelectedTab();
		if (!main.isAncestorOf(this))
		{
			main.add(this, BorderLayout.PAGE_END);
			main.revalidate();
			main.repaint();
		}
		lm.removeAllElements();
		if (currentTab.getFile() != null)
		{
			boolean isGlobal = getBoolean0("Compile.useGlobal");
			try
			{
				currentTab.save(currentTab.getFile(),true);
				//delete old
				loadConfig();
				String regex = getConfig0("Compile.regex");
				File parent = currentTab.getFile().getParentFile();
				File dir = null;
				if (getBoolean0("Compile.removeOriginal"))
				{								
					File[] files = parent.listFiles();
					for (File _f: files)
					{
						if (_f.getPath().matches(regex))
						{
							if (dir == null)
							{
								dir = makeNextOldDir(parent);
							}
							cut(_f,dir);
						}
					}
				}
				//start running
				String command = null;
				if ((!isGlobal)||(currentTab.getFile()!=null))
				{
					command = getConfig0("Compile.command."+currentTab.getFile().getPath());
				}
				command = command==null?getConfig0("Compile.command"):command;
				ProcessBuilder builder1;
				if (command == null)
				{
					command = "javac -classpath " + currentTab.getFile().getParent() + " " + currentTab.getFile().getPath();
				}
				else
				{
					command = command.replace("%f", currentTab.getFile().getPath()).replace("%p", currentTab.getFile().getParent());
				}
				Process proc = Runtime.getRuntime().exec(command,null,parent);
				//from error stream
				try
				{
					BufferedReader reader1 = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
					String buffer;
					while ((buffer = reader1.readLine()) != null)
					{
						lm.addElement(buffer);
					}
					reader1.close();
				}
				catch (Exception ex)
				{
				}
				//from input stream
				try
				{
					BufferedReader reader2 = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					String buffer;
					while ((buffer = reader2.readLine()) != null)
					{
						lm.addElement(buffer);
					}
					reader2.close();
					//compiled successfully
					if (lm.size() == 0)
					{
						lm.addElement("The file " + currentTab.getFile().getPath() + " is compiled successfully.");
					}
				}
				catch (Exception ex)
				{
				}
			}
			catch (Exception ex)
			{
				exception(ex);
			}
		}
	}
	
	private int lineno(String value)
	{
		try
		{
			value = value.substring(currentTab.getFile().getPath().length()+1, value.length());
			return Integer.parseInt(value.substring(0,value.indexOf(":")));
		}
		catch (Exception ex)
		{
			return -1;
		}
	}
	
	private File makeNextOldDir(File f)
	{
		int i=0;
		while (true)
		{
			i++;
			File _f = new File(f, "old_currentTab.getFile()_backup_" + i);
			if (!_f.exists())
			{
				_f.mkdir();
				return _f;
			}
		}
	}
	
	private void cut(File src, File dir)
	{
		try
		{
			FileInputStream input = new FileInputStream(src);
			FileOutputStream output = new FileOutputStream(new File(dir,src.getName()));
			byte[] buffer = new byte[4096];
			int read;
			while ((read = input.read(buffer))>0)
			{
				output.write(buffer,0,read);
			}
			input.close();
			output.close();
			src.delete();
		}
		catch (Exception ex)
		{
		}
	}
}
