/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;
import java.text.*;
import java.net.*;
import exec.*;
import myjava.io.*;
import myjava.gui.common.*;
import static exec.SourceManager.*;
import static myjava.gui.ExceptionDialog.*;
import static myjava.util.StaticUtilities.*;

public class Tab extends JPanel implements DocumentListener, CaretListener, Resources
{
	/*
	 * constants
	 */
	public static final DecimalFormat format3 = new DecimalFormat("##0.###");
	/*
	 * fields
	 */
	private MyTextArea textArea = new MyTextArea();
	private MyUmbrellaLayerUI layerUI = new MyUmbrellaLayerUI();
	private JLayer<? extends JComponent> layer1 = new JLayer<MyTextArea>(textArea, layerUI);
	private JPanel bottomPanel = new JPanel(new BorderLayout());
	private MyBlackLinePanel bottomP1 = new MyBlackLinePanel(0);
	private MyBlackLinePanel bottomP2 = new MyBlackLinePanel(1);
	private CountLabel countLabel = new CountLabel(0, true);
	private FileLabel fileLabel = new FileLabel();
	private UndoDialog undoDialog;	
	private File file;
	private FileWatcher watcher;
	private BorderLayout layout = new BorderLayout();
	private boolean isNew = true;
	private boolean isSaved = true;
	/*
	 * for adding to JTabbedPane
	 */
	private JPanel tabPanel = new JPanel(new BorderLayout());
	private MyLabel tabLabel = new MyLabel();
	private JPopupMenu tabPopup = new JPopupMenu();
	/*
	 * static properties
	 */
	static
	{
		loadConfig();
	}
	private static boolean countWords = getBoolean0("showCount"); //count or not
	private static boolean enableCountWords = true; //enable or disable, changed when opening file
	private static int edgeType = getBoolean0("isUseNarrowEdge")?Edge.NARROW:Edge.WIDE;
	
	public Tab(File file)
	{
		super();
		this.setLayout(layout);
		this.add(new JScrollPane(this.layer1), BorderLayout.CENTER);
		this.file = file;
		this.undoDialog = new UndoDialog(this);
		//
		this.bottomP1.setPreferredSize(new Dimension(160,1));
		this.bottomP1.add(countLabel);
		this.bottomP2.add(fileLabel);
		this.add(bottomPanel, BorderLayout.PAGE_END);
		//
		this.update();
		this.textArea.getDocument().addDocumentListener(this);
		this.textArea.addCaretListener(this);
		//
		MouseAdapter mouseListener = new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent ev)
			{
				if (ev.isPopupTrigger())
				{					
					tabPopup.show(tabLabel,ev.getX(),ev.getY());					
				}
				else
				{
					/*
					 * adding MouseListener will disable tab swap
					 * so implements it manually
					 */
					if (ev.getClickCount() == 1)
					{
						JTabbedPane tabbedPane = MainPanel.getInstance().getTabbedPane();
						MouseEvent me = SwingUtilities.convertMouseEvent(tabLabel,ev,tabbedPane);
						tabbedPane.getMouseListeners()[0].mousePressed(me);
					}
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent ev)
			{
				JTabbedPane tabbedPane = MainPanel.getInstance().getTabbedPane();
				MouseEvent me = SwingUtilities.convertMouseEvent(tabLabel,ev,tabbedPane);
				tabbedPane.getMouseListeners()[0].mouseEntered(me);
			}
			
			@Override 
			public void mouseDragged(MouseEvent ev)
			{
				JTabbedPane tabbedPane = MainPanel.getInstance().getTabbedPane();
				MouseEvent me = SwingUtilities.convertMouseEvent(tabLabel,ev,tabbedPane);
				int draggedIndex = tabbedPane.indexOfComponent(Tab.this);
				int reachedIndex = tabbedPane.indexAtLocation(me.getX(), me.getY());
				if ((reachedIndex != -1)&&(draggedIndex != reachedIndex))
				{
					/*
					 * swap "draggedIndex" and "reachedIndex"
					 * remove dragged->add dragged to reachedIndex->remove reached->add reached to draggedIndex
					 */
					Tab reached = (Tab)(tabbedPane.getComponentAt(draggedIndex));
					MainPanel.getInstance().swapTab(draggedIndex, reachedIndex);
				}
			}
		};
		tabLabel.addMouseListener(mouseListener);
		tabLabel.addMouseMotionListener(mouseListener);
		tabPanel.setOpaque(false);
		tabPanel.add(tabLabel, BorderLayout.CENTER);
		//
		tabPopup.add(new MyPopupMenuItem("New tab","NEW",-14));
		tabPopup.add(new MyPopupMenuItem("Save as","SAVE",-12));
		tabPopup.add(new MyPopupMenuItem("Save",-13));
		tabPopup.addSeparator();
		tabPopup.add(new MyPopupMenuItem("Close","CLOSE",-11));
	}
	
	public Tab()
	{
		this(null);
	}
	
	public static Tab getNewTab()
	{
		/*
		 * iterate through the list and 
		 * see if an empty Tab can be obtained
		 * returned Tab must be added into MainPanel
		 */
		for (Tab tab: MainPanel.getAllTab())
		{
			if (tab.isNew)
			{
				if (tab.file == null)
				{
					if (tab.textArea.getText().isEmpty())
					{
						return tab;
					}
				}
			}
		}
		Tab newTab = new Tab();
		MainPanel.add(newTab);
		return newTab;
	}
	
	public static void setEdgeType(int edgeType)
	{
		Tab.edgeType = edgeType;
	}
	
	public static void setEnableCountWords(boolean countWords)
	{
		Tab.countWords = countWords;
	}
	
	public void update()
	{
		// edge
		try
		{
			layout.removeLayoutComponent(layout.getLayoutComponent(BorderLayout.LINE_START));
			layout.removeLayoutComponent(layout.getLayoutComponent(BorderLayout.LINE_END));
		}
		catch (Exception ex)
		{
			// fail sliently
		}
		this.add(Edge.createEdge(edgeType), BorderLayout.LINE_START);
		this.add(Edge.createEdge(edgeType), BorderLayout.LINE_END);
		// countWords
		try
		{
			bottomPanel.removeAll();
		}
		catch (Exception ex)
		{
			// fail sliently
		}
		bottomPanel.add(bottomP2, BorderLayout.CENTER);
		if (countWords)
		{
			bottomPanel.add(bottomP1, BorderLayout.LINE_START);
			this.updateCount();
		}
		// textArea
		this.textArea.update();
		this.revalidate();
		this.repaint();
	}
	
	@Override
	public void caretUpdate(CaretEvent ev)
	{
		this.updateCount();
	}
	
	@Override
	public void insertUpdate(DocumentEvent ev)
	{
		this.isNew = false;
		this.setSaved(false);
		this.updateCount();
	}
	
	@Override
	public void changedUpdate(DocumentEvent ev)
	{
		this.isNew = false;
		this.setSaved(false);
		this.updateCount();
	}
	
	@Override
	public void removeUpdate(DocumentEvent ev)
	{
		this.isNew = false;
		this.setSaved(false);
		this.updateCount();
	}
	
	public void updateCount()
	{
		//word count
		if (countWords&&enableCountWords)
		{		
			if (SwingUtilities.getAncestorOfClass(JPanel.class, this.bottomP1) != null)
			{
				String buffer = null;
				try
				{
					buffer = this.textArea.getSelectedText();
				}
				catch (Exception ex)
				{
				}
				final String text = buffer!=null?buffer:(this.textArea.getText());
				this.countLabel.setText("Loading...");
				//
				Thread thread = new Thread()
				{
					@Override
					public void run()
					{
						this.setPriority(Thread.MIN_PRIORITY);
						final int count = Tab.this.countLabel.isWordCount?wordCount(text):charCount(text);
						SwingUtilities.invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								Tab.this.countLabel.setCountInfo(count, Tab.this.countLabel.isWordCount);
							}
						});
					}
				};
				thread.setDaemon(true);
				thread.start();
			}
		}
	}
	
	public void save() throws IOException
	{
		this.save(this.file, false);
	}
	
	public void save(File dest, boolean useEncoding) throws IOException
	{
		/*
		 * close FileWatcher (will be enabled again later)
		 */
		if (watcher != null)
		{
			watcher.setEnabled(false);
			watcher.close();
		}
		loadConfig();
		/*
		 * encoding
		 */
		String encoding;
		if (useEncoding)
		{
			encoding = getConfig0("Encoding");
			if (encoding == null) encoding = "default1";
		}
		else
		{
			encoding = "default1";
		}
		/*
		 * lineSeparator
		 */
		String lineSeparator = getConfig0("lineSeparator");
		if (lineSeparator == null) lineSeparator = "\n";
		lineSeparator = lineSeparator.replace("\\n", "\n").replace("\\r", "\r");
		/*
		 * now start writing the file
		 */
		File tempFile = createTempSaveFile(dest);
		if (dest.exists())
		{
			assert (!tempFile.exists());
			Files.move(dest.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		if (encoding.equals("default1"))
		{
			/*
			 * default1 mode: use PrintWriter
			 */
			
			PrintWriter out = new PrintWriter(new FileOutputStream(dest),true);
			String[] strs = this.textArea.getText().split("\n");
			for (String str: strs)
			{
				out.print(str + lineSeparator);
			}
			out.close();
		}
		else if (encoding.equals("default2"))
		{
			/*
			 * default2 mode: use FileOutputStream
			 */
			byte[] bytes = this.textArea.getText().replace("\n", lineSeparator).getBytes();
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dest));
			output.write(bytes,0,bytes.length);
			output.flush();
			output.close();
		}
		else
		{
			/*
			 * use specified encoding
			 */
			byte[] bytes = this.textArea.getText().replace("\n", lineSeparator).getBytes(encoding);
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dest));
			output.write(bytes,0,bytes.length);
			output.flush();
			output.close();
			JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), "You are using " + encoding + " encoding (beta).\nPlease check if the this.file is saved correctly.", "Saved", JOptionPane.WARNING_MESSAGE);
		}
		/*
		 * finished: now change this.file
		 */
		setCaret(this.file, this.textArea.getCaretPosition());
		saveConfig();
		this.setSaved(true);		
		this.setFile(dest);
		/*
		 * 
		 */
		encoding = null;
		lineSeparator = null;
		/*
		 * finally remove backup
		 */
		if (tempFile.exists())
		{
			boolean success = tempFile.delete();
			if (!success)
			{
				error("Cannot delete backup file: " + tempFile + ".");
			}
		}
	}
	
	class MyWorker extends SwingWorker<String,String>
	{
		private final Window parent = SwingUtilities.windowForComponent(Tab.this);
		private volatile boolean complete;
		private FileInputStream input;
		private BufferedReader br1;
		private RandomProgress prog;
		private File f;
		private String encoding;
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
				if (s != null) Tab.this.textArea.append(s);
			}
			Tab.this.textArea.setCaretPosition(0);
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
					Tab.this.textArea.append(this.get());
				}
			}
			catch (Exception ex)
			{
				exception(ex);
			}
			Tab.enableCountWords = true;
			if (countWords)
			{
				Tab.this.updateCount();
			}
			Tab.this.getTextArea().setAutoBackup(true);
			Tab.this.getTextArea().setOpening(false);
			if (complete)
			{
				Tab.this.setFile(f);
				loadConfig();
				if (getBoolean0("Caret.save"))
				{
					try
					{
						Tab.this.textArea.setCaretPosition(Integer.parseInt(getConfig0("Caret."+f.getPath())));
					}
					catch (Exception ex)
					{
						Tab.this.textArea.setCaretPosition(0);
					}
				}
				else
				{
					Tab.this.textArea.setCaretPosition(0);
				}
				prog.dispose();
				double rate = getShowRate();
				if (rate < 0.95)
				{
					if (JOptionPane.showConfirmDialog(parent, (encoding!=null?(encoding):"System default") + " encoding is used and only " + format3.format(rate*100) + "% of the characters are correctly shown.\nWould you like to specify the charset and reload?", "Reload", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					{
						Tab.this.showSpecifiedCharsetDialog(f);
					}
				}
			}
			else
			{
				Tab.this.setFile(null);
			}
			MainPanel.setSelectedComponent(Tab.this);
			Tab.this.setSaved(true);
		}
	}
	
	public void open(File src)
	{
		if (src.exists()&&(src.isFile()))
		{
			this.open0(src);
		}
		else
		{
			error("Cannot read file " + src.getPath() + "!");
		}
	}
	
	public void openAndWait(File src) throws Exception
	{
		if (src.exists()&&(src.isFile()))
		{
			this.open0(src).get();
		}
		else
		{
			error("Cannot read file " + src.getPath() + "!");
		}
	}
	
	private SwingWorker<?,?> open0(File src)
	{
		MainPanel.setSelectedComponent(Tab.this);
		Window parent = SwingUtilities.windowForComponent(Tab.this);
		if (watcher != null)
		{
			watcher.setEnabled(false);
			watcher.close();
		}
		/*
		 * disable count words to prevent hanging
		 */
		enableCountWords = false;
		/*
		 * disable auto backup
		 */
		this.getTextArea().setAutoBackup(false);
		this.getTextArea().setOpening(true);
		/*
		 * cannot be new tab
		 */
		this.isNew = false;
		/*
		 * show "loading"
		 */
		RandomProgress prog = new RandomProgress(parent);
		this.textArea.setText("");
		int lineno = 0;
		String encoding = getEncoding(src);		
		final MyWorker worker = new MyWorker(prog,src,encoding);		
		prog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent ev)
			{
				worker.cancel(true);
				JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(Tab.this), "Stopped opening file " + file.getPath() + ".", "Stopped", JOptionPane.WARNING_MESSAGE);
				Tab.this.setFile(null);
				Tab.enableCountWords = true;
				Tab.this.getTextArea().setAutoBackup(true);
				Tab.this.getTextArea().setOpening(false);
				if (Tab.countWords)
				{
					updateCount();
				}
				if (Tab.this.watcher != null)
				{
					watcher.close();
				}
			}
		});
		worker.execute();
		return worker;
	}
	
	public void open(File src, String charset)
	{
		if (src.exists()&&(src.isFile()))
		{
			this.open0(src,charset);
		}
		else
		{
			error("Cannot read file " + src.getPath() + "!");
		}
	}
	
	private Thread open0(final File src, final String charset)
	{
		MainPanel.setSelectedComponent(Tab.this);
		Window parent = SwingUtilities.windowForComponent(Tab.this);
		if (watcher != null)
		{
			watcher.setEnabled(false);
			watcher.close();
		}
		/*
		 * disable count words to prevent hanging
		 */
		enableCountWords = false;
		this.getTextArea().setAutoBackup(false);
		this.getTextArea().setOpening(true);
		/*
		 * cannot be new tab
		 */
		this.isNew = false;
		/*
		 * show "loading"
		 */
		final RandomProgress prog = new RandomProgress(parent);		
		final Thread thread = new Thread()
		{
			@Override
			public void run()
			{
				this.setPriority(Thread.MIN_PRIORITY);
				try
				{
					/*
					 * Java 7+
					 */
					byte[] bytes = Files.readAllBytes(src.toPath());
					final String decoded = new String(bytes, Charset.forName(charset));
					try
					{
						SwingUtilities.invokeAndWait(new Runnable()
						{
							@Override
							public void run()
							{
								Tab.this.textArea.setText(decoded);
							}
						});
					}
					catch (InterruptedException ex)
					{
						throw new InternalError(); //shouldn't be interrupted
					}
				}
				catch (Exception ex1)
				{
					exception(ex1);
					return;
				}
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						if (getBoolean0("Caret.save"))
						{
							try
							{
								Tab.this.textArea.setCaretPosition(Integer.parseInt(getConfig0("Caret."+file.getPath())));
							}
							catch (Exception ex)
							{
								Tab.this.textArea.setCaretPosition(0);
							}
						}
						else
						{
							Tab.this.textArea.setCaretPosition(0);
						}
						Tab.this.setFile(src);
						Tab.enableCountWords = true;
						Tab.this.getTextArea().setAutoBackup(true);
						Tab.this.getTextArea().setOpening(false);
						if (Tab.countWords)
						{
							updateCount();
						}
						Tab.this.setSaved(true);
						/*
						 * remove old watcher, and create a new one
						 */
						prog.dispose();
					}
				});
			}
		};
		prog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent ev)
			{
				thread.interrupt();
				Tab.this.file = null;
				Tab.this.fileLabel.setFile(null);
				Tab.enableCountWords = true;
				Tab.this.getTextArea().setAutoBackup(true);
				if (Tab.countWords)
				{
					updateCount();
				}
				if (Tab.this.watcher != null)
				{
					watcher.close();
				}
			}
		});
		thread.start();
		return thread;
	}
	
	public void open(URL url) throws IOException
	{
		this.getTextArea().setOpening(true);
		this.isNew = false;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream())))
		{
			String text;
			while ((text=reader.readLine()) != null)
			{
				textArea.append(text+"\n");
			}
		}
		finally
		{
			textArea.setCaretPosition(0);
			this.getTextArea().setOpening(false);
		}
	}
		
	public String getEncoding(File f)
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
	
	public void showSpecifiedCharsetDialog(File file)
	{
		final Frame parent = (Frame)(SwingUtilities.windowForComponent(this));
		/*
		 * show JDialog:
		 */
		final JDialog dialog = new JDialog(parent,"Open file (charset)",true);
		dialog.setLayout(new GridLayout(4,1,0,0));
		dialog.getContentPane().setBackground(Color.WHITE);
		MyPanel P1 = new MyPanel(MyPanel.LEFT);
		P1.add(new MyLabel("Open file by specifying the charset:"));
		dialog.add(P1);
		final JComboBox<String> comboBox = createCharsetComboBox();
		comboBox.setPreferredSize(new Dimension(210, comboBox.getSize().height));
		MyPanel P2 = new MyPanel(MyPanel.CENTER);
		P2.add(comboBox);
		dialog.add(P2);
		MyPanel P3 = new MyPanel(MyPanel.CENTER);
		final MyTextField tf = new MyTextField(15);
		tf.setPreferredSize(new Dimension(125,26));
		if (file != null)
		{
			tf.setText(file.getPath());
		}
		P3.add(tf);
		MyButton choose = new MyButton("?")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				File file = FileChooser.showPreferredFileDialog(parent, FileChooser.OPEN, new String[0]);
				if (file != null) tf.setText(file.getPath());
			}
		};
		choose.setPreferredSize(new Dimension(26,26));
		P3.add(choose);
		dialog.add(P3);
		MyPanel P4 = new MyPanel(MyPanel.CENTER);
		P4.add(new MyButton("Open")
		{
			{
				dialog.getRootPane().setDefaultButton(this);
			}
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				dialog.setVisible(false);
				File src = new File(tf.getText());
				dialog.dispose();
				/*
				 * open
				 */
				Tab newTab;
				if (src.equals(Tab.this.file))
				{
					newTab = Tab.this;
				}
				else
				{
					newTab = Tab.getNewTab();
				}
				if (!MainPanel.getAllTab().contains(newTab))
				{
					MainPanel.add(newTab);
				}
				newTab.open(src, (String)(comboBox.getSelectedItem()));				
			}
		});
		P4.add(new MyButton("Cancel")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				dialog.dispose();
			}
		});
		dialog.add(P4);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}
	
	public double getShowRate()
	{
		String text = this.textArea.getText();
		Font currentFont = this.textArea.getFont();
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
	
	/*
	 * createWatcher: create everytime after a file has been opened
	 */
	public FileWatcher createFileWatcher(final File file)
	{
		return new FileWatcher(file)
		{
			@Override
			public void fileChanged(WatchEvent ev)
			{
				ArrayList<String> strs = new ArrayList<>(3);
				strs.add("Save this file again");
				strs.add("Ignore");
				if (file.exists())
				{
					strs.add("Load the current version");
				}
				String[] option = strs.toArray(new String[strs.size()]);
				String chosen = (String)JOptionPane.showInputDialog(SwingUtilities.windowForComponent(Tab.this), "The file " + Tab.this.file.getPath() + " has been amended externally.", "Warning", JOptionPane.WARNING_MESSAGE, null, option, option[0]);
				if (option[0].equals(chosen))
				{
					//save
					try
					{
						Tab.this.save(Tab.this.file, true);
					}
					catch (IOException ex)
					{
						exception(ex);
					}
				}
				else if (option[1].equals(chosen)||(chosen == null))
				{
					//ignore
					Tab.this.setSaved(false);
				}
				else if (option.length == 3)
				{
					if (option[2].equals(chosen))
					{
						//load
						Tab.this.open(Tab.this.file);
					}
				}
			}
		};
	}
	
	public File getFile()
	{
		return this.file;
	}
	
	public void setFile(final File file)
	{
		this.file = file;
		this.fileLabel.setFile(file);
		MainPanel.getInstance().updateTabName(this);
		this.watcher = this.createFileWatcher(file);
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				//save recent files
				Set<String> filePaths = new LinkedHashSet<>();
				filePaths.add(file.getPath());
				//re-add old files
				String recentFiles = getConfig("recentFiles");
				if (recentFiles != null)
				{
					Collections.addAll(filePaths, recentFiles.split("\n"));
				}
				java.util.List<String> filePathList = new ArrayList<>(filePaths).subList(0,Math.min(10,filePaths.size()));
				StringBuilder builder = new StringBuilder();
				for (String path: filePathList)
				{
					builder.append("\n"+path);
				}
				writeConfig("recentFiles", builder.toString());
			}
		};
		(new Thread(runnable)).start();
	}
	
	public FileLabel getFileLabel()
	{
		return this.fileLabel;
	}
	
	public MyTextArea getTextArea()
	{
		return this.textArea;
	}
	
	public JLayer<? extends JComponent> getLayer()
	{
		return this.layer1;
	}
	
	public MyUmbrellaLayerUI getLayerUI()
	{
		return this.layerUI;
	}
	
	public UndoDialog getUndoDialog()
	{
		return this.undoDialog;
	}
	
	public MyLabel getTabLabel()
	{
		return this.tabLabel;
	}
	
	public JPanel getTabPanel()
	{
		return this.tabPanel;
	}
	
	public JPopupMenu getTabPopupMenu()
	{
		return this.tabPopup;
	}
	
	public FileWatcher getFileWatcher()
	{
		return this.watcher;
	}
	
	public boolean isSaved()
	{
		return this.isSaved;
	}
	
	public void setSaved(boolean isSaved)
	{
		boolean oldIsSaved = this.isSaved;
		this.isSaved = isSaved;
		if (oldIsSaved ^ this.isSaved)
		{
			//changed
			MainPanel.getInstance().updateTabName(this);
		}
	}
	
	public static JComboBox<String> createCharsetComboBox()
	{
		JComboBox<String> comboBox = new JComboBox<>(new Vector<String>(Charset.availableCharsets().keySet()));
		comboBox.setSize(new Dimension(150,26));
		comboBox.setBackground(Color.WHITE);
		comboBox.setFont(f13);
		return comboBox;
	}
	
	public static File createTempSaveFile(File file)
	{
		int i = 0;
		File tmp;
		while ((tmp = new File(file.getParent(), file.getName()+"_refluxedit_save_backup_" + i)).exists())
		{
			i++;
		}
		return tmp;
	}
	
	static class MyBlackLinePanel extends JPanel
	{
		private int type;
		public MyBlackLinePanel(int type)
		{
			super(new FlowLayout());
			this.type = type;
		}
	
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if (Tab.countWords)
			{
				int width = this.getWidth();
				int height = this.getHeight();
				switch (type)
				{
					case 0: //left: wordCount
					g.setColor(Color.BLACK);
					g.drawLine(0,0,width,0);
					break;
					
					case 1: //right: fileLabel
					g.setColor(Color.BLACK);
					g.drawLine(0,0,0,height);
					g.drawLine(0,0,width,0);
					break;
					
					default:
					break;
				}
			}
		}
	}
	
	class CountLabel extends MyLabel implements MouseListener
	{
		boolean isWordCount; //wordCount or charCount
		CountLabel(int number, boolean isWordCount)
		{
			super("");
			this.isWordCount = isWordCount;
			this.setCountInfo(number, isWordCount);			
			this.addMouseListener(this);
		}
		
		private void setCountInfo(int number, boolean isWordCount)
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
			isWordCount = !isWordCount;
			Tab.this.updateCount();
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
	
	class MyPopupMenuItem extends JMenuItem implements ActionListener
	{
		private int x;
		MyPopupMenuItem(String text, int x)
		{
			super(text);
			this.setFont(f13);
			this.setBackground(Color.WHITE);
			this.addActionListener(this);
			this.x = x;
		}
		
		MyPopupMenuItem(String text, String icon, int x)
		{
			this(text,x);
			try
			{
				this.setIcon(icon(icon));
			}
			catch (Exception ex)
			{
				//fail sliently
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent ev)
		{
			switch (x)
			{
				case -11: //close
				{
					if (Tab.this.isSaved())
					{
						MainPanel.close(Tab.this);
					}
					else
					{
						int option = JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(Tab.this),"The file hasn't been saved yet.\nWould you like to save it?","Confirm close",JOptionPane.YES_NO_CANCEL_OPTION);
						if (option == JOptionPane.YES_OPTION)
						{
							File dest = Tab.this.file;
							if (dest == null)
							{
								dest = FileChooser.showPreferredFileDialog((Frame)SwingUtilities.windowForComponent(Tab.this),FileChooser.SAVE,new String[0]);
							}
							if (dest != null)
							{
								try
								{
									Tab.this.save(dest,false);
									MainPanel.close(Tab.this);
								}
								catch (Exception ex)
								{
									exception(ex);
									return;
								}
							}
						}
						else if (option == JOptionPane.NO_OPTION)
						{
							MainPanel.close(Tab.this);
							break;
						}
						else break;
					}
				}
				break;
				
				case -13: //save
				if (Tab.this.file != null)
				{
					try
					{
						Tab.this.save(Tab.this.file,true);
						break;
					}
					catch (Exception ex)
					{
						exception(ex);
					}
				}
				
				case -12: //save as
				File dest = FileChooser.showPreferredFileDialog((Frame)SwingUtilities.windowForComponent(Tab.this),FileChooser.SAVE,new String[0]);		
				if (dest != null)
				{
					try
					{
						Tab.this.save(dest,true);
					}
					catch (Exception ex)
					{
					}
				}
				break;
				
				case -14: //new tab
				new MyListener(1).actionPerformed(ev);
				break;				
			}
		}
	}
}
