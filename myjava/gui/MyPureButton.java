/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

/**
 * Requires myjava.gui.common.Resources to work
 */

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.Resources;

public class MyPureButton extends JPanel implements Resources
{
	private JLabel label = new JLabel();
	public MyPureButton(String text)
	{
		super();
		this.setLayout(new GridBagLayout());
		this.setBackground(new Color(238,238,238));
		this.setPreferredSize(new Dimension(65,25));
		label.setText(text);
		label.setFont(f13);
		this.add(label);
		this.setBorder(bord1);
	}
	
	public void setAlignLeft()
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
	}
	
	public void setLabelForeground(Color c)
	{
		this.label.setForeground(c);
	}
	
	public void setLabelFont(Font f)
	{
		this.label.setFont(f);
	}
}
