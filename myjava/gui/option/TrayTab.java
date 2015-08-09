/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.option;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import myjava.gui.*;
import static exec.SourceManager.*;

public class TrayTab extends OptionTab implements ActionListener
{
	private JCheckBox isUseTray = new MyCheckBox("Use system tray", getBoolean0("useTray"));
	private JCheckBox closeToTray = new MyCheckBox("Close to tray", getBoolean0("CloseToTray"));
	public TrayTab()
	{
		super(new FlowLayout(FlowLayout.CENTER), "System tray");
		this.add(isUseTray);
		this.add(closeToTray);
		closeToTray.setEnabled(isUseTray.isSelected());
		isUseTray.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		closeToTray.setEnabled(isUseTray.isSelected());
	}
	
	@Override
	public void onExit()
	{
		boolean _use = isUseTray.isSelected();
		setConfig("useTray", _use+"");
		setConfig("CloseToTray", closeToTray.isSelected()+"");
		try
		{
			SystemTray systemTray = SystemTray.getSystemTray();
			TrayIcon[] icons = systemTray.getTrayIcons();
			if (_use&&(icons.length==0))
			{
				parent.createTray();
			}
			else if (icons.length == 1)
			{
				systemTray.remove(icons[0]); //only added one, or none
			}
		}
		catch (Exception ex)
		{
		}
	}
}
