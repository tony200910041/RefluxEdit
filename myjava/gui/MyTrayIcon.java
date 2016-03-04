/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

public class MyTrayIcon extends TrayIcon implements MouseListener, WindowFocusListener
{
	private JDialog hiddenDialog = new JDialog();
	private JPopupMenu popup;
	public MyTrayIcon(Image image, String tooltip, JPopupMenu popup)
	{
		super(image,tooltip);
		this.popup = popup;
		this.hiddenDialog.setSize(0,0);
		this.hiddenDialog.setUndecorated(true);
		this.hiddenDialog.addWindowFocusListener(this);
		this.addMouseListener(this);
	}
	
	@Override
	public void windowLostFocus(WindowEvent ev)
	{
		hiddenDialog.setVisible(false);
		popup.setVisible(false);
	}
	
	@Override
	public void mouseReleased(MouseEvent ev)
	{
		maybeShowPopup(ev);
	}
		
	@Override
	public void mousePressed(MouseEvent ev)
	{
		maybeShowPopup(ev);
	}
	
	private void maybeShowPopup(MouseEvent ev)
	{
		if (ev.isPopupTrigger())
		{
			popup.setInvoker(hiddenDialog);
			hiddenDialog.setVisible(true);
			popup.show(hiddenDialog,ev.getXOnScreen(),ev.getYOnScreen()-popup.getPreferredSize().height);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
	}
	
	@Override
	public void mouseEntered(MouseEvent ev)
	{
	}
	
	@Override
	public void mouseExited(MouseEvent ev)
	{
	}
	
	@Override
	public void windowGainedFocus(WindowEvent ev)
	{
	}
}
