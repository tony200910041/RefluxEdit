/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.Resources;

public class MyLabel extends JLabel implements Resources
{
	public MyLabel()
	{
		super();
		this.setFont(f13);
		this.setForeground(Color.BLACK);
	}
	
	public MyLabel(String str)
	{
		this();
		this.setText(str);
	}
}
