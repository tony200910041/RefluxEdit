package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;

public class MyToolBarUI extends BasicToolBarUI
{
	private boolean forceStopFloating = false;
	MyToolBarUI()
	{
		super();
	}
	
	@Override
	public boolean isFloating()
	{
		if (forceStopFloating) return false;
		else return super.isFloating();
	}
	
	public void stopFloating()
	{
		this.forceStopFloating = true;
	}
}
