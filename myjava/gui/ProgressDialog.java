/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;
import java.text.*;

public class ProgressDialog extends JDialog
{
	private JProgressBar bar = new JProgressBar();
	private long start;
	{
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(bar);
		this.bar.setStringPainted(true);
		this.bar.setFont(myjava.gui.common.Resources.f13);
		this.pack();
		this.setLocationRelativeTo(this.getParent());
	}	
	public ProgressDialog(Window parent, String name)
	{
		super(parent,"Progress");
		this.setModal(false);
		this.bar.setString(name);
	}
	
	public ProgressDialog(Window parent)
	{
		this(parent,"Please wait...");
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (visible)
		{
			//start calculating time
			start = System.nanoTime();
		}
	}
	
	public void setMax(int max)
	{
		this.bar.setMaximum(max);
	}
	
	public void setValue(int value)
	{
		this.bar.setValue(value);
	}
	
	public void setIndeterminate(boolean newValue)
	{
		this.bar.setIndeterminate(newValue);
	}
	
	public void setString(String s)
	{
		this.bar.setString(s);
	}
	
	public String timeUsed()
	{
		return new DecimalFormat("0.00").format((System.nanoTime()-start)/Math.pow(10,9));
	}
}
