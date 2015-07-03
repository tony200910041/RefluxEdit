/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.text.*;
import java.util.*;
import myjava.gui.common.*;
import static myjava.util.StaticUtilities.*;

public class CharCountDialog extends JDialog implements Resources
{
	private static final DecimalFormat f3dp = new DecimalFormat("0.###");
	private JTable table = new JTable();
	private String text;
	private CharCountDialog(Frame w, String text)
	{
		super(w,"Character count",true);
		this.text = text;
		char textArray[] = text.replace("\n", "").toCharArray();
		/*
		 * 64 types of characters
		 */
		int letterFreq[] = new int[64];
		Arrays.fill(letterFreq, 0);
		/*
		 * iterate through textArray to count
		 */
		for (char x: textArray)
		{
			letterFreq[toNumber(x)]++;
		}
		/*
		 * insert row to table
		 */
		Object[][] rowList = new Object[64][];
		for (int i=0; i<64; i++)
		{
			rowList[i] = new Object[]{toLetter(i), letterFreq[i]}; //build each row
		}
		/*
		 * setup table
		 */
		DefaultTableModel tableModel = new DefaultTableModel(rowList, new String[]{"Character", "Frequency"})
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		table.setModel(tableModel);
		table.setFont(f13);
		table.setGridColor(Color.BLACK);
		table.getTableHeader().setFont(f13);
		table.getTableHeader().setReorderingAllowed(false);
		table.setDragEnabled(false);
		table.setDefaultRenderer(Object.class, new CellRen());
		/*
		 * sorter
		 */
		table.setAutoCreateRowSorter(true);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>();
		sorter.setModel(tableModel);
		sorter.setComparator(0, new Comparator<String>()
		{
			@Override
			public int compare(String s1, String s2)
			{
				if (s1.length() > s2.length())
				{
					return 1;
				}
				else if (s2.length() > s1.length())
				{
					return -1;
				}
				else return Character.compare(s1.charAt(0),s2.charAt(0));
			}
		});
		sorter.setComparator(1, new Comparator<Integer>()
		{
			@Override
			public int compare(Integer i1, Integer i2)
			{
				return i1 - i2;
			}
		});
		table.setRowSorter(sorter);
		table.revalidate();
		/*
		 * add table to dialog
		 */
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	public static void showDialog(Frame w, String text)
	{
		CharCountDialog dialog = new CharCountDialog(w, text);
		dialog.setSize(280,450);
		dialog.setLocationRelativeTo(w);
		dialog.setVisible(true);
	}
	
	private class CellRen extends DefaultTableCellRenderer
	{
		CellRen()
		{
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			JLabel label = (JLabel)(super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column));
			String s = value.toString();
			if (isInteger(s)&&(column != 0))
			{
				label.setText(s + " (" + f3dp.format(Integer.parseInt(s)*100.0/CharCountDialog.this.text.length()) + "%)");
			}
			return label;
		}
		
		private boolean isInteger(String text)
		{
			try
			{
				Integer.parseInt(text);
				return true;
			}
			catch (NumberFormatException ex)
			{
				return false;
			}
		}
	}
}
