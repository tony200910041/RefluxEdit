import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import javax.swing.* ;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.ColorUIResource;

import java.io.*;
import java.util.Properties;
import java.util.Locale;

public class RefluxEdit extends JFrame
{
	//values:
	int i, j, k, l, timematch, confirmvalue, returnvalue;
	
	byte wordamended = 0;
	byte currentstring = 0;
	
	boolean ready = false;
	
	//Conponents:
	JButton saveas = new JButton("Save As");
	JButton save = new JButton("Save");
	JButton open = new JButton("Open");
	JButton newfile = new JButton("New");
	
	JLabel current = new JLabel("\n");
	
	JTextArea TEXTAREA = new JTextArea(12, 30);
	JScrollPane jsp = new JScrollPane(TEXTAREA);
	
	JFileChooser chooser = new JFileChooser();
	FileNameExtensionFilter[] filter1 = {new FileNameExtensionFilter("Text files", "txt", "ini", "log", "java", "py", "bat", "cmd", "htm", "html", "xml", "php"), new FileNameExtensionFilter("Text (*.txt, *.ini, *.log)", "txt", "ini", "log"), new FileNameExtensionFilter("Command (*.bat, *.cmd)", "bat", "cmd"), new FileNameExtensionFilter("Website and programming (*.htm, *.html, *.xml, *.php, *.java, *.py)", "htm", "html", "xml", "php", "java", "py")};
	
	//GUI:
	static RefluxEdit w;
	Font J1 = new Font("Microsoft Jhenghei", Font.PLAIN, 12);
	Font J2 = new Font("Microsoft Jhenghei", Font.PLAIN, 15);
	
	Border bord1 = new LineBorder(Color.BLACK, 1);

	Color darkblue = new Color(37,47,104);
	Color brown = new Color(120,77,26);
	
	//others:
	File file = null;
	File chooserdefault = new File("./");
	File SettingsFile = new File(getJARPath() + "\\REFLUXEDITPREF.PROPERTIES\\");
	Properties prop = new Properties();

	String str = "";
	String tmp;
	String tmp1;
	String tmp3;
	
	String[] backup = {"", "", "", "", "", "", "", "", "", ""};
	String[] arg;
	
	Transferable tmp2;		
	Clipboard systemclipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	UIManager uim = new UIManager();
	
	public static void main(String[] args)
	{
		w = new RefluxEdit(args);
		w.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		w.setMinimumSize(new Dimension(275,250));
		w.setTitle("RefluxEdit 1.2");
		w.setResizable(true);
		w.setVisible(true);
		w.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent ev)
			{
				WriteSize();
				Object[] options = {"YES", "NO"};
				int close = JOptionPane.showOptionDialog(null, "Do you really want to close RefluxEdit?", "Confirm exit", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
				if (close == JOptionPane.YES_OPTION)
				{
					System.exit(0);
				}
			}
		});
	}
	
	public RefluxEdit(String[] s)
	{
		//create setting file if not exists
		if (!SettingsFile.exists())
		{
			try
			{
				PrintWriter writer = new PrintWriter(SettingsFile, "UTF-8");
				writer.close();
				WriteConfig("LineWrap", "true");
				WriteConfig("WrapByWord", "true");
				WriteConfig("Editing", "true");
				WriteConfig("Location.x", "0");
				WriteConfig("Location.y", "0");
				WriteConfig("Size.x", "460");
				WriteConfig("Size.y", "395");
				WriteConfig("AutoAddTxt", "true");
			}
			catch (Exception ex)
			{
			}
		}
		//restore settings
		try
		{
			TEXTAREA.setLineWrap(Boolean.parseBoolean(getConfig("LineWrap")));
		}
		catch (Exception ex)
		{
		}
		try
		{	
			TEXTAREA.setWrapStyleWord(Boolean.parseBoolean(getConfig("WrapByWord")));
		}
		catch (Exception ex)
		{
		}
		try
		{
			if (Boolean.parseBoolean(getConfig("Editing")))
			{
				TEXTAREA.setBackground(Color.WHITE);
				TEXTAREA.setEditable(true);
			}
			else
			{
				TEXTAREA.setBackground(new Color(245,245,245));
				TEXTAREA.setEditable(false);
			}
		}
		catch (Exception ex)
		{
		}
		/*System.out.println(getConfig("Location.x"));
		System.out.println(getConfig("Location.y"));
		System.out.println(getConfig("Size.x"));
		System.out.println(getConfig("Size.y"));*/
		try
		{
			i = (int)Double.parseDouble(getConfig("Size.x"));
			j = (int)Double.parseDouble(getConfig("Size.y"));
			if (i > (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth())
			{
				i = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			}		
			if (j > (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight())
			{
				j = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			}
		}
		catch (Exception ex)
		{
			i = 0;
			j = 0;
		}
		if ((i>=275)&&(j>=250))
		{
			this.setSize(i, j);
		}
		else
		{
			this.setSize(275, 250);
		}
		
		try
		{
			k = (int)Double.parseDouble(getConfig("Location.x"));
			l = (int)Double.parseDouble(getConfig("Location.y"));
			if ((k+i) > (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth())
			{
				k = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()-i;
			}
			if ((l+j) > (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight())
			{
				l = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()-j;
			}
		}
		catch (Exception ex)
		{
			k = 0;
			l = 0;
		}	
		if ((k>=0)&&(l>=0))
		{
			this.setLocation(k, l);
		}
		else
		{
			this.setLocation(0, 0);
		}
		
		//set language: English
		JComponent.setDefaultLocale(Locale.ENGLISH);
		chooser = new JFileChooser();
		
		//Menu bar:
		JMenuBar menubar1 = new JMenuBar();
		menubar1.setFont(J1);
		menubar1.setBackground(new Color(242,254,255));
		
		JMenu menu1 = new JMenu("File");
		menu1.setMnemonic(KeyEvent.VK_F);
		menu1.setFont(J1);
		JMenuItem item1 = new JMenuItem("New File       ");
		JMenuItem item2 = new JMenuItem("Open File");
		JMenuItem item3 = new JMenuItem("Save As...");
		JMenuItem item4 = new JMenuItem("Save");
		JMenuItem item5 = new JMenuItem("Close");
		JMenuItem item29 = new JMenuItem("Open File (Quick)");
		item1.setFont(J1);
		item2.setFont(J1);
		item3.setFont(J1);
		item4.setFont(J1);
		item5.setFont(J1);
		item29.setFont(J1);
		item1.setBackground(Color.WHITE);
		item2.setBackground(Color.WHITE);
		item3.setBackground(Color.WHITE);
		item4.setBackground(Color.WHITE);
		item5.setBackground(Color.WHITE);
		item29.setBackground(Color.WHITE);
		
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
		JMenuItem item26 = new JMenuItem("Insert key words (html)");
		JMenuItem item27 = new JMenuItem("Insert key words (Java)");
		JMenuItem item28 = new JMenuItem("Generate random words");
		JMenuItem item30 = new JMenuItem("Convert to invert case");
		JMenuItem item31 = new JMenuItem("Disable/Enable automatically add .txt when saving");
		
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
		item26.setFont(J1);
		item27.setFont(J1);
		item28.setFont(J1);
		item30.setFont(J1);
		item31.setFont(J1);
				
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
		item26.setBackground(Color.WHITE);
		item27.setBackground(Color.WHITE);
		item28.setBackground(Color.WHITE);
		item30.setBackground(Color.WHITE);
		item31.setBackground(Color.WHITE);
		
		menu1.add(item1);
		menu1.add(item2);
		menu1.add(item29);
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
		menu4.add(item31);
		menu4.add(new JSeparator());
		menu4.add(item18);
		menu4.add(item19);
		menu4.add(new JSeparator());
		menu4.add(item16);
		menu4.add(item17);
		menu4.add(item30);	
		menu4.add(new JSeparator());
		menu4.add(item21);
		menu4.add(item22);
		menu4.add(item26);
		menu4.add(item27);
		menu4.add(item28);
		menu4.add(item25);
		
		menu3.add(item8);
		
		menubar1.add(new JLabel(" "));
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
		item26.addMouseListener(new MouseLis(27));
		item27.addMouseListener(new MouseLis(28));
		item28.addMouseListener(new MouseLis(29));
		item29.addMouseListener(new MouseLis(30));
		item30.addMouseListener(new MouseLis(31));
		item31.addMouseListener(new MouseLis(32));
		
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
		TEXTAREA.setTabSize(3);
		
		current.setFont(J1);	
		
		setFileChooserStyle(chooser.getComponents());
		chooser.setPreferredSize(new Dimension(520, 450));
		
		uim.put("OptionPane.buttonFont", J1);
		uim.put("OptionPane.messageFont", J1);		
		
		uim.put("OptionPane.okButtonText", "OK");
		
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
		
		jsp.getHorizontalScrollBar().setBackground(new Color(245,245,245));
		jsp.getHorizontalScrollBar().getComponent(0).setBackground(new Color(245,245,245));
		jsp.getHorizontalScrollBar().getComponent(1).setBackground(new Color(245,245,245));
		
		jsp.getVerticalScrollBar().setBackground(new Color(245,245,245));
		jsp.getVerticalScrollBar().getComponent(0).setBackground(new Color(245,245,245));
		jsp.getVerticalScrollBar().getComponent(1).setBackground(new Color(245,245,245));
		
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
		
		this.addWindowListener(new WindowAdapter()
		{
            public void windowClosing(WindowEvent ev)
            {
				WriteConfig("LineWrap", TEXTAREA.getLineWrap() + "");
				WriteConfig("WrapByWord", TEXTAREA.getWrapStyleWord() + "");
				WriteConfig("Editing", TEXTAREA.isEditable() + "");
			}
		});	
		
		try
		{
			this.setIconImage((new ImageIcon(getClass().getResource("/SRC/APPICON.PNG"))).getImage());
		}
		catch (Exception ex)
		{
		}
		
		//open file by command line/file association
		if (s.length == 1)
		{
			if ((new File(s[0])).exists())
			{
				file = new File(s[0]);
				Openfile();
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
			System.out.println(file);
			tmp = file + "";
			if (tmp.length() > 50)
			{
				tmp = tmp.substring(0, 25) + "..." + tmp.substring(tmp.length()-25, tmp.length());
			}
			current.setText("File: " + tmp);
			tmp = null;
			TEXTAREA.requestFocus();
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Cannot save file!\nException message\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
			TEXTAREA.requestFocus();
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Cannot open file!\nException message:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
					if ((!(file + "").contains("."))&&(Boolean.parseBoolean(getConfig("AutoAddTxt"))))
					{
						file = new File(file + ".txt");
					}
					PrintWriter pw1 = new PrintWriter(file, "UTF-8");
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
					if (tmp.compareTo("null") != 0)
					{
						continue;
					}
					else
					{
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
	
	public void InsertKeyWord(Object[] options)
	{
		uim.put("OptionPane.cancelButtonText", "Cancel");
		uim.put("ComboBox.font", J1);
		tmp = (String)JOptionPane.showInputDialog(null, "Choose the key words you want to insert:", "Insert key words", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]); 
		uim.put("OptionPane.cancelButtonText", "NO");
		if (tmp != null)
		{
			SwitchBackup();
			TEXTAREA.insert(tmp, TEXTAREA.getCaretPosition());
			TEXTAREA.requestFocus();
		}
	}
	
	public void CannotParseInt()
	{
		JOptionPane.showMessageDialog(null, "Please enter a positive integer!", "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public String NumberToLetter(long a)
	{
		if (a == 1)
		{
			return "a";
		}
		if (a == 2)
		{
			return "b";
		}
		if (a == 3)
		{
			return "c";
		}
		if (a == 4)
		{
			return "d";
		}
		if (a == 5)
		{
			return "e";
		}
		if (a == 6)
		{
			return "f";
		}
		if (a == 7)
		{
			return "g";
		}
		if (a == 8)
		{
			return "h";
		}
		if (a == 9)
		{
			return "i";
		}
		if (a == 10)
		{
			return "j";
		}
		if (a == 11)
		{
			return "k";
		}
		if (a == 12)
		{
			return "l";
		}
		if (a == 13)
		{
			return "m";
		}
		if (a == 14)
		{
			return "n";
		}
		if (a == 15)
		{
			return "o";
		}
		if (a == 16)
		{
			return "p";
		}
		if (a == 17)
		{
			return "q";
		}
		if (a == 18)
		{
			return "r";
		}
		if (a == 19)
		{
			return "s";
		}
		if (a == 20)
		{
			return "t";
		}
		if (a == 21)
		{
			return "u";
		}
		if (a == 22)
		{
			return "v";
		}
		if (a == 23)
		{
			return "w";
		}
		if (a == 24)
		{
			return "x";
		}
		if (a == 25)
		{
			return "y";
		}
		else
		{
			return "z";
		}
	}
	
	public void ShowNotEditable(String str)
	{
		JOptionPane.showMessageDialog(null, "Editing the text is DISABLED!\nPlease enable editing before " + str, "NOT Editable", JOptionPane.WARNING_MESSAGE);
	}
	
	public String UC(String str)
	{
		str = str.replace("a", "A");
		str = str.replace("b", "B");
		str = str.replace("c", "C");
		str = str.replace("d", "D");
		str = str.replace("e", "E");
		str = str.replace("f", "F");
		str = str.replace("g", "G");
		str = str.replace("h", "H");
		str = str.replace("i", "I");
		str = str.replace("j", "J");
		str = str.replace("k", "K");
		str = str.replace("l", "L");
		str = str.replace("m", "M");
		str = str.replace("n", "N");
		str = str.replace("o", "O");
		str = str.replace("p", "P");
		str = str.replace("q", "Q");
		str = str.replace("r", "R");
		str = str.replace("s", "S");
		str = str.replace("t", "T");
		str = str.replace("u", "U");
		str = str.replace("v", "V");
		str = str.replace("w", "W");
		str = str.replace("x", "X");
		str = str.replace("y", "Y");
		str = str.replace("z", "Z");
		return str;
	}
	
	public String LC(String str)
	{
		str = str.replace("A", "a");
		str = str.replace("B", "b");
		str = str.replace("C", "c");
		str = str.replace("D", "d");
		str = str.replace("E", "e");
		str = str.replace("F", "f");
		str = str.replace("G", "g");
		str = str.replace("H", "h");
		str = str.replace("I", "i");
		str = str.replace("J", "j");
		str = str.replace("K", "k");
		str = str.replace("L", "l");
		str = str.replace("M", "m");
		str = str.replace("N", "n");
		str = str.replace("O", "o");
		str = str.replace("P", "p");
		str = str.replace("Q", "q");
		str = str.replace("R", "r");
		str = str.replace("S", "s");
		str = str.replace("T", "t");
		str = str.replace("U", "u");
		str = str.replace("V", "v");
		str = str.replace("W", "w");
		str = str.replace("X", "x");
		str = str.replace("Y", "y");
		str = str.replace("Z", "x");
		return str;
	}
	
	public String IC(String str)
	{
		tmp1 = "";
		for (i=0; i<str.length(); i++)
		{
			char TMP_C = str.charAt(i);
			if (Character.isUpperCase(TMP_C))
			{
				tmp1 = tmp1 + LC(TMP_C + "");
				System.out.println("Convert to lowercase: " + tmp1);
			}
			else if (Character.isLowerCase(TMP_C))
			{
				tmp1 = tmp1 + UC(TMP_C + "");
				System.out.println("Convert to uppercase: " + tmp1);
			}
			else
			{
				tmp1 = tmp1 + TMP_C;
				System.out.println("No conversion: " + tmp1);
			}	
		}
		return tmp1;
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
				WriteConfig("LineWrap", TEXTAREA.getLineWrap() + "");
				WriteConfig("WrapByWord", TEXTAREA.getWrapStyleWord() + "");
				WriteConfig("Editing", TEXTAREA.isEditable() + "");
				/*WriteConfig("Size.x", w.getSize().getWidth() + "");
				WriteConfig("Size.y", w.getSize().getHeight() + "");
				WriteConfig("Location.x", w.getLocation().getX() + "");
				WriteConfig("Location.y", w.getLocation().getY() + "");*/
				WriteSize();
				System.exit(0);
				break;
				
				case 6:
				Newfile();
				break;
				
				case 7:
				if (TEXTAREA.isEditable())
				{
					TEXTAREA.setEditable(false);
					TEXTAREA.setBackground(new Color(245,245,245));
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
				JOptionPane.showConfirmDialog(null, "RefluxEdit 1.2 is a very small text editor written in Java.\nBy tony200910041\nhttp://tony200910041.wordpress.com\nDistributed under MPL2.0.", "About RefluxEdit", JOptionPane.PLAIN_MESSAGE);
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
						TEXTAREA.requestFocus();
					}
					catch (Exception ex)
					{
					}
				}
				else
				{
					ShowNotEditable("pasting word(s)");
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
					ShowNotEditable("cutting word(s)!");
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
						TEXTAREA.requestFocus();
					}
					catch (Exception ex)
					{
					}
				}
				else
				{
					ShowNotEditable("pasting word(s)");
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
					ShowNotEditable("deleting word(s)");
				}
				break;
				
				//uppercase
				case 17:
				if (TEXTAREA.isEditable())
				{
					SwitchBackup();
					TEXTAREA.replaceSelection(UC(TEXTAREA.getSelectedText()));
				}
				else
				{
					ShowNotEditable("case conversion!");
				}
				break;
				
				//lowercase
				case 18:
				if (TEXTAREA.isEditable())
				{
					SwitchBackup();					
					TEXTAREA.replaceSelection(LC(TEXTAREA.getSelectedText()));
				}
				else
				{
					ShowNotEditable("case conversion!");
				}
				break;
				
				//search
				case 19:
				SearchWord();				
				break;
				
				//replace
				case 20:
				if (TEXTAREA.isEditable())
				{
					ReplaceWord();
				}
				else
				{
					ShowNotEditable("replacing word(s)!");
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
				
				//insert 10 equal signs
				case 22:
				if (TEXTAREA.isEditable())
				{
					TEXTAREA.insert("\n==========\n", TEXTAREA.getCaretPosition());
				}
				else
				{
					ShowNotEditable("inserting characters!");
				}
				break;
				
				//insert four space
				case 23:
				if (TEXTAREA.isEditable())
				{
					TEXTAREA.insert("    ", TEXTAREA.getCaretPosition());
				}
				else
				{
					ShowNotEditable("inserting characters!");
				}
				break;
				
				//undo: 0: newest; 9: oldest
				case 24:
				if (TEXTAREA.isEditable())
				{
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
				}
				else
				{
					ShowNotEditable("performing undo operation!");
				}
				break;
				
				//redo
				case 25:
				if (TEXTAREA.isEditable())
				{
					if (currentstring != 0)
					{
						currentstring--;
						TEXTAREA.setText(backup[currentstring]);
					}
				}
				else
				{
					ShowNotEditable("performing redo operation!");
				}
				break;
				
				//delete blank lines
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
					 ShowNotEditable("deleting blank line(s)!");
				}
				break;
				
				//Insert keywords (html):
				case 27:
				if (TEXTAREA.isEditable())
				{
					Object[] options1 = {"<a href=\"", "</a>", "<img alt=\"\" src=\"", "\n<br>\n", "target=\"_blank\""};
					InsertKeyWord(options1);
				}
				else
				{
					ShowNotEditable("inserting characters!");
				}
				break;
				
				//Insert keywords (Java):
				case 28:
				if (TEXTAREA.isEditable())
				{
					Object[] options2 = {"Integer.parseInt(", "Double.parseDouble(", ".setText(", ".getText(", ".setBackground(", ".setForeground(", "getContentPane()", "JOptionPane.showMessageDialog(null, \"", "import java.awt.*;\nimport javax.swing.*;\n", ".addMouseListener(new MouseAdapter() {\n", "public void mouseReleased(MouseEvent ev) {\n", "public static void main(String[] args) {\n", ".setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);\n", ".setLocationRelativeTo(null);\n", ".setVisible(true);\n"};
					InsertKeyWord(options2);
				}
				else
				{
					ShowNotEditable("inserting characters!");
				}
				break;
				
				//Generate random words
				case 29:
				if (TEXTAREA.isEditable())
				{
					uim.put("OptionPane.cancelButtonText", "Cancel");
					tmp = JOptionPane.showInputDialog(null, "Enter the number of words: (1-100000)", "Generate random words", JOptionPane.QUESTION_MESSAGE);
					if (tmp != null)
					{
						try
						{
							k = Integer.parseInt(tmp);
						}
						catch (NumberFormatException ex)
						{
							CannotParseInt();
							break;
						}
						if (k < 1)
						{
							CannotParseInt();
							break;
						}
						if (k > 100000)
						{
							Object[] options2 = {"YES", "NO"};
							int cont = JOptionPane.showOptionDialog(null, "Generating more than 100000 random words may spend a long period of time.\nDo you want to continue?", "Confirm action", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options2, options2[1]);
							if (cont == JOptionPane.NO_OPTION)
							{
								break;
							}
						}
						SwitchBackup();
						str = "";
						TEXTAREA.insert("\n", TEXTAREA.getCaretPosition());
						for (j=1; j<=k; j++)
						{
							tmp = "";
							long m = Math.round(Math.random()*9+1);
							for (l=1; l<=m; l++)
							{
								TEXTAREA.insert(NumberToLetter(Math.round(Math.random()*25+1)), TEXTAREA.getCaretPosition());
							}
							TEXTAREA.insert(" ", TEXTAREA.getCaretPosition());
						}
					}
					TEXTAREA.requestFocus();
				}
				else
				{
					ShowNotEditable("inserting words!");
				}
				break;
				
				//open file (quick):
				case 30:
				uim.put("OptionPane.cancelButtonText", "Cancel");
				str = JOptionPane.showInputDialog(null, "Please enter the path:", "Open file (quick)", JOptionPane.QUESTION_MESSAGE);
				if (str != null)
				{
					file = new File(str);
					Openfile();
				}
				str = null;
				break;
				
				//Invert case:
				case 31:
				if (TEXTAREA.isEditable())
				{
					SwitchBackup();
					TEXTAREA.replaceSelection(IC(TEXTAREA.getSelectedText()));
				}
				else
				{
					ShowNotEditable("case conversion!");
				}
				break;
				
				case 32:
				{
					WriteConfig("AutoAddTxt", !Boolean.parseBoolean(getConfig("AutoAddTxt")) + "");
				}
				break;
			}
		}
	}
	
	public String getConfig(String name)
	{
		try
		{
			prop.load(new FileInputStream(SettingsFile));
		}
		catch (Exception ex)
		{
			return null;
		}
		String configstr = prop.getProperty(name);
		return configstr;
	}
	
	public void WriteConfig(String key, String value)
	{
		prop.setProperty(key, value);
		try
		{
			prop.store(new FileOutputStream(SettingsFile), null);
		}
		catch (Exception ex)
		{
		}
	}
	
	public File getJARPath()
	{
		try
		{			
			return new File((new File(RefluxEdit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getPath());
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	public void setFileChooserStyle(Component[] comp)  
	{		
		for (int x = 0; x < comp.length; x++)  
		{
			if (comp[x] instanceof Container)
			{
				setFileChooserStyle(((Container)comp[x]).getComponents());
			}
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
	
	public static void WriteSize()
	{
		w.WriteConfig("Size.x", w.getSize().getWidth() + "");
		w.WriteConfig("Size.y", w.getSize().getHeight() + "");
		w.WriteConfig("Location.x", w.getLocation().getX() + "");
		w.WriteConfig("Location.y", w.getLocation().getY() + "");
	}
}
