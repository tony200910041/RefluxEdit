package myjava.gui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.*;
import exec.*;

public class MyUmbrellaLayerUI extends LayerUI<JTextArea>
{
	private static boolean paintUmbrella;
	private static Color color;
	static
	{
		try
		{
			paintUmbrella = SourceManager.getBoolean0("showUmbrella");
			color = new Color(251,231,51,Short.parseShort(SourceManager.getConfig0("Umbrella.alpha")));
		}
		catch (Exception ex)
		{
			color = new Color(251,231,51,60);
		}
	}
	
	public MyUmbrellaLayerUI()
	{
		super();
	}
	
	public static void setPaintUmbrella(boolean paintUmbrella)
	{
		MyUmbrellaLayerUI.paintUmbrella = paintUmbrella;
	}
	
	public static boolean isPaintUmbrella()
	{
		return paintUmbrella;
	}
	
	public static void setUmbrellaColor(Color color)
	{
		MyUmbrellaLayerUI.color = color;
	}
	
	public static Color getUmbrellaColor()
	{
		return color;
	}
	
	@Override
	public void paint(Graphics g, JComponent c)
	{
		super.paint(g,c);
		if (this.paintUmbrella)
		{
			Graphics2D g2d = (Graphics2D)g;
			Rectangle rect = c.getVisibleRect();
			int width = rect.width;
			int height = rect.height;
			int x = rect.x;
			int y = rect.y;
			int radius = (int)((Math.min(width,height)-20)/2.43);
			int edge = radius/7;
			if (edge%2 != 0) edge++;			
			//umbrella "top part"
			g2d.setColor(color);
			g2d.fill(new Arc2D.Double(width/2-radius+x,height/2-radius+y,2*radius,2*radius,180,-180,Arc2D.PIE));
			//umbrella "top point"
			g2d.fillRect(width/2-edge/2+x,height/2-radius-edge+y,edge,edge+1);
			//umbrella "stick"
			g2d.fillRect(width/2-edge/2+x,height/2+y,edge,radius);
			Arc2D.Double bottom = new Arc2D.Double(width/2.0-2.5*edge+x,height/2.0+radius-3*edge/2.0+y,3*edge,3*edge,180,180,Arc2D.PIE);
			Arc2D.Double removing = new Arc2D.Double(width/2.0-1.5*edge+x,height/2.0+radius-edge/2.0+y,edge,edge,180,180,Arc2D.PIE);
			Area area = new Area(bottom);
			area.subtract(new Area(removing));
			g2d.fill(area);
		}
	}
}
