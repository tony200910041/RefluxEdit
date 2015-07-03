package myjava.gui;

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.*;

public class RandomProgress extends JDialog implements Resources
{
	private JProgressBar prog;
	private double initialTime;
	public RandomProgress(Window parent, int min, int max)
	{
		/*
		 * modal: false, otherwise will block EDT
		 */
		super(parent,"Progress");
		this.setModal(false);
		this.setLayout(new FlowLayout());
		this.prog = new JProgressBar(min, max);
		this.prog.setFont(f13);
		this.prog.setString("Please wait...");
		this.prog.setStringPainted(true);
		this.add(prog);
		this.pack();
		this.setLocationRelativeTo(parent);
		this.setResizable(false);
		this.setVisible(true);
		this.initialTime = System.currentTimeMillis();
	}
	
	public RandomProgress(Window parent)
	{
		super(parent);
		this.setModal(false);
		this.setTitle("Progress");
		this.setLayout(new FlowLayout());
		this.prog = new JProgressBar();
		this.prog.setIndeterminate(true);
		this.prog.setFont(f13);
		this.prog.setString("Please wait...");
		this.prog.setStringPainted(true);
		this.add(prog);
		this.pack();
		this.setLocationRelativeTo(parent);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public void setValue(int x)
	{
		this.prog.setValue(x);
	}
	
	public void setRange(int start, int end)
	{
		this.prog.setMinimum(start);
		this.prog.setMaximum(end);
	}
	
	public double timeUsed()
	{
		return (System.currentTimeMillis() - this.initialTime)/1000.0;
	}
	
	public int getValue()
	{
		return this.prog.getValue();
	}
	
	public void setString(String s)
	{
		this.prog.setString(s);
	}
}
