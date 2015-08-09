/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.option;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import exec.*;
import myjava.gui.*;
import myjava.util.*;

public class FileFormatTab extends OptionTab
{
	public FileFormatTab()
	{
		super(new BorderLayout(), "File format");
		final JList<String> definedFormat = new JList<>(new Vector<>(TextFileFormat.PRE_DEFINED));
		definedFormat.setFont(f13);
		definedFormat.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		definedFormat.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		//
		JPanel definedFormatPanel = new JPanel(new BorderLayout());
		definedFormatPanel.setBorder(new TitledBorder("Pre-defined format:"));
		definedFormatPanel.add(new JScrollPane(definedFormat), BorderLayout.CENTER);
		definedFormatPanel.setBackground(Color.WHITE);
		//
		final DefaultListModel<String> userFormatModel = new DefaultListModel<>();
		final JList<String> userFormat = new JList<>(userFormatModel);
		for (String ext: TextFileFormat.USER_DEFINED)
		{
			userFormatModel.addElement(ext);
		}
		userFormat.setFont(f13);
		userFormat.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		userFormat.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		//
		JPanel userFormatPanel = new JPanel(new BorderLayout());
		userFormatPanel.setBorder(new TitledBorder("Custom format:"));
		userFormatPanel.add(new JScrollPane(userFormat), BorderLayout.CENTER);
		userFormatPanel.setBackground(Color.WHITE);
		//
		JPanel controlPanel = new JPanel(new GridBagLayout());
		MyButton formatAdd = new MyButton("Add")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				String ext = JOptionPane.showInputDialog(FileFormatTab.this, "Enter the extension(s):", "Extensions", JOptionPane.QUESTION_MESSAGE);
				if ((ext != null)&&(!ext.isEmpty()))
				{
					String[] extensions = ext.split("[;, ]");
					for (String s: extensions)
					{
						userFormatModel.addElement(s.toLowerCase());
					}
					Collections.addAll(TextFileFormat.USER_DEFINED, extensions);
					//write to settings
					SourceManager.writeConfig("userDefinedTextFormats", TextFileFormat.getUserFormatString());
				}
			}
		};
		MyButton formatRemove = new MyButton("Remove")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				int[] rows = userFormat.getSelectedIndices();
				if ((rows != null)&&(rows.length != 0))
				{
					int option = JOptionPane.showConfirmDialog(FileFormatTab.this,"Remove format?", "Confirm", JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION)
					{
						Set<String> extSet = new HashSet<>();
						for (int i=rows.length-1; i>=0; i--)
						{
							extSet.add(userFormatModel.remove(rows[i]));
						}
						TextFileFormat.reloadUserDefinedFormat();
						TextFileFormat.USER_DEFINED.removeAll(extSet);
						SourceManager.writeConfig("userDefinedTextFormats", TextFileFormat.getUserFormatString());
					}
				}
			}
		};
		if (isMetal) formatRemove.setPreferredSize(new Dimension(65,28));
		MyPanel buttonPanel = new MyPanel(MyPanel.CENTER);
		buttonPanel.add(formatAdd);
		buttonPanel.add(formatRemove);
		controlPanel.setBackground(Color.WHITE);
		controlPanel.add(buttonPanel);
		//
		JPanel listPanel = new JPanel(new GridLayout(2,1,0,0));
		listPanel.add(definedFormatPanel);
		listPanel.add(userFormatPanel);
		this.add(listPanel, BorderLayout.CENTER);
		this.add(controlPanel, BorderLayout.LINE_END);
	}
	
	@Override
	public void onExit() {}
}
