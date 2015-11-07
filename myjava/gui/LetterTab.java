/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.concurrent.*;

public class LetterTab extends JComponent implements ActionListener
{
	private static final Font LETTER_FONT = new Font("Microsoft Jhenghei", Font.PLAIN, 800);
	private static final BufferedImage TRANSPARENT_16 = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
	private static final Cursor TRANSPARENT_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(TRANSPARENT_16, new Point(0,0), "blank");
	private Color YELLOW; //239, 211, 59
	private String text;
	private int alpha = 0;
	private Timer timer;
	private CountDownLatch latch = new CountDownLatch(1);
	public LetterTab(String text)
	{
		super();
		this.text = text;
		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				Window win = SwingUtilities.windowForComponent(LetterTab.this);
				win.setVisible(false);
				win.dispose();
			}
		});
		this.setCursor(TRANSPARENT_CURSOR);
	}
	
	public LetterTab()
	{
		this(null);
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void play() throws InterruptedException
	{
		timer = new Timer(10,this);
		timer.start();
		latch.await();
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		if (alpha != 255)
		{
			alpha += 5;
			YELLOW = new Color(239,211,59,alpha);
			this.repaint();
		}
		else
		{
			timer.stop();
			latch.countDown();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		int width = this.getWidth();
		int height = this.getHeight();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0,0,width,height);
		/*
		 * calculate font size
		 */
		g2d.setFont(LETTER_FONT);
		FontMetrics metrics = g2d.getFontMetrics();
		int stringWidth = metrics.stringWidth(this.text);
		int stringHeight = metrics.getAscent()-metrics.getDescent();
		if (text.length() != 1)
		{
			//assume linear relationship
			float sizeByWidth = (width-50)*LETTER_FONT.getSize2D()/stringWidth;	
			float sizeByHeight = (height-50)*LETTER_FONT.getSize2D()/stringHeight;
			g2d.setFont(LETTER_FONT.deriveFont(Math.min(Math.min(sizeByWidth, sizeByHeight), 500)));
			//now recalculate width/height
			metrics = g2d.getFontMetrics();
			stringWidth = metrics.stringWidth(this.text);
			stringHeight = metrics.getAscent()-metrics.getDescent();
		}
		/*
		 * paint words
		 */
		g2d.setColor(YELLOW);
		g2d.drawString(this.text, (width-stringWidth)/2, (height+stringHeight)/2);
	}
}
