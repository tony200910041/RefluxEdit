package myjava.gui;

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
	private JPanel gapPanel = new JPanel();
	private JPanel buttonPanel = new ButtonPanel();
	private JPanel contentPanel;
	public MyRibbonPanel()
	{
		super();
		this.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel(new BorderLayout());
		gapPanel.setPreferredSize(new Dimension(0,3));
		topPanel.add(gapPanel, BorderLayout.PAGE_START);
		topPanel.add(buttonPanel, BorderLayout.PAGE_END);
		this.add(topPanel, BorderLayout.PAGE_START);
	}
		
	public void addTab(String name, JPanel panel)
	{
		MyPureButton button = new MyPureButton(name);
		buttonPanel.add(button);
		list.put(button, panel);
		button.addMouseListener(this);
		if (list.size() == 1)
		{
			//initialize
			button.setBorder(new MyRibbonBorder(MyRibbonBorder.SELECTED));
			button.setBackground(Color.WHITE);
			contentPanel = panel;
			this.add(panel, BorderLayout.CENTER);
		}
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
		if (contentPanel != null) this.remove(contentPanel);
		contentPanel = list.get(button);
		this.add(contentPanel, BorderLayout.CENTER);
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
		buttonPanel.repaint();
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
