package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.nio.file.*;
import java.net.*;
import exec.*;
import myjava.gui.*;
import static myjava.gui.common.Resources.*;

public class MPLDialog extends JDialog
{
	private static final String MPL_DESCRIPTION = MPLDialog.getDescription();
	private static final SimpleAttributeSet HEADER_SET = new SimpleAttributeSet();
	private static final SimpleAttributeSet PLAIN_SET = new SimpleAttributeSet();
	static
	{
		StyleConstants.setBold(HEADER_SET, true);
		StyleConstants.setFontFamily(HEADER_SET, f13.getName());
		StyleConstants.setFontSize(HEADER_SET, 26);
		StyleConstants.setBold(PLAIN_SET, false);
		StyleConstants.setFontFamily(PLAIN_SET, f13.getName());
		StyleConstants.setFontSize(PLAIN_SET, 15);
	}
	protected MPLDialog(final Frame parent)
	{
		super(parent,"About Mozilla Public License 2.0",true);
		this.setLayout(new BorderLayout());
		/*
		 * main JEditorPane
		 */
		StyledDocument doc = new DefaultStyledDocument();
		JTextPane textPane = new JTextPane(doc);
		textPane.setEditable(false);
		try
		{
			doc.insertString(0, "About Mozilla Public License 2.0\n\n", HEADER_SET);
			doc.insertString(doc.getLength(), MPL_DESCRIPTION, PLAIN_SET);
		}
		catch (BadLocationException ex)
		{
			throw new InternalError();
		}
		this.add(new JScrollPane(textPane), BorderLayout.CENTER);
		/*
		 * done button
		 */
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(new MyButton("Browse")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				String[] options = new String[]{"MPL 2.0 full license", "MPL 2.0 FAQ", "Wikipedia: Mozilla Public License"};
				String chosen = (String)JOptionPane.showInputDialog(parent, "Please select the website you want to browse:", "Website", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (chosen != null)
				{
					String s;
					if (chosen.equals(options[0]))
					{
						s = "https://www.mozilla.org/MPL/2.0/";
					}
					else if (chosen.equals(options[1]))
					{
						s = "https://www.mozilla.org/MPL/2.0/FAQ.html";
					}
					else
					{
						s = "http://en.wikipedia.org/wiki/Mozilla_Public_License";
					}
					try
					{
						URI uri = new URI(s);
						Desktop.getDesktop().browse(uri);
					}
					catch (Exception ex)
					{
						ExceptionDialog.exception(ex);
					}
				}
			}
		});
		bottomPanel.add(new MyButton("Done")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				MPLDialog.this.setVisible(false);
				MPLDialog.this.dispose();
			}
		});
		this.add(bottomPanel, BorderLayout.PAGE_END);
		this.setSize(450,278);
		this.setLocationRelativeTo(parent);
	}
	
	public static void showDialog(Frame parent)
	{
		MPLDialog dialog = new MPLDialog(parent);
		dialog.setVisible(true);
	}
	
	public static String getDescription()
	{
		return "The MPL is a simple copyleft license. The MPL's \"file-level\" copyleft"
			 + "is designed to encourage contributors to share modifications they make to your code,"
			 + " while still allowing them to combine your code with code under other licenses "
			 + "(open or proprietary) with minimal restrictions. (copied from MPL 2.0 FAQ)";
	}
}
