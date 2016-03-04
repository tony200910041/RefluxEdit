/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.statusbar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import myjava.gui.*;

public abstract class StatusComponent extends JPanel implements MouseListener
{
	protected Tab tab;
	private Color original;
	private boolean isComponentSet = false;
	public StatusComponent(Tab tab)
	{
		super();
		this.tab = tab;
		this.setLayout(new FlowLayout(FlowLayout.CENTER,8,2));
		this.addMouseListener(this);
	}
	
	public void setComponent(JComponent c)
	{
		if (!isComponentSet)
		{
			c.addMouseListener(this);
			this.add(c);
			isComponentSet = true;
		}
		else throw new IllegalStateException("JComponent already set");
	}
	
	public boolean fillHorizontal()
	{
		return false;
	}
	
	public void update()
	{
		//empty by default
	}
	
	public void postCaretUpdate()
	{
		//empty by default
	}
	
	public void postDocumentUpdate()
	{
		//empty by default
	}
	
	private static Color slightlyDarker(Color c)
	{
		int x = 25;
		return new Color(Math.max(0,c.getRed()-x),Math.max(0,c.getGreen()-x),Math.max(0,c.getBlue()-x));
	}
	
	@Override
	public void mouseEntered(MouseEvent ev)
	{
		this.original = this.getBackground();
		this.setBackground(slightlyDarker(this.original));
	}
	
	@Override
	public void mouseExited(MouseEvent ev)
	{
		this.setBackground(this.original);
	}
	
	@Override
	public void mouseReleased(MouseEvent ev) {}
	
	@Override
	public void mousePressed(MouseEvent ev) {}
	
	@Override
	public void mouseClicked(MouseEvent ev) {}
}
