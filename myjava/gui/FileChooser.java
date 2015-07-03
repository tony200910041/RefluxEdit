package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.*;
import myjava.gui.common.Resources;

public class FileChooser extends JDialog implements Resources
{
	private static Color background = Color.WHITE;
	private static Font font = f13;
	
	private MyList list = new MyList();
	private MyComboBox box_type = new MyComboBox();
	private MyTextField fileNameField = new MyTextField(15);
	
	MyButton roots = new MyButton("Roots");
	MyButton location = new MyButton("Path");
	
	MyPopupMenu rootMenu = new MyPopupMenu(1);
	MyPopupMenu locationMenu;
	
	private MySmallButton button1 = new MySmallButton("\u2190");
	private MySmallButton button2 = new MySmallButton("\u2191");
	private MySmallButton button3 = new MySmallButton("");
	private MySmallButton button4 = new MySmallButton("\u2261");
	
	MyButton ok = new MyButton("OK");
	MyButton cancel = new MyButton("Cancel");
	
	private java.util.List<File> history = new java.util.ArrayList<File>();
	private static java.util.List<MyFileFilter> filterList = new java.util.ArrayList<MyFileFilter>();
	private File[] selection = null;
	
	MyFileFilter allFilter = new MyFileFilter()
	{
		@Override
		public boolean accept(File f)
		{
			return true;
		}
		
		@Override
		public String getDescription()
		{
			return new String("All Files (*.*)                 ");
		}
	};		
	MyFileFilter dirFilter = new MyFileFilter()
	{
		@Override
		public boolean accept(File f)
		{
			return f.isDirectory();
		}
		
		@Override
		public String getDescription()
		{
			return new String("Directories only");
		}
	};
	
	private FileChooser(File f, JFrame w)
	{
		//560,390
		super(w, "Java === " + f.getPath(), true);
		history.add(f);
		this.setSize(560,400);
		this.setMinimumSize(new Dimension(460,380));
		this.setLocationRelativeTo(w);
		this.setLayout(new BorderLayout());
		this.setAlwaysOnTop(true);
		MyPanel top = new MyPanel();
		top.add(new MyLabel("Jump to:"));
		
		roots.addMouseListener(new MyMouseListener(3));
		location.addMouseListener(new MyMouseListener(4));
		top.add(roots);
		top.add(location);
		top.add(this.button1);
		top.add(this.button2);
		top.add(this.button3);
		top.add(this.button4);
		button3.setIcon(FileSystemView.getFileSystemView().getSystemIcon(new File(System.getProperty("user.home"))));
		this.add(top, BorderLayout.PAGE_START);
		
		JScrollPane JSP = new JScrollPane(list);
		this.add(JSP, BorderLayout.CENTER);
		
		MyPanel bottom = new MyPanel();
		MyPanel bottom_1 = new MyPanel();
		bottom_1.setLayout(new GridLayout(2,1,0,0));
		MyPanel TMP1 = new MyPanel();
		TMP1.add(new MyLabel("File name: "));
		TMP1.add(this.fileNameField);
		MyPanel TMP2 = new MyPanel();
		TMP2.add(new MyLabel("File type:   "));
		TMP2.add(this.box_type);
		bottom_1.add(TMP1);
		bottom_1.add(TMP2);
		bottom.add(bottom_1);
		
		bottom.add(ok);
		bottom.add(cancel);
		this.add(bottom, BorderLayout.PAGE_END);
		
		this.setFilesToList(f);		
		this.list.addMouseListener(new MyMouseListener(0));
		ok.addMouseListener(new MyMouseListener(1));
		cancel.addMouseListener(new MyMouseListener(2));
		button1.addMouseListener(new MyMouseListener(-1));
		button2.addMouseListener(new MyMouseListener(-2));
		button3.addMouseListener(new MyMouseListener(-3));
		button4.addMouseListener(new MyMouseListener(-4));
		
		box_type.addItem(allFilter);
		box_type.addItem(dirFilter);
		box_type.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int x, boolean isSelected, boolean b)
			{
				Component label = super.getListCellRendererComponent(list, value, x, isSelected, b);
				((JLabel)label).setText(((MyFileFilter)value).getDescription());
				return label;
			}
		});
		box_type.setBackground(Color.WHITE);
		box_type.setPreferredSize(new Dimension(214,25));
		for (MyFileFilter filt: filterList)
		{
			box_type.addItem(filt);
		}
	}
	
	public void setFilesToList(File fil)
	{
		if (fil != null)
		{
			MyFileFilter filter = (MyFileFilter)(box_type.getSelectedItem());
			File[] f_list = fil.listFiles(filter);
			if (f_list != null)
			{
				java.util.List<File> files = new java.util.ArrayList<File>();
				java.util.List<File> directories = new java.util.ArrayList<File>();
				for (File f: f_list)
				{
					if (f.isDirectory())
					{
						directories.add(f);
					}
					else if (f.isFile())
					{
						files.add(f);
					}
				}
				this.list.lm.removeAllElements();
				if (directories != null)
				{
					Collections.sort(files, new Comparator<File>()
					{
						public int compare(File f1, File f2)
						{
							return f1.getName().compareTo(f2.getName());
						}
					});
					for (File f: directories)
					{
						this.list.lm.addElement(f);
					}
				}
				if (files != null)
				{
					Collections.sort(directories, new Comparator<File>()
					{
						public int compare(File f1, File f2)
						{
							return f1.getName().compareTo(f2.getName());
						}
					});
					for (File f: files)
					{
						this.list.lm.addElement(f);
					}
				}
			}
			else
			{
				this.list.lm.removeAllElements();
			}
			setTitle("Java === " + fil.getPath());
		}		
	}
	
	public static void addFileFilter(MyFileFilter filter)
	{
		filterList.add(filter);
	}
	
	public static void resetFileFilter()
	{
		filterList = new ArrayList<MyFileFilter>();
	}
	
	public static void setMainBackground(Color c)
	{
		background = c;
	}
	
	public static File[] showFileChooser(File f, JFrame w)
	{
		FileChooser chooser = new FileChooser(f, w);		
		chooser.setVisible(true);
		File[] files = chooser.selection;
		chooser.dispose();
		return files;
	}
	
	private class MyMouseListener extends MouseAdapter
	{
		private int x;
		public MyMouseListener(int x)
		{
			this.x = x;
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
			if (ev.getSource() instanceof JButton)
			{
				((JButton)(ev.getSource())).setBorder(bord2);
			}
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
		{
			if (ev.getSource() instanceof JButton)
			{
				((JButton)(ev.getSource())).setBorder(bord1);
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent ev)
		{
			if (!ev.isPopupTrigger())
			{			
				switch (this.x)
				{
					//list
					case 0:
					if (ev.getClickCount() == 1)
					{
						MyList list = (MyList)(ev.getSource());
						if (!list.isSelectionEmpty())
						{
							Object[] obj_ar = list.getSelectedValuesList().toArray();
							File[] fil_ar = new File[obj_ar.length];
							for (int i=0; i<obj_ar.length; i++)
							{
								fil_ar[i] = (File)(obj_ar[i]);
							}
							selection = fil_ar;
							if (selection.length == 1)
							{
								fileNameField.setText(selection[0].getName());
							}
							else
							{
								fileNameField.setText("Multiple files selected");
							}
						}
					}
					if (ev.getClickCount() == 2)
					{
						MyList list = (MyList)(ev.getSource());
						if (!list.isSelectionEmpty())
						{
							File file = (File)(list.getSelectedValue());
							if (file.isDirectory())
							{
								selection = null;
								fileNameField.setText("");
								history.add(file);
								setFilesToList(file);
							}
						}
					}
					break;
				}
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
			File f; MyButton button = null;
			if (ev.getSource() instanceof MyButton)
			{
				button = (MyButton)(ev.getSource());
			}
			
			switch (this.x)
			{
				//case 1: ok
				//case 2: cancel				
				case 1:
				if (selection != null)
				{
					if (!selection[0].getName().equals(fileNameField.getText()))
					{
						selection = new File[1];
						selection[0] = new File(history.get(history.size()-1), fileNameField.getText());
					}
				}
				else if (fileNameField.getText() != null)
				{
					selection = new File[1];
					selection[0] = new File(history.get(history.size()-1), fileNameField.getText());
				}
				setVisible(false);
				break;
				
				case 2:
				selection = null;
				setVisible(false);
				break;
				
				//back
				case -1:
				if (history.size() >= 2)
				{
					setFilesToList(history.get(history.size()-2));
					history.remove(history.size()-1);
				}
				break;
				
				//parent
				case -2:
				f = history.get(history.size()-1).getParentFile();
				if (f != null)
				{
					setFilesToList(f);
					history.add(f);
				}	
				break;
				
				//new folder
				case -3:
				String str = JOptionPane.showInputDialog(null, "Please enter the name:", "Create new folder", JOptionPane.QUESTION_MESSAGE);
				if (str != null)
				{
					File file = new File(history.get(history.size()-1), str);
					file.mkdir();
					history.add(file);
					setFilesToList(file);		
				}		
				break;
				
				//list style
				case -4:
				if (list.getLayoutOrientation() == JList.VERTICAL_WRAP)
				{
					list.setLayoutOrientation(JList.VERTICAL);
				}
				else
				{
					list.setLayoutOrientation(JList.VERTICAL_WRAP);
				}
				break;
				
				//roots
				case 3:
				rootMenu.show(button, 0, (int)(button.getPreferredSize().getHeight()));
				break;
				
				//location
				case 4:
				locationMenu = new MyPopupMenu(2);
				f = history.get(history.size()-1);
				do
				{
					locationMenu.insert(new MyMenuItem(f.getPath()), 0);
					f = f.getParentFile();
				} while (f != null);
				locationMenu.pack();
				locationMenu.show(button, 0, (int)(button.getPreferredSize().getHeight()));
				break;
			}
		}
	}
	
	private class MyPopupMenu extends JPopupMenu
	{
		private int x;
		private MyPopupMenu(int x)
		{ 
			super();
			if (x == 1)
			{	//root: 1; path: 2
				File[] rootList = File.listRoots();
				for (File f: rootList)
				{
					this.add(new MyMenuItem(f.getPath()));
				}
				this.add(new JSeparator());
				this.add(new MyMenuItem(System.getProperty("user.home")));
			}
		}
	}
	
	private class MyMenuItem extends JMenuItem implements MouseListener
	{
		private int x;
		private MyMenuItem(String str)
		{
			super(str);
			this.setFont(font);
			this.setBackground(Color.WHITE);
			this.setForeground(Color.BLACK);
			this.addMouseListener(this);
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
			File file = new File(this.getText());
			setFilesToList(file);
			history.add(file);
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
		{
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
		}
		
		@Override
		public void mousePressed(MouseEvent ev)
		{
		}
		
		@Override
		public void mouseClicked(MouseEvent ev)
		{
		}
	}
	
	private class MyList extends JList
	{
		final DefaultListModel lm = new DefaultListModel();
		private MyList()
		{
			this.setModel(lm);
			this.setLayoutOrientation(JList.VERTICAL_WRAP);
			this.setVisibleRowCount(11);
			this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			this.setCellRenderer(new DefaultListCellRenderer()
			{
				@Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
				{
					Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (isSelected)
					{
						c.setBackground(new Color(255,253,225));
						c.setForeground(Color.BLACK);
					}
					else
					{
						c.setBackground(Color.WHITE);
					}
					if (value instanceof File)
					{
						((JLabel)c).setIcon(getIcon((File)value));
						((JLabel)c).setText(((File)value).getName());
					}
					return c;
				}
				
				public Icon getIcon(File f)
				{
					return FileSystemView.getFileSystemView().getSystemIcon(f);
				}
			});
			this.setFont(font);
		}
	}
	
	private class MyComboBox extends JComboBox implements ActionListener
	{
		private MyComboBox()
		{
			super();
			this.setFont(new Font(font.getFontName(), font.getStyle(), font.getSize()-1));
			this.addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent ev)
		{
			setFilesToList(history.get(history.size()-1));
		}
	}
	
	private class MyButton extends JButton
	{
		private MyButton(String str)
		{
			super(str);
			this.setFont(font);
			this.setFocusable(false);
			this.setBorder(bord1);
			this.setBackground(Color.WHITE);
			this.setForeground(Color.BLACK);
			this.setPreferredSize(new Dimension(50,30));
		}
	}
	
	private class MySmallButton extends MyButton
	{
		private MySmallButton(String str)
		{
			super(str);
			this.setPreferredSize(new Dimension(30,30));
		}
	}
	
	private class MyPanel extends JPanel
	{
		private MyPanel()
		{
			this.setBackground(background);
		}
	}
	
	private class MyLabel extends JLabel
	{
		private MyLabel(String str)
		{
			super(str);
			this.setFont(font);
		}
	}
	
	private class MyTextField extends JTextField
	{
		private MyTextField(int x)
		{
			super(x);
			this.setBackground(new Color(240,240,240));
			this.setFont(new Font(font.getFontName(), font.getStyle(), font.getSize()+2));
		}
	}
}
