/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.*;

public class MyMenu extends JMenu implements Resources
{
	public MyMenu(String str)
	{
		super(str);
		this.setFont(f13);
		this.setForeground(Color.BLACK);
		this.setBackground(Color.WHITE);
		this.setOpaque(false);
	}
}
