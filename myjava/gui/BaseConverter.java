/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.text.*;
import java.math.*;
import myjava.gui.*;
import myjava.gui.common.*;

public class BaseConverter extends JDialog implements DocumentListener, ChangeListener, Runnable, Resources
{
	private static final Font f15 = f13.deriveFont(15f);
	private static final Color RED = new Color(255,133,133);
	private MyCheckBox checkBox1 = new MyCheckBox("From base:",false);
	private MyCheckBox checkBox2 = new MyCheckBox("To base:",false);
	private JSpinner base1 = new JSpinner(new SpinnerNumberModel(10,2,36,1));
	private JSpinner base2 = new JSpinner(new SpinnerNumberModel(2,2,36,1));
	private JTextField tf1 = new JTextField();
	private JTextField tf2 = new JTextField();		
	private BaseConverter(Frame parent)
	{
		super(parent,"Base converter",true);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(2,2,2,2);
		c.anchor = GridBagConstraints.LINE_START;
		this.add(checkBox1,c);
		//
		c.gridx = 1;
		base1.setFont(f15);
		base1.addChangeListener(this);
		this.add(base1,c);
		//
		c.gridx = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		tf1.setFont(f15);
		tf1.setPreferredSize(new Dimension(300,26));
		tf1.setBackground(Color.WHITE);
		tf1.getDocument().addDocumentListener(this);
		tf1.setDragEnabled(true);
		this.add(tf1,c);
		//
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		this.add(checkBox2,c);
		//
		c.gridx = 1;
		base2.setFont(f15);
		base2.addChangeListener(this);
		this.add(base2,c);
		//
		c.gridx = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		tf2.setFont(f15);
		tf2.setPreferredSize(new Dimension(300,26));
		tf2.setBackground(Color.WHITE);
		tf2.setEditable(false);
		tf2.setDragEnabled(true);
		this.add(tf2,c);
	}
	
	@Override
	public void changedUpdate(DocumentEvent ev)
	{
		update();
	}
	
	@Override
	public void insertUpdate(DocumentEvent ev)
	{
		update();
	}
	
	@Override
	public void removeUpdate(DocumentEvent ev)
	{
		update();
	}
	
	@Override
	public void stateChanged(ChangeEvent ev)
	{
		update();
	}
	
	void update()
	{
		Thread thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run()
	{
		/*
		 * not EDT, so invokeLater
		 */
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				String input = tf1.getText();
				if ((input != null)&&(!input.isEmpty()))
				{
					try
					{
						base1.commitEdit();
					}
					catch (ParseException ex)
					{
					}
					try
					{
						base2.commitEdit();
					}
					catch (ParseException ex)
					{
					}
					tf1.getDocument().removeDocumentListener(BaseConverter.this);
					try
					{
						int from = Integer.parseInt(base1.getValue().toString());
						int to = Integer.parseInt(base2.getValue().toString());
						tf2.setText(convert(input,from,to));
						tf1.setBackground(Color.WHITE);
						tf2.setBackground(Color.WHITE);
					}
					catch (Exception ex)
					{
						tf1.setBackground(RED);
					}
					finally
					{
						tf1.getDocument().addDocumentListener(BaseConverter.this);
					}
				}
				else
				{
					tf1.setBackground(Color.WHITE);
					tf2.setText("");
				}
			}
		});
	}
	
	private BigInteger toDenary(String number, int base)
	{
		return new BigInteger(number, base);
	}
	
	private String toBase(BigInteger denary, int base)
	{
		return denary.toString(base).toUpperCase();
	}
	
	private String convert(String number, int from, int to)
	{
		
		return toBase(toDenary(number, from), to);
	}
	
	private void checkRange(int x)
	{
		if ((x < 2)||(x > 36)) throw new IllegalArgumentException();
	}
	
	public static String showDialog(Frame parent)
	{
		BaseConverter converter = new BaseConverter(parent);
		converter.pack();
		converter.setLocationRelativeTo(parent);
		converter.setVisible(true);
		/*
		 * closed
		 */
		String result = "";
		if (converter.checkBox1.isSelected())
		{
			String denary = converter.tf1.getText();
			if (!denary.isEmpty())
			{
				result += denary + " ";
			}
		}
		if (converter.checkBox2.isSelected())
		{
			String otherBase = converter.tf2.getText();
			if (!otherBase.isEmpty())
			{
				result += otherBase + " ";
			}
		}
		return result.trim();
	}
}
