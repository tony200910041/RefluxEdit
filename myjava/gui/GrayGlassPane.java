package myjava.gui;

import java.awt.*;
import javax.swing.*;

public class GrayGlassPane extends JComponent
{
	private static final GrayGlassPane INSTANCE = new GrayGlassPane();
	private static final Color TRANSPARENT_GRAY = new Color(0,0,0,40);
	private boolean isTransparent = true;
	private GrayGlassPane()
	{
		super();
	}
	
	public static GrayGlassPane getInstance()
	{
		return INSTANCE;
	}
	
	public void setTransparent(boolean isTransparent)
	{
		this.isTransparent = isTransparent;
		this.setVisible(!isTransparent);
		this.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		if (!isTransparent)
		{
			g.setColor(TRANSPARENT_GRAY);
			g.fillRect(0,0,this.getWidth(),this.getHeight());
		}
	}
}
