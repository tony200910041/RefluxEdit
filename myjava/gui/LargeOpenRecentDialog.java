/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;
import myjava.gui.*;
import myjava.gui.common.*;
import exec.*;
import static exec.SourceManager.*;

public class LargeOpenRecentDialog extends JDialog implements Resources, ColorConstants
{
	//open buttons
	private OpenButton open = new OpenButton("Open file","OPEN80",2);
	private OpenButton quick = new OpenButton("Quick",null,3);
	private OpenButton charset = new OpenButton("Charset",null,51);
	private OpenButton cancel = new OpenButton("Cancel",null,0);
	//recent files
	private DefaultListModel<File> recentListModel = new DefaultListModel<>();
	private JList<File> recentList = new JList<>(recentListModel);
	private LargeOpenRecentDialog(Frame parent)
	{
		//open window in ribbon UI: open(2), open_quick(3), open_charset(51)
		super(parent,"",true);
		this.setUndecorated(true);
		this.setLayout(new GridBagLayout());
		this.getRootPane().setBorder(raisedBorder);
		this.getContentPane().setBackground(Color.WHITE);
		GridBagConstraints c = new GridBagConstraints();
		//left: open buttons
		JPanel leftIn = new JPanel(new GridLayout(2,1,5,5));
		leftIn.setOpaque(false);
		leftIn.add(MyPanel.wrap(MyPanel.CENTER,open));
		leftIn.add(MyPanel.wrap(MyPanel.CENTER,quick,charset));
		//
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.insets = new Insets(7,5,5,5);
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(leftIn, c);
		//right: recent files
		JPanel right = new JPanel(new BorderLayout());
		right.setBackground(Color.WHITE);
		JLabel recentLabel = new JLabel("Recent files");
		recentLabel.setFont(f13.deriveFont(23f));
		right.add(recentLabel, BorderLayout.PAGE_START);
		right.add(new JScrollPane(recentList), BorderLayout.CENTER);
		right.add(MyPanel.wrap(MyPanel.RIGHT, new MyButton("Clear")
		{
			{
				if (isMetal) this.setPreferredSize(new Dimension(60,28));
			}
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				loadConfig();
				removeConfig0("recentFiles");
				saveConfig();
				recentListModel.removeAllElements();
			}
		}, cancel), BorderLayout.PAGE_END);
		//setup list
		loadFiles();
		recentList.setCellRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				JLabel label = (JLabel)super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
				if (value instanceof File)
				{
					File file = (File)value;
					if (file.isFile())
					{
						label.setText("<html><font size=\"4\"><b>" + file.getName() + "</b></font><br>" + file.getParent() + "</html>");
					}
					else
					{
						label.setText("Unknown file: " + file.getPath());
					}
				}
				return label;
			}
		});
		recentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		recentList.setFont(f13);
		recentList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				if (ev.getClickCount() == 2)
				{
					File selected = recentList.getSelectedValue();
					LargeOpenRecentDialog.this.dispose();
					MainPanel.openFile(selected);
				}
			}
		});
		//
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0,5,5,5);
		c.fill = GridBagConstraints.BOTH;
		this.add(right, c);
	}
	
	void loadFiles()
	{
		String recentFiles = getConfig("recentFiles");
		if (recentFiles != null)
		{
			ArrayList<String> files = new ArrayList<>(10);
			Collections.addAll(files, recentFiles.split("\n"));
			int count = 0;
			for (String s: files)
			{
				File file = new File(s);
				if (file.exists())
				{
					count++;
					recentListModel.addElement(file);
					if (count == 10) break;
				}
			}
		}		
	}
	
	public static void showOpenDialog(Frame parent)
	{
		LargeOpenRecentDialog dialog = new LargeOpenRecentDialog(parent);
		dialog.setMinimumSize(new Dimension(450,510));
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		//
		GrayGlassPane.getInstance().setTransparent(false);
		dialog.setVisible(true);
	}
	
	class OpenButton extends MyButton
	{
		private int x;
		OpenButton(String text, String icon, int x)
		{
			super(text);
			this.x = x;
			/*
			 * set icon if exists
			 */
			if (icon != null)
			{
				if (isNimbus) this.setBorder(null);
				this.setPreferredSize(new Dimension(110,110));
				this.setVerticalTextPosition(SwingConstants.BOTTOM);
				this.setHorizontalTextPosition(SwingConstants.CENTER);
				this.setIcon(SourceManager.icon(icon));
			}
			else if (isMetal)
			{
				this.setPreferredSize(new Dimension(60,28));
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent ev)
		{
			GrayGlassPane.getInstance().setTransparent(true);
			LargeOpenRecentDialog.this.dispose();
			if (x >= 1)
			{				
				(new MyListener(x)).actionPerformed(ev);
			}
		}
	}
}
