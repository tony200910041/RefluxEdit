/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import myjava.gui.*;
import myjava.util.*;
import myjava.gui.common.*;

public class ClassCreatorDialog extends JDialog
{
	private MyRadioButton _default = new MyRadioButton("Default",false,1);
	private MyRadioButton _public = new MyRadioButton("Public",true,1);
	private MyRadioButton _protected = new MyRadioButton("Protected",false,1);
	private MyRadioButton _private = new MyRadioButton("Private",false,1);
	private MyRadioButton _allman = new MyRadioButton("Allman",true,1);
	private MyRadioButton _1tbs = new MyRadioButton("1TBS",false,2);
	private MyCheckBox _static = new MyCheckBox("Static",false);
	private MyCheckBox createMain = new MyCheckBox("Create main method",true);
	private MyCheckBox isGUI = new MyCheckBox("Swing class",false);
	private MyCheckBox isSingleton = new MyCheckBox("Singleton",false);
	private MyTextField name = new MyTextField(18);
	private MyTextField packageName = new MyTextField(18); 
	private MyTextField superClass = new MyTextField(13);
	private MyTextField interfaces = new MyTextField(15);
	private volatile boolean isDone = false;
	private ClassCreatorDialog(Frame parent)
	{
		super(parent, "Class creator", true);
		this.setLayout(new GridLayout(5,1,0,0));
		//
		JPanel p1 = new JPanel();
		p1.add(new MyLabel("Modifier:"));
		p1.add(_public);
		p1.add(_default);
		p1.add(_protected);
		p1.add(_private);
		p1.add(_static);
		_protected.setToolTipText("For nested class only");
		_static.setToolTipText("For nested class only");
		this.add(p1);
		//
		JPanel p2 = new JPanel();
		p2.add(createMain);
		p2.add(isGUI);
		p2.add(isSingleton);
		p2.add(new MyLabel("Indent style:"));
		p2.add(_allman);
		p2.add(_1tbs);
		this.add(p2);
		//
		JPanel p3 = new JPanel();
		p3.add(new MyLabel("Package:"));
		p3.add(packageName);
		p3.add(new MyLabel("Class name:"));
		p3.add(name);
		this.add(p3);
		//
		JPanel p4 = new JPanel();
		p4.add(new MyLabel("Superclass:"));
		p4.add(superClass);
		superClass.getDocument().addDocumentListener(new MyDocumentListener(superClass));
		p4.add(new MyLabel("Implementing interface(s):"));
		p4.add(interfaces);
		interfaces.getDocument().addDocumentListener(new MyDocumentListener(interfaces));
		interfaces.setToolTipText("Use comma \",\" to separate");
		this.add(p4);
		//
		JPanel p5 = new JPanel();
		p5.add(new MyButton("Done")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				isDone = true;
				ClassCreatorDialog.this.setVisible(false);
			}
		});
		p5.add(new MyButton("Cancel")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				isDone = false;
				ClassCreatorDialog.this.setVisible(false);
			}
		});
		this.add(p5);
		//
		ActionListener l1 = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				MyRadioButton button = (MyRadioButton)(ev.getSource());
				_default.setSelected(false);
				_public.setSelected(false);
				_protected.setSelected(false);
				_private.setSelected(false);
				button.setSelected(true);
			}
		};
		_default.addActionListener(l1);
		_public.addActionListener(l1);
		_protected.addActionListener(l1);
		_private.addActionListener(l1);
		//
		ActionListener l2 = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				MyRadioButton button = (MyRadioButton)(ev.getSource());
				switch (button.getIndex())
				{
					case 1:
					_allman.setSelected(true);
					_1tbs.setSelected(false);
					break;
					
					case 2:
					_allman.setSelected(false);
					_1tbs.setSelected(true);
					break;
				}
			}
		};
		_allman.addActionListener(l2);
		_1tbs.addActionListener(l2);
	}
	
	static class MyDocumentListener implements DocumentListener, Runnable, ColorConstants
	{
		private final JTextComponent t;
		MyDocumentListener(JTextComponent t)
		{
			this.t = t;
		}
		
		@Override
		public void changedUpdate(DocumentEvent ev)
		{
			update();
		}
		
		@Override
		public void removeUpdate(DocumentEvent ev)
		{
			update();
		}
		
		@Override
		public void insertUpdate(DocumentEvent ev)
		{
			update();
		}
		
		private synchronized void update()
		{
			Thread thread = new Thread(this);
			thread.start();
		}
		
		@Override
		public void run()
		{
			synchronized(this)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						String text = MyDocumentListener.this.t.getText();
						if (isValidClassNames(text))
						{
							MyDocumentListener.this.t.setBackground(Color.WHITE);
						}
						else
						{
							MyDocumentListener.this.t.setBackground(RED);
						}
					}
				});
			}
		}
	}
	
	private static boolean isValidClassNames(String name)
	{
		String[] names = name.replace(", ",",").split(",");
		for (String s: names)
		{
			if (!isValidClassName(s)) return false;
		}
		return true;
	}
	
	private static boolean isValidClassName(String name)
	{
		try
		{
			Class.forName(name);
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}
	
	public static String showDialog(Frame parent)
	{
		ClassCreatorDialog dialog = new ClassCreatorDialog(parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		/*
		 * closed
		 */
		if (dialog.isDone)
		{
			ClassCreator c = new ClassCreator();
			try
			{
				String superClassInput = dialog.superClass.getText();
				if (superClassInput.isEmpty())
				{
					c.setSuperClass(null);
				}
				else
				{
					c.setSuperClass(Class.forName(superClassInput));
				}
				String inameinput = dialog.interfaces.getText();
				if (inameinput.isEmpty())
				{
					c.setInterfaces(null);
				}
				else
				{
					String[] inames = inameinput.replace(", ", ",").split(",");
					Class<?>[] iclasses = new Class<?>[inames.length];
					for (int i=0; i<iclasses.length; i++)
					{
						iclasses[i] = Class.forName(inames[i]);
					}
					c.setInterfaces(iclasses);
				}
				c.setSingleton(dialog.isSingleton.isSelected());
				c.setCreateMain(dialog.createMain.isSelected());
				c.setName(dialog.name.getText());
				c.setPackage(dialog.packageName.getText());
				c.setGUI(dialog.isGUI.isSelected());
				c.setStatic(dialog._static.isSelected());
				c.setIndentation(dialog._allman.isSelected()?ClassCreator.ALLMAN:ClassCreator.ONETBS);
				if (dialog._default.isSelected())
				{
					c.setAccess(ClassCreator.DEFAULT);
				}
				else if (dialog._public.isSelected())
				{
					c.setAccess(ClassCreator.PUBLIC);
				}
				else if (dialog._protected.isSelected())
				{
					c.setAccess(ClassCreator.PROTECTED);
				}
				else if (dialog._private.isSelected())
				{
					c.setAccess(ClassCreator.PRIVATE);
				}
				return c.toClassString();
			}
			catch (Exception ex)
			{
				ExceptionDialog.exception(ex);
				return null;
			}
		}
		else return null;
	}
}
