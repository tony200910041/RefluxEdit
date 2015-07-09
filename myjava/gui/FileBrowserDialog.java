/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.tree.*;
import java.io.*;
import java.util.*;
import exec.*;
import myjava.util.*;
import myjava.gui.common.*;

public class FileBrowserDialog extends JDialog implements PopupMenuListener, ActionListener
{
	private static final FileBrowserDialog INSTANCE = new FileBrowserDialog();
	private DefaultComboBoxModel<Tab> comboBoxModel = new DefaultComboBoxModel<>();
	private JComboBox<Tab> openedBox = new JComboBox<>(comboBoxModel);
	private ComboBoxLayerUI comboBoxLayerUI = new ComboBoxLayerUI();
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode(RootFile.getInstance());
	private JTree tree = new JTree(root);
	//first launch: set location
	private boolean firstLaunch = true;
	@SuppressWarnings("unchecked")
	private FileBrowserDialog()
	{
		super(RefluxEdit.getInstance(), "File browser", false);
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(250,150));
		//
		JPanel top = new JPanel();
		top.setLayout(new GridLayout()); //one element->expand
		top.setBorder(new EmptyBorder(5,5,5,5));
		top.add(new JLayer<JComboBox>(openedBox,comboBoxLayerUI));
		openedBox.setBackground(Color.WHITE);
		openedBox.setRenderer(new BasicComboBoxRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				JLabel label = (JLabel)super.getListCellRendererComponent(list,value,index,isSelected, cellHasFocus);
				if (value instanceof Tab)
				{
					File file = ((Tab)value).getFile();
					if (file != null)
					{
						label.setText(file.getName());
					}
					else
					{
						label.setText("Untitled");
					}
				}
				return label;
			}
		});
		openedBox.addActionListener(this);
		openedBox.addPopupMenuListener(this);
		this.add(top, BorderLayout.PAGE_START);
		//
		tree.setCellRenderer(new TreeFileRenderer());
		tree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				if (ev.getClickCount() == 2)
				{
					DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode)(tree.getLastSelectedPathComponent());
					if (lastNode != null)
					{
						File selected = (File)(lastNode.getUserObject());
						if (selected != null)
						{
							if (selected.isDirectory())
							{
								lastNode.removeAllChildren();
								File[] files = selected.listFiles();
								if (files != null)
								{
									Arrays.sort(files, new FileComparator());
									for (File file: files)
									{
										DefaultMutableTreeNode added = new DefaultMutableTreeNode(file);
										lastNode.add(added);
									}
									((DefaultTreeModel)(tree.getModel())).reload(lastNode);
									tree.expandPath(tree.getSelectionPath());
								}
							}
							else if (selected.isFile())
							{
								boolean open;
								if (TextFileFormat.getFormatList().contains(StaticUtilities.getFileExtension(selected).toLowerCase()))
								{
									open = true;
								}
								else
								{
									int option = JOptionPane.showConfirmDialog(FileBrowserDialog.this, selected.getPath() + " does not look like a text file.\nWould you like to continue?", "Warning", JOptionPane.YES_NO_OPTION);
									open = (option == JOptionPane.YES_OPTION);
								}
								if (open)
								{
									for (Tab tab: MainPanel.getAllTab())
									{
										if (selected.equals(tab.getFile()))
										{
											MainPanel.setSelectedComponent(tab);
											return;
										}
									}
									Tab newTab = Tab.getNewTab();
									try
									{
										if (!MainPanel.getAllTab().contains(newTab))
										{
											MainPanel.getInstance().addTab(newTab);
										}
										newTab.openAndWait(selected);
									}
									catch (Exception ex)
									{
										ExceptionDialog.exception(ex);
									}
								}
							}
						}
					}
				}
			}
		});
		this.add(new JScrollPane(tree), BorderLayout.CENTER);
		//
		this.pack();
	}
	
	static class FileComparator implements Comparator<File>
	{
		FileComparator()
		{
			super();
		}
		
		@Override
		public int compare(File f1, File f2)
		{
			if (f1.isDirectory()&&f2.isFile())
			{
				return -1;
			}
			else if (f1.isFile()&&f2.isDirectory())
			{
				return 1;
			}
			else return f1.compareTo(f2);
		}
	}
	
	static class RootFile extends File
	{
		private static final RootFile INSTANCE = new RootFile();
		private RootFile()
		{
			super("");
		}
		
		@Override
		public File[] listFiles()
		{
			return File.listRoots();
		}
		
		@Override
		public String getName()
		{
			return "File system";
		}
		
		@Override
		public boolean isDirectory()
		{
			return true;
		}
		
		@Override
		public boolean isFile()
		{
			return false;
		}
		
		static RootFile getInstance()
		{
			return INSTANCE;
		}
	}
	
	static class TreeFileRenderer extends DefaultTreeCellRenderer
	{
		TreeFileRenderer()
		{
			super();
		}
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			JLabel label = (JLabel)super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);
			if (value instanceof DefaultMutableTreeNode)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
				File file = (File)(node.getUserObject());
				String name = file.getName();
				this.setText(name.isEmpty()?file.getPath():name);
				if (sel&&file.isDirectory())
				{
					this.setIcon((Icon)UIManager.get("Tree.openIcon"));
				}
				if (file.isFile())
				{
					Icon icon = (Icon)UIManager.get("Tree.leafIcon");
					this.setIcon(icon);
				}
				else if (file.isDirectory())
				{
					this.setIcon((Icon)UIManager.get("Tree.closedIcon"));
				}
			}
			return label;
		}
	}
	
	static class ComboBoxLayerUI extends LayerUI<JComboBox>
	{
		private static final String PAINT_STRING = "Select:";
		private boolean paint = true;
		ComboBoxLayerUI()
		{
			super();
		}
		
		@Override
		public void paint(Graphics g, JComponent c)
		{
			super.paint(g,c);
			if (paint)
			{
				Graphics2D g2d = (Graphics2D)(g.create());
				g2d.setFont(Resources.f13);
				FontMetrics metrics = g.getFontMetrics(Resources.f13);
				int stringHeight = metrics.getAscent()-metrics.getDescent();
				g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
				g2d.setColor(Color.BLACK);
				g2d.drawString(PAINT_STRING, 3, (c.getHeight()+stringHeight)/2);
				g2d.dispose();
			}
		}
		
		void setPaint(boolean paint)
		{
			this.paint = paint;
		}
	}
	
	DefaultComboBoxModel getComboBoxModel()
	{
		return this.comboBoxModel;
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		if (ev.getSource() instanceof JComboBox<?>)
		{
			if (comboBoxModel.getSize() != 1)
			{
				Tab selected = (Tab)(openedBox.getSelectedItem());
				if (selected != null)
				{
					MainPanel.setSelectedComponent(selected);
				}
			}
		}
	}
	
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent ev)
	{
		comboBoxModel.removeAllElements();
		for (Component tab: MainPanel.getInstance().getAllTab())
		{
			comboBoxModel.addElement((Tab)tab);
		}
		comboBoxLayerUI.setPaint(false);
	}
	
	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent ev)
	{
		comboBoxModel.removeAllElements();
		comboBoxLayerUI.setPaint(true);
	}
	
	@Override
	public void popupMenuCanceled(PopupMenuEvent ev)
	{
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		if (visible&&firstLaunch)
		{
			JFrame parent = RefluxEdit.getInstance();
			if (parent.getExtendedState() != JFrame.MAXIMIZED_BOTH)
			{
				Point p = parent.getLocation();
				int width = parent.getSize().width;
				this.setLocation(Math.min(p.x+width+5, Toolkit.getDefaultToolkit().getScreenSize().width-this.getWidth()), p.y);
			}
			firstLaunch = false;
		}
		super.setVisible(visible);
	}
	
	public static FileBrowserDialog getInstance()
	{
		return INSTANCE;
	}
}
