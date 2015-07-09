/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.text.*;
import exec.*;
import myjava.gui.common.*;

public class MyUmbrellaLayerUI extends LayerUI<MyTextArea>
{
	/*
	 * static member
	 * affect all instances
	 */
	private static final Color TRANSPARENT_GRAY = new Color(0,0,0,150);
	private static final Color TRANSPARENT_YELLOW = new Color(255,254,208,150);
	private static final Font TEXT_FONT = new Font(Resources.f13.getName(),Font.PLAIN,65);
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
	/*
	 * instance fields
	 */
	private boolean drop = false;
	/*
	 * constructor
	 */
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
	
	public void setDrop(boolean drop)
	{
		this.drop = drop;
	}
	
	@Override
	public void paint(Graphics g, JComponent c)
	{
		super.paint(g,c);
		Graphics2D g2d = (Graphics2D)g;
		Rectangle rect = c.getVisibleRect();
		int width = rect.width;
		int height = rect.height;
		if (this.drop)
		{
			/*
			 * drag enter
			 */		
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setColor(TRANSPARENT_GRAY);
			g2d.fillRect(rect.x,rect.y,width,height);
			/*
			 * draw the word "Drag"
			 */
			String text = "Drop here";
			g2d.setColor(TRANSPARENT_YELLOW);
			g2d.setFont(TEXT_FONT);
			FontMetrics metrics = g2d.getFontMetrics();
			int stringWidth = metrics.stringWidth(text);
			int stringHeight = metrics.getAscent()-metrics.getDescent();
			/*
			 * now calculate location
			 */
			g2d.drawString(text,rect.x+(width-stringWidth)/2,rect.y+(height+stringHeight)/2);
		}
		else if (this.paintUmbrella)
		{
			/*
			 * paint umbrella
			 */
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
