/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

/**
 * Requires myjava.gui.common.Resources to work
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import myjava.gui.common.*;

public class MyFontChooser extends JPanel implements ActionListener, ChangeListener, Resources
{
	private static final Font[] usableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	protected Font selectedFont;	
	protected JComboBox<String> comboBox = new JComboBox<>();
	protected JSpinner spinner = new JSpinner(new SpinnerNumberModel(12, 1, 200, 1));
	protected MyRadioButton plain = new MyRadioButton("Plain", false, 1);
	protected MyRadioButton bold = new MyRadioButton("Bold", false, 2);
	protected MyRadioButton italic = new MyRadioButton("Italic", false, 3);	
	public MyFontChooser(Font f)
	{
		super();
		this.selectedFont = f;
		this.setBackground(Color.WHITE);
		this.setLayout(new FlowLayout());
		/*
		 * setup JComboBox
		 */
		for (Font font: usableFonts)
		{
			comboBox.addItem(font.getFontName());
		}
		comboBox.setFont(f13);
		comboBox.setSelectedItem(f.getFontName());
		comboBox.setBackground(Color.WHITE);
		comboBox.addActionListener(this);
		this.add(comboBox);
		/*
		 * setup JSpinner
		 */
		int size = f.getSize();
		if ((size>=1)||(size<=200))
		{
			spinner.setValue(size);
		}
		spinner.setFont(new Font("Microsoft Jhenghei", Font.PLAIN, 14));
		spinner.addChangeListener(this);
		this.add(spinner);
		/*
		 * setup JRadioButton
		 */
		switch (f.getStyle())
		{
			case 0: //plain
			plain.setSelected(true);
			break;
			
			case 1: //bold
			bold.setSelected(true);
			break;
			
			case 2: //italic
			italic.setSelected(true);
			break;
		}
		this.add(plain);
		this.add(bold);
		this.add(italic);
	}
	
	class MyRadioButton extends JRadioButton implements ActionListener
	{
		private int x;
		public MyRadioButton(String str, boolean isSelected, int x)
		{
			super(str, isSelected);
			this.setFont(f13);
			this.setOpaque(false);
			this.setFocusable(false);
			this.x = x;
			this.addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent ev)
		{
			switch (x)
			{
				case 1: //plain
				plain.setSelected(true);
				bold.setSelected(false);
				italic.setSelected(false);
				break;
				
				case 2: //bold
				plain.setSelected(false);
				bold.setSelected(true);
				italic.setSelected(false);
				break;
				
				case 3: //italic
				plain.setSelected(false);
				bold.setSelected(false);
				italic.setSelected(true);
				break;
			}
			userInput();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		userInput();
	}
	
	@Override
	public void stateChanged(ChangeEvent ev)
	{
		userInput();
	}
	
	public void userInput()
	{
		int style = 0;
		if (bold.isSelected()) style = 1;
		if (italic.isSelected()) style = 2;
		try
		{
			spinner.commitEdit();
		}
		catch (java.text.ParseException ex)
		{
		}
		selectedFont = new Font(comboBox.getSelectedItem().toString(), style, Integer.parseInt(spinner.getValue().toString()));
	}
	
	public Font getFont()
	{
		return this.selectedFont;
	}
	
	public JSpinner getSpinner()
	{
		return this.spinner;
	}
}
