/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.option;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import exec.*;
import myjava.gui.*;
import myjava.util.*;
import myjava.gui.common.*;

public abstract class OptionTab extends JPanel implements Resources
{
	public static final RefluxEdit parent = RefluxEdit.getInstance();
	protected final JComponent topPanel = parent.getPageStartComponent();
	protected final Tab tab = MainPanel.getSelectedTab();
	protected final MyTextArea textArea = tab.getTextArea();
	protected final File file = tab.getFile();
	protected final UndoManager undoManager = textArea.getUndoManager();
	private String name;
	public OptionTab(LayoutManager layout, String name)
	{
		super(layout);
		this.name = name;
		this.setBackground(Color.WHITE);
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public static Set<OptionTab> getAllTabs()
	{
		Set<OptionTab> tabSet = new LinkedHashSet<>();
		tabSet.add(new GeneralTab());
		tabSet.add(new EditorTab());
		tabSet.add(new OutputTab());
		tabSet.add(new CompileTab());
		tabSet.add(new SyntaxTab());
		tabSet.add(new FileFormatTab());
		tabSet.add(new LookAndFeelTab());
		tabSet.add(new TrayTab());
		tabSet.add(new HintTab());
		return tabSet;
	}
	
	public abstract void onExit();
}
