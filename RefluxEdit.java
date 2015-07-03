import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.LayerUI;
import java.io.*;
import java.util.Properties;
import java.util.Arrays;
import MyJava.*;

public class RefluxEdit extends JFrame
{
	private static final float VERSION_NO = (float)2.0;
	private static final String BETA_NO = "";
	private static final Font f13 = new Font("Microsoft Jhenghei", Font.PLAIN, 13);
	private static final LineBorder bord1 = new LineBorder(Color.BLACK, 1);
	private static final LineBorder bord2 = new LineBorder(Color.GRAY, 1);
	private static final File SettingsFile = new File(getSettingsFilePath() + "\\REFLUXEDITPREF.PROPERTIES\\");
	private static final Properties prop = new Properties();
	
	private static final int WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private static final int HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	private static JTextArea TEXTAREA = new JTextArea();
	private JLabel currentFile = new JLabel(" ");
	private static JMenuBar menubar;
	
	private static JFileChooser WindowsChooser;
	private static MyFileChooser JavaChooser;
	private static JFileChooser chooser;
	private File file = null;
	
	private static final JPopupMenu popup = new JPopupMenu();
	private static final Clipboard clipbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	UndoManager undoManager = new UndoManager(20);
	static boolean isSaved = true;
	
	static RefluxEdit w;
	JComponent topPanel;
	JPanel bottomPanel = new JPanel();
	
	private static int i, j, k, l, m;
	private static String TMP1, TMP2, TMP3, TMP4;
	
	public static void main(final String[] args)
	{
		final double initialTime = System.currentTimeMillis();
		final SplashScreen splash = SplashScreen.getSplashScreen();
		splash.createGraphics();
		SwingUtilities.invokeLater(new Runnable()
		{
			SwingWorker<Void, Void> chooserWorker, menuWorker, popupWorker;
			@Override
			public void run()
			{
				RefluxEdit.setLAF();
				w = new RefluxEdit("RefluxEdit " + VERSION_NO);
				w.restoreFrame();
				w.buildWindowsChooser();
				w.restoreTextArea();
				if (args.length == 1)
				{
					try
					{
						w.openToTextAreaNoProgress(new File(args[0]));
					}
					catch (Exception ex)
					{
					}
				}
				chooserWorker = new SwingWorker<Void, Void>()
				{
					@Override
					public Void doInBackground()
					{
						w.restoreChoosers();
						return null;
					}
					
					@Override
					public void done()
					{
						showJFrame();
					}
				};
				chooserWorker.execute();				
				
				menuWorker = new SwingWorker<Void, Void>()
				{
					@Override
					public Void doInBackground()
					{
						w.restoreMenus();
						return null;
					}
					
					@Override
					public void done()
					{
						showJFrame();
					}
				};
				menuWorker.execute();
				
				popupWorker = new SwingWorker<Void, Void>()
				{
					@Override
					public Void doInBackground()
					{
						w.restorePopup();
						return null;
					}
					
					@Override
					public void done()
					{
						showJFrame();
					}
				};
				popupWorker.execute();
			}
			
			public void showJFrame()
			{
				if ((chooserWorker.isDone())&&(menuWorker.isDone())&&(popupWorker.isDone()))
				{
					splash.close();
					w.setVisible(true);
					w.writeConfig("LastStartupTimeTaken", System.currentTimeMillis()-initialTime + "ms");
				}
			}
		});
	}
	
	public RefluxEdit(String title)
	{
		super(title);
		this.setJMenuBar(menubar);
		
		final RefluxEdit frame = this;
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setMinimumSize(new Dimension(275,250));
		this.setLayout(new BorderLayout());
		this.initialize();
		
		currentFile.setFont(f13);
		bottomPanel.add(currentFile);
		this.add(bottomPanel, BorderLayout.PAGE_END);
		this.add(new JLabel("  "), BorderLayout.LINE_START);
		this.add(new JLabel("  "), BorderLayout.LINE_END);
		this.add(new JScrollPane(TEXTAREA), BorderLayout.CENTER);
		this.addTopPanel();
		
		UIManager.put("OptionPane.messageFont", f13);
		UIManager.put("OptionPane.buttonFont", f13);
		UIManager.put("OptionPane.okButtonText", "OK");
		UIManager.put("OptionPane.yesButtonText", "YES");
		UIManager.put("OptionPane.noButtonText", "NO");
		UIManager.put("Button.background", Color.WHITE);
		UIManager.put("MenuItem.acceleratorForeground", new Color(34,131,132));
		UIManager.put("MenuItem.selectionBackground", new Color(220,220,220));
		UIManager.put("PopupMenu.border", new LineBorder(Color.BLACK, 1));
		UIManager.put("Separator.foreground", Color.BLACK);
		UIManager.put("ComboBox.font", f13);
		UIManager.put("TextField.font", f13);
		UIManager.put("Label.font", f13);
		UIManager.put("TabbedPane.font", f13);
		UIManager.put("RadioButton.font", f13);
		UIManager.put("CheckBox.font", f13);
		UIManager.put("Button.font", f13);
		UIManager.put("TitledBorder.font", f13);
		UIManager.put("PopupMenu.background", Color.WHITE);		
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
	
	public static void setLAF()
	{
		try
		{
			switch (getConfig("LAF"))
			{
				case "Windows":
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				menubar = new JMenuBar();
				menubar.setBackground(Color.WHITE);				
				break;
							
				case "Nimbus":
				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
				menubar = new JMenuBar();				
				TEXTAREA.setSelectedTextColor(Color.BLACK);
				UIManager.put("nimbusInfoBlue", new Color(255,186,0));
				break;
				
				default:
				menubar = new JMenuBar();
				menubar.setBackground(new Color(242,254,255));
				break;
			}
		}
		catch (Throwable ex)
		{
			menubar = new JMenuBar();
			menubar.setBackground(new Color(242,254,255));
		}
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
				writeConfig("LAF", "Default");
				writeConfig("isPanel", "true");
				writeConfig("ToolBar.new", "true");
				writeConfig("ToolBar.open", "true");
				writeConfig("ToolBar.save", "true");
				writeConfig("ToolBar.print", "true");
				writeConfig("ToolBar.cut", "true");
				writeConfig("ToolBar.copy", "true");
				writeConfig("ToolBar.paste", "true");
				writeConfig("ToolBar.delete", "true");
				writeConfig("ToolBar.search", "true");
				writeConfig("ToolBar.replace", "true");
				writeConfig("TextAreaFont.fontName", "Microsoft Jhenghei");
				writeConfig("TextAreaFont.fontStyle", "0");
				writeConfig("TextAreaFont.fontSize", "13");
			}
			catch (Exception ex)
			{
			}
		}
	}
	
	public void addTopPanel()
	{
		try
		{
			TMP1 = getConfig("isPanel");
			if (TMP1 == null) throw new Exception(); 
		}
		catch (Exception ex)
		{
			TMP1 = "true";
		}
		finally
		{
			switch (TMP1)
			{
				case "no":
				break;
				
				case "true": //use panel
				topPanel = new JPanel();
				topPanel.add(new MyButton("New", -1));
				topPanel.add(new MyButton("Open", 2));
				topPanel.add(new MyButton("Save as", 4));
				topPanel.add(new MyButton("Save", 5));
				this.add(topPanel, BorderLayout.PAGE_START);
				break;
				
				case "false": //use toolbar
				topPanel = new JToolBar("ToolBar");
				boolean b1 = getBoolean("ToolBar.new");
				boolean b2 = b1;
				if (b1) topPanel.add(new MyToolBarButton("NEW32", 1));
				b1 = getBoolean("ToolBar.open");
				b2 = b2||b1;
				if (b1) topPanel.add(new MyToolBarButton("OPEN32", 2));
				b1 = getBoolean("ToolBar.save");
				b2 = b2||b1;
				if (b1) topPanel.add(new MyToolBarButton("SAVE32", 4));
				b1 = getBoolean("ToolBar.print");
				b2 = b2||b1;
				if (b1) topPanel.add(new MyToolBarButton("PRINT32", 38));
				if (b2) ((JToolBar)topPanel).addSeparator();
				
				b1 = getBoolean("ToolBar.cut");
				b2 = b1;
				if (b1) topPanel.add(new MyToolBarButton("CUT32", 11));
				b1 = getBoolean("ToolBar.copy");
				b2 = b1||b2;
				if (b1) topPanel.add(new MyToolBarButton("COPY32", 12));
				b1 = getBoolean("ToolBar.paste");
				b2 = b1||b2;
				if (b1) topPanel.add(new MyToolBarButton("PASTE32", 13));
				if (b2) ((JToolBar)topPanel).addSeparator();
				
				if (getBoolean("ToolBar.delete")) topPanel.add(new MyToolBarButton("DELETE32", 15));
				if (getBoolean("ToolBar.search")) topPanel.add(new MyToolBarButton("SEARCH32", 23));
				if (getBoolean("ToolBar.replace")) topPanel.add(new MyToolBarButton("REPLACE32", 24));
				topPanel.add(new MyToolBarButton("OPTIONS32", 0));				
				this.add(topPanel, BorderLayout.PAGE_START);
				break;
			}
		}
	}
	
	public static boolean getBoolean(String str)
	{
		try
		{
			return getConfig(str).equals("true");
		}
		catch (Exception ex)
		{
			return false;
		}
	}
	
	class MyToolBarButton extends JButton implements MouseListener
	{
		private int x;
		public MyToolBarButton(String icon, int x)
		{
			super();
			this.setFocusable(false);
			this.setPreferredSize(new Dimension(32,32));
			this.setBackground(new Color(224,223,227));
			if (x != 0)
			{
				this.addMouseListener(new MyListener(x));
			}
			else
			{
				this.addMouseListener(this);
			}
			try
			{
				this.setIcon(new ImageIcon(getClass().getResource("/MyJava/SRC/" + icon + ".PNG")));
			}
			catch (Exception ex)
			{
			}
			this.x = x;
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
			if (this.x == 0)
			{
				JDialog buttonSelect = new JDialog(w, "Button selection", true);				
				MyCheckBox _new = new MyCheckBox("New", getBoolean("ToolBar.new"));
				MyCheckBox open = new MyCheckBox("Open", getBoolean("ToolBar.open"));
				MyCheckBox save = new MyCheckBox("Save", getBoolean("ToolBar.save"));
				MyCheckBox print = new MyCheckBox("Print", getBoolean("ToolBar.print"));
				MyCheckBox cut = new MyCheckBox("Cut", getBoolean("ToolBar.cut"));
				MyCheckBox copy = new MyCheckBox("Copy", getBoolean("ToolBar.copy"));
				MyCheckBox paste = new MyCheckBox("Paste", getBoolean("ToolBar.paste"));
				MyCheckBox delete = new MyCheckBox("Delete", getBoolean("ToolBar.delete"));
				MyCheckBox search = new MyCheckBox("Search", getBoolean("ToolBar.search"));
				MyCheckBox replace = new MyCheckBox("Replace", getBoolean("ToolBar.replace"));
				buttonSelect.setLayout(new GridLayout(5,2,0,0));
				buttonSelect.add(_new);
				buttonSelect.add(open);
				buttonSelect.add(save);
				buttonSelect.add(print);
				buttonSelect.add(cut);
				buttonSelect.add(copy);
				buttonSelect.add(paste);
				buttonSelect.add(delete);
				buttonSelect.add(search);
				buttonSelect.add(replace);
				buttonSelect.setSize(155,170);
				buttonSelect.setLocationRelativeTo(w);
				buttonSelect.setVisible(true);
				boolean isNew = _new.isSelected();
				boolean isOpen = open.isSelected();
				boolean isSave = save.isSelected();
				boolean isPrint = print.isSelected();
				boolean isCut = cut.isSelected();
				boolean isCopy = copy.isSelected();
				boolean isPaste = paste.isSelected();
				boolean isDelete = delete.isSelected();
				boolean isSearch = search.isSelected();
				boolean isReplace = replace.isSelected();
				buttonSelect.dispose();
				writeConfig("ToolBar.new", isNew + "");
				writeConfig("ToolBar.open", isOpen + "");
				writeConfig("ToolBar.save", isSave + "");
				writeConfig("ToolBar.print", isPrint + "");
				writeConfig("ToolBar.cut", isCut + "");
				writeConfig("ToolBar.copy", isCopy + "");
				writeConfig("ToolBar.paste", isPaste + "");
				writeConfig("ToolBar.delete", isDelete + "");
				writeConfig("ToolBar.search", isSearch + "");
				writeConfig("ToolBar.replace", isReplace + "");
				
				topPanel.removeAll();
				boolean b1 = isNew;
				boolean b2 = isNew;
				if (b1) topPanel.add(new MyToolBarButton("NEW32", 1));
				b1 = isOpen;
				b2 = b2||b1;
				if (b1) topPanel.add(new MyToolBarButton("OPEN32", 2));
				b1 = isSave;
				b2 = b2||b1;
				if (b1) topPanel.add(new MyToolBarButton("SAVE32", 4));
				b1 = isPrint;
				b2 = b2||b1;
				if (b1) topPanel.add(new MyToolBarButton("PRINT32", 38));
				if (b2) ((JToolBar)topPanel).addSeparator();
				
				b1 = isCut;
				b2 = b1;
				if (b1) topPanel.add(new MyToolBarButton("CUT32", 11));
				b1 = isCopy;
				b2 = b1||b2;
				if (b1) topPanel.add(new MyToolBarButton("COPY32", 12));
				b1 = isPaste;
				b2 = b1||b2;
				if (b1) topPanel.add(new MyToolBarButton("PASTE32", 13));
				if (b2) ((JToolBar)topPanel).addSeparator();
				
				if (isDelete) topPanel.add(new MyToolBarButton("DELETE32", 15));
				if (isSearch) topPanel.add(new MyToolBarButton("SEARCH32", 23));
				if (isReplace) topPanel.add(new MyToolBarButton("REPLACE32", 24));
				topPanel.add(new MyToolBarButton("OPTIONS32", 0));
				topPanel.revalidate();
				topPanel.repaint();
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
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
				return (f.isDirectory())||(FILE.endsWith("txt"))||(FILE.endsWith("java"))||(FILE.endsWith("py"))||(FILE.endsWith("php"))||(FILE.endsWith("html"))||(FILE.endsWith("htm"))||(FILE.endsWith("xml"))||(FILE.endsWith("bot"))||(FILE.endsWith("properties"));
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
		MyMenu menu1 = new MyMenu("File");
		MyMenu menu2 = new MyMenu("Edit");
		MyMenu menu3 = new MyMenu("Tools");
		JMenu menu3_1 = new JMenu("Options");
		menu3_1.setFont(f13);
		JMenu menu3_2 = new JMenu("Case conversion");
		menu3_2.setFont(f13);
		MyMenu menu4 = new MyMenu("Insert");
		MyMenu menu5 = new MyMenu("Help");
		
		menu1.add(new MyMenuItem("New file", "NEW", 1));
		menu1.add(new MyMenuItem("Open file", "OPEN", 2).setAccelerator(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menu1.add(new MyMenuItem("Open file (quick)", null, 3));
		menu1.add(new JSeparator());
		menu1.add(new MyMenuItem("Save as", "SAVE", 4).setAccelerator(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu1.add(new MyMenuItem("Save", null, 5));
		menu1.add(new JSeparator());
		menu1.add(new MyMenuItem("Print", null, 38).setAccelerator(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
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
		
		menu3.add(new MyMenuItem("Enable/disable editing", null, 17));
		menu3.add(new MyMenuItem("Enable/disable always on top", null, 21));
		menu3.add(menu3_1);
		menu3.add(new JSeparator());		
		menu3.add(new MyMenuItem("Word count (beta)", null, 22).setAccelerator(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));
		menu3.add(new MyMenuItem("Delete blank lines", null, 35));
		menu3.add(menu3_2);
		menu3.add(new JSeparator());
		menu3.add(new MyMenuItem("Search", "SEARCH", 23).setAccelerator(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		menu3.add(new MyMenuItem("Replace", null, 24));
		menu3.add(new MyMenuItem("Replace in selection", null, 25));
		menu3.add(new JSeparator());
		menu3.add(new MyMenuItem("Show JColorChooser", null, 40));
		menu3.add(new MyMenuItem("Base conversion (2/8/10/16)", null, 41));
		
		menu3_1.add(new MyMenuItem("Line Wrap options", null, 18));
		menu3_1.add(new MyMenuItem("Encoding options", null, 19));
		menu3_1.add(new MyMenuItem("FileChooser options", null, 20));
		menu3_1.add(new MyMenuItem("Tab size options", null, 29));
		menu3_1.add(new MyMenuItem("Selection color options", null, 36));
		menu3_1.add(new MyMenuItem("Look and Feel options", "LAF", 37));
		menu3_1.add(new JSeparator());
		menu3_1.add(new MyMenuItem("Other options", null, 39));
		menu3_1.add(new MyMenuItem("Reduce memory usage", null, 42));
		
		menu3_2.add(new MyMenuItem("Convert to upper case", "UPPERCASE", 26));
		menu3_2.add(new MyMenuItem("Convert to lower case", "LOWERCASE", 27));
		menu3_2.add(new MyMenuItem("Convert to invert case", "INVERTCASE", 28));
		
		menu4.add(new MyMenuItem("Insert ten equal signs", null, 30));
		menu4.add(new MyMenuItem("Insert four spaces", null, 31));
		menu4.add(new MyMenuItem("Generate random words", null, 32));
		menu4.add(new JSeparator());
		menu4.add(new MyMenuItem("Insert key words (Java)", "KEYWORDJAVA", 33));
		menu4.add(new MyMenuItem("Insert key words (html)", "KEYWORDHTML", 34));		
		
		menu5.add(new MyMenuItem("About RefluxEdit", "APPICON16", 16).setAccelerator(KeyEvent.VK_F1, ActionEvent.CTRL_MASK));
		//next one: 43
	}
	
	public void restoreTextArea()
	{
		TEXTAREA.setDragEnabled(true);
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
				if (!ev.isControlDown())
				{
					undoManager.setPosition(0);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent ev)
			{
				if (ev.isControlDown())
				{
					i = ev.getKeyCode();
					MyMenuItem menuItem = null;
					if (TMP2 != null)
					{
						TMP1 = new String(TMP2);
					}
					else
					{
						TMP1 = null;
					}
					TMP2 = TEXTAREA.getText();
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
					else if (i == KeyEvent.VK_P)
					{
						menuItem = new MyMenuItem(null, null, 38);
					}
					else if (i == KeyEvent.VK_V)
					{
						undoManager.backup(TMP1);
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
		TEXTAREA.setDropTarget(new DropTarget()
		{
			@Override
            public synchronized void drop(DropTargetDropEvent dtde)
            {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				try
				{
					File file = (File)(((java.util.List)(dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor))).get(0));
					i = JOptionPane.showConfirmDialog(w, "Open file " + file.getPath() + "?\nNote that the current file will not be saved.", "Confirm Dialog", JOptionPane.YES_NO_OPTION);
					if (i == JOptionPane.YES_OPTION)
					{
						openToTextArea(file);
					}
				}
				catch (Throwable ex)
				{
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
		//font
		try
		{
			TMP1 = getConfig("TextAreaFont.fontName");
		}
		catch (Exception ex)
		{
			TMP1 = "Microsoft Jhenghei";
		}
		try
		{
			i = Integer.parseInt(getConfig("TextAreaFont.fontStyle"));
			if ((i>2)||(i<0)) throw new Exception();
		}
		catch (Exception ex)
		{
			i = 0;
		}
		try
		{
			j = Integer.parseInt(getConfig("TextAreaFont.fontSize"));
			if ((j>200)||(j<1)) throw new Exception();
		}
		catch (Exception ex)
		{
			j = 15;
		}
		TEXTAREA.setFont(new Font(TMP1, i, j));
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
			this.setBorder(bord1);
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
			this.setAccelerator(KeyStroke.getKeyStroke(keyEvent, actionEvent));
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
				default: case 26: return "z";
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
			if (ev.getSource() instanceof MyButton)
			{
				((MyButton)(ev.getSource())).setBorder(bord2);
			}
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
		{
			if (x == -1)
			{
				isOnNew = false;
				((MyButton)(ev.getSource())).setText("New");
			}
			if (ev.getSource() instanceof MyButton)
			{
				((MyButton)(ev.getSource())).setBorder(bord1);
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
					isSaved = true;
					((MyButton)(ev.getSource())).setText("New");
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
						i = JOptionPane.showConfirmDialog(w, "Create new file?\nThe current file will be DISCARDED!", "Confirm", JOptionPane.YES_NO_OPTION);
						if (i == JOptionPane.YES_OPTION)
						{
							currentFile.setText(" ");
							file = null;
							TEXTAREA.setText("");
							isOnNew = false;
						}
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
						openToTextArea(chooser.getSelectedFile());
					}
					break;
										
					case "Beta":
					File[] f_a = FileChooser.showFileChooser(new File(getSettingsFilePath()));
					if (f_a == null) break outswitch;
					openToTextArea(f_a[0]);
					break;
				}
				break;
				
				case 3: //open quick
				TMP1 = JOptionPane.showInputDialog(w, "Please enter the path:", "Input", JOptionPane.QUESTION_MESSAGE);
				if ((TMP1 != null)&&(!TMP1.isEmpty()))
				{
					openToTextArea(new File(TMP1));
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
				TMP1 = f2.getPath();
				if (!TMP1.contains("."))
				{
					f2 = new File(TMP1 + ".txt");
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
						i = TEXTAREA.getCaretPosition();
						TEXTAREA.setText(TMP1);
						TEXTAREA.setCaretPosition(i);
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
						i = TEXTAREA.getCaretPosition();
						TEXTAREA.setText(TMP1);
						TEXTAREA.setCaretPosition(i);
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
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 12: //copy
				clipbrd.setContents(new StringSelection(TEXTAREA.getSelectedText()), null);
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
					isSaved = false;
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
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 15: //delete
				if (TEXTAREA.isEditable())
				{
					undoManager.backup(TEXTAREA.getText());
					TEXTAREA.replaceSelection(null);
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 16: //about RefluxEdit
				JOptionPane.showMessageDialog(w, "RefluxEdit " + VERSION_NO + BETA_NO + " -- a lightweight plain text editor written in Java.\nBy tony200910041, http://tony200910041.wordpress.com\nDistributed under MPL 2.0.\nuser.home: " + System.getProperty("user.home") + "\nYour operating system is " + System.getProperty("os.name") + " (" + System.getProperty("os.version") + "), " + System.getProperty("os.arch") + "\n\nIcon sources: http://www.iconarchive.com and LibreOffice.", "About RefluxEdit " + VERSION_NO, JOptionPane.INFORMATION_MESSAGE);
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
				JDialog wrap = new JDialog(w, "Wrap option", true);
				wrap.setSize(300,80);
				wrap.setLocationRelativeTo(w);
				wrap.getContentPane().setBackground(Color.WHITE);
				MyCheckBox lineWrap = new MyCheckBox("Line Wrap", TEXTAREA.getLineWrap());
				MyCheckBox wrapStyleWord = new MyCheckBox("Wrap by word", TEXTAREA.getWrapStyleWord());
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
				JDialog encoding = new JDialog(w);
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
				
				ActionListener listener1 = new ActionListener()
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
				};
				isDefault.addActionListener(listener1);
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
				JDialog chooserOption = new JDialog(w, "Encoding option", true);
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
				ActionListener listener2 = new ActionListener()
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
				};
				isJava.addActionListener(listener2);
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
					final JDialog replace = new JDialog(w, "Replace", true);
					replace.getContentPane().setBackground(Color.WHITE);
					replace.setLayout(new GridLayout(3,1,0,0));
					final JTextField wd1 = new JTextField(10);
					wd1.setFont(f13);
					final JTextField wd2 = new JTextField(10);
					wd2.setFont(f13);
					MyPanel original = new MyPanel(MyPanel.LEFT);
					original.add(new MyLabel("Original: "));
					original.add(wd1);
					replace.add(original);
					MyPanel replaced = new MyPanel(MyPanel.LEFT);
					replaced.add(new MyLabel("Replaced by: "));
					replaced.add(wd2);
					replace.add(replaced);
					MyPanel panel_button = new MyPanel(MyPanel.CENTER);
					final JButton button = new JButton("Start");
					panel_button.add(button);
					replace.add(panel_button);
					button.setFont(f13);
					button.setPreferredSize(new Dimension(60,30));
					button.setBorder(new LineBorder(Color.BLACK, 1));
					button.setFocusable(false);
					button.addMouseListener(new MouseAdapter()
					{
						@Override
						public void mouseEntered(MouseEvent ev)
						{
							button.setBackground(Color.LIGHT_GRAY);
						}
						
						@Override
						public void mouseExited(MouseEvent ev)
						{
							button.setBackground(Color.WHITE);
						}
						
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
							if (TMP3 != null)
							{
								if (TMP3.isEmpty()) return;
							}
							else return;							
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
					isSaved = false;
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
					isSaved = false;
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
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 29: //tab size
				JDialog tabSize = new JDialog(w);
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
					isSaved = false;
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
					isSaved = false;
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
									isSaved = false;
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
						isSaved = false;
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
						isSaved = false;
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
				JDialog selectionColorDialog = new JDialog(w);
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
				
				case 37: //LAF
				JDialog LAFOption = new JDialog(w);
				LAFOption.setModal(true);
				LAFOption.setTitle("Look and Feel option");
				LAFOption.getContentPane().setBackground(Color.WHITE);
				boolean DefaultL = false;
				boolean WindowsL = false;
				boolean Nimbus = false;
				try
				{
					TMP1 = getConfig("LAF");
					if (TMP1 == null) throw new Exception();
				}
				catch (Exception ex)
				{
					TMP1 = "Default";
				}
				finally
				{
					switch (TMP1)
					{
						case "Default":
						DefaultL = true;
						break;
						
						case "Windows":
						WindowsL = true;
						break;
						
						case "Nimbus":
						Nimbus = true;
						break;
					}					
				}
				final MyRadioButton isDefaultL = new MyRadioButton("Use default Look and Feel", DefaultL, 1);
				final MyRadioButton isWindowsL = new MyRadioButton("Use Windows Look and Feel", WindowsL, 2);
				final MyRadioButton isNimbus = new MyRadioButton("Use Nimbus Look and Feel", Nimbus, 4);
				ActionListener listener3 = new ActionListener()
				{
					public void actionPerformed(ActionEvent ev)
					{
						switch (((MyRadioButton)(ev.getSource())).getIndex())
						{
							case 1:
							isDefaultL.setSelected(true);
							isWindowsL.setSelected(false);
							isNimbus.setSelected(false);
							TMP1 = "Default";
							break;
							
							case 2:
							isDefaultL.setSelected(false);
							isWindowsL.setSelected(true);
							isNimbus.setSelected(false);
							TMP1 = "Windows";
							break;
							
							//case 3:
							
							case 4:
							isDefaultL.setSelected(false);
							isWindowsL.setSelected(false);
							isNimbus.setSelected(true);
							TMP1 = "Nimbus";
							break;
						}
					}
				};
				isDefaultL.addActionListener(listener3);
				isWindowsL.addActionListener(listener3);
				isNimbus.addActionListener(listener3);	
				LAFOption.setLayout(new GridLayout(3,1,0,0));
				LAFOption.add(isDefaultL);
				LAFOption.add(isWindowsL);
				LAFOption.add(isNimbus);
				LAFOption.setSize(250,140);
				LAFOption.setLocationRelativeTo(w);
				LAFOption.setVisible(true);				
				writeConfig("LAF", TMP1);
				JOptionPane.showMessageDialog(w, "The Look and Feel will be changed after restart.", "Done", JOptionPane.INFORMATION_MESSAGE);
				break;
				
				case 38: //print
				try
				{
					boolean printed = TEXTAREA.print();
				}
				catch (PrinterException ex)
				{
				}
				break;
				
				case 39: //other options
				JDialog option = new JDialog(w, "Other options", true);
				option.setLayout(new GridLayout(2,1,0,0));
				option.getContentPane().setBackground(Color.WHITE);
				try
				{
					TMP1 = getConfig("isPanel");
					if (TMP1 == null) throw new Exception();
				}
				catch (Exception ex)
				{
					TMP1 = "true";
				}
				final MyRadioButton isPanel = new MyRadioButton("Use panel", false, 1);
				final MyRadioButton isToolBar = new MyRadioButton("Use toolbar", false, 2);
				final MyRadioButton NoContainer = new MyRadioButton("Hide panel/toolbar", false, 3);
				switch (TMP1)
				{
					case "true":
					isPanel.setSelected(true);
					break;
					
					case "false":
					isToolBar.setSelected(true);
					break;
					
					case "no": default:
					NoContainer.setSelected(true);
					break;
				}
				ActionListener optionlis1 = new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						switch (((MyRadioButton)(ev.getSource())).getIndex())
						{
							case 1:
							isPanel.setSelected(true);
							isToolBar.setSelected(false);
							NoContainer.setSelected(false);
							break;
							
							case 2:
							isPanel.setSelected(false);
							isToolBar.setSelected(true);
							NoContainer.setSelected(false);
							break;
							
							case 3:
							isPanel.setSelected(false);
							isToolBar.setSelected(false);
							NoContainer.setSelected(true);
							break;
						}
					}
				};
				isPanel.addActionListener(optionlis1);
				isToolBar.addActionListener(optionlis1);
				NoContainer.addActionListener(optionlis1);
				MyPanel P1 = new MyPanel(MyPanel.LEFT);
				P1.add(new MyLabel("Toolbar mode (require restart):"));
				P1.add(isPanel);
				P1.add(isToolBar);
				P1.add(NoContainer);
				option.add(P1);
				try
				{
					TMP1 = getConfig("TextAreaFont.fontName");
					if (TMP1 == null) throw new Exception();
				}
				catch (Exception ex)
				{
					TMP1 = "Microsoft Jhenghei";
				}
				try
				{
					i = Integer.parseInt(getConfig("TextAreaFont.fontStyle"));
					if ((i<0)||(i>2)) throw new Exception();
				}
				catch (Exception ex)
				{
					i = 0;
				}
				try
				{
					j = Integer.parseInt(getConfig("TextAreaFont.fontSize"));
					if ((j<1)||(j>200)) throw new Exception();
				}
				catch (Exception ex)
				{
					j = 13;
				}
				MyPanel P2 = new MyPanel(MyPanel.LEFT);
				P2.add(new MyLabel("Text area font:"));
				MyFontChooser fontChooser = new MyFontChooser(new Font(TMP1,i,j));
				P2.add(fontChooser);
				option.add(P2);
				option.setSize(620,120);
				option.setLocationRelativeTo(w);
				option.setVisible(true);
				if (isPanel.isSelected())
				{
					writeConfig("isPanel", "true");
				}
				else if (isToolBar.isSelected())
				{
					writeConfig("isPanel", "false");
				}
				else if (NoContainer.isSelected())
				{
					writeConfig("isPanel", "no");
				}
				Font selectedFont = fontChooser.getFont();
				writeConfig("TextAreaFont.fontName", selectedFont.getFontName());
				writeConfig("TextAreaFont.fontStyle", selectedFont.getStyle() + "");
				writeConfig("TextAreaFont.fontSize", selectedFont.getSize() + "");
				TEXTAREA.setFont(selectedFont);
				break;
				
				case 40:
				final JColorChooser cc = new JColorChooser(Color.WHITE);
				cc.setFont(f13);
				ActionListener colorDialogListener = new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						Color color = cc.getColor();
						final JDialog colorCode = new JDialog(w, "Insert", true);
						colorCode.setLayout(new GridLayout(4,1,0,0));
						colorCode.getContentPane().setBackground(Color.WHITE);
						TMP1 = getRGBString(color);
						TMP2 = getHSBString(color);
						TMP3 = getHEXString(color);
						
						MyPanel P1 = new MyPanel(MyPanel.LEFT);
						final MyCheckBox rgb = new MyCheckBox("RGB", true);
						MyLabel rgbLabel = new MyLabel(TMP1);
						P1.add(rgb);
						P1.add(rgbLabel);
						
						MyPanel P2 = new MyPanel(MyPanel.LEFT);
						final MyCheckBox hsb = new MyCheckBox("HSB", false);
						MyLabel hsbLabel = new MyLabel(TMP2);
						P2.add(hsb);
						P2.add(hsbLabel);
						
						MyPanel P3 = new MyPanel(MyPanel.LEFT);
						final MyCheckBox hex = new MyCheckBox("HEX", false);
						MyLabel hexLabel = new MyLabel(TMP3);
						P3.add(hex);
						P3.add(hexLabel);
						
						MyPanel P4 = new MyPanel(MyPanel.CENTER);
						final JButton colorCodeClose = new JButton("OK");
						colorCodeClose.setBackground(Color.WHITE);
						colorCodeClose.setBorder(bord1);
						colorCodeClose.setFont(f13);
						colorCodeClose.setPreferredSize(new Dimension(50,30));
						colorCodeClose.setFocusPainted(false);						
						colorCodeClose.addMouseListener(new MouseAdapter()
						{
							@Override
							public void mouseReleased(MouseEvent ev)
							{
								TMP4 = "";
								if (rgb.isSelected()) TMP4 = " " + TMP1;
								if (hsb.isSelected()) TMP4 = TMP4 + " " + TMP2;
								if (hex.isSelected()) TMP4 = TMP4 + " " + TMP3;
								TEXTAREA.insert(TMP4, TEXTAREA.getCaretPosition());
								colorCode.setVisible(false);
								colorCode.dispose();
							}
							
							@Override
							public void mouseEntered(MouseEvent ev)
							{
								colorCodeClose.setBackground(Color.LIGHT_GRAY);
							}
							
							@Override
							public void mouseExited(MouseEvent ev)
							{
								colorCodeClose.setBackground(Color.WHITE);
							}
						});
						P4.add(colorCodeClose);
						colorCode.add(P1);
						colorCode.add(P2);
						colorCode.add(P3);
						colorCode.add(P4);
						colorCode.pack();
						colorCode.setLocationRelativeTo(w);
						colorCode.setVisible(true);
					}
					
					public String getRGBString(Color c)
					{
						return "(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")";
					}
					
					public String getHSBString(Color c)
					{
						float[] hsbvals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
						return "(" + hsbvals[0] + ", " + hsbvals[1] + ", " + hsbvals[2] + ")";
					}
					
					public String getHEXString(Color c)
					{
						return "#" + Integer.toHexString(c.getRGB()).substring(2);
					}
				};
				final JDialog colorDialog = JColorChooser.createDialog(w, "Color chooser (JColorChooser)", true, cc, colorDialogListener, null);
				colorDialog.setVisible(true);
				break;
				
				case 41: //number conversion
				JDialog baseConvert = new JDialog(w, "Base conversion (2/8/10/16)", true);
				baseConvert.setLayout(new GridLayout(4,1,0,0));
				
				MyPanel base2 = new MyPanel(MyPanel.LEFT);
				MyCheckBox cb2 = new MyCheckBox("Base 2: ", false);
				base2.add(cb2);
				final MyTextField tf2 = new MyTextField(8,2);
				base2.add(tf2);
				baseConvert.add(base2);
				
				MyPanel base8 = new MyPanel(MyPanel.LEFT);
				MyCheckBox cb8 = new MyCheckBox("Base 8: ", false);
				base8.add(cb8);
				final MyTextField tf8 = new MyTextField(8,8);
				base8.add(tf8);
				baseConvert.add(base8);
				
				MyPanel base10 = new MyPanel(MyPanel.LEFT);
				MyCheckBox cb10 = new MyCheckBox("Base 10: ", false);
				base10.add(cb10);
				final MyTextField tf10 = new MyTextField(8,10);
				base10.add(tf10);
				baseConvert.add(base10);
				
				MyPanel base16 = new MyPanel(MyPanel.LEFT);
				MyCheckBox cb16 = new MyCheckBox("Base 16: ", false);
				base16.add(cb16);
				final MyTextField tf16 = new MyTextField(8,16);
				base16.add(tf16);
				baseConvert.add(base16);
				
				ActionListener baseTextFieldListener = new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						TMP1 = ((JTextField)(ev.getSource())).getText();
						long x;
						switch (((MyTextField)(ev.getSource())).getIndex())
						{
							case 2:
							try
							{
								x = Long.valueOf(TMP1, 2);
								tf8.setText(Long.toString(x,8));
								tf10.setText(x+"");
								tf16.setText(Long.toString(x,16));
							}
							catch (Exception ex)
							{
							}
							finally
							{
								break;
							}
							
							case 8:
							try
							{
								x = Long.valueOf(TMP1, 8);
								tf2.setText(Long.toString(x,2));
								tf10.setText(x+"");
								tf16.setText(Long.toString(x,16));
							}
							catch (Exception ex)
							{
							}
							finally
							{
								break;
							}
							
							case 10:
							try
							{
								x = Long.parseLong(tf10.getText());
								tf2.setText(Long.toString(x,2));
								tf8.setText(Long.toString(x,8));
								tf16.setText(Long.toString(x,16));
							}
							catch (Exception ex)
							{
							}
							finally
							{
								break;
							}
							
							case 16:
							try
							{
								x = Long.valueOf(TMP1, 16);
								tf2.setText(Long.toString(x,2));
								tf8.setText(Long.toString(x,8));
								tf10.setText(x+"");
							}
							catch (Exception ex)
							{
							}
							finally
							{
								break;
							}
						}
					}
				};
				tf2.addActionListener(baseTextFieldListener);
				tf8.addActionListener(baseTextFieldListener);
				tf10.addActionListener(baseTextFieldListener);
				tf16.addActionListener(baseTextFieldListener);
				baseConvert.pack();
				baseConvert.setLocationRelativeTo(null);
				baseConvert.setVisible(true);
				TMP1 = "";
				if (cb2.isSelected()) TMP1 = tf2.getText() + " ";
				if (cb8.isSelected()) TMP1 = TMP1 + tf8.getText() + " ";
				if (cb10.isSelected()) TMP1 = TMP1 + tf10.getText() + " ";
				if (cb16.isSelected()) TMP1 = TMP1 + tf16.getText() + " ";
				TEXTAREA.insert(TMP1, TEXTAREA.getCaretPosition());
				baseConvert.dispose();
				break;
				
				case 42: //reduce memory usage
				i = JOptionPane.showConfirmDialog(w, "Reduce memory usage?\nClick YES to run the garbage collector.\nSystem.gc() will be executed.", "Reduce memory usage", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.YES_OPTION)
				{
					TMP1 = null;
					TMP2 = null;
					TMP3 = null;
					TMP4 = null;
					System.gc();
				}
				break;
			}
		}
	}
	
	class RandomProgress extends JDialog
	{
		private JProgressBar prog;
		private double initialTime;
		public RandomProgress(int min, int max)
		{
			super(w);
			this.setTitle("Progress");
			this.setLayout(new FlowLayout());
			this.prog = new JProgressBar(min, max);
			this.prog.setFont(f13);
			this.prog.setString("Please wait...");
			this.prog.setStringPainted(true);
			this.add(prog);
			this.pack();
			this.setLocationRelativeTo(w);
			this.setResizable(false);
			this.setVisible(true);
			this.initialTime = System.currentTimeMillis();
		}
		
		public RandomProgress()
		{
			super(w);
			this.setTitle("Progress");
			this.setLayout(new FlowLayout());
			this.prog = new JProgressBar();
			this.prog.setIndeterminate(true);
			this.prog.setFont(f13);
			this.prog.setString("Please wait...");
			this.prog.setStringPainted(true);
			this.add(prog);
			this.pack();
			this.setLocationRelativeTo(w);
			this.setResizable(false);
			this.setVisible(true);
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
			FileWriter writer = new FileWriter(f);
			TEXTAREA.write(writer);
			writer.close();
		}
		else
		{
			byte[] bytes = TEXTAREA.getText().getBytes(TMP1);
			FileOutputStream output = new FileOutputStream(f);
			output.write(bytes);
			output.close();
			JOptionPane.showMessageDialog(w, "You are using " + TMP1 + " encoding (beta).\nPlease check if the file is saved correctly.", "Saved", JOptionPane.WARNING_MESSAGE);
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
	
	public void openToTextAreaNoProgress(File f)
	{
		TEXTAREA.setText("");
		try
		{
			BufferedReader br1 = new BufferedReader(new FileReader(f));
			while ((TMP1 = br1.readLine()) != null)
			{
				TEXTAREA.append(TMP1 + "\n");
			}
			br1.close();
		}
		catch (Throwable ex)
		{
			JOptionPane.showMessageDialog(w, "Cannot open file!\nError message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		setFileLabel(f);
		TEXTAREA.setCaretPosition(0);
	}
	
	public void openToTextArea(final File f)
	{
		final RandomProgress prog = new RandomProgress();
		final SwingWorker<Void, Void> task = new SwingWorker<Void, Void>()
		{
			@Override
			public Void doInBackground()
			{
				TEXTAREA.setText("");
				try
				{
					BufferedReader br1 = new BufferedReader(new FileReader(f));
					while (((TMP1 = br1.readLine()) != null)&&(!this.isCancelled()))
					{
						TEXTAREA.append(TMP1 + "\n");
					}
					br1.close();
				}
				catch (Throwable ex)
				{
					JOptionPane.showMessageDialog(w, "Cannot open file!\nError message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					return null;
				}
				setFileLabel(f);
				TEXTAREA.setCaretPosition(0);
				prog.dispose();
				return null;
			}
		};
		task.execute();
		prog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent ev)
			{
				if (task.cancel(true))
				{
					JOptionPane.showMessageDialog(w, "Stopped opening file " + f.getPath() + ".", "Stopped", JOptionPane.WARNING_MESSAGE);
				}
				currentFile.setText(" ");
				file = null;
			}
		});
	}
	
	public void setFileLabel(File f)
	{
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
	
	public static String getConfig(String name)
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
	
	public static void writeConfig(String key, String value)
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
	
	class MyTextField extends JTextField
	{
		private int x;
		public MyTextField(int size, int x)
		{
			super(size);
			this.x = x;
			this.setFont(f13);
			this.setForeground(Color.BLACK);
			this.setBorder(bord1);
		}
		
		public int getIndex()
		{
			return this.x;
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
			this.setFocusPainted(false);
			this.x = x;
		}
		
		public int getIndex()
		{
			return this.x;
		}
	}
	
	class MyCheckBox extends JCheckBox
	{
		public MyCheckBox(String str, boolean isSelected)
		{
			super(str, isSelected);
			this.setFont(f13);
			this.setBackground(Color.WHITE);
			this.setFocusPainted(false);
		}
	}
	
	class MyPanel extends JPanel
	{
		public static final int LEFT = 1;
		public static final int CENTER = 2;
		public MyPanel(int x)
		{
			super();
			if (x == LEFT)
			{
				this.setLayout(new FlowLayout(FlowLayout.LEFT));
			}
			else
			{
				this.setLayout(new FlowLayout(FlowLayout.CENTER));
			}
			this.setBackground(Color.WHITE);
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
		
		public void setPosition(int position)
		{
			this.position = position;
		}
	}
}
