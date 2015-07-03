/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import myjava.io.*;

public class MainPanel extends JPanel
{	
	private static final ArrayList<Tab> tabList = new ArrayList<>();
	private MyCompileToolBar compileDialog = MyCompileToolBar.getInstance();
	private JTabbedPane tabbedPane = new JTabbedPane();
	private static final MainPanel INSTANCE = new MainPanel();
	private MainPanel()
	{
		super(new BorderLayout());
		this.add(tabbedPane, BorderLayout.CENTER);
		Tab tab = Tab.getNewTab();
		this.addTab(tab);
		this.updateTabName(tab);
		this.tabbedPane.setFocusable(false);
	}
	
	public static MainPanel getInstance()
	{
		return INSTANCE;
	}
	
	public static Tab getSelectedTab()
	{
		return (Tab)(INSTANCE.tabbedPane.getSelectedComponent());
	}
	
	public static ArrayList<Tab> getAllTab()
	{
		return tabList;
	}
	
	public static void updateAllTab()
	{
		for (Tab tab: getAllTab())
		{
			tab.update();
		}
	}
	
	public static void add(Tab tab)
	{
		//convenient method
		MainPanel.getInstance().addTab(tab);
	}
	
	public void addTab(Tab tab)
	{
		File file = tab.getFile();
		this.tabbedPane.addTab(null, null, tab, null);
		//
		tabList.add(tab);
	}
	
	public void updateTabName(Tab tab)
	{
		File file = tab.getFile();
		tab.getTabLabel().setText(file==null?"Untitled":file.getName());
		int index = tabbedPane.indexOfComponent(tab);
		tabbedPane.setTabComponentAt(index, tab.getTabPanel());
		String path = file==null?"Untitled":file.getPath();
		tabbedPane.setToolTipTextAt(index, path);
		tab.getTabPanel().setToolTipText(path);
	}
	
	public static void setSelectedComponent(Tab tab)
	{
		MainPanel.getInstance().tabbedPane.setSelectedComponent(tab);
	}
	
	public JTabbedPane getTabbedPane()
	{
		return this.tabbedPane;
	}
	
	public void removeTab(Tab tab)
	{
		if (tabbedPane.getTabCount() == 1)
		{
			Tab _tab = new Tab();
			this.addTab(_tab);
			this.updateTabName(_tab);
		}
		tabbedPane.remove(tab);
		FileWatcher watcher = tab.getFileWatcher();
		if (watcher != null)
		{
			watcher.close();
		}
		tabList.remove(tab);
	}
	
	public static void close(Tab tab)
	{
		//convenient method
		MainPanel.getInstance().removeTab(tab);
	}
}
