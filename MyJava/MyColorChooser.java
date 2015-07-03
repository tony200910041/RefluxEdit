package MyJava;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class MyColorChooser extends JPanel implements ChangeListener
{
	private Color option = Color.BLACK;
	MySpinner R = new MySpinner();
	MySpinner G = new MySpinner();
	MySpinner B = new MySpinner();		
	JPanel PREV = new JPanel();
	
	private static final Font f13 = new Font("Microsoft Jhenghei", Font.PLAIN, 13);
	private static final Border bord = new LineBorder(Color.BLACK, 1);
	
	public MyColorChooser()
	{
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
		PREV.setBorder(bord);
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
		R.setValue(c.getRed());
		G.setValue(c.getGreen());
		B.setValue(c.getBlue());
	}
	
	private class ColorButton extends JButton
	{
		private final Color c;
		private ColorButton(Color c1)
		{
			this.c = c1;
			this.setBackground(c1);
			this.setPreferredSize(new Dimension(30,30));
			this.setFocusable(false);
			this.setBorder(bord);
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
	
	private class MyLabel extends JLabel
	{
		public MyLabel(String str)
		{
			super(str);
			this.setFont(f13);
			this.setBackground(Color.BLACK);
			this.setForeground(Color.BLACK);
		}
	}
}
