/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import myjava.gui.*;
import static exec.SourceManager.*;

public class RecentMenu extends MyMenu implements MenuListener
{
	private static final RecentMenu INSTANCE = new RecentMenu();
	private RecentMenu()
	{
		super("Recent files");
		this.addMenuListener(this);
	}
	
	public static RecentMenu getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public void menuSelected(MenuEvent ev)
	{
		RecentMenu.this.removeAll();
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
					JMenuItem item = RecentMenu.this.add(new FileAction(file));
					item.setText(count + ": " + file.getPath());
					if (count == 10) break;
				}
			}
			RecentMenu.this.addSeparator();
		}		
		JMenuItem clear = RecentMenu.this.add(new ClearAction());
		clear.setText("Clear");
	}
	
	private static class FileAction extends AbstractAction
	{
		private File file;
		FileAction(File file)
		{
			super();
			this.file = file;
		}
		
		@Override
		public void actionPerformed(ActionEvent ev)
		{
			Tab tab = Tab.getNewTab();
			if (!MainPanel.getAllTab().contains(tab))
			{
				MainPanel.add(tab);
			}
			tab.open(this.file);
		}
	}
	
	private static class ClearAction extends AbstractAction
	{
		ClearAction()
		{
			super();
		}
		
		@Override
		public void actionPerformed(ActionEvent ev)
		{
			loadConfig();
			removeConfig0("recentFiles");
			saveConfig();
		}
	}
	
	@Override
	public void menuDeselected(MenuEvent ev)
	{
	}
	
	@Override
	public void menuCanceled(MenuEvent ev)
	{
	}
}
