package myjava.gui;

/**
 * Requires the following classes to work:
 * myjava.gui.MyPureButton
 * myjava.gui.MyRibbonBorder
 * myjava.gui.common.Resources
 */

import java.awt.*;
import javax.swing.*;
import myjava.gui.MyPureButton;
import myjava.gui.MyRibbonBorder;
import myjava.gui.common.Resources;

public class MyPureTabButton extends MyPureButton implements Resources
{
	public MyPureTabButton(String text)
	{
		super(text);
		this.setBorder(new MyRibbonBorder(MyRibbonBorder.UNSELECTED));
	}
}
