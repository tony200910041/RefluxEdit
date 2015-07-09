/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;

public class MyPanel extends JPanel
{
	public static final int LEFT = FlowLayout.LEFT;
	public static final int CENTER = FlowLayout.CENTER;
	public static final int RIGHT = FlowLayout.RIGHT;
	public MyPanel(int x)
	{
		super();
		this.setLayout(new FlowLayout(x));
		this.setBackground(Color.WHITE);
	}
	
	public static MyPanel wrap(Component... c)
	{
		MyPanel panel = new MyPanel(LEFT);
		for (Component component: c)
		{
			panel.add(component);
		}
		return panel;
	}
}
