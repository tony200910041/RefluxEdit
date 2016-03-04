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
import exec.*;
import myjava.gui.*;
import myjava.gui.MyToolBar.*;
import static exec.SourceManager.*;

public class GeneralTab extends OptionTab
{
	//toolbar mode:
	private JRadioButton isPanel = new MyRadioButton("Use panel", false);
	private JRadioButton isToolBar = new MyRadioButton("Use toolbar", false);
	private JRadioButton noContainer = new MyRadioButton("Hide panel/toolbar", false);
	private MyButtonGroup toolBarModeGroup = new MyButtonGroup(isPanel,isToolBar,noContainer);
	//menubar:
	private JCheckBox useNewMenuBar = new MyCheckBox("Use new colored menu bar", getBoolean0("frame.newMenuBar"));
	//narrow edge:
	private JCheckBox useNarrowEdge = new MyCheckBox("Use narrower edge", getBoolean0("frame.narrowerEdge"));
	//auto-indent:
	private JCheckBox useAutoIndent = new MyCheckBox("Use automatic indentation", textArea.getFilter().isAutoIndent());
	private JRadioButton useTab = new MyRadioButton("Tab", false);
	private JRadioButton useFourSpaces = new MyRadioButton("4 spaces", false);
	private MyButtonGroup indentModeGroup = new MyButtonGroup(useTab,useFourSpaces);
	//umbrella:
	private JCheckBox useUmbrella = new MyCheckBox("Show umbrella", MyUmbrellaLayerUI.isPaintUmbrella());
	private JSlider alphaYellow = new JSlider(0,255);
	//caret:
	private JCheckBox saveCaret = new MyCheckBox("Remember caret position", getBoolean0("caret.save"));
	private JButton manageCaret;
	//confirm drag:
	private JCheckBox confirmDrag = new MyCheckBox("Confirm drag", getBoolean0("textArea.confirmDrag"));
	//recent files:
	private JCheckBox rememberRecentFiles = new MyCheckBox("Remember recent files", getBoolean0("recentFiles.remember"));
	//constructor:
	public GeneralTab()
	{
		super(new GridLayout(8,1,0,0),"General");
		//toolbar mode:
		final MyButton customToolBar = new MyButton("Custom")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				JDialog dialog = new JDialog(SwingUtilities.windowForComponent(GeneralTab.this),"Toolbar options");
				dialog.setModal(true);
				dialog.getContentPane().setBackground(Color.WHITE);
				//
				final DefaultListModel<Indexable> toolbarListModel = new DefaultListModel<>();
				final JList<Indexable> toolbarList = new JList<>(toolbarListModel);
				final DefaultListModel<Indexable> unusedListModel = new DefaultListModel<>();
				final JList<Indexable> unusedList = new JList<>(unusedListModel);
				toolbarList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				toolbarList.setFont(f13);
				toolbarList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
				toolbarList.setVisibleRowCount(1);
				unusedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				unusedList.setFont(f13);
				unusedList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
				unusedList.setVisibleRowCount(1);
				DefaultListCellRenderer renderer = new DefaultListCellRenderer()
				{
					@Override
					public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
					{
						JLabel label = (JLabel)super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
						if (value instanceof Indexable)
						{
							Indexable i = (Indexable)value;
							label.setIcon(i.getIcon());
							label.setText(null);
							label.setToolTipText(i.toString());
							label.setHorizontalTextPosition(JLabel.CENTER);
							label.setHorizontalAlignment(JLabel.CENTER);
							label.setVerticalTextPosition(JLabel.BOTTOM);
							label.setVerticalAlignment(JLabel.BOTTOM);
						}
						return label;
					}
				};
				toolbarList.setCellRenderer(renderer);
				unusedList.setCellRenderer(renderer);
				//load to JLists
				Component[] buttons = MyToolBar.getInstance().getComponents();
				java.util.List<Indexable> toolbarButtons = new ArrayList<>();
				Set<MyToolBarButton> allButtons = new LinkedHashSet<>(MyToolBarButton.all());
				for (Component c: buttons)
				{
					toolbarButtons.add((Indexable)c);
					if (c instanceof MyToolBarButton)
					{
						allButtons.remove(c);
					}
				}
				for (Indexable c: toolbarButtons)
				{
					toolbarListModel.addElement(c);
				}
				for (MyToolBarButton c: allButtons)
				{
					unusedListModel.addElement(c);
				}
				//
				JPanel controlPanel = new MyPanel(MyPanel.CENTER);
				controlPanel.add(new MyButton(icon("LEFT_ARROW"))
				{
					{
						this.setToolTipText("Move to the left");
					}
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						int index = toolbarList.getSelectedIndex();
						if (index > 0)
						{
							toolbarListModel.add(index-1, toolbarListModel.remove(index));
							toolbarList.setSelectedIndex(index-1);
						}
					}
				});
				controlPanel.add(new MyButton(icon("RIGHT_ARROW"))
				{
					{
						this.setToolTipText("Move to the right");
					}
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						int index = toolbarList.getSelectedIndex();
						if ((index != -1)&&(index < toolbarListModel.getSize()-1))
						{
							toolbarListModel.add(index+1, toolbarListModel.remove(index));
							toolbarList.setSelectedIndex(index+1);
						}
					}
				});
				controlPanel.add(new MyButton("Add")
				{
					{
						if (isMetal) this.setPreferredSize(new Dimension(70,28));
					}
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						int index = unusedList.getSelectedIndex();
						if (index != -1)
						{
							toolbarListModel.addElement(unusedListModel.remove(index));
							toolbarList.setSelectedIndex(toolbarListModel.size()-1);
						}
					}
				});
				controlPanel.add(new MyButton("Remove")
				{
					{
						if (isMetal) this.setPreferredSize(new Dimension(70,28));
					}
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						int index = toolbarList.getSelectedIndex();
						if (index != -1)
						{
							Indexable c = toolbarListModel.remove(index);
							if (c instanceof MyToolBarButton)
							{
								unusedListModel.addElement(c);
							}
							toolbarList.setSelectedIndex(Math.max(0, index-1));
						}
					}
				});
				controlPanel.add(new MyButton("Separator")
				{
					{
						if (isMetal) this.setPreferredSize(new Dimension(70,28));
						this.setToolTipText("Add a new separator");
					}
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						toolbarListModel.addElement(new MyToolBar.MySeparator());
					}
				});
				JPanel center = new JPanel(new GridBagLayout());
				center.setOpaque(false);
				center.add(controlPanel);
				//
				dialog.setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();
				//
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 0;
				c.weighty = 1;
				c.insets = new Insets(2,2,2,2);
				dialog.add(new MyLabel("Toolbar:"), c);
				//
				c.gridx = 1;
				c.weightx = 1;
				c.fill = GridBagConstraints.BOTH;
				dialog.add(new JScrollPane(toolbarList), c);
				//
				c.gridx = 0;
				c.gridy = 1;
				c.weightx = 1;
				c.weighty = 0;
				c.gridwidth = 2;
				c.fill = GridBagConstraints.HORIZONTAL;
				dialog.add(controlPanel, c);
				//
				c.gridx = 0;
				c.gridy = 2;
				c.weightx = 0;
				c.weighty = 1;
				dialog.add(new MyLabel("Unused:"), c);
				//
				c.gridx = 1;
				c.weightx = 1;
				c.fill = GridBagConstraints.BOTH;
				dialog.add(new JScrollPane(unusedList), c);
				//
				dialog.setMinimumSize(new Dimension(450,0));
				dialog.pack();
				dialog.setLocationRelativeTo(GeneralTab.this);
				dialog.setVisible(true);
				//closed
				StringBuilder buttonCode = new StringBuilder();
				for (Object indexable: toolbarListModel.toArray())
				{
					buttonCode.append(((Indexable)indexable).getIndexString());
				}
				writeConfig("toolbar.Buttons", buttonCode.toString());
			}
		};
		if (isMetal)
		{
			customToolBar.setPreferredSize(new Dimension(60,28));
		}
		customToolBar.setToolTipText("Toolbar options");
		this.add(MyPanel.wrap(new MyLabel("Toolbar mode: "),isPanel,isToolBar,noContainer,customToolBar));
		String toolBarMode = getConfig0("frame.isPanel");
		if (toolBarMode != null)
		{
			switch (toolBarMode)
			{
				case "true": isPanel.setSelected(true); break;
				case "false": isToolBar.setSelected(true); break;
				case "no": default: noContainer.setSelected(true); break;
			}
		}
		else noContainer.setSelected(true);
		customToolBar.setEnabled(isToolBar.isSelected());
		ActionListener enableCustomListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				customToolBar.setEnabled(isToolBar.isSelected());
			}
		};
		isPanel.addActionListener(enableCustomListener);
		isToolBar.addActionListener(enableCustomListener);
		noContainer.addActionListener(enableCustomListener);
		//menubar:
		this.add(MyPanel.wrap(useNewMenuBar));
		useNewMenuBar.setToolTipText("For Metal Look and Feel only");
		//narrow edge:
		this.add(MyPanel.wrap(useNarrowEdge));
		//auto-indent:
		this.add(MyPanel.wrap(useAutoIndent,useTab,useFourSpaces));
		if (("    ").equals(MyIndentFilter.getIndentString()))
		{
			useFourSpaces.setSelected(true);
		}
		else
		{
			useTab.setSelected(true);
		}
		//umbrella:
		this.add(MyPanel.wrap(useUmbrella,new MyLabel("alpha value:"),alphaYellow));
		alphaYellow.setValue(MyUmbrellaLayerUI.getUmbrellaColor().getAlpha());
		alphaYellow.setBackground(Color.WHITE);
		alphaYellow.setFont(f13);
		alphaYellow.setPaintLabels(true);
		Dictionary<Integer, JLabel> dict = new Hashtable<>();
		dict.put(0,new MyLabel("0"));
		dict.put(60,new MyLabel("60"));
		dict.put(255,new MyLabel("255"));
		alphaYellow.setLabelTable(dict);
		useUmbrella.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				alphaYellow.setEnabled(useUmbrella.isSelected());
			}
		});
		alphaYellow.setEnabled(useUmbrella.isSelected());
		//caret
		manageCaret = new MyButton("Manage")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				final JDialog dialog = new JDialog(parent,"Manage caret position data",true);
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
				for (String name: keys())
				{
					if (name.startsWith("caret.")&&(!name.equals("caret.save")))
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
						int[] rows = table.getSelectedRows();
						if ((rows != null)&&(rows.length != 0))
						{
							int option = JOptionPane.showConfirmDialog(parent,"Remove caret data?", "Confirm", JOptionPane.YES_NO_OPTION);
							if (option == JOptionPane.YES_OPTION)
							{							
								ArrayList<String> paths = new ArrayList<>();
								for (int count=0; count<rows.length; count++)
								{
									paths.add(tam.getValueAt(rows[count],0).toString());
								}
								for (String path: paths)
								{
									removeConfig0("caret." + path);
								}
								for (int i=rows.length-1; i>=0; i--)
								{
									tam.removeRow(rows[i]);
								}
								saveConfig();
							}
						}
					}
				};
				if (isMetal) remove.setPreferredSize(new Dimension(65,28));
				bottom.add(remove);
				bottom.add(new MyButton("Clear")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						int option = JOptionPane.showConfirmDialog(parent,"Remove all caret data?", "Confirm", JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION)
						{
							for (Object n: propertyNames())
							{
								String name = n.toString();
								if ((name.startsWith("caret."))&&(!name.equals("caret.save")))
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
				dialog.setLocationRelativeTo(parent);
				dialog.setVisible(true);
				dialog.dispose();
			}
		};
		if (isMetal) manageCaret.setPreferredSize(new Dimension(65,28));
		manageCaret.setEnabled(saveCaret.isSelected());
		saveCaret.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				manageCaret.setEnabled(saveCaret.isSelected());
			}
		});
		this.add(MyPanel.wrap(saveCaret, manageCaret));
		//confirm drag:
		this.add(MyPanel.wrap(confirmDrag));
		//remember recent files:
		this.add(MyPanel.wrap(rememberRecentFiles, new MyButton("Clear")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				removeConfig0("recentFiles");
				saveConfig();
			}
		}));
	}
	
	@Override
	public void onExit()
	{
		//toolbar mode
		JComponent topComponent = null;
		JRadioButton selected = toolBarModeGroup.getSelected();
		if (isPanel == selected)
		{
			setConfig("frame.isPanel", "true");
			topComponent = FourButtonPanel.getInstance();
		}
		else if (isToolBar == selected)
		{
			setConfig("frame.isPanel", "false");
			MyToolBar toolbar = MyToolBar.getInstance();
			toolbar.loadButtons();
			topComponent = toolbar;
		}
		else if (noContainer == selected)
		{
			setConfig("frame.isPanel", "no");
			topComponent = null;
		}
		if (!RefluxEdit.isRibbon)
		{
			if (topPanel != null)
			{
				parent.remove(topPanel);
			}
			parent.setPageStartComponent(topComponent);
		}
		//menubar
		boolean _useNewMenuBar = useNewMenuBar.isSelected();
		setConfig("frame.newMenuBar", _useNewMenuBar + "");
		ColoredMenuBar menubar = ColoredMenuBar.getInstance();
		if (isMetal)
		{
			if (_useNewMenuBar)
			{
				menubar.setStyle(ColoredMenuBar.MODERN);
			}
			else
			{
				menubar.setStyle(ColoredMenuBar.BLUE);
			}
		}
		//narrow edge
		boolean _narrowEdge = useNarrowEdge.isSelected();
		setConfig("frame.narrowerEdge", _narrowEdge+"");
		Tab.setEdgeType(_narrowEdge?Edge.NARROW:Edge.WIDE);
		//auto-indent
		boolean _autoIndent = useAutoIndent.isSelected();
		setConfig("textArea.autoIndent", _autoIndent+"");
		String indentString = useTab.isSelected()?"\t":"    ";
		setConfig("textArea.autoIndentString", indentString);
		MyIndentFilter.setAutoIndent(_autoIndent);
		MyIndentFilter.setIndentString(indentString);
		//use umbrella
		//paint?
		boolean paintTextArea = useUmbrella.isSelected();
		setConfig("textArea.umbrella.show", paintTextArea+"");
		MyUmbrellaLayerUI.setPaintUmbrella(paintTextArea);
		//color
		int alpha = alphaYellow.getValue();
		setConfig("textArea.umbrella.alpha", alpha+"");
		MyUmbrellaLayerUI.setUmbrellaColor(new Color(251,231,51,alpha));
		//caret
		setConfig("caret.save", saveCaret.isSelected()+"");		
		//confirm drag
		setConfig("textArea.confirmDrag", confirmDrag.isSelected()+"");
		//remember recent files
		boolean remember = rememberRecentFiles.isSelected();
		setConfig("recentFiles.remember", remember+"");
		if (!remember)
		{
			removeConfig0("recentFiles");
		}
	}
}
