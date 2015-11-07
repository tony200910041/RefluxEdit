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
	private JCheckBox insertRGB = new MyCheckBox("Insert RGB",false);
	private JCheckBox insertHSV = new MyCheckBox("Insert HSV",false);
	private JCheckBox insertHSL = new MyCheckBox("Insert HSL",false);
	private JCheckBox insertHEX = new MyCheckBox("Insert HEX",false);
	private JCheckBox insertCMYK = new MyCheckBox("Insert CMYK",false);
	private boolean isInsert = false;
	private ColorDialog(Frame parent)
	{
		super(parent,"Color chooser",true);
		this.setLayout(new BorderLayout());
		this.add(chooser, BorderLayout.CENTER);
		JPanel bottom = new JPanel(new GridLayout(2,1,0,0));
		JPanel bottom1 = new JPanel();
		bottom1.add(insertRGB);
		bottom1.add(insertHSV);
		bottom1.add(insertHSL);
		bottom1.add(insertHEX);
		bottom1.add(insertCMYK);
		insertCMYK.setToolTipText("***converted without calibration");
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
				buffer += (getRGBString(c)+" ");
			}
			if (dialog.insertHSV.isSelected())
			{
				buffer += (getHSVString(c)+" ");
			}
			if (dialog.insertHSL.isSelected())
			{
				buffer += (getHSLString(c)+" ");
			}
			if (dialog.insertHEX.isSelected())
			{
				buffer += (getHEXString(c)+" ");
			}
			if (dialog.insertCMYK.isSelected())
			{
				buffer += (getCMYKString(c)+" ");
			}
			if (buffer.endsWith(" "))
			{
				buffer = buffer.substring(0,buffer.length()-1);
			}
			return buffer;
		}
		else return "";
	}
	
	private static String getRGBString(Color c)
	{
		return "(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")";
	}
	
	private static String getHSVString(Color c)
	{
		//use self-implementation
		double r1 = c.getRed()/255d;
		double g1 = c.getGreen()/255d;
		double b1 = c.getBlue()/255d;
		double cmax = Math.max(Math.max(r1,g1),b1);
		double cmin = Math.min(Math.min(r1,g1),b1);
		double delta = cmax - cmin;
		//calculate h
		double h;
		if (delta == 0) h = 0;
		else if (cmax == r1) h = 60*(((g1-b1)/delta)%6);
		else if (cmax == g1) h = 60*((b1-r1)/delta+2);
		else h = 60*((r1-g1)/delta+4);
		while (h < 0) h += 360;
		//calculate s
		double s = cmax==0?0:delta/cmax;
		//return String
		return "(" + Math.round(h) + ", " + Math.round(s*100) + ", " + Math.round(cmax*100) + ")";
	}
	
	private static String getHSLString(Color c)
	{
		//use self-implementation
		double r1 = c.getRed()/255d;
		double g1 = c.getGreen()/255d;
		double b1 = c.getBlue()/255d;
		double cmax = Math.max(Math.max(r1,g1),b1);
		double cmin = Math.min(Math.min(r1,g1),b1);
		double delta = cmax - cmin;
		//calculate h
		double h;
		if (delta == 0) h = 0;
		else if (cmax == r1) h = 60*(((g1-b1)/delta)%6);
		else if (cmax == g1) h = 60*((b1-r1)/delta+2);
		else h = 60*((r1-g1)/delta+4);
		while (h < 0) h += 360;
		//calculate l
		double l = (cmax+cmin)/2;
		//calculate s
		double s = delta==0?0:(delta/(1-Math.abs(2*l-1)));
		//return String
		return "(" + Math.round(h) + ", " + Math.round(s*100) + ", " + Math.round(l*100) + ")";
	}
	
	private static String getHEXString(Color c)
	{
		return "#" + Integer.toHexString(c.getRGB()).substring(2);
	}
	
	private static String getCMYKString(Color color)
	{
		double r1 = color.getRed()/255d;
		double g1 = color.getGreen()/255d;
		double b1 = color.getBlue()/255d;
		//
		double k = 1-Math.max(Math.max(r1,g1),b1);
		double c = (1-r1-k)/(1-k);
		double m = (1-g1-k)/(1-k);
		double y = (1-b1-k)/(1-k);
		//
		return "(" + Math.round(c*255) + ", " + Math.round(m*255) + ", " + Math.round(y*255) + ", " + Math.round(k*255) + ")";
	}
}
