/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

/**
 * Requires the following classes to work:
 * myjava.gui.MyPureButton
 * myjava.gui.MyRibbonBorder
 * myjava.gui.common.Resources
 */

import java.awt.*;
import javax.swing.*;
import myjava.gui.MyPureButton;
import myjava.gui.MyRibbonBorder;
import myjava.gui.common.Resources;

public class MyPureTabButton extends MyPureButton implements Resources
{
	public MyPureTabButton(String text)
	{
		super(text);
		this.setBorder(new MyRibbonBorder(MyRibbonBorder.UNSELECTED));
	}
}
