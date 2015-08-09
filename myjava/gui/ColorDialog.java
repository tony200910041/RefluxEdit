/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import myjava.gui.common.*;

public class ColorDialog extends JDialog implements ActionListener
{
	private JColorChooser chooser = new JColorChooser(Color.WHITE);
	private MyCheckBox insertRGB = new MyCheckBox("Insert RGB",false);
	private MyCheckBox insertHSB = new MyCheckBox("Insert HSB",false);
	private MyCheckBox insertHEX = new MyCheckBox("Insert HEX",false);
	private boolean isInsert = false;
	private ColorDialog(Frame parent)
	{
		super(parent,"Color chooser",true);
		this.setLayout(new BorderLayout());
		this.add(chooser, BorderLayout.CENTER);
		JPanel bottom = new JPanel(new GridLayout(2,1,0,0));
		JPanel bottom1 = new JPanel();
		bottom1.add(insertRGB);
		bottom1.add(insertHSB);
		bottom1.add(insertHEX);
		bottom.add(bottom1);
		JPanel bottom2 = new JPanel();
		MyButton ok = new MyButton("OK");
		ok.addActionListener(this);
		MyButton cancel = new MyButton("Cancel");
		cancel.addActionListener(this);
		MyButton reset = new MyButton("Reset");
		reset.addActionListener(this);
		bottom2.add(ok);
		bottom2.add(cancel);
		bottom2.add(reset);
		bottom.add(bottom2);
		this.add(bottom, BorderLayout.PAGE_END);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		switch (((JButton)(ev.getSource())).getText())
		{
			case "OK":
			this.setVisible(false);
			isInsert = true;
			break;
			
			case "Cancel":
			this.setVisible(false);
			isInsert = false;
			break;
			
			case "Reset":
			this.remove(chooser);
			chooser = new JColorChooser(Color.WHITE);			
			this.add(chooser, BorderLayout.CENTER);
			this.revalidate();
			this.repaint();
			break;
		}
	}
	
	public static String showColorDialog(Frame parent)
	{
		ColorDialog dialog = new ColorDialog(parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		if (dialog.isInsert)
		{
			Color c = dialog.chooser.getColor();
			String buffer = "";
			if (dialog.insertRGB.isSelected())
			{
				buffer += (getRGBString(c) + " ");
			}
			if (dialog.insertHSB.isSelected())
			{
				buffer += (getHSBString(c) + " ");
			}
			if (dialog.insertHEX.isSelected())
			{
				buffer += (getHEXString(c) + " ");
			}
			if (buffer.endsWith(" "))
			{
				buffer = buffer.substring(0, buffer.length()-1);
			}
			return buffer;
		}
		else return "";
	}
	
	private static String getRGBString(Color c)
	{
		return "(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")";
	}
	
	private static String getHSBString(Color c)
	{
		float[] hsbvals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		return "(" + hsbvals[0] + ", " + hsbvals[1] + ", " + hsbvals[2] + ")";
	}
	
	private static String getHEXString(Color c)
	{
		return "#" + Integer.toHexString(c.getRGB()).substring(2);
	}
}
