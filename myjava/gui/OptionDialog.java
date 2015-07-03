/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;
import java.text.*;
import exec.*;
import myjava.util.*;
import myjava.gui.common.*;
import static exec.SourceManager.*;
import static myjava.gui.ExceptionDialog.*;

public class OptionDialog extends JDialog implements Resources
{
	/*
	 * predefined variables
	 */
	private static final boolean isWindows = Resources.LAF.contains("windows");
	private static final RefluxEdit w = RefluxEdit.getInstance();
	private static final JComponent topPanel = w.getPageStartComponent();
	private static final Tab tab = MainPanel.getSelectedTab();
	private static final MyTextArea textArea = tab.getTextArea();
	private static final File file = tab.getFile();
	private static final UndoManager undoManager = textArea.getUndoManager();
	private static String TMP1;
	
	/*
	 * for convenience
	 */	
	public static void showDialog(Frame parent)
	{
		final JDialog option = new JDialog(w, "Other options", true);
		option.getContentPane().setBackground(Color.WHITE);
		JTabbedPane tabbedPane = new JTabbedPane();
		loadConfig();
		//
		//tab1: general
		JPanel tab1 = new JPanel(new GridLayout(9,1,0,0));
		String isPanelString = getConfig0("isPanel");
		final MyRadioButton isPanel = new MyRadioButton("Use panel", false, 1);
		final MyRadioButton isToolBar = new MyRadioButton("Use toolbar", false, 2);
		final MyRadioButton NoContainer = new MyRadioButton("Hide panel/toolbar", false, 3);
		switch (isPanelString)
		{
			case "true":
			isPanel.setSelected(true);
			break;
			
			case "false":
			isToolBar.setSelected(true);
			break;
			
			case "no": default:
			NoContainer.setSelected(true);
			break;
		}
		ActionListener optlistener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				switch (((MyRadioButton)(ev.getSource())).getIndex())
				{
					case 1:
					isPanel.setSelected(true);
					isToolBar.setSelected(false);
					NoContainer.setSelected(false);
					break;
					
					case 2:
					isPanel.setSelected(false);
					isToolBar.setSelected(true);
					NoContainer.setSelected(false);
					break;
					
					case 3:
					isPanel.setSelected(false);
					isToolBar.setSelected(false);
					NoContainer.setSelected(true);
					break;
				}
			}
		};
		isPanel.addActionListener(optlistener);
		isToolBar.addActionListener(optlistener);
		NoContainer.addActionListener(optlistener);
		MyPanel P1 = new MyPanel(MyPanel.LEFT);
		P1.add(new MyLabel("Toolbar mode: "));
		P1.add(isPanel);
		P1.add(isToolBar);
		P1.add(NoContainer);
		tab1.add(P1);
		//
		String fontName = getConfig0("TextAreaFont.fontName");
		if (fontName == null)
		{
			fontName = "Microsoft Jhenghei";
		}
		int fontStyle, fontSize;
		try
		{
			fontStyle = Integer.parseInt(getConfig0("TextAreaFont.fontStyle"));
			if ((fontStyle<0)||(fontStyle>2)) fontStyle = 0;
		}
		catch (Exception ex)
		{
			fontStyle = 0;
		}
		try
		{
			fontSize = Integer.parseInt(getConfig0("TextAreaFont.fontSize"));
			if ((fontSize<1)||(fontSize>200)) fontSize = 13;
		}
		catch (Exception ex)
		{
			fontSize = 13;
		}
		MyPanel P2 = new MyPanel(MyPanel.LEFT);
		P2.add(new MyLabel("Text area font:"));
		MyFontChooser fontChooser = new MyFontChooser(new Font(fontName,fontStyle,fontSize));
		P2.add(fontChooser);
		tab1.add(P2);
		//
		MyPanel P3 = new MyPanel(MyPanel.LEFT);
		boolean isUseNewMenuBar = getBoolean0("isUseNewMenuBar");
		MyCheckBox useNewMenuBar = new MyCheckBox("Use new colored menu bar (for CrossPlatform Look and Feel only):", isUseNewMenuBar);
		P3.add(useNewMenuBar);
		tab1.add(P3);
		//
		MyPanel P4 = new MyPanel(MyPanel.LEFT);
		boolean narrowEdge = getBoolean0("isUseNarrowEdge");
		MyCheckBox useNarrowEdge = new MyCheckBox("Use narrower edge", narrowEdge);
		P4.add(useNarrowEdge);
		tab1.add(P4);	
		//
		MyPanel P5 = new MyPanel(MyPanel.LEFT);
		boolean showCount = getBoolean0("showCount");
		MyCheckBox useCount = new MyCheckBox("Show word and character count", showCount);
		P5.add(useCount);
		tab1.add(P5);
		//
		MyPanel P6 = new MyPanel(MyPanel.LEFT);
		MyCheckBox useIndent = new MyCheckBox("Use automatic indentation", textArea.getFilter().isAutoIndent());
		P6.add(useIndent);
		tab1.add(P6);
		//
		MyPanel P7 = new MyPanel(MyPanel.LEFT);
		MyCheckBox useUmbrella = new MyCheckBox("Show umbrella", MyUmbrellaLayerUI.isPaintUmbrella());
		P7.add(useUmbrella);
		JSlider alphaYellow = new JSlider(0,255);
		alphaYellow.setValue(MyUmbrellaLayerUI.getUmbrellaColor().getAlpha());
		alphaYellow.setBackground(Color.WHITE);
		alphaYellow.setFont(f13);
		alphaYellow.setPaintLabels(true);
		Dictionary<Integer, JLabel> dict = new Hashtable<>();
		dict.put(0,new MyLabel("0"));
		dict.put(60,new MyLabel("60"));
		dict.put(255,new MyLabel("255"));
		alphaYellow.setLabelTable(dict);
		P7.add(new MyLabel("alpha value:"));
		P7.add(alphaYellow);
		tab1.add(P7);
		//
		MyPanel P8 = new MyPanel(MyPanel.LEFT);
		boolean saveCaret = getBoolean0("Caret.save");
		final MyCheckBox saveCaretPosition = new MyCheckBox("Remember caret position", saveCaret);
		P8.add(saveCaretPosition);
		final MyButton manage = new MyButton("Manage")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				if (!option.isEnabled()) return;
				final JDialog dialog = new JDialog(w,"Manage caret position data",true);
				dialog.setLayout(new BorderLayout());
				dialog.getContentPane().setBackground(Color.WHITE);
				final DefaultTableModel tam = new DefaultTableModel()
				{
					@Override
					public boolean isCellEditable(int row, int column)
					{
						return false;
					}
				};
				final JTable table = new JTable(tam);
				table.setFont(f13);					
				table.setRowHeight(25);
				table.getTableHeader().setFont(f13);
				table.getTableHeader().setReorderingAllowed(false);
				table.setDragEnabled(false);
				table.setAutoCreateRowSorter(true);
				tam.addColumn("File name");
				tam.addColumn("Caret position");
				for (String name: propertyNames())
				{
					if (name.startsWith("Caret.")&&(!name.equals("Caret.save")))
					{
						tam.addRow(new String[]{name.substring(6,name.length()),getConfig0(name.toString())});
					}
				}
				dialog.add(new JScrollPane(table));
				MyPanel bottom = new MyPanel(MyPanel.CENTER);
				MyButton remove = new MyButton("Remove")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						int option = JOptionPane.showConfirmDialog(w,"Remove caret data?", "Confirm", JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION)
						{
							int[] rows = table.getSelectedRows();
							ArrayList<String> paths = new ArrayList<>();
							for (int count=0; count<rows.length; count++)
							{
								paths.add(tam.getValueAt(rows[count],0).toString());
							}
							for (String path: paths)
							{
								removeConfig0("Caret." + path);
							}
							for (int i=rows.length-1; i>=0; i--)
							{
								tam.removeRow(rows[i]);
							}
							saveConfig();
						}							
					}
				};
				if (!isWindows) remove.setPreferredSize(new Dimension(65,28));
				bottom.add(remove);
				bottom.add(new MyButton("Clear")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						int option = JOptionPane.showConfirmDialog(w,"Remove all caret data?", "Confirm", JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION)
						{
							for (Object n: propertyNames())
							{
								String name = n.toString();
								if ((name.startsWith("Caret."))&&(!name.equals("Caret.save")))
								{
									removeConfig0(name);
								}
							}
							tam.getDataVector().clear();
							tam.fireTableDataChanged();
							saveConfig();
						}							
					}
				});
				bottom.add(new MyButton("Done")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						dialog.setVisible(false);
						dialog.dispose();
					}
				});
				dialog.add(bottom, BorderLayout.PAGE_END);
				dialog.pack();
				dialog.setLocationRelativeTo(w);
				dialog.setVisible(true);
				dialog.dispose();
			}
		};
		manage.setEnabled(saveCaret);
		saveCaretPosition.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				manage.setEnabled(saveCaretPosition.isSelected());
			}
		});
		if (!isWindows) manage.setPreferredSize(new Dimension(65,25));
		P8.add(manage);
		tab1.add(P8);
		//
		MyPanel P9 = new MyPanel(MyPanel.LEFT);
		boolean confirmDrag = getBoolean("ConfirmDrag");
		MyCheckBox useConfirmDrag = new MyCheckBox("Confirm drag", confirmDrag);
		P9.add(useConfirmDrag);
		tab1.add(P9);
		//
		//tab2: line wrap
		MyPanel wrap = new MyPanel(MyPanel.CENTER);
		final MyCheckBox lineWrap = new MyCheckBox("Line Wrap", textArea.getLineWrap());
		final MyCheckBox wrapStyleWord = new MyCheckBox("Wrap by word", textArea.getWrapStyleWord());
		wrapStyleWord.setEnabled(lineWrap.isSelected());
		lineWrap.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				wrapStyleWord.setEnabled(lineWrap.isSelected());
			}
		});
		wrap.add(lineWrap);
		wrap.add(wrapStyleWord);
		//
		//tab3: encoding
		JPanel encoding_inner = new JPanel(new GridLayout(4,1,0,0));
		encoding_inner.setBackground(Color.WHITE);
		boolean _default1 = false;
		boolean _default2 = false;
		boolean _others = false;
		String encodingName = getConfig0("Encoding");
		if (encodingName == null)
		{
			encodingName = "default1";
		}
		switch (encodingName)
		{
			case "default1":
			_default1 = true;
			break;
			
			case "default2":
			_default2 = true;
			break;
			
			default:
			_others = true;
			break;
		}
		final MyRadioButton isDefault1 = new MyRadioButton("Use Java default 1 (PrintWriter)", _default1, 1);
		final MyRadioButton isDefault2 = new MyRadioButton("Use Java default 2 (FileOutputStream)", _default2, 2);
		final MyRadioButton others = new MyRadioButton("Use specified charset", _others, 3);
		encoding_inner.add(isDefault1);
		encoding_inner.add(isDefault2);
		encoding_inner.add(others);
		final JComboBox<String> comboBox = Tab.createCharsetComboBox();
		if (!encodingName.startsWith("default"))
		{
			comboBox.setSelectedItem(encodingName);
			comboBox.setEnabled(true);
		}
		else
		{
			comboBox.setEnabled(false);
		}
		encoding_inner.add(comboBox);
		//
		ActionListener enclistener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ev)
			{
				switch (((MyRadioButton)(ev.getSource())).getIndex())
				{
					case 1:
					isDefault1.setSelected(true);
					isDefault2.setSelected(false);
					others.setSelected(false);
					comboBox.setEnabled(false);
					break;
					
					case 2:
					isDefault1.setSelected(false);
					isDefault2.setSelected(true);
					others.setSelected(false);
					comboBox.setEnabled(false);
					break;
					
					case 3:
					isDefault1.setSelected(false);
					isDefault2.setSelected(false);
					others.setSelected(true);
					comboBox.setEnabled(true);
					break;
				}
			}
		};
		isDefault1.addActionListener(enclistener);
		isDefault2.addActionListener(enclistener);
		others.addActionListener(enclistener);
		MyPanel encoding = new MyPanel(MyPanel.CENTER);
		encoding.add(encoding_inner);
		//
		//tab4: line separator
		MyPanel sepPanel = new MyPanel(MyPanel.CENTER);
		final MyRadioButton sep_n = new MyRadioButton("\\n (Java default, Linux, Mac OS X)", false, 1);
		final MyRadioButton sep_r = new MyRadioButton("\\r (Mac OS 9)", false, 2);
		final MyRadioButton sep_nr = new MyRadioButton("\\r\\n (Windows, Symbian OS)", false, 3);
		String lineSeparatorString = getConfig0("lineSeparator");
		if (lineSeparatorString == null) lineSeparatorString = "\\n";
		switch (lineSeparatorString)
		{
			case "\\r":
			sep_r.setSelected(true);
			break;
			
			case "\\r\\n":
			sep_nr.setSelected(true);
			break;
			
			default:
			sep_n.setSelected(true);
			break;
		}
		ActionListener sepLis = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				switch (((MyRadioButton)(ev.getSource())).getIndex())
				{
					case 1:
					sep_n.setSelected(true);
					sep_r.setSelected(false);
					sep_nr.setSelected(false);
					break;
					
					case 2:
					sep_n.setSelected(false);
					sep_r.setSelected(true);
					sep_nr.setSelected(false);
					break;
					
					case 3:
					sep_n.setSelected(false);
					sep_r.setSelected(false);
					sep_nr.setSelected(true);
					break;
				}
			}
		};
		sep_n.addActionListener(sepLis);
		sep_r.addActionListener(sepLis);
		sep_nr.addActionListener(sepLis);
		sepPanel.add(sep_n);
		sepPanel.add(sep_r);
		sepPanel.add(sep_nr);
		//
		//tab5: filechooser
		JPanel chooserOption = new JPanel(new FlowLayout(FlowLayout.CENTER));
		chooserOption.setBackground(Color.WHITE);
		boolean Java = false;
		boolean System = false;
		String chooserStyle = getConfig0("ChooserStyle");
		if (chooserStyle == null)
		{
			chooserStyle = "Java";
		}
		switch (chooserStyle)
		{
			case "Java":
			default:
			Java = true;
			break;
			
			case "System":
			System = true;
			break;
		}
		final MyRadioButton isJava = new MyRadioButton("Use Java JFileChooser", Java, 1);
		final MyRadioButton isSystem = new MyRadioButton("Use system FileDialog", System, 2);
		ActionListener filclistener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ev)
			{
				switch (((MyRadioButton)(ev.getSource())).getIndex())
				{
					case 1:
					isJava.setSelected(true);
					isSystem.setSelected(false);
					break;
					
					case 2:
					isJava.setSelected(false);
					isSystem.setSelected(true);
					break;
				}
			}
		};
		isJava.addActionListener(filclistener);
		isSystem.addActionListener(filclistener);
		chooserOption.add(isJava);
		chooserOption.add(isSystem);
		//
		//tab6: tab size
		MyPanel tab6 = new MyPanel(MyPanel.CENTER);
		JSpinner spinnerTabSize = new JSpinner();
		spinnerTabSize.setModel(new SpinnerNumberModel(textArea.getTabSize(), 1, 50, 1));
		spinnerTabSize.setFont(f13);
		tab6.add(new MyLabel("Tab size: "));
		tab6.add(spinnerTabSize);
		//
		//tab7: selection color
		MyPanel selectionColor = new MyPanel(MyPanel.CENTER);
		MyColorChooser colorChooser = new MyColorChooser(MyColorChooser.LARGE);
		colorChooser.setColor(textArea.getSelectionColor());
		JPanel selectionColor_inner = new JPanel(new BorderLayout());
		selectionColor_inner.setBackground(Color.WHITE);
		selectionColor_inner.add(colorChooser, BorderLayout.CENTER);
		//
		MyPanel scP1 = new MyPanel(MyPanel.CENTER);
		scP1.add(new MyLabel("Default: (244, 223, 255)"));
		selectionColor_inner.add(scP1, BorderLayout.PAGE_END);
		//
		selectionColor.add(selectionColor_inner);
		//
		//tab8: LAF
		JPanel LAFOption_inner = new JPanel(new GridLayout(2,1,0,0));
		LAFOption_inner.setBackground(Color.WHITE);
		boolean DefaultL = false;
		boolean WindowsL = false;
		boolean Nimbus = false;
		String LAF = getConfig0("LAF");
		if (LAF == null)
		{
			LAF = "Default";
		}
		switch (LAF)
		{
			case "Default":
			DefaultL = true;
			break;
			
			case "System":
			WindowsL = true;
			break;
			
			case "Nimbus":
			Nimbus = true;
			break;
		}
		final MyRadioButton isDefaultL = new MyRadioButton("Use default Look and Feel", DefaultL, 1);
		final MyRadioButton isWindowsL = new MyRadioButton("Use Windows Look and Feel", WindowsL, 2);
		final MyRadioButton isNimbus = new MyRadioButton("Use Nimbus Look and Feel", Nimbus, 4);
		ActionListener LAFLis = new ActionListener()
		{
			public void actionPerformed(ActionEvent ev)
			{
				switch (((MyRadioButton)(ev.getSource())).getIndex())
				{
					case 1:
					isDefaultL.setSelected(true);
					isWindowsL.setSelected(false);
					isNimbus.setSelected(false);
					break;
					
					case 2:
					isDefaultL.setSelected(false);
					isWindowsL.setSelected(true);
					isNimbus.setSelected(false);
					break;
					
					case 4:
					isDefaultL.setSelected(false);
					isWindowsL.setSelected(false);
					isNimbus.setSelected(true);
					break;
				}
			}
		};
		isDefaultL.addActionListener(LAFLis);
		isWindowsL.addActionListener(LAFLis);
		isNimbus.addActionListener(LAFLis);
		MyCheckBox isRibbonBox = new MyCheckBox("Use Ribbon UI", RefluxEdit.isRibbon);
		MyPanel LAF_hori1 = new MyPanel(MyPanel.LEFT);
		LAF_hori1.add(isDefaultL);
		LAF_hori1.add(isWindowsL);
		LAF_hori1.add(isNimbus);
		MyPanel LAF_hori2 = new MyPanel(MyPanel.LEFT);
		LAF_hori2.add(isRibbonBox);
		LAFOption_inner.add(LAF_hori1);
		LAFOption_inner.add(LAF_hori2);
		MyPanel LAFOption = new MyPanel(MyPanel.CENTER);
		LAFOption.add(LAFOption_inner);
		//
		//tab9: compile command
		JPanel compilePanel_inner = new JPanel(new GridLayout(6,1,0,0));
		compilePanel_inner.setBackground(Color.WHITE);
		boolean isGlobal = getBoolean0("Compile.useGlobal");
		String command = null, runcommand = null, filename = null;
		if ((!isGlobal)&&(file != null))
		{
			//read from file
			String path = file.getPath();
			command = getConfig0("Compile.command."+path);
			runcommand = getConfig0("Compile.runCommand."+path);
			filename = getConfig0("Compile.runCommandFileName."+path);
		}
		command = command==null?getConfig0("Compile.command"):command;
		runcommand = runcommand==null?getConfig0("Compile.runCommand"):runcommand;
		filename = filename==null?getConfig0("Compile.runCommandFileName"):filename;
		//
		boolean isDeleteOld = getBoolean0("Compile.removeOriginal");
		String regex = getConfig0("Compile.regex");
		//
		MyPanel compileP0 = new MyPanel(MyPanel.LEFT);
		final MyTextField compiletf = new MyTextField(30,0);
		compiletf.setPreferredSize(new Dimension(compiletf.getSize().width,25));
		compiletf.setText(command);
		compileP0.add(new MyLabel("Compile command: "));
		compileP0.add(compiletf);
		//
		MyPanel compileP1 = new MyPanel(MyPanel.LEFT);
		final MyTextField runtf = new MyTextField(30,0);
		runtf.setPreferredSize(new Dimension(compiletf.getSize().width,25));
		runtf.setText(runcommand);
		compileP1.add(new MyLabel("Run command: "));
		compileP1.add(runtf);
		//
		MyPanel compileP2 = new MyPanel(MyPanel.LEFT);
		final MyTextField filetf = new MyTextField(30,0);
		filetf.setPreferredSize(new Dimension(compiletf.getSize().width,25));
		filetf.setText(filename);
		compileP2.add(new MyLabel("Command file name: "));
		compileP2.add(filetf);
		//
		MyPanel compileP3 = new MyPanel(MyPanel.LEFT);
		final MyCheckBox removeOld = new MyCheckBox("Remove old file", isDeleteOld);			
		final MyTextField removeRegexTF = new MyTextField(20,0);
		removeRegexTF.setText(regex);
		removeRegexTF.setEnabled(isDeleteOld);
		removeOld.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				removeRegexTF.setEnabled(removeOld.isSelected());
			}
		});
		compileP3.add(removeOld);
		compileP3.add(new MyLabel("Regex to match: "));
		compileP3.add(removeRegexTF);
		//
		MyPanel compileP4 = new MyPanel(MyPanel.LEFT);
		final MyCheckBox useGlobal = new MyCheckBox("Use global commands", isGlobal);
		compileP4.add(useGlobal);
		useGlobal.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				String s1 = null, s2 = null, s3 = null;
				if ((!useGlobal.isSelected())&&(file != null))
				{
					String path = file.getPath();
					s1 = getConfig0("Compile.command."+path);
					s2 = getConfig0("Compile.runCommand."+path);
					s3 = getConfig0("Compile.runCommandFileName."+path);						
				}
				//use global
				s1 = s1==null?getConfig0("Compile.command"):s1;
				s2 = s2==null?getConfig0("Compile.runCommand"):s2;
				s3 = s3==null?getConfig0("Compile.runCommandFileName"):s3;
				compiletf.setText(s1);
				runtf.setText(s2);
				filetf.setText(s3);
			}
		});
		//
		MyPanel compileP5 = new MyPanel(MyPanel.LEFT);
		compileP5.add(new MyLabel("Use %f for the file path, %p for the directory, %s for the simple name of the file and %n for a new line.     "));
		//
		compilePanel_inner.add(compileP0);
		compilePanel_inner.add(compileP1);
		compilePanel_inner.add(compileP2);
		compilePanel_inner.add(compileP3);
		compilePanel_inner.add(compileP4);
		compilePanel_inner.add(compileP5);
		MyPanel compilePanel = new MyPanel(MyPanel.CENTER);
		compilePanel.add(compilePanel_inner);
		//tab10: check update
		JPanel updatePanel = new MyPanel(MyPanel.LEFT);			
		MyCheckBox update = new MyCheckBox("Check update automatically", getBoolean0("CheckUpdate"));
		updatePanel.add(update);
		MyButton checkUpdateButton = new MyButton("Check now")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				(new Thread()
				{
					@Override
					public void run()
					{
						VersionChecker.showUpdateDialog(w,true);
					}
						
				}).start();
			}
		};
		if (!isWindows) checkUpdateButton.setPreferredSize(new Dimension(80,27));
		updatePanel.add(checkUpdateButton);
		//
		//tab11: compile command
		JPanel trayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		trayPanel.setBackground(Color.WHITE);
		final MyCheckBox isUseTray = new MyCheckBox("Use system tray", getBoolean0("useTray"));
		final MyCheckBox closeToTray = new MyCheckBox("Close to tray", getBoolean0("CloseToTray"));
		closeToTray.setEnabled(isUseTray.isSelected());
		isUseTray.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				closeToTray.setEnabled(isUseTray.isSelected());
			}
		});
		trayPanel.add(isUseTray);
		trayPanel.add(closeToTray);
		//
		//tab12: show hints
		JPanel hintPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		hintPanel.setBackground(Color.WHITE);
		MyCheckBox showHint = new MyCheckBox("Show hints on startup", getBoolean0("showHint"));
		hintPanel.add(showHint);
		MyButton hintPanelNow = new MyButton("Show now")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				HintDialog.showHintDialog(w);
			}
		};
		if (!isWindows) hintPanelNow.setPreferredSize(new Dimension(80,27));
		hintPanel.add(hintPanelNow);
		//
		//
		//restore toolbar to original location
		MyToolBar.getInstance().stopFloating();
		//
		tabbedPane.addTab("General",icon("OPTIONS16"),tab1,"General");
		tabbedPane.addTab("Wrapping",icon("LINEWRAP16"),wrap,"Line wrap");
		tabbedPane.addTab("Encoding",icon("ENCODING16"),encoding,"Encoding");
		tabbedPane.addTab("Line separator",icon("LINESEPARATOR16"),sepPanel,"Line separator");
		tabbedPane.addTab("File dialog",icon("FILECHOOSER16"),chooserOption,"File chooser");
		tabbedPane.addTab("Tab",icon("TABSIZE16"),tab6,"Tab size");
		tabbedPane.addTab("Selection color",icon("SELECTIONCOLOR16"),selectionColor,"Selection color");
		tabbedPane.addTab("Look and Feel",icon("LAF"),LAFOption,"Look and Feel");
		tabbedPane.addTab("Compile",icon("COMPILE16"),compilePanel,"Compile");
		tabbedPane.addTab("Update",icon("APPICON16"),updatePanel,"Check update");
		tabbedPane.addTab("System tray",icon("SYSTEMTRAY16"),trayPanel,"System tray");
		tabbedPane.addTab("Hints",icon("NEWJAVA16"),hintPanel,"Hints");
		tabbedPane.setFont(f13);
		option.setLayout(new BorderLayout());
		option.add(tabbedPane, BorderLayout.CENTER);
		option.pack();
		option.setLocationRelativeTo(parent);
		option.setVisible(true);
		/*
		 * dialog closed
		 */
		int edgeType, tabSize=4;
		boolean countWords, isEditable=true, isLineWrap, isWrapStyleWord, autoIndent;
		Color newSelectionColor;
		Font font;
		{
			//general
			//topPanel
			JComponent topComponent = null;
			if (isPanel.isSelected())
			{
				setConfig("isPanel", "true");
				topComponent = FourButtonPanel.getInstance();
			}
			else if (isToolBar.isSelected())
			{
				setConfig("isPanel", "false");
				topComponent = MyToolBar.getInstance();
			}
			else if (NoContainer.isSelected())
			{
				setConfig("isPanel", "no");
				topComponent = null;
			}
			if (!RefluxEdit.isRibbon)
			{
				if (topPanel != null)
				{
					w.remove(topPanel);
				}
				w.setPageStartComponent(topComponent);
			}
			//font
			try
			{
				fontChooser.getSpinner().commitEdit();
			}
			catch (ParseException ex)
			{
				exception(ex);
			}
			font = fontChooser.getFont();
			setConfig("TextAreaFont.fontName", font.getFontName());
			setConfig("TextAreaFont.fontStyle", font.getStyle() + "");
			setConfig("TextAreaFont.fontSize", font.getSize() + "");
			textArea.setFont(font);
			//menubar
			isUseNewMenuBar = useNewMenuBar.isSelected();
			setConfig("isUseNewMenuBar", isUseNewMenuBar + "");
			ColoredMenuBar menubar = ColoredMenuBar.getInstance();
			if (RefluxEdit.isCrossPlatformLAF)
			{
				if (isUseNewMenuBar)
				{
					menubar.setStyle(ColoredMenuBar.MODERN);
				}
				else
				{
					menubar.setStyle(ColoredMenuBar.BLUE);
				}
			}
			//narrowEdge
			narrowEdge = useNarrowEdge.isSelected();
			setConfig("isUseNarrowEdge", narrowEdge+"");
			edgeType = narrowEdge?Edge.NARROW:Edge.WIDE;			
			//word count panel
			countWords = useCount.isSelected();
			setConfig("showCount", countWords+"");
			//indentation
			autoIndent = useIndent.isSelected();
			setConfig("autoIndent", autoIndent+"");
			//use umbrella
			//paint?
			boolean paintTextArea = useUmbrella.isSelected();
			setConfig("showUmbrella", paintTextArea+"");
			MyUmbrellaLayerUI.setPaintUmbrella(paintTextArea);
			//color
			int alpha = alphaYellow.getValue();
			setConfig("Umbrella.alpha", alpha+"");
			MyUmbrellaLayerUI.setUmbrellaColor(new Color(251,231,51,alpha));
			//caret
			saveCaret = saveCaretPosition.isSelected();
			setConfig("Caret.save", saveCaret+"");
			//
			confirmDrag = useConfirmDrag.isSelected();
			setConfig("ConfirmDrag", confirmDrag+"");
		}
		{
			//line wrap
			isLineWrap = lineWrap.isSelected();
			isWrapStyleWord = wrapStyleWord.isSelected();
			setConfig("LineWrap", isLineWrap + "");
			setConfig("WrapStyleWord", isWrapStyleWord + "");
		}
		{
			//encoding
			String encodingChosen = null;
			if (isDefault1.isSelected())
			{
				encodingChosen = "default1";
			}
			else if (isDefault2.isSelected())
			{
				encodingChosen = "default2";
			}
			else //if (others.isSelected())
			{
				encodingChosen = comboBox.getSelectedItem().toString();
			}
			setConfig("Encoding", encodingChosen);
		}
		{
			//filechooser
			String csChosen;
			if (isJava.isSelected())
			{
				csChosen = "Java";
			}
			else
			{
				csChosen = "System";
			}
			setConfig("ChooserStyle", csChosen);
		}
		{
			//line separator
			String lineSeparator = "\\n";
			if (sep_r.isSelected()) lineSeparator = "\\r";
			if (sep_nr.isSelected()) lineSeparator = "\\r\\n";
			setConfig("lineSeparator", lineSeparator);
		}
		{
			//tab size
			try
			{
				spinnerTabSize.commitEdit();
				tabSize = Byte.parseByte(spinnerTabSize.getValue().toString());
			}
			catch (Exception ex)
			{
				tabSize = 4;
			}				
			finally
			{
				setConfig("TabSize", tabSize+"");
			}
		}
		{
			//selection color
			newSelectionColor = colorChooser.getColor();
			setConfig("SelectionColor.r", newSelectionColor.getRed()+"");
			setConfig("SelectionColor.g", newSelectionColor.getGreen()+"");
			setConfig("SelectionColor.b", newSelectionColor.getBlue()+"");
		}
		{
			//LAF
			if (isDefaultL.isSelected()) TMP1 = "Default";
			else if (isWindowsL.isSelected()) TMP1 = "System";
			else if (isNimbus.isSelected()) TMP1 = "Nimbus";
			setConfig("LAF", TMP1);
			boolean newIsRibbon = isRibbonBox.isSelected();
			setConfig("isRibbon", newIsRibbon+"");
			if (!LAF.equals(TMP1)||(newIsRibbon != RefluxEdit.isRibbon))
			{
				JOptionPane.showMessageDialog(w, "The Look and Feel will be changed after restart.", "Done", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		{
			//compile
			boolean global = useGlobal.isSelected();
			setConfig("Compile.removeOriginal", removeOld.isSelected()+"");
			setConfig("Compile.regex", removeRegexTF.getText());
			setConfig("Compile.useGlobal", global+"");
			if (global||(file==null))
			{
				setConfig("Compile.command", compiletf.getText());
				setConfig("Compile.runCommand", runtf.getText());
				setConfig("Compile.runCommandFileName", filetf.getText());					
			}
			else
			{					
				String path = file.getPath();
				setConfig("Compile.command."+path, compiletf.getText());
				setConfig("Compile.runCommand."+path, runtf.getText());
				setConfig("Compile.runCommandFileName."+path, filetf.getText());
			}
		}
		{
			//check update
			setConfig("CheckUpdate", update.isSelected()+"");
		}
		{
			//system tray
			boolean tray = isUseTray.isSelected();
			setConfig("useTray", tray+"");
			setConfig("CloseToTray", closeToTray.isSelected()+"");
			SystemTray systemTray = SystemTray.getSystemTray();
			TrayIcon[] icons = systemTray.getTrayIcons();
			if (tray)
			{
				if (icons.length == 0)
				{
					w.createTray();
				}
			}
			else
			{
				if (icons.length >= 1)
				{
					systemTray.remove(icons[0]); //only added one, or none
				}
			}
		}
		{
			//hint
			setConfig("showHint", showHint.isSelected()+"");
		}
		/*
		 * update:
		 */
		Tab.setGlobalProperties(edgeType, countWords, isEditable, isLineWrap, isWrapStyleWord, tabSize, newSelectionColor, font, autoIndent);
		MainPanel.updateAllTab();
		ClipboardDialog.getInstance().getTextArea().setSelectionColor(newSelectionColor);
		/*
		 * done 
		 */
		w.revalidate();
		w.repaint();
		saveConfig();
	}
	
	private OptionDialog()
	{
		throw new InternalError();
	}
}
