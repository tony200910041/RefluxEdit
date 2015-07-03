import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicMenuItemUI;
import java.io.*;
import java.util.Properties;
import java.util.Arrays;
import MyJava.MyFileChooser;
import MyJava.FileChooser;
import MyJava.MyFileFilter;
import MyJava.MyColorChooser;

public class RefluxEdit extends JFrame
{
	private static final float VERSION_NO = (float)2.0;
	private static final Font f13 = new Font("Microsoft Jhenghei", Font.PLAIN, 13);
	private static final File SettingsFile = new File(getSettingsFilePath() + "\\REFLUXEDITPREF.PROPERTIES\\");
	private static final Properties prop = new Properties();
	
	private static final int WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private static final int HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	JTextArea TEXTAREA = new JTextArea();
	JLabel currentFile = new JLabel(" ");
	JMenuBar menubar;
	
	static JFileChooser WindowsChooser;
	static MyFileChooser JavaChooser;
	static JFileChooser chooser;
	File file = null;
	
	private static final JPopupMenu popup = new JPopupMenu();
	private static final Clipboard clipbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	UndoManager undoManager = new UndoManager(20);
	static boolean isSaved = true;
	
	static RefluxEdit w;
	JPanel topPanel = new JPanel();
	JPanel bottomPanel = new JPanel();
	static int i, j, k, l, m;
	static String TMP1, TMP2, TMP3, TMP4;
	
	public static void main(final String[] args)
	{
		final double initialTime = System.currentTimeMillis();
		final SplashScreen splash = SplashScreen.getSplashScreen();
		splash.createGraphics();
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				w = new RefluxEdit("RefluxEdit " + VERSION_NO);
				w.restoreFrame();
				w.buildWindowsChooser();
				w.restoreTextArea();
				if (args.length == 1)
				{
					try
					{
						w.openToTextArea(new File(args[0]));
					}
					catch (Exception ex)
					{
					}
				}
				(new SwingWorker<Void, Void>()
				{
					@Override
					public Void doInBackground()
					{
						w.restoreChoosers();
						return null;
					}
				}).execute();				
				
				(new SwingWorker<Void, Void>()
				{
					@Override
					public Void doInBackground()
					{
						w.restoreMenus();
						return null;
					}
				}).execute();
				
				(new SwingWorker<Void, Void>()
				{
					@Override
					public Void doInBackground()
					{
						w.restorePopup();
						return null;
					}
				}).execute();
				
				splash.close();
				w.setVisible(true);
				w.writeConfig("LastStartupTimeTaken", System.currentTimeMillis()-initialTime + "ms");
			}
		});
	}
	
	public RefluxEdit(String title)
	{
		super(title);
		final RefluxEdit frame = this;
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setMinimumSize(new Dimension(275,250));
		this.setLayout(new BorderLayout());
		this.initialize();
		menubar = new JMenuBar();
		this.setJMenuBar(menubar);
		
		currentFile.setFont(f13);
		topPanel.add(new MyButton("New", -1));
		topPanel.add(new MyButton("Open", 2));
		topPanel.add(new MyButton("Save as", 4));
		topPanel.add(new MyButton("Save", 5));
		bottomPanel.add(currentFile);
		
		this.add(topPanel, BorderLayout.PAGE_START);
		this.add(bottomPanel, BorderLayout.PAGE_END);
		this.add(new JLabel("  "), BorderLayout.LINE_START);
		this.add(new JLabel("  "), BorderLayout.LINE_END);
		this.add(new JScrollPane(TEXTAREA), BorderLayout.CENTER);
		
		UIManager.put("OptionPane.messageFont", f13);
		UIManager.put("OptionPane.buttonFont", f13);
		UIManager.put("OptionPane.okButtonText", "OK");
		UIManager.put("OptionPane.yesButtonText", "YES");
		UIManager.put("OptionPane.noButtonText", "NO");
		UIManager.put("Button.background", Color.WHITE);
		UIManager.put("ComboBox.font", f13);
		UIManager.put("MenuItem.acceleratorForeground", new Color(34,131,132));
		UIManager.put("MenuItem.selectionBackground", new Color(220,220,220));
		UIManager.put("PopupMenu.border", new LineBorder(Color.BLACK, 1));
		UIManager.put("Separator.foreground", Color.BLACK);
		
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent ev)
			{
				if (isSaved)
				{
					TMP1 = "Do you really want to close RefluxEdit?";
				}
				else
				{
					TMP1 = "NOT YET SAVED!\nDo you really want to close RefluxEdit?";
				}
				int x = JOptionPane.showConfirmDialog(frame, TMP1, "Confirm close", JOptionPane.YES_NO_OPTION);
				if (x == JOptionPane.YES_OPTION)
				{
					writeConfig("Size.x", getSize().getWidth() + "");
					writeConfig("Size.y", getSize().getHeight() + "");
					writeConfig("Location.x", getLocation().getX() + "");
					writeConfig("Location.y", getLocation().getY() + "");
					System.exit(0);
				}
			}
		});
	}
	
	public void initialize()
	{
		//this.setSize(300,600);
		//this.setLocationRelativeTo(null);
		//size
		if (!SettingsFile.exists())
		{
			try
			{
				PrintWriter writer = new PrintWriter(SettingsFile, "UTF-8");
				writer.close();
				writeConfig("Size.x", "460");
				writeConfig("Size.y", "395");
				writeConfig("Location.x", "0");
				writeConfig("Location.y", "0");
				writeConfig("isEditable", "true");
				writeConfig("LineWrap", "true");
				writeConfig("WrapStyleWord", "true");
				writeConfig("Encoding", "default");
				writeConfig("ChooserStyle", "Java");
				writeConfig("OnTop", "false");
				writeConfig("TabSize", "4");
				writeConfig("SelectionColor.r", "244");
				writeConfig("SelectionColor.g", "223");
				writeConfig("SelectionColor.b", "255");
			}
			catch (Exception ex)
			{
			}
		}
	}
	
	public void restoreFrame()
	{
		try
		{
			i = (int)Double.parseDouble(getConfig("Size.x"));
			j = (int)Double.parseDouble(getConfig("Size.y"));
			if (i > WIDTH)
			{
				i = WIDTH;
			}		
			if (j > HEIGHT)
			{
				j = HEIGHT;
			}
		}
		catch (Exception ex)
		{
		}
		if ((i>=275)&&(j>=250))
		{
			this.setSize(i, j);
		}
		else
		{
			this.setSize(275,250);
		}
		
		//location
		try
		{
			k = (int)Double.parseDouble(getConfig("Location.x"));
			l = (int)Double.parseDouble(getConfig("Location.y"));
			if ((k+i) > WIDTH)
			{
				k = WIDTH-i;
			}
			if ((l+j) > HEIGHT)
			{
				l = HEIGHT-j;
			}
		}
		catch (Exception ex)
		{
		}	
		if ((k>=0)&&(l>=0))
		{
			this.setLocation(k, l);
		}
		else
		{
			this.setLocation(0, 0);
		}
		//ontop
		try
		{
			this.setAlwaysOnTop(getConfig("OnTop").equals("true"));
		}
		catch (Exception ex)
		{
			this.setAlwaysOnTop(false);
		}
		//icon
		try
		{
			this.setIconImage((new ImageIcon(getClass().getResource("/MyJava/SRC/APPICON.PNG"))).getImage());
		}
		catch (Exception ex)
		{
		}
	}
	
	public void buildWindowsChooser()
	{
		LookAndFeel LAF = UIManager.getLookAndFeel();
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			WindowsChooser = new JFileChooser();
			UIManager.setLookAndFeel(LAF);
			LAF = null;
		}
		catch (Throwable ex)
		{
			WindowsChooser = new MyFileChooser("Choose a text file:");
		}
	}
	
	public void restoreChoosers()
	{
		JComponent.setDefaultLocale(java.util.Locale.ENGLISH);
		JavaChooser = new MyFileChooser("Choose a text file:");		
		JavaChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text file", new String[]{"txt", "java", "py", "php", "html", "htm", "xml", "bot", "properties"}));
		WindowsChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text file", new String[]{"txt", "java", "py", "php", "html", "htm", "xml", "bot", "properties"}));
		WindowsChooser.setPreferredSize(new Dimension(560,390));
		WindowsChooser.setDialogTitle("Choose a text file:");
		WindowsChooser.setCurrentDirectory(new File("./"));
		FileChooser.addFileFilter(new MyFileFilter()
		{
			@Override
			public boolean accept(File f)
			{
				String FILE = f.getPath().toLowerCase();
				return (FILE.endsWith("txt"))||(FILE.endsWith("java"))||(FILE.endsWith("py"))||(FILE.endsWith("php"))||(FILE.endsWith("html"))||(FILE.endsWith("htm"))||(FILE.endsWith("xml"))||(FILE.endsWith("bot"))||(FILE.endsWith("properties"));
			}
			
			@Override
			public String getDescription()
			{
				return new String("Text Files");
			}
		});
	}
	
	public void restoreMenus()
	{
		menubar.setBackground(new Color(242,254,255));
		MyMenu menu1 = new MyMenu("File");
		MyMenu menu2 = new MyMenu("Edit");
		MyMenu menu3 = new MyMenu("Tools");
		MyMenu menu4 = new MyMenu("Insert");
		MyMenu menu5 = new MyMenu("Help");
		
		menu1.add(new MyMenuItem("New file", "NEW", 1));
		menu1.add(new MyMenuItem("Open file", "OPEN", 2).setAccelerator(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menu1.add(new MyMenuItem("Open file (quick)", null, 3));
		menu1.add(new JSeparator());
		menu1.add(new MyMenuItem("Save as", "SAVE", 4).setAccelerator(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu1.add(new MyMenuItem("Save", null, 5));
		menu1.add(new JSeparator());
		menu1.add(new MyMenuItem("Close", "CLOSE", 6));
		
		menu2.add(new MyMenuItem("Undo", "UNDO", 7).setAccelerator(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		menu2.add(new MyMenuItem("Redo", "REDO", 8).setAccelerator(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		menu2.add(new JSeparator());
		menu2.add(new MyMenuItem("Select all", null, 9).setAccelerator(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menu2.add(new MyMenuItem("Select all and copy", null, 10));
		menu2.add(new JSeparator());
		menu2.add(new MyMenuItem("Cut", "CUT", 11).setAccelerator(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		menu2.add(new MyMenuItem("Copy", "COPY", 12).setAccelerator(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		menu2.add(new MyMenuItem("Paste", "PASTE", 13).setAccelerator(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		menu2.add(new MyMenuItem("Paste on next line", null, 14));
		menu2.add(new MyMenuItem("Delete", null, 15));
		
		menu5.add(new MyMenuItem("About RefluxEdit", "APPICON16", 16).setAccelerator(KeyEvent.VK_F1, ActionEvent.CTRL_MASK));
		
		menu3.add(new MyMenuItem("Enable/disable editing", null, 17));
		menu3.add(new MyMenuItem("Enable/disable always on top", null, 21));
		menu3.add(new JSeparator());
		menu3.add(new MyMenuItem("Line Wrap options", null, 18));
		menu3.add(new MyMenuItem("Encoding options", null, 19));
		menu3.add(new MyMenuItem("FileChooser options", null, 20));
		menu3.add(new MyMenuItem("Tab size options", null, 29));
		menu3.add(new MyMenuItem("Selection color options", null, 36));
		menu3.add(new JSeparator());
		menu3.add(new MyMenuItem("Word count (beta)", null, 22).setAccelerator(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));
		menu3.add(new MyMenuItem("Delete blank lines", null, 35));
		menu3.add(new JSeparator());
		menu3.add(new MyMenuItem("Search", "SEARCH", 23).setAccelerator(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		menu3.add(new MyMenuItem("Replace", null, 24));
		menu3.add(new MyMenuItem("Replace in selection", null, 25));
		menu3.add(new JSeparator());
		menu3.add(new MyMenuItem("Convert to upper case", "UPPERCASE", 26));
		menu3.add(new MyMenuItem("Convert to lower case", "LOWERCASE", 27));
		menu3.add(new MyMenuItem("Convert to invert case", "INVERTCASE", 28));
		
		menu4.add(new MyMenuItem("Insert ten equal signs", null, 30));
		menu4.add(new MyMenuItem("Insert four spaces", null, 31));
		menu4.add(new MyMenuItem("Generate random words", null, 32));
		menu4.add(new JSeparator());
		menu4.add(new MyMenuItem("Insert key words (Java)", "KEYWORDJAVA", 33));
		menu4.add(new MyMenuItem("Insert key words (html)", "KEYWORDHTML", 34));
		//next one: 37
	}
	
	public void restoreTextArea()
	{
		TEXTAREA.setFont(new Font(f13.getFontName(), f13.getStyle(), f13.getSize()+2));
		TEXTAREA.setText("");
		TEXTAREA.addKeyListener(new KeyAdapter()
		{
			int time = 0;
			@Override
			public void keyTyped(KeyEvent ev)
			{
				isSaved = false;
				time++;
				if (time == 10)
				{
					undoManager.backup(TEXTAREA.getText());
					time = 0;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent ev)
			{
				if (ev.isControlDown())
				{
					i = ev.getKeyCode();
					MyMenuItem menuItem = null;
					if (i == KeyEvent.VK_Z)
					{
						menuItem = new MyMenuItem(null, null, 7);
					}
					else if (i == KeyEvent.VK_Y)
					{
						menuItem = new MyMenuItem(null, null, 8);
					}
					else if (i == KeyEvent.VK_S)
					{
						menuItem = new MyMenuItem(null, null, 4);
					}
					else if (i == KeyEvent.VK_F1)
					{
						menuItem = new MyMenuItem(null, null, 16);
					}
					else if (i == KeyEvent.VK_F)
					{
						menuItem = new MyMenuItem(null, null, 23);
					}
					else if (i == KeyEvent.VK_O)
					{
						menuItem = new MyMenuItem(null, null, 2);
					}
					else if (i == KeyEvent.VK_F2)
					{
						menuItem = new MyMenuItem(null, null, 22);
					}
					try
					{
						menuItem.dispatchEvent(new MouseEvent(menuItem, MouseEvent.MOUSE_RELEASED, 1, MouseEvent.NOBUTTON, 0, 0, 1, false));
					}
					catch (Exception ex)
					{
					}
				}
			}
		});
		TEXTAREA.addMouseListener(new MyListener(0));
		//editable
		try
		{
			boolean isEditable = getConfig("isEditable").equals("true");
			if (isEditable)
			{
				TEXTAREA.setEditable(true);
				TEXTAREA.setBackground(Color.WHITE);
			}
			else
			{
				TEXTAREA.setEditable(false);
				TEXTAREA.setBackground(new Color(245,245,245));
			}
		}
		catch (Exception ex)
		{
			TEXTAREA.setEditable(true);
		}
		//wrapping
		try
		{
			TEXTAREA.setLineWrap(getConfig("LineWrap").equals("true"));
		}
		catch (Exception ex)
		{
			TEXTAREA.setLineWrap(true);
		}
		//wrap, word
		try
		{
			TEXTAREA.setWrapStyleWord(getConfig("WrapStyleWord").equals("true"));
		}
		catch (Exception ex)
		{
			TEXTAREA.setWrapStyleWord(true);
		}
		//tab size
		try
		{
			TEXTAREA.setTabSize(Integer.parseInt(getConfig("TabSize")));
		}
		catch (Exception ex)
		{
			TEXTAREA.setTabSize(4);
		}
		//selection color
		try
		{
			TEXTAREA.setSelectionColor(new Color(Short.parseShort(getConfig("SelectionColor.r")), Short.parseShort(getConfig("SelectionColor.g")), Short.parseShort(getConfig("SelectionColor.b"))));
		}
		catch (Exception ex)
		{
			TEXTAREA.setSelectionColor(new Color(244,223,255));
		}
	}
	
	public void restorePopup()
	{
		popup.add(new MyMenuItem("Cut", "CUT", 11));
		popup.add(new MyMenuItem("Copy", "COPY", 12));
		popup.add(new MyMenuItem("Paste", "PASTE", 13));
		popup.add(new MyMenuItem("Delete", null, 15));
	}
	
	class MyButton extends JButton
	{
		public MyButton(String str, int x)
		{
			super(str);
			this.setBackground(Color.WHITE);
			this.setForeground(new Color(120,77,26));
			this.setPreferredSize(new Dimension(60,30));
			this.setFocusable(false);
			this.setFont(f13);
			this.setBorder(new LineBorder(Color.BLACK, 1));
			this.addMouseListener(new MyListener(x));
		}
	}
	
	class MyLabel extends JLabel
	{
		public MyLabel(String str)
		{
			super(str);
			this.setFont(f13);
		}
	}
	
	class MyMenu extends JMenu
	{
		public MyMenu(String str)
		{
			super(str);
			this.setFont(f13);
			this.setBackground(Color.WHITE);
			this.setForeground(Color.BLACK);
			menubar.add(new JLabel(" "));
			menubar.add(this);
		}
	}
	
	class MyMenuItem extends JMenuItem
	{
		public MyMenuItem(String str, String icon, int x)
		{
			super(str);
			this.setFont(f13);
			this.setBackground(Color.WHITE);
			this.setForeground(Color.BLACK);
			this.addMouseListener(new MyListener(x));
			try
			{
				this.setIcon(new ImageIcon(getClass().getResource("/MyJava/SRC/" + icon + ".PNG")));
			}
			catch (Exception ex)
			{
			}
		}
		
		public MyMenuItem setAccelerator(int keyEvent, int actionEvent)
		{
			if (System.getProperty("os.name").toUpperCase().startsWith("WIN"))
			{
				this.setAccelerator(KeyStroke.getKeyStroke(keyEvent, actionEvent));
			}
			return this;
		}
	}
	
	class MyListener extends MouseAdapter
	{
		private int x;
		private boolean isOnNew = false;
		public MyListener(int x)
		{
			this.x = x;
		}
		
		public void cannotEdit()
		{
			JOptionPane.showMessageDialog(w, "Editing the text is DISABLED!\nPlease enable editing!", "Error", JOptionPane.WARNING_MESSAGE);
		}
		
		public void cannotOpen(Throwable ex)
		{
			JOptionPane.showMessageDialog(w, "Cannot open file!\nError message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		public int isOverride()
		{
			return JOptionPane.showConfirmDialog(w, "Override old file?", "Warning", JOptionPane.WARNING_MESSAGE);
		}
		
		public String toInvertCase(String str)
		{
			char c;
			char[] chars = str.toCharArray();
			for (i=0; i<chars.length; i++)
			{
				c = chars[i];
				if (Character.isUpperCase(c))
				{
					chars[i] = Character.toLowerCase(c);
				}
				else if (Character.isLowerCase(c))
				{
					chars[i] = Character.toUpperCase(c);
				}
			}
			return new String(chars);
		}
		
		public String toLetter(int a)
		{
			switch (a)
			{
				case 1: return "a";
				case 2: return "b";
				case 3: return "c";
				case 4: return "d";
				case 5: return "e";
				case 6: return "f";
				case 7: return "g";
				case 8: return "h";
				case 9: return "i";
				case 10: return "j";
				case 11: return "k";
				case 12: return "l";
				case 13: return "m";
				case 14: return "n";
				case 15: return "o";
				case 16: return "p";
				case 17: return "q";
				case 18: return "r";
				case 19: return "s";
				case 20: return "t";
				case 21: return "u";
				case 22: return "v";
				case 23: return "w";
				case 24: return "x";
				case 25: return "y";
				default:
				case 26: return "z";
			}
		}
		
		public String toLetter2(int a)
		{
			return ("abcdefghijklmnopqrstuvwxyz").substring(i-1, i);
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
		{
			if (x == -1)
			{
				isOnNew = false;
				((MyButton)(ev.getSource())).setText("New");
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)		
		{
			outswitch:
			switch (x)
			{
				case 0: //right click text area
				if (ev.isPopupTrigger())
				{
					popup.show(TEXTAREA, ev.getX(), ev.getY());
				}
				break;
				
				case -1:
				case 1: //new file
				if (isOnNew)
				{
					currentFile.setText(" ");
					file = null;
					TEXTAREA.setText("");
					isOnNew = false;
				}
				else
				{
					if (ev.getSource() instanceof MyButton)
					{
						((MyButton)(ev.getSource())).setText("Really?");
						isOnNew = true;
					}
					else
					{
						currentFile.setText(" ");
						file = null;
						TEXTAREA.setText("");
						isOnNew = false;
					}
				}
				break;
				
				case 2: //open file
				TMP1 = getConfig("ChooserStyle");
				if (TMP1 == null)
				{
					TMP1 = "Java";
				}
				if (TMP1.equals("Java"))
				{
					chooser = JavaChooser;
				}
				else if (TMP1.equals("Windows"))
				{
					chooser = WindowsChooser;
				}
				switch (TMP1)
				{
					case "Java":					
					case "Windows":
					i = chooser.showOpenDialog(w);
					if (i == JFileChooser.APPROVE_OPTION)
					{
						try
						{
							openToTextArea(chooser.getSelectedFile());
						}
						catch (IOException ex)
						{
							cannotOpen(ex);
						}
					}
					break;
										
					case "Beta":
					try
					{
						File[] f_a = FileChooser.showFileChooser(new File(getSettingsFilePath()));
						if (f_a == null) break outswitch;
						openToTextArea(f_a[0]);
					}
					catch (Exception ex)
					{
						cannotOpen(ex);
					}
					break;
				}
				break;
				
				case 3: //open quick
				TMP1 = JOptionPane.showInputDialog(w, "Please enter the path:", "Input", JOptionPane.QUESTION_MESSAGE);
				if ((TMP1 != null)&&(!TMP1.isEmpty()))
				{
					try
					{
						openToTextArea(new File(TMP1));
					}
					catch (Exception ex)
					{
						cannotOpen(ex);
					}
				}
				break;
				 
				case 4: //save as
				File f1 = null;
				boolean save1 = false;
				try
				{
					TMP1 = getConfig("ChooserStyle");
					if (TMP1 == null) throw new Exception();
				}
				catch (Throwable ex)
				{
					TMP1 = "Java";
				}
				if (TMP1.equals("Beta"))
				{
					outdo1:
					do
					{
						File[] f_a = FileChooser.showFileChooser(new File(getSettingsFilePath()));
						if (f_a == null) break outswitch;
						f1 = f_a[0];
						if (f1 != null)
						{
							if (f1.exists())
							{
								save1 = (isOverride() == JOptionPane.YES_OPTION);
							}
							else break outdo1;
						}
						else break outswitch;
					} while (!save1);
				}
				else
				{
					if (TMP1.equals("Java"))
					{
						chooser = JavaChooser;
					}
					else
					{
						chooser = WindowsChooser;
					}
					outdo2:
					do
					{
						i = chooser.showSaveDialog(w);
						if (i == JFileChooser.APPROVE_OPTION)
						{
							f1 = chooser.getSelectedFile();
							if (f1.exists())
							{
								save1 = (isOverride() == JOptionPane.YES_OPTION);
							}
							else break outdo2;
						}
						else break outswitch;
					} while (!save1);
				}
				TMP1 = f1.getPath();
				if (!TMP1.contains("."))
				{
					f1 = new File(TMP1 + ".txt");
				}
				try
				{
					save(f1);
					isSaved = true;
				}
				catch (IOException ex)
				{
				}
				break;
				
				case 5: //save
				File f2 = null;
				boolean save2 = false;				
				if (file == null)
				{
					try
					{
						TMP1 = getConfig("ChooserStyle");
						if (TMP1 == null) throw new Exception();
					}
					catch (Throwable ex)
					{
						TMP1 = "Java";
					}
					
					if (TMP1.equals("Beta"))
					{
						outdo3:
						do
						{
							File[] f_a = FileChooser.showFileChooser(new File(getSettingsFilePath()));
							if (f_a == null) break outswitch;
							f2 = f_a[0];
							if (f2 != null)
							{
								if (f2.exists())
								{
									save1 = (isOverride() == JOptionPane.YES_OPTION);
								}
								else break outdo3;
							}
							else break outswitch;
						} while (!save2);
					}
					else
					{
						if (TMP1.equals("Java"))
						{
							chooser = JavaChooser;
						}
						else
						{
							chooser = WindowsChooser;
						}				
						outdo4:
						do
						{
							i = chooser.showSaveDialog(w);
							if (i == JFileChooser.APPROVE_OPTION)
							{
								f2 = chooser.getSelectedFile();
								if (f2.exists())
								{
									save2 = (isOverride() == JOptionPane.YES_OPTION);
								}
								else break outdo4;
							}
							else break outswitch;
						} while (!save2);
					}
				}
				else
				{
					f2 = file;
				}
				try
				{
					save(f2);
					isSaved = true;
				}
				catch (IOException ex)
				{
				}
				break;
				
				case 6: //close
				w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
				break;
				
				case 7: //undo
				if (TEXTAREA.isEditable())
				{
					if (undoManager.getPosition() == 0)
					{
						undoManager.setInitial(TEXTAREA.getText());
					}
					TMP1 = undoManager.undo();
					if (TMP1 != null)
					{
						TEXTAREA.setText(TMP1);
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 8: //redo
				if (TEXTAREA.isEditable())
				{
					TMP1 = undoManager.redo();
					if (TMP1 != null)
					{
						TEXTAREA.setText(TMP1);
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 9: //select all
				TEXTAREA.selectAll();
				break;
				
				case 10: //select all and copy
				TEXTAREA.selectAll();
				clipbrd.setContents(new StringSelection(TEXTAREA.getText()), null);
				break;
				
				case 11: //cut
				if (TEXTAREA.isEditable())
				{
					undoManager.backup(TEXTAREA.getText());
					clipbrd.setContents(new StringSelection(TEXTAREA.getSelectedText()), null);
					TEXTAREA.replaceSelection(null);
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 12: //copy
				if (TEXTAREA.isEditable())
				{
					clipbrd.setContents(new StringSelection(TEXTAREA.getSelectedText()), null);
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 13: //paste
				if (TEXTAREA.isEditable())
				{
					try
					{
						TMP1 = clipbrd.getData(DataFlavor.stringFlavor).toString();
					}
					catch (Exception ex)
					{
						break;
					}
					undoManager.backup(TEXTAREA.getText());
					TEXTAREA.insert(TMP1, TEXTAREA.getCaretPosition());
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 14: //paste on next line
				if (TEXTAREA.isEditable())
				{
					try
					{
						TMP1 = clipbrd.getData(DataFlavor.stringFlavor).toString();
					}
					catch (Exception ex)
					{
						break;
					}
					undoManager.backup(TEXTAREA.getText());
					TEXTAREA.insert("\n" + TMP1, TEXTAREA.getCaretPosition());
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 15: //delete
				TEXTAREA.replaceSelection(null);
				break;
				
				case 16: //about RefluxEdit
				JOptionPane.showMessageDialog(w, "RefluxEdit " + VERSION_NO + " -- a lightweight plain text editor written in Java.\nBy tony200910041, http://tony200910041.wordpress.com\nDistributed under MPL 2.0.\nuser.home: " + System.getProperty("user.home") + "\nYour operating system is " + System.getProperty("os.name") + " (" + System.getProperty("os.version") + "), " + System.getProperty("os.arch") + "\n\nIcon sources: http://www.iconarchive.com and LibreOffice.", "About RefluxEdit " + VERSION_NO, JOptionPane.INFORMATION_MESSAGE);
				break;
				
				case 17: //editing
				if (TEXTAREA.isEditable())
				{
					TEXTAREA.setEditable(false);
					TEXTAREA.setBackground(new Color(245,245,245));
					writeConfig("isEditable", "false");
				}
				else
				{
					TEXTAREA.setEditable(true);
					TEXTAREA.setBackground(Color.WHITE);
					writeConfig("isEditable", "true");
				}
				break;
				
				case 18: //wrap option
				JDialog wrap = new JDialog();
				wrap.setModal(true);
				wrap.setTitle("Wrap option");
				wrap.setSize(300,80);
				wrap.setLocationRelativeTo(w);
				wrap.getContentPane().setBackground(Color.WHITE);
				MyRadioButton lineWrap = new MyRadioButton("Line Wrap", TEXTAREA.getLineWrap(), 0);
				MyRadioButton wrapStyleWord = new MyRadioButton("Wrap by word", TEXTAREA.getWrapStyleWord(), 0);
				wrap.setLayout(new FlowLayout());
				wrap.add(lineWrap);
				wrap.add(wrapStyleWord);
				wrap.setVisible(true);				
				boolean isWrap = lineWrap.isSelected();
				boolean isWrapStyleWord = wrapStyleWord.isSelected();
				writeConfig("LineWrap", isWrap + "");
				writeConfig("WrapStyleWord", isWrapStyleWord + "");
				TEXTAREA.setLineWrap(isWrap);
				TEXTAREA.setWrapStyleWord(isWrapStyleWord);
				wrap.dispose();
				break;
				
				case 19: //encoding
				JDialog encoding = new JDialog();
				encoding.setModal(true);
				encoding.setTitle("Encoding option");
				encoding.setSize(300,150);
				encoding.setLocationRelativeTo(w);
				encoding.getContentPane().setBackground(Color.WHITE);
				boolean _default = false;
				boolean ISO88591 = false;
				boolean UTF8 = false;
				boolean UTF16 = false;
				try
				{
					TMP1 = getConfig("Encoding");
					if (TMP1 == null) throw new Exception();
				}
				catch (Exception ex)
				{
					TMP1 = "default";
				}
				finally
				{
					switch (TMP1)
					{
						case "default":
						_default = true;
						break;
						
						case "ISO-8859-1":
						ISO88591 = true;
						break;
						
						case "UTF-8":
						UTF8 = true;
						break;
						
						case "UTF-16":
						UTF16 = true;
						break;
					}
				}
				final MyRadioButton isDefault = new MyRadioButton("Use default Java encoding", _default, 1);
				final MyRadioButton isISO88591 = new MyRadioButton("Use ISO-8859-1 (beta)", ISO88591, 2);
				final MyRadioButton isUTF8 = new MyRadioButton("Use UTF-8 (beta)", UTF8, 3);
				final MyRadioButton isUTF16 = new MyRadioButton("Use UTF-16BE (beta)", UTF16, 4);
				
				isDefault.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ev)
					{
						switch (((MyRadioButton)(ev.getSource())).getIndex())
						{
							case 1:
							isDefault.setSelected(true);
							isISO88591.setSelected(false);
							isUTF8.setSelected(false);
							isUTF16.setSelected(false);
							TMP1 = "default";
							break;
							
							case 2:
							isDefault.setSelected(false);
							isISO88591.setSelected(true);
							isUTF8.setSelected(false);
							isUTF16.setSelected(false);
							TMP1 = "ISO-8859-1";
							break;
							
							case 3:
							isDefault.setSelected(false);
							isISO88591.setSelected(false);
							isUTF8.setSelected(true);
							isUTF16.setSelected(false);
							TMP1 = "UTF-8";
							break;
							
							case 4:
							isDefault.setSelected(false);
							isISO88591.setSelected(false);
							isUTF8.setSelected(false);
							isUTF16.setSelected(true);
							TMP1 = "UTF-16";
							break;
						}
					}
				});
				ActionListener listener1 = isDefault.getActionListeners()[0];
				isISO88591.addActionListener(listener1);
				isUTF8.addActionListener(listener1);
				isUTF16.addActionListener(listener1);		
				encoding.setLayout(new GridLayout(4,1,0,0));
				encoding.add(isDefault);
				encoding.add(isISO88591);
				encoding.add(isUTF8);
				encoding.add(isUTF16);				
				encoding.setVisible(true);				
				writeConfig("Encoding", TMP1);
				break;
				
				case 20: //file chooser
				JDialog chooserOption = new JDialog();
				chooserOption.setModal(true);
				chooserOption.setTitle("Encoding option");
				chooserOption.setSize(300,120);
				chooserOption.setLocationRelativeTo(w);
				chooserOption.getContentPane().setBackground(Color.WHITE);
				boolean Java = false;
				boolean Windows = false;
				boolean Beta = false;
				try
				{
					TMP1 = getConfig("ChooserStyle");
					if (TMP1 == null) throw new Exception();
				}
				catch (Exception ex)
				{
					TMP1 = "Java";
				}
				finally
				{
					switch (TMP1)
					{
						case "Java":
						Java = true;
						break;
						
						case "Windows":
						Windows = true;
						break;
						
						case "Beta":
						Beta = true;
						break;
					}					
				}
				final MyRadioButton isJava = new MyRadioButton("Use Java JFileChooser", Java, 1);
				final MyRadioButton isWindows = new MyRadioButton("Use Windows LAF chooser", Windows, 2);
				final MyRadioButton isBeta = new MyRadioButton("Use beta chooser", Beta, 3);
				isJava.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ev)
					{
						switch (((MyRadioButton)(ev.getSource())).getIndex())
						{
							case 1:
							isJava.setSelected(true);
							isWindows.setSelected(false);
							isBeta.setSelected(false);
							TMP1 = "Java";
							break;
							
							case 2:
							isJava.setSelected(false);
							isWindows.setSelected(true);
							isBeta.setSelected(false);
							TMP1 = "Windows";
							break;
							
							case 3:
							isJava.setSelected(false);
							isWindows.setSelected(false);
							isBeta.setSelected(true);
							TMP1 = "Beta";
							break;
						}
					}
				});
				ActionListener listener2 = isJava.getActionListeners()[0];
				isJava.addActionListener(listener2);
				isWindows.addActionListener(listener2);
				isBeta.addActionListener(listener2);		
				chooserOption.setLayout(new GridLayout(3,1,0,0));
				chooserOption.add(isJava);
				chooserOption.add(isWindows);
				chooserOption.add(isBeta);				
				chooserOption.setVisible(true);				
				writeConfig("ChooserStyle", TMP1);
				break;
				
				case 21: //always on top
				if (w.isAlwaysOnTop())
				{
					writeConfig("OnTop", "false");
					w.setAlwaysOnTop(false);
				}
				else
				{
					writeConfig("OnTop", "true");
					w.setAlwaysOnTop(true);
				}
				break;
				
				case 22: //word count
				if ((TMP1 = TEXTAREA.getSelectedText()) == null)
				{
					TMP1 = TEXTAREA.getText();
				}
				if ((i = TMP1.replace("\n", "").length()) == 0)
				{
					JOptionPane.showMessageDialog(w, "Number of words (separated by space): 0\nNumber of characters: 0\nNumber of rows: " + TMP1.split("\n").length, "Word count", JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					JOptionPane.showMessageDialog(w, "Number of words (separated by space): " + TMP1.split("\\s+").length + "\nNumber of characters: " + i + "\nNumber of rows: " + TMP1.split("\n").length, "Word count", JOptionPane.INFORMATION_MESSAGE);
				}
				break;
				
				case 23: //search
				TMP1 = null;
				i = 0;
				j = TEXTAREA.getText().length();
				k = 0;
				TMP1 = JOptionPane.showInputDialog(w, "Please enter the word you want to search:", "Search", JOptionPane.QUESTION_MESSAGE);
				if ((TMP1 != null)&&(!TMP1.isEmpty())&&(TMP1.length() <= j))
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							UIManager.put("OptionPane.yesButtonText", "Continue");
							UIManager.put("OptionPane.noButtonText", "Cancel");
							RandomProgress searchProg = new RandomProgress(0, TEXTAREA.getText().length());
							TMP2 = TEXTAREA.getText();
							outFor:			
							for (i=0; i<=j-TMP1.length(); i++)
							{
								if (TMP2.substring(i, i+TMP1.length()).equals(TMP1))
								{
									TEXTAREA.setSelectionStart(i);
									TEXTAREA.setSelectionEnd(i+TMP1.length());
									k++;
									searchProg.setVisible(false);
									l = JOptionPane.showConfirmDialog(w, "Found: " + k + " result(s)", "Results", JOptionPane.YES_NO_OPTION);
									searchProg.setVisible(true);
									if (l == JOptionPane.NO_OPTION)
									{
										break outFor;
									}
								}
								if (i%50 == 0)
								{
									searchProg.setValue(i);
								}
							}
							UIManager.put("OptionPane.yesButtonText", "YES");
							UIManager.put("OptionPane.noButtonText", "NO");
							searchProg.dispose();
							JOptionPane.showMessageDialog(w, "Done. " + k + " result(s) found.", "Results", JOptionPane.INFORMATION_MESSAGE);
						}
					}).start();
				}
				break;
				
				case 24: //replace
				case 25: //replace, selection
				if (TEXTAREA.isEditable())
				{
					final JDialog replace = new JDialog();
					replace.setTitle("Replace");
					replace.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					replace.getContentPane().setBackground(Color.WHITE);
					replace.setLayout(new GridLayout(3,1,0,0));
					final JTextField wd1 = new JTextField(10);
					wd1.setFont(f13);
					final JTextField wd2 = new JTextField(10);
					wd2.setFont(f13);
					JPanel original = new JPanel();
					original.setBackground(Color.WHITE);
					original.add(new MyLabel("Original: "));
					original.add(wd1);
					replace.add(original);
					JPanel replaced = new JPanel();
					replaced.setBackground(Color.WHITE);
					replaced.add(new MyLabel("Replaced by: "));
					replaced.add(wd2);
					replace.add(replaced);
					JPanel panel_button = new JPanel();
					panel_button.setBackground(Color.WHITE);
					JButton button = new JButton("Start");
					panel_button.add(button);
					replace.add(panel_button);
					button.setFont(f13);
					button.setPreferredSize(new Dimension(60,30));
					button.setBorder(new LineBorder(Color.BLACK, 1));
					button.setFocusable(false);
					button.addMouseListener(new MouseAdapter()
					{
						@Override
						public void mouseReleased(MouseEvent ev)
						{
							TMP1 = wd1.getText();
							TMP2 = wd2.getText();
							if (x == 24)
							{
								TMP3 = TEXTAREA.getText();
							}
							else if (x == 25)
							{
								TMP3 = TEXTAREA.getSelectedText();
							}
							TMP4 = TEXTAREA.getText();						
							replace.setVisible(false);
							replace.dispose();
							j = TMP1.length();
							k = 0;
							for (i=0; i<=TMP3.length()-j; i++)
							{
								if (TMP3.substring(i, i+j).equals(TMP1))
								{
									TMP3 = TMP3.substring(0, i) + TMP2 + TMP3.substring(i+j, TMP3.length());
									k++;
									i+=TMP2.length()-1;
								}
							}
							if (k != 0)
							{
								undoManager.backup(TMP4);
							}
							if (x == 24)
							{
								TEXTAREA.setText(TMP3);
							}
							else if (x == 25)
							{
								TEXTAREA.replaceSelection(TMP3);
							}
							JOptionPane.showMessageDialog(w, k + " time(s) replaced.", "Replace", JOptionPane.INFORMATION_MESSAGE);
						}
					});
					replace.pack();
					replace.setLocationRelativeTo(w);
					replace.setVisible(true);
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 26: //upper case
				if (TEXTAREA.isEditable())
				{
					undoManager.backup(TEXTAREA.getText());
					TMP1 = TEXTAREA.getSelectedText();
					if (TMP1 != null)
					{
						TEXTAREA.replaceSelection(TMP1.toUpperCase());
					}
					else
					{
						TEXTAREA.setText(TEXTAREA.getText().toUpperCase());
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 27: //lower case
				if (TEXTAREA.isEditable())
				{
					undoManager.backup(TEXTAREA.getText());
					TMP1 = TEXTAREA.getSelectedText();
					if (TMP1 != null)
					{
						TEXTAREA.replaceSelection(TMP1.toLowerCase());
					}
					else
					{
						TEXTAREA.setText(TEXTAREA.getText().toLowerCase());
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 28: //invert case
				if (TEXTAREA.isEditable())
				{
					undoManager.backup(TEXTAREA.getText());
					TMP1 = TEXTAREA.getSelectedText();
					if (TMP1 != null)
					{
						TEXTAREA.replaceSelection(toInvertCase(TMP1));
					}
					else
					{
						TEXTAREA.setText(toInvertCase(TEXTAREA.getText()));
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 29: //tab size
				JDialog tabSize = new JDialog();
				tabSize.setTitle("Tab size");
				tabSize.setModal(true);
				tabSize.getContentPane().setBackground(Color.WHITE);
				JSpinner spinnerTabSize = new JSpinner();
				spinnerTabSize.setModel(new SpinnerNumberModel(TEXTAREA.getTabSize(), 1, 50, 1));
				spinnerTabSize.setFont(f13);
				tabSize.setLayout(new FlowLayout());
				tabSize.add(new MyLabel("Tab size: "));
				tabSize.add(spinnerTabSize);
				tabSize.pack();
				tabSize.setLocationRelativeTo(w);
				tabSize.setVisible(true);
				try
				{
					i = Byte.parseByte(spinnerTabSize.getValue().toString());
				}
				catch (Exception ex)
				{
					i = 4;
				}
				finally
				{
					TEXTAREA.setTabSize(i);
					writeConfig("TabSize", i+"");
				}
				break;
				
				case 30: //10 equal signs
				if (TEXTAREA.isEditable())
				{
					undoManager.backup(TEXTAREA.getText());
					TEXTAREA.insert("\n==========\n", TEXTAREA.getCaretPosition());
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 31: //four spaces
				if (TEXTAREA.isEditable())
				{
					undoManager.backup(TEXTAREA.getText());
					TEXTAREA.insert("    ", TEXTAREA.getCaretPosition());
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 32: //random word
				if (TEXTAREA.isEditable())
				{
					TMP1 = null;
					TMP1 = JOptionPane.showInputDialog(w, "Please enter the number of words you want to generate:", "Input", JOptionPane.QUESTION_MESSAGE);
					if (TMP1 != null)
					{
						try
						{
							m = Integer.parseInt(TMP1);
						}
						catch (NumberFormatException ex)
						{
							JOptionPane.showMessageDialog(w, "Please enter a positive integer!", "Error", JOptionPane.ERROR_MESSAGE);
							break outswitch;
						}
						if (m >= 100000)
						{
							j = JOptionPane.showConfirmDialog(w, "Generating 100000 words or more may take very long time.\nContinue?", "Confirm", JOptionPane.YES_NO_OPTION);
							if (j != JOptionPane.YES_OPTION) break outswitch;
						}
						undoManager.backup(TEXTAREA.getText());
						final RandomProgress prog = new RandomProgress(1, m);
						//now start to generate
						final Thread thr = new Thread()
						{
							@Override
							public void run()
							{
								try
								{
									for (j=1; j<=m; j++)
									{
										k = (int)(Math.random()*9+1);
										TMP1 = "";
										for (l=1; l<=k; l++)
										{
											TMP1 = TMP1 + toLetter((int)(Math.random()*26+1));
										}
										TEXTAREA.insert(TMP1 + " ", TEXTAREA.getCaretPosition());
										
										if (j%50 == 0)
										{
											prog.setValue(j);
										}
									}
									prog.dispose();
									JOptionPane.showMessageDialog(w, "Done: " + (j-1) + " word(s) generated.\nTime taken: " + prog.timeUsed() + " second(s)", "Done", JOptionPane.INFORMATION_MESSAGE);
								}
								catch (Throwable ex)
								{
								}
							}
						};
						thr.start();
						prog.addWindowListener(new WindowAdapter()
						{
							@Override
							public void windowClosing(WindowEvent ev)
							{
								thr.interrupt();
								double time = prog.timeUsed();
								prog.dispose();
								JOptionPane.showMessageDialog(w, "Aborted: " + j + " word(s) generated.\nTime taken: " + time + " second(s)", "Aborted", JOptionPane.INFORMATION_MESSAGE);
							}
						});
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 33: //keyword, Java
				if (TEXTAREA.isEditable())
				{
					Object[] keywordJava = new Object[]{"public static void main(String[] args) {", "import java.awt.*;\nimport java.awt.event.*;\nimport javax.swing.*;", "class MyListener extends MouseAdapter {", "throw new Exception();", "Integer.parseInt(", "Double.parseDouble(", "JOptionPane.showMessageDialog(", "JOptionPane.showInputDialog(", "public void mouseReleased(MouseEvent ev) {", "public void actionPeformed(ActionEvent ev) {", "public void windowClosing(WindowEvent ev) {", "System.out.println();"};
					TMP1 = (String)JOptionPane.showInputDialog(w, "Please choose one:", "Keyword (Java)", JOptionPane.QUESTION_MESSAGE, null, keywordJava, keywordJava[0]);
					if (TMP1 != null)
					{
						undoManager.backup(TEXTAREA.getText());
						TEXTAREA.insert(TMP1, TEXTAREA.getCaretPosition());
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 34: //keyword, html
				if (TEXTAREA.isEditable())
				{
					Object[] keywordHTML = new Object[]{"<a target=\"_blank\" href=\"\">", "<img alt=\"\" src=\"\">", "<br>"};
					TMP1 = (String)JOptionPane.showInputDialog(w, "Please choose one:", "Keyword (html)", JOptionPane.QUESTION_MESSAGE, null, keywordHTML, keywordHTML[0]);
					if (TMP1 != null)
					{
						undoManager.backup(TEXTAREA.getText());
						TEXTAREA.insert(TMP1, TEXTAREA.getCaretPosition());
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 35: //delete blank lines
				if (TEXTAREA.isEditable())
				{
					TMP1 = TEXTAREA.getText();
					undoManager.backup(TMP1);
					i = TEXTAREA.getLineCount();					
					for (j=i; j>=2; j--)
					{
						TMP2 = "\n";
						for (k=1; k<j; k++)
						{
							TMP2 = TMP2 + "\n";
						}
						TMP1 = TMP1.replace(TMP2, "\n");
					}
					TEXTAREA.setText(TMP1);
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 36: //selection color
				JDialog selectionColorDialog = new JDialog();
				selectionColorDialog.setTitle("Selection Color");
				selectionColorDialog.setLayout(new FlowLayout());
				selectionColorDialog.setModal(true);
				MyColorChooser colorChooser = new MyColorChooser();
				colorChooser.setColor(TEXTAREA.getSelectionColor());
				selectionColorDialog.add(colorChooser);
				selectionColorDialog.add(new MyLabel("Default: (244, 223, 255)"));
				selectionColorDialog.setSize(370,210);
				selectionColorDialog.setLocationRelativeTo(w);
				selectionColorDialog.getContentPane().setBackground(Color.WHITE);
				selectionColorDialog.setVisible(true);
				Color chosen = colorChooser.getColor();
				TEXTAREA.setSelectionColor(chosen);
				writeConfig("SelectionColor.r", chosen.getRed()+"");
				writeConfig("SelectionColor.g", chosen.getGreen()+"");
				writeConfig("SelectionColor.b", chosen.getBlue()+"");
				selectionColorDialog.dispose();
				break;
			}
		}
	}
	
	class RandomProgress extends JDialog
	{
		private JProgressBar prog;
		private double initialTime;
		RandomProgress(int min, int max)
		{
			this.setTitle("Progress");
			this.setLayout(new FlowLayout());
			this.prog = new JProgressBar(min, max);
			this.prog.setFont(f13);
			this.prog.setString("Please wait...");
			this.prog.setStringPainted(true);
			this.add(prog);
			this.pack();
			this.setLocationRelativeTo(null);
			this.setAlwaysOnTop(true);
			this.setVisible(true);
			this.initialTime = System.currentTimeMillis();
		}
		
		public void setValue(int x)
		{
			this.prog.setValue(x);
		}
		
		public double timeUsed()
		{
			return (System.currentTimeMillis() - this.initialTime)/1000.0;
		}
	}
	
	public void save(File f) throws IOException
	{
		TMP1 = getConfig("Encoding");
		if (TMP1.equals("default"))
		{
			BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
			bw1.write(TEXTAREA.getText());
			bw1.close();
		}
		else
		{
			byte[] bytes = TEXTAREA.getText().getBytes(TMP1);
			FileOutputStream output = new FileOutputStream(f);
			output.write(bytes);
			output.close();
		}
		file = f;
		TMP1 = file.getPath();
		if (TMP1.length() > 50)
		{
			currentFile.setText("Current file: " + TMP1.substring(0,25) + "..." + TMP1.substring(TMP1.length()-25, TMP1.length()));
		}
		else
		{
			currentFile.setText("Current file: " + TMP1);
		}
	}
	
	public void openToTextArea(File f) throws IOException
	{
		TEXTAREA.setText("");
		BufferedReader br1 = new BufferedReader(new FileReader(f));
		while ((TMP1 = br1.readLine()) != null)
		{
			TEXTAREA.append(TMP1 + "\n");
		}
		br1.close();
		TMP1 = f.getPath();
		file = f;
		if (TMP1.length() > 50)
		{
			currentFile.setText("Current file: " + TMP1.substring(0,25) + "..." + TMP1.substring(TMP1.length()-25, TMP1.length()));
		}
		else
		{
			currentFile.setText("Current file: " + TMP1);
		}
		TEXTAREA.setCaretPosition(0);
	}
	
	public static String getSettingsFilePath()
	{
		try
		{			
			return (new File(RefluxEdit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getPath();
		}
		catch (Exception ex)
		{
			return null;
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
		return prop.getProperty(name);
	}
	
	public void writeConfig(String key, String value)
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
	
	class MyRadioButton extends JRadioButton
	{
		private int x;
		public MyRadioButton(String str, boolean isSelected, int x)
		{
			super(str, isSelected);
			this.setFont(f13);
			this.setBackground(Color.WHITE);
			this.setFocusable(false);
			this.x = x;
		}
		
		public int getIndex()
		{
			return this.x;
		}
	}
	
	class UndoManager
	{
		private String[] backup;
		private int position = 0;
		private int no;
		public UndoManager(int x)
		{
			backup = new String[x+1];
			this.no = x;
		}
		
		public void backup(String str)
		{
			for (i=no; i>1; i--)
			{
				backup[i] = backup[i-1];
			}
			backup[1] = str;
		}
		
		public void setInitial(String str)
		{
			backup[0] = str;
		}
		
		public String undo()
		{
			if (position < no)
			{
				if (backup[position+1] == null)
				{
					JOptionPane.showMessageDialog(w, "Reached undo limit!", "Error", JOptionPane.ERROR_MESSAGE);
					return null;
				}
				position++;
				return backup[position];
			}
			else return null;
		}
		
		public String redo()
		{
			if (position >= 1)
			{
				position--;
				return backup[position];
			}
			else if (position == 0)
			{
				JOptionPane.showMessageDialog(w, "Reached redo limit!", "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			else return null;
		}
		
		public int getPosition()
		{
			return this.position;
		}
	}
}
