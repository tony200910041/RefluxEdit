/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import myjava.gui.*;
import static myjava.gui.common.Resources.*;

public class ToolTipMessage extends JDialog implements MouseListener, WindowFocusListener, ActionListener
{
	private static final Color YELLOW = new Color(254,246,126);
	private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(3,6,3,12);
	private JTextPane textPane = new JTextPane();
	private Timer timer;
	private ToolTipMessage(Point p, String title, String message)
	{
		super();
		this.setUndecorated(true);
		this.setFocusableWindowState(false);
		this.getContentPane().setBackground(Color.BLACK);
		this.setOpacity(0.75f);
		/*
		 * components:
		 */
		this.setLayout(new BorderLayout());
		this.textPane.setFont(f13);
		this.textPane.setEditable(false);
		this.textPane.setFocusable(false);
		this.textPane.setHighlighter(null);
		this.textPane.setDragEnabled(false);
		this.textPane.setForeground(YELLOW);
		this.textPane.setBorder(EMPTY_BORDER);
		this.textPane.setOpaque(false);
		/*
		 * insert text
		 */
		DefaultStyledDocument doc = new DefaultStyledDocument();
		this.textPane.setDocument(doc);
		SimpleAttributeSet boldAttr = new SimpleAttributeSet();
		StyleConstants.setBold(boldAttr,true);
		StyleConstants.setFontSize(boldAttr,15);
		try
		{		
			doc.insertString(0, title+"\n", boldAttr);
			doc.insertString(doc.getLength(), message, null);
		}
		catch (BadLocationException ex)
		{
			//must succeed
		}
		this.add(textPane, BorderLayout.CENTER);
		/*
		 * size and location
		 * pack first
		 */
		this.pack();
		this.setShape(new RoundRectangle2D.Double(0,0,this.getWidth(),this.getHeight(),8,8));
		this.setLocation(p);
		this.addWindowFocusListener(this);
		this.textPane.addMouseListener(this);
		/*
		 * timer
		 */
		this.timer = new Timer(5000,this);
	}
	
	public static void showMessage(Point p, String title, String message)
	{
		ToolTipMessage ttm = new ToolTipMessage(p, title, message);
		ttm.setVisible(true);
		ttm.timer.restart();
	}
	
	public static void showMessage(int x, int y, String title, String message)
	{
		showMessage(new Point(x,y), title, message);
	}
	
	@Override
	public void mouseReleased(MouseEvent ev)
	{
		this.dispose();
	}
	
	@Override
	public void windowLostFocus(WindowEvent ev)
	{
		this.dispose();
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		this.dispose();
		this.timer.stop();
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
	public void mousePressed(MouseEvent ev)
	{
	}
	
	@Override
	public void windowGainedFocus(WindowEvent ev)
	{
	}
}
