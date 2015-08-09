/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.option;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import myjava.gui.*;
import static exec.SourceManager.*;

public class OutputTab extends OptionTab
{
	//encoding
	private JRadioButton printWriter = new MyRadioButton("Default mode 1");
	private JRadioButton fileOutputStream = new MyRadioButton("Default mode 2");
	private JRadioButton specifyEncoding = new MyRadioButton("Specify an encoding:");
	private JComboBox<String> charsetBox = Tab.createCharsetComboBox();
	private MyButtonGroup encodingGroup = new MyButtonGroup(printWriter,fileOutputStream,specifyEncoding);
	//line separator
	private JRadioButton _n = new MyRadioButton("\\n");
	private JRadioButton _r = new MyRadioButton("\\r");
	private JRadioButton _rn = new MyRadioButton("\\r\\n");
	private MyButtonGroup lineSeparatorGroup = new MyButtonGroup(_n,_r,_rn);
	public OutputTab()
	{
		super(new FlowLayout(FlowLayout.LEFT), "Output");
		//encoding and line separator
		JPanel in = new JPanel(new GridLayout(2,1,0,0));
		//encoding
		String encoding = getConfig("Encoding");
		if (encoding != null)
		{
			switch (encoding)
			{
				case "default1":
				printWriter.setSelected(true);
				break;
				
				case "default2":
				fileOutputStream.setSelected(true);
				break;
				
				default:
				specifyEncoding.setSelected(true);
				charsetBox.setSelectedItem(encoding);
				break;
			}
		}
		else printWriter.setSelected(true);
		JPanel panel1 = MyPanel.wrap(printWriter,fileOutputStream,specifyEncoding,charsetBox);
		panel1.setBorder(new TitledBorder("Encoding"));
		charsetBox.setEnabled(specifyEncoding.isSelected());
		specifyEncoding.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				charsetBox.setEnabled(specifyEncoding.isSelected());
			}
		});
		printWriter.setToolTipText("Use Java PrintWriter implementation");
		fileOutputStream.setToolTipText("Use Java default FileOutputStream implementation");
		specifyEncoding.setToolTipText("Custom encoding");
		in.add(panel1);
		//line separator
		String lineSeparator = getConfig0("lineSeparator");
		if (lineSeparator != null)
		{
			switch (lineSeparator)
			{
				case "\\n":
				_n.setSelected(true);
				break;
				
				case "\\r":
				_r.setSelected(true);
				break;
				
				case "\\r\\n":
				_rn.setSelected(true);
				break;
			}
		}
		else _n.setSelected(true);
		_n.setToolTipText("<html>Unix and Unix-like system,<br>including Linux, Mac OS X and FreeBSD.</html>");
		_r.setToolTipText("Mac OS 9");
		_rn.setToolTipText("Windows and Symbian OS");
		JPanel panel2 = MyPanel.wrap(_n, _r, _rn);
		panel2.setBorder(new TitledBorder("Line separator"));
		in.add(panel2);
		//
		this.add(in);
	}
	
	@Override
	public void onExit()
	{
		//encoding
		if (printWriter.isSelected())
		{
			setConfig("Encoding","default1");
		}
		else if (fileOutputStream.isSelected())
		{
			setConfig("Encoding","default2");
		}
		else
		{
			setConfig("Encoding",(String)(charsetBox.getSelectedItem()));
		}
		//line separator
		setConfig("lineSeparator", lineSeparatorGroup.getSelected().getText());
	}
}
