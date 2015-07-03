/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import myjava.gui.*;
import myjava.gui.common.*;

public class RegexDialog extends JDialog implements DocumentListener, ColorConstants
{
	private static final Font f20 = new Font("Microsoft Jhenghei",Font.PLAIN,20);
	private JTextField string = new JTextField();
	private JTextField regex = new JTextField();
	private JLabel match = new JLabel("Regex matcher");
	public static void showRegexDialog(Frame parent)
	{
		RegexDialog dialog = new RegexDialog(parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setResizable(false);
		dialog.setVisible(true);
	}
	
	private RegexDialog(Frame parent)
	{
		super(parent,"Regex matcher",true);
		this.setLayout(new GridLayout(3,1,0,0));
		JPanel p0 = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
		string.setFont(f20);
		string.setPreferredSize(new Dimension(350,33));
		string.getDocument().addDocumentListener(this);
		p0.add(string);
		this.add(p0);
		//
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
		match.setFont(f20);
		p1.add(match);
		this.add(p1);
		//
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
		regex.setFont(f20);
		regex.setPreferredSize(new Dimension(350,33));
		regex.getDocument().addDocumentListener(this);
		p2.add(regex);
		this.add(p2);
	}
	
	public void update()
	{
		String s1 = string.getText();
		String s2 = regex.getText();
		if (s1.isEmpty()||s2.isEmpty())
		{
			string.setBackground(Color.WHITE);
			regex.setBackground(Color.WHITE);
			match.setText("Regex matcher");
		}
		else 
		{
			try
			{
				if (s1.matches(s2))
				{
					string.setBackground(GREEN);
					regex.setBackground(GREEN);
					match.setText("matches");
					return;
				}
			}
			catch (Exception ex)
			{
				//input not finishes, fail sliently
			}
			string.setBackground(RED);
			regex.setBackground(RED);
			match.setText("doesn't match");
		}
	}
	
	@Override
	public void changedUpdate(DocumentEvent ev)
	{
		update();
	}
	
	@Override
	public void removeUpdate(DocumentEvent ev)
	{
		update();
	}
	
	@Override
	public void insertUpdate(DocumentEvent ev)
	{
		update();
	}
}
