/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;
import myjava.gui.*;
import myjava.gui.common.*;

public class LineChooser extends JDialog implements Resources
{
	protected JSpinner lineSpinner;
	protected boolean isCancelled = true; //default, for closing dialog directly
	protected LineChooser(Frame parent, JTextArea textArea)
	{
		super(parent,"Goto line",true);
		int count = textArea.getLineCount();
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		/*
		 * first panel
		 */
		this.add(new MyLabel("Goto line: "));
		this.lineSpinner = new JSpinner(new SpinnerNumberModel(1,1,count,1));
		this.lineSpinner.setFont(new Font(f13.getName(),Font.PLAIN,14));
		this.add(this.lineSpinner);
		this.add(new MyButton("Done")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				LineChooser.this.isCancelled = false;
				LineChooser.this.setVisible(false);
			}
		});
		this.add(new MyButton("Cancel")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				LineChooser.this.isCancelled = true;
				LineChooser.this.setVisible(false);
			}
		});
		/*
		 * dialog
		 */
		this.pack();
		this.setLocationRelativeTo(parent);
		this.setResizable(false);
	}
	
	public static Integer showDialog(Frame parent, JTextArea textArea)
	{
		LineChooser chooser = new LineChooser(parent, textArea);
		chooser.setVisible(true);
		if (chooser.isCancelled)
		{
			return null;
		}
		else
		{
			try
			{
				chooser.lineSpinner.commitEdit();
				return Integer.valueOf(chooser.lineSpinner.getValue().toString());
			}
			catch (ParseException ex)
			{
				ExceptionDialog.exception(ex);
				return null;
			}
		}
	}
}
