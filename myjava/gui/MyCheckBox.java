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

public class MyCheckBox extends JCheckBox
{
	public MyCheckBox(String str, boolean isSelected)
	{
		super(str, isSelected);
		this.setFont(Resources.f13);
		this.setFocusPainted(false);
		this.setOpaque(false);
	}
}
