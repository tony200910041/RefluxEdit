package myjava.gui; //version: 1.1

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import myjava.gui.MyButton;
import myjava.gui.MyLabel;
import myjava.gui.common.Resources;

public class MyColorChooser extends JPanel implements ChangeListener, Resources
{
	public static final int LARGE = 0;
	public static final int SMALL = 1;
	protected static final Dimension d30 = new Dimension(30,30);
	private Color option = Color.BLACK;
	// LARGE:
	private MySpinner R = new MySpinner();
	private MySpinner G = new MySpinner();
	private MySpinner B = new MySpinner();
	// SMALL:
	private MyButton select;
	//
	private JPanel PREV = new JPanel();	
	private int mode;
	private Window parent = null;
	public MyColorChooser(int x)
	{
		super();
		this.mode = x;
		switch (mode)
		{
			case 0:
			this.setLayout(new BorderLayout());
			this.add(new MyLabel("Please choose a color:"), BorderLayout.PAGE_START);
			this.setBackground(Color.WHITE);
			JPanel P0 = new JPanel();
			P0.setBackground(Color.WHITE);
			P0.add(new ColorButton(Color.RED));
			P0.add(new ColorButton(Color.YELLOW));	
			P0.add(new ColorButton(Color.GREEN));	
			P0.add(new ColorButton(Color.BLUE));
			P0.add(new ColorButton(Color.WHITE));
			P0.add(new ColorButton(Color.BLACK));
			this.add(P0, BorderLayout.CENTER);
			JPanel P1 = new JPanel();
			P1.setBackground(Color.WHITE);
			P1.add(new MyLabel("Or enter the RGB value:"));
			P1.add(R);
			P1.add(G);
			P1.add(B);
			
			MyLabel PREVTEXT = new MyLabel("Color Preview");
			PREVTEXT.setHorizontalAlignment(JLabel.CENTER);
			PREVTEXT.setBackground(Color.WHITE);
			PREVTEXT.setForeground(Color.WHITE);
			PREV.setPreferredSize(new Dimension(300,40));
			PREV.setBorder(bord1);
			PREV.setBackground(Color.BLACK);
			PREV.add(PREVTEXT);
			
			JPanel P2 = new JPanel();
			P2.setBackground(Color.WHITE);
			P2.setLayout(new BorderLayout());
			P2.add(P1, BorderLayout.PAGE_START);
			P2.add(PREV, BorderLayout.PAGE_END);
			this.add(P2, BorderLayout.PAGE_END);
			
			R.addChangeListener(this);
			G.addChangeListener(this);
			B.addChangeListener(this);
			break;
			
			case 1:
			this.setLayout(new FlowLayout(FlowLayout.CENTER));
			this.PREV.setPreferredSize(d30);
			this.PREV.setBackground(Color.BLACK);
			this.setBackground(Color.WHITE);
			this.add(PREV);
			select = new MyButton("\u2190\u2192")
			{
				@Override
				public void mouseReleased(MouseEvent ev)
				{
					option = showColorDialog(parent, "Color chooser", PREV.getBackground());
					PREV.setBackground(option);
				}
			};
			select.setPreferredSize(d30);
			this.add(select);
			break;
		}
	}
	
	public void setParent(Window w)
	{
		this.parent = w;
	}
	
	public void stateChanged(ChangeEvent ev)
	{
		option = new Color(Short.parseShort(R.getValue().toString()), Short.parseShort(G.getValue().toString()), Short.parseShort(B.getValue().toString()));		
		PREV.setBackground(option);
	}
	
	public Color getColor()
	{
		return option;
	}
	
	public void setColor(Color c)
	{
		switch (this.mode)
		{
			case 0:
			if (c != null)
			{
				R.setValue(c.getRed());
				G.setValue(c.getGreen());
				B.setValue(c.getBlue());
			}
			else
			{
				option = null;
			}
			break;
			
			case 1:
			option = c;
			if (c != null)
			{
				PREV.setBackground(c);
			}
			break;
		}
	}
	
	public static Color showColorDialog(Window w, String title, Color color)
	{
		final JDialog dialog = new JDialog(w, title);
		if (w != null) dialog.setAlwaysOnTop(w.isAlwaysOnTop());
		dialog.setModal(true);
		dialog.setLayout(new BorderLayout());
		JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		dialog.add(centerPanel, BorderLayout.CENTER);
		dialog.add(bottomPanel, BorderLayout.PAGE_END);
		final MyColorChooser cc = new MyColorChooser(0);
		centerPanel.add(cc);
		cc.setColor(color);
		MyButton choose = new MyButton("OK")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				dialog.setVisible(false);
			}
		};
		MyButton cancel = new MyButton("Cancel")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				cc.setColor(null);
				dialog.dispose();
			}
		};
		bottomPanel.add(choose);
		bottomPanel.add(cancel);
		dialog.pack();
		dialog.setLocationRelativeTo(w);
		dialog.setVisible(true);
		dialog.setVisible(false);
		return cc.getColor();
	}
	
	private class ColorButton extends JButton
	{
		private final Color c;
		private ColorButton(Color c1)
		{
			this.c = c1;
			this.setBackground(c1);
			this.setPreferredSize(d30);
			this.setFocusable(false);
			this.setBorder(bord1);
			this.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseReleased(MouseEvent ev)
				{
					option = c;
					R.setValue(c.getRed());
					G.setValue(c.getGreen());
					B.setValue(c.getBlue());
					PREV.setBackground(c);
				}
			});
		}
	}
	
	private class MySpinner extends JSpinner
	{
		public MySpinner()
		{			
			this.setModel(new SpinnerNumberModel(0, 0, 255, 1));
			this.setFont(f13);
		}
	}
}
