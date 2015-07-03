import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.geom.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.nio.file.*;
import java.nio.charset.*;
import myjava.gui.*;
import myjava.gui.common.*;
import myjava.util.*;

public class RefluxEdit extends JFrame implements Resources
{
	// constants
	private static final String VERSION_NO = "4.0";
	private static final String BETA_STRING = "";
	private static final String BETA_NO = "";
	private static final String REV_STRING = "";
	private static final String REV_NO = "";
	private static final File settingsFile = new File(getsettingsFilePath(), "REFLUXEDITPREF.PROPERTIES");
	private static final Properties prop = new Properties();
	private static final Color gray = new Color(238,238,238);
	private static final Color vlgray = new Color(250,250,250);
	private static final Color lightGreen = new Color(243,255,241);
	private static final Color lightYellow = new Color(252,247,221);
	private static final Color brown = new Color(111,107,100);
	private static Color transYellow;
	private static final DecimalFormat format3 = new DecimalFormat("##0.###");
	private static final Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	// boolean settings
	private static boolean isSaved = true; //control exit reminder
	private static boolean countWords = true; //control whether DocumentListener is on
	private static boolean autoIndent = false; //control whether auto indent is on
	private static boolean paintTextArea = true; //control whether textarea is painted with umbrella
	// components
	private JTextArea textArea = new JTextArea();
	private JLayer<JTextArea> layer1 = new JLayer<>(textArea, new MyUmbrellaLayerUI());
	private JScrollPane scrollPane = new JScrollPane(layer1);
	private DropTarget defaultDropTarget = textArea.getDropTarget();
	private TextAreaListener taListener = new TextAreaListener();
	private MyPanel bottomP1 = new MyPanel(MyPanel.CENTER);
	private MyPanel bottomP2 = new MyPanel(MyPanel.CENTER);
	private FlaggedLabel countLabel = new FlaggedLabel(0,true);
	private MyLabel currentFile = new MyLabel(" ");
	private MyLabel leftEdge = new MyLabel(" ");
	private MyLabel rightEdge = new MyLabel(" ");
	private JLabel leftEdgeOld = new JLabel("  ");
	private JLabel rightEdgeOld = new JLabel("  ");
	private static JMenuBar menubar;
	// ribbon
	private MyRibbonPanel ribbon = new MyRibbonPanel();
	private static boolean isOtherLAF = true;
	private static boolean isRibbon = true;
	// file choosers	
	private static MyFileChooser JavaChooser;
	private static FileDialog systemChooser;
	private static FilenameFilter systemTextFilter = new FilenameFilter()
	{
		@Override
		public boolean accept(File dir, String name)
		{
			dir = new File(dir,name);
			String file = name.toLowerCase();
			return (dir.isDirectory())||(file.endsWith("txt"))||(file.endsWith("java"))||(file.endsWith("py"))||(file.endsWith("php"))||(file.endsWith("html"))||(file.endsWith("htm"))||(file.endsWith("xml"))||(file.endsWith("bot"))||(file.endsWith("properties"));
		}
	};
	private static final FileNameExtensionFilter textFilter = new FileNameExtensionFilter("Text file", new String[]{"txt", "java", "py", "php", "html", "htm", "xml", "bot", "properties"});
	private File file = null;
	//
	private static final JPopupMenu popup = new JPopupMenu();
	private static final Clipboard clipbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
	// undo
	private UndoManager undoManager = new UndoManager();
	private JDialog undoDialog = new JDialog(w,"Undo and Redo record",false);
	private JList<String> undoJList = createUndoRecordList();
	private JList<String> redoJList = createUndoRecordList();
	// compile
	private JDialog compileDialog;
	// main window
	private static RefluxEdit w;
	private JComponent topPanel = null;
	private JPanel fourButtonPanel = new JPanel();
	private JToolBar toolBar = new JToolBar("ToolBar");
	private JPanel bottomPanel = new JPanel(new BorderLayout());
	// toolBar buttons
	MyToolBarButton toolBarNew = new MyToolBarButton("NEW32", "New file", 1);
	MyToolBarButton toolBarOpen = new MyToolBarButton("OPEN32", "Open file", 2);
	MyToolBarButton toolBarSave = new MyToolBarButton("SAVE32", "Save as", 4);
	MyToolBarButton toolBarPrint = new MyToolBarButton("PRINT32", "Print", 38);
	MyToolBarButton toolBarUndo = new MyToolBarButton("UNDO32", "Undo", 7);
	MyToolBarButton toolBarRedo = new MyToolBarButton("REDO32", "Redo", 8);
	MyToolBarButton toolBarCut = new MyToolBarButton("CUT32", "Cut selection", 11);
	MyToolBarButton toolBarCopy = new MyToolBarButton("COPY32", "Copy selection", 12);
	MyToolBarButton toolBarPaste = new MyToolBarButton("PASTE32", "Paste", 13);
	MyToolBarButton toolBarDelete = new MyToolBarButton("DELETE32", "Delete selection", 15);
	MyToolBarButton toolBarSelectAll = new MyToolBarButton("SELECT32", "Select all", 9);
	MyToolBarButton toolBarSelectAllCopy = new MyToolBarButton("SELECT32", "Select all and copy", 10);
	MyToolBarButton toolBarSearch = new MyToolBarButton("SEARCH32", "Search", 23);
	MyToolBarButton toolBarReplace = new MyToolBarButton("REPLACE32", "Replace", 24);
	MyToolBarButton toolBarOptions = new MyToolBarButton("OPTIONS32", "Toolbar options", 0);
	// temporary variables
	private static int i, j, k, l, m;
	private static String TMP1, TMP2, TMP3, TMP4;
	//
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
				RefluxEdit.initialize();
				RefluxEdit.setLAF();
				w = new RefluxEdit("RefluxEdit " + VERSION_NO);
				w.restoreFrame();
				w.restoreTextArea();
				w.restoreMenus();
				w.restorePopup();
				Thread t1 = new Thread()
				{
					@Override
					public void run()
					{
						w.restoreUndoDialog();
						w.restoreChoosers();
					}
				};
				t1.start();
				try
				{
					t1.join();
					showJFrame();
				}
				catch (InterruptedException ex)
				{
					exception(ex);
				}
			}
			//
			void showJFrame()
			{
				splash.close();
				w.setVisible(true);
				writeConfig("LastStartupTimeTaken", System.currentTimeMillis()-initialTime + "ms");
				if (getBoolean0("CheckUpdate"))
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							checkUpdate(false);
						}
							
					}).start();
				}
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
			}
		});
	}
	
	protected static void initialize()
	{
		// Swing defaults
		UIManager.put("OptionPane.messageFont", f13);
		UIManager.put("OptionPane.buttonFont", f13);
		UIManager.put("OptionPane.okButtonText", "OK");
		UIManager.put("OptionPane.yesButtonText", "YES");
		UIManager.put("OptionPane.noButtonText", "NO");
		UIManager.put("ComboBox.background", Color.WHITE);
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
		//size
		if (!settingsFile.exists())
		{
			try
			{
				PrintWriter writer = new PrintWriter(settingsFile, "UTF-8");
				writer.close();
				setConfig("Size.x", "690");
				setConfig("Size.y", "550");
				setConfig("Location.x", "0");
				setConfig("Location.y", "0");
				setConfig("isEditable", "true");
				setConfig("LineWrap", "true");
				setConfig("WrapStyleWord", "true");
				setConfig("Encoding", "default1");
				setConfig("ChooserStyle", "Java");
				setConfig("OnTop", "false");
				setConfig("TabSize", "4");
				setConfig("SelectionColor.r", "244");
				setConfig("SelectionColor.g", "223");
				setConfig("SelectionColor.b", "255");
				setConfig("LAF", "Default");
				setConfig("isPanel", "true");
				setConfig("ToolBar.new", "true");
				setConfig("ToolBar.open", "true");
				setConfig("ToolBar.save", "true");
				setConfig("ToolBar.print", "true");
				setConfig("ToolBar.undo", "true");
				setConfig("ToolBar.undo", "true");
				setConfig("ToolBar.cut", "true");
				setConfig("ToolBar.copy", "true");
				setConfig("ToolBar.paste", "true");
				setConfig("ToolBar.selectAll", "true");
				setConfig("ToolBar.selectAllAndCopy", "false");
				setConfig("ToolBar.delete", "true");
				setConfig("ToolBar.search", "true");
				setConfig("ToolBar.replace", "true");
				setConfig("TextAreaFont.fontName", "Microsoft Jhenghei");
				setConfig("TextAreaFont.fontStyle", "0");
				setConfig("TextAreaFont.fontSize", "15");
				setConfig("isUseNewMenuBar", "true");
				setConfig("isUseNarrowEdge", "true");
				setConfig("lineSeparator", "\\n");
				setConfig("isRibbon", "true");
				setConfig("showCount", "false");
				setConfig("autoIndent", "true");
				setConfig("showUmbrella", "true");
				setConfig("Umbrella.alpha", "60");
				setConfig("Compile.command", "javac %f");
				setConfig("Compile.runCommand", "java -classpath %p %s%nPAUSE");
				setConfig("Compile.runCommandFileName","CMD.BAT");
				setConfig("Compile.removeOriginal", "false");
				setConfig("Compile.regex", ".*\\.class");
				setConfig("Compile.useGlobal", "true");
				setConfig("Caret.save", "true");
				setConfig("CheckUpdate", "true");
				saveConfig();
			}
			catch (IOException ex)
			{
			}
		}
		else
		{
			loadConfig();
		}
		//load static fields
		//(static) transyellow
		try
		{
			transYellow = new Color(251,231,51,Short.parseShort(getConfig0("Umbrella.alpha")));
		}
		catch (Exception ex)
		{
			transYellow = new Color(251,231,51,60);
		}
	}
	
	protected static void setLAF()
	{
		try
		{
			switch (getConfig0("LAF"))
			{
				case "System":
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				menubar = new JMenuBar();
				menubar.setBackground(Color.WHITE);
				break;
							
				case "Nimbus":
				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
				menubar = new JMenuBar();
				UIManager.put("nimbusInfoBlue", new Color(255,186,0));
				UIManager.put("OptionPane.sameSizeButtons", true);
				break;
				
				default:				
				menubar = new ColoredMenuBar(getBoolean0("isUseNewMenuBar"));
				isOtherLAF = false;
				menubar.setBorderPainted(false);
				UIManager.put("Button.background", Color.WHITE);
				break;
			}
		}
		catch (Throwable ex)
		{
			menubar = new JMenuBar();
			menubar.setBackground(new Color(242,254,255));
		}
	}
	
	public RefluxEdit(String title)
	{
		super(title);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLayout(new BorderLayout());
		//
		bottomP1.setPreferredSize(new Dimension(160,0));
		bottomP1.add(countLabel);
		bottomP1.setBackground(gray);
		bottomP1.special = 1;
		bottomP1.repaint();
		bottomP2.setBackground(gray);
		if (getBoolean0("showCount"))
		{
			bottomPanel.add(bottomP1, BorderLayout.LINE_START);			
			bottomP2.special = 2;
			bottomP2.repaint();
		}
		else
		{
			bottomP2.setOpaque(false);
		}
		bottomP2.add(currentFile);
		bottomPanel.add(bottomP2, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.PAGE_END);
		// narrow edge
		if (getBoolean0("isUseNarrowEdge"))
		{
			this.add(leftEdge, BorderLayout.LINE_START);
			this.add(rightEdge, BorderLayout.LINE_END);
		}
		else
		{
			this.add(leftEdgeOld, BorderLayout.LINE_START);
			this.add(rightEdgeOld, BorderLayout.LINE_END);
		}
		this.add(scrollPane, BorderLayout.CENTER);
		this.addTopPanel();
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent ev)
			{
				int x;
				if (textArea.getText().isEmpty()) isSaved = true;
				if (isSaved)
				{
					x = JOptionPane.showConfirmDialog(RefluxEdit.this, "Do you really want to close RefluxEdit?", "Confirm close", JOptionPane.YES_NO_OPTION);
				}
				else
				{
					String[] options = new String[]{"<html><center>Close<br>RefluxEdit</center></html>","Cancel","<html><center>Save and Close<br>(use system encoding)</center></html>"};
					x = JOptionPane.showOptionDialog(RefluxEdit.this, "NOT YET SAVED!\nDo you really want to close RefluxEdit?", "Confirm close", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				}
				if (x == JOptionPane.YES_OPTION)
				{
					finalSaveSettings();
				}
				else if ((x == JOptionPane.CANCEL_OPTION)&&(!isSaved))
				{
					//save and close
					try
					{
						if (file != null)
						{
							save(file,false);
							finalSaveSettings();
						}
						else
						{
							File f = getSaveToFile();
							if (f != null)
							{
								save(f,false);
								finalSaveSettings();
							}
						}
					}
					catch (IOException ex)
					{
						exception(ex);
					}
				}
			}
			
			void finalSaveSettings()
			{
				Dimension d = RefluxEdit.this.getSize();
				Point l = RefluxEdit.this.getLocation();
				loadConfig();
				setConfig("Size.x", d.width + "");
				setConfig("Size.y", d.height + "");
				setConfig("Location.x", l.x + "");
				setConfig("Location.y", l.y + "");
				setConfig("isMaxmized", String.valueOf(RefluxEdit.this.getExtendedState() == JFrame.MAXIMIZED_BOTH));
				//save caret position
				setCaret(file,textArea.getCaretPosition());
				saveConfig();
				System.exit(0);
			}
						
			@Override
			public void windowDeactivated(WindowEvent ev)
			{
				textArea.setDropTarget(taListener);
			}
		});
	}
	
	static void checkUpdate(boolean showUpToDate)
	{
		BufferedReader reader = null;
		try
		{
			//load information
			URL update = new URL("http://refluxedit.sourceforge.net/update/newversion.txt");
			reader = new BufferedReader(new InputStreamReader(update.openStream()));
			StringBuilder builder = new StringBuilder();
			String buffer;
			while ((buffer=reader.readLine()) != null)
			{
				builder.append(buffer+"\n");
			}
			final String[] fragment = builder.toString().split("\n");
			//compare
			double newVersionNumber = getVersionNumber(fragment);
			double currentVersionNumber = getVersionNumber(new String[]{VERSION_NO, BETA_STRING, BETA_NO, REV_NO});
			if (newVersionNumber > currentVersionNumber)
			{
				StringBuilder des = new StringBuilder();
				for (int i=5; i<fragment.length; i++)
				{
					des.append(fragment[i]+"\n");
				}
				final String description = des.toString();
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						int option = JOptionPane.showConfirmDialog(w, "There is a new version of RefluxEdit."
														+ "\nVersion: " + fragment[0] + (fragment[1].equals("final")?"":(fragment[1] + fragment[2]
														+ (Byte.parseByte(fragment[3])==0?"":("rev"+fragment[3]))))
														+ "\nPublished: " + fragment[4]
														+ "\n\n" + description
														+ "\nWould you like to download it?", "Update found", JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION)
						{
							try
							{
								Desktop.getDesktop().browse(new URI("https://sourceforge.net/projects/refluxedit/"));
							}
							catch (Exception ex)
							{
							}
						}
					}
				});
			}
			else if (showUpToDate)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						JOptionPane.showMessageDialog(w,"Your RefluxEdit is up to date.", "Check update", JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
		}
		catch (Exception ex)
		{
			exception(ex);
		}
		finally
		{
			try
			{
				if (reader != null) reader.close();
			}
			catch (IOException ex)
			{
			}
		}
	}
						
	static double getVersionNumber(String[] fragment)
	{
		double version_no = Double.parseDouble(fragment[0]);
		version_no+=getTestNumber(fragment[1])/10.0;
		version_no+=(fragment[2].isEmpty()?0:Double.parseDouble(fragment[2]))/100;
		version_no+=(fragment[3].isEmpty()?0:Double.parseDouble(fragment[3]))/1000;
		return version_no;
	}
	
	static byte getTestNumber(String str)
	{
		byte beta_version;
		switch (str)
		{
			case "alpha":
			beta_version=0;
			break;
			
			case "beta":
			beta_version=1;
			break;
			
			case "final":
			case "":
			default:
			beta_version=2;
			break;
		}
		return beta_version;
	}

	protected void addTopPanel()
	{
		TMP1 = getConfig0("isPanel");
		// setup "four button panel"
		fourButtonPanel.add(new MyShortcutButton("New", -1));
		fourButtonPanel.add(new MyShortcutButton("Open", 2));
		fourButtonPanel.add(new MyShortcutButton("Save as", 4));
		fourButtonPanel.add(new MyShortcutButton("Save", 5));
		// setup toolbar
		// section 1
		boolean b1 = getBoolean0("ToolBar.new");
		boolean b2 = b1;
		if (b1) toolBar.add(toolBarNew);
		b1 = getBoolean0("ToolBar.open");
		b2 = b2||b1;
		if (b1) toolBar.add(toolBarOpen);
		b1 = getBoolean0("ToolBar.save");
		b2 = b2||b1;
		if (b1) toolBar.add(toolBarSave);
		b1 = getBoolean0("ToolBar.print");
		b2 = b2||b1;
		if (b1) toolBar.add(toolBarPrint);
		if (b2) ((JToolBar)toolBar).addSeparator();
		// section 2
		b1 = getBoolean0("ToolBar.undo");
		b2 = b1;
		if (b1) toolBar.add(toolBarUndo);
		b1 = getBoolean0("ToolBar.redo");
		b2 = b1;
		if (b1) toolBar.add(toolBarRedo);
		b1 = getBoolean0("ToolBar.cut");
		b2 = b1;
		if (b1) toolBar.add(toolBarCut);
		b1 = getBoolean0("ToolBar.copy");
		b2 = b1||b2;
		if (b1) toolBar.add(toolBarCopy);
		b1 = getBoolean0("ToolBar.paste");
		b2 = b1||b2;
		if (b1) toolBar.add(toolBarPaste);
		b1 = getBoolean0("ToolBar.selectAll");
		b2 = b1||b2;
		if (b1) toolBar.add(toolBarSelectAll);
		b1 = getBoolean0("ToolBar.selectAllAndCopy");
		b2 = b1||b2;
		if (b1) toolBar.add(toolBarSelectAllCopy);
		b1 = getBoolean0("ToolBar.delete");
		b2 = b1||b2;
		if (b1) toolBar.add(toolBarDelete);
		if (b2) ((JToolBar)toolBar).addSeparator();
		// section 3
		if (getBoolean0("ToolBar.search")) toolBar.add(toolBarSearch);
		if (getBoolean0("ToolBar.replace")) toolBar.add(toolBarReplace);
		toolBar.add(toolBarOptions);
		switch (TMP1)
		{
			case "no": //no container
			default:
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
	
	static class ColoredMenuBar extends JMenuBar
	{
		private boolean isColored;
		ColoredMenuBar(boolean isColored)
		{
			super();
			this.isColored = isColored;
		}
		
		void setColored(boolean isColored)
		{
			this.isColored = isColored;
		}
		
		@Override
		protected void paintComponent(Graphics g)
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
	
	class FlaggedLabel extends MyLabel implements MouseListener
	{
		boolean isWordCount; //wordCount or charCount
		FlaggedLabel(int number, boolean isWordCount)
		{
			super();
			this.setCountInfo(number, isWordCount);
			this.isWordCount = isWordCount;
			this.addMouseListener(this);
		}
		
		void setCountInfo(int number, boolean isWordCount)
		{
			String text = "";
			if (isWordCount)
			{
				if (number > 1) text = "About " + number + " words";
				else text = "About " + number + " word";
			}
			else
			{
				if (number > 1) text = "About " + number + " characters";
				else text = "About " + number + " character";
			}
			this.setText(text);
			this.setToolTipText(text);
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
			if (isWordCount)
			{
				isWordCount = false;
				//now set to charCount
				this.setCountInfo(textArea.getText().length(),false);
			}
			else
			{
				isWordCount = true;
				//now set to wordCount
				this.setCountInfo(wordCount(textArea.getText()),true);
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent ev) {}
		
		@Override
		public void mouseExited(MouseEvent ev) {}
		
		@Override
		public void mousePressed(MouseEvent ev) {}
		
		@Override
		public void mouseClicked(MouseEvent ev) {}
	}
	
	public void restoreFrame()
	{
		this.setMinimumSize(new Dimension(275,250));
		try
		{
			i = (int)Double.parseDouble(getConfig0("Size.x"));
			j = (int)Double.parseDouble(getConfig0("Size.y"));
		}
		catch (Exception ex)
		{
			i = 690;
			j = 550;
		}
		finally
		{
			i = Math.min(Math.max(275,i),scrSize.width);
			j = Math.min(Math.max(250,j),scrSize.height);
			this.setSize(i,j);
		}
		//location
		try
		{
			k = (int)Double.parseDouble(getConfig0("Location.x"));
			l = (int)Double.parseDouble(getConfig0("Location.y"));
		}
		catch (Exception ex)
		{
			k = 0;
			l = 0;
		}	
		finally
		{
			k = Math.max(0,Math.min(k,scrSize.width));
			l = Math.max(0,Math.min(l,scrSize.height));
			this.setLocation(k,l);
		}
		//maximized
		if (getBoolean0("isMaxmized"))
		{
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		//ontop
		this.setAlwaysOnTop(getBoolean0("OnTop"));
		//icon
		try
		{
			this.setIconImage(icon("APPICON").getImage());
		}
		catch (Exception ex)
		{
		}
	}
	
	protected void restoreChoosers()
	{
		JComponent.setDefaultLocale(Locale.ENGLISH);
		JavaChooser = new MyFileChooser("Choose:");
		systemChooser = new FileDialog(this,"Choose:");
		systemChooser.setDirectory("./");
		systemChooser.setMultipleMode(false);
	}
	
	protected void restoreMenus()
	{		
		isRibbon = getBoolean0("isRibbon");
		if (isRibbon)
		{
			this.add(ribbon, BorderLayout.PAGE_START);
			MyRibbonFirst button = new MyRibbonFirst();
			button.add("New File", 1);
			button.add("Open File", -2);
			button.add("", -1);
			button.add("Save As", 4);
			button.add("Save", 5);
			button.add("", -1);
			button.add("Export to Image", 43);
			button.add("Print", 38);
			button.add("Close", 6);
			ribbon.addAsFirstComponent(button);
			//
			MyRibbonTab tab2 = new MyRibbonTab("EDIT");
			MyRibbonTab tab3 = new MyRibbonTab("VIEW");
			MyRibbonTab tab4 = new MyRibbonTab("TOOLS");
			MyRibbonTab tab5 = new MyRibbonTab("INSERT");
			MyRibbonTab tab6 = new MyRibbonTab("HELP");
			// Edit
			tab2.add(new MyRibbonButton("Undo", "UNDO32", "<html><font size=\"4\"><b>Undo&nbsp;&nbsp;&nbsp;Ctrl+Z</b></font><br>Undo the last amendment.</font></html>", false, 7));
			tab2.add(new MyRibbonButton("Redo", "REDO32", "<html><font size=\"4\"><b>Redo&nbsp;&nbsp;&nbsp;Ctrl+Y</b></font><br>Redo the undo amendment.</html>", false, 8));
			JPanel tab2_1 = new JPanel(new GridLayout(2,1,10,10));
			tab2_1.setOpaque(false);
			tab2_1.add(new MyRibbonButton("Select all", "SELECT", "<html><font size=\"4\"><b>Select all&nbsp;&nbsp;&nbsp;Ctrl+A</b></font><br>Select all text in the text area.</html>", true, 9));
			tab2_1.add(new MyRibbonButton("Select all and copy", "SELECT", "<html><font size=\"4\"><b>Select all and copy</b></font><br>Select all text in the text area and<br>copy to the system clipboard.</html>", true, 10));
			tab2.add(tab2_1);
			tab2.add(createSeparator());
			tab2.add(new MyRibbonButton("Cut", "CUT32", "<html><font size=\"4\"><b>Cut selected text&nbsp;&nbsp;&nbsp;Ctrl+X</b></font><br>The selected text will be moved<br>to the system clipboard.</html>", false, 11));
			tab2.add(new MyRibbonButton("Copy", "COPY32", "<html><font size=\"4\"><b>Copy selected text&nbsp;&nbsp;&nbsp;Ctrl+C</b></font><br>The selected text will be copied<br>to the system clipboard.</html>", false, 12));
			JPanel tab2_2 = new JPanel(new GridLayout(2,1,10,10));
			tab2_2.setOpaque(false);
			tab2_2.add(new MyRibbonButton("Paste", "PASTE", "<html><font size=\"4\"><b>Paste text&nbsp;&nbsp;&nbsp;Ctrl+V</b></font><br>Paste the text in the system clipboard<br>to the text area.</html>", true, 13));
			tab2_2.add(new MyRibbonButton("Paste on next line", "PASTE", "<html><font size=\"4\"><b>Paste text on next line</b></font><br>Insert the text in the system clipboard<br>on the next line.</html>", true, 14));
			tab2.add(tab2_2);
			tab2.add(createSeparator());
			tab2.add(new MyRibbonButton("Delete", "DELETE32", "<html><font size=\"4\"><b>Delete selected text&nbsp;&nbsp;&nbsp;Delete</b></font><br>The selected text will be deleted.</html>", false, 15));
			tab2.add(createSeparator());
			JPanel tab2_3 = new JPanel(new GridLayout(2,1,10,10));
			tab2_3.setOpaque(false);
			tab2_3.add(new MyRibbonButton("Indent\u2191", "INDENT+", "<html><font size=\"4\"><b>Increase indentation&nbsp;&nbsp;&nbsp;Ctrl+I</b></font><br>Increase the indentation of the selected text by 1</html>", true, 18));
			tab2_3.add(new MyRibbonButton("Indent\u2193", "INDENT-", "<html><font size=\"4\"><b>Decrease indentation&nbsp;&nbsp;&nbsp;Ctrl+U</b></font><br>Decrease the indentation of the selected text by 1</html>", true, 19));
			tab2.add(tab2_3);
			// View
			tab3.add(new MyRibbonButton("<html>Editing/<br>viewing</html>", "EDIT32", "<html><font size=\"4\"><b>Enable/disable editing</b></font><br>Click here to disable/re-enable editing.<br></html>", false, 17));
			tab3.add(new MyRibbonButton("<html>On top</html>", "ONTOP", "<html><font size=\"4\"><b>Enable/disable always on top</b></font><br>Click here to enable/disable RefluxEdit always staying on top.</html>", false, 21));
			tab3.add(new MyRibbonButton("<html><center><font color=\"green\">Undo</font><br>dialog</center></htnml>", "UNDODIALOG32", "<html><font size=\"4\"><b>Undo record dialog</b></font><br>Show the undo record dialog.</html>", false, 52));
			MyPanel panel1 = new MyPanel(MyPanel.CENTER);
			panel1.setPreferredSize(new Dimension(1,85));
			tab3.add(panel1);
			// Tools
			tab4.add(new MyRibbonButton("<html><center>Compile<br>code</center></html>", "COMPILE32", "<html><font size=\"4\"><b>Compile code</b></font><br>Compile the code.</html>", false, 53));
			tab4.add(createSeparator());
			JPanel tab4_1 = new JPanel(new GridLayout(3,1,0,0));
			tab4_1.setOpaque(false);
			tab4_1.add(new MyRibbonButton("Word count", "WORDCOUNT", "<html><font size=\"4\"><b>Word count&nbsp;&nbsp;&nbsp;Ctrl+F2</b></font><br>Count how many words are in the selected text,<br>or all words if no text is selected.</html>", true, 22));
			tab4_1.add(new MyRibbonButton("Character count", "CHARACTERCOUNT", "<html><font size=\"4\"><b>Character count</b></font><br>Count how many characters are in the selected text,<br>or all characters if no text is selected.</html>", true, 44));
			tab4_1.add(new MyRibbonButton("Invert characters", null, "<html><font size=\"4\"><b>Invert characters</b></font><br>Invert all characters!</html>", true, 50));			
			tab4.add(tab4_1);
			tab4.add(createSeparator());
			tab4.add(new MyRibbonButton("<html><center><font color=\"red\">Delete</font><br>blank<br>lines</center></html>", null, "<html><font size=\"4\"><b>Delete blank lines</b></font><br>ALL blank lines will be deleted.</html>", false, 35));
			JPanel tab4_2 = new JPanel(new GridLayout(3,1,0,0));
			tab4_2.setOpaque(false);
			tab4_2.add(new MyRibbonButton("Uppercase", "UPPERCASE", "<html><font size=\"4\"><b>Convert to uppercase</b></font><br>Convert the selected text to uppercase,<br>or all characters if no text is selected.</html>", true, 26));
			tab4_2.add(new MyRibbonButton("Lowercase", "LOWERCASE", "<html><font size=\"4\"><b>Convert to lowercase</b></font><br>Convert the selected text to lowercase,<br>or all characters if no text is selected.</html>", true, 27));
			tab4_2.add(new MyRibbonButton("Invert case", "INVERTCASE", "<html><font size=\"4\"><b>Convert to invert case</b></font><br>Convert the selected text to invert case<br>(uppercase to lowercase, and lowercase to uppercase),<br>or all characters if no text is selected.</html>", true, 28));
			tab4.add(tab4_2);
			tab4.add(createSeparator());
			JPanel tab4_3 = new JPanel(new GridLayout(3,1,0,0));
			tab4_3.setOpaque(false);
			tab4_3.add(new MyRibbonButton("Search", "SEARCH", "<html><font size=\"4\"><b>Search words&nbsp;&nbsp;&nbsp;Ctrl+F</b></font><br>Search words in the whole text.</html>", true, 23));
			tab4_3.add(new MyRibbonButton("Replace", "REPLACE", "<html><font size=\"4\"><b>Replace words</b></font><br>Replace words in the whole text.</html>", true, 24));
			tab4_3.add(new MyRibbonButton("Replace (selected)", "REPLACE", "<html><font size=\"4\"><b>Replace words in selected text</b></font><br>Replace words in the SELECTED text.</html>", true, 25));
			tab4.add(tab4_3);
			tab4.add(createSeparator());
			tab4.add(new MyRibbonButton("<html><center>Color<br>chooser</center></html>", "COLORCHOOSER32", "<html><font size=\"4\"><b>Show color chooser</b></font><br>A color chooser will be shown which allows you to choose<br>a color and insert REB, HSV or HEX code.</html>", false, 40));
			tab4.add(new MyRibbonButton("<html><center>Base<br>converter</center></html>", "BASE32", "<html><font size=\"4\"><b>Base converter</b></font><br>Convert numbers between base 2, 8, 10 and 16.</html>", false, 41));
			// Insert
			JPanel tab5_1 = new JPanel(new GridLayout(3,1,0,0));
			tab5_1.setOpaque(false);
			tab5_1.add(new MyRibbonButton("10 \"=\"", null, "<html><font size=\"4\"><b>Insert 10 \"=\"</b></font><br>Insert ten equal signs</html>", true, 30));
			tab5_1.add(new MyRibbonButton("Four \" \"", null, "<html><font size=\"4\"><b>Insert four spaces</b></font><br>Insert four spaces. Useful for programmers.</html>", true, 31));
			tab5_1.add(new MyRibbonButton("Spaces!", null, "<html><font size=\"4\"><b>Spaces!</b></font><br>Insert spaces between characters!</html>", true, 49));
			tab5.add(tab5_1);
			tab5.add(createSeparator());
			tab5.add(new MyRibbonButton("<html><center>Random<br>words</center></html>", "RANDOM", "<html><font size=\"4\"><b>Generate random words</b></font><br>Generate specified number of \"words\" randomly.<br>The words will be between 1 and 10 character(s) long.<br>Note that performing this action may take a long time.</html>", false, 32));
			tab5.add(new MyRibbonButton("<html><center>Java<br>keywords</center></html>", "JAVA32", "<html><font size=\"4\"><b>Insert Java keywords</b></font><br>Insert Java keywords. Useful for Java developers.<br>More will be introduced in later versions.</html>", false, 33));
			tab5.add(new MyRibbonButton("<html><center>HTML<br>keywords</center></html>", "HTML32", "<html><font size=\"4\"><b>Insert HTML keywords</b></font><br>Insert HTML keywords. Useful for web developers.<br>More will be introduced in later versions.</html>", false, 34));
			tab5.add(new MyRibbonButton("<html><center>Unicode<br>character</center></html>", "UNICODE32", "<html><font size=\"4\"><b>Insert unicode character</b></font><br>Insert unicode character by given code value.</html>", false, 45));
			tab5.add(new MyRibbonButton("<html><center>Unicode<br>value</center></html>", "UNICODE32", "<html><font size=\"4\"><b>Insert unicode value</b></font><br>Insert unicode value by given character.</html>", false, 46));
			// About
			MyPanel panel2 = new MyPanel(MyPanel.CENTER);
			panel2.setPreferredSize(new Dimension(5,85));
			tab6.add(panel2);
			JPanel tab6_1 = new JPanel(new GridLayout(2,1,10,10));
			tab6_1.setOpaque(false);
			tab6_1.add(new MyRibbonButton("<html>About RefluxEdit</html>", "APPICON16", "<html><font size=\"4\"><b>About RefluxEdit&nbsp;&nbsp;&nbsp;Ctrl+F1</b></font><br>RefluxEdit is a lightweight plain text editor written in Java by tony200910041.<br>SourceForge page: http://refluxedit.sourceforge.net</html>", true, 16));
			tab6_1.add(new MyRibbonButton("<html>Visit SourceForge Page</html>", "VISIT16", "<html><font size=\"4\"><b>Visit SourceForge homepage</b></font><br>http://refluxedit.sourceforge.net/</html>", true, 48));
			tab6.add(tab6_1);
			tab6.add(createSeparator());
			tab6.add(new MyRibbonButton("<html><center><font color=\"green\">Reduce</font><br>memory<br>usage</center></html>", null, "<html><font size=\"4\"><b>Reduce memory usage</b></font><br>System.gc() will be executed.</html>", false, 42));
			tab6.add(createSeparator());
			tab6.add(new MyRibbonButton("<html>Options</html>", "OPTIONS32", "<html><font size=\"4\"><b>Options</b></font><br>Miscellaneous options</html>", false, 39));
		}   //"<html><font size=\"4\"><b></b></font><br></html>"
		else
		{		
			this.setJMenuBar(menubar);
			MyMenu menu1 = new MyMenu("File");
			MyMenu menu2 = new MyMenu("Edit");
			MyMenu menu3 = new MyMenu("View");
			MyMenu menu4 = new MyMenu("Tools");
			JMenu menu4_1 = new JMenu("Options");
			menu4_1.setFont(f13);
			JMenu menu4_2 = new JMenu("Case conversion");
			menu4_2.setFont(f13);
			MyMenu menu5 = new MyMenu("Insert");
			MyMenu menu6 = new MyMenu("Help");
			//
			menu1.add(new MyMenuItem("New file", "NEW", 1));
			menu1.add(new MyMenuItem("Open file", "OPEN", 2).setAccelerator(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			menu1.add(new MyMenuItem("Open file (quick)", null, 3));
			menu1.add(new MyMenuItem("Open file (charset)",null, 51));
			menu1.add(new JSeparator());
			menu1.add(new MyMenuItem("Save as", "SAVE", 4).setAccelerator(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			menu1.add(new MyMenuItem("Save", null, 5));
			menu1.add(new MyMenuItem("Export to image", null, 43));
			menu1.add(new JSeparator());
			menu1.add(new MyMenuItem("Print", null, 38).setAccelerator(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
			menu1.add(new MyMenuItem("Close", "CLOSE", 6));
			//
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
			menu2.add(new JSeparator());
			menu2.add(new MyMenuItem("Increase indentation", "INDENT+", 18).setAccelerator(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
			menu2.add(new MyMenuItem("Decrease indentation", "INDENT-", 19).setAccelerator(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
			//
			menu3.add(new MyMenuItem("Enable/disable editing", "EDIT16", 17));
			menu3.add(new MyMenuItem("Enable/disable always on top", "ONTOP16", 21));
			menu3.add(new MyMenuItem("Undo record dialog", null, 52));
			//
			menu4.add(new MyMenuItem("Options", "OPTIONS16", 39));
			menu4.add(new JSeparator());
			menu4.add(new MyMenuItem("Compile code", "COMPILE16", 53));
			menu4.add(new MyMenuItem("Word count", null, 22).setAccelerator(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));
			menu4.add(new MyMenuItem("Character count", null, 44));
			menu4.add(new MyMenuItem("Delete blank lines", null, 35));
			menu4.add(new MyMenuItem("Invert characters", null, 50));
			menu4.add(menu4_2);
			menu4.add(new JSeparator());
			menu4.add(new MyMenuItem("Search", "SEARCH", 23).setAccelerator(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
			menu4.add(new MyMenuItem("Replace", null, 24));
			menu4.add(new MyMenuItem("Replace in selection", null, 25));
			menu4.add(new JSeparator());
			menu4.add(new MyMenuItem("Show JColorChooser", "COLORCHOOSER16", 40));
			menu4.add(new MyMenuItem("Base converter (2/8/10/16)", "BASE16", 41));
			//
			menu4_2.add(new MyMenuItem("Convert to upper case", "UPPERCASE", 26));
			menu4_2.add(new MyMenuItem("Convert to lower case", "LOWERCASE", 27));
			menu4_2.add(new MyMenuItem("Convert to invert case", "INVERTCASE", 28));
			//
			menu5.add(new MyMenuItem("Insert ten equal signs", null, 30));
			menu5.add(new MyMenuItem("Insert four spaces", null, 31));
			menu5.add(new MyMenuItem("Insert spaces between characters", null, 49));
			menu5.add(new MyMenuItem("Generate random words", null, 32));
			menu5.add(new JSeparator());
			menu5.add(new MyMenuItem("Insert key words (Java)", "KEYWORDJAVA", 33));
			menu5.add(new MyMenuItem("Insert key words (html)", "KEYWORDHTML", 34));
			menu5.add(new MyMenuItem("Insert unicode character", null, 45));
			menu5.add(new MyMenuItem("Insert unicode value", null, 46));
			//
			menu6.add(new MyMenuItem("About RefluxEdit", "APPICON16", 16).setAccelerator(KeyEvent.VK_F1, ActionEvent.CTRL_MASK));
			menu6.add(new MyMenuItem("Visit SourceForge page", "VISIT16", 48));
			//next one: 54
			//free: 47,29,36,37
		}
	}
	
	class MyUmbrellaLayerUI extends LayerUI<JTextArea>
	{
		MyUmbrellaLayerUI()
		{
			super();
		}
		
		@Override
		public void paint(Graphics g, JComponent c)
		{
			super.paint(g,c);
			if (paintTextArea)
			{
				Graphics2D g2d = (Graphics2D)g;
				Rectangle rect = c.getVisibleRect();
				int width = rect.width;
				int height = rect.height;
				int x = rect.x;
				int y = rect.y;
				int radius = (int)((Math.min(width,height)-20)/2.43);
				int edge = radius/7;
				if (edge%2 != 0) edge++;			
				//umbrella "top part"
				g2d.setColor(transYellow);
				g2d.fill(new Arc2D.Double(width/2-radius+x,height/2-radius+y,2*radius,2*radius,180,-180,Arc2D.PIE));
				//umbrella "top point"
				g2d.fillRect(width/2-edge/2+x,height/2-radius-edge+y,edge,edge+1);
				//umbrella "stick"
				g2d.fillRect(width/2-edge/2+x,height/2+y,edge,radius);
				Arc2D.Double bottom = new Arc2D.Double(width/2.0-2.5*edge+x,height/2.0+radius-3*edge/2.0+y,3*edge,3*edge,180,180,Arc2D.PIE);
				Arc2D.Double removing = new Arc2D.Double(width/2.0-1.5*edge+x,height/2.0+radius-edge/2.0+y,edge,edge,180,180,Arc2D.PIE);
				Area area = new Area(bottom);
				area.subtract(new Area(removing));
				g2d.fill(area);
			}
		}
	}
	
	class MyIndentFilter extends DocumentFilter
	{
		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException
		{
			if (("\n").equals(string)&&autoIndent)
			{
				String _char = getLastCharacter(fb, offset);
				if (_char.equals("{"))
				{
					string = string + "\t" + getIndentOfLastLine(fb, offset);
				}
				else
				{
					string = string + getIndentOfLastLine(fb, offset);
				}
				super.insertString(fb, offset, string, attr);
			}
			else if (("}").equals(string)&&autoIndent)
			{
				super.insertString(fb, offset, string, attr);
				AbstractDocument doc = (AbstractDocument)(fb.getDocument());
				int start = textArea.getLineStartOffset(textArea.getLineOfOffset(offset));				
				doc.replace(start,offset-start,removeOneTab(doc.getText(start,offset-start)),attr);
			}
			else super.insertString(fb, offset, string, attr);
		}
		
		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException
		{
			if (("\n").equals(string)&&autoIndent)
			{
				String _char = getLastCharacter(fb, offset);
				if (_char.equals("{"))
				{
					string = string + "\t" + getIndentOfLastLine(fb, offset);
				}
				else
				{
					string = string + getIndentOfLastLine(fb, offset);
				}
				super.replace(fb, offset, length, string, attr);
			}
			else if (("}").equals(string)&&autoIndent)
			{
				super.replace(fb, offset, length, string, attr);
				if (containsTabAndSpaceOnly(offset))
				{
					AbstractDocument doc = (AbstractDocument)(fb.getDocument());
					int start = textArea.getLineStartOffset(textArea.getLineOfOffset(offset));				
					doc.replace(start,offset-start,removeOneTab(doc.getText(start,offset-start)),attr);
				}
			}
			else super.replace(fb, offset, length, string, attr);
		}
		
		private String getIndentOfLastLine(DocumentFilter.FilterBypass fb, int offset) throws BadLocationException
		{
			Document doc = fb.getDocument();
			int line = textArea.getLineOfOffset(offset);
			if (line != 0)
			{
				int start = textArea.getLineStartOffset(line);
				int end = textArea.getLineEndOffset(line);
				String str = "";
				for (int i=start; i<end; i++)
				{
					String character = doc.getText(i,1);
					if (character.equals("\t")) str = str + "\t";
					else if (character.equals(" ")) str = str + " ";
					else break;
				}
				return str;
			}
			else return "";
		}
		
		private String getLastCharacter(DocumentFilter.FilterBypass fb, int offset) throws BadLocationException
		{
			Document doc = fb.getDocument();
			for (int i=offset; i>0; i--)
			{
				String _char = doc.getText(i-1,1);
				if ((!_char.equals("\n"))&&(!_char.equals(" "))) return _char;
			}
			return "1";
		}
		
		private String removeOneTab(String text)
		{
			if (text.contains("\t"))
			{
				int index = text.lastIndexOf("\t");
				text = text.substring(0,index)+text.substring(index+1,text.length());
			}
			return text;
		}
		
		private boolean containsTabAndSpaceOnly(int offset) throws BadLocationException
		{
			int line = textArea.getLineOfOffset(offset);
			int start = textArea.getLineStartOffset(line);
			String text = textArea.getDocument().getText(start, offset-start);
			for (int i=0; i<text.length(); i++)
			{
				char c = text.charAt(i);
				if ((c != '\t')&&(c != ' ')) return false;
			}
			return true;
		}
	}
	
	protected void restoreTextArea()
	{
		//autoIndent
		autoIndent = getBoolean0("autoIndent");
		((AbstractDocument)(textArea.getDocument())).setDocumentFilter(new MyIndentFilter());
		//paint umbrella
		paintTextArea = getBoolean0("showUmbrella");
		//general
		textArea.setDragEnabled(true);
		textArea.setText("");
		textArea.setSelectedTextColor(Color.BLACK);
		//editable	
		if (getBoolean0("isEditable"))
		{
			textArea.setEditable(true);
			textArea.setBackground(Color.WHITE);
		}
		else
		{
			textArea.setEditable(false);
			textArea.setBackground(new Color(245,245,245));
		}
		//wrapping
		textArea.setLineWrap(getBoolean0("LineWrap"));
		textArea.setWrapStyleWord(getBoolean0("WrapStyleWord"));
		//tab size
		try
		{
			textArea.setTabSize(Integer.parseInt(getConfig0("TabSize")));
		}
		catch (Exception ex)
		{
			textArea.setTabSize(4);
		}
		//selection color
		try
		{
			textArea.setSelectionColor(new Color(Short.parseShort(getConfig0("SelectionColor.r")), Short.parseShort(getConfig0("SelectionColor.g")), Short.parseShort(getConfig0("SelectionColor.b"))));
		}
		catch (Exception ex)
		{
			textArea.setSelectionColor(new Color(244,223,255));
		}
		//font
		TMP1 = getConfig0("TextAreaFont.fontName");
		if (TMP1 == null)
		{
			TMP1 = "Microsoft Jhenghei";
		}
		try
		{
			i = Integer.parseInt(getConfig0("TextAreaFont.fontStyle"));
			if ((i>2)||(i<0)) throw new Exception();
		}
		catch (Exception ex)
		{
			i = 0;
		}
		try
		{
			j = Integer.parseInt(getConfig0("TextAreaFont.fontSize"));
			if ((j>200)||(j<1)) throw new Exception();
		}
		catch (Exception ex)
		{
			j = 15;
		}
		textArea.setFont(new Font(TMP1, i, j));
		//remove "delete", "backspace" and "paste(Ctrl+V)" function and implements them later (for undo function)
		InputMap map = textArea.getInputMap();
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0),"none");
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,0),"none");
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK),"none");
		textArea.getActionMap().put("none",null);
		//
		textArea.addMouseListener(new MyListener(0));
		textArea.addMouseListener(taListener);
		textArea.addKeyListener(taListener);
		textArea.getDocument().addDocumentListener(taListener);
		textArea.addCaretListener(taListener);
		textArea.setDropTarget(taListener);
	}
	
	class TextAreaListener extends DropTarget implements KeyListener, CaretListener, DocumentListener, MouseListener
	{
		TextAreaListener()
		{
			super();
		}
		
		@Override
		public synchronized void drop(DropTargetDropEvent dtde)
		{
			dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
			try
			{
				loadConfig();
				setCaret(file,textArea.getCaretPosition());
				saveConfig();
				File file = (File)(((java.util.List)(dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor))).get(0));
				i = JOptionPane.showConfirmDialog(w, "Open file " + file.getPath() + "?\nNote that the current file will not be saved.", "Confirm Dialog", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.YES_OPTION)
				{
					openToTextArea(file);
				}
			}
			catch (Exception ex)
			{
				exception(ex);
			}
		}
		
		private void backup(KeyEvent ev)
		{
			isSaved = false;
			if (!ev.isControlDown())
			{
				undoManager.backup(textArea.getText());
				undoManager.clearRedoList();
			}
		}
		
		@Override
		public void keyTyped(KeyEvent ev)
		{
			synchronized(textArea)
			{
				this.backup(ev);
			}
		}
		//
		@Override
		public void keyPressed(KeyEvent ev)
		{
			//handle shortcut			
			if (ev.isControlDown())
			{				
				MyMenuItem menuItem = null;
				i = ev.getKeyCode();
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
					menuItem = new MyMenuItem(null, null, 13);
				}
				else if (i == KeyEvent.VK_I)
				{
					menuItem = new MyMenuItem(null, null, 18);
				}
				else if (i == KeyEvent.VK_U)
				{
					menuItem = new MyMenuItem(null, null, 19);
				}
				try
				{
					menuItem.dispatchEvent(new MouseEvent(menuItem, MouseEvent.MOUSE_RELEASED, 1, MouseEvent.NOBUTTON, 0, 0, 1, false));
				}
				catch (Exception ex)
				{
				}
			}
			else if (textArea.isEditable())
			{
				//handle delete and backspace
				int code = ev.getKeyCode();
				if (code == KeyEvent.VK_BACK_SPACE)
				{
					if (textArea.getSelectedText() != null)
					{
						backup(ev);
						textArea.replaceSelection(null);
					}
					else
					{
						j = textArea.getCaretPosition();
						if (j >= 1)
						{
							backup(ev);
							textArea.replaceRange(null,j-1,j);
						}
					}
				}
				else if (code == KeyEvent.VK_DELETE)
				{
					if (textArea.getSelectedText() != null)
					{
						backup(ev);
						textArea.replaceSelection(null);
					}
					else
					{
						j = textArea.getCaretPosition();
						if (j+1 <= textArea.getText().length())
						{
							backup(ev);
							textArea.replaceRange(null,j,j+1);
						}
					}
				}
			}
		}
		
		@Override
		public void keyReleased(KeyEvent ev) {}
		
		@Override
		public void changedUpdate(DocumentEvent ev)
		{
			updateCount();
		}
		
		@Override
		public void insertUpdate(DocumentEvent ev)
		{
			updateCount();
		}
		
		@Override
		public void removeUpdate(DocumentEvent ev)
		{
			updateCount();
		}
		
		@Override
		public void caretUpdate(CaretEvent ev)
		{
			updateCount();
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
		}
		
		@Override
		public void mousePressed(MouseEvent ev)
		{
			textArea.setDropTarget(defaultDropTarget);
		}
		
		@Override
		public void mouseClicked(MouseEvent ev)
		{
		}
		
		@Override
		public void mouseEntered(MouseEvent ev)
		{
		}
		@Override
		public void mouseExited(MouseEvent ev)
		{
		}
	}
	
	void updateCount()
	{
		//word count
		if (countWords)
		{		
			synchronized(textArea)
			{
				if (SwingUtilities.getAncestorOfClass(JPanel.class, bottomP1) != null)
				{
					String buffer = null;
					try
					{
						buffer = textArea.getSelectedText();
					}
					catch (Exception ex)
					{
					}
					final String text = (buffer != null?buffer:textArea.getText());
					countLabel.setText("Loading...");
					//
					(new Thread()
					{
						@Override
						public void run()
						{
							this.setPriority(Thread.MIN_PRIORITY);
							final int i = countLabel.isWordCount?wordCount(text):charCount(text);
							SwingUtilities.invokeLater(new Runnable()
							{
								@Override
								public void run()
								{
									countLabel.setCountInfo(i,countLabel.isWordCount);
								}
							});
						}
					}).start();
				}
			}
		}
	}
	
	protected void restorePopup()
	{
		popup.add(new MyMenuItem("Cut", "CUT", 11));
		popup.add(new MyMenuItem("Copy", "COPY", 12));
		popup.add(new MyMenuItem("Paste", "PASTE", 13));
		popup.add(new MyMenuItem("Delete", null, 15));
		popup.add(new JSeparator());
		popup.add(new MyMenuItem("Select all", "SELECT", 9));
		popup.add(new MyMenuItem("Select all and copy", null, 10));
	}
	
	protected void restoreUndoDialog()
	{
		undoDialog.setLayout(new BorderLayout());
		undoDialog.getContentPane().setBackground(Color.WHITE);
		final JPanel center = new JPanel(new GridLayout(1,2,0,0));
		center.add(new JScrollPane(undoJList));
		center.add(new JScrollPane(redoJList));
		undoDialog.add(center, BorderLayout.CENTER);
		MyPanel bottom = new MyPanel(MyPanel.CENTER);
		MyButton undo_b = new MyButton("Use undo list item")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				int index = undoJList.getSelectedIndex();
				for (int i=0; i<=index; i++)
				{
					textArea.setText(undoManager.undo());
				}
				resetUndoDialogList();
			}
		};
		MyButton redo_b = new MyButton("Use redo list item")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				int index = redoJList.getSelectedIndex();
				for (int i=0; i<=index; i++)
				{
					textArea.setText(undoManager.redo());
				}
				resetUndoDialogList();
			}
		};
		undo_b.setPreferredSize(new Dimension(125,28));
		redo_b.setPreferredSize(new Dimension(125,28));
		bottom.add(undo_b);
		bottom.add(redo_b);
		bottom.add(new MyButton("Reload")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				resetUndoDialogList();
			}
		});
		undoDialog.add(bottom, BorderLayout.PAGE_END);
	}
	
	private void resetUndoDialogList()
	{
		//reset list
		DefaultListModel undo_m = (DefaultListModel)(undoJList.getModel());
		undo_m.removeAllElements();
		for (String _undo: undoManager.undoList)
		{
			undo_m.addElement(_undo);
		}
		//
		DefaultListModel redo_m = (DefaultListModel)(redoJList.getModel());
		redo_m.removeAllElements();
		for (String _redo: undoManager.redoList)
		{
			redo_m.addElement(_redo);
		}
	}
	
	private JList<String> createUndoRecordList()
	{
		DefaultListModel<String> lm = new DefaultListModel<String>();
		JList<String> list = new JList<>(lm);
		list.setFont(f13);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new DefaultListCellRenderer()
		{
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				JLabel label = (JLabel)(super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus));
				String str = value.toString();
				int length = str.length();
				if (length > 25) str = str.substring(0,25) + "...";
				if (length > 1) str = str + " (" + length + " characters)";
				else str = str + " (" + length + " character)";
				label.setText(str);
				label.setToolTipText(str);
				return label;
			}
		});
		return list;
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
				this.setIcon(icon(icon));
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
				loadConfig();
				JDialog buttonSelect = new JDialog(w, "Button selection", true);				
				MyCheckBox _new = new MyCheckBox("New", getBoolean0("ToolBar.new"));
				MyCheckBox open = new MyCheckBox("Open", getBoolean0("ToolBar.open"));
				MyCheckBox save = new MyCheckBox("Save", getBoolean0("ToolBar.save"));
				MyCheckBox print = new MyCheckBox("Print", getBoolean0("ToolBar.print"));
				MyCheckBox undo = new MyCheckBox("Undo", getBoolean0("ToolBar.undo"));
				MyCheckBox redo = new MyCheckBox("Redo", getBoolean0("ToolBar.redo"));
				MyCheckBox cut = new MyCheckBox("Cut", getBoolean0("ToolBar.cut"));
				MyCheckBox copy = new MyCheckBox("Copy", getBoolean0("ToolBar.copy"));
				MyCheckBox paste = new MyCheckBox("Paste", getBoolean0("ToolBar.paste"));
				MyCheckBox delete = new MyCheckBox("Delete", getBoolean0("ToolBar.delete"));
				MyCheckBox selectAll = new MyCheckBox("Select all", getBoolean0("ToolBar.selectAll"));
				MyCheckBox selectAllCopy = new MyCheckBox("Select all and copy", getBoolean0("ToolBar.selectAllAndCopy"));
				MyCheckBox search = new MyCheckBox("Search", getBoolean0("ToolBar.search"));
				MyCheckBox replace = new MyCheckBox("Replace", getBoolean0("ToolBar.replace"));
				buttonSelect.setLayout(new GridLayout(7,2,0,0));
				buttonSelect.add(_new);
				buttonSelect.add(open);
				buttonSelect.add(save);
				buttonSelect.add(print);
				buttonSelect.add(undo);
				buttonSelect.add(redo);
				buttonSelect.add(cut);
				buttonSelect.add(copy);
				buttonSelect.add(paste);
				buttonSelect.add(delete);
				buttonSelect.add(selectAll);
				buttonSelect.add(selectAllCopy);
				buttonSelect.add(search);
				buttonSelect.add(replace);
				buttonSelect.pack();
				buttonSelect.setLocationRelativeTo(w);
				buttonSelect.setVisible(true);
				boolean isNew = _new.isSelected();
				boolean isOpen = open.isSelected();
				boolean isSave = save.isSelected();
				boolean isPrint = print.isSelected();
				boolean isUndo = undo.isSelected();
				boolean isRedo = redo.isSelected();
				boolean isCut = cut.isSelected();
				boolean isCopy = copy.isSelected();
				boolean isPaste = paste.isSelected();
				boolean isDelete = delete.isSelected();
				boolean isSelectAll = selectAll.isSelected();
				boolean isSelectAllCopy = selectAllCopy.isSelected();
				boolean isSearch = search.isSelected();
				boolean isReplace = replace.isSelected();
				buttonSelect.dispose();
				setConfig("ToolBar.new", isNew + "");
				setConfig("ToolBar.open", isOpen + "");
				setConfig("ToolBar.save", isSave + "");
				setConfig("ToolBar.print", isPrint + "");
				setConfig("ToolBar.undo", isUndo + "");
				setConfig("ToolBar.redo", isRedo + "");
				setConfig("ToolBar.cut", isCut + "");
				setConfig("ToolBar.copy", isCopy + "");
				setConfig("ToolBar.paste", isPaste + "");
				setConfig("ToolBar.delete", isDelete + "");
				setConfig("ToolBar.selectAll", isSelectAll + "");
				setConfig("ToolBar.selectAllAndCopy", isSelectAllCopy + "");
				setConfig("ToolBar.search", isSearch + "");
				setConfig("ToolBar.replace", isReplace + "");
				saveConfig();
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
				//
				b1 = isUndo;
				b2 = b1;
				if (b1) toolBar.add(toolBarUndo);
				b1 = isRedo;
				b2 = b1;
				if (b1) toolBar.add(toolBarRedo);
				b1 = isCut;
				b2 = b1;
				if (b1) toolBar.add(toolBarCut);
				b1 = isCopy;
				b2 = b1||b2;
				if (b1) toolBar.add(toolBarCopy);
				b1 = isPaste;
				b2 = b1||b2;
				if (b1) toolBar.add(toolBarPaste);
				b1 = isSelectAll;
				b2 = b1||b2;
				if (b1) toolBar.add(toolBarSelectAll);
				b1 = isSelectAllCopy;
				b2 = b1||b2;
				if (b1) toolBar.add(toolBarSelectAllCopy);
				b1 = isDelete;
				b2 = b1||b2;
				if (b1) toolBar.add(toolBarDelete);
				if (b2) ((JToolBar)toolBar).addSeparator();
				//
				if (isSearch) toolBar.add(toolBarSearch);
				if (isReplace) toolBar.add(toolBarReplace);
				toolBar.add(toolBarOptions);
				toolBar.revalidate();
				toolBar.repaint();
			}
		}		
		@Override
		public void mouseEntered(MouseEvent ev) {}
		
		@Override
		public void mouseExited(MouseEvent ev) {}
		
		@Override
		public void mousePressed(MouseEvent ev) {}
		
		@Override
		public void mouseClicked(MouseEvent ev) {}
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
	
	public static void cannotEdit()
	{
		JOptionPane.showMessageDialog(w, "Editing the text is DISABLED!\nPlease enable editing!", "Error", JOptionPane.WARNING_MESSAGE);
	}
	
	public static int isOverride()
	{
		return JOptionPane.showConfirmDialog(w, "Override old file?", "Warning", JOptionPane.WARNING_MESSAGE);
	}
	
	public static String toInvertCase(String str)
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
	
	public static int toNumber(char letter)
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
			//
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
			//
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
	
	public static String toLetter(int a)
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
			//
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
			//
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
	
	public static char toChar(String unicode)
	{
		return (char)Integer.parseInt(unicode, 16);
	}
	
	public static int wordCount(String text)
	{
		text = text.replace("\n"," ");
		if (text != null)
		{
			if (text.isEmpty())
			{
				return 0;
			}
			else
			{
				ArrayList<String> list = new ArrayList<String>(Arrays.asList(text.split(" ")));
				while (list.remove("")) {}
				return list.size();
			}
		}
		else return 0;
	}
	
	public static int charCount(String text)
	{
		return text.replace("\n","").length();
	}
	
	public static String toUnicodeValue(char c)
	{
		TMP1 = Integer.toHexString(c);
		j = TMP1.length();
		for (i=1; i<5-j; i++)
		{
			TMP1 = "0" + TMP1;
		}
		return "\\u" + TMP1.toUpperCase();
	}
	
	public static String insertSpaces(String text)
	{
		char[] chars = text.toCharArray();
		ArrayList<Character> tmpList = new ArrayList<>();
		for (int i=0; i<chars.length; i++)
		{
			tmpList.add(chars[i]);
			boolean _this = (!Character.isSpaceChar(chars[i]))&&(chars[i] != '\t');
			boolean _next = true;
			if (i != chars.length-1)
			{
				_next = (!Character.isSpaceChar(chars[i+1]))&&(chars[i+1] != '\t');
			}
			if (_this&&_next) tmpList.add(' ');
		}
		char[] returnArray = new char[tmpList.size()];
		for (int i=0; i<returnArray.length; i++)
		{
			returnArray[i] = tmpList.get(i).charValue();
		}
		return new String(returnArray);
	}
	
	public String invert(String text)
	{
		char[] chars = text.toCharArray();
		ArrayList<Character> list = new ArrayList<>();
		for (int i=chars.length-1; i>=0; i--)
		{
			list.add(chars[i]);
		}
		int size = list.size();
		chars = new char[size];
		for (int i=0; i<size; i++)
		{
			chars[i] = list.get(i);
		}
		return new String(chars);
	}
	
	public String getFileName(File file)
	{
		String path = file.getName();
		return path.substring(0,path.lastIndexOf("."));
	}
	
	class MyListener extends MouseAdapter
	{
		private int x;
		private boolean isOnNew = false;
		public MyListener(int x)
		{
			this.x = x;
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
		
		class OpenButton extends MyButton
		{
			private JDialog dialog;
			private int x;
			OpenButton(JDialog dialog, String text, String icon, int x)
			{
				this(dialog,text);
				this.x = x;
				if (icon != null)
				{
					this.setPreferredSize(new Dimension(120,120));
					this.setVerticalTextPosition(SwingConstants.BOTTOM);
					this.setHorizontalTextPosition(SwingConstants.CENTER);
					try
					{
						this.setIcon(icon(icon));
					}
					catch (Exception ex)
					{
					}
				}
			}
			
			OpenButton(JDialog dialog, String text)
			{
				super(text);
				this.dialog = dialog;
			}
			
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				dialog.dispose();
				if (x >= 1)
				{
					(new MyListener(x)).mouseReleased(ev);
				}
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)		
		{
			outswitch:
			switch (x)
			{
				case -2: //open window in ribbon UI: open(2), open_quick(3), open_charset(51)
				{
					final JDialog openDialog = new JDialog(w,"Open file...",true);
					openDialog.setUndecorated(true);					
					openDialog.setLayout(new FlowLayout(FlowLayout.CENTER));
					openDialog.getContentPane().setBackground(gray);
					openDialog.getRootPane().setBorder(new BevelBorder(BevelBorder.RAISED));
					OpenButton open = new OpenButton(openDialog,"Open file","OPEN80",2);
					OpenButton quick = new OpenButton(openDialog,"Open file (quick)","OPENQ80",3);
					OpenButton charset = new OpenButton(openDialog,"Open file (charset)","OPENC80",51);
					OpenButton cancel = new OpenButton(openDialog,"Cancel");
					openDialog.add(open);
					openDialog.add(quick);
					openDialog.add(charset);
					openDialog.add(cancel);
					openDialog.pack();
					openDialog.setLocationRelativeTo(w);
					openDialog.setVisible(true);
				}
				break;
				
				case 0: //right click text area
				if (ev.isPopupTrigger())
				{
					popup.show(textArea, ev.getX(), ev.getY());
				}
				break;
				
				case -1:
				case 1: //new file
				if (isOnNew)
				{
					loadConfig();
					setCaret(file,textArea.getCaretPosition());
					saveConfig();
					currentFile.setText(" ");
					file = null;
					textArea.setText("");
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
							textArea.setText("");
							isOnNew = false;
						}
					}
				}
				break;
				
				case 2: //open file
				loadConfig();
				TMP1 = getConfig0("ChooserStyle");
				if (TMP1 == null)
				{
					TMP1 = "Java";
				}
				//
				switch (TMP1)
				{
					case "Java":
					default:
					JavaChooser.resetChoosableFileFilters();
					JavaChooser.addChoosableFileFilter(textFilter);
					i = JavaChooser.showOpenDialog(w);
					if (i == JFileChooser.APPROVE_OPTION)
					{
						setCaret(file,textArea.getCaretPosition());
						saveConfig();
						openToTextArea(JavaChooser.getSelectedFile());
					}
					break;
					
					case "System":
					systemChooser.setFilenameFilter(systemTextFilter);
					systemChooser.setMode(FileDialog.LOAD);
					systemChooser.setVisible(true);
					String child = systemChooser.getFile();					
					if (child != null)
					{
						setCaret(file,textArea.getCaretPosition());
						saveConfig();
						openToTextArea(new File(systemChooser.getDirectory(), child));
					}
					break;
				}
				break;
				
				case 3: //open quick
				TMP1 = JOptionPane.showInputDialog(w, "Please enter the path:", "Input", JOptionPane.QUESTION_MESSAGE);
				if ((TMP1 != null)&&(!TMP1.isEmpty()))
				{
					loadConfig();
					setCaret(file,textArea.getCaretPosition());
					saveConfig();
					openToTextArea(new File(TMP1));
				}
				break;
				 
				case 4: //save as
				File f1 = getSaveToFile();
				if (f1 != null)
				{
					try
					{
						save(f1,true);
						file = f1;
						isSaved = true;
					}
					catch (IOException ex)
					{
						exception(ex);
					}
				}
				break;
				
				case 5: //save
				File f;
				if (file == null)
				{
					f = getSaveToFile();
				}
				else
				{
					f = file;
				}
				//
				if (f != null)
				{
					try
					{
						save(f,true);
						file = f;
						isSaved = true;
					}
					catch (IOException ex)
					{
						exception(ex);
					}
				}
				break;
				
				case 6: //close
				w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
				break;
				
				case 7: //undo
				if (textArea.isEditable())
				{
					String received = undoManager.undo();
					if (received != null)
					{
						i = textArea.getCaretPosition();
						textArea.setText(received);
						try
						{
							textArea.setCaretPosition(i);
						}
						catch (Exception ex)
						{
							textArea.setCaretPosition(0);
						}
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 8: //redo
				if (textArea.isEditable())
				{
					TMP1 = undoManager.redo();
					if (TMP1 != null)
					{
						i = textArea.getCaretPosition();
						textArea.setText(TMP1);
						textArea.setCaretPosition(i);
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 9: //select all
				textArea.selectAll();
				break;
				
				case 10: //select all and copy
				textArea.selectAll();
				clipbrd.setContents(new StringSelection(textArea.getText()), null);
				break;
				
				case 11: //cut
				if (textArea.isEditable())
				{
					undoManager.backup(textArea.getText());
					clipbrd.setContents(new StringSelection(textArea.getSelectedText()), null);
					textArea.replaceSelection(null);
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 12: //copy
				clipbrd.setContents(new StringSelection(textArea.getSelectedText()), null);
				break;
				
				case 13: //paste
				if (textArea.isEditable())
				{
					try
					{
						TMP1 = clipbrd.getData(DataFlavor.stringFlavor).toString();
					}
					catch (Exception ex)
					{
						break;
					}
					undoManager.backup(textArea.getText());
					if (textArea.getSelectedText() != null)
					{
						textArea.replaceSelection(TMP1);
					}
					else
					{
						textArea.insert(TMP1, textArea.getCaretPosition());
					}
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 14: //paste on next line
				if (textArea.isEditable())
				{
					try
					{
						TMP1 = clipbrd.getData(DataFlavor.stringFlavor).toString();
					}
					catch (Exception ex)
					{
						break;
					}
					undoManager.backup(textArea.getText());
					textArea.insert("\n" + TMP1, textArea.getCaretPosition());
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 15: //delete
				if (textArea.isEditable())
				{
					undoManager.backup(textArea.getText());
					textArea.replaceSelection(null);
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 16: //about RefluxEdit
				TMP1 = "RefluxEdit " + VERSION_NO + BETA_STRING + BETA_NO + REV_STRING + REV_NO + " -- a lightweight plain text editor written in Java.\nBy tony200910041, http://tony200910041.wordpress.com\nDistributed under MPL 2.0.\nuser.home: " + System.getProperty("user.home") + "\nYour operating system is " + System.getProperty("os.name") + " (" + System.getProperty("os.version") + "), " + System.getProperty("os.arch") + "\n\nIcon sources: http://www.iconarchive.com and LibreOffice.";
				TMP2 = "About RefluxEdit " + VERSION_NO;
				try
				{
					JOptionPane.showMessageDialog(w, TMP1, TMP2, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(RefluxEdit.class.getResource("/myjava/SRC/Duke.gif")));
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(w, TMP1, TMP2, JOptionPane.INFORMATION_MESSAGE);
				}
				break;
				
				case 17: //editing
				if (textArea.isEditable())
				{
					textArea.setEditable(false);
					textArea.setBackground(new Color(245,245,245));
					writeConfig("isEditable", "false");
				}
				else
				{
					textArea.setEditable(true);
					textArea.setBackground(Color.WHITE);
					writeConfig("isEditable", "true");
				}
				break;
				
				case 18: //increase indentation
				case 19: //decrease indentation
				if (textArea.isEditable())
				{
					i = textArea.getSelectionStart();
					j = textArea.getSelectionEnd();
					int start = Math.min(i,j);
					int end = Math.max(i,j);
					try
					{
						int start_line = textArea.getLineOfOffset(start);
						int end_line = textArea.getLineOfOffset(end);
						for (int x=start_line; x<=end_line; x++)
						{
							int offset = textArea.getLineStartOffset(x);
							if (this.x == 18)
								textArea.insert("\t",offset);
							else
							{
								String _char = textArea.getText(offset,1);
								if (_char.equals("\t")||_char.equals(" "))
								{
									textArea.replaceRange(null,offset,offset+1);
								}
							}
							isSaved = false;
						}
					}
					catch (BadLocationException ex)
					{
					}
				}
				else
				{
					cannotEdit();
				}
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
				{
					TMP2 = textArea.getSelectedText();
					if (TMP2 != null) TMP1 = TMP2;
					else TMP1 = textArea.getText();
					i = wordCount(TMP1);
					if (i == 0)
					{
						JOptionPane.showMessageDialog(w, "Number of words (separated by space): 0\nNumber of characters: 0\nNumber of rows: " + TMP1.split("\n").length, "Word count", JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						JOptionPane.showMessageDialog(w, "Number of words (separated by space): " + i + "\nNumber of characters: " + charCount(textArea.getText()) + "\nNumber of rows: " + TMP1.split("\n").length, "Word count", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				break;
				
				case 23: //search <NON Thread safe>
				TMP1 = textArea.getText();
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
							String[] options = new String[]{"Continue","Cancel"};
							RandomProgress searchProg = new RandomProgress(0, textArea.getText().length());
							final MutableBoolean isContinue = new MutableBoolean(true);
							searchProg.addWindowListener(new WindowAdapter()
							{
								@Override
								public void windowClosing(WindowEvent ev)
								{
									isContinue.set(false);
								}
							});
							TMP2 = textArea.getText();
							textArea.requestFocusInWindow();
							textArea.setCaretPosition(0);
							outFor:			
							for (i=0; i<=j-TMP1.length(); i++)
							{
								if (!isContinue.get())
								{
									break outFor;
								}
								else if (TMP2.substring(i, i+TMP1.length()).equals(TMP1))
								{
									textArea.setSelectionStart(i);
									textArea.setSelectionEnd(i+TMP1.length());
									k++;
									searchProg.setVisible(false);
									l = JOptionPane.showOptionDialog(w, "Found: " + k + " result(s)", "Results", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
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
							searchProg.dispose();
							JOptionPane.showMessageDialog(w, "Done. " + k + " result(s) found.", "Results", JOptionPane.INFORMATION_MESSAGE);
						}
					}).start();
				}
				break;
				
				case 24: //replace
				case 25: //replace, selection
				{
					TMP1 = textArea.getText();
					if (TMP1 == null) return;
					if (TMP1.isEmpty()) return;
					if (textArea.isEditable())
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
								final RandomProgress prog = new RandomProgress(0,1);
								final SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>()
								{
									String original = wd1.getText();
									String replaced = wd2.getText();
									String backup = textArea.getText();
									String text = x==24?backup:textArea.getSelectedText();
									int time = 0;
									@Override
									protected Void doInBackground()
									{
										//initialization
										replace.setVisible(false);
										replace.dispose();
										if (text != null)
										{
											if (text.isEmpty()) return null;
										}
										else return null;
										//
										int wordL = original.length();
										int length = text.length();
										prog.setRange(0, length);
										i = 0;
										while ((i<=length-wordL)&&(!this.isCancelled()))
										{
											if (text.substring(i, i+wordL).equals(original))
											{
												text = text.substring(0, i) + replaced + text.substring(i+wordL, text.length());
												time++;
												i+=replaced.length();												
											}
											else
											{
												i++;
											}
											if (i%50 == 0)
											{
												this.publish(i);
											}
										}
										return null;
									}
									
									protected void process(java.util.List<Integer> chunks)
									{
										int value = chunks.get(chunks.size()-1);
										prog.setValue(value);
									}
									
									protected void done()
									{
										//end
										if (time != 0)
										{
											undoManager.backup(backup);
										}
										if (!this.isCancelled())
										{
											if (x == 24)
											{
												textArea.setText(text);
											}
											else if (x == 25)
											{
												textArea.replaceSelection(text);
											}
										}
										prog.dispose();
										JOptionPane.showMessageDialog(w, time + " time(s) replaced.", "Replace", JOptionPane.INFORMATION_MESSAGE);
									}
								};
								prog.addWindowListener(new WindowAdapter()
								{
									@Override
									public void windowClosing(WindowEvent ev)
									{
										worker.cancel(true);
									}
								});
								worker.execute();
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
				}
				break;
				
				case 26: //upper case
				if (textArea.isEditable())
				{
					undoManager.backup(textArea.getText());
					TMP1 = textArea.getSelectedText();
					if (TMP1 != null)
					{
						textArea.replaceSelection(TMP1.toUpperCase());
					}
					else
					{
						textArea.setText(textArea.getText().toUpperCase());
					}
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 27: //lower case
				if (textArea.isEditable())
				{
					undoManager.backup(textArea.getText());
					TMP1 = textArea.getSelectedText();
					if (TMP1 != null)
					{
						textArea.replaceSelection(TMP1.toLowerCase());
					}
					else
					{
						textArea.setText(textArea.getText().toLowerCase());
					}
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 28: //invert case
				if (textArea.isEditable())
				{
					undoManager.backup(textArea.getText());
					TMP1 = textArea.getSelectedText();
					if (TMP1 != null)
					{
						textArea.replaceSelection(toInvertCase(TMP1));
					}
					else
					{
						textArea.setText(toInvertCase(textArea.getText()));
					}
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 30: //10 equal signs
				if (textArea.isEditable())
				{
					undoManager.backup(textArea.getText());
					textArea.insert("\n==========\n", textArea.getCaretPosition());
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 31: //four spaces
				if (textArea.isEditable())
				{
					undoManager.backup(textArea.getText());
					textArea.insert("    ", textArea.getCaretPosition());
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 32: //random words
				if (textArea.isEditable())
				{
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
						if (m >= 1000000)
						{
							j = JOptionPane.showConfirmDialog(w, "Generating 1,000,000 words or more may take very long time.\nContinue?", "Confirm", JOptionPane.YES_NO_OPTION);
							if (j != JOptionPane.YES_OPTION) break outswitch;
						}
						undoManager.backup(textArea.getText());
						final RandomProgress prog = new RandomProgress(1, m);
						//now start to generate
						final SwingWorker<String, String> worker = new SwingWorker<String, String>()
						{
							int currentProcess;
							@Override
							public String doInBackground()
							{
								String text = "";
								String buffer;
								for (currentProcess=0; currentProcess<m; currentProcess++)
								{
									if (this.isCancelled())
									{
										return text;
									}
									else
									{
										int random = (int)(Math.random()*9+1);
										buffer = "";
										for (int i=1; i<=random; i++)
										{
											buffer = buffer + toLetter((int)(Math.random()*26+1));
										}
										text = text + buffer + " ";
										if (currentProcess%50==0)
										{
											this.publish(text);
											text = "";
										}
									}
								}
								return text;
							}
							
							@Override
							protected void process(java.util.List<String> chunks)
							{
								for (String s: chunks)
								{
									int caret = textArea.getCaretPosition();
									textArea.insert(s,caret);
									textArea.setCaretPosition(s.length()+caret);
								}
								prog.setValue(currentProcess);
							}
							
							@Override
							protected void done()							
							{
								try
								{
									if (!this.isCancelled())
									{
										String s = this.get();
										int caret = textArea.getCaretPosition();
										textArea.insert(s,caret);
										textArea.setCaretPosition(s.length()+caret);
									}
								}
								catch (Exception ex)
								{
								}
								prog.dispose();
								isSaved = false;
								JOptionPane.showMessageDialog(w, "Done: " + currentProcess + " word(s) generated.\nTime taken: " + prog.timeUsed() + " second(s)", "Done", JOptionPane.INFORMATION_MESSAGE);
							}
						};
						worker.execute();
						prog.addWindowListener(new WindowAdapter()
						{
							@Override
							public void windowClosing(WindowEvent ev)
							{
								worker.cancel(true);
								prog.dispose();
								try
								{				
									JOptionPane.showMessageDialog(w, "Aborted: " + worker.getClass().getField("currentProcess").get(worker) + " word(s) generated.\nTime taken: " + prog.timeUsed() + " second(s)", "Aborted", JOptionPane.INFORMATION_MESSAGE);
								}
								catch (Exception ex)
								{
								}
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
				if (textArea.isEditable())
				{
					Object[] keywordJava = new Object[]{"public static void main(String[] args) {", "import java.awt.*;\nimport java.awt.event.*;\nimport javax.swing.*;", "SwingUtilities.invokeLater(new Runnable() {", "class MyListener extends MouseAdapter {", "throw new Exception();", "Integer.parseInt(", "Double.parseDouble(", "JOptionPane.showMessageDialog(", "JOptionPane.showInputDialog(", "public void mouseReleased(MouseEvent ev) {", "public void actionPeformed(ActionEvent ev) {", "public void windowClosing(WindowEvent ev) {", "System.out.println();"};
					TMP1 = (String)JOptionPane.showInputDialog(w, "Please choose one:", "Keyword (Java)", JOptionPane.QUESTION_MESSAGE, null, keywordJava, keywordJava[0]);
					if (TMP1 != null)
					{
						undoManager.backup(textArea.getText());
						textArea.insert(TMP1, textArea.getCaretPosition());
						isSaved = false;
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 34: //keyword, html
				if (textArea.isEditable())
				{
					Object[] keywordHTML = new Object[]{"<a target=\"_blank\" href=\"\"></a>", "<img alt=\"\" src=\"\"></img>", "<font face=\"\"></font>", "<br>"};
					TMP1 = (String)JOptionPane.showInputDialog(w, "Please choose one:", "Keyword (html)", JOptionPane.QUESTION_MESSAGE, null, keywordHTML, keywordHTML[0]);
					if (TMP1 != null)
					{
						undoManager.backup(textArea.getText());
						textArea.insert(TMP1, textArea.getCaretPosition());
						isSaved = false;
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 35: //delete blank lines
				if (textArea.isEditable())
				{
					i = textArea.getLineCount();
					j = JOptionPane.YES_OPTION;
					if (i>=1000)
					{
						j = JOptionPane.showConfirmDialog(w, "This may spend very long time if you have more than 1000 lines.\nContinue?", "Warning", JOptionPane.YES_NO_OPTION);
					}
					if (j == JOptionPane.YES_OPTION)
					{
						TMP1 = textArea.getText();
						undoManager.backup(TMP1);					
						for (j=i; j>=2; j--)
						{
							TMP2 = "";
							for (k=1; k<=j; k++)
							{
								TMP2 = TMP2 + "\n";
							}
							TMP1 = TMP1.replace(TMP2, "\n");
						}
						textArea.setText(TMP1);
					}
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 38: //print
				try
				{
					boolean printed = textArea.print();
				}
				catch (PrinterException ex)
				{
					exception(ex);
				}
				break;
				
				case 39: //other options
				showOptionDialog();
				break;
				
				case 40: //color chooser
				{
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
									if (textArea.isEditable())
									{
										TMP4 = "";
										if (rgb.isSelected()) TMP4 = " " + TMP1;
										if (hsb.isSelected()) TMP4 = TMP4 + " " + TMP2;
										if (hex.isSelected()) TMP4 = TMP4 + " " + TMP3;
										if (!TMP4.isEmpty())
										{
											undoManager.backup(textArea.getText());
										}
										textArea.insert(TMP4, textArea.getCaretPosition());
									}
									colorCode.setVisible(false);
									colorCode.dispose();
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
				}
				break;
				
				case 41: //number conversion
				{
					JDialog baseConvert = new JDialog(w, "Base conversion (2/8/10/16)", true);
					baseConvert.setLayout(new GridLayout(4,1,0,0));
					//
					MyPanel base2 = new MyPanel(MyPanel.LEFT);
					MyCheckBox cb2 = new MyCheckBox("Base 2: ", false);
					base2.add(cb2);
					final MyTextField tf2 = new MyTextField(8,2);
					base2.add(tf2);
					baseConvert.add(base2);
					//
					MyPanel base8 = new MyPanel(MyPanel.LEFT);
					MyCheckBox cb8 = new MyCheckBox("Base 8: ", false);
					base8.add(cb8);
					final MyTextField tf8 = new MyTextField(8,8);
					base8.add(tf8);
					baseConvert.add(base8);
					//
					MyPanel base10 = new MyPanel(MyPanel.LEFT);
					MyCheckBox cb10 = new MyCheckBox("Base 10: ", false);
					base10.add(cb10);
					final MyTextField tf10 = new MyTextField(8,10);
					base10.add(tf10);
					baseConvert.add(base10);
					//
					MyPanel base16 = new MyPanel(MyPanel.LEFT);
					MyCheckBox cb16 = new MyCheckBox("Base 16: ", false);
					base16.add(cb16);
					final MyTextField tf16 = new MyTextField(8,16);
					base16.add(tf16);
					baseConvert.add(base16);
					//
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
									tf16.setText(Long.toString(x,16).toUpperCase());
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
									tf16.setText(Long.toString(x,16).toUpperCase());
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
									tf16.setText(Long.toString(x,16).toUpperCase());
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
					if (textArea.isEditable())
					{
						TMP1 = "";
						if (cb2.isSelected()) TMP1 = tf2.getText() + " ";
						if (cb8.isSelected()) TMP1 = TMP1 + tf8.getText() + " ";
						if (cb10.isSelected()) TMP1 = TMP1 + tf10.getText() + " ";
						if (cb16.isSelected()) TMP1 = TMP1 + tf16.getText() + " ";
						if (!TMP1.isEmpty())
						{
							undoManager.backup(textArea.getText());
						}
						textArea.insert(TMP1, textArea.getCaretPosition());
						isSaved = false;
					}
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
				{
					final JDialog exportImage = new JDialog(w, "Export to image", true);
					exportImage.setLayout(new GridLayout(2,1,0,0));
					MyPanel exportP1 = new MyPanel(MyPanel.LEFT);
					final MyTextField exportTF = new MyTextField(28,0);
					exportP1.add(new MyLabel("Export to: "));
					exportP1.add(exportTF);
					MyButton exportB1 = new MyButton("?");
					exportB1.setPreferredSize(new Dimension(20,20));
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
								TMP1 = getConfig("ChooserStyle");
								if (TMP1 == null)
								{
									TMP1 = "Java";
								}
								if (TMP1.equals("Java"))
								{
									outdo2:
									do
									{
										JavaChooser.resetChoosableFileFilters();
										JavaChooser.addChoosableFileFilter(new FileNameExtensionFilter(TMP3.toUpperCase() + " file", new String[]{TMP3}));
										i = JavaChooser.showSaveDialog(w);
										if (i == JFileChooser.APPROVE_OPTION)
										{
											f1 = JavaChooser.getSelectedFile();
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
								else// if (TMP1.equals("System"))
								{
									outdo3:
									do
									{
										systemChooser.setFilenameFilter(new FilenameFilter()
										{
											@Override
											public boolean accept(File dir, String name)
											{
												dir = new File(dir,name);
												return dir.isDirectory()||name.toUpperCase().endsWith(TMP3);
											}
										});
										systemChooser.setMode(FileDialog.SAVE);
										systemChooser.setVisible(true);
										f1 = new File(systemChooser.getDirectory(), systemChooser.getFile());;
										if (f1 != null)
										{
											if (f1.exists())
											{
												save1 = (isOverride() == JOptionPane.YES_OPTION);
											}
											else break outdo3;
										}
										else return;
									} while (!save1);
								}
								TMP1 = f1.getPath();
								exportTF.setText(TMP1);
								break;
								
								case "Export":
								final String dest = exportTF.getText();
								try
								{
									exportSpin.commitEdit();
								}
								catch (Exception ex)
								{
								}
								m = Short.parseShort(exportSpin.getValue().toString());
								exportImage.dispose();
								if (dest == null) return;
								if (dest.isEmpty()) return;
								// get each line
								final ArrayList<String> lines = new ArrayList<String>();
								int start = 0;
								int end;
								int length = textArea.getText().length();
								do
								{
									try
									{
										end = Utilities.getRowEnd(textArea,start);
										lines.add(textArea.getText(start,end-start).replace("\n",""));
									}
									catch (BadLocationException ex)
									{
										exception(ex);
										return;
									}
									start=end+1;
								}
								while (start<length);								
								final RandomProgress exportProg = new RandomProgress(0, lines.size());
								final Thread exportThr = new Thread()
								{
									@Override
									public void run()
									{							
										Font font = textArea.getFont();
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
											ImageIO.write(image, TMP3, new File(dest));
										}
										catch (IOException ex)
										{
										}
										finally
										{
											font = null;
											f = null;
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
				}
				break;
				
				case 44: //character count
				{
					if ((TMP1 = textArea.getSelectedText()) == null)
					{
						TMP1 = textArea.getText();
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
				}
				break;
				
				case 45: //unicode character
				{
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
					unicodeLbl.setFont(new Font(textArea.getFont().getName(), Font.PLAIN, 50));
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
								if (textArea.isEditable())
								{
									undoManager.backup(textArea.getText());
									textArea.insert(toChar(unicodeField.getText())+"", textArea.getCaretPosition());
									isSaved = false;
								}
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
					isSaved = false;
				}
				break;
				
				case 46: //char to unicode value
				{
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
								if (textArea.isEditable())
								{
									undoManager.backup(textArea.getText());
									textArea.insert(toUnicodeValue(charField.getText().charAt(0)), textArea.getCaretPosition());
									isSaved = false;
								}
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
					TMP1 = textArea.getSelectedText();
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
				}
				break;
				
				case 48: //visit SourceForge
				try
				{
					Desktop.getDesktop().browse(new URI("http://refluxedit.sourceforge.net/"));
				}
				catch (Exception ex)
				{
					exception(ex);
				}
				break;
				
				case 49: //insert space between character
				if (textArea.isEditable())
				{
					String text = textArea.getText();
					undoManager.backup(text);
					textArea.setText(insertSpaces(text));
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 50: //invert all character
				if (textArea.isEditable())
				{
					String text_a = textArea.getText();
					undoManager.backup(text_a);
					String text_s = textArea.getSelectedText();
					if (text_s != null)
					{
						textArea.replaceSelection(invert(text_s));
					}
					else
					{					
						textArea.setText(invert(text_a));
					}
					isSaved = false;
				}
				else
				{
					cannotEdit();
				}
				break;
				
				case 51: //open by charset
				showSpecifiedCharsetDialog(file);
				break;
				
				case 52: //undo/redo list dialog
				{
					resetUndoDialogList();
					undoDialog.pack();
					undoDialog.setLocationRelativeTo(w);
					undoDialog.setVisible(true);
				}
				break;
				
				case 53: //compile
				{
					try
					{
						compileDialog.dispose();
					}
					catch (Exception ex)
					{
					}
					if (file != null)
					{						
						compileDialog = new JDialog(w,"Compile dialog",false);
						DefaultListModel<String> lm = new DefaultListModel<>();
						final JList<String> compileList = new JList<>(lm);
						compileList.setCellRenderer(new DefaultListCellRenderer()
						{
							public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
							{
								JLabel label = (JLabel)(super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus));
								String str = value.toString();
								switch (stringType(str))
								{
									case 0:
									label.setForeground(Color.RED);
									break;
									
									case 1:
									default:
									label.setForeground(Color.BLACK);
									break;
								}
								return label;
							}
							
							private int stringType(String value)
							{
								if (value.startsWith(file.getPath()))
								{
									return 0;
								}
								else return 1;
							}
						});
						compileList.addMouseListener(new MouseAdapter()
						{
							@Override
							public void mousePressed(MouseEvent ev)
							{
								int line = lineno(compileList.getSelectedValue().toString());
								if (line >= 0)
								{
									try
									{
										int start = textArea.getLineStartOffset(line-1);
										int end = textArea.getLineEndOffset(line-1);
										textArea.select(start, end);
										textArea.requestFocus();
									}
									catch (BadLocationException ex)
									{
										exception(ex);
									}
								}
							}
						});
						compileList.setFont(f13);
						compileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						final boolean isGlobal = getBoolean0("Compile.useGlobal");
						try
						{
							save(file,true);
							//delete old file
							loadConfig();
							String regex = getConfig0("Compile.regex");
							File parent = file.getParentFile();
							File dir = null;
							if (getBoolean0("Compile.removeOriginal"))
							{								
								File[] files = parent.listFiles();
								for (File _f: files)
								{
									if (_f.getPath().matches(regex))
									{
										if (dir == null)
										{
											dir = makeNextOldDir(parent);
										}
										cut(_f,dir);
									}
								}
							}
							//start running
							String command = null;
							if ((!isGlobal)||(file!=null))
							{
								command = getConfig0("Compile.command."+file.getPath());
							}
							command = command==null?getConfig0("Compile.command"):command;
							ProcessBuilder builder1;
							if (command == null)
							{
								command = "javac -classpath " + file.getParent() + " " + file.getPath();
							}
							else
							{
								command = command.replace("%f", file.getPath()).replace("%p", file.getParent());
							}
							Process proc = Runtime.getRuntime().exec(command,null,parent);
							//from error stream
							try
							{
								BufferedReader reader1 = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
								while ((TMP1 = reader1.readLine()) != null)
								{
									lm.addElement(TMP1);
								}
								reader1.close();
							}
							catch (Exception ex)
							{
							}
							//from input stream
							try
							{
								BufferedReader reader2 = new BufferedReader(new InputStreamReader(proc.getInputStream()));
								while ((TMP1 = reader2.readLine()) != null)
								{
									lm.addElement(TMP1);
								}
								reader2.close();
								//compiled successfully
								if (lm.size() == 0)
								{
									lm.addElement("The file " + file.getPath() + " is compiled successfully.");
								}
							}
							catch (Exception ex)
							{
							}
						}
						catch (Exception ex)
						{
							exception(ex);
						}
						compileDialog.setLayout(new BorderLayout());
						compileDialog.add(new JScrollPane(compileList), BorderLayout.CENTER);
						MyPanel bottom = new MyPanel(MyPanel.CENTER);
						compileDialog.add(bottom, BorderLayout.PAGE_END);
						bottom.add(new MyButton("Run")
						{
							@Override
							public void mouseReleased(MouseEvent ev)
							{
								(new Thread()
								{
									@Override
									public void run()
									{
										loadConfig();
										String command = null;
										if ((!isGlobal)&&(file != null))
										{
											command = getConfig0("Compile.runCommand."+file.getPath());
										}
										command = (command==null)?getConfig0("Compile.runCommand"):command;
										if (command == null)
										{
											command = "java -classpath " + file.getParent() + " " + getFileName(file) + "\nPAUSE";
										}
										else
										{
											command = replaceExpressions(command);
										}
										String cmdFileName = null;
										if ((!isGlobal)&&(file != null))
										{
											cmdFileName = getConfig0("Compile.runCommandFileName."+file.getPath());
										}
										cmdFileName = (cmdFileName==null)?getConfig0("Compile.runCommandFileName"):cmdFileName;
										if (cmdFileName == null)
										{
											cmdFileName = "CMD.BAT";
										}
										else
										{
											command = replaceExpressions(command);
										}
										File cmdfile = new File(file.getParent(),cmdFileName);
										try
										{
											PrintWriter writer = new PrintWriter(cmdfile,"UTF-8");
											writer.write(command);
											writer.close();
											Desktop.getDesktop().open(cmdfile);
										}
										catch (Exception ex)
										{
										}
									}
									
									String replaceExpressions(String command)
									{
										return command.replace("%f", file.getPath()).replace("%p", file.getParent()).replace("%n",System.getProperty("line.separator")).replace("%s",getFileName(file));
									}
								}).start();
							}
						});
						compileDialog.pack();
						compileDialog.setVisible(true);
					}
				}
				break;
				
				default:
				break;
			}
		}
		
		private int lineno(String value)
		{
			value = value.substring(file.getPath().length()+1, value.length());
			try
			{
				return Integer.parseInt(value.substring(0,value.indexOf(":")));
			}
			catch (Exception ex)
			{
				return -1;
			}
		}
		
		private File makeNextOldDir(File f)
		{
			int i=0;
			while (true)
			{
				i++;
				File _f = new File(f, "old_file_backup_" + i);
				if (!_f.exists())
				{
					_f.mkdir();
					return _f;
				}
			}
		}
		
		private void cut(File src, File dir)
		{
			try
			{
				FileInputStream input = new FileInputStream(src);
				FileOutputStream output = new FileOutputStream(new File(dir,src.getName()));
				byte[] buffer = new byte[4096];
				int read;
				while ((read = input.read(buffer))>0)
				{
					output.write(buffer,0,read);
				}
				input.close();
				output.close();
				src.delete();
			}
			catch (Throwable ex)
			{
			}
		}
		
		private void showOptionDialog()
		{
			JDialog option = new JDialog(w, "Other options", true);
			option.getContentPane().setBackground(Color.WHITE);
			JTabbedPane tabbedPane = new JTabbedPane();
			loadConfig();
			//
			//tab1: general
			JPanel tab1 = new JPanel(new GridLayout(8,1,0,0));
			TMP1 = getConfig0("isPanel");
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
			ActionListener optlistener = new ActionListener()
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
			isPanel.addActionListener(optlistener);
			isToolBar.addActionListener(optlistener);
			NoContainer.addActionListener(optlistener);
			MyPanel P1 = new MyPanel(MyPanel.LEFT);
			P1.add(new MyLabel("Toolbar mode: "));
			P1.add(isPanel);
			P1.add(isToolBar);
			P1.add(NoContainer);
			tab1.add(P1);
			//
			TMP1 = getConfig0("TextAreaFont.fontName");
			if (TMP1 == null)
			{
				TMP1 = "Microsoft Jhenghei";
			}
			try
			{
				i = Integer.parseInt(getConfig0("TextAreaFont.fontStyle"));
				if ((i<0)||(i>2)) throw new Exception();
			}
			catch (Exception ex)
			{
				i = 0;
			}
			try
			{
				j = Integer.parseInt(getConfig0("TextAreaFont.fontSize"));
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
			tab1.add(P2);
			//
			MyPanel P3 = new MyPanel(MyPanel.LEFT);
			boolean isUseNewMenuBar = getBoolean0("isUseNewMenuBar");
			MyCheckBox useNewMenuBar = new MyCheckBox("Use new colored menu bar (for CrossPlatform Look and Feel only):", isUseNewMenuBar);
			P3.add(useNewMenuBar);
			tab1.add(P3);
			//
			MyPanel P4 = new MyPanel(MyPanel.LEFT);
			boolean narrowEdge = getBoolean0("isUseNarrowEdge");
			MyCheckBox useNarrowEdge = new MyCheckBox("Use narrower edge", narrowEdge);
			P4.add(useNarrowEdge);
			tab1.add(P4);	
			//
			MyPanel P5 = new MyPanel(MyPanel.LEFT);
			boolean showCount = getBoolean0("showCount");
			MyCheckBox useCount = new MyCheckBox("Show word and character count", showCount);
			P5.add(useCount);
			tab1.add(P5);
			//
			MyPanel P6 = new MyPanel(MyPanel.LEFT);
			MyCheckBox useIndent = new MyCheckBox("Use automatic indentation", autoIndent);
			P6.add(useIndent);
			tab1.add(P6);
			//
			MyPanel P7 = new MyPanel(MyPanel.LEFT);
			MyCheckBox useUmbrella = new MyCheckBox("Show umbrella", paintTextArea);
			P7.add(useUmbrella);
			JSlider alphaYellow = new JSlider(0,255);
			alphaYellow.setValue(transYellow.getAlpha());
			alphaYellow.setBackground(Color.WHITE);
			alphaYellow.setFont(f13);
			alphaYellow.setPaintLabels(true);
			Dictionary<Integer, JLabel> dict = new Hashtable<>();
			dict.put(0,new MyLabel("0"));
			dict.put(60,new MyLabel("60"));
			dict.put(255,new MyLabel("255"));
			alphaYellow.setLabelTable(dict);
			P7.add(new MyLabel("alpha value:"));
			P7.add(alphaYellow);
			tab1.add(P7);
			//
			MyPanel P8 = new MyPanel(MyPanel.LEFT);
			boolean saveCaret = getBoolean0("Caret.save");
			final MyCheckBox saveCaretPosition = new MyCheckBox("Remember caret position", saveCaret);
			P8.add(saveCaretPosition);
			final MyButton manage = new MyButton("Manage")
			{
				@Override
				public void mouseReleased(MouseEvent ev)
				{
					if (!this.isEnabled()) return;
					final JDialog dialog = new JDialog(w,"Manage caret position data",true);
					dialog.setLayout(new BorderLayout());
					dialog.getContentPane().setBackground(Color.WHITE);
					final DefaultTableModel tam = new DefaultTableModel()
					{
						@Override
						public boolean isCellEditable(int row, int column)
						{
							return false;
						}
					};
					final JTable table = new JTable(tam);
					table.setFont(f13);					
					table.setRowHeight(25);
					table.getTableHeader().setFont(f13);
					table.getTableHeader().setReorderingAllowed(false);
					tam.addColumn("File name");
					tam.addColumn("Caret position");
					ArrayList names = Collections.list(prop.propertyNames());
					for (Object n: names)
					{
						String name = n.toString();
						if (name.startsWith("Caret.")&&(!name.equals("Caret.save")))
						{
							tam.addRow(new String[]{name.substring(6,name.length()),getConfig0(name.toString())});
						}
					}
					dialog.add(new JScrollPane(table));
					MyPanel bottom = new MyPanel(MyPanel.CENTER);
					bottom.add(new MyButton("Remove")
					{
						@Override
						public void mouseReleased(MouseEvent ev)
						{
							int[] rows = table.getSelectedRows();
							ArrayList<String> paths = new ArrayList<>();
							for (int row: rows)
							{
								paths.add(table.getValueAt(row,0).toString());
							}
							int option = JOptionPane.showConfirmDialog(w,"Really remove caret data of " + getString(paths) + "?", "Confirm", JOptionPane.YES_NO_OPTION);
							if (option == JOptionPane.YES_OPTION)
							{
								for (String path: paths)
								{
									removeConfig0("Caret." + path);
								}
									for (int i=0; i<rows.length; i++)
								{
									tam.removeRow(rows[i]-i);
								}
							}							
						}
						
						private String getString(ArrayList<String> list)
						{
							if (list.size() == 1)
							{
								return list.get(0);
							}
							else
							{
								StringBuilder builder = new StringBuilder();
								int size = list.size();
								for (int i=0; i<size; i++)
								{
									if (i==size-2)
									{
										builder.append(list.get(i) + " and ");
									}
									else if (i==size-1)
									{
										builder.append(list.get(i));
									}
									else
									{
										builder.append(list.get(i) + ", ");
									}
								}
								return builder.toString();
							}
						}
						
						@Override
						public Dimension getPreferredSize()
						{
							return new Dimension(65,28);
						}
					});
					bottom.add(new MyButton("Clear")
					{
						@Override
						public void mouseReleased(MouseEvent ev)
						{
							int option = JOptionPane.showConfirmDialog(w,"Remove all caret data?", "Confirm", JOptionPane.YES_NO_OPTION);
							if (option == JOptionPane.YES_OPTION)
							{
								ArrayList names = Collections.list(prop.propertyNames());
								for (Object n: names)
								{
									String name = n.toString();
									if ((name.startsWith("Caret."))&&(!name.equals("Caret.save")))
									{
										removeConfig0(name);
									}
								}
								tam.getDataVector().clear();
								tam.fireTableDataChanged();
							}							
						}
					});
					bottom.add(new MyButton("Cancel")
					{
						@Override
						public void mouseReleased(MouseEvent ev)
						{
							dialog.setVisible(false);
							dialog.dispose();
						}
					});
					dialog.add(bottom, BorderLayout.PAGE_END);
					dialog.pack();
					dialog.setLocationRelativeTo(w);
					dialog.setVisible(true);
					dialog.dispose();
				}
			};
			manage.setEnabled(saveCaret);
			saveCaretPosition.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					manage.setEnabled(saveCaretPosition.isSelected());
				}
			});
			manage.setPreferredSize(new Dimension(65,25));
			P8.add(manage);
			tab1.add(P8);
			//
			//tab2: line wrap
			MyPanel wrap = new MyPanel(MyPanel.CENTER);
			MyCheckBox lineWrap = new MyCheckBox("Line Wrap", textArea.getLineWrap());
			MyCheckBox wrapStyleWord = new MyCheckBox("Wrap by word", textArea.getWrapStyleWord());
			wrap.add(lineWrap);
			wrap.add(wrapStyleWord);
			//
			//tab3: encoding
			JPanel encoding_inner = new JPanel(new GridLayout(4,1,0,0));
			encoding_inner.setBackground(Color.WHITE);
			boolean _default1 = false;
			boolean _default2 = false;
			boolean _others = false;
			TMP1 = getConfig0("Encoding");
			if (TMP1 == null)
			{
				TMP1 = "default1";
			}
			switch (TMP1)
			{
				case "default1":
				_default1 = true;
				break;
				
				case "default2":
				_default2 = true;
				break;
				
				default:
				_others = true;
				break;
			}
			final MyRadioButton isDefault1 = new MyRadioButton("Use Java default 1 (PrintWriter)", _default1, 1);
			final MyRadioButton isDefault2 = new MyRadioButton("Use Java default 2 (FileOutputStream)", _default2, 2);
			final MyRadioButton others = new MyRadioButton("Use specified charset", _others, 3);
			encoding_inner.add(isDefault1);
			encoding_inner.add(isDefault2);
			encoding_inner.add(others);
			final JComboBox<String> comboBox = createCharsetComboBox();
			if (!TMP1.startsWith("default"))
			{
				comboBox.setSelectedItem(TMP1);
				comboBox.setEnabled(true);
			}
			else
			{
				comboBox.setEnabled(false);
			}
			encoding_inner.add(comboBox);
			//
			ActionListener enclistener = new ActionListener()
			{
				public void actionPerformed(ActionEvent ev)
				{
					switch (((MyRadioButton)(ev.getSource())).getIndex())
					{
						case 1:
						isDefault1.setSelected(true);
						isDefault2.setSelected(false);
						others.setSelected(false);
						comboBox.setEnabled(false);
						break;
						
						case 2:
						isDefault1.setSelected(false);
						isDefault2.setSelected(true);
						others.setSelected(false);
						comboBox.setEnabled(false);
						break;
						
						case 3:
						isDefault1.setSelected(false);
						isDefault2.setSelected(false);
						others.setSelected(true);
						comboBox.setEnabled(true);
						break;
					}
				}
			};
			isDefault1.addActionListener(enclistener);
			isDefault2.addActionListener(enclistener);
			others.addActionListener(enclistener);
			MyPanel encoding = new MyPanel(MyPanel.CENTER);
			encoding.add(encoding_inner);
			//
			//tab4: line separator
			MyPanel sepPanel = new MyPanel(MyPanel.CENTER);
			final MyRadioButton sep_n = new MyRadioButton("\\n (Java default, Linux, Mac OS X)", false, 1);
			final MyRadioButton sep_r = new MyRadioButton("\\r (Mac OS 9)", false, 2);
			final MyRadioButton sep_nr = new MyRadioButton("\\r\\n (Windows, Symbian OS)", false, 3);
			TMP1 = getConfig0("lineSeparator");
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
			sepPanel.add(sep_n);
			sepPanel.add(sep_r);
			sepPanel.add(sep_nr);
			//
			//tab5: filechooser
			JPanel chooserOption = new JPanel(new FlowLayout(FlowLayout.CENTER));
			chooserOption.setBackground(Color.WHITE);
			boolean Java = false;
			boolean System = false;
			TMP1 = getConfig0("ChooserStyle");
			if (TMP1 == null)
			{
				TMP1 = "Java";
			}
			switch (TMP1)
			{
				case "Java":
				default:
				Java = true;
				break;
				
				case "System":
				System = true;
				break;
			}
			final MyRadioButton isJava = new MyRadioButton("Use Java JFileChooser", Java, 1);
			final MyRadioButton isSystem = new MyRadioButton("Use system FileDialog", System, 2);
			ActionListener filclistener = new ActionListener()
			{
				public void actionPerformed(ActionEvent ev)
				{
					switch (((MyRadioButton)(ev.getSource())).getIndex())
					{
						case 1:
						isJava.setSelected(true);
						isSystem.setSelected(false);
						break;
						
						case 2:
						isJava.setSelected(false);
						isSystem.setSelected(true);
						break;
					}
				}
			};
			isJava.addActionListener(filclistener);
			isSystem.addActionListener(filclistener);
			chooserOption.add(isJava);
			chooserOption.add(isSystem);
			//
			//tab6: tab size
			MyPanel tab6 = new MyPanel(MyPanel.CENTER);
			JSpinner spinnerTabSize = new JSpinner();
			spinnerTabSize.setModel(new SpinnerNumberModel(textArea.getTabSize(), 1, 50, 1));
			spinnerTabSize.setFont(f13);
			tab6.add(new MyLabel("Tab size: "));
			tab6.add(spinnerTabSize);
			//
			//tab7: selection color
			MyPanel selectionColor = new MyPanel(MyPanel.CENTER);
			MyColorChooser colorChooser = new MyColorChooser(MyColorChooser.LARGE);
			colorChooser.setColor(textArea.getSelectionColor());
			JPanel selectionColor_inner = new JPanel(new BorderLayout());
			selectionColor_inner.setBackground(Color.WHITE);
			selectionColor_inner.add(colorChooser, BorderLayout.CENTER);
			//
			MyPanel scP1 = new MyPanel(MyPanel.CENTER);
			scP1.add(new MyLabel("Default: (244, 223, 255)"));
			selectionColor_inner.add(scP1, BorderLayout.PAGE_END);
			//
			selectionColor.add(selectionColor_inner);
			//
			//tab8: LAF
			JPanel LAFOption_inner = new JPanel(new GridLayout(2,1,0,0));
			LAFOption_inner.setBackground(Color.WHITE);
			boolean DefaultL = false;
			boolean WindowsL = false;
			boolean Nimbus = false;
			String LAF = getConfig0("LAF");
			if (LAF == null)
			{
				LAF = "Default";
			}
			switch (LAF)
			{
				case "Default":
				DefaultL = true;
				break;
				
				case "System":
				WindowsL = true;
				break;
				
				case "Nimbus":
				Nimbus = true;
				break;
			}
			final MyRadioButton isDefaultL = new MyRadioButton("Use default Look and Feel", DefaultL, 1);
			final MyRadioButton isWindowsL = new MyRadioButton("Use Windows Look and Feel", WindowsL, 2);
			final MyRadioButton isNimbus = new MyRadioButton("Use Nimbus Look and Feel", Nimbus, 4);
			ActionListener LAFLis = new ActionListener()
			{
				public void actionPerformed(ActionEvent ev)
				{
					switch (((MyRadioButton)(ev.getSource())).getIndex())
					{
						case 1:
						isDefaultL.setSelected(true);
						isWindowsL.setSelected(false);
						isNimbus.setSelected(false);
						break;
						
						case 2:
						isDefaultL.setSelected(false);
						isWindowsL.setSelected(true);
						isNimbus.setSelected(false);
						break;
						
						case 4:
						isDefaultL.setSelected(false);
						isWindowsL.setSelected(false);
						isNimbus.setSelected(true);
						break;
					}
				}
			};
			isDefaultL.addActionListener(LAFLis);
			isWindowsL.addActionListener(LAFLis);
			isNimbus.addActionListener(LAFLis);
			MyCheckBox isRibbonBox = new MyCheckBox("Use Ribbon UI", isRibbon);
			MyPanel LAF_hori1 = new MyPanel(MyPanel.LEFT);
			LAF_hori1.add(isDefaultL);
			LAF_hori1.add(isWindowsL);
			LAF_hori1.add(isNimbus);
			MyPanel LAF_hori2 = new MyPanel(MyPanel.LEFT);
			LAF_hori2.add(isRibbonBox);
			LAFOption_inner.add(LAF_hori1);
			LAFOption_inner.add(LAF_hori2);
			MyPanel LAFOption = new MyPanel(MyPanel.CENTER);
			LAFOption.add(LAFOption_inner);
			//
			//tab9: compile command
			JPanel compilePanel_inner = new JPanel(new GridLayout(6,1,0,0));
			compilePanel_inner.setBackground(Color.WHITE);
			boolean isGlobal = getBoolean0("Compile.useGlobal");
			String command = null, runcommand = null, filename = null;
			if ((!isGlobal)&&(file != null))
			{
				//read from file
				String path = file.getPath();
				command = getConfig0("Compile.command."+path);
				runcommand = getConfig0("Compile.runCommand."+path);
				filename = getConfig0("Compile.runCommandFileName."+path);
			}
			command = command==null?getConfig0("Compile.command"):command;
			runcommand = runcommand==null?getConfig0("Compile.runCommand"):runcommand;
			filename = filename==null?getConfig0("Compile.runCommandFileName"):filename;
			//
			boolean isDeleteOld = getBoolean0("Compile.removeOriginal");
			String regex = getConfig0("Compile.regex");
			//
			MyPanel compileP0 = new MyPanel(MyPanel.LEFT);
			final MyTextField compiletf = new MyTextField(30,0);
			compiletf.setPreferredSize(new Dimension(compiletf.getSize().width,25));
			compiletf.setText(command);
			compileP0.add(new MyLabel("Compile command: "));
			compileP0.add(compiletf);
			//
			MyPanel compileP1 = new MyPanel(MyPanel.LEFT);
			final MyTextField runtf = new MyTextField(30,0);
			runtf.setPreferredSize(new Dimension(compiletf.getSize().width,25));
			runtf.setText(runcommand);
			compileP1.add(new MyLabel("Run command: "));
			compileP1.add(runtf);
			//
			MyPanel compileP2 = new MyPanel(MyPanel.LEFT);
			final MyTextField filetf = new MyTextField(30,0);
			filetf.setPreferredSize(new Dimension(compiletf.getSize().width,25));
			filetf.setText(filename);
			compileP2.add(new MyLabel("Command file name: "));
			compileP2.add(filetf);
			//
			MyPanel compileP3 = new MyPanel(MyPanel.LEFT);
			final MyCheckBox removeOld = new MyCheckBox("Remove old file", isDeleteOld);			
			final MyTextField removeRegexTF = new MyTextField(20,0);
			removeRegexTF.setText(regex);
			removeRegexTF.setEnabled(isDeleteOld);
			removeOld.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					removeRegexTF.setEnabled(removeOld.isEnabled());
				}
			});
			compileP3.add(removeOld);
			compileP3.add(new MyLabel("Regex to match: "));
			compileP3.add(removeRegexTF);
			//
			MyPanel compileP4 = new MyPanel(MyPanel.LEFT);
			final MyCheckBox useGlobal = new MyCheckBox("Use global commands", isGlobal);
			compileP4.add(useGlobal);
			useGlobal.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					String s1 = null, s2 = null, s3 = null;
					if ((!useGlobal.isSelected())&&(file != null))
					{
						String path = file.getPath();
						s1 = getConfig0("Compile.command."+path);
						s2 = getConfig0("Compile.runCommand."+path);
						s3 = getConfig0("Compile.runCommandFileName."+path);						
					}
					//use global
					s1 = s1==null?getConfig0("Compile.command"):s1;
					s2 = s2==null?getConfig0("Compile.runCommand"):s2;
					s3 = s3==null?getConfig0("Compile.runCommandFileName"):s3;
					compiletf.setText(s1);
					runtf.setText(s2);
					filetf.setText(s3);
				}
			});
			//
			MyPanel compileP5 = new MyPanel(MyPanel.LEFT);
			compileP5.add(new MyLabel("Use %f for the file path, %p for the directory, %s for the simple name of the file and %n for a new line."));
			//
			compilePanel_inner.add(compileP0);
			compilePanel_inner.add(compileP1);
			compilePanel_inner.add(compileP2);
			compilePanel_inner.add(compileP3);
			compilePanel_inner.add(compileP4);
			compilePanel_inner.add(compileP5);
			MyPanel compilePanel = new MyPanel(MyPanel.CENTER);
			compilePanel.add(compilePanel_inner);
			//tab10: check update
			JPanel updatePanel = new MyPanel(MyPanel.LEFT);			
			MyCheckBox update = new MyCheckBox("Check update automatically", getBoolean0("CheckUpdate"));
			updatePanel.add(update);
			MyButton checkUpdateButton = new MyButton("Check now")
			{
				@Override
				public void mouseReleased(MouseEvent ev)
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							checkUpdate(true);
						}
							
					}).start();
				}
				
				@Override
				public Dimension getPreferredSize()
				{
					return new Dimension(80,28);
				}
			};
			updatePanel.add(checkUpdateButton);
			//
			//
			//restore toolbar to original location
			toolBar.setUI(new BasicToolBarUI()
			{
				@Override
				public boolean isFloating()
				{
					return false;
				}
			});
			toolBar.updateUI();
			//
			tabbedPane.addTab("General",icon("OPTIONS16"),tab1,"General");
			tabbedPane.addTab("Line wrap",icon("LINEWRAP16"),wrap,"Line wrap");
			tabbedPane.addTab("Encoding",icon("ENCODING16"),encoding,"Encoding");
			tabbedPane.addTab("Line separator",icon("LINESEPARATOR16"),sepPanel,"Line separator");
			tabbedPane.addTab("FileChooser",icon("FILECHOOSER16"),chooserOption,"FileChooser");
			tabbedPane.addTab("Tab size",icon("TABSIZE16"),tab6,"Tab size");
			tabbedPane.addTab("Selection color",icon("SELECTIONCOLOR16"),selectionColor,"Selection color");
			tabbedPane.addTab("Look and Feel",icon("LAF"),LAFOption,"Look and Feel");
			tabbedPane.addTab("Compile",icon("COMPILE16"),compilePanel,"Compile");
			tabbedPane.addTab("Check update",icon("APPICON16"),updatePanel,"Check update");
			tabbedPane.setFont(f13);
			option.setLayout(new BorderLayout());
			option.add(tabbedPane, BorderLayout.CENTER);
			option.pack();
			option.setLocationRelativeTo(w);			
			option.setVisible(true);
			//end
			{
				//general
				//topPanel
				if ((!isRibbon)&&(topPanel != null))
				{
					w.remove(topPanel);
				}
				if (isPanel.isSelected())
				{
					setConfig("isPanel", "true");
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
					setConfig("isPanel", "false");
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
					setConfig("isPanel", "no");
					topPanel = null;
				}
				//font
				try
				{
					fontChooser.getSpinner().commitEdit();
				}
				catch (ParseException ex)
				{
					exception(ex);
				}
				Font selectedFont = fontChooser.getFont();
				setConfig("TextAreaFont.fontName", selectedFont.getFontName());
				setConfig("TextAreaFont.fontStyle", selectedFont.getStyle() + "");
				setConfig("TextAreaFont.fontSize", selectedFont.getSize() + "");
				textArea.setFont(selectedFont);
				//menubar
				isUseNewMenuBar = useNewMenuBar.isSelected();
				setConfig("isUseNewMenuBar", isUseNewMenuBar + "");
				if ((menubar instanceof ColoredMenuBar)&&(!isRibbon))
				{
					ColoredMenuBar coloredBar = (ColoredMenuBar)menubar;
					coloredBar.setColored(isUseNewMenuBar);
					coloredBar.repaint();
				}
				//narrowEdge
				narrowEdge = useNarrowEdge.isSelected();
				setConfig("isUseNarrowEdge", narrowEdge+"");
				BorderLayout layout = (BorderLayout)(w.getContentPane().getLayout());
				w.remove(layout.getLayoutComponent(BorderLayout.LINE_START));
				w.remove(layout.getLayoutComponent(BorderLayout.LINE_END));
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
				//word count panel
				showCount = useCount.isSelected();
				setConfig("showCount", showCount+"");
				if (showCount)
				{
					bottomP2.special = 2;
					bottomP2.setOpaque(true);
					bottomP2.repaint();
					w.bottomPanel.add(bottomP1, BorderLayout.LINE_START);
				}
				else
				{
					w.bottomPanel.remove(bottomP1);
					bottomP2.special = 0;
					bottomP2.setOpaque(false);
					bottomP2.repaint();
				}
				updateCount();
				//indentation
				autoIndent = useIndent.isSelected();
				setConfig("autoIndent", autoIndent+"");
				//use umbrella
				paintTextArea = useUmbrella.isSelected();
				setConfig("showUmbrella", paintTextArea+"");
				int alpha = alphaYellow.getValue();
				transYellow = new Color(251,231,51,alpha);
				setConfig("Umbrella.alpha", alpha+"");
				//caret
				saveCaret = saveCaretPosition.isSelected();
				setConfig("Caret.save", saveCaret+"");
			}
			{
				//line wrap
				boolean isWrap = lineWrap.isSelected();
				boolean isWrapStyleWord = wrapStyleWord.isSelected();
				setConfig("LineWrap", isWrap + "");
				setConfig("WrapStyleWord", isWrapStyleWord + "");
				textArea.setLineWrap(isWrap);
				textArea.setWrapStyleWord(isWrapStyleWord);
			}
			{
				//encoding
				if (isDefault1.isSelected()) TMP1 = "default1";
				else if (isDefault2.isSelected()) TMP1 = "default2";
				else if (others.isSelected()) TMP1 = comboBox.getSelectedItem().toString();
				setConfig("Encoding", TMP1);
			}
			{
				//filechooser
				if (isJava.isSelected()) TMP1 = "Java";
				else TMP1 = "System";
				setConfig("ChooserStyle", TMP1);
			}
			{
				//line separator
				TMP1 = "\\n";
				if (sep_r.isSelected()) TMP1 = "\\r";
				if (sep_nr.isSelected()) TMP1 = "\\r\\n";
				setConfig("lineSeparator", TMP1);
			}
			{
				//tab size
				try
				{
					spinnerTabSize.commitEdit();
					i = Byte.parseByte(spinnerTabSize.getValue().toString());
				}
				catch (Exception ex)
				{
					i = 4;
				}				
				finally
				{
					textArea.setTabSize(i);
					setConfig("TabSize", i+"");
				}
			}
			{
				//selection color
				Color chosen = colorChooser.getColor();
				textArea.setSelectionColor(chosen);
				setConfig("SelectionColor.r", chosen.getRed()+"");
				setConfig("SelectionColor.g", chosen.getGreen()+"");
				setConfig("SelectionColor.b", chosen.getBlue()+"");
			}
			{
				//LAF
				if (isDefaultL.isSelected()) TMP1 = "Default";
				else if (isWindowsL.isSelected()) TMP1 = "System";
				else if (isNimbus.isSelected()) TMP1 = "Nimbus";
				setConfig("LAF", TMP1);
				boolean newIsRibbon = isRibbonBox.isSelected();
				setConfig("isRibbon", newIsRibbon+"");
				if (!LAF.equals(TMP1)||(newIsRibbon != isRibbon))
				{
					JOptionPane.showMessageDialog(w, "The Look and Feel will be changed after restart.", "Done", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			{
				//compile
				boolean global = useGlobal.isSelected();
				setConfig("Compile.removeOriginal", removeOld.isSelected()+"");
				setConfig("Compile.regex", removeRegexTF.getText());
				setConfig("Compile.useGlobal", global+"");
				if (global||(file==null))
				{
					setConfig("Compile.command", compiletf.getText());
					setConfig("Compile.runCommand", runtf.getText());
					setConfig("Compile.runCommandFileName", filetf.getText());					
				}
				else
				{					
					String path = file.getPath();
					setConfig("Compile.command."+path, compiletf.getText());
					setConfig("Compile.runCommand."+path, runtf.getText());
					setConfig("Compile.runCommandFileName."+path, filetf.getText());
				}
			}
			{
				//check update
				setConfig("CheckUpdate", update.isSelected()+"");
			}
			//
			w.revalidate();
			w.repaint();
			saveConfig();
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
				this.setIcon(icon(icon));
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
		RandomProgress(int min, int max)
		{
			super(w,"Progress",false);
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
		
		RandomProgress()
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
		
		void setValue(int x)
		{
			this.prog.setValue(x);
		}
		
		void setRange(int start, int end)
		{
			this.prog.setMinimum(start);
			this.prog.setMaximum(end);
		}
		
		double timeUsed()
		{
			return (System.currentTimeMillis() - this.initialTime)/1000.0;
		}
		
		int getValue()
		{
			return this.prog.getValue();
		}
		
		void setString(String s)
		{
			this.prog.setString(s);
		}
	}
	
	protected static JComboBox<String> createCharsetComboBox()
	{
		JComboBox<String> comboBox = new JComboBox<>(new Vector<String>(Charset.availableCharsets().keySet()));
		comboBox.setSize(new Dimension(150,26));
		comboBox.setBackground(Color.WHITE);
		comboBox.setFont(f13);
		return comboBox;
	}
	
	protected static JSeparator createSeparator()
	{
		JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
		sep.setPreferredSize(new Dimension(2,85));
		return sep;
	}
	
	protected File getSaveToFile()
	{
		File f = null;
		boolean save = false;
		// file: globar variable
		TMP1 = getConfig("ChooserStyle");
		if (TMP1 == null)
		{
			TMP1 = "Java";
		}
		if (TMP1.equals("Java"))	
		{	
			outdo1:
			do
			{
				JavaChooser.resetChoosableFileFilters();
				JavaChooser.addChoosableFileFilter(textFilter);
				i = JavaChooser.showSaveDialog(w);
				if (i == JFileChooser.APPROVE_OPTION)
				{
					f = JavaChooser.getSelectedFile();
					String path = f.getPath();
					if ((!path.toLowerCase().endsWith(".txt"))&&(!path.contains("."))) f = new File(path+".txt");
					if (f.exists())
					{
						save = (isOverride() == JOptionPane.YES_OPTION);
					}
					else break outdo1;
				}
				else return null; //cancelled
			} while (!save);
		}
		else if (TMP1.equals("System"))
		{
			outdo2:
			do
			{
				systemChooser.setFilenameFilter(systemTextFilter);
				systemChooser.setMode(FileDialog.SAVE);
				systemChooser.setVisible(true);
				String child = systemChooser.getFile();				
				if (child != null)
				{
					if ((!child.toLowerCase().endsWith(".txt"))&&(!child.contains("."))) f = new File(child+".txt");
					f = new File(systemChooser.getDirectory(), child);	
					if (f.exists())
					{
						save = (isOverride() == JOptionPane.YES_OPTION);
					}
					else break outdo2;
				}
				else return null;
			} while (!save);
		}
		return f;
	}
	
	protected void save(File f, boolean useEncoding) throws IOException
	{
		loadConfig();
		if (useEncoding)
		{
			TMP1 = getConfig0("Encoding");
			if (TMP1 == null) TMP1 = "default1";
		}
		else
		{
			TMP1 = "default1";
		}
		TMP2 = getConfig0("lineSeparator");
		if (TMP2 == null) TMP2 = "\n";
		TMP2 = TMP2.replace("\\n", "\n").replace("\\r", "\r");
		if (TMP1.equals("default1"))
		{
			PrintWriter out = new PrintWriter(f);
			String[] strs = textArea.getText().split("\n");
			for (String str: strs)
			{
				out.print(str + TMP2);
			}
			out.close();
		}
		else if (TMP1.equals("default2"))
		{
			byte[] bytes = textArea.getText().replace("\n", TMP2).getBytes();
			FileOutputStream out = new FileOutputStream(f);
			out.write(bytes);
			out.close();
		}
		else
		{
			byte[] bytes = textArea.getText().replace("\n", TMP2).getBytes(TMP1);
			FileOutputStream out = new FileOutputStream(f);
			out.write(bytes);
			out.close();
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
		setCaret(file, textArea.getCaretPosition());
		saveConfig();
	}
	
	class MyWorker extends SwingWorker<String,String>
	{
		private boolean complete;
		FileInputStream input;
		BufferedReader br1;
		RandomProgress prog;
		File f;
		String encoding;		
		MyWorker(RandomProgress dialogToClose, File fileToLoad, String encoding)
		{
			this.prog = dialogToClose;
			this.f = fileToLoad;
			this.encoding = encoding;
		}
		
		@Override
		protected String doInBackground()
		{
			if (encoding != null)
			{
				Charset charset = Charset.forName(encoding);
				byte[] buffer = new byte[4096];
				int byte_read;
				int total_byte_read;
				String text = "";
				String current = "";
				try
				{
					input = new FileInputStream(f);
					//remove BOM
					long x;
					if (encoding.startsWith("UTF-16"))
					{
						x = input.skip(2);
						assert x == 2;
					}
					else if (encoding.startsWith("UTF-32"))
					{
						x = input.skip(4);
						assert x == 4;
					}
					//
					while (((byte_read = input.read(buffer,0,4096)) != -1)&&(!this.isCancelled()))
					{
						if (byte_read != 4096) //buffer: "too large"
						{
							byte[] _new = new byte[byte_read];
							System.arraycopy(buffer,0,_new,0,byte_read);
							String _final = new String(_new,charset);
							if (_final == null) _final = "";
							complete = true;
							return text + _final;
						}
						else //normal
						{
							current = new String(buffer,charset);
							if (current != null)
							{
								this.publish(text + current);
							}
							else
							{
								this.publish(text);
							}
							text = "";
						}
					}
					complete = true;
					return text;
				}
				catch (Exception ex)
				{
					exception(ex);
					prog.dispose();
					complete = false;
					return text;
				}
			}
			else
			{
				String text = "", buffer;
				int lineno = 0;
				try
				{
					br1 = new BufferedReader(new FileReader(f));
					while (((buffer = br1.readLine()) != null)&&(!this.isCancelled()))
					{
						text = text + buffer + "\n";
						lineno++;
						if (lineno%100 == 0)
						{
							this.publish(text);
							text = "";
						}
					}
				}
				catch (Exception ex)
				{
					exception(ex);
					prog.dispose();
					complete = false;
					return text;
				}			
				complete = true;	
				return text;
			}
		}
		
		@Override
		protected void process(java.util.List<String> chunks)
		{
			for (String s: chunks)
			{
				if (s != null) textArea.append(s);
			}
			textArea.setCaretPosition(0);
		}
		
		@Override
		protected void done()
		{
			try
			{
				if (encoding != null) input.close();
				else br1.close();
			}
			catch (Exception ex)
			{
			}
			try
			{
				if (!this.isCancelled())
				{
					textArea.append(this.get());
				}
			}
			catch (Exception ex)
			{
				exception(ex);
			}
			countWords = true;
			updateCount();
			if (complete)
			{
				setFileLabel(f);
				loadConfig();
				if (getBoolean0("Caret.save"))
				{
					try
					{
						textArea.setCaretPosition(Integer.parseInt(getConfig0("Caret."+f.getPath())));
					}
					catch (Exception ex)
					{
						textArea.setCaretPosition(0);
					}
				}
				else
				{
					textArea.setCaretPosition(0);
				}
				prog.dispose();
				double rate = getShowRate();
				if (rate < 0.95)
				{
					if (JOptionPane.showConfirmDialog(w, (encoding!=null?(encoding):"System default") + " encoding is used and only " + format3.format(rate*100) + "% of the characters are correctly shown.\nWould you like to specify the charset and reload?", "Reload", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					{
						showSpecifiedCharsetDialog(f);
					}
				}
			}
		}
	}
	
	protected void openToTextArea(final File f)
	{
		RandomProgress prog = new RandomProgress();
		textArea.setText("");
		int lineno = 0;
		countWords = false;
		String encoding = getEncoding(f);
		final MyWorker worker = new MyWorker(prog,f,encoding);		
		prog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent ev)
			{
				worker.cancel(true);
				JOptionPane.showMessageDialog(w, "Stopped opening file " + f.getPath() + ".", "Stopped", JOptionPane.WARNING_MESSAGE);
				currentFile.setText(" ");
				file = null;
				countWords = true;
				updateCount();
			}
		});
		worker.execute();
	}
	
	protected void openFromFileByCharset(final File file, final String charset)
	{
		final RandomProgress prog = new RandomProgress();		
		final Thread thread = new Thread()
		{
			@Override
			public void run()
			{
				this.setPriority(Thread.MIN_PRIORITY);
				try
				{
					byte[] bytes = Files.readAllBytes(file.toPath());
					textArea.setText(new String(bytes, Charset.forName(charset)));
				}
				catch (Exception ex)
				{
					exception(ex);
					return;
				}
				if (getBoolean0("Caret.save"))
				{
					try
					{
						textArea.setCaretPosition(Integer.parseInt(getConfig0("Caret."+file.getPath())));
					}
					catch (Exception ex)
					{
						textArea.setCaretPosition(0);
					}
				}
				else
				{
					textArea.setCaretPosition(0);
				}
				setFileLabel(file);
				prog.dispose();
			}
		};
		thread.start();
		prog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent ev)
			{
				thread.interrupt();
				prog.dispose();
			}
		});
	}
	
	protected String getEncoding(File f)
	{
		byte[] four = new byte[4];
		FileInputStream input = null;
		if (f.length() < 4) return null;
		try
		{
			input = new FileInputStream(f);
			input.read(four);
			if ((four[0]==-2)||(four[1]==-1)) return "UTF-16BE";
			else if ((four[0]==-1)||(four[1]==-2)) return "UTF-16LE";
			else if ((four[0]==0)||(four[1]==0)||(four[2]==-2)||(four[3]==-1)) return "UTF-32BE";
			else if ((four[0]==-1)||(four[1]==-2)||(four[2]==0)||(four[3]==0)) return "UTF-32LE";
			else return null;
		}
		catch (Exception ex)
		{
			return null;
		}
		finally
		{
			try
			{
				input.close();
			}
			catch (Exception ex)
			{
			}
		}
	}
	
	protected void showSpecifiedCharsetDialog(File file)
	{
		final JDialog dialog = new JDialog(w,"Open file (charset)",true);
		dialog.setLayout(new GridLayout(4,1,0,0));
		dialog.getContentPane().setBackground(Color.WHITE);
		MyPanel P1 = new MyPanel(MyPanel.LEFT);
		P1.add(new MyLabel("Open file by specifying the charset:"));
		dialog.add(P1);
		final JComboBox<String> comboBox = createCharsetComboBox();
		MyPanel P2 = new MyPanel(MyPanel.CENTER);
		P2.add(comboBox);
		dialog.add(P2);
		MyPanel P3 = new MyPanel(MyPanel.CENTER);
		final MyTextField tf = new MyTextField(15,0);
		tf.setPreferredSize(new Dimension(125,26));
		if (file != null)
		{
			tf.setText(file.getPath());
		}
		P3.add(tf);
		MyButton choose = new MyButton("?")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				File file = null;
				switch (getConfig("ChooserStyle"))
				{
					case "Java":
					default:
					JavaChooser.resetChoosableFileFilters();
					JavaChooser.addChoosableFileFilter(textFilter);
					i = JavaChooser.showOpenDialog(w);
					if (i == JFileChooser.APPROVE_OPTION)
					{
						file = JavaChooser.getSelectedFile();
					}
					break;
					
					case "System":
					systemChooser.setFilenameFilter(systemTextFilter);
					systemChooser.setMode(FileDialog.LOAD);
					systemChooser.setVisible(true);
					String child = systemChooser.getFile();					
					if (child != null)
					{						
						file = new File(systemChooser.getDirectory(), child);
					}
				}
				if (file != null) tf.setText(file.getPath());
			}
		};
		choose.setPreferredSize(new Dimension(26,26));
		P3.add(choose);
		dialog.add(P3);
		MyPanel P4 = new MyPanel(MyPanel.CENTER);
		P4.add(new MyButton("Open")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				dialog.setVisible(false);
				openFromFileByCharset(new File(tf.getText()), comboBox.getSelectedItem().toString());
				dialog.dispose();
			}
		});
		P4.add(new MyButton("Cancel")
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				dialog.dispose();
			}
		});
		dialog.add(P4);
		dialog.pack();
		dialog.setLocationRelativeTo(w);
		dialog.setVisible(true);
	}
	
	protected double getShowRate()
	{
		String text = textArea.getText();
		Font currentFont = textArea.getFont();
		int canDisplay = 0;
		for (int i=0; i<text.length(); i++)
		{
			if (currentFont.canDisplay(Character.codePointAt(text,i)))
			{
				canDisplay++;
			}
		}
		return (canDisplay*1.0)/text.length();
	}
	
	protected void setFileLabel(File f)
	{
		TMP1 = f.getPath();
		file = f;
		currentFile.setToolTipText(TMP1);
		if (TMP1.length() > 50)
		{
			currentFile.setText("Current file: " + TMP1.substring(0,25) + "..." + TMP1.substring(TMP1.length()-25, TMP1.length()));
		}
		else
		{
			currentFile.setText("Current file: " + TMP1);
		}
	}
	
	protected static ImageIcon icon(String name)
	{
		try
		{
			return new ImageIcon(RefluxEdit.class.getResource("/myjava/SRC/" + name + ".PNG"));
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	protected static String getsettingsFilePath()
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
	
	protected static void loadConfig()
	{
		try
		{
			prop.load(new FileInputStream(settingsFile));
		}
		catch (Exception ex)
		{
		}
	}
	
	protected static String getConfig(String name)
	{
		loadConfig();
		return getConfig0(name);
	}
	
	private static String getConfig0(String name)
	{
		return prop.getProperty(name);
	}
	
	protected static boolean getBoolean(String name)
	{
		loadConfig();
		return getBoolean0(name);
	}
	
	private static boolean getBoolean0(String str)
	{
		return ("true").equals(prop.getProperty(str));
	}
		
	protected static void saveConfig()
	{
		try
		{
			prop.store(new FileOutputStream(settingsFile), null);
		}
		catch (Exception ex)
		{
		}
	}
	
	protected static void setConfig(String key, String value)
	{
		prop.setProperty(key, value);
	}
	
	protected static void removeConfig0(String key)
	{
		prop.remove(key);
	}
	
	protected static void writeConfig(String key, String value)
	{
		prop.setProperty(key, value);
		saveConfig();
	}
	
	protected static void setCaret(File f, int pos)
	{
		if (getBoolean0("Caret.save")&&(f != null))
		{
			setConfig("Caret."+f.getPath(), pos+"");
		}
	}
	
	protected static void error(String str)
	{
		JOptionPane.showMessageDialog(w,"Error!\n" + str, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	protected static void exception(Throwable ex)
	{
		error("Exception type: " + ex.getClass().getName() + "\nException message: " + ex.getMessage());
	}
	
	static class MyTextField extends JTextField implements MouseListener
	{
		private int x;
		public MyTextField(int size, int x)
		{
			super(size);
			this.x = x;
			this.setFont(f13);
			this.setForeground(Color.BLACK);
			this.setBackground(vlgray);
			this.setBorder(bord1);
			this.setDragEnabled(true);
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
			this.setBackground(vlgray);
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
	
	static class MyPanel extends JPanel
	{
		static final int LEFT = 1;
		static final int CENTER = 2;
		int special = 0;
		MyPanel(int x)
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
		
		MyPanel(int x, int special)
		{
			this(x);
			this.special = special;
		}
		
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			int width = this.getWidth();
			int height = this.getHeight();
			switch (special)
			{
				case 1:
				g.setColor(Color.BLACK);
				g.drawLine(0,0,width,0);
				break;
				
				case 2:
				g.setColor(Color.BLACK);
				g.drawLine(0,0,0,height);
				g.drawLine(0,0,width,0);
				break;
				
				default:
				break;
			}
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
				this.setPreferredSize(new Dimension(this.getWidth(),22));
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
	
	class MyRibbonFirst extends JButton implements MouseListener, PopupMenuListener
	{
		private JPanel panel = new JPanel();
		private JPopupMenu menu = new JPopupMenu();
		private boolean visible = false;
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
			panel.setBackground(brown);
			panel.setLayout(new FlowLayout(FlowLayout.LEFT,0,3));
			menu.insert(this.panel,0);
			menu.addPopupMenuListener(this);
			menu.setBorder(new LineBorder(brown,1));
		}
		
		@Override
		public void popupMenuCanceled(PopupMenuEvent ev)
		{
			Point p = MouseInfo.getPointerInfo().getLocation();
			SwingUtilities.convertPointFromScreen(p,this);
			visible = this.contains(p.x, p.y);
		}
		
		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent ev)
		{
		}
		
		@Override
		public void mousePressed(MouseEvent ev)
		{
		}
		
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent ev)
		{
		}
		
		@Override
		public void mouseReleased(MouseEvent ev)
		{
			if (!visible)
			{
				this.panel.setPreferredSize(new Dimension(165, textArea.getVisibleRect().height));
				menu.pack();
				menu.show(this,0,110);
			}
			else
			{
				menu.setVisible(false);
			}
			visible = !visible;
		}
		
		class MyItemButton extends MyPureButton
		{
			private int x;
			public MyItemButton(String item, final int x)
			{
				super("  " + item);
				this.setBackground(new Color(111,107,100));
				this.setLabelForeground(Color.WHITE);				
				this.setBorder(new LineBorder(Color.BLACK,0));
				this.setLabelFont(new Font("Microsoft Jhenghei", Font.PLAIN, 14));
				this.setAlignLeft();
				this.x = x;
				if (x != -1)
				{
					this.setPreferredSize(new Dimension(165,33));
					this.addMouseListener(new MouseAdapter()
					{
						@Override
						public void mouseReleased(MouseEvent ev)
						{
							menu.setVisible(false);
							visible = false;
							this.mouseExited(ev);
							(new MyListener(x)).mouseReleased(ev);
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
				else
				{
					this.setPreferredSize(new Dimension(165,1));
				}
			}
			
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				if (x == -1)
				{
					g.setColor(Color.WHITE);
					g.drawLine(3,0,162,0);
				}
			}
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
		
		public JPanel getPanel()
		{
			return this.panel;
		}
		
		public void add(String item, int x)
		{
			this.panel.add(new MyItemButton(item, x));
		}
	}
	
	class UndoManager
	{
		Vector<String> undoList = new Vector<>();
		Vector<String> redoList = new Vector<>();
		public UndoManager()
		{
			super();
		}
		
		public void backup(String str)
		{
			undoList.add(0, str); //newer: smaller index
		}
		
		public String undo()
		{
			if (undoList.size() == 0)
			{
				JOptionPane.showMessageDialog(w, "Reached undo limit!", "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			else
			{
				String text = textArea.getText();
				if (!undoList.get(0).equals(text))
				{
					redoList.add(0, text);
				}
				else
				{
					redoList.add(0, undoList.get(0));
				}
				return undoList.remove(0);
			}
		}
		
		public String redo()
		{
			if (redoList.isEmpty())
			{
				JOptionPane.showMessageDialog(w, "Reached redo limit!", "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			else
			{
				undoList.add(0,textArea.getText());
				return redoList.remove(0);
			}
		}
		
		public void clearRedoList()
		{
			redoList.clear();
		}
	}
}
