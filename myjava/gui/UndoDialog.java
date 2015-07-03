package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import myjava.gui.common.*;
import exec.*;

public class UndoDialog extends JDialog implements Resources
{
	private JList<String> undoJList = createUndoRecordList();
	private JList<String> redoJList = createUndoRecordList();
	private Tab tab;
	public UndoDialog(final Tab tab)
	{
		super(RefluxEdit.getInstance(),"Undo and Redo record",false);
		this.setLayout(new BorderLayout());
		this.getContentPane().setBackground(Color.WHITE);
		this.tab = tab;
		/*
		 * center
		 */
		final JPanel center = new JPanel(new GridLayout(1,2,0,0));
		center.add(new JScrollPane(undoJList));
		center.add(new JScrollPane(redoJList));
		this.add(center, BorderLayout.CENTER);
		/*
		 * bottom
		 */
		MyPanel bottom = new MyPanel(MyPanel.CENTER);
		MyButton undo_b = new MyButton("Use undo list item")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				int index = undoJList.getSelectedIndex();
				MyTextArea textArea = tab.getTextArea();
				if (textArea.isEditable())
				{
					textArea.setAutoBackup(false);
					textArea.getUndoManager().undo(index+1); //e.g. index=0 means undo once
					textArea.setAutoBackup(false);
					resetUndoDialogList();
				}
				else
				{
					MyListener.cannotEdit();
				}
			}
		};
		MyButton redo_b = new MyButton("Use redo list item")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				int index = redoJList.getSelectedIndex();
				MyTextArea textArea = tab.getTextArea();
				if (textArea.isEditable())
				{
					textArea.setAutoBackup(false);
					textArea.getUndoManager().redo(index+1);
					textArea.setAutoBackup(false);
					resetUndoDialogList();
				}
				else
				{
					MyListener.cannotEdit();
				}
			}
		};
		undo_b.setPreferredSize(new Dimension(125,28));
		redo_b.setPreferredSize(new Dimension(125,28));
		bottom.add(undo_b);
		bottom.add(redo_b);
		bottom.add(new MyButton("Reload")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				resetUndoDialogList();
			}
		});
		this.add(bottom, BorderLayout.PAGE_END);
	}
	
	public void resetUndoDialogList()
	{
		//reset list
		DefaultListModel<String> undo_m = (DefaultListModel<String>)(undoJList.getModel());
		undo_m.removeAllElements();
		for (String _undo: tab.getTextArea().getUndoManager().getUndoList())
		{
			/*
			 * first one in list = peek
			 */
			undo_m.addElement(_undo);
		}
		//
		DefaultListModel<String> redo_m = (DefaultListModel<String>)(redoJList.getModel());
		redo_m.removeAllElements();
		for (String _redo: tab.getTextArea().getUndoManager().getRedoList())
		{
			redo_m.addElement(_redo);
		}
	}
	
	public static JList<String> createUndoRecordList()
	{
		//return a formatted JList
		DefaultListModel<String> lm = new DefaultListModel<String>();
		JList<String> list = new JList<>(lm);
		list.setFont(f13);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new DefaultListCellRenderer()
		{
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				JLabel label = (JLabel)(super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus));
				String str = value.toString();
				int length = str.length();
				if (length > 25) str = str.substring(0,25) + "...";
				if (length > 1) str = str + " (" + length + " characters)";
				else str = str + " (" + length + " character)";
				label.setText(str);
				label.setToolTipText(str);
				return label;
			}
		});
		return list;
	}
}
