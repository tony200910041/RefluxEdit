/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import exec.*;
import myjava.gui.common.*;
import static myjava.gui.ExceptionDialog.*;

public class SaveDialog extends JDialog implements Resources
{
	private static final ImageIcon QUESTION_ICON = SourceManager.createQuestionMessageIcon();
	private static final Dimension METAL_BUTTON_DIZE = new Dimension(65,28);
	private JLabel iconLabel = new JLabel(QUESTION_ICON);
	private boolean close = false;
	public SaveDialog(Frame parent)
	{
		super(parent,true);
		this.setLayout(new BorderLayout());
		//gather unsaved tab
		Set<Tab> unsaved = new LinkedHashSet<>();
		for (Tab tab: MainPanel.getAllTab())
		{
			if (!tab.isSaved())
			{
				unsaved.add(tab);
			}
		}
		if (unsaved.isEmpty())
		{
			//close directly
			this.setTitle("Confirm close");
			//upper labels
			JPanel labels = new JPanel(new FlowLayout(FlowLayout.LEFT,11,7));
			labels.add(iconLabel);
			labels.add(new MyLabel(" Do you really want to close RefluxEdit?"));
			//buttons
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,4,7));
			buttons.add(new MyButton("YES")
			{
				{
					SaveDialog.this.getRootPane().setDefaultButton(this);
					this.setFocusPainted(true);
				}
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					close = true;
					SaveDialog.this.setVisible(false);
				}
			});
			buttons.add(new MyButton("NO")
			{
				{
					this.setFocusPainted(true);
				}
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					close = false;
					SaveDialog.this.setVisible(false);
				}
			});
			buttons.setBorder(new EmptyBorder(0,0,5,0));
			//
			this.add(labels, BorderLayout.CENTER);
			this.add(buttons, BorderLayout.PAGE_END);
		}
		else
		{
			//ask save changes
			this.setTitle("Unsaved changes");
			//upper components: icon and list
			JPanel upper = new JPanel(new GridBagLayout());
			JPanel listPane = new JPanel(new BorderLayout());
			final DefaultListModel<Tab> listModel = new DefaultListModel<>();
			final JList<Tab> tabList = new JList<>(listModel);
			tabList.setFont(f13);
			tabList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			tabList.setCellRenderer(new DefaultListCellRenderer()
			{
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			 	{
					JLabel label = (JLabel)(super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus));
					if (value instanceof Tab)
					{
						String text = ((Tab)value).getTabLabel().getText();
						label.setText(text.substring(1)); //remove "*"
					}
					return label;
				}
			});
			for (Tab tab: unsaved)
			{
				listModel.addElement(tab);
			}
			tabList.getSelectionModel().setSelectionInterval(0,listModel.size()-1);
			JScrollPane scrollPane = new JScrollPane(tabList);
			scrollPane.setPreferredSize(new Dimension(350,170));
			listPane.add(new MyLabel("The following tabs are unsaved:"), BorderLayout.PAGE_START);
			listPane.add(scrollPane, BorderLayout.CENTER);
			//setup upper
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 0;
			c.weighty = 1;
			c.insets = new Insets(5,5,5,5);
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			upper.add(iconLabel, c);
			//
			c.gridx = 1;
			c.weightx = 1;
			c.fill = GridBagConstraints.BOTH;
			upper.add(listPane, c);
			//lower components: buttons
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,4,7));
			buttons.add(new MyButton("Save")
			{
				{
					this.setFocusPainted(true);
					this.setToolTipText("Save selected tab(s)");
					SaveDialog.this.getRootPane().setDefaultButton(this);
					if (isMetal)
					{
						this.setPreferredSize(METAL_BUTTON_DIZE);
					}
				}
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					for (Tab tab: tabList.getSelectedValuesList())
					{
						if (tab.getFile() != null)
						{
							try
							{
								tab.save();
								MainPanel.close(tab);
								listModel.removeElement(tab);
							}
							catch (Exception ex)
							{
								exception(ex);
								break;
							}
						}
						else
						{
							File file = FileChooser.showPreferredFileDialog(RefluxEdit.getInstance(), FileChooser.SAVE, new String[0]);
							if (file != null)
							{
								try
								{
									tab.save(file,false);
									MainPanel.close(tab);
									listModel.removeElement(tab);
								}
								catch (Exception ex)
								{
									exception(ex);
									break;
								}
							}
						}
					}
					if (listModel.size() == 0)
					{
						RefluxEdit.getInstance().close();
					}
				}
			});
			buttons.add(new MyButton("Discard")
			{
				{
					this.setFocusPainted(true);
					this.setToolTipText("Discard selected tab(s)");
					if (isMetal)
					{
						this.setPreferredSize(METAL_BUTTON_DIZE);
					}
				}
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					for (Tab tab: tabList.getSelectedValuesList())
					{
						MainPanel.close(tab);
						listModel.removeElement(tab);
					}
					if (listModel.size() == 0)
					{
						RefluxEdit.getInstance().close();
					}
				}
			});
			buttons.add(new MyButton("Close")
			{
				{
					this.setFocusPainted(true);
					this.setToolTipText("Close RefluxEdit");
					if (isMetal)
					{
						this.setPreferredSize(METAL_BUTTON_DIZE);
					}
				}
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					SaveDialog.this.close = true;
					SaveDialog.this.setVisible(false);
				}
			});
			buttons.add(new MyButton("Cancel")
			{
				{
					this.setFocusPainted(true);
					if (isMetal)
					{
						this.setPreferredSize(METAL_BUTTON_DIZE);
					}
				}
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					SaveDialog.this.close = false;
					SaveDialog.this.setVisible(false);
				}
			});
			this.add(upper, BorderLayout.CENTER);
			this.add(buttons, BorderLayout.PAGE_END);
		}
	}
	
	public static void showDialog(Frame parent)
	{
		SaveDialog dialog = new SaveDialog(parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		/*
		 * closed
		 */
		if (dialog.close)
		{
			RefluxEdit.getInstance().close();
		}
	}
}
