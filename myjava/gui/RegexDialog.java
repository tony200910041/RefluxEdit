/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import myjava.gui.*;
import myjava.gui.common.*;

public class RegexDialog extends JDialog implements DocumentListener, ColorConstants
{
	private static final Font f15 = Resources.f13.deriveFont(15f);
	private static final Font f20 = Resources.f13.deriveFont(20f);
	private JTextArea string = new JTextArea();
	private JTextField regex = new JTextField();
	private JLabel match = new JLabel("Regex matcher");
	public static void showRegexDialog(Frame parent)
	{
		RegexDialog dialog = new RegexDialog(parent);
		dialog.setMinimumSize(new Dimension(370,330));
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}
	
	private RegexDialog(Frame parent)
	{
		super(parent,"Regex matcher",false);
		this.setLayout(new BorderLayout());
		//
		string.setFont(f15);
		string.setDragEnabled(true);
		string.setLineWrap(true);
		string.setWrapStyleWord(true);
		string.getDocument().addDocumentListener(this);
		JScrollPane scrollPane = new JScrollPane(string);
		this.add(scrollPane, BorderLayout.CENTER);
		//
		regex.setFont(f15);
		regex.setPreferredSize(new Dimension(350,33));
		regex.getDocument().addDocumentListener(this);
		regex.setDragEnabled(true);
		//
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
		match.setFont(f20);
		p1.add(match);
		//
		JPanel lower = new JPanel(new GridLayout(2,1,0,0));
		lower.add(p1);
		lower.add(regex);
		this.add(lower, BorderLayout.PAGE_END);
		
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
