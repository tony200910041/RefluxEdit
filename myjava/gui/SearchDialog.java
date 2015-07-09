/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.regex.*;
import exec.*;
import myjava.gui.*;
import myjava.util.*;

public class SearchDialog extends JDialog
{
	private SearchDialog(final Frame parent, final JTextArea textArea)
	{
		super(parent, "Search and Replace", false);
		this.getContentPane().setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		/*
		 * original text:
		 */
		MyLabel la1 = new MyLabel("Original: ");
		final MyTextField wd1 = new MyTextField(20);
		c.gridx=0;
		c.gridy=0;
		c.weightx=0.2;
		c.insets=new Insets(5,5,5,5);
		c.fill=GridBagConstraints.NONE;
		this.add(la1,c);
		c.gridx=1;
		c.gridy=0;
		c.weightx=0.8;
		c.fill=GridBagConstraints.HORIZONTAL;
		this.add(wd1,c);
		/*
		 * this text:
		 */							
		MyLabel la2 = new MyLabel("Replaced by: ");
		final MyTextField wd2 = new MyTextField(20);
		c.gridx=0;
		c.gridy=1;
		c.weightx=0.2;
		c.fill=GridBagConstraints.NONE;
		this.add(la2,c);
		c.gridx=1;
		c.gridy=1;
		c.weightx=0.8;
		c.fill=GridBagConstraints.HORIZONTAL;
		this.add(wd2,c);
		/*
		 * options
		 */
		final MyCheckBox regex = new MyCheckBox("Use regex",false);
		final MyCheckBox caseSensitive = new MyCheckBox("Case sensitive",true);
		MyPanel panelBox = new MyPanel(MyPanel.CENTER);
		panelBox.add(regex);
		panelBox.add(caseSensitive);
		c.gridx=0;
		c.gridy=2;
		c.gridwidth=2;
		c.fill=GridBagConstraints.HORIZONTAL;
		this.add(panelBox,c);
		/*
		 * start button
		 */
		MyPanel panelButton = new MyPanel(MyPanel.CENTER);
		MyButton button1 = new MyButton("Start")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				if (textArea.isEditable())
				{
					String original = textArea.getText();
					String selected = textArea.getSelectedText();
					String buffer = selected==null?original:selected;
					String find = wd1.getText();
					String match = wd2.getText();
					boolean useRegex = regex.isSelected();
					boolean isCaseSensitive = caseSensitive.isSelected();
					int count = StaticUtilities.count(buffer,find,useRegex,isCaseSensitive);
					if (count != 0)
					{
						String result = StaticUtilities.replace(buffer,find,match,useRegex,isCaseSensitive);
						if (selected != null)
						{
							int start = textArea.getSelectionStart();
							textArea.replaceSelection(result);
							textArea.select(start,start+result.length());
						}
						else
						{
							textArea.setText(result);
						}
					}
					JOptionPane.showMessageDialog(parent, count + " time(s) thisd", "Replace", JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					MyListener.cannotEdit();
				}
			}
		};
		panelButton.add(button1);
		MyButton button2 = new MyButton("Next")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				String original = textArea.getText();
				String find = wd1.getText();
				boolean useRegex = regex.isSelected();
				if (!caseSensitive.isSelected())
				{
					//case insensitive
					original = original.toLowerCase();
					find = find.toLowerCase();
				}
				int caret = Math.max(Math.max(textArea.getCaretPosition(),textArea.getSelectionStart()),textArea.getSelectionEnd());
				int index=0, end=0;
				Pattern pattern = Pattern.compile(find);
				if (caret != original.length())
				{
					if (useRegex)
					{
						//use regex
						Matcher matcher = pattern.matcher(original);
						if (matcher.find(caret))
						{
							index = matcher.start();
							end = matcher.end();
						}
						else
						{
							index = -1;
						}
					}
					else
					{
						index = original.indexOf(find,caret);
						end = index+find.length();
					}
				}
				else
				{
					index = -1;
				}
				if (index != -1)
				{
					textArea.select(index, end);
				}
				else
				{
					int option = JOptionPane.showConfirmDialog(parent, "Reached the end of the file!\nSearch from the start again?", "Error", JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION)
					{
						textArea.setCaretPosition(0);
						this.actionPerformed(ev); //restart searching
					}
				}
			}
		};
		button2.setToolTipText("Find next occurrence");
		this.getRootPane().setDefaultButton(button2);
		panelButton.add(button2);
		MyButton button3 = new MyButton("Last")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				String original = textArea.getText();
				String find = wd1.getText();
				boolean useRegex = regex.isSelected();
				if (!caseSensitive.isSelected())
				{
					//case insensitive
					original = original.toLowerCase();
					find = find.toLowerCase();
				}
				int caret = Math.min(Math.min(textArea.getCaretPosition(),textArea.getSelectionStart()),textArea.getSelectionEnd());
				int index=0, end=0;
				Pattern pattern = Pattern.compile(find);
				if (caret != 0)
				{
					if (useRegex)
					{
						for (int i=caret-find.length(); i>=0; i--)
						{
							//use regex
							String fragment = original.substring(i,caret);
							Matcher matcher = pattern.matcher(fragment);
							if (matcher.find())
							{
								index = matcher.start()+i;
								end = matcher.end()+i;
								break;
							}
							else
							{
								index = -1;
							}
						}
					}
					else
					{
						String fragment = original.substring(0,caret);
						index = fragment.lastIndexOf(find);
						if (index != -1)
						{
							end = index + find.length();
						}
					}
				}
				else
				{
					index = -1;
				}
				if (index != -1)
				{
					textArea.select(index, end);
					return;
				}
				else
				{
					int option = JOptionPane.showConfirmDialog(parent, "Reached the start of the file!\nSearch from the end again?", "Error", JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION)
					{
						textArea.setCaretPosition(original.length());
						this.actionPerformed(ev);
						return;
					}
				}
			}
		};
		button3.setToolTipText("Find last occurrence");
		panelButton.add(button3);
		c.gridx=0;
		c.gridy=3;
		c.gridwidth=2;
		c.fill=GridBagConstraints.HORIZONTAL;
		this.add(panelButton,c);
	}
	
	public static void showDialog(Frame parent, JTextArea textArea)
	{
		SearchDialog dialog = new SearchDialog(parent, textArea);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}
}
