/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import exec.*;
import myjava.io.*;

public class MainPanel extends JPanel
{
	private static final MainPanel INSTANCE = new MainPanel();
	private MyCompileToolBar compileDialog = MyCompileToolBar.getInstance();
	private JTabbedPane tabbedPane = new JTabbedPane();	
	private MainPanel()
	{
		super(new BorderLayout(0,5));
		this.add(tabbedPane, BorderLayout.CENTER);
		Tab tab = new Tab();
		this.addTab(tab);
		this.updateTabName(tab);
		this.tabbedPane.setFocusable(false);
		MouseAdapter mouseListener = new MouseAdapter()
		{
			private Tab lastPressedTab = null;
			@Override
			public void mousePressed(MouseEvent ev)
			{
				int index = tabbedPane.indexAtLocation(ev.getX(), ev.getY());
				if (index != -1)
				{
					this.lastPressedTab = (Tab)(tabbedPane.getComponentAt(index));
				}
				else
				{
					this.lastPressedTab = null;
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				if (ev.isPopupTrigger())
				{
					int index = tabbedPane.indexAtLocation(ev.getX(), ev.getY());
					if (index != -1)
					{
						Tab tab = (Tab)(tabbedPane.getComponentAt(index));
						JLabel tabLabel = tab.getTabLabel();
						for (MouseListener listener: tabLabel.getMouseListeners())
						{
							listener.mouseReleased(SwingUtilities.convertMouseEvent(tabbedPane,ev,tabLabel));
						}
					}
				}
			}
			
			@Override 
			public void mouseDragged(MouseEvent ev)
			{
				if (this.lastPressedTab != null)
				{
					JLabel tabLabel = this.lastPressedTab.getTabLabel();
					for (MouseMotionListener listener: tabLabel.getMouseMotionListeners())
					{
						listener.mouseDragged(SwingUtilities.convertMouseEvent(tabbedPane,ev,tabLabel));
					}
				}
			}
		};
		this.tabbedPane.addMouseListener(mouseListener);
		this.tabbedPane.addMouseMotionListener(mouseListener);
	}
	
	public static MainPanel getInstance()
	{
		return INSTANCE;
	}
	
	public static Tab getSelectedTab()
	{
		return (Tab)(INSTANCE.tabbedPane.getSelectedComponent());
	}
	
	public static Set<Tab> getAllTab()
	{
		Set<Tab> tabs = new LinkedHashSet<>();
		for (int i=0; i<INSTANCE.tabbedPane.getTabCount(); i++)
		{
			tabs.add((Tab)(INSTANCE.tabbedPane.getComponentAt(i)));
		}
		return tabs;
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
		this.tabbedPane.addTab(null, null, tab, null);
	}
	
	public void updateTabName(Tab tab)
	{
		File file = tab.getFile();
		//set tabLabel text
		JLabel tabLabel = tab.getTabLabel();
		String tabText = (tab.isSaved()?"":"*") + (file==null?"Untitled":file.getName());
		tabLabel.setText(tabText);
		//calculate size
		JPanel tabPanel = tab.getTabPanel();
		int stringWidth = tabLabel.getFontMetrics(tabLabel.getFont()).stringWidth(tabText);
		tabPanel.setPreferredSize(new Dimension(Math.max(stringWidth,60), UISetter.getLookAndFeelTabHeight()));
		//add tabLabel
		int index = tabbedPane.indexOfComponent(tab);
		tabbedPane.setTabComponentAt(index, tabPanel);
		//tooltip: whole path
		String path = file==null?"Untitled":file.getPath();
		tabLabel.setToolTipText(path);
	}
	
	public static void setSelectedComponent(Tab tab)
	{
		MainPanel.getInstance().tabbedPane.setSelectedComponent(tab);
	}
	
	public JTabbedPane getTabbedPane()
	{
		return this.tabbedPane;
	}
	
	public static void close(Tab tab)
	{
		//convenient method
		MainPanel.getInstance().removeTab(tab);
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
	}
	
	public void swapTab(int draggedIndex, int reachedIndex)
	{
		Tab dragged = (Tab)(tabbedPane.getComponentAt(draggedIndex));
		Tab reached = (Tab)(tabbedPane.getComponentAt(reachedIndex));
		tabbedPane.remove(dragged);
		tabbedPane.insertTab(null,null,dragged,null,reachedIndex);
		tabbedPane.remove(reached);
		tabbedPane.insertTab(null,null,reached,null,draggedIndex);
		this.updateTabName(dragged);
		this.updateTabName(reached);
		this.setSelectedComponent(dragged);
	}
}
