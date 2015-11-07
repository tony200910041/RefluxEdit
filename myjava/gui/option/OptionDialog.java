/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.option;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;
import exec.*;
import myjava.gui.*;
import myjava.gui.common.*;

public class OptionDialog extends JDialog implements TreeSelectionListener, Resources
{
	private CardLayout centerCardLayout = new CardLayout();
	private JPanel centerPanel = new JPanel(centerCardLayout);
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Options");
	private JTree tree = new JTree(root);	
	private Set<OptionTab> tabSet = OptionTab.getAllTabs();
	private OptionDialog(Frame parent)
	{
		super(parent, "Preferences", true);
		//setup tree
		tree.setFont(f13);
		tree.addTreeSelectionListener(this);
		//add tabs
		for (OptionTab tab: tabSet)
		{
			String tabName = tab.getName();
			root.add(new DefaultMutableTreeNode(tabName));
			centerPanel.add(tabName, tab);
		}
		this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), centerPanel), BorderLayout.CENTER);
		((DefaultTreeModel)(tree.getModel())).reload(root);
		tree.expandPath(tree.getSelectionPath());
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent ev)
	{
		TreePath path = ev.getNewLeadSelectionPath();
		String selected = (String)(((DefaultMutableTreeNode)(path.getLastPathComponent())).getUserObject());
		if (selected != null)
		{
			if (!("Options").equals(selected))
			{
				centerCardLayout.show(centerPanel, selected);
			}
		}
	}
	
	public static void showDialog(Frame parent)
	{
		SourceManager.loadConfig();
		//restore toolbar to original location
		MyToolBar.getInstance().setUI(new StopFloatingToolBarUI());
		MyToolBar.getInstance().updateUI();
		MyCompileToolBar.getInstance().setUI(new StopFloatingToolBarUI());
		MyCompileToolBar.getInstance().updateUI();
		//build dialog
		OptionDialog dialog = new OptionDialog(parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		/*
		 * closed
		 */		
		for (OptionTab tab: dialog.tabSet)
		{
			tab.onExit();
		}
		SourceManager.saveConfig();
		for (Tab tab: MainPanel.getAllTab())
		{
			tab.update();
		}
	}
}
