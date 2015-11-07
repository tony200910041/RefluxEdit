/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.option;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import exec.*;
import myjava.gui.*;

public class HintTab extends OptionTab
{
	private JCheckBox showHints = new MyCheckBox("Show hints on startup", SourceManager.getBoolean0("showHint"));
	public HintTab()
	{
		super(new FlowLayout(FlowLayout.CENTER), "Hints");
		this.add(showHints);
		this.add(new MyButton("Show now")
		{
			{
				if (isMetal) this.setPreferredSize(new Dimension(80,28));
			}
			
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				HintDialog.showHintDialog(parent);
			}
		});
	}
	
	@Override
	public void onExit()
	{
		SourceManager.setConfig("showHint", showHints.isSelected()+"");
	}
}
