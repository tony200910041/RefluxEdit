/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;

public class EmptyIcon implements Icon
{
	private static final EmptyIcon INSTANCE = new EmptyIcon();
	private EmptyIcon()
	{
		super();
	}
	
	public static EmptyIcon getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public int getIconWidth()
	{
		return 16;
	}
	
	@Override
	public int getIconHeight()
	{
		return 16;
	}
	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
	}
}
