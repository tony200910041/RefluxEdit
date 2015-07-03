package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import myjava.gui.*;

public class BaseConverter extends JDialog implements DocumentListener, ActionListener, ChangeListener, Runnable
{
	private static final Font f15 = new Font("Microsoft Jhenghei",Font.PLAIN,15);
	private static final Color RED = new Color(255,133,133);
	private JTextField denary = new JTextField();
	private JTextField otherBase = new JTextField();
	private JSpinner base = new JSpinner(new SpinnerNumberModel(2,2,36,1));
	private MyRadioButton from10 = new MyRadioButton("Convert from denary",true,1);
	private MyRadioButton to10 = new MyRadioButton("Convert to denary",false,2);
	private MyCheckBox insertDenary = new MyCheckBox("",false);
	private MyCheckBox insertOtherBase = new MyCheckBox("",false);
	private BaseConverter(Frame parent)
	{
		super(parent,"Base converter",true);
		this.setLayout(new GridLayout(3,1,0,0));
		//
		JPanel p0 = new JPanel();
		from10.addActionListener(this);
		to10.addActionListener(this);
		p0.add(from10);
		p0.add(to10);
		this.add(p0);
		//
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		denary.setFont(f15);
		denary.setBackground(Color.WHITE);
		denary.setPreferredSize(new Dimension(300,26));
		denary.getDocument().addDocumentListener(this);
		insertDenary.setToolTipText("Insert this result");
		p1.add(insertDenary);
		p1.add(denary);
		p1.add(new MyLabel("Base 10"));
		this.add(p1);
		//
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		otherBase.setFont(f15);
		otherBase.setBackground(Color.WHITE);
		otherBase.setPreferredSize(new Dimension(232,26));
		otherBase.getDocument().addDocumentListener(this);
		otherBase.setEditable(false);
		insertOtherBase.setToolTipText("Insert this result");
		p2.add(insertOtherBase);
		p2.add(otherBase);
		base.setFont(f15);
		base.addChangeListener(this);
		p2.add(base);
		this.add(p2);
	}
	
	@Override
	public void changedUpdate(DocumentEvent ev)
	{
		update();
	}
	
	@Override
	public void insertUpdate(DocumentEvent ev)
	{
		update();
	}
	
	@Override
	public void removeUpdate(DocumentEvent ev)
	{
		update();
	}
	
	@Override
	public void stateChanged(ChangeEvent ev)
	{
		update();
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		if (((MyRadioButton)(ev.getSource())).getIndex() == 1)
		{
			//from10
			from10.setSelected(true);
			to10.setSelected(false);
			denary.setEditable(true);
			otherBase.setEditable(false);
		}
		else
		{
			//to10
			from10.setSelected(false);
			to10.setSelected(true);
			denary.setEditable(false);
			otherBase.setEditable(true);
		}
	}
	
	void update()
	{
		Thread thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run()
	{
		/*
		 * not EDT
		 */
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					base.commitEdit();
				}
				catch (Exception ex)
				{
				}
				denary.getDocument().removeDocumentListener(BaseConverter.this);
				otherBase.getDocument().removeDocumentListener(BaseConverter.this);
				try
				{
					int baseno = Integer.parseInt(base.getValue().toString());
					if (from10.isSelected())
					{
						String s1 = denary.getText();
						if (s1.isEmpty())
						{
							otherBase.setText("");
							denary.setBackground(Color.WHITE);
							otherBase.setBackground(Color.WHITE);
						}
						else
						{
							otherBase.setText(toBase(Long.parseLong(s1),baseno));
							denary.setBackground(Color.WHITE);
							otherBase.setBackground(Color.WHITE);
						}
					}
					else
					{
						String s2 = otherBase.getText();
						if (s2.isEmpty())
						{
							denary.setText("");
							denary.setBackground(Color.WHITE);
							otherBase.setBackground(Color.WHITE);
						}
						else
						{
							denary.setText(toDenary(s2,baseno)+"");
							denary.setBackground(Color.WHITE);
							otherBase.setBackground(Color.WHITE);
						}
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					denary.setBackground(RED);
					otherBase.setBackground(RED);
				}
				finally
				{
					denary.getDocument().addDocumentListener(BaseConverter.this);
					otherBase.getDocument().addDocumentListener(BaseConverter.this);
				}
			}
		});
	}
	
	private long toDenary(String number, int base)
	{
		return Long.valueOf(number,base);
	}
	
	private String toBase(long denary, int base)
	{
		return Long.toString(denary,base).toUpperCase();
	}
	
	public static String showBaseConverter(Frame parent)
	{
		BaseConverter converter = new BaseConverter(parent);
		converter.pack();
		converter.setLocationRelativeTo(parent);
		converter.setVisible(true);
		/*
		 * closed
		 */
		String result = "";
		if (converter.insertDenary.isSelected())
		{
			result+=converter.denary.getText()+" ";
		}
		if (converter.insertOtherBase.isSelected())
		{
			result+=converter.otherBase.getText()+" ";
		}
		return result;
	}
}
