/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.option;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import myjava.gui.*;
import static exec.SourceManager.*;
import static myjava.util.StaticUtilities.*;

public class CompileTab extends OptionTab
{
	//textfields
	private JTextField compiletf = new MyTextField(30);
	private JTextField runtf = new MyTextField(30);
	private JTextField filetf = new MyTextField(30);
	//options
	private boolean _removeOld = getBoolean0("Compile.removeOriginal");
	private boolean _isGlobal = getBoolean0("Compile.useGlobal");
	private boolean _beep = getBoolean0("Compile.end.beep");
	private JCheckBox removeOld = new MyCheckBox("Remove old file", _removeOld);
	private JTextField removeRegexTF = new MyTextField(20);
	private JCheckBox useGlobal = new MyCheckBox("Use global commands", _isGlobal);
	private JCheckBox endBeep = new MyCheckBox("Emit a beep when compilation ends", _beep);
	//format path
	private String quoteOption = getConfig0("Compile.pathQuote");
	private JRadioButton curlyQuote = new MyRadioButton("Curly", ("curly").equals(quoteOption));
	private JRadioButton straightQuote = new MyRadioButton("Straight", ("straight").equals(quoteOption));
	private JRadioButton noQuote = new MyRadioButton("No",false);
	private MyButtonGroup quoteGroup = new MyButtonGroup(curlyQuote,straightQuote,noQuote);
	private JCheckBox escapeSpace = new MyCheckBox("Escape whitespaces", getBoolean0("Compile.escapeSpace"));
	public CompileTab()
	{
		super(new BorderLayout(),"Compilation");
		this.setBackground(Color.WHITE);
		//load command data
		reloadCommands();
		removeRegexTF.setText(getConfig0("Compile.regex"));
		//setup tooltip messages
		String toolTip = "<html>%f: file path<br>%p: directory<br>%s: simple name of file<br>%a: file name without extension<br>%n: new line.</html>";
		compiletf.setToolTipText(toolTip);
		runtf.setToolTipText(toolTip);
		//change global prefs
		useGlobal.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				reloadCommands();
			}
		});
		removeRegexTF.setEnabled(removeOld.isSelected());
		removeOld.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				removeRegexTF.setEnabled(removeOld.isSelected());
			}
		});
		//manage command button
		MyButton manageCommand = new MyButton("Manage")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				final DefaultTableModel tam = new DefaultTableModel();
				final JTable table = new JTable(tam)
				{
					@Override
					public boolean isCellEditable(int row, int column)
					{
						return column != 0;
					}
				};
				table.setAutoCreateRowSorter(true);
				table.setRowHeight(25);
				table.setFont(f13);
				table.getTableHeader().setFont(f13);
				table.getTableHeader().setReorderingAllowed(false);
				tam.addColumn("Key");
				tam.addColumn("Value");
				//
				Set<String> keys = keys();
				for (String key: keys)
				{
					if (key.startsWith("Compile.command.default."))
					{
						tam.addRow(new String[]{key, getConfig0(key)});
					}
				}
				for (String key: keys)
				{
					if (key.startsWith("Compile.runCommand.default."))
					{
						tam.addRow(new String[]{key, getConfig0(key)});
					}
				}
				//
				final JDialog dialog = new JDialog(parent,"Default command",true);
				dialog.setLayout(new BorderLayout());
				dialog.add(new JScrollPane(table), BorderLayout.CENTER);
				//
				JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				dialog.addWindowListener(new WindowAdapter()
				{
					@Override
					public void windowClosing(WindowEvent ev)
					{
						//discard change
						loadConfig();
					}
				});
				bottomPanel.add(new MyButton("Add")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						JPanel commandPanel = new JPanel(new GridBagLayout());
						GridBagConstraints c = new GridBagConstraints();
						//
						c.gridx = 0;
						c.gridy = 0;
						c.weightx = 0;
						c.insets = new Insets(3,3,3,3);
						commandPanel.add(new MyLabel("Extension:"), c);
						//
						c.gridx = 1;
						c.weightx = 1;
						c.gridwidth = 3;						
						c.fill = GridBagConstraints.HORIZONTAL;
						MyTextField extension = new MyTextField(20);
						commandPanel.add(extension, c);
						//
						c.gridx = 0;
						c.gridy = 1;
						c.weightx = 0;
						c.gridwidth = 1;
						c.fill = GridBagConstraints.NONE;
						commandPanel.add(new MyLabel("Command:"), c);
						//
						c.gridx = 1;
						c.gridwidth = 3;
						c.weightx = 1;
						c.fill = GridBagConstraints.HORIZONTAL;
						MyTextField command = new MyTextField(20);
						commandPanel.add(command, c);
						//
						c.gridx = 0;
						c.gridy = 2;
						c.weightx = 0;
						c.gridwidth = 1;
						c.fill = GridBagConstraints.NONE;
						commandPanel.add(new MyLabel("Run command:"), c);
						//
						c.gridx = 1;
						c.weightx = 1;
						c.gridwidth = 3;
						c.fill = GridBagConstraints.HORIZONTAL;
						MyTextField runCommand = new MyTextField(20);
						commandPanel.add(runCommand, c);
						//
						int option = JOptionPane.showConfirmDialog(parent, commandPanel, "Command", JOptionPane.OK_CANCEL_OPTION);
						if (option == JOptionPane.OK_OPTION)
						{
							String key = extension.getText();
							if (!key.isEmpty())
							{
								String commandKey = "Compile.command.default."+key;
								String runCommandKey = "Compile.runCommand.default."+key;
								//
								String commandText = command.getText();
								String runCommandText = runCommand.getText();
								if (!commandText.isEmpty())
								{
									if (keys().contains(commandKey))
									{
										int replace = JOptionPane.showConfirmDialog(dialog,"Replace command?","Confirm",JOptionPane.YES_NO_OPTION);
										if (replace == JOptionPane.YES_OPTION)
										{
											setConfig(commandKey, commandText);
											if (!runCommandText.isEmpty())
											{
												setConfig(runCommandKey, runCommandText);
												for (int i=0; i<tam.getRowCount(); i++)
												{
													String item = (String)(tam.getValueAt(i,0));
													if (item.equals(runCommandKey))
													{
														tam.removeRow(i);
														tam.addRow(new String[]{runCommandKey, runCommandText});
													}
												}
											}
											for (int i=0; i<tam.getRowCount(); i++)
											{
												String item = (String)(tam.getValueAt(i,0));
												if (item.equals(commandKey))
												{
													tam.removeRow(i);
													tam.addRow(new String[]{commandKey, commandText});
												}
											}
										}
									}
									else
									{
										setConfig(commandKey, commandText);
										tam.addRow(new String[]{commandKey, commandText});
										if (!runCommandText.isEmpty())
										{
											setConfig(runCommandKey, runCommandText);
											tam.addRow(new String[]{runCommandKey, runCommandText});											
										}
									}
								}
							}
						}
					}
				});
				MyButton removeCommand = new MyButton("Remove")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						int[] rows = table.getSelectedRows();
						if ((rows != null)&&(rows.length != 0))
						{
							int option = JOptionPane.showConfirmDialog(parent, "Remove commands?", "Confirm", JOptionPane.YES_NO_OPTION);
							if (option == JOptionPane.YES_OPTION)
							{							
								Set<String> _keySet = new HashSet<>();
								for (int count=0; count<rows.length; count++)
								{
									_keySet.add((String)(tam.getValueAt(rows[count],0)));
								}
								for (String _key: _keySet)
								{
									removeConfig0(_key);
								}
								for (int count=rows.length-1; count>=0; count--)
								{
									tam.removeRow(rows[count]);
								}
							}
						}
					}	
				};
				if (isMetal)
				{
					removeCommand.setPreferredSize(new Dimension(70,28));
				}
				bottomPanel.add(removeCommand);
				bottomPanel.add(new MyButton("Done")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						Vector<?> vect = tam.getDataVector();
						for (Object v: vect)
						{
							Vector<?> vector = (Vector<?>)v;
							setConfig((String)(vector.get(0)), (String)(vector.get(1))); //only two columns
						}
						dialog.setVisible(false);
						dialog.dispose();
						saveConfig();
					}
				});
				dialog.add(bottomPanel, BorderLayout.PAGE_END);
				//
				dialog.pack();
				dialog.setLocationRelativeTo(CompileTab.this);
				dialog.setVisible(true);
			}
		};
		if (isMetal)
		{
			manageCommand.setPreferredSize(new Dimension(70,28));
		}
		//build UI
		JPanel upper = new JPanel(new GridBagLayout());
		upper.setBackground(Color.WHITE);
		GridBagConstraints c = new GridBagConstraints();
		//
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5,5,5,5);
		upper.add(new MyLabel("Compile command:"), c);
		//
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		upper.add(compiletf, c);
		//
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		upper.add(new MyLabel("Run command:"), c);
		//
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		upper.add(runtf, c);
		//
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		upper.add(new MyLabel("Command file name:"), c);
		//
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		upper.add(filetf, c);
		//
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.insets = new Insets(0,0,0,0);
		upper.add(MyPanel.wrap(removeOld, removeRegexTF), c);
		//
		c.gridy = 4;
		upper.add(MyPanel.wrap(useGlobal), c);
		//
		c.gridy = 5;
		upper.add(MyPanel.wrap(manageCommand), c);
		//
		c.gridy = 6;
		upper.add(MyPanel.wrap(new MyLabel("Quotation: "), curlyQuote, straightQuote, noQuote), c);
		if (!(curlyQuote.isSelected()||straightQuote.isSelected()||noQuote.isSelected())) noQuote.setSelected(true);
		//
		c.gridy = 7;
		upper.add(MyPanel.wrap(escapeSpace), c);
		//
		c.gridy = 8;
		upper.add(MyPanel.wrap(endBeep), c);
		this.add(upper, BorderLayout.PAGE_START);
	}
	
	void reloadCommands()
	{
		String s1 = null, s2 = null, s3 = null;
		if ((!useGlobal.isSelected())&&(file != null))
		{
			//not use global
			String path = file.getPath();
			s1 = getConfig0("Compile.command."+path);
			s2 = getConfig0("Compile.runCommand."+path);
			s3 = getConfig0("Compile.runCommandFileName."+path);
		}
		else
		{
			//use global
			s1 = getConfig0("Compile.command");
			s2 = getConfig0("Compile.runCommand");
			s3 = getConfig0("Compile.runCommandFileName");
		}
		if (((s1 == null)||s1.isEmpty())&&(file != null))
		{
			String ext = getFileExtension(file);
			s1 = getCommand(ext);
		}
		if (((s2 == null)||s2.isEmpty())&&(file != null))
		{
			String ext = getFileExtension(file);
			s2 = getRunCommand(ext);
		}
		if (((s3 == null)||s3.isEmpty())&&(file != null))
		{
			s3 = "run.bat";
		}
		compiletf.setText(s1);
		runtf.setText(s2);
		filetf.setText(s3);
	}
	
	@Override
	public void onExit()
	{
		String commandText = compiletf.getText();
		String runText = runtf.getText();
		String fileNameText = filetf.getText();
		if (useGlobal.isSelected())
		{
			setConfig("Compile.command", commandText);
			setConfig("Compile.runCommand", runText);
			setConfig("Compile.runCommandFileName", fileNameText);
		}
		if (file != null)
		{
			String path = file.getPath();
			setConfig("Compile.command."+path, commandText);
			setConfig("Compile.runCommand."+path, runText);
			setConfig("Compile.runCommandFileName."+path, fileNameText);
		}
		setConfig("Compile.useGlobal", useGlobal.isSelected()+"");
		setConfig("Compile.removeOriginal", removeOld.isSelected()+"");
		setConfig("Compile.regex", removeRegexTF.getText());
		setConfig("Compile.pathQuote", curlyQuote.isSelected()?"curly":(straightQuote.isSelected()?"straight":"no"));
		setConfig("Compile.escapeSpace", escapeSpace.isSelected()+"");
		setConfig("Compile.end.beep", endBeep.isSelected()+"");
	}
}
