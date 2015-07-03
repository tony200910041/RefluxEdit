import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.filechooser.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
import java.text.*;
import myjava.gui.*;
import myjava.gui.common.*;

public class RefluxEdit extends JFrame implements Resources
{
	private static final float VERSION_NO = (float)3.0;
	private static final String BETA_NO = "alpha";
	private static final File SettingsFile = new File(getSettingsFilePath(), "REFLUXEDITPREF.PROPERTIES");
	private static final Properties prop = new Properties();
	// screen dimension
	private static final int WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private static final int HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	// components
	private static JTextArea TEXTAREA = new JTextArea();
	private MyLabel currentFile = new MyLabel(" ");
	private final MyLabel leftEdge = new MyLabel(" ");
	private final MyLabel rightEdge = new MyLabel(" ");
	private final JLabel leftEdgeOld = new JLabel("  ");
	private final JLabel rightEdgeOld = new JLabel("  ");
	private static JMenuBar menubar;
	private MyRibbonPanel ribbon = new MyRibbonPanel();
	private static boolean isOtherLAF = true;
	private static boolean isRibbon = true;
	// file choosers
	private static JFileChooser WindowsChooser;
	private static MyFileChooser JavaChooser;
	private static JFileChooser chooser;
	private static MyFileFilter myTextFilter = new MyFileFilter()
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
	};
	private static final FileNameExtensionFilter textFilter = new FileNameExtensionFilter("Text file", new String[]{"txt", "java", "py", "php", "html", "htm", "xml", "bot", "properties"});
	private File file = null;	
	private static final JPopupMenu popup = new JPopupMenu();
	private static final Clipboard clipbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	UndoManager undoManager = new UndoManager();
	static boolean isSaved = true;
	// main window
	private static RefluxEdit w;
	private JComponent topPanel = null;
	private JPanel fourButtonPanel = new JPanel();
	private JToolBar toolBar = new JToolBar("ToolBar");
	// toolBar buttons
	MyToolBarButton toolBarNew = new MyToolBarButton("NEW32", "New file", 1);
	MyToolBarButton toolBarOpen = new MyToolBarButton("OPEN32", "Open file", 2);
	MyToolBarButton toolBarSave = new MyToolBarButton("SAVE32", "Save as", 4);
	MyToolBarButton toolBarPrint = new MyToolBarButton("PRINT32", "Print", 38);
	MyToolBarButton toolBarCut = new MyToolBarButton("CUT32", "Cut selection", 11);
	MyToolBarButton toolBarCopy = new MyToolBarButton("COPY32", "Copy selection", 12);
	MyToolBarButton toolBarPaste = new MyToolBarButton("PASTE32", "Paste", 13);
	MyToolBarButton toolBarDelete = new MyToolBarButton("DELETE32", "Delete selection", 15);
	MyToolBarButton toolBarSearch = new MyToolBarButton("SEARCH32", "Search", 23);
	MyToolBarButton toolBarReplace = new MyToolBarButton("REPLACE32", "Replace", 24);
	MyToolBarButton toolBarOptions = new MyToolBarButton("OPTIONS32", "Toolbar options", 0);
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
				RefluxEdit.initialize();
				RefluxEdit.setLAF();				
				w = new RefluxEdit("RefluxEdit " + VERSION_NO);
				w.restoreFrame();
				w.buildWindowsChooser();
				w.restoreTextArea();
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
			
			void showJFrame()
			{
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
				if ((chooserWorker.isDone())&&(menuWorker.isDone())&&(popupWorker.isDone()))
				{
					splash.close();
					w.setVisible(true);
					writeConfig("LastStartupTimeTaken", System.currentTimeMillis()-initialTime + "ms");
				}
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
		
		bottomPanel.add(currentFile);
		this.add(bottomPanel, BorderLayout.PAGE_END);
		// narrow edge
		boolean isNarrow;
		try
		{
			isNarrow = getConfig("isUseNarrowEdge").equals("true");
		}
		catch (Exception ex)
		{
			isNarrow = true;
		}
		if (isNarrow)
		{
			this.add(leftEdge, BorderLayout.LINE_START);
			this.add(rightEdge, BorderLayout.LINE_END);
		}
		else
		{
			this.add(leftEdgeOld, BorderLayout.LINE_START);
			this.add(rightEdgeOld, BorderLayout.LINE_END);
		}
		JScrollPane scrollPane = new JScrollPane(TEXTAREA);
		this.add(scrollPane, BorderLayout.CENTER);
		this.addTopPanel();
		// Swing defaults
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
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setDismissDelay(6500);
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
					writeConfig("isMaxmized", (getExtendedState() == JFrame.MAXIMIZED_BOTH)+"");
					System.exit(0);
				}
			}
		});
	}
	
	protected static void setLAF()
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
				boolean isColored;
				try
				{
					isColored = getConfig("isUseNewMenuBar").equals("true");
				}
				catch (Exception ex)
				{
					isColored = true;
				}
				menubar = new ColoredMenuBar(isColored);
				isOtherLAF = false;
				menubar.setBorderPainted(false);
				break;
			}
		}
		catch (Throwable ex)
		{
			menubar = new JMenuBar();
			menubar.setBackground(new Color(242,254,255));
		}
	}
	
	static class ColoredMenuBar extends JMenuBar
	{
		private boolean isColored;
		public ColoredMenuBar(boolean isColored)
		{
			super();
			this.isColored = isColored;
		}
		
		public void setColored(boolean isColored)
		{
			this.isColored = isColored;
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2D = (Graphics2D)(g.create());
			int w = this.getWidth();
			int h = this.getHeight();
			this.setBackground(Color.WHITE);
			if (this.isColored)
			{				
				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)0.5));
				g2D.setPaint(new GradientPaint(0, 0, new Color(242,254,255), w, 0, new Color(255,250,217)));
			}
			else
			{
				g2D.setColor(new Color(242,254,255));
			}
			g2D.fillRect(0, 0, w, h);
			g2D.dispose();	
		}
	}
	
	protected static void initialize()
	{
		//size
		if (!SettingsFile.exists())
		{
			try
			{
				PrintWriter writer = new PrintWriter(SettingsFile, "UTF-8");
				writer.close();
				writeConfig("Size.x", "690"); //460,395
				writeConfig("Size.y", "550");
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
				writeConfig("isUseNewMenuBar", "true");
				writeConfig("isUseNarrowEdge", "true");
				writeConfig("lineSeparator", "\\n");
				writeConfig("isRibbon", "true");
			}
			catch (Exception ex)
			{
			}
		}
	}
	
	protected void addTopPanel()
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
			// setup "four button panel"
			fourButtonPanel.add(new MyShortcutButton("New", -1));
			fourButtonPanel.add(new MyShortcutButton("Open", 2));
			fourButtonPanel.add(new MyShortcutButton("Save as", 4));
			fourButtonPanel.add(new MyShortcutButton("Save", 5));
			// setup toolbar
			// section 1
			boolean b1 = getBoolean("ToolBar.new");
			boolean b2 = b1;
			if (b1) toolBar.add(toolBarNew);
			b1 = getBoolean("ToolBar.open");
			b2 = b2||b1;
			if (b1) toolBar.add(toolBarOpen);
			b1 = getBoolean("ToolBar.save");
			b2 = b2||b1;
			if (b1) toolBar.add(toolBarSave);
			b1 = getBoolean("ToolBar.print");
			b2 = b2||b1;
			if (b1) toolBar.add(toolBarPrint);
			if (b2) ((JToolBar)toolBar).addSeparator();
			// section 2
			b1 = getBoolean("ToolBar.cut");
			b2 = b1;
			if (b1) toolBar.add(toolBarCut);
			b1 = getBoolean("ToolBar.copy");
			b2 = b1||b2;
			if (b1) toolBar.add(toolBarCopy);
			b1 = getBoolean("ToolBar.paste");
			b2 = b1||b2;
			if (b1) toolBar.add(toolBarPaste);
			if (b2) ((JToolBar)toolBar).addSeparator();
			// section 3
			if (getBoolean("ToolBar.delete")) toolBar.add(toolBarDelete);
			if (getBoolean("ToolBar.search")) toolBar.add(toolBarSearch);
			if (getBoolean("ToolBar.replace")) toolBar.add(toolBarReplace);
			toolBar.add(toolBarOptions);
			switch (TMP1)
			{
				case "no": //no container
				topPanel = null;
				break;
				
				case "true": //use panel
				topPanel = fourButtonPanel;			
				this.add(fourButtonPanel, BorderLayout.PAGE_START);
				break;
				
				case "false": //use toolbar
				topPanel = toolBar;
				this.add(toolBar, BorderLayout.PAGE_START);
				break;
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
		//maximized
		try
		{
			TMP1 = getConfig("isMaxmized");
			if (TMP1 == null) throw new Exception();
		}
		catch (Exception ex)
		{
			TMP1 = "false";
		}
		finally
		{
			if (TMP1.equals("true"))
			{
				this.setExtendedState(JFrame.MAXIMIZED_BOTH);
			}
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
			this.setIconImage((new ImageIcon(getClass().getResource("/myjava/SRC/APPICON.PNG"))).getImage());
		}
		catch (Exception ex)
		{
		}
	}
	
	protected void buildWindowsChooser()
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
	
	protected void restoreChoosers()
	{
		JComponent.setDefaultLocale(Locale.ENGLISH);
		JavaChooser = new MyFileChooser("Choose:");
		WindowsChooser.setPreferredSize(new Dimension(560,390));
		WindowsChooser.setDialogTitle("Choose:");
		WindowsChooser.setCurrentDirectory(new File("./"));
	}
	
	protected void restoreMenus()
	{
		String menuOption;
		try
		{
			menuOption = getConfig("isRibbon");
		}
		catch (Exception ex)
		{
			menuOption = "true";
		}
		if (menuOption == null) menuOption = "true";
		isRibbon = menuOption.equals("true");
		if (isRibbon)
		{
			this.add(ribbon, BorderLayout.PAGE_START);
			MyRibbonFirst button = new MyRibbonFirst();
			button.add("New File", 1);
			button.add("Open File", 2);
			button.add("Open File (quick)", 3);
			button.add("Save As", 4);
			button.add("Save", 5);
			button.add("Export to Image", 43);
			button.add("Print", 38);
			button.add("Close", 6);
			button.add("\u2190", 0);
			ribbon.addAsFirstComponent(button);
			
			MyRibbonTab tab2 = new MyRibbonTab("EDIT");
			MyRibbonTab tab3 = new MyRibbonTab("TOOLS");
			MyRibbonTab tab4 = new MyRibbonTab("INSERT");
			MyRibbonTab tab5 = new MyRibbonTab("HELP");
			
			tab2.add(new MyRibbonButton("Undo", "UNDO32", "<html><font size=\"4\"><b>Undo previous change(s)</b></font><br>You can also press Ctrl+Z to perform undo action.</font></html>", false, 7));
			tab2.add(new MyRibbonButton("Redo", "REDO32", "<html><font size=\"4\"><b>Redo previous change(s)</b></font><br>You can also press Ctrl+Y to perform redo action.</html>", false, 8));
			JPanel tab2_1 = new JPanel(new GridLayout(2,1,10,10));
			tab2_1.setOpaque(false);
			tab2_1.add(new MyRibbonButton("Select all", "SELECT", "<html><font size=\"4\"><b>Select all</b></font><br>Select all text in the text area.</html>", true, 9));
			tab2_1.add(new MyRibbonButton("Select all and copy", "SELECT", "<html><font size=\"4\"><b>Select all and copy</b></font><br>Select all text in the text area and<br>copy to the system clipboard.</html>", true, 10));
			tab2.add(tab2_1);
			tab2.add(separator());
			tab2.add(new MyRibbonButton("Cut", "CUT32", "<html><font size=\"4\"><b>Cut selected text</b></font><br>The selected text will be moved<br>to the system clipboard.</html>", false, 11));
			tab2.add(new MyRibbonButton("Copy", "COPY32", "<html><font size=\"4\"><b>Copy selected text</b></font><br>The selected text will be copied<br>to the system clipboard.</html>", false, 12));
			JPanel tab2_2 = new JPanel(new GridLayout(2,1,10,10));
			tab2_2.setOpaque(false);
			tab2_2.add(new MyRibbonButton("Paste", "PASTE", "<html><font size=\"4\"><b>Paste text</b></font><br>Paste the text in the system clipboard<br>to the text area.</html>", true, 13));
			tab2_2.add(new MyRibbonButton("Paste on next line", "PASTE", "<html><font size=\"4\"><b>Paste text on next line</b></font><br>Insert the text in the system clipboard<br>on the next line.</html>", true, 14));
			tab2.add(tab2_2);
			tab2.add(separator());
			tab2.add(new MyRibbonButton("Delete", "DELETE32", "<html><font size=\"4\"><b>Delete selected text</b></font><br>The selected text will be deleted.</html>", false, 15));
			
			tab3.add(new MyRibbonButton("<html>Editing/<br>viewing</html>", "EDIT32", "<html><font size=\"4\"><b>Enable/disable editing</b></font><br>Click here to disable/re-enable editing.<br></html>", false, 17));
			tab3.add(new MyRibbonButton("<html>On top</html>", "ONTOP", "<html><font size=\"4\"><b>Enable/disable always on top</b></font><br>Click here to enable/disable RefluxEdit always staying on top.</html>", false, 21));
			tab3.add(separator());
			JPanel tab3_1 = new JPanel(new GridLayout(2,1,10,10));
			tab3_1.setOpaque(false);
			tab3_1.add(new MyRibbonButton("Word count", "WORDCOUNT", "<html><font size=\"4\"><b>Word count</b></font><br>Count how many words are in the selected text,<br>or all words if no text is selected.</html>", true, 22));
			tab3_1.add(new MyRibbonButton("Character count", "CHARACTERCOUNT", "<html><font size=\"4\"><b>Character count</b></font><br>Count how many characters are in the selected text,<br>or all characters if no text is selected.</html>", true, 44));
			tab3.add(tab3_1);
			tab3.add(separator());
			tab3.add(new MyRibbonButton("<html><font color=\"red\">Delete</font><br>&nbsp;blank<br>&nbsp;&nbsp;lines</html>", null, "<html><font size=\"4\"><b>Delete blank lines</b></font><br>ALL blank lines will be deleted.</html>", false, 35));
			tab3.add(separator());
			JPanel tab3_2 = new JPanel(new GridLayout(3,1,0,0));
			tab3_2.setOpaque(false);
			tab3_2.add(new MyRibbonButton("Uppercase", "UPPERCASE", "<html><font size=\"4\"><b>Convert to uppercase</b></font><br>Convert the selected text to uppercase,<br>or all characters if no text is selected.</html>", true, 26));
			tab3_2.add(new MyRibbonButton("Lowercase", "LOWERCASE", "<html><font size=\"4\"><b>Convert to lowercase</b></font><br>Convert the selected text to lowercase,<br>or all characters if no text is selected.</html>", true, 27));
			tab3_2.add(new MyRibbonButton("Invert case", "INVERTCASE", "<html><font size=\"4\"><b>Convert to invert case</b></font><br>Convert the selected text to invert case<br>(uppercase to lowercase, and lowercase to uppercase),<br>or all characters if no text is selected.</html>", true, 28));
			tab3.add(tab3_2);
			tab3.add(separator());
			JPanel tab3_3 = new JPanel(new GridLayout(3,1,0,0));
			tab3_3.setOpaque(false);
			tab3_3.add(new MyRibbonButton("Search", "SEARCH", "<html><font size=\"4\"><b>Search words</b></font><br>Search words in the whole text.</html>", true, 23));
			tab3_3.add(new MyRibbonButton("Replace", "REPLACE", "<html><font size=\"4\"><b>Replace words</b></font><br>Replace words in the whole text.</html>", true, 24));
			tab3_3.add(new MyRibbonButton("Replace (selected)", "REPLACE", "<html><font size=\"4\"><b>Replace words in selected text</b></font><br>Replace words in the SELECTED text.</html>", true, 25));
			tab3.add(tab3_3);
			tab3.add(separator());
			tab3.add(new MyRibbonButton("<html>&nbsp;&nbsp;Color<br>chooser</html>", "COLORCHOOSER32", "<html><font size=\"4\"><b>Show color chooser</b></font><br>A color chooser will be shown which allows you to choose<br>a color and insert REB, HSV or HEX code.</html>", false, 40));
			tab3.add(new MyRibbonButton("<html>&nbsp;&nbsp;&nbsp;Base<br>converter</html>", "BASE32", "<html><font size=\"4\"><b>Base converter</b></font><br>Convert numbers between base 2, 8, 10 and 16.</html>", false, 41));
			
			JPanel tab4_1 = new JPanel(new GridLayout(2,1,10,10));
			tab4_1.setOpaque(false);
			tab4_1.add(new MyRibbonButton("10 \"=\"", null, "<html><font size=\"4\"><b>Insert 10 \"=\"</b></font><br>Insert ten equal signs</html>", true, 30));
			tab4_1.add(new MyRibbonButton("Four \" \"", null, "<html><font size=\"4\"><b>Insert four spaces</b></font><br>Insert four spaces. Useful for programmers.</html>", true, 31));
			tab4.add(tab4_1);
			tab4.add(separator());
			tab4.add(new MyRibbonButton("<html>Random<br>&nbsp;&nbsp;words</html>", "RANDOM", "<html><font size=\"4\"><b>Generate random words</b></font><br>Generate specified number of \"words\" randomly.<br>The words will be between 1 and 10 character(s) long.<br>Note that performing this action may take a long time.</html>", false, 32));
			tab4.add(new MyRibbonButton("<html>&nbsp;&nbsp;&nbsp;Java<br>keywords</html>", "JAVA32", "<html><font size=\"4\"><b>Insert Java keywords</b></font><br>Insert Java keywords. Useful for Java developers.<br>More will be introduced in later versions.</html>", false, 33));
			tab4.add(new MyRibbonButton("<html>&nbsp;&nbsp;&nbsp;HTML<br>keywords</html>", "HTML32", "<html><font size=\"4\"><b>Insert HTML keywords</b></font><br>Insert HTML keywords. Useful for web developers.<br>More will be introduced in later versions.</html>", false, 34));
			tab4.add(separator());
			tab4.add(new MyRibbonButton("<html>&nbsp;Unicode<br>character</html>", "UNICODE32", "<html><font size=\"4\"><b>Insert unicode character</b></font><br>Insert unicode character by given code value.</html>", false, 45));
			tab4.add(new MyRibbonButton("<html>Unicode<br>&nbsp;&nbsp;value</html>", "UNICODE32", "<html><font size=\"4\"><b>Insert unicode value</b></font><br>Insert unicode value by given character.</html>", false, 46));
			
			tab5.add(new MyRibbonButton("<html>&nbsp;&nbsp;About<br>RefluxEdit</html>", "APPICON32", "<html><font size=\"4\"><b>About RefluxEdit</b></font><br>RefluxEdit is a lightweight plain text editor written in Java by tony200910041.<br>SourceForge page: http://refluxedit.sourceforge.net</html>", false, 16));
			tab5.add(separator());
			tab5.add(new MyRibbonButton("<html>Line Wrap</html>", "LINEWRAP32", "<html><font size=\"4\"><b>Line wrap options</b></font><br>Choose to enable/disable line wrap (automatically breaking lines)<br>and its style.</html>", false, 18));
			tab5.add(new MyRibbonButton("<html>Encoding</html>", "ENCODING48", "<html><font size=\"4\"><b>Encoding options</b></font><br>Four encoding options: default, ISO-8859-1, UTF-8 and UTF-16BE.<br>Note that this is still a beta function and may contain some unknown bugs.<br>Please see if the text file is saved correctly if default encoding is not used.</html>", false, 19));
			tab5.add(new MyRibbonButton("<html>&nbsp;&nbsp;&nbsp;&nbsp;Line<br>separator</html>", "LINESEPARATOR32", "<html><font size=\"4\"><b>Line separator options</b></font><br>Three line separators: \\n, \\r and \\r\\n</html>", false, 47));
			tab5.add(new MyRibbonButton("<html>&nbsp;&nbsp;&nbsp;&nbsp;File<br>chooser</html>", "FILECHOOSER32", "<html><font size=\"4\"><b>File chooser options</b></font><br>Three file choosers: Java, Windows and Beta</html>", false, 20));
			tab5.add(new MyRibbonButton("<html>Tab size</html>", "TABSIZE32", "<html><font size=\"4\"><b>Tab size options</b></font><br>Change the tab size.</html>", false, 29));
			tab5.add(new MyRibbonButton("<html>Selection<br>&nbsp;&nbsp;&nbsp;color</html>", "SELECTIONCOLOR32", "<html><font size=\"4\"><b>Selection color options</b></font><br>Change the selection color of the text.</html>", false, 36));
			tab5.add(new MyRibbonButton("<html>LAF</html>", "LAF48", "<html><font size=\"4\"><b>Look and Feel options</b></font><br>Change the look of RefluxEdit!</html>", false, 37));
			tab5.add(new MyRibbonButton("<html>&nbsp;Other<br>options</html>", "OPTIONS32", "<html><font size=\"4\"><b>Other options</b></font><br>Miscellaneous options</html>", false, 39));
			tab5.add(new MyRibbonButton("<html>&nbsp;Reduce<br>memory<br>&nbsp;&nbsp;usage</html>", null, "<html><font size=\"4\"><b>Reduce memory usage</b></font><br>System.gc() will be executed.</html>", false, 42));
		}   //"<html><font size=\"4\"><b></b></font><br></html>"
		else
		{		
			this.setJMenuBar(menubar);
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
			menu1.add(new MyMenuItem("Export to image", null, 43));
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
			menu3.add(new MyMenuItem("Word count", null, 22).setAccelerator(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));
			menu3.add(new MyMenuItem("Character count", null, 44));
			menu3.add(new MyMenuItem("Delete blank lines", null, 35));
			menu3.add(menu3_2);
			menu3.add(new JSeparator());
			menu3.add(new MyMenuItem("Search", "SEARCH", 23).setAccelerator(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
			menu3.add(new MyMenuItem("Replace", null, 24));
			menu3.add(new MyMenuItem("Replace in selection", null, 25));
			menu3.add(new JSeparator());
			menu3.add(new MyMenuItem("Show JColorChooser", null, 40));
			menu3.add(new MyMenuItem("Base converter (2/8/10/16)", null, 41));
			
			menu3_1.add(new MyMenuItem("Line Wrap options", null, 18));
			menu3_1.add(new MyMenuItem("Encoding options", null, 19));
			menu3_1.add(new MyMenuItem("Line separator options", null, 47));
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
			menu4.add(new MyMenuItem("Insert unicode character", null, 45));
			menu4.add(new MyMenuItem("Insert unicode value", null, 46));
			
			menu5.add(new MyMenuItem("About RefluxEdit", "APPICON16", 16).setAccelerator(KeyEvent.VK_F1, ActionEvent.CTRL_MASK));
			//next one: 48
		}
	}
	
	protected void restoreTextArea()
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
				if (time == 3)
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
	
	protected void restorePopup()
	{
		popup.add(new MyMenuItem("Cut", "CUT", 11));
		popup.add(new MyMenuItem("Copy", "COPY", 12));
		popup.add(new MyMenuItem("Paste", "PASTE", 13));
		popup.add(new MyMenuItem("Delete", null, 15));
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
		public MyToolBarButton(String icon, String tooltip, int x)
		{
			super();
			this.setFocusable(false);
			this.setToolTipText(tooltip);
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
				this.setIcon(new ImageIcon(getClass().getResource("/myjava/SRC/" + icon + ".PNG")));
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
				toolBar.removeAll();
				boolean b1 = isNew;
				boolean b2 = isNew;
				if (b1) toolBar.add(toolBarNew);
				b1 = isOpen;
				b2 = b2||b1;
				if (b1) toolBar.add(toolBarOpen);
				b1 = isSave;
				b2 = b2||b1;
				if (b1) toolBar.add(toolBarSave);
				b1 = isPrint;
				b2 = b2||b1;
				if (b1) toolBar.add(toolBarPrint);
				if (b2) ((JToolBar)toolBar).addSeparator();
				
				b1 = isCut;
				b2 = b1;
				if (b1) toolBar.add(toolBarCut);
				b1 = isCopy;
				b2 = b1||b2;
				if (b1) toolBar.add(toolBarCopy);
				b1 = isPaste;
				b2 = b1||b2;
				if (b1) toolBar.add(toolBarPaste);
				if (b2) ((JToolBar)toolBar).addSeparator();
				
				if (isDelete) toolBar.add(toolBarDelete);
				if (isSearch) toolBar.add(toolBarSearch);
				if (isReplace) toolBar.add(toolBarReplace);
				toolBar.add(toolBarOptions);
				toolBar.revalidate();
				toolBar.repaint();
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
	
	class MyShortcutButton extends JButton
	{
		public MyShortcutButton(String str, int x)
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
		
		public int toNumber(char letter)
		{
			switch (letter)
			{
				case ' ': return 0;
				case 'a': return 1;
				case 'b': return 2;
				case 'c': return 3;
				case 'd': return 4;
				case 'e': return 5;
				case 'f': return 6;
				case 'g': return 7;
				case 'h': return 8;
				case 'i': return 9;
				case 'j': return 10;
				case 'k': return 11;
				case 'l': return 12;
				case 'm': return 13;
				case 'n': return 14;
				case 'o': return 15;
				case 'p': return 16;
				case 'q': return 17;
				case 'r': return 18;
				case 's': return 19;
				case 't': return 20;
				case 'u': return 21;
				case 'v': return 22;
				case 'w': return 23;
				case 'x': return 24;
				case 'y': return 25;
				case 'z': return 26;
				
				case 'A': return 27;
				case 'B': return 28;
				case 'C': return 29;
				case 'D': return 30;
				case 'E': return 31;
				case 'F': return 32;
				case 'G': return 33;
				case 'H': return 34;
				case 'I': return 35;
				case 'J': return 36;
				case 'K': return 37;
				case 'L': return 38;
				case 'M': return 39;
				case 'N': return 40;
				case 'O': return 41;
				case 'P': return 42;
				case 'Q': return 43;
				case 'R': return 44;
				case 'S': return 45;
				case 'T': return 46;
				case 'U': return 47;
				case 'V': return 48;
				case 'W': return 49;
				case 'X': return 50;
				case 'Y': return 51;
				case 'Z': return 52;
				
				case '1': return 53;
				case '2': return 54;
				case '3': return 55;
				case '4': return 56;
				case '5': return 57;
				case '6': return 58;
				case '7': return 59;
				case '8': return 60;
				case '9': return 61;
				case '0': return 62;
				default: return 63;
			}
		}
		
		public String toLetter(int a)
		{
			switch (a)
			{
				case 0: return "Space";
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
				case 26: return "z";
				
				case 27: return "A";
				case 28: return "B";
				case 29: return "C";
				case 30: return "D";
				case 31: return "E";
				case 32: return "F";
				case 33: return "G";
				case 34: return "H";
				case 35: return "I";
				case 36: return "J";
				case 37: return "K";
				case 38: return "L";
				case 39: return "M";
				case 40: return "N";
				case 41: return "O";
				case 42: return "P";
				case 43: return "Q";
				case 44: return "R";
				case 45: return "S";
				case 46: return "T";
				case 47: return "U";
				case 48: return "V";
				case 49: return "W";
				case 50: return "X";
				case 51: return "Y";
				case 52: return "Z";
				
				case 53: return "1";
				case 54: return "2";
				case 55: return "3";
				case 56: return "4";
				case 57: return "5";
				case 58: return "6";
				case 59: return "7";
				case 60: return "8";
				case 61: return "9";
				case 62: return "0";
				default: return "Others";
			}
		}
		
		public char toChar(String unicode)
		{
			return (char)Integer.parseInt(unicode, 16);
		}
		
		public String toUnicodeValue(char c)
		{
			TMP1 = Integer.toHexString(c);
			j = TMP1.length();
			for (i=1; i<5-j; i++)
			{
				TMP1 = "0" + TMP1;
			}
			return "\\u" + TMP1.toUpperCase();
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
			if (ev.getSource() instanceof MyShortcutButton)
			{
				((MyShortcutButton)(ev.getSource())).setBorder(bord2);
			}
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
		{
			if (x == -1)
			{
				isOnNew = false;
				((MyShortcutButton)(ev.getSource())).setText("New");
			}
			if (ev.getSource() instanceof MyShortcutButton)
			{
				((MyShortcutButton)(ev.getSource())).setBorder(bord1);
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
					((MyShortcutButton)(ev.getSource())).setText("New");
				}
				else
				{
					if (ev.getSource() instanceof MyShortcutButton)
					{
						((MyShortcutButton)(ev.getSource())).setText("Really?");
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
					chooser.resetChoosableFileFilters();
					chooser.addChoosableFileFilter(textFilter);
					i = chooser.showOpenDialog(w);
					if (i == JFileChooser.APPROVE_OPTION)
					{
						openToTextArea(chooser.getSelectedFile());
					}
					break;
										
					case "Beta":
					FileChooser.resetFileFilter();
					FileChooser.addFileFilter(myTextFilter);
					File[] f_a = FileChooser.showFileChooser(new File(getSettingsFilePath()), w);
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
					FileChooser.resetFileFilter();
					FileChooser.addFileFilter(myTextFilter);
					outdo1:
					do
					{
						File[] f_a = FileChooser.showFileChooser(new File(getSettingsFilePath()), w);
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
						chooser.resetChoosableFileFilters();
						chooser.addChoosableFileFilter(textFilter);
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
						FileChooser.resetFileFilter();
						FileChooser.addFileFilter(myTextFilter);
						outdo3:
						do
						{
							File[] f_a = FileChooser.showFileChooser(new File(getSettingsFilePath()), w);
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
							chooser.resetChoosableFileFilters();
							chooser.addChoosableFileFilter(textFilter);
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
						undoManager.backup(TEXTAREA.getText());
					}
					TMP1 = undoManager.undo();
					try
					{
						if (TMP1 != null)
						{
							i = TEXTAREA.getCaretPosition();
							TEXTAREA.setText(TMP1);
							TEXTAREA.setCaretPosition(i);
						}
					}
					catch (Exception ex)
					{
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
				TMP1 = "RefluxEdit " + VERSION_NO + BETA_NO + " -- a lightweight plain text editor written in Java.\nBy tony200910041, http://tony200910041.wordpress.com\nDistributed under MPL 2.0.\nuser.home: " + System.getProperty("user.home") + "\nYour operating system is " + System.getProperty("os.name") + " (" + System.getProperty("os.version") + "), " + System.getProperty("os.arch") + "\n\nIcon sources: http://www.iconarchive.com and LibreOffice.";
				TMP2 = "About RefluxEdit " + VERSION_NO;
				try
				{
					JOptionPane.showMessageDialog(w, TMP1, TMP2, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/myjava/SRC/Duke.gif")));
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(w, TMP1, TMP2, JOptionPane.INFORMATION_MESSAGE);
				}
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
				if (TMP1 == null) return;
				if (TMP1.isEmpty()) return;
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
				TMP1 = TEXTAREA.getText();
				if (TMP1 == null) return;
				if (TMP1.isEmpty()) return;
				i = 0;
				j = TMP1.length();
				k = 0;
				TMP1 = null;
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
				TMP1 = TEXTAREA.getText();
				if (TMP1 == null) return;
				if (TMP1.isEmpty()) return;
				if (TEXTAREA.isEditable())
				{
					final JDialog replace = new JDialog(w, "Replace", true);
					replace.getContentPane().setBackground(Color.WHITE);
					replace.setLayout(new GridLayout(3,1,0,0));
					final MyTextField wd1 = new MyTextField(10, 0);
					final MyTextField wd2 = new MyTextField(10, 0);
					MyPanel original = new MyPanel(MyPanel.LEFT);
					original.add(new MyLabel("Original: "));
					original.add(wd1);
					replace.add(original);
					MyPanel replaced = new MyPanel(MyPanel.LEFT);
					replaced.add(new MyLabel("Replaced by: "));
					replaced.add(wd2);
					replace.add(replaced);
					MyPanel panel_button = new MyPanel(MyPanel.CENTER);
					final MyButton button = new MyButton("Start")
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
					};
					panel_button.add(button);
					replace.add(panel_button);
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
				
				case 32: //random words
				if (TEXTAREA.isEditable())
				{
					TMP1 = null;
					TMP2 = "";
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
										TMP2 = TMP2 + TMP1 + " ";
										if (j%50 == 0)
										{
											prog.setValue(j);
										}
										if (this.interrupted() == true) return;
									}
									prog.dispose();
									isSaved = false;
									TEXTAREA.insert(TMP2, TEXTAREA.getCaretPosition());
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
								TEXTAREA.insert(TMP2, TEXTAREA.getCaretPosition());
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
				MyCheckBox isRibbonBox = new MyCheckBox("Use Ribbon UI", isRibbon);				
				LAFOption.setLayout(new GridLayout(4,1,0,0));
				LAFOption.add(isDefaultL);
				LAFOption.add(isWindowsL);
				LAFOption.add(isNimbus);
				LAFOption.add(isRibbonBox);
				LAFOption.setSize(250,180);
				LAFOption.setLocationRelativeTo(w);
				LAFOption.setVisible(true);				
				writeConfig("LAF", TMP1);
				writeConfig("isRibbon", isRibbonBox.isSelected()+"");
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
				option.setLayout(new GridLayout(4,1,0,0));
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
				P1.add(new MyLabel("Toolbar mode: "));
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
				MyPanel P3 = new MyPanel(MyPanel.LEFT);
				boolean isUseNewMenuBar;
				try
				{
					isUseNewMenuBar = getConfig("isUseNewMenuBar").equals("true");
				}
				catch (Exception ex)
				{
					isUseNewMenuBar = true;
				}
				MyCheckBox useNewMenuBar = new MyCheckBox("Use new colored menu bar (for CrossPlatform Look and Feel only):", isUseNewMenuBar);
				P3.add(useNewMenuBar);
				option.add(P3);
				MyPanel P4 = new MyPanel(MyPanel.LEFT);
				boolean narrowEdge;
				try
				{
					narrowEdge = getConfig("isUseNarrowEdge").equals("true");
				}
				catch (Exception ex)
				{
					narrowEdge = true;
				}
				MyCheckBox useNarrowEdge = new MyCheckBox("Use narrower edge", narrowEdge);
				P4.add(useNarrowEdge);
				option.add(P4);				
				option.setSize(620,220);
				option.setLocationRelativeTo(w);
				toolBar.setUI(new BasicToolBarUI()
				{
					@Override
					public boolean isFloating()
					{
						return false;
					}
				});
				toolBar.updateUI();
				option.setVisible(true);
				if ((!isRibbon)&&(topPanel != null))
				{
					w.remove(topPanel);
				}
				if (isPanel.isSelected())
				{
					writeConfig("isPanel", "true");
					if (!isRibbon)
					{
						w.add(fourButtonPanel, BorderLayout.PAGE_START);
						fourButtonPanel.revalidate();
						fourButtonPanel.repaint();
						topPanel = fourButtonPanel;
					}
				}
				else if (isToolBar.isSelected())
				{
					writeConfig("isPanel", "false");
					if (!isRibbon)
					{
						w.add(toolBar, BorderLayout.PAGE_START);
						toolBar.revalidate();
						toolBar.repaint();
						topPanel = toolBar;
					}
				}
				else if (NoContainer.isSelected())
				{
					writeConfig("isPanel", "no");
					topPanel = null;
				}
				Font selectedFont = fontChooser.getFont();
				writeConfig("TextAreaFont.fontName", selectedFont.getFontName());
				writeConfig("TextAreaFont.fontStyle", selectedFont.getStyle() + "");
				writeConfig("TextAreaFont.fontSize", selectedFont.getSize() + "");
				TEXTAREA.setFont(selectedFont);
				isUseNewMenuBar = useNewMenuBar.isSelected();
				writeConfig("isUseNewMenuBar", isUseNewMenuBar + "");
				if ((menubar instanceof ColoredMenuBar)&&(!isRibbon))
				{
					ColoredMenuBar coloredBar = (ColoredMenuBar)menubar;
					coloredBar.setColored(isUseNewMenuBar);
					coloredBar.repaint();
				}
				narrowEdge = useNarrowEdge.isSelected();
				writeConfig("isUseNarrowEdge", narrowEdge+"");
				try
				{
					w.remove(leftEdge);
					w.remove(rightEdge);
				}
				catch (Exception ex)
				{
				}
				try
				{
					w.remove(leftEdgeOld);
					w.remove(rightEdgeOld);
				}
				catch (Exception ex)
				{
				}
				if (narrowEdge)
				{
					w.add(leftEdge, BorderLayout.LINE_START);
					w.add(rightEdge, BorderLayout.LINE_END);
				}
				else
				{
					w.add(leftEdgeOld, BorderLayout.LINE_START);
					w.add(rightEdgeOld, BorderLayout.LINE_END);
				}
				w.revalidate();
				w.repaint();
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
						final MyButton colorCodeClose = new MyButton("OK")
						{
							@Override
							public void mouseReleased(MouseEvent ev)
							{
								if (TEXTAREA.isEnabled())
								{
									TMP4 = "";
									if (rgb.isSelected()) TMP4 = " " + TMP1;
									if (hsb.isSelected()) TMP4 = TMP4 + " " + TMP2;
									if (hex.isSelected()) TMP4 = TMP4 + " " + TMP3;
									if (!TMP4.isEmpty())
									{
										undoManager.backup(TEXTAREA.getText());
									}
									TEXTAREA.insert(TMP4, TEXTAREA.getCaretPosition());
									colorCode.setVisible(false);
									colorCode.dispose();
								}
							}
						};
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
						TMP1 = ((MyTextField)(ev.getSource())).getText();
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
				if (TEXTAREA.isEnabled())
				{
					TMP1 = "";
					if (cb2.isSelected()) TMP1 = tf2.getText() + " ";
					if (cb8.isSelected()) TMP1 = TMP1 + tf8.getText() + " ";
					if (cb10.isSelected()) TMP1 = TMP1 + tf10.getText() + " ";
					if (cb16.isSelected()) TMP1 = TMP1 + tf16.getText() + " ";
					if (!TMP1.isEmpty())
					{
						undoManager.backup(TEXTAREA.getText());
					}
					TEXTAREA.insert(TMP1, TEXTAREA.getCaretPosition());
					baseConvert.dispose();
				}
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
				
				case 43: //export as image
				final JDialog exportImage = new JDialog(w, "Export to image", true);
				exportImage.setLayout(new GridLayout(2,1,0,0));
				MyPanel exportP1 = new MyPanel(MyPanel.LEFT);
				final MyTextField exportTF = new MyTextField(28,0);
				exportP1.add(new MyLabel("Export to: "));
				exportP1.add(exportTF);
				JButton exportB1 = new JButton("?");
				exportB1.setBackground(Color.WHITE);
				exportB1.setBorder(bord1);
				exportB1.setPreferredSize(new Dimension(20,20));
				exportB1.setFont(f13);
				exportB1.setFocusPainted(false);
				exportP1.add(exportB1);
				MyPanel exportP2 = new MyPanel(MyPanel.CENTER);
				exportP2.add(new MyLabel("Extra gap (in pixels):"));
				final JSpinner exportSpin = new JSpinner(new SpinnerNumberModel(5,0,100,1));
				exportSpin.setFont(f13);
				exportP2.add(exportSpin);
				exportP2.add(new MyLabel("Format:"));
				String[] formatList = ImageIO.getWriterFormatNames();
				HashSet<String> formatSet = new HashSet<String>();
				for (String name: formatList)
				{
					formatSet.add(name.toLowerCase());
				}
				final JComboBox exportFormat = new JComboBox(formatSet.toArray());
				formatList = null;
				formatSet = null;
				exportFormat.setFont(new Font("Microsoft Jhenghei", Font.PLAIN, 12));
				exportFormat.setBackground(Color.WHITE);
				exportFormat.setSelectedItem("png");
				exportFormat.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						TMP3 = exportTF.getText();
						TMP1 = (new File(TMP3)).getName().toLowerCase();
						TMP2 = exportFormat.getSelectedItem().toString();
						if (TMP1.contains(".")&&!TMP1.endsWith(TMP2))
						{
							exportTF.setText(TMP3.substring(0, TMP3.lastIndexOf(".")) + "." + TMP2);
						}
					}
				});
				exportP2.add(exportFormat);
				MyButton exportB2 = new MyButton("Export");
				exportP2.add(exportB2);
				MyButton exportB3 = new MyButton("Cancel");
				exportP2.add(exportB3);
				exportImage.add(exportP1);
				exportImage.add(exportP2);
				exportImage.pack();
				exportImage.setLocationRelativeTo(w);
				MouseListener exportListener = new MouseAdapter()
				{					
					@Override
					public void mouseReleased(MouseEvent ev)
					{
						TMP3 = exportFormat.getSelectedItem().toString();
						switch (((JButton)(ev.getSource())).getText())
						{
							case "?":
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
								FileChooser.resetFileFilter();
								FileChooser.addFileFilter(new MyFileFilter()
								{
									@Override
									public boolean accept(File f)
									{										
										return (f.isDirectory())||(f.getPath().toLowerCase().endsWith("." + TMP3));
									}
									
									@Override
									public String getDescription()
									{
										return new String("Image (*." + TMP3 + ")");
									}
								});
								outdo1:
								do
								{
									File[] f_a = FileChooser.showFileChooser(new File(getSettingsFilePath()), w);
									if (f_a == null) return;
									f1 = f_a[0];
									if (f1 != null)
									{
										if (f1.exists())
										{
											save1 = (isOverride() == JOptionPane.YES_OPTION);
										}
										else break outdo1;
									}
									else return;
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
									chooser.resetChoosableFileFilters();
									chooser.addChoosableFileFilter(new FileNameExtensionFilter(TMP3.toUpperCase() + " file", new String[]{TMP3}));
									i = chooser.showSaveDialog(w);
									if (i == JFileChooser.APPROVE_OPTION)
									{
										f1 = chooser.getSelectedFile();
										if (!f1.getPath().endsWith("." + TMP3))
										{
											f1 = new File(f1.getPath() + "." + TMP3);
										}
										if (f1.exists())
										{
											save1 = (isOverride() == JOptionPane.YES_OPTION);
										}
										else break outdo2;
									}
									else return;
								} while (!save1);
							}
							TMP1 = f1.getPath();
							exportTF.setText(TMP1);
							break;
							
							case "Export":
							TMP1 = exportTF.getText();
							try
							{
								exportSpin.commitEdit();
							}
							catch (Exception ex)
							{
							}
							m = Short.parseShort(exportSpin.getValue().toString());
							exportImage.dispose();
							if (TMP1 == null) return;
							if (TMP1.isEmpty()) return;
							// get each line
							final ArrayList<String> lines = new ArrayList<String>();
							i = 0;
							TMP2 = TEXTAREA.getText();
							do
							{
								try
								{
									j = Utilities.getRowEnd(TEXTAREA,i);
								}
								catch (BadLocationException ex)
								{
								}
								lines.add(TMP2.substring(i, Math.min(j+1, TMP2.length())).replace("\n", ""));
								i = j+1;
							} while ((j>0)&&(i<TMP2.length()));
							final RandomProgress exportProg = new RandomProgress(0, lines.size());
							final Thread exportThr = new Thread()
							{
								@Override
								public void run()
								{							
									Font font = TEXTAREA.getFont();
									FontMetrics f = new Canvas().getFontMetrics(font);
									j = f.getHeight()+m; //default m=5
									BufferedImage image = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
									Graphics2D g;
									//draw each line
									for (String str: lines)
									{
										i = f.stringWidth(str)+1;
										//lineImage: single line text
										BufferedImage lineImage = new BufferedImage(i, j, BufferedImage.TYPE_INT_RGB);
										g = lineImage.createGraphics();
										g.setFont(font);
										g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
										g.setColor(Color.WHITE);
										g.fillRect(0,0,i,j);
										g.setColor(Color.BLACK);
										g.drawString(str,0,j-m);
										g.dispose();
										k = Math.max(image.getWidth(),i);
										l = image.getHeight()+j;
										BufferedImage newImage = new BufferedImage(k,l,BufferedImage.TYPE_INT_RGB);
										g = newImage.createGraphics();
										g.setColor(Color.WHITE);
										g.fillRect(0,0,k,l);
										g.drawImage(image,0,0,null);
										g.drawImage(lineImage,0,image.getHeight(),null);
										g.dispose();
										image = newImage;
										lineImage = null;
										newImage = null;
										exportProg.setValue(exportProg.getValue()+1);
										if (this.isInterrupted()) return;
									}
									try
									{
										exportProg.setString("Writing image...");
										ImageIO.write(image, TMP3, new File(TMP1));
									}
									catch (IOException ex)
									{
									}
									finally
									{
										font = null;
										f = null;
										TMP1 = null;
										TMP2 = null;
										TMP3 = null;
										exportProg.dispose();
										image = null;
									}
								}
							};
							exportThr.start();
							exportProg.addWindowListener(new WindowAdapter()
							{
								@Override
								public void windowClosing(WindowEvent ev)
								{
									exportThr.interrupt();
								}
							});
							break;
							
							case "Cancel":
							exportImage.dispose();
							return;
						}
					}
				};
				exportB1.addMouseListener(exportListener);
				exportB2.addMouseListener(exportListener);
				exportB3.addMouseListener(exportListener);
				exportImage.setVisible(true);
				break;
				
				case 44: //character count
				if ((TMP1 = TEXTAREA.getSelectedText()) == null)
				{
					TMP1 = TEXTAREA.getText();
				}
				if (TMP1 == null) return;
				if (TMP1.isEmpty()) return;
				char textArray[] = TMP1.replace("\n", "").toCharArray();
				int letterFreq[] = new int[64];
				Arrays.fill(letterFreq, 0);
				for (char x: textArray)
				{
					letterFreq[toNumber(x)]++;
				}
				JDialog charFreqDialog = new JDialog(w, "Character count", true);
				Object[][] rowList = new Object[64][];
				DecimalFormat format3 = new DecimalFormat("##0.###");
				for (i=0; i<64; i++)
				{
					rowList[i] = new Object[]{toLetter(i), letterFreq[i] + " (" + format3.format(letterFreq[i]*100.0/textArray.length) + "%)"}; //build each row
				}
				JTable charFreqTable = new JTable();
				charFreqTable.setModel(new DefaultTableModel(rowList, new String[]{"Character", "Frequency"})
				{
					@Override
					public boolean isCellEditable(int row, int column)
					{
						return false;
					}
				});
				charFreqTable.setFont(f13);
				charFreqTable.setGridColor(Color.BLACK);
				charFreqTable.getTableHeader().setFont(f13);
				charFreqTable.getTableHeader().setReorderingAllowed(false);
				charFreqTable.setDragEnabled(false);
				charFreqTable.revalidate();
				charFreqDialog.setLayout(new BorderLayout());
				charFreqDialog.add(new JScrollPane(charFreqTable), BorderLayout.CENTER);
				charFreqDialog.setSize(250,450);
				charFreqDialog.setLocationRelativeTo(w);
				charFreqDialog.setVisible(true);
				charFreqDialog.dispose();
				rowList = null;					
				charFreqTable = null;
				break;
				
				case 45: //unicode character
				final JDialog unicodeDialog = new JDialog(w, "Insert unicode character", true);
				unicodeDialog.setLayout(new BorderLayout());
				MyPanel unicodeP0 = new MyPanel(MyPanel.LEFT);
				unicodeP0.setLayout(new GridLayout(2,1,0,0));
				MyPanel unicodeP1 = new MyPanel(MyPanel.LEFT);
				unicodeP1.add(new MyLabel("Please enter the unicode e.g. 2190, 00F7 etc."));
				unicodeP0.add(unicodeP1);
				MyPanel unicodeP2 = new MyPanel(MyPanel.CENTER);
				final MyTextField unicodeField = new MyTextField(25, 0);
				unicodeP2.add(unicodeField);
				unicodeP0.add(unicodeP2);
				unicodeDialog.add(unicodeP0, BorderLayout.PAGE_START);
				MyPanel unicodeP3 = new MyPanel(MyPanel.CENTER);
				final JLabel unicodeLbl = new JLabel("N/A");
				unicodeLbl.setFont(new Font(TEXTAREA.getFont().getName(), Font.PLAIN, 50));
				unicodeP3.add(unicodeLbl);
				unicodeDialog.add(unicodeP3, BorderLayout.CENTER);
				MyPanel unicodeP4 = new MyPanel(MyPanel.CENTER);
				MyButton unicodeB1 = new MyButton("Add")
				{
					@Override
					public void mouseReleased(MouseEvent ev)
					{
						unicodeDialog.setVisible(false);
						try
						{
							TEXTAREA.insert(toChar(unicodeField.getText())+"", TEXTAREA.getCaretPosition());
						}
						catch (Exception ex)
						{
						}
					}
				};
				unicodeP4.add(unicodeB1);
				unicodeDialog.add(unicodeP4, BorderLayout.PAGE_END);
				unicodeField.getDocument().addDocumentListener(new DocumentListener()
				{
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
					
					private void update()
					{
						try
						{
							unicodeLbl.setText(toChar(unicodeField.getText())+"");
						}
						catch (Exception ex)
						{
							unicodeLbl.setText("N/A");
						}
					}
				});
				unicodeDialog.pack();
				unicodeDialog.setLocationRelativeTo(w);
				unicodeDialog.setMinimumSize(new Dimension(315,205));
				unicodeDialog.setVisible(true);
				break;
				
				case 46: //char to unicode value
				final JDialog charDialog = new JDialog(w, "Insert unicode value", true);
				charDialog.setLayout(new BorderLayout());
				MyPanel charP0 = new MyPanel(MyPanel.LEFT);
				charP0.setLayout(new GridLayout(2,1,0,0));
				MyPanel charP1 = new MyPanel(MyPanel.LEFT);
				charP1.add(new MyLabel("Please paste the character here: e.g. \u2190, \u00F7"));
				charP0.add(charP1);
				MyPanel charP2 = new MyPanel(MyPanel.CENTER);
				final MyTextField charField = new MyTextField(25, 0);
				charP2.add(charField);
				charP0.add(charP2);
				charDialog.add(charP0, BorderLayout.PAGE_START);
				MyPanel charP3 = new MyPanel(MyPanel.CENTER);
				final JLabel charLbl = new JLabel("N/A");
				charLbl.setFont(new Font("Microsoft Jhenghei", Font.PLAIN, 50));
				charP3.add(charLbl);
				charDialog.add(charP3, BorderLayout.CENTER);
				MyPanel charP4 = new MyPanel(MyPanel.CENTER);
				MyButton charB1 = new MyButton("Add")
				{
					@Override
					public void mouseReleased(MouseEvent ev)
					{
						charDialog.setVisible(false);
						try
						{
							TEXTAREA.insert(toUnicodeValue(charField.getText().charAt(0)), TEXTAREA.getCaretPosition());
						}
						catch (Exception ex)
						{
						}
					}
				};
				charP4.add(charB1);
				charDialog.add(charP4, BorderLayout.PAGE_END);
				charField.getDocument().addDocumentListener(new DocumentListener()
				{
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
					
					private void update()
					{
						TMP1 = charField.getText();
						if (TMP1 != null)
						{
							if ((TMP1.length()!=1)&&(TMP1.length()!=0))
							{
								charField.setBackground(new Color(255,133,133));
								charLbl.setText("N/A");
								return;
							}
							else if (TMP1.length()==1)
							{
								charLbl.setText(toUnicodeValue(charField.getText().charAt(0)));
							}
							else if (TMP1.length()==0)
							{
								charLbl.setText("N/A");
							}
						}
						charField.setBackground(Color.WHITE);
					}
				});
				TMP1 = TEXTAREA.getSelectedText();
				if (TMP1 != null)
				{
					if (TMP1.length() == 1)
					{
						charField.setText(TMP1);
					}
				}
				charDialog.pack();
				charDialog.setLocationRelativeTo(w);
				charDialog.setMinimumSize(new Dimension(315,205));
				charDialog.setVisible(true);
				break;
				
				case 47: //line separator
				JDialog sepDialog = new JDialog(w, "Line separator options", true);
				final MyRadioButton sep_n = new MyRadioButton("\\n (Java default)", false, 1);
				final MyRadioButton sep_r = new MyRadioButton("\\r (Mac OS 9)", false, 2);
				final MyRadioButton sep_nr = new MyRadioButton("\\r\\n (Windows)", false, 3);
				TMP1 = getConfig("lineSeparator");
				if (TMP1 == null) TMP1 = "\\n";
				switch (TMP1)
				{
					case "\\r":
					sep_r.setSelected(true);
					break;
					
					case "\\r\\n":
					sep_nr.setSelected(true);
					break;
					
					default:
					sep_n.setSelected(true);
					break;
				}
				ActionListener sepLis = new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						switch (((MyRadioButton)(ev.getSource())).getIndex())
						{
							case 1:
							sep_n.setSelected(true);
							sep_r.setSelected(false);
							sep_nr.setSelected(false);
							break;
							
							case 2:
							sep_n.setSelected(false);
							sep_r.setSelected(true);
							sep_nr.setSelected(false);
							break;
							
							case 3:
							sep_n.setSelected(false);
							sep_r.setSelected(false);
							sep_nr.setSelected(true);
							break;
						}
					}
				};
				sep_n.addActionListener(sepLis);
				sep_r.addActionListener(sepLis);
				sep_nr.addActionListener(sepLis);
				sepDialog.setLayout(new FlowLayout(FlowLayout.LEFT));
				sepDialog.add(sep_n);
				sepDialog.add(sep_r);
				sepDialog.add(sep_nr);
				sepDialog.getContentPane().setBackground(Color.WHITE);
				sepDialog.pack();
				sepDialog.setLocationRelativeTo(w);
				sepDialog.setVisible(true);
				TMP1 = "\\n";
				if (sep_r.isSelected()) TMP1 = "\\r";
				if (sep_nr.isSelected()) TMP1 = "\\r\\n";
				writeConfig("lineSeparator", TMP1);
				break;
			}
		}
	}
	
	class MyMenu extends JMenu
	{
		public MyMenu(String str)
		{
			super(str);
			this.setFont(f13);
			this.setForeground(Color.BLACK);
			menubar.add(new JLabel(" "));
			menubar.add(this);
			if (!isOtherLAF) this.setOpaque(false);
			else this.setBackground(Color.WHITE);
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
				this.setIcon(new ImageIcon(getClass().getResource("/myjava/SRC/" + icon + ".PNG")));
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
		
		public int getValue()
		{
			return this.prog.getValue();
		}
		
		public void setString(String s)
		{
			this.prog.setString(s);
		}
	}
	
	protected void save(File f) throws IOException
	{
		TMP1 = getConfig("Encoding");
		if (TMP1 == null) TMP1 = "default";
		TMP2 = getConfig("lineSeparator");
		if (TMP2 == null) TMP2 = "\n";
		TMP2 = TMP2.replace("\\n", "\n").replace("\\r", "\r");
		if (TMP1.equals("default"))
		{
			PrintWriter out = new PrintWriter(f);
			String[] strs = TEXTAREA.getText().split("\n");
			for (String str: strs)
			{
				out.print(str + TMP2);
			}
			out.close();
		}
		else
		{
			byte[] bytes = TEXTAREA.getText().replace("\n", TMP2).getBytes(TMP1);
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
					prog.dispose();
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
	
	protected void setFileLabel(File f)
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
	
	protected JSeparator separator()
	{
		JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
		sep.setPreferredSize(new Dimension(2,85));
		return sep;
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
	
	protected static String getConfig(String name)
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
	
	protected static void writeConfig(String key, String value)
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
	
	class MyTextField extends JTextField implements MouseListener
	{
		private int x;
		private final Color gray = new Color(250,250,250);
		public MyTextField(int size, int x)
		{
			super(size);
			this.x = x;
			this.setFont(f13);
			this.setForeground(Color.BLACK);
			this.setBackground(gray);
			this.setBorder(bord1);
			this.addMouseListener(this);
		}
		
		public int getIndex()
		{
			return this.x;
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
			this.setBackground(Color.WHITE);
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
		{
			this.setBackground(gray);
		}
		
		@Override
		public void mouseClicked(MouseEvent ev)
		{
		}
		
		@Override
		public void mousePressed(MouseEvent ev)
		{
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
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
	
	class MyButton extends JButton implements MouseListener
	{
		public MyButton(String str)
		{
			super(str);
			this.setFont(f13);
			this.setPreferredSize(new Dimension(50,28));
			this.setBorder(bord1);
			this.setFocusable(false);
			this.addMouseListener(this);
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
			this.setBackground(new Color(254,254,254));
			this.setBorder(bord2);
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
		{
			this.setBackground(Color.WHITE);
			this.setBorder(bord1);
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
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
	
	class MyRibbonTab extends JPanel
	{
		public MyRibbonTab(String name)
		{
			super();
			this.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
			this.setBackground(Color.WHITE);
			this.setPreferredSize(new Dimension(0,85));
			ribbon.addTab(name, this);
		}
	}
	
	class MyRibbonButton extends JPanel implements MouseListener
	{
		private JLabel label;
		private String icon;
		private boolean isHorizontal;
		private int x;
		MyRibbonButton(String text, String icon, String tooltip, boolean isHorizontal, int x)
		{
			this(text,icon,isHorizontal,x);
			this.setToolTipText(tooltip);
		}
		
		MyRibbonButton(String text, String icon, boolean isHorizontal, int x)
		{
			super();
			label = new MyLabel(text);
			if (isHorizontal)
			{
				this.setLayout(new FlowLayout(FlowLayout.LEFT));
				this.add(new MyLabel("    "));
				this.add(label);
				this.label.setFont(new Font("Microsoft Jhenghei", Font.PLAIN, 12));
				this.setSize(this.getPreferredSize()); // pack()
				this.setPreferredSize(new Dimension(this.getWidth(),25));
			}
			else
			{
				this.setLayout(new BorderLayout());
				MyPanel panel = new MyPanel(MyPanel.CENTER);
				panel.add(label);
				panel.setOpaque(false);
				this.add(panel, BorderLayout.SOUTH);
				this.setSize(this.getPreferredSize());
				this.setPreferredSize(new Dimension(this.getWidth()+8,75));
			}
			this.isHorizontal = isHorizontal;
			this.icon = icon;
			this.setBorder(new LineBorder(Color.BLACK,0));
			this.setBackground(Color.WHITE);
			this.addMouseListener(this);
			this.addMouseListener(new MyListener(x));
		}
		
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if (this.icon != null)
			{
				ImageIcon image = null;
				try
				{
					image = new ImageIcon(getClass().getResource("/myjava/SRC/" + this.icon + ".PNG"));
					if (this.isHorizontal)
					{
						g.drawImage(image.getImage(),2,(this.getHeight()-image.getIconHeight())/2,null);
					}
					else
					{
						g.drawImage(image.getImage(),(this.getWidth()-image.getIconWidth())/2,1,null);
					}
				}
				catch (Exception ex)
				{
				}
			}
		}
		
		@Override
		public void mousePressed(MouseEvent ev)
		{
			this.setBackground(new Color(230,230,230));
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
			this.setBackground(new Color(240,240,240));
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
		{
			this.setBackground(Color.WHITE);
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
			this.setBackground(Color.WHITE);
		}
		
		@Override
		public void mouseClicked(MouseEvent ev)
		{
		}
	}
	
	class MyRibbonFirst extends JButton implements MouseListener
	{
		private JPanel panel = new JPanel();
		public MyRibbonFirst()
		{
			super("FILE");
			this.setPreferredSize(new Dimension(65,25));
			this.setBackground(new Color(154,192,229));
			this.setForeground(Color.WHITE);
			this.setFont(f13);
			this.setBorder(bord1);
			this.setFocusPainted(false);
			this.addMouseListener(this);
			panel.setBackground(new Color(111,107,100));
			panel.setLayout(new FlowLayout());
			panel.setPreferredSize(new Dimension(165,0));
		}
		
		public JPanel getPanel()
		{
			return this.panel;
		}
		
		class MyItemButton extends MyPureButton
		{
			public MyItemButton(String item, int x)
			{
				super("  " + item);
				this.setBackground(new Color(111,107,100));
				this.setLabelForeground(Color.WHITE);
				this.setPreferredSize(new Dimension(165,33));
				this.setBorder(new LineBorder(Color.BLACK,0));
				this.setLabelFont(new Font("Microsoft Jhenghei", Font.PLAIN, 14));
				this.setAlignLeft();
				if (x != 0)
				{
					this.addMouseListener(new MyListener(x));
				}
				this.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent ev)
					{
						((Component)(ev.getSource())).setBackground(new Color(111,107,100));
						w.remove(panel);
						try
						{
							TMP1 = getConfig("isUseNarrowEdge");
							if (TMP1 == null) throw new Exception();
						}
						catch (Exception ex)
						{
							TMP1 = "false";
						}
						if (TMP1.equals("true"))
						{
							w.add(leftEdge, BorderLayout.LINE_START);
						}
						else
						{
							w.add(leftEdgeOld, BorderLayout.LINE_START);
						}
						w.revalidate();
						w.repaint();
					}
					
					@Override
					public void mouseEntered(MouseEvent ev)
					{
						((MyItemButton)(ev.getSource())).setBackground(new Color(50,50,50));
					}
					
					@Override
					public void mouseExited(MouseEvent ev)
					{
						((MyItemButton)(ev.getSource())).setBackground(new Color(111,107,100));
					}
				});
			}
		}
		
		public void add(String item, int x)
		{
			this.panel.add(new MyItemButton(item, x));
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
			if (ev.getSource() instanceof MyRibbonFirst)
			{
				boolean contain = false;
				if (Arrays.asList(w.getContentPane().getComponents()).contains(this.panel))
				{
					w.remove(this.panel);
					try
					{
						TMP1 = getConfig("isUseNarrowEdge");
						if (TMP1 == null) throw new Exception();
					}
					catch (Exception ex)
					{
						TMP1 = "false";
					}
					if (TMP1.equals("true"))
					{
						w.add(leftEdge, BorderLayout.LINE_START);
					}
					else
					{
						w.add(leftEdgeOld, BorderLayout.LINE_START);
					}
				}
				else
				{
					w.add(this.panel, BorderLayout.LINE_START);
				}
				w.revalidate();
				w.repaint();
			}
		}
		
		@Override
		public void mousePressed(MouseEvent ev)
		{
		}
		
		@Override
		public void mouseClicked(MouseEvent ev)
		{
		}
		
		@Override
		public void mouseExited(MouseEvent ev)
		{
			this.setBackground(new Color(154,192,229));
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
			this.setBackground(new Color(183,206,228));
		}
	}
	
	class UndoManager
	{
		private ArrayList<String> backup = new ArrayList<String>();
		private int position = 0;
		public UndoManager()
		{
		}
		
		public void backup(String str) 
		//larger index: older
		//smaller index: newer
		{
			backup.add(0, str);
		}
		
		public String undo()
		{
			if (position<backup.size()-1)
			{
				position++;
				return backup.get(position);
			}
			else
			{
				JOptionPane.showMessageDialog(w, "Reached undo limit!", "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		
		public String redo()
		{
			if (position>0)
			{
				position--;
				return backup.get(position);
			}
			else
			{
				JOptionPane.showMessageDialog(w, "Reached redo limit!", "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
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
