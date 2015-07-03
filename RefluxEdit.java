import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import javax.swing.* ;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.ColorUIResource;

import java.io.*;

public class RefluxEdit extends JFrame
{
	//values:
	int confirmvalue;
	int returnvalue;
	int i;
	int j;
	int k;
	int timematch;
	
	byte wordamended = 0;
	byte currentstring = 0;
	
	boolean ready = false;
	
	public static void main(String [] args)
	{
		RefluxEdit w = new RefluxEdit();
		w.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		w.setSize(460,395);
		w.setVisible(true);
		w.setTitle("RefluxEdit 1.0");
		w.setLocationRelativeTo(null);
		w.addWindowListener(new WindowAdapter()
		{
            public void windowClosing(WindowEvent ev)
            {
				Object[] options = {"YES", "NO"};
				int close = JOptionPane.showOptionDialog(null, "Do you really want to close RefluxEdit?", "Confirm exit", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
				if (close == JOptionPane.YES_OPTION)
				{
					System.exit(0);
				}
			}
		});	
		w.setResizable(true);
	}
	
	//Conponents:
	JButton saveas = new JButton("Save As");
	JButton save = new JButton("Save");
	JButton open = new JButton("Open");
	JButton newfile = new JButton("New");
	
	JLabel current = new JLabel("\n");
	
	JTextArea TEXTAREA = new JTextArea(12, 30);
	JScrollPane jsp = new JScrollPane(TEXTAREA);
	
	JFileChooser chooser = new JFileChooser();
	FileNameExtensionFilter[] filter1 = {new FileNameExtensionFilter("Text files (*.txt, *.ini, *.log, *.java, *.py, *.bat, *.cmd, *.htm, *.html, *.xml, *.php)", "txt", "ini", "log", "java", "py", "bat", "cmd", "htm", "html", "xml", "php"), new FileNameExtensionFilter("Text (*.txt, *.ini, *.log)", "txt", "ini", "log"), new FileNameExtensionFilter("Command (*.bat, *.cmd)", "bat", "cmd"), new FileNameExtensionFilter("Website and programming (*.htm, *.html, *.xml, *.php, *.java, *.py)", "htm", "html", "xml", "php", "java", "py")};
	
	//GUI:
	Font J1 = new Font("Microsoft Jhenghei", Font.PLAIN, 12);
	Font J2 = new Font("Microsoft Jhenghei", Font.PLAIN, 15);
	
	Border bord1 = new LineBorder(Color.BLACK, 1);
	
	Color blue = new Color(242,254,255);	
	Color darkblue = new Color(37,47,104);
	Color orange = new Color(245,223,134);
	Color brown = new Color(120,77,26);
	Color green = new Color(219,255,197);
	Color grey = new Color(230,230,230);
	
	//others:
	File file = null;
	File chooserdefault = new File("./");

	String str = "";
	String tmp;
	String tmp1;
	String tmp3;
	
	String[] backup = {"", "", "", "", "", "", "", "", "", ""};
	
	Transferable tmp2;		
	Clipboard systemclipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	UIManager uim = new UIManager();
	
	public RefluxEdit()
	{
		//Menu bar:
		JMenuBar menubar1 = new JMenuBar();
		menubar1.setFont(J1);
		menubar1.setBackground(blue);
		
		JMenu menu1 = new JMenu("File");
		menu1.setMnemonic(KeyEvent.VK_F);
		menu1.setFont(J1);
		JMenuItem item1 = new JMenuItem("New File       ");
		JMenuItem item2 = new JMenuItem("Open File");
		JMenuItem item3 = new JMenuItem("Save As...");
		JMenuItem item4 = new JMenuItem("Save");
		JMenuItem item5 = new JMenuItem("Close");
		item1.setFont(J1);
		item2.setFont(J1);
		item3.setFont(J1);
		item4.setFont(J1);
		item5.setFont(J1);
		item1.setBackground(Color.WHITE);
		item2.setBackground(Color.WHITE);
		item3.setBackground(Color.WHITE);
		item4.setBackground(Color.WHITE);
		item5.setBackground(Color.WHITE);
		
		JMenu menu2 = new JMenu("Edit");
		menu2.setMnemonic(KeyEvent.VK_E);
		menu2.setFont(J1);
		JMenuItem item9 = new JMenuItem("Select All");
		JMenuItem item10 = new JMenuItem("Select All and copy");
		JMenuItem item11 = new JMenuItem("Copy");
		JMenuItem item12 = new JMenuItem("Paste");
		JMenuItem item13 = new JMenuItem("Cut");
		JMenuItem item14 = new JMenuItem("Paste on next line");
		JMenuItem item15 = new JMenuItem("Delete");
		
		item9.setFont(J1);
		item10.setFont(J1);
		item11.setFont(J1);
		item12.setFont(J1);
		item13.setFont(J1);
		item14.setFont(J1);
		item15.setFont(J1);
		
		item9.setBackground(Color.WHITE);
		item10.setBackground(Color.WHITE);
		item11.setBackground(Color.WHITE);
		item12.setBackground(Color.WHITE);
		item13.setBackground(Color.WHITE);
		item14.setBackground(Color.WHITE);
		item15.setBackground(Color.WHITE);
	
		JMenu menu3 = new JMenu("Help");
		menu3.setMnemonic(KeyEvent.VK_H);
		menu3.setFont(J1);
		JMenuItem item8 = new JMenuItem("About       ");
		item8.setFont(J1);
		item8.setBackground(Color.WHITE);
		
		JMenu menu4 = new JMenu("Tools");
		menu4.setMnemonic(KeyEvent.VK_T);
		menu4.setFont(J1);
		JMenuItem item6 = new JMenuItem("Disable/Enable editing");
		JMenuItem item7 = new JMenuItem("Disable/Enable auto word wrap");
		JMenuItem item16 = new JMenuItem("Convert to uppercase");
		JMenuItem item17 = new JMenuItem("Convert to lowercase");
		JMenuItem item18 = new JMenuItem("Find");
		JMenuItem item19 = new JMenuItem("Replace");
		JMenuItem item20 = new JMenuItem("Disable/Enable wrapping by words");
		JMenuItem item21 = new JMenuItem("Insert ten equal signs");
		JMenuItem item22 = new JMenuItem("Insert four spaces");
		JMenuItem item23 = new JMenuItem("Undo");
		JMenuItem item24 = new JMenuItem("Redo");
		JMenuItem item25 = new JMenuItem("Delete blank line");
		
		item6.setFont(J1);
		item7.setFont(J1);
		item16.setFont(J1);
		item17.setFont(J1);
		item18.setFont(J1);
		item19.setFont(J1);
		item20.setFont(J1);
		item21.setFont(J1);
		item22.setFont(J1);
		item23.setFont(J1);
		item24.setFont(J1);
		item25.setFont(J1);
				
		item6.setBackground(Color.WHITE);
		item7.setBackground(Color.WHITE);
		item16.setBackground(Color.WHITE);
		item17.setBackground(Color.WHITE);
		item18.setBackground(Color.WHITE);
		item19.setBackground(Color.WHITE);
		item20.setBackground(Color.WHITE);
		item21.setBackground(Color.WHITE);
		item22.setBackground(Color.WHITE);
		item23.setBackground(Color.WHITE);
		item24.setBackground(Color.WHITE);
		item25.setBackground(Color.WHITE);
		
		menu1.add(item1);
		menu1.add(item2);
		menu1.add(new JSeparator());
		menu1.add(item3);
		menu1.add(item4);
		menu1.add(new JSeparator());
		menu1.add(item5);		

		menu2.add(item23);
		menu2.add(item24);
		menu2.add(new JSeparator());
		menu2.add(item9);
		menu2.add(item10);
		menu2.add(new JSeparator());
		menu2.add(item13);
		menu2.add(item11);
		menu2.add(item12);		
		menu2.add(item14);
		menu2.add(item15);
		
		menu4.add(item6);
		menu4.add(item7);
		menu4.add(item20);
		menu4.add(new JSeparator());
		menu4.add(item16);
		menu4.add(item17);
		menu4.add(new JSeparator());
		menu4.add(item18);
		menu4.add(item19);
		menu4.add(new JSeparator());
		menu4.add(item21);
		menu4.add(item22);
		menu4.add(item25);
		
		menu3.add(item8);
		
		menubar1.add(menu1);
		menubar1.add(new JLabel(" "));
		menubar1.add(menu2);
		menubar1.add(new JLabel(" "));
		menubar1.add(menu4);
		menubar1.add(new JLabel(" "));
		menubar1.add(menu3);
		this.setJMenuBar(menubar1);
		
		item1.addMouseListener(new MouseLis(6));
		item2.addMouseListener(new MouseLis(3));
		item3.addMouseListener(new MouseLis(1));
		item4.addMouseListener(new MouseLis(2));
		item5.addMouseListener(new MouseLis(5));
		item6.addMouseListener(new MouseLis(7));
		item7.addMouseListener(new MouseLis(8));
		item8.addMouseListener(new MouseLis(9));
		item9.addMouseListener(new MouseLis(10));
		item10.addMouseListener(new MouseLis(11));
		item11.addMouseListener(new MouseLis(12));
		item12.addMouseListener(new MouseLis(13));
		item13.addMouseListener(new MouseLis(14));
		item14.addMouseListener(new MouseLis(15));
		item15.addMouseListener(new MouseLis(16));
		item16.addMouseListener(new MouseLis(17));
		item17.addMouseListener(new MouseLis(18));
		item18.addMouseListener(new MouseLis(19));
		item19.addMouseListener(new MouseLis(20));
		item20.addMouseListener(new MouseLis(21));
		item21.addMouseListener(new MouseLis(22));
		item22.addMouseListener(new MouseLis(23));
		item23.addMouseListener(new MouseLis(24));
		item24.addMouseListener(new MouseLis(25));
		item25.addMouseListener(new MouseLis(26));
		
		getContentPane().setLayout(new BorderLayout());
		
		saveas.setPreferredSize(new Dimension(60,30));
		save.setPreferredSize(new Dimension(60,30));
		open.setPreferredSize(new Dimension(60,30));
		newfile.setPreferredSize(new Dimension(60,30));
		
		saveas.setBackground(Color.WHITE);
		save.setBackground(Color.WHITE);
		open.setBackground(Color.WHITE);
		newfile.setBackground(Color.WHITE);
		
		saveas.setForeground(brown);
		save.setForeground(brown);
		open.setForeground(brown);
		newfile.setForeground(brown);
		
		saveas.setFont(J1);
		save.setFont(J1);
		open.setFont(J1);
		newfile.setFont(J1);
		
		saveas.setBorder(bord1);
		save.setBorder(bord1);
		open.setBorder(bord1);
		newfile.setBorder(bord1);
		
		TEXTAREA.setFont(J2);
		TEXTAREA.setLineWrap(true);		
		TEXTAREA.setWrapStyleWord(true);
		TEXTAREA.setDragEnabled(true);
		TEXTAREA.setTabSize(3);
		
		current.setFont(J1);	
		
		setFileChooserStyle(chooser.getComponents());
		
		uim.put("OptionPane.buttonFont", J1);
		uim.put("OptionPane.messageFont", J1);		
		
		uim.put("OptionPane.okButtonText", "OK");
		uim.put("OptionPane.cancelButtonText", "NO");
		
		uim.put("Button.background", Color.WHITE);
		uim.put("PopupMenu.background", Color.WHITE);
		uim.put("MenuItem.background", Color.WHITE);
		uim.put("RadioButtonMenuItem.background", Color.WHITE);
		
		uim.put("PopupMenu.foreground", darkblue);
		uim.put("MenuItem.foreground", darkblue);
		uim.put("Menu.foreground", J1);
		uim.put("RadioButtonMenuItem.foreground", darkblue);
		
		uim.put("PopupMenu.font", J1);
		uim.put("MenuItem.font", J1);
		uim.put("Menu.font", J1);
		uim.put("RadioButtonMenuItem.font", J1);
		uim.put("TextField.font", J1);
		
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setBorder(bord1);
		
		saveas.addMouseListener(new MouseLis(1));
		save.addMouseListener(new MouseLis(2));
		open.addMouseListener(new MouseLis(3));
		newfile.addMouseListener(new MouseLis(4));
		
		// create JPanel so that more than one objects can be added into the layout
		JPanel p1 = new JPanel();
		p1.add(newfile);
		p1.add(open);
		p1.add(saveas);
		p1.add(save);
		
		JPanel p2 = new JPanel();
		p2.add(current);
				
		getContentPane().add(p1, BorderLayout.PAGE_START);
		getContentPane().add(jsp, BorderLayout.CENTER);
		getContentPane().add(p2, BorderLayout.PAGE_END);
		getContentPane().add(new JLabel("  "), BorderLayout.LINE_START);
		getContentPane().add(new JLabel("  "), BorderLayout.LINE_END);
		
		TEXTAREA.addKeyListener(new KeyAdapter()
		{
			public void keyTyped(KeyEvent e)
			{	
				if (currentstring != 0)
				{
					currentstring = 0;
					for (i=0; i<=9; i++)
					{
						backup[i] = "";
					}
					backup[1] = TEXTAREA.getText();
				}
				wordamended++;
				if (wordamended == 10)
				{
					SwitchBackup();
				}
			}
		});
		
		try
		{
			ImageIcon ico = new ImageIcon(getClass().getResource("/SRC/APPICON.PNG"));
			this.setIconImage(ico.getImage());
			ico = null;
		}
		catch (Exception ex)
		{
		}
	}
	
	public void setFileChooserStyle(Component[] comp)  
	{		
		for (int x = 0; x < comp.length; x++)  
		{
			if (comp[x] instanceof Container) setFileChooserStyle(((Container)comp[x]).getComponents());  
			try
			{
				comp[x].setFont(J1);
				comp[x].setForeground(darkblue);
				if (comp[x] instanceof JButton)
				{
					//set button
					comp[x].setBackground(Color.WHITE);
				}
				if (comp[x] instanceof JList)
				{
					//set file field
					comp[x].setBackground(Color.WHITE);
				}
				if (comp[x] instanceof JComboBox)
				{
					//set the choosing list
					comp[x].setBackground(Color.WHITE);
				}
				if (comp[x] instanceof JTextField)
				{
					//set the text field
					comp[x].setBackground(Color.WHITE);
				}				
				if (comp[x] instanceof JToggleButton)
				{
					comp[x].setBackground(Color.WHITE);
				}
				if (comp[x] instanceof JScrollBar)
				{
					comp[x].setBackground(Color.WHITE);
					comp[x].setForeground(Color.WHITE);
				}
				if (comp[x] instanceof JLabel)
				{
					comp[x].setBackground(Color.WHITE);
				}		
			}  
			catch (Exception ex)
			{
			}
		}
	}
	
	public void Savefile()
	{
		try
		{
			FileWriter fw1 = new FileWriter(file);
			TEXTAREA.write(fw1);
			fw1.close();
			tmp = file + "";
			if (tmp.length() > 50)
			{
				tmp = tmp.substring(0, 25) + "..." + tmp.substring(tmp.length()-25, tmp.length());
			}
			current.setText("File: " + tmp);
			tmp = null;
		}
		catch (Exception ex)
		{
			ex.getMessage();
		}
	} 
	
	public void Openfile()
	{
		try
		{
			TEXTAREA.setText(null);
			str = null;
			BufferedReader br1 = new BufferedReader(new FileReader(file));
			while ((str = br1.readLine()) != null)
			{
				TEXTAREA.append(str);
				TEXTAREA.append("\n");
			}
			br1.close();
			TEXTAREA.setCaretPosition(0);
			tmp = file + "";
			if (tmp.length() > 50)
			{
				tmp = tmp.substring(0, 25) + "..." + tmp.substring(tmp.length()-25, tmp.length());
			}
			current.setText("File: " + tmp);
			tmp = null;
			str = null;
		}
		catch (Exception ex)
		{
			ex.getMessage();
		}
	}
	
	public void Newfile()
	{
		TEXTAREA.setText(null);
		file = null;
		str = null;
		current.setText(" ");
		for (i=0; i<=9; i++)
		{
			backup[i] = "";
		}
		currentstring = 0;
		wordamended = 0;
	}
	
	public void SaveChooser()
	{
		chooser.addChoosableFileFilter(filter1[0]);
		chooser.addChoosableFileFilter(filter1[1]);
		chooser.addChoosableFileFilter(filter1[2]);
		chooser.addChoosableFileFilter(filter1[3]);
		chooser.setFileFilter(filter1[0]);
		chooser.setCurrentDirectory(chooserdefault);
		chooser.setDialogTitle("Choose save location:");
		chooser.setFileHidingEnabled(false);
		//added:
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		//
		chooser.setApproveButtonText("Save");
		
		//returnvalue = chooser.showSaveDialog(null);
		returnvalue = chooser.showSaveDialog(null);
		chooserdefault = chooser.getCurrentDirectory();
		if (returnvalue == chooser.APPROVE_OPTION) 
		{
			file = chooser.getSelectedFile();
			
			if (file.exists())
			{	
				Object[] options2 = {"YES", "NO"};
				confirmvalue = JOptionPane.showOptionDialog(null, "Replace existing file?", "Replace?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options2, options2[1]);
				if (confirmvalue == JOptionPane.YES_OPTION)
				{
					Savefile();
				}
			}
			if (!file.exists())
			{
				try
				{
					PrintWriter pw1 = new PrintWriter(file + "", "UTF-8");
					pw1.close();
				}
				catch (Exception ex)
				{
				}
				Savefile();
			}
		}
	}
	
	public void OpenChooser()
	{
		chooser.addChoosableFileFilter(filter1[0]);
		chooser.addChoosableFileFilter(filter1[1]);
		chooser.addChoosableFileFilter(filter1[2]);
		chooser.addChoosableFileFilter(filter1[3]);
		chooser.setFileFilter(filter1[0]);
		chooser.setCurrentDirectory(chooserdefault);
		chooser.setDialogTitle("Choose file location:");
		chooser.setFileHidingEnabled(false);
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setApproveButtonText("Choose");				
		
		returnvalue = chooser.showOpenDialog(null);
		chooserdefault = chooser.getCurrentDirectory();
		if (returnvalue == chooser.APPROVE_OPTION) 
		{
			file = chooser.getSelectedFile();
			Openfile();
		}
	}
	
	public void SearchWord()
	{
		i = 0;
		timematch = 0;
		uim.put("OptionPane.okButtonText", "Find");
		uim.put("OptionPane.cancelButtonText", "Cancel");
		tmp = JOptionPane.showInputDialog(null, "Enter the word(s) you want to find:");
		if (tmp != null)
		{
			for (i=i; i<=TEXTAREA.getText().length()-tmp.length(); i++)
			{
				if ((TEXTAREA.getText()).substring(i, i+tmp.length()).compareTo(tmp) == 0)
				{
					TEXTAREA.setSelectionStart(i);
					TEXTAREA.setSelectionEnd(i+tmp.length());
					timematch++;
					tmp = JOptionPane.showInputDialog(null, "Enter the word(s) you want to find:", tmp) + "";
					if (tmp != null)
					{
						continue;
					}
					else
					{
						tmp = null;
						uim.put("OptionPane.okButtonText", "OK");
						uim.put("OptionPane.cancelButtonText", "NO");
						break;
					}
				}
			}
			uim.put("OptionPane.okButtonText", "OK");
			if (timematch == 1)
			{
				tmp = "Aborted or reached the end of the file! 1 result.";
			}
			else if (timematch == 0)
			{
				tmp = "Aborted or reached the end of the file! NO result.";
			}
			else
			{
				tmp = "Aborted or reached the end of the file! " + timematch + " results.";
			}
			JOptionPane.showMessageDialog(null, tmp, "End", JOptionPane.WARNING_MESSAGE);
		}
		tmp = null;
		uim.put("OptionPane.okButtonText", "OK");
		uim.put("OptionPane.cancelButtonText", "NO");
	}
	
	public void ReplaceWord()
	{
		i = 0;
		timematch = 0;
		uim.put("OptionPane.okButtonText", "Enter");
		uim.put("OptionPane.cancelButtonText", "Cancel");
		tmp = JOptionPane.showInputDialog(null, "Enter the word(s) you want to be replaced:");
		if (tmp != null)
		{
			tmp1 = JOptionPane.showInputDialog(null, "Enter the new word(s):");
			if (tmp1 != null)
			{
				for (i=i; i<=TEXTAREA.getText().length()-tmp.length(); i++)
				{
					if ((TEXTAREA.getText()).substring(i, i+tmp.length()).compareTo(tmp) == 0)
					{
						TEXTAREA.setSelectionStart(i);
						TEXTAREA.setSelectionEnd(i+tmp.length());
						TEXTAREA.replaceSelection(tmp1);
						i+=tmp1.length();
						timematch++;
					}
				}
				uim.put("OptionPane.okButtonText", "OK");
				if (timematch == 1)
				{
					tmp = "One word replaced!";
				}
				else if (timematch == 0)
				{
					tmp = "NO word replaced!.";
				}
				else
				{
					tmp = timematch + " word replaced!";
				}
				JOptionPane.showMessageDialog(null, tmp, "End", JOptionPane.WARNING_MESSAGE);
			}
		}
		tmp = null;
		tmp1 = null;
		uim.put("OptionPane.okButtonText", "OK");
		uim.put("OptionPane.cancelButtonText", "NO");
	}

	public void SwitchBackup()
	{
		for (i=9; i>=2; i--)
		{
			backup[i] = backup[i-1];
		}
		backup[1] = TEXTAREA.getText();
		wordamended = 0;
	}
	
	class MouseLis extends MouseAdapter
	{
		int select;
		public MouseLis(int select)
		{
			this.select = select;
		}		
		
		public void mouseExited(MouseEvent e)
		{
			switch (select)
			{
				case 4:
				newfile.setText("New");
				ready = false;
				break;
				
				case 2:
				save.setText("Save");
				break;
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			switch (select)
			{
				case 2:
				if (file == null)
				{
				}
				else
				{
					Savefile();
					save.setText("Saved");
					break;
				}
				
				case 1:
				SaveChooser();
				break;
				
				case 3:
				OpenChooser();
				break;
				
				case 4:
				if (ready)
				{
					Newfile();
					newfile.setText("New");
					ready = false;
				}
				else
				{
					newfile.setText("Really?");
					ready = true;
				}
				break;
				
				case 5:
				System.exit(0);
				break;
				
				case 6:
				Newfile();
				break;
				
				case 7:
				if (TEXTAREA.isEditable())
				{
					TEXTAREA.setEditable(false);
					TEXTAREA.setBackground(grey);
				}
				else
				{
					TEXTAREA.setEditable(true);
					TEXTAREA.setBackground(Color.WHITE);
				}
				break;
				
				case 8:
				if (TEXTAREA.getLineWrap())
				{
					TEXTAREA.setLineWrap(false);
				}
				else
				{
					TEXTAREA.setLineWrap(true);
				}
				break;
				
				case 9:
				JOptionPane.showConfirmDialog(null, "RefluxEdit 1.0 is a very small text editor written in Java.\nBy tony200910041\nhttp://tony200910041.wordpress.com\nDistributed under MPL2.0.", "About RefluxEdit", JOptionPane.PLAIN_MESSAGE);
				break;
				
				//Select all
				case 10:
				TEXTAREA.selectAll();
				break;
				
				//Select all and copy
				case 11:
				TEXTAREA.selectAll();
				systemclipboard.setContents(new StringSelection(TEXTAREA.getText()), null);
				break;
				
				//Copy
				case 12:
				if (TEXTAREA.getSelectedText() != null)
				{
					systemclipboard.setContents(new StringSelection(TEXTAREA.getSelectedText()), null);
				}
				break;
				
				//Paste
				case 13:
				if (TEXTAREA.isEditable())
				{
					tmp2 = systemclipboard.getContents(this);
					try
					{
						SwitchBackup();
						TEXTAREA.insert(tmp2.getTransferData(DataFlavor.stringFlavor) + "", TEXTAREA.getCaretPosition());
					}
					catch (Exception ex)
					{
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Editing the text is DISABLED!\nPlease enable editing before pasting word(s)!", "NOT Editable", JOptionPane.WARNING_MESSAGE);
				}
				break;
				
				//Cut
				case 14:
				if ((TEXTAREA.getSelectedText() != null)&&(TEXTAREA.isEditable()))
				{
					SwitchBackup();
					systemclipboard.setContents(new StringSelection(TEXTAREA.getSelectedText()), null);
					TEXTAREA.replaceSelection(null);
				}
				else if (!TEXTAREA.isEditable())
				{
					JOptionPane.showMessageDialog(null, "Editing the text is DISABLED!\nPlease enable editing before cutting word(s)!", "NOT Editable", JOptionPane.WARNING_MESSAGE);
				}
				break;
				
				//Paste on next line
				case 15:
				if (TEXTAREA.isEditable())
				{
					tmp2 = systemclipboard.getContents(this);
					try
					{
						SwitchBackup();
						TEXTAREA.insert("\n" + tmp2.getTransferData(DataFlavor.stringFlavor) + "", TEXTAREA.getCaretPosition());
					}
					catch (Exception ex)
					{
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Editing the text is DISABLED!\nPlease enable editing before pasting word(s)!", "NOT Editable", JOptionPane.WARNING_MESSAGE);
				}
				break;
				
				//Delete
				case 16:
				if (TEXTAREA.isEditable())
				{
					TEXTAREA.replaceSelection(null);
					SwitchBackup();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Editing the text is DISABLED!\nPlease enable editing before deleting word(s)!", "NOT Editable", JOptionPane.WARNING_MESSAGE);
				}
				break;
				
				//uppercase
				case 17:
				if (TEXTAREA.isEditable())
				{
					SwitchBackup();
					tmp3 = TEXTAREA.getSelectedText();
					tmp3 = tmp3.replace("a", "A");
					tmp3 = tmp3.replace("b", "B");
					tmp3 = tmp3.replace("c", "C");
					tmp3 = tmp3.replace("d", "D");
					tmp3 = tmp3.replace("e", "E");
					tmp3 = tmp3.replace("f", "F");
					tmp3 = tmp3.replace("g", "G");
					tmp3 = tmp3.replace("h", "H");
					tmp3 = tmp3.replace("i", "I");
					tmp3 = tmp3.replace("j", "J");
					tmp3 = tmp3.replace("k", "K");
					tmp3 = tmp3.replace("l", "L");
					tmp3 = tmp3.replace("m", "M");
					tmp3 = tmp3.replace("n", "N");
					tmp3 = tmp3.replace("o", "O");
					tmp3 = tmp3.replace("p", "P");
					tmp3 = tmp3.replace("q", "Q");
					tmp3 = tmp3.replace("r", "R");
					tmp3 = tmp3.replace("s", "S");
					tmp3 = tmp3.replace("t", "T");
					tmp3 = tmp3.replace("u", "U");
					tmp3 = tmp3.replace("v", "V");
					tmp3 = tmp3.replace("w", "W");
					tmp3 = tmp3.replace("x", "X");
					tmp3 = tmp3.replace("y", "Y");
					tmp3 = tmp3.replace("z", "Z");
					TEXTAREA.replaceSelection(tmp3);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Editing the text is DISABLED!\nPlease enable editing before case conversion!", "NOT Editable", JOptionPane.WARNING_MESSAGE);
				}
				break;
				
				//lowercase
				case 18:
				if (TEXTAREA.isEditable())
				{
					SwitchBackup();
					tmp3 = TEXTAREA.getSelectedText();
					tmp3 = tmp3.replace("A", "a");
					tmp3 = tmp3.replace("B", "b");
					tmp3 = tmp3.replace("C", "c");
					tmp3 = tmp3.replace("D", "d");
					tmp3 = tmp3.replace("E", "e");
					tmp3 = tmp3.replace("F", "f");
					tmp3 = tmp3.replace("G", "g");
					tmp3 = tmp3.replace("H", "h");
					tmp3 = tmp3.replace("I", "i");
					tmp3 = tmp3.replace("J", "j");
					tmp3 = tmp3.replace("K", "k");
					tmp3 = tmp3.replace("L", "l");
					tmp3 = tmp3.replace("M", "m");
					tmp3 = tmp3.replace("N", "n");
					tmp3 = tmp3.replace("O", "o");
					tmp3 = tmp3.replace("P", "p");
					tmp3 = tmp3.replace("Q", "q");
					tmp3 = tmp3.replace("R", "r");
					tmp3 = tmp3.replace("S", "s");
					tmp3 = tmp3.replace("T", "t");
					tmp3 = tmp3.replace("U", "u");
					tmp3 = tmp3.replace("V", "v");
					tmp3 = tmp3.replace("W", "w");
					tmp3 = tmp3.replace("X", "x");
					tmp3 = tmp3.replace("Y", "y");
					tmp3 = tmp3.replace("Z", "x");
					TEXTAREA.replaceSelection(tmp3);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Editing the text is DISABLED!\nPlease enable editing before case conversion!", "NOT Editable", JOptionPane.WARNING_MESSAGE);
				}
				break;
				
				//search
				case 19:
				SearchWord();				
				break;
				
				case 20:
				if (TEXTAREA.isEditable())
				{
					ReplaceWord();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Editing the text is DISABLED!\nPlease enable editing before replacing words!", "NOT Editable", JOptionPane.WARNING_MESSAGE);
				}
				break;
				
				case 21:
				if (TEXTAREA.getWrapStyleWord())
				{
					TEXTAREA.setWrapStyleWord(false);
				}
				else
				{
					TEXTAREA.setWrapStyleWord(true);
				}
				break;
				
				case 22:
				if (TEXTAREA.isEditable())
				{
					TEXTAREA.append("\n==========");
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Editing the text is DISABLED!\nPlease enable editing before inserting characters!", "NOT Editable", JOptionPane.WARNING_MESSAGE);
				}
				break;
				
				case 23:
				if (TEXTAREA.isEditable())
				{
					TEXTAREA.append("    ");
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Editing the text is DISABLED!\nPlease enable editing before inserting characters!", "NOT Editable", JOptionPane.WARNING_MESSAGE);
				}
				break;
				
				//undo: 0: newest; 9: oldest
				case 24:
				if (currentstring == 0)
				{
					backup[0] = TEXTAREA.getText();
				}
				if (backup[currentstring+1] != "")
				{
					if (currentstring != 9)
					{
						currentstring++;
						TEXTAREA.setText(backup[currentstring]);
					}
				}
				break;
				
				//redo
				case 25:
				if (currentstring != 0)
				{
					currentstring--;
					TEXTAREA.setText(backup[currentstring]);
				}
				break;
				
				case 26:
				if (TEXTAREA.isEditable())
				{
					SwitchBackup();
					str = "";
					j = TEXTAREA.getLineCount();
					for (i=j; i>=2; i--)
					{
						//i: the no of "\n"
						for (k=1; k<=i; k++)
						{
							str = str + "\n";
						}
						TEXTAREA.setText(TEXTAREA.getText().replace(str, "\n"));
						str = "";
					}
					str = null;
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Editing the text is DISABLED!\nPlease enable editing before deleting blank lines!", "NOT Editable", JOptionPane.WARNING_MESSAGE);
				}
				break;
			}
		}
	}
}
