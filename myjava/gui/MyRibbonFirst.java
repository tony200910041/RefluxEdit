/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import myjava.gui.*;
import myjava.gui.common.*;
import exec.*;
import static exec.SourceManager.*;

public class MyRibbonFirst extends MyPureButton implements MouseListener, Resources, ColorConstants
{
	private static final MyRibbonFirst INSTANCE = new MyRibbonFirst(); //amend later
	private static final String LAF = UIManager.getLookAndFeel().getName().toLowerCase();
	private JDialog menuDialog;
	private MyRibbonFirst()
	{
		super("FILE");
		this.setPreferredSize(new Dimension(65,25));
		this.setBackground(new Color(154,192,229));
		this.setForeground(Color.WHITE);
		this.setFont(f13);
		this.setLabelForeground(Color.WHITE);
		this.addMouseListener(this);
	}
	
	public static MyRibbonFirst getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public void mousePressed(MouseEvent ev)
	{
	}
	
	@Override
	public void mouseReleased(MouseEvent ev)
	{
		GrayGlassPane.getInstance().setTransparent(false);
		/*
		 * dialog: shown after a button clicked
		 */
		menuDialog = new JDialog(RefluxEdit.getInstance(), null, true);
		menuDialog.setUndecorated(true);
		menuDialog.setLayout(new GridLayout(2,4,5,5));
		menuDialog.setBackground(gray);
		menuDialog.getRootPane().setBorder(raisedBorder);
		/*
		 * buttons:
		 */
		menuDialog.add(new MyItemButton("New File", "NEW80", -3));
		menuDialog.add(new MyItemButton("Open File", "OPEN80", -2));
		menuDialog.add(new MyItemButton("Save As", "SAVEAS80", 4));
		menuDialog.add(new MyItemButton("Save", "SAVE80", 5));
		menuDialog.add(new MyItemButton("Export to Image", "EXPORT80", 43));
		menuDialog.add(new MyItemButton("Print", "PRINT80", 38));
		menuDialog.add(new MyItemButton("Close", "CLOSE80", 6));
		menuDialog.add(new MyItemButton("Cancel", "TRANSPARENT80", -10));
		/*
		 * show dialog:
		 */
		menuDialog.pack();
		menuDialog.setLocationRelativeTo(menuDialog.getParent());			
		menuDialog.setVisible(true);
	}
	
	private class MyItemButton extends MyButton
	{
		private int x;
		public MyItemButton(String item, String icon, int x)
		{
			super(item);
			this.x = x; //x=-10: cancel, x=-2: open dialog, x=-3: new dialog
			if (icon != null)
			{
				this.setVerticalTextPosition(SwingConstants.BOTTOM);
				this.setHorizontalTextPosition(SwingConstants.CENTER);
				try
				{
					this.setIcon(icon(icon));
				}
				catch (Exception ex)
				{
					this.setIcon(null);
				}
			}
			if (x != -10)
			{
				this.setPreferredSize(new Dimension(110,110));
				this.addMouseListener(new MyListener(x));
			}
			if (LAF.contains("nimbus")||LAF.contains("windows"))
			{
				this.setFocusPainted(true);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
			this.mouseExited(ev);
			menuDialog.setVisible(false);
			if (x == -2)
			{
				LargeOpenDialog.showOpenDialog();
			}
			if (x == -3)
			{
				LargeNewDialog.showNewDialog();
			}
			else
			{
				GrayGlassPane.getInstance().setTransparent(true);
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
	}
	
	@Override
	public void mouseExited(MouseEvent ev)
	{
		this.setBackground(new Color(154,192,229));
	}
	
	@Override
	public void mouseEntered(MouseEvent ev)
	{
		this.setBackground(new Color(183,206,228));
	}
}
