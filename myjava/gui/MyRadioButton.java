/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

/**
 * Requires the following classes to work:
 * myjava.gui.common.Resources
 */

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.Resources;

public class MyRadioButton extends JRadioButton implements Resources
{	
	public MyRadioButton(String str, boolean isSelected)
	{
		super(str, isSelected);
		this.setFont(f13);
		this.setOpaque(false);
		this.setFocusPainted(false);
	}
	
	public MyRadioButton(String str)
	{
		this(str,false);
	}
}
