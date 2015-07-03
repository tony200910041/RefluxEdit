/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.io.*;
import myjava.gui.*;
import myjava.gui.common.*;

public class HintDialog extends JDialog
{
	private static final SimpleAttributeSet BOLD_SET = new SimpleAttributeSet();
	private static final SimpleAttributeSet PLAIN_SET = new SimpleAttributeSet();
	private static final java.util.List<String> cache = new ArrayList<>(30);
	static
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(HintDialog.class.getResourceAsStream("/SRC/HINT/HINT.TXT"))))
		{
			String text;
			while ((text = reader.readLine()) != null)
			{
				cache.add(text);
			}
		}
		catch (IOException ex)
		{
			ExceptionDialog.exception(ex);
		}
		StyleConstants.setBold(BOLD_SET,true);
		StyleConstants.setFontFamily(BOLD_SET,Resources.f13.getName());
		StyleConstants.setFontSize(BOLD_SET,26);
		StyleConstants.setFontFamily(PLAIN_SET,Resources.f13.getName());
		StyleConstants.setFontSize(PLAIN_SET,15);
	}
	private JTextPane pane = new JTextPane();
	protected HintDialog(Frame parent)
	{
		super(parent,"Hints",true);
		this.setLayout(new BorderLayout());
		/*
		 * add component
		 */
		this.add(new JScrollPane(pane), BorderLayout.CENTER);
		JPanel bottom = new JPanel();
		bottom.add(new MyButton("Next")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				HintDialog.this.setRandomHint();
			}
		});
		bottom.add(new MyButton("Done")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				HintDialog.this.dispose();
			}
		});
		this.add(bottom, BorderLayout.PAGE_END);
		/*
		 * configure pane
		 */
		pane.setEditable(false);
		this.setRandomHint();
		/*
		 * configure dialog
		 */
		this.setSize(new Dimension(350,216));
		this.setLocationRelativeTo(parent);
	}
	
	private void setHint(int index)
	{
		Document doc = pane.getDocument();
		try
		{
			doc.remove(0, doc.getLength());
			doc.insertString(0, "Do you know...", BOLD_SET);
			doc.insertString(doc.getLength(), "\n\n"+cache.get(index)+"\n", PLAIN_SET);
		}
		catch (BadLocationException ex)
		{
			throw new InternalError();
		}
	}
	
	private void setRandomHint()
	{
		this.setHint((int)Math.round(Math.random()*(cache.size()-1)));
	}
	
	public static void showHintDialog(Frame parent)
	{
		HintDialog dialog = new HintDialog(parent);
		dialog.setVisible(true);
	}
}
