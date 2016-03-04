/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package exec;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.border.*; //open file handler
import java.io.*;
import java.nio.file.*;
import java.util.*;
import myjava.gui.*;
import myjava.gui.common.*;
import myjava.util.*;
import exec.*;
import myjava.io.*;
import static exec.SourceManager.*;
import static myjava.gui.ExceptionDialog.*;
import static myjava.gui.common.Resources.*;

public class RefluxEdit extends JFrame implements ColorConstants, VersionConstants
{
	/**
	 * singleton class: RefluxEdit
	 */
	static
	{
		//initialization:
		SourceManager.initialize();
		RefluxEdit.setLookAndFeel();
		UISetter.initialize();
	}
	/*
	 * constants
	 */
	private static final Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static final ImageIcon questionIcon = SourceManager.createQuestionMessageIcon();
	private static final boolean useTray = SystemTray.isSupported();
	/*
	 * important settings:
	 */
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
	private static FADetector detector;
	public static void main(final String[] args)
	{
		final double initialTime = System.currentTimeMillis();
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				if (SourceManager.IS_MAC)
				{
					MacHandler.initialize();
				}
				else
				{
					launch0(args);
					detector.setInstance(true);
				}
				RefluxEdit.createInstance();
				setConfig("LastStartupTimeTaken", System.currentTimeMillis()-initialTime + "ms");
				RefluxEdit.postOperation(args);
			}
		});
	}
		
	private static void launch0(final String[] args)
	{
		detector = new FADetector()
		{
			@Override
			public synchronized void hasNewFile(File[] files)
			{
				if (files != null)
				{
					outFor:
					for (File received: files)
					{
						if (received.exists())
						{
							if (!w.isVisible())
							{
								w.setVisible(true);
							}
							for (Tab tab: MainPanel.getAllTab())
							{
								if (received.equals(tab.getFile()))
								{
									//select existing tab
									MainPanel.setSelectedComponent(tab);
									continue outFor;
								}
							}
							//create new tab and load file
							Tab newTab = Tab.getNewTab();
							MainPanel.add(newTab);
							try
							{
								newTab.openAndWait(received);
							}
							catch (Exception ex)
							{
								exception(ex);
							}
						}
					}
				}
			}
		};
		if (detector.hasInstance())
		{
			boolean response = detector.awaitResponse(isMac?20000:5000);
			if (response)
			{
				if (args.length >= 1)
				{
					detector.setFiles(convertToFileArray(args));
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
	}
	
	private static void createInstance()
	{
		// load a new RefluxEdit frame
		w = new RefluxEdit();
		w.restoreFrame();
		w.restoreComponents();
		w.createTray();
		SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash != null)
		{
			splash.close();
		}
		w.setVisible(true);
	}
	
	private static void postOperation(String[] args)
	{
		ClipboardDialog.initialize();
		if (getBoolean0("showHint"))
		{
			HintDialog.showHintDialog(w);
		}
		if (args.length >= 1)
		{
			for (File argFile: convertToFileArray(args))
			{
				Tab newTab = Tab.getNewTab();
				MainPanel.add(newTab);
				try
				{
					newTab.openAndWait(argFile);
				}
				catch (Exception ex)
				{
					exception(ex);
				}
			}
		}
	}
			
	private static int showCrashedDialog()
	{
		String[] options = {"Launch", "Quit"};
		return JOptionPane.showOptionDialog(null, "RefluxEdit looks like crashed.\nWould you like to launch a new instance?", "Error", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]);
	}
	
	protected static void setLookAndFeel()
	{
		JComponent.setDefaultLocale(Locale.ENGLISH);		
		String laf = getConfig0("LAF");
		if (laf == null)
		{
			laf = UIManager.getCrossPlatformLookAndFeelClassName();
		}
		try
		{
			UIManager.setLookAndFeel(laf);
		}
		catch (Exception ex)
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
		WindowAdapter windowListener = new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent ev)
			{
				loadConfig();
				if (useTray&&getBoolean0("systemTray.use")&&getBoolean0("systemTray.closeToTray"))
				{
					//close to tray
					RefluxEdit.this.setVisible(false);
				}
				else
				{
					RefluxEdit.this.confirmClose();
				}
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
			
			@Override
			public void windowLostFocus(WindowEvent ev)
			{
				//to center print dialog
				Window win = ev.getOppositeWindow();
				if (win != null)
				{
					if (win.getClass().getName().contains("print") && (win instanceof JDialog))
					{
						JDialog dialog = (JDialog)win;
						dialog.setLocationRelativeTo(RefluxEdit.this);
						dialog.setResizable(true);
						dialog.setAlwaysOnTop(RefluxEdit.this.isAlwaysOnTop());
						dialog.setIconImages(RefluxEdit.this.getIconImages());
					}
				}
			}
		};
		this.addWindowListener(windowListener);
		this.addWindowFocusListener(windowListener);
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
		if (getBoolean0("frame.isMaxmized"))
		{
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		else
		{
			try
			{
				sizex = (int)Double.parseDouble(getConfig0("frame.size.width"));
				sizey = (int)Double.parseDouble(getConfig0("frame.size.height"));
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
				locationx = (int)Double.parseDouble(getConfig0("frame.location.x"));
				locationy = (int)Double.parseDouble(getConfig0("frame.location.y"));
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
		}
		//ontop
		this.setAlwaysOnTop(getBoolean0("frame.onTop"));
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
	public void restoreComponents()
	{
		/*
		 * use ribbon panel or menubar
		 */
		if (getBoolean0("frame.isRibbon"))
		{
			isRibbon = true;
			this.add(MyRefluxEditRibbonPanel.getInstance(), BorderLayout.PAGE_START);			
		}
		else
		{
			isRibbon = false;
			ColoredMenuBar menubar = ColoredMenuBar.getInstance();
			this.setJMenuBar(menubar);
			if (Resources.isMetal)
			{			
				if (getBoolean0("frame.newMenuBar"))
				{
					menubar.setStyle(ColoredMenuBar.MODERN);
				}
				else
				{
					menubar.setStyle(ColoredMenuBar.BLUE);
				}
			}
			/*
			 * top panel
			 */
			String isPanel = getConfig0("frame.isPanel");
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
		this.add(MainPanel.getInstance(), BorderLayout.CENTER);
	}
	
	/*
	 * system tray
	 */
	public void createTray()
	{
		if (useTray&&getBoolean0("systemTray.use"))
		{
			//JPopupMenu
			JPopupMenu popup = new JPopupMenu();
			popup.add(new MyPopupMenuItem("Show/Hide",null,-1));
			popup.add(new JSeparator());
			popup.add(new MyPopupMenuItem("New file","NEW",1));
			popup.add(new MyPopupMenuItem("New file (clipboard)","NEWCLIPBOARD16",56));
			popup.add(new MyPopupMenuItem("Open file","OPEN",2));
			popup.add(new JSeparator());
			popup.add(new MyPopupMenuItem("About RefluxEdit","APPICON16",16));
			popup.add(new MyPopupMenuItem("Close","CLOSE",6));
			MyTrayIcon icon = new MyTrayIcon(icon("APPICON16").getImage(), "RefluxEdit " + VERSION_NO, popup);
			icon.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseReleased(MouseEvent ev)
				{
					if (!ev.isPopupTrigger())
					{
						RefluxEdit.this.setVisible(!RefluxEdit.this.isVisible());
					}
				}
			});
			try
			{
				SystemTray.getSystemTray().add(icon);
			}
			catch (Exception ex)
			{
				//shouldn't happen
				throw new InternalError();
			}
		}
	}
	
	/*
	 * confirm close
	 */
	public void confirmClose()
	{
		this.setVisible(true);
		SaveDialog.showDialog(this);
	}
	
	/*
	 * close RefluxEdit
	 */
	public void close()
	{
		Dimension d = RefluxEdit.this.getSize();
		Point l = RefluxEdit.this.getLocation();
		setConfig("frame.size.width",Integer.toString(d.width));
		setConfig("frame.size.height",Integer.toString(d.height));
		setConfig("frame.location.x",Integer.toString(l.x));
		setConfig("frame.location.y",Integer.toString(l.y));
		setConfig("frame.isMaxmized",String.valueOf(RefluxEdit.this.getExtendedState() == JFrame.MAXIMIZED_BOTH));
		//save caret position
		for (Tab tab: MainPanel.getAllTab())
		{
			setCaret(tab.getFile(), tab.getTextArea().getCaretPosition());
		}
		saveConfig();
		if (detector != null)
		{
			detector.setInstance(false);
		}
		System.exit(0);
	}
	
	/*
	 * singleton instance getter
	 */	
	public static RefluxEdit getInstance()
	{
		return w;
	}
	
	/*
	 * MyRibbonFirst: FILE button
	 */
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
	
	/*
	 * convert from String[] to File[]
	 */
	private static java.util.List<File> convertToFileArray(String[] paths)
	{
		java.util.List<File> list = new ArrayList<>();
		for (String arg: paths)
		{
			if (arg != null)
			{
				File file = new File(arg);
				if (file.exists())
				{
					list.add(file);
				}
			}
		}
		return list;
	}
}
