/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class MyButtonGroup extends ButtonGroup
{
	public MyButtonGroup(JRadioButton... buttons)
	{
		super();
		for (JRadioButton button: buttons)
		{
			this.add(button);
		}
	}
	
	public JRadioButton getSelected()
	{
		for (Enumeration<AbstractButton> e = this.getElements(); e.hasMoreElements();)
		{
			JRadioButton button = (JRadioButton)(e.nextElement());
			if (button.isSelected()) return button;
		}
		return null;
	}
}
