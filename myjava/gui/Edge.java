package myjava.gui;

import javax.swing.*;

public class Edge extends JLabel
{
	//type:
	public static final int WIDE = 0;
	public static final int NARROW = 1;
	//
	public static JLabel createEdge(int type)
	{
		if (type == WIDE)
		{
			return new JLabel("  ");
		}
		else if (type == NARROW)
		{
			return new JLabel(" ");
		}
		else throw new IllegalArgumentException("must be Edge.WIDE or Edge.NARROW");
	}
}
