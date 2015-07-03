/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import exec.*;
import myjava.util.*;
import static exec.SourceManager.*;
import static myjava.gui.common.Resources.*;

public class ClipboardDialog extends JDialog
{
	/*
	 * static fields
	 */
	private static final ClipboardDialog INSTANCE = new ClipboardDialog();
	/*
	 * instance fields
	 */
	private DefaultListModel<String> lm = new DefaultListModel<>();
	private JList<String> list = new JList<>(lm);
	private JTextArea textArea = new JTextArea();
	private JScrollPane upperPane = new JScrollPane(list);
	private JScrollPane lowerPane = new JScrollPane(textArea);
	private boolean isFirstLaunch = true;
	private ClipboardDialog()
	{
		super(RefluxEdit.getInstance(),"Clipboard listener",false);
		this.setLayout(new BorderLayout());
		this.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT,upperPane,lowerPane), BorderLayout.CENTER);
		upperPane.setPreferredSize(new Dimension(240,310));
		lowerPane.setPreferredSize(new Dimension(240,200));
		lowerPane.setMinimumSize(new Dimension(240,200));
		/*
		 * setup JTextArea
		 */
		textArea.setFont(new Font("Times New Roman",Font.PLAIN,15));
		textArea.setBackground(Color.WHITE);
		textArea.setEditable(false);
		textArea.setDragEnabled(true);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		try
		{
			loadConfig();
			textArea.setSelectionColor(new Color(Short.parseShort(getConfig0("SelectionColor.r")), Short.parseShort(getConfig0("SelectionColor.g")), Short.parseShort(getConfig0("SelectionColor.b"))));
		}
		catch (Exception ex)
		{
			textArea.setSelectionColor(new Color(244,223,255));
		}
		/*
		 * setup list
		 */
		list.setCellRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				JLabel label = (JLabel)(super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus));
				if (value instanceof String)
				{
					String s = (String)value;
					if (s.length() <= 20)
					{
						label.setText(s);
					}
					else
					{
						label.setText(s.substring(0,20) + "... (" + s.length() + " characters");
					}
				}
				return label;
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				String selected = list.getSelectedValue();
				if (selected != null)
				{
					textArea.setText(selected);
					textArea.selectAll();
					textArea.requestFocusInWindow();
				}
			}
		});
		list.setFont(f13);
		this.pack();
		/*
		 * initialize listener
		 */
		(new ClipboardListener()
		{
			@Override
			public void clipboardChanged(Transferable t)
			{
				if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
				{
					try
					{
						Object data = t.getTransferData(DataFlavor.stringFlavor);
						if (data != null)
						{
							lm.addElement((String)data);
						}
					}
					catch (Exception ex)
					{
					}
				}
			}
		}).start();
	}
	
	@Override
	public void setVisible(boolean isVisible)
	{
		if (isVisible)
		{
			if (isFirstLaunch) //first launch per start
			{
				isFirstLaunch = false;
				Point p = RefluxEdit.getInstance().getLocation();
				this.setLocation(p.x-255, p.y);
			}
		}
		super.setVisible(isVisible);
		if (getBoolean("FirstTime.clipboardListener"))
		{
			Point p = this.list.getLocationOnScreen();
			ToolTipMessage.showMessage(p.x+5, p.y+5, "Clipboard listener", "This listens to text changes in system\nclipboard when RefluxEdit is launched.");
			writeConfig("FirstTime.clipboardListener", "false");
		}
	}
	
	public static void initialize()
	{
		// initialize INSTANCE automatically
	}
	
	public static ClipboardDialog getInstance()
	{
		return INSTANCE;
	}
	
	public JTextArea getTextArea()
	{
		return this.textArea;
	}
}
