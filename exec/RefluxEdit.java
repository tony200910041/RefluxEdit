package exec;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import myjava.gui.*;
import myjava.gui.common.*;
import myjava.util.*;
import exec.*;
import myjava.io.*;
import static myjava.gui.ExceptionDialog.*;
import static exec.SourceManager.*;

public class RefluxEdit extends JFrame implements ColorConstants, VersionConstants
{
	/**
	 * singleton class: RefluxEdit
	 */
	static
	{
		//initialization:
		SourceManager.initialize();
		RefluxEdit.setLAF();
		UISetter.initialize();
	}
	/*
	 * constants
	 */
	private static final Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static final ImageIcon questionIcon = SourceManager.createQuestionMessageIcon();
	/*
	 * important settings:
	 */
	public static boolean isCrossPlatformLAF = false;
	public static boolean isRibbon = false;
	/*
	 * component instances:
	 */
	private JComponent topPanel;
	private static RefluxEdit w;
	/*
	 * file association detector
	 * static for convenience
	 */
	private static FADetector detector = new FADetector()
	{
		@Override
		public synchronized void hasNewFile()
		{
			File received = this.getFile();
			if (received != null)
			{
				if (received.exists())
				{
					for (Tab tab: MainPanel.getAllTab())
					{
						if (received.equals(tab.getFile()))
						{
							//select existing tab
							MainPanel.setSelectedComponent(tab);
							return;
						}
					}
					//create new tab and load file
					Tab newTab = Tab.getNewTab();					
					MainPanel.add(newTab);
					newTab.open(received);
				}
			}
		}
	};	
	/*
	 * main method:
	 */
	public static void main(final String[] args)
	{
		final double initialTime = System.currentTimeMillis();
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				if (detector.hasInstance())
				{
					boolean response = detector.awaitResponse(5000);
					if (response)
					{
						if (args.length >= 1)
						{
							//load file
							detector.setFile(new File(args[0]));
						}
						System.exit(0);
					}
					else
					{
						//crashed
						int option = showCrashedDialog();
						if (option == JOptionPane.YES_OPTION)
						{
							//launch anyway
						}
						else if (option == JOptionPane.NO_OPTION)
						{
							//quit
							System.exit(0);
						}
					}
				}
				// load a new RefluxEdit frame
				w = new RefluxEdit();
				w.restoreFrame();
				w.build();
				SplashScreen splash = SplashScreen.getSplashScreen();
				if (splash != null)
				{
					splash.close();
				}
				w.setVisible(true);
				writeConfig("LastStartupTimeTaken", System.currentTimeMillis()-initialTime + "ms");
				if (getBoolean0("CheckUpdate"))
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							VersionChecker.showUpdateDialog(w,false);
						}
					}).start();
				}
				detector.setInstance(true);
				if (args.length >= 1)
				{
					Tab newTab = Tab.getNewTab();
					MainPanel.add(newTab);
					newTab.open(new File(args[0]));
				}
			}
			
			int showCrashedDialog()
			{
				String[] options = {"Launch", "Quit"};
				return JOptionPane.showOptionDialog(null, "RefluxEdit looks like crashed.\nWould you like to launch a new instance?", "Error", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]);
			}
		});
	}
	
	protected static void setLAF()
	{
		JComponent.setDefaultLocale(Locale.ENGLISH);
		try
		{
			switch (getConfig0("LAF"))
			{
				case "System":
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				break;
				
				case "Nimbus":
				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
				UIDefaults defaults = UIManager.getLookAndFeelDefaults();
				defaults.put("ToolTip.font", Resources.f13);
				defaults.put("Button.background", Color.WHITE);
				defaults.put("nimbusInfoBlue", new Color(255,186,0));
				defaults.put("OptionPane.sameSizeButtons", true);
				break;
				
				default:
				UIManager.put("ToolTip.font", new Font("Microsoft Jhenghei", Font.PLAIN, 13));
				UIManager.put("Button.background", Color.WHITE);
				break;
			}
		}
		catch (Throwable ex)
		{
		}
		
	}
	
	public RefluxEdit()
	{
		super("RefluxEdit " + VERSION_NO);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setGlassPane(GrayGlassPane.getInstance());
		/*
		 * confirm closing dialog
		 */
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent ev)
			{
				//load confirm dialog
				int option;
				boolean isSaved = true;
				outFor:
				for (Tab tab: MainPanel.getAllTab())
				{
					if ((!tab.getTextArea().isSaved())&&(!tab.getTextArea().getText().isEmpty()))
					{
						isSaved = false;
						break outFor;
					}
				}
				if (isSaved)
				{
					option = JOptionPane.showConfirmDialog(RefluxEdit.this, "Do you really want to close RefluxEdit?", "Confirm close", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, questionIcon);
				}
				else
				{
					String[] options = new String[]{"<html><center>Close<br>RefluxEdit</center></html>","Cancel","<html><center>Save all and Close<br>(use system encoding)</center></html>"};
					option = JOptionPane.showOptionDialog(RefluxEdit.this, "NOT YET SAVED!\nDo you really want to close RefluxEdit?", "Confirm close", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, questionIcon, options, options[1]);
				}
				if (option == JOptionPane.YES_OPTION)
				{
					this.finalSaveSettings();
				}
				else if ((option == JOptionPane.CANCEL_OPTION)&&(!isSaved))
				{
					//save and close
					try
					{
						/*
						 * iterate through tab list to save file
						 */
						for (Tab tab: new ArrayList<Tab>(MainPanel.getAllTab()))
						{
							if (!tab.getTextArea().isSaved())
							{
								File tabFile = tab.getFile();
								if (tabFile!=null)
								{
									tab.save(tabFile,false); //not using encoding
									MainPanel.close(tab);
								}
								else
								{
									File dest = FileChooser.showPreferredFileDialog(RefluxEdit.this, FileChooser.SAVE, new String[0]);
									if (dest != null)
									{
										tab.save(dest,false);
										MainPanel.close(tab);
									}
									else return; //don't close RefluxEdit
								}
							}
							else
							{
								MainPanel.close(tab);
							}
						}
						this.finalSaveSettings();
					}
					catch (IOException ex)
					{
						exception(ex);
					}
				}
			}
			
			void finalSaveSettings()
			{
				Tab tab = MainPanel.getSelectedTab();
				Dimension d = RefluxEdit.this.getSize();
				Point l = RefluxEdit.this.getLocation();
				loadConfig();
				setConfig("Size.x", d.width + "");
				setConfig("Size.y", d.height + "");
				setConfig("Location.x", l.x + "");
				setConfig("Location.y", l.y + "");
				setConfig("isMaxmized", String.valueOf(RefluxEdit.this.getExtendedState() == JFrame.MAXIMIZED_BOTH));
				//save caret position
				for (Tab t: MainPanel.getAllTab())
				{
					setCaret(t.getFile(), t.getTextArea().getCaretPosition());
				}
				saveConfig();
				detector.setInstance(false);
				System.exit(0);
			}
						
			@Override
			public void windowDeactivated(WindowEvent ev)
			{
				//if deactivated, setDropTarget such that textArea receives drops of file outside
				for (Tab tab: MainPanel.getAllTab())
				{
					tab.getTextArea().setDropTarget(new MyDropTarget(tab));
				}
			}
		});
		/*
		 * set parent
		 */
		ExceptionDialog.setGlobalParent(this);
	}
	
	/*
	 * restore frame size/location etc
	 */
	public void restoreFrame()
	{
		this.setMinimumSize(new Dimension(275,250));
		int sizex=0, sizey=0, locationx=0, locationy=0;
		try
		{
			sizex = (int)Double.parseDouble(getConfig0("Size.x"));
			sizey = (int)Double.parseDouble(getConfig0("Size.y"));
		}
		catch (Exception ex)
		{
			sizex = 690;
			sizey = 550;
		}
		finally
		{
			sizex = Math.min(Math.max(275,sizex),scrSize.width);
			sizey = Math.min(Math.max(250,sizey),scrSize.height);
			this.setSize(sizex, sizey);
		}
		//location
		try
		{
			locationx = (int)Double.parseDouble(getConfig0("Location.x"));
			locationy = (int)Double.parseDouble(getConfig0("Location.y"));
		}
		catch (Exception ex)
		{
			locationx = 0;
			locationy = 0;
		}	
		finally
		{
			locationx = Math.max(0,Math.min(locationx,scrSize.width));
			locationy = Math.max(0,Math.min(locationy,scrSize.height));
			this.setLocation(locationx, locationy);
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
	
	/*
	 * build components
	 */
	public void build()
	{
		/*
		 * use ribbon panel or menubar
		 */
		if (getBoolean0("isRibbon"))
		{
			this.add(MyRefluxEditRibbonPanel.getInstance(), BorderLayout.PAGE_START);
			isRibbon = true;
		}
		else
		{
			/*
			 * menubar
			 */
			isRibbon = false; //further confirm
			ColoredMenuBar menubar = ColoredMenuBar.getInstance();
			String LAF = getConfig0("LAF");
			if (LAF == null)
			{
				LAF = "default";
			}
			switch (LAF)
			{
				case "System":
				menubar.setStyle(ColoredMenuBar.WHITE);
				break;
				
				case "Nimbus":
				break;			
				
				case "default":
				default:
				isCrossPlatformLAF = true;
				if (getBoolean0("isUseNewMenuBar"))
				{
					menubar.setStyle(ColoredMenuBar.MODERN);
				}
				else
				{
					menubar.setStyle(ColoredMenuBar.BLUE);
				}
				break;
			}
			this.setJMenuBar(menubar);
			/*
			 * top panel
			 */
			String isPanel = getConfig0("isPanel");
			if (isPanel != null)
			{
				switch (isPanel)
				{
					case "no":
					default:
					topPanel = null;
					break;
					
					case "true":
					topPanel = FourButtonPanel.getInstance();
					this.add(topPanel, BorderLayout.PAGE_START);
					break;
					
					case "false":
					topPanel = MyToolBar.getInstance();
					this.add(topPanel, BorderLayout.PAGE_START);
					break;
				}
			}
			else topPanel = null;
		}
		this.add(MainPanel.getInstance());
	}
	
	public static RefluxEdit getInstance()
	{
		return w;
	}
	
	public JComponent getPageStartComponent()
	{
		return this.topPanel;
	}
	
	public void setPageStartComponent(JComponent c)
	{
		if (!isRibbon)
		{
			try
			{
				this.remove(this.topPanel);
			}
			catch (Exception ex)
			{
				//fail sliently
			}
			if (c != null)
			{
				this.add(c, BorderLayout.PAGE_START);
			}
			this.topPanel = c;
			this.revalidate();
			this.repaint();
		}
	}
}
