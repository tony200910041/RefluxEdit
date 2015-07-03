package myjava.gui;

import java.awt.*;
import javax.swing.*;
import myjava.gui.MyPureButton;
import myjava.gui.MyRibbonBorder;
import myjava.gui.common.Resources;

public class MyPureTabButton extends MyPureButton implements Resources
{
	private JLabel label = new JLabel();
	public MyPureTabButton(String text)
	{
		super(text);
		this.setBorder(new MyRibbonBorder(MyRibbonBorder.UNSELECTED));
	}
}
