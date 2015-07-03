package myjava.gui;

/**
 * Requires the following classes to work:
 * myjava.gui.MyPureButton
 * myjava.gui.MyRibbonBorder
 * myjava.gui.common.Resources
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import myjava.gui.*;
import myjava.gui.common.Resources;

public class MyRibbonPanel extends JPanel implements MouseListener
{
	private static final Color LIGHT_GRAY = new Color(238,238,238);
	private HashMap<MyPureButton, JPanel> list = new HashMap<MyPureButton, JPanel>();
	/*
	 * gapPanel: create initial gap
	 */
	private JPanel gapPanel = new JPanel();
	/*
	 * buttonPanel: the panel containing tab name
	 */
	private ButtonPanel buttonPanel = new ButtonPanel();
	/*
	 * contentPanel: the panel containing the current showing tab
	 */
	private JPanel contentPanel = new JPanel(new BorderLayout());
	/*
	 * linePanel: contains a horizontal line
	 */
	private LinePanel linePanel = new LinePanel();
	public MyRibbonPanel()
	{
		super();
		this.setLayout(new BorderLayout());
		//
		JPanel topPanel = new JPanel(new BorderLayout());
		gapPanel.setPreferredSize(new Dimension(0,3));
		topPanel.add(gapPanel, BorderLayout.PAGE_START);
		topPanel.add(buttonPanel, BorderLayout.PAGE_END);
		this.add(topPanel, BorderLayout.PAGE_START);
		//
		this.add(contentPanel, BorderLayout.CENTER);
		this.add(linePanel, BorderLayout.PAGE_END);
	}
		
	public void addTab(String name, JPanel panel)
	{
		MyPureTabButton button = new MyPureTabButton(name);
		buttonPanel.add(button);
		list.put(button, panel);
		button.addMouseListener(this);
		if (list.size() == 1)
		{
			//initialize, so that contentPanel contains the first panel
			button.setBorder(new MyRibbonBorder(MyRibbonBorder.SELECTED));
			button.setBackground(Color.WHITE);
			contentPanel.add(panel, BorderLayout.CENTER);			
		}
		this.revalidate();
		this.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent ev)
	{
		MyPureButton button = (MyPureButton)(ev.getSource());
		for (MyPureButton b: list.keySet())
		{
			b.setBorder(new MyRibbonBorder(MyRibbonBorder.UNSELECTED));
			b.setBackground(LIGHT_GRAY);			
			button.setBorder(new MyRibbonBorder(MyRibbonBorder.SELECTED));
			button.setBackground(Color.WHITE);
		}
		/*
		 * change current showing tab
		 */
		contentPanel.removeAll();
		contentPanel.add(list.get(button), BorderLayout.CENTER);
		this.revalidate();
		this.repaint();
	}
	
	public void addAsFirstComponent(Component firstButton)
	{
		Component[] comp = buttonPanel.getComponents();
		buttonPanel.removeAll();
		firstButton.setPreferredSize(new Dimension(65,25));
		buttonPanel.add(firstButton);
		for (Component c: comp)
		{
			buttonPanel.add(c);
		}
		buttonPanel.revalidate();
		buttonPanel.repaint();/*
		firstButton.setPreferredSize(new Dimension(65,25));
		buttonPanel.add(firstButton,0);*/
	}
	
	private class ButtonPanel extends JPanel
	{
		ButtonPanel()
		{
			super();
			this.setBackground(LIGHT_GRAY);
			this.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		}
		
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.drawLine(0,this.getHeight()-1,this.getWidth(),this.getHeight()-1);
		}
	}
	
	private class LinePanel extends JPanel
	{
		LinePanel()
		{
			super();
			this.setPreferredSize(new Dimension(0,1));
		}
		
		@Override
		protected void paintComponent(Graphics g)
		{
			/*
			 * draw horizontal line
			 */
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.drawLine(0,0,this.getWidth(),0);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent ev)
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
	public void mouseClicked(MouseEvent ev)
	{
	}
}
