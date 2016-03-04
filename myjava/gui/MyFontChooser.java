/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.util.*;
import java.text.*;
import myjava.gui.common.*;

public final class MyFontChooser extends JDialog implements ActionListener, ChangeListener, Resources
{
	private JComboBox<String> fontComboBox = new JComboBox<>(getAllFonts());
	private JComboBox<Style> styleComboBox = new JComboBox<>(Style.values());
	private JSpinner fontSizeSpinner = new JSpinner(new SpinnerNumberModel(12,1,200,1));
	private JTextArea previewArea = new JTextArea(getTestString());
	private RawFont originalFont;
	private boolean isOK = false;
	@SuppressWarnings("unchecked")
	private MyFontChooser(Window window, RawFont font)
	{
		super(window,"Font chooser",Dialog.ModalityType.APPLICATION_MODAL);
		this.setLayout(new BorderLayout());
		this.getContentPane().setBackground(Color.WHITE);
		this.add(MyPanel.wrap(fontComboBox,styleComboBox,fontSizeSpinner), BorderLayout.PAGE_START);
		this.add(new JScrollPane(new JLayer<JTextArea>(previewArea,new PreviewLayerUI())), BorderLayout.CENTER);
		this.add(MyPanel.wrap(MyPanel.CENTER,new MyButton("OK")
		{
			{
				MyFontChooser.this.getRootPane().setDefaultButton(this);
			}
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				MyFontChooser.this.isOK = true;
				MyFontChooser.this.setVisible(false);
			}
		}, new MyButton("Cancel")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				MyFontChooser.this.isOK = false;
				MyFontChooser.this.setVisible(false);
			}
		}), BorderLayout.PAGE_END);
		//
		this.originalFont = font;
		this.setSelectedFont(font);
		fontComboBox.addActionListener(this);
		styleComboBox.addActionListener(this);
		fontSizeSpinner.addChangeListener(this);
	}
	
	private void setSelectedFont(RawFont font)
	{
		fontComboBox.setSelectedItem(font.getName());
		styleComboBox.setSelectedItem(font.getStyle());
		fontSizeSpinner.setValue(font.getSize());
		previewArea.setFont(font.toFont());
	}
	
	private RawFont getSelectedFont()
	{
		try
		{
			fontSizeSpinner.commitEdit();
		}
		catch (ParseException ex)
		{
			//pass
		}
		return new RawFont((String)(fontComboBox.getSelectedItem()),(Style)(styleComboBox.getSelectedItem()),Integer.parseInt(fontSizeSpinner.getValue().toString()));
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		this.updatePreviewArea();
	}
	
	@Override
	public void stateChanged(ChangeEvent ev)
	{
		this.updatePreviewArea();
	}
	
	private void updatePreviewArea()
	{
		previewArea.setFont(this.getSelectedFont().toFont());
	}
	
	private static String[] getAllFonts()
	{
		Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		String[] fontNames = new String[fonts.length];
		for (int i=0; i<fonts.length; i++)
		{
			fontNames[i] = fonts[i].getName();
		}
		Arrays.sort(fontNames);
		return fontNames;
	}
	
	private static String getTestString()
	{
		return "The quick brown fox jumps over the lazy dog.";
	}
	
	public static RawFont showDialog(Window parent, RawFont font)
	{
		MyFontChooser dialog = new MyFontChooser(parent,font);
		dialog.setMinimumSize(new Dimension(300,250));
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog.isOK?dialog.getSelectedFont():dialog.originalFont;
	}
	
	public static class PreviewPanel extends JPanel
	{
		private RawFont font;
		private JLabel fontName = new JLabel();
		public PreviewPanel(Font font)
		{
			super(new FlowLayout(FlowLayout.CENTER,6,2));
			this.font = new RawFont(font);
			this.add(fontName);
			this.add(new MyButton("Change")
			{
				{
					if (isMetal)
					{
						this.setPreferredSize(new Dimension(60,28));
					}
				}
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					PreviewPanel.this.font = showDialog(SwingUtilities.windowForComponent(PreviewPanel.this), PreviewPanel.this.font);
					PreviewPanel.this.updateLabel();
				}
			});
			this.updateLabel();
			this.setOpaque(false);
		}
		
		private void updateLabel()
		{
			fontName.setText(font.getName()+", "+font.getStyle().toString()+", "+font.getSize());
		}
		
		public Font getSelectedFont()
		{
			return font.toFont();
		}
	}
	
	static class RawFont
	{
		private String name;
		private Style style;
		private int size;
		RawFont(String name, Style style, int size)
		{
			super();
			this.name = name;
			this.style = style;
			this.size = size;
		}
		
		RawFont(Font f)
		{
			this(f.getName(),Style.of(f.getStyle()),f.getSize());
		}
		
		String getName()
		{
			return this.name;
		}
		
		Style getStyle()
		{
			return this.style;
		}
		
		int getSize()
		{
			return this.size;
		}
		
		Font toFont()
		{
			return new Font(this.name,this.style.getStyle(),this.size);
		}
	}
	
	static enum Style
	{
		PLAIN("Plain",Font.PLAIN),BOLD("Bold",Font.BOLD),ITALIC("Italic",Font.ITALIC),BOLD_AND_ITALIC("Bold and italic",Font.BOLD+Font.ITALIC);
		private String name;
		private int style;
		Style(String name, int style)
		{
			this.name = name;
			this.style = style;
		}
		
		@Override
		public String toString()
		{
			return this.name;
		}
		
		int getStyle()
		{
			return this.style;
		}
		
		static Style of(int styleInt)
		{
			for (Style s: Style.values())
			{
				if (s.style == styleInt)
				{
					return s;
				}
			}
			return null;
		}
	}
	
	static class PreviewLayerUI extends LayerUI<JTextArea>
	{
		private static final Color LIGHT_DARK = new Color(150,150,150,50);
		private static final String PREVIEW_STRING = "-Preview-";
		private static final Font LAYER_FONT = f13.deriveFont(Font.BOLD,50f);
		PreviewLayerUI()
		{
			super();
		}
		
		@Override
		public void paint(Graphics g, JComponent c)
		{
			super.paint(g,c);
			Graphics2D g2d = (Graphics2D)g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			FontMetrics fontMetrics = g2d.getFontMetrics(LAYER_FONT);
			g2d.setFont(LAYER_FONT);
			g2d.setColor(LIGHT_DARK);
			g2d.drawString(PREVIEW_STRING,(c.getWidth()-fontMetrics.stringWidth(PREVIEW_STRING))/2,(c.getHeight()+fontMetrics.getAscent())/2);
		}
	}
}
