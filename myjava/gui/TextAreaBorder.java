/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

public class TextAreaBorder implements Border
{
	private static final int EMPTY_WIDTH = 20;
	private static final Color COUNTER_BACKGROUND = new Color(207,207,206);
	public TextAreaBorder()
	{		
		super();
	}
	
	@Override
	public Insets getBorderInsets(Component c)
	{
		JTextArea textArea = (JTextArea)c;
		Font font = textArea.getFont();
		FontMetrics metrics = textArea.getFontMetrics(font);
		int width = metrics.stringWidth(String.valueOf(textArea.getLineCount()))+EMPTY_WIDTH;
		return new Insets(0,width,0,0);
	}
	
	@Override
	public boolean isBorderOpaque()
	{
		return true;
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		JTextArea textArea = (JTextArea)c;
		int lineCount = textArea.getLineCount();
		Graphics2D g2d = (Graphics2D)(g.create());
		g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		//setup font information
		Font font = textArea.getFont();
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);
		int stringWidth = metrics.stringWidth(String.valueOf(lineCount));
		int stringHeight = metrics.getAscent();
		//paint
		g2d.setColor(COUNTER_BACKGROUND);
		g2d.fillRect(x,y,stringWidth+EMPTY_WIDTH,height);
		g2d.setColor(Color.BLACK);
		for (int i=0; i<lineCount; i++)
		{
			try
			{
				Rectangle rect = textArea.modelToView(textArea.getLineStartOffset(i));
				g2d.drawString(String.valueOf(i+1),rect.x+2-stringWidth-EMPTY_WIDTH,rect.y+stringHeight);
			}
			catch (BadLocationException ex)
			{
				throw new InternalError();
			}
		}
		g2d.dispose();
	}
}
