/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.*;
import java.util.regex.*;
import java.io.*;
import java.nio.file.*;
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
	 * private constructor
	 */
	private MyCompileToolBar()
	{
		super("Compile dialog");
		this.add(panel);
		JScrollPane scrollPane = new JScrollPane(compileList);
		scrollPane.setPreferredSize(new Dimension(440,130));
		panel.add(scrollPane, BorderLayout.CENTER);
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
				//only work for Java...
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
						currentTab = MainPanel.getSelectedTab();
						File currentFile = currentTab.getFile();
						if (currentFile != null)
						{
							boolean isGlobal = getBoolean0("Compile.useGlobal");
							String command = null;
							if ((!isGlobal)&&(currentFile != null))
							{
								command = getConfig0("Compile.runCommand."+currentFile.getPath());
							}
							else if (isGlobal)
							{
								command = getConfig0("Compile.runCommand");
							}
							if ((command == null)||command.isEmpty())
							{
								//still null, use default
								command = getRunCommand(getFileExtension(currentFile));
							}
							command = replaceCommand(command, currentFile);
							String cmdFileName = null;
							if ((!isGlobal)&&(currentFile!=null))
							{
								cmdFileName = getConfig0("Compile.runCommandFileName."+currentFile.getPath());
							}
							else if (isGlobal)
							{
								cmdFileName = getConfig0("Compile.runCommandFileName");
							}
							if ((cmdFileName == null)||cmdFileName.isEmpty())
							{
								cmdFileName = isMac?"run.command":"run.bat";
							}
							File cmdfile = new File(currentFile.getParent(),cmdFileName);
							boolean success = cmdfile.delete();
							try (PrintWriter writer = new PrintWriter(cmdfile,"UTF-8"))
							{
								writer.println(command);
							}
							catch (Exception ex)
							{							
							}
							try
							{
								Desktop.getDesktop().open(cmdfile);
							}
							catch (Exception ex)
							{
							}
						}
					}
				}).start();
			}
		};
		MyButton hide = new MyButton("Hide")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				MyCompileToolBar.this.setUI(new StopFloatingToolBarUI());
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
		File currentFile = currentTab.getFile();
		if (currentFile != null)
		{
			boolean isGlobal = getBoolean0("Compile.useGlobal");
			try
			{
				currentTab.save(currentFile,true);
				//delete old
				loadConfig();
				String regex = getConfig0("Compile.regex");
				File parent = currentFile.getParentFile();
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
				if ((!isGlobal)&&(currentFile!=null))
				{
					command = getConfig0("Compile.command."+currentFile.getPath());
				}
				else if (isGlobal)
				{
					command = getConfig0("Compile.command");
				}
				ProcessBuilder builder1;
				if ((command == null)||(command.isEmpty()))
				{
					//default command
					command = getCommand(getFileExtension(currentFile));
				}
				command = replaceCommand(command, currentFile);			
				if ((command != null)&&(!command.isEmpty()))
				{
					lm.addElement(">> Executing " + command);
					final Process proc = Runtime.getRuntime().exec(command,null,parent);
					final SwingWorker<Void, String> worker = new SwingWorker<Void, String>()
					{
						@Override
						protected Void doInBackground()
						{						
							//from error stream
							try (BufferedReader reader1 = new BufferedReader(new InputStreamReader(proc.getErrorStream())))
							{
								String buffer;
								while ((buffer = reader1.readLine()) != null)
								{
									this.publish(buffer);
								}
							}
							catch (Exception ex)
							{
							}
							//from input stream
							try (BufferedReader reader2 = new BufferedReader(new InputStreamReader(proc.getInputStream())))
							{
								String buffer;
								while ((buffer = reader2.readLine()) != null)
								{
									this.publish(buffer);
								}
							}
							catch (Exception ex)
							{
							}
							return null;
						}
						
						@Override
						protected void process(java.util.List<String> chunks)
						{
							for (String s: chunks)
							{
								lm.addElement(s);
							}
						}
						
						@Override
						protected void done()
						{
							lm.addElement("<html><font color=\"red\">---Process exited with exit code " + proc.exitValue() + "---</font></html>");
						}
					};
					worker.execute();
					//compiled successfully
					Thread thread = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							//wait for worker to return
							try
							{
								worker.get();
							}
							catch (Exception ex)
							{
								throw new InternalError();
							}
							//beep
							if (getBoolean0("Compile.end.beep"))
							{
								System.out.println("\007");
							}
						}
					});
					thread.start();
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
	
	private File makeNextOldDir(File f) throws IOException
	{
		int i=0;
		while (true)
		{
			i++;
			File _f = new File(f, "old_" + currentTab.getFile().getName() + "_backup_" + i);
			if (!_f.exists())
			{
				boolean success = _f.mkdir();
				if (success) return _f;
				else throw new IOException("cannot make dir: " + _f.getPath());
			}
		}
	}
	
	private void cut(File src, File dir)
	{
		try (FileInputStream input = new FileInputStream(src);
			 FileOutputStream output = new FileOutputStream(new File(dir,src.getName())))
		{
			byte[] buffer = new byte[4096];
			int read;
			while ((read = input.read(buffer))>0)
			{
				output.write(buffer,0,read);
			}
			boolean success = src.delete();
			if (!success) throw new IOException("cannot delete " + src.getPath());
		}
		catch (Exception ex)
		{
		}
	}
	
	private String replaceCommand(String command, File currentFile)
	{
		String currentParentPath = currentFile.getParent();
		String fileSeparator = FileSystems.getDefault().getSeparator();
		command = command.replace("%f", formatPath(currentFile.getPath()));
		command = command.replace("%p", formatPath(currentParentPath.endsWith(fileSeparator)?currentParentPath:(currentParentPath+fileSeparator)));
		command = command.replace("%s", currentFile.getName());
		command = command.replace("%a", getFileName(currentFile));
		return command.replace("%n", System.getProperty("line.separator"));
	}
	
	private String formatPath(String s)
	{
		String quoteOption = getConfig0("Compile.pathQuote");
		boolean escapeSpace = getBoolean0("Compile.escapeSpace");
		if (("curly").equals(quoteOption))
		{
			s = "\u201C" + s + "\u201D";
		}
		else if (("straight").equals(quoteOption))
		{
			s = "\"" + s + "\"";
		}
		if (escapeSpace)
		{
			s = s.replace(" ", "\\ ");
		}
		return s;
	}
}
