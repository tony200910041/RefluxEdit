/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.border.Border;

public class MyRibbonBorder implements Border
{
	public static final int SELECTED = 0;
	public static final int UNSELECTED = 1;
	private int type;
	public MyRibbonBorder(int type)
	{
		this.type = type;
	}
	
	@Override
	public Insets getBorderInsets(Component c)
	{
		return new Insets(0,0,0,0);
	}
	
	@Override
	public boolean isBorderOpaque()
	{
		return true;
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		g.setColor(Color.BLACK);
		if (this.type == SELECTED)
		{
			g.setColor(Color.BLACK);
			g.drawLine(x,y,x+width,y);
			g.drawLine(x,y,x,y+height);
			g.drawLine(x+width-1,y,x+width-1,y+height);
		}
		else if (this.type == UNSELECTED)
		{
			g.drawLine(x,y+height-1,x+width,y+height-1);
		}
	}
}
