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
import java.util.*;
import exec.*;
import myjava.gui.common.*;
import myjava.gui.syntax.*;

public class MyUmbrellaLayerUI extends LayerUI<MyTextArea>
{
	/*
	 * static member
	 * affect all instances
	 */
	private static final Color TRANSPARENT_GRAY = new Color(0,0,0,150);
	private static final Color TRANSPARENT_YELLOW = new Color(255,254,208,150);
	private static final Font TEXT_FONT = new Font(Resources.f13.getName(),Font.PLAIN,65);
	private static boolean paintUmbrella, highlightSyntax, matchBracket;
	private static Color color;
	static
	{
		try
		{
			paintUmbrella = SourceManager.getBoolean0("textArea.umbrella.show");
			highlightSyntax = SourceManager.getBoolean0("syntax.highlight");
			matchBracket = SourceManager.getBoolean0("syntax.matchBrackets");
			color = new Color(251,231,51,Short.parseShort(SourceManager.getConfig0("textArea.umbrella.alpha")));
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
	
	public static void setHighlightingStatus(boolean highlightSyntax, boolean matchBracket)
	{
		boolean oldHighlightSyntax = MyUmbrellaLayerUI.highlightSyntax;
		boolean oldMatchBracket = MyUmbrellaLayerUI.matchBracket;
		MyUmbrellaLayerUI.highlightSyntax = highlightSyntax;
		MyUmbrellaLayerUI.matchBracket = matchBracket;
		if ((!oldHighlightSyntax)&&highlightSyntax)
		{
			//rehighlight
			for (Tab tab: MainPanel.getAllTab())
			{
				tab.getTextArea().reparse();
			}
		}
		else if (highlightSyntax&&(!oldMatchBracket)&&matchBracket)
		{
			//just update bracket matching information
			for (Tab tab: MainPanel.getAllTab())
			{
				tab.getTextArea().getFilter().matchBracket();
			}
		}
	}
	
	public static boolean isSyntaxHighlightingEnabled()
	{
		return MyUmbrellaLayerUI.highlightSyntax;
	}
	
	public static boolean isBracketMatchingEnabled()
	{
		return MyUmbrellaLayerUI.matchBracket;
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
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		Rectangle rect = c.getVisibleRect();
		int width = rect.width;
		int height = rect.height;
		if (highlightSyntax)
		{
			/*
			 * highlight syntax
			 */
			MyTextArea textArea = (MyTextArea)(((JLayer<?>)c).getView());
			Parser parser = textArea.getFilter().getParser();
			if (parser != null)
			{
				Font areaFont = textArea.getFont();
				FontMetrics areaFontMetrics = g2d.getFontMetrics(areaFont);
				int start = textArea.viewToModel(rect.getLocation());
				int end = textArea.viewToModel(new Point(rect.x+rect.width,rect.y+rect.height));
				int selectionStart = textArea.getSelectionStart();
				int selectionEnd = textArea.getSelectionEnd();
				Color backgroundColor = textArea.getBackground();
				Color selectionColor = textArea.getSelectionColor();
				Caret caret = textArea.getCaret();
				myjava.gui.syntax.Painter tokenPainter = myjava.gui.syntax.Painter.getCurrentInstance();
				g2d.setFont(areaFont);
				for (Token token: parser.getTokenSet(start,end-start))
				{
					int off = token.off();
					int len = token.length();
					Color tokenColor = tokenPainter.fromType(token.getType());
					try
					{
						for (int i=off; i<off+len; i++)
						{
							String _char = textArea.getDocument().getText(i,1);
							Rectangle _this = textArea.modelToView(i);
							Rectangle next = textArea.modelToView(i+1);
							g2d.setColor(((i>=selectionStart)&&(i<selectionEnd))?selectionColor:backgroundColor);
							g2d.fill(new Rectangle(_this.x,_this.y,next.x>_this.x?(next.x-_this.x):(areaFontMetrics.stringWidth(_char)),_this.height)); //char before line end
							g2d.setColor(tokenColor);
							g2d.drawString(_char,_this.x,_this.y+_this.height-areaFontMetrics.getDescent());
						}
					}
					catch (BadLocationException ex)
					{
						ex.printStackTrace();
					}
				}
				caret.paint(g2d);
				if (matchBracket)
				{
					BracketMatcher bracketMatcher = textArea.getFilter().getBracketMatcher();
					int effective = bracketMatcher.getEffectiveBracketLocation();
					int result = bracketMatcher.getResultLocation();
					try
					{
						if ((effective != -1)&&(!bracketMatcher.isMatching()))
						{
							if (result != -1)
							{
								//found
								Rectangle effectiveRect = textArea.modelToView(effective);
								Rectangle effectiveRect1 = textArea.modelToView(effective+1);
								Rectangle resultRect = textArea.modelToView(result);
								Rectangle resultRect1 = textArea.modelToView(result+1);
								g2d.setColor(tokenPainter.fromType(Token.Type.MATCHED_BRACKET));
								g2d.fill(new Rectangle(effectiveRect.x,effectiveRect.y,effectiveRect1.x-effectiveRect.x,effectiveRect.height));
								g2d.fill(new Rectangle(resultRect.x,resultRect.y,resultRect1.x-resultRect.x,resultRect.height));
							}
							else
							{
								//matching bracket not found
								Rectangle effectiveRect = textArea.modelToView(effective);
								Rectangle effectiveRect1 = textArea.modelToView(effective+1);
								g2d.setColor(tokenPainter.fromType(Token.Type.UNMATCHED_BRACKET));
								g2d.fill(new Rectangle(effectiveRect.x,effectiveRect.y,effectiveRect1.x-effectiveRect.x,effectiveRect.height));
							}
						}
					}
					catch (BadLocationException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		if (this.drop)
		{
			/*
			 * drag enter
			 */
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
		else if (paintUmbrella)
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
