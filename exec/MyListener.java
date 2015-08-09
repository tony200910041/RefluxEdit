/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package exec;

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
import java.util.regex.*;
import java.text.*;
import java.net.*;
import java.nio.file.*;
import java.nio.charset.*;
import exec.*;
import myjava.gui.*;
import myjava.util.*;
import myjava.gui.common.*;
import myjava.gui.option.*;
import static exec.SourceManager.*;
import static myjava.gui.ExceptionDialog.*;
import static myjava.util.StaticUtilities.*;
import static exec.RefluxEdit.*;

public class MyListener extends AbstractAction implements MouseListener, VersionConstants, Resources
{
	private static final Clipboard clipbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
	private static final boolean isWindows = Resources.LAF.contains("windows");
	/*
	 * temporary variables 
	 */
	private int x;
	public MyListener(int x)
	{
		this.x = x;
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		update(ev);
	}
	
	@Override
	public void mouseReleased(MouseEvent ev)
	{
		update(ev);
	}
	
	void update(AWTEvent ev)
	{
		/*
		 * retrieve common instances
		 */
		final RefluxEdit w = RefluxEdit.getInstance();
		final Tab tab = MainPanel.getSelectedTab();
		final MyTextArea textArea = tab.getTextArea();
		final File file = tab.getFile();
		final UndoManager undoManager = textArea.getUndoManager();
		/*
		 * next one: 60
		 * free: 36, 37, 42, 48
		 * 
		 * 
		 * 
		 * main part
		 */
		outswitch:
		switch (x)
		{			
			case -1:
			case 1: //new
			{
				Tab _tab = new Tab();
				MainPanel.add(_tab);
				MainPanel.getInstance().updateTabName(_tab);
				MainPanel.setSelectedComponent(_tab);
			}
			break;
			
			case 2: //open file
			{
				File src = FileChooser.showPreferredFileDialog(w,FileChooser.OPEN,new String[0]);
				if (src != null)
				{
					if (src.exists())
					{
						MainPanel.openFile(src);
					}
					else
					{
						error("The file " + src.getPath() + " does not exist!");
					}
				}
			}
			break;
			
			case 3: //open quick
			{
				String input = JOptionPane.showInputDialog(w, "Enter the path, or the URL:", "Input", JOptionPane.QUESTION_MESSAGE);
				if (input != null)
				{
					if (!input.isEmpty())
					{
						if (input.equals("animation"))
						{
							/*
							 * must run on non-Event Dispatch Thread
							 * great care should be taken
							 * intentionally hidden
							 */
							(new Thread()
							{
								@Override
								public void run()
								{
									String[] words = new String[]{"R","e","f","l","u","x","E","d","i","t","RefluxEdit " + VersionConstants.VERSION_NO};
									JFrame tabFrame = new JFrame();
									tabFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
									tabFrame.setUndecorated(true);
									tabFrame.setLayout(new BorderLayout());
									tabFrame.setAlwaysOnTop(true);
									LetterTab centerTab = null;
									tabFrame.setVisible(true);
									for (String s: words)
									{
										try
										{
											if (centerTab != null)
											{
												tabFrame.remove(centerTab);
											}
											centerTab = new LetterTab(s);
											tabFrame.add(centerTab, BorderLayout.CENTER);
											centerTab.revalidate();
											centerTab.repaint();
											centerTab.play();
										}
										catch (InterruptedException ex)
										{
										}
									}
									try
									{
										this.sleep(1500);
									}
									catch (InterruptedException ex)
									{
									}
									tabFrame.setVisible(false);
									tabFrame.dispose();
								}
							}).start();
						}
						else
						{
							File src = new File(input);
							if (src.exists())
							{
								MainPanel.openFile(src);
							}
							else
							{
								try
								{
									URL url = new URL(input);
									Tab _new = Tab.getNewTab();
									_new.open(url);
									MainPanel.add(_new);
									MainPanel.getInstance().updateTabName(_new);
									MainPanel.setSelectedComponent(_new);
									int option = JOptionPane.showConfirmDialog(w,"Finished reading the URL.\nWould you like to save the text?","Confirm save",JOptionPane.YES_NO_OPTION);
									if (option == JOptionPane.YES_OPTION)
									{
										(new MyListener(4)).update(ev);
									}
								}
								catch (MalformedURLException ex)
								{
									error("The path " + input + " is invalid!");
								}
								catch (IOException ex)
								{
									exception(ex);
								}
							}
						}
					}
				}
			}
			break;
			 
			case 4: //save as
			{
				File dest = FileChooser.showPreferredFileDialog(w,FileChooser.SAVE,new String[0]);
				if (dest != null)
				{
					try
					{
						tab.save(dest,true);
					}
					catch (IOException ex)
					{
						exception(ex);
					}
				}
			}
			break;
			
			case 5: //save
			File dest;
			if (file == null)
			{
				dest = FileChooser.showPreferredFileDialog(w,FileChooser.SAVE,new String[0]);
			}
			else
			{
				dest = file;
			}
			if (dest != null)
			{
				try
				{
					tab.save(dest,true);
				}
				catch (IOException ex)
				{
					exception(ex);
				}
			}
			break;
			
			case 6: //close
			w.confirmClose();
			break;
			
			case 7: //undo
			if (textArea.isEditable())
			{
				int caret = textArea.getCaretPosition();
				textArea.setAutoBackup(false);
				undoManager.undo();
				textArea.setAutoBackup(true);
				//calculate new caret position
				try
				{
					textArea.setCaretPosition(caret);
				}
				catch (Exception ex)
				{
					textArea.setCaretPosition(0);
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
				int caret = textArea.getCaretPosition();
				textArea.setAutoBackup(false);
				undoManager.redo();
				textArea.setAutoBackup(true);
				try
				{
					textArea.setCaretPosition(caret);
				}
				catch (Exception ex)
				{
					textArea.setCaretPosition(0);
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
			textArea.copy();
			break;
			
			case 11: //cut
			if (textArea.isEditable())
			{
				textArea.cut();
				tab.setSaved(false);
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
			textArea.paste();
			break;
			
			case 14: //paste on next line
			String data;
			try
			{
				data = clipbrd.getData(DataFlavor.stringFlavor).toString();
			}
			catch (Exception ex)
			{
				break;
			}
			textArea.insert("\n" + data, textArea.getCaretPosition());
			break;
			
			case 15: //delete
			textArea.replaceSelection(null);
			break;
			
			case 16: //about RefluxEdit
			String s1 = "RefluxEdit " + VERSION_NO + BETA_STRING + BETA_NO + REV_STRING + REV_NO + " -- a lightweight plain text editor written in Java.\nBy tony200910041, http://tony200910041.github.io/RefluxEdit/\nDistributed under MPL 2.0.\nuser.home: " + System.getProperty("user.home") + "\nYour operating system is " + System.getProperty("os.name") + " (" + System.getProperty("os.version") + "), " + System.getProperty("os.arch") + ".\n\nIcon sources: http://www.iconarchive.com and LibreOffice.";
			String s2 = "About RefluxEdit " + VERSION_NO;
			try
			{
				JOptionPane.showMessageDialog(w, s1, s2, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(MyListener.class.getResource("/SRC/Duke.gif")));
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(w, s1, s2, JOptionPane.INFORMATION_MESSAGE);
			}
			break;
			
			case 17: //editable
			((Component)(ev.getSource())).requestFocusInWindow(); //must be invoked, bug #00032
			if (textArea.isEditable())
			{
				MyTextArea.setTextEditable(false);
				textArea.setBackground(new Color(245,245,245));
				writeConfig("isEditable", "false");
			}
			else
			{
				MyTextArea.setTextEditable(true);
				textArea.setBackground(Color.WHITE);
				writeConfig("isEditable", "true");
				textArea.requestFocusInWindow();
			}
			MainPanel.updateAllTab();
			break;
			
			case 18: //increase indentation
			case 19: //decrease indentation
			if (textArea.isEditable())
			{
				int i = textArea.getSelectionStart();
				int j = textArea.getSelectionEnd();
				int start = Math.min(i,j);
				int end = Math.max(i,j);
				String indentString = MyIndentFilter.getIndentString();
				try
				{
					int start_line = textArea.getLineOfOffset(start);
					int end_line = textArea.getLineOfOffset(end);
					for (int x=start_line; x<=end_line; x++)
					{
						int offset = textArea.getLineStartOffset(x);
						if (this.x == 18)
							textArea.insert(indentString,offset);
						else
						{
							String _char = textArea.getText(offset,1);
							if (_char.equals(indentString)||_char.equals(" "))
							{
								textArea.replaceRange(null,offset,offset+1);
							}
						}
						tab.setSaved(false);
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
				String selected = textArea.getSelectedText();
				String buffer;
				int lineCount;
				if (selected == null)
				{
					buffer = textArea.getText();
					lineCount = textArea.getLineCount();
				}
				else
				{
					buffer = selected;
					lineCount = buffer.split("\n").length;
				}
				if (buffer != null)
				{
					JOptionPane.showMessageDialog(w, "Number of words (separated by space): " + wordCount(buffer) + "\nNumber of characters: " + charCount(buffer) + "\nNumber of rows: " + lineCount, "Word count", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			break;
			
			case 23: //regex matcher
			RegexDialog.showRegexDialog(w);
			break;
			
			case 24: //search and replace
			{
				//check if show dialog:
				final String text = textArea.getText();
				if ((text != null)&&(!text.isEmpty()))
				{
					SearchDialog.showDialog(w,textArea);
				}
			}
			break;
			
			case 26: //upper case
			{
				String selected = textArea.getSelectedText();
				if (selected != null)
				{
					textArea.replaceSelection(selected.toUpperCase());
				}
				else
				{
					textArea.setText(textArea.getText().toUpperCase());
				}
			}
			break;
			
			case 27: //lower case
			{
				String selected = textArea.getSelectedText();
				if (selected != null)
				{
					textArea.replaceSelection(selected.toLowerCase());
				}
				else
				{
					textArea.setText(textArea.getText().toLowerCase());
				}
			}
			break;
			
			case 28: //invert case
			{
				String selected = textArea.getSelectedText();
				if (selected != null)
				{
					textArea.replaceSelection(toInvertCase(selected));
				}
				else
				{
					textArea.setText(toInvertCase(textArea.getText()));
				}
			}
			break;
			
			case 29: //clipboard listener
			{
				ClipboardDialog.getInstance().setVisible(true);
			}
			break;
			
			case 30: //10 equal signs
			textArea.insert("\n==========\n", textArea.getCaretPosition());
			break;
			
			case 31: //four spaces
			textArea.insert("    ", textArea.getCaretPosition());
			break;
			
			case 32: //random words
			if (textArea.isEditable())
			{
				String input = JOptionPane.showInputDialog(w, "Please enter the number of words you want to generate:", "Input", JOptionPane.QUESTION_MESSAGE);
				if (input != null)
				{
					final int[] number = new int[1];
					try
					{
						number[0] = Integer.parseInt(input);
					}
					catch (NumberFormatException ex)
					{
						JOptionPane.showMessageDialog(w, "Please enter a positive integer!", "Error", JOptionPane.ERROR_MESSAGE);
						break outswitch;
					}
					if (number[0] >= 1000000)
					{
						int option = JOptionPane.showConfirmDialog(w, "Generating 1,000,000 words or more may take very long time.\nContinue?", "Confirm", JOptionPane.YES_NO_OPTION);
						if (option != JOptionPane.YES_OPTION) break outswitch;
					}
					final RandomProgress prog = new RandomProgress(w,1,number[0]);
					//now start to generate
					final SwingWorker<String, String> worker = new SwingWorker<String, String>()
					{
						int currentProcess;
						@Override
						public String doInBackground()
						{
							StringBuilder text = new StringBuilder(1000);
							/*
							 * modify textArea consecutively, disable auto backup first
							 */
							textArea.setAutoBackup(false);
							textArea.getUndoManager().backup();
							for (currentProcess=0; currentProcess<number[0]; currentProcess++)
							{
								if (this.isCancelled())
								{
									return text.toString();
								}
								else
								{
									int random = (int)(Math.random()*9+1);
									StringBuilder buffer = new StringBuilder(100);
									for (int i=1; i<=random; i++)
									{
										buffer.append(toLetter((int)(Math.random()*26+1)));
									}
									text.append(buffer.toString()+" ");
									if (currentProcess%50==0)
									{
										this.publish(text.toString());
										text = new StringBuilder();
									}
								}
							}
							return text.toString();
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
							tab.setSaved(false);
							/*
							 * re-enable backup
							 */
							textArea.setAutoBackup(true);
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
								//misuse of reflection
								throw new InternalError();
							}
							textArea.getUndoManager().backup();
							textArea.setAutoBackup(true);
							tab.setSaved(false);
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
			{
				Object[] keywordJava = new Object[]{"public static void main(String[] args) {", "import java.awt.*;\nimport java.awt.event.*;\nimport javax.swing.*;", "SwingUtilities.invokeLater(new Runnable() {", "class MyListener extends MouseAdapter {", "throw new Exception();", "Integer.parseInt(", "Double.parseDouble(", "JOptionPane.showMessageDialog(", "JOptionPane.showInputDialog(", "public void mouseReleased(MouseEvent ev) {", "public void actionPeformed(ActionEvent ev) {", "public void windowClosing(WindowEvent ev) {", "System.out.println();"};
				String option = (String)JOptionPane.showInputDialog(w, "Please choose one:", "Keyword (Java)", JOptionPane.QUESTION_MESSAGE, null, keywordJava, keywordJava[0]);
				if (option != null)
				{
					textArea.insert(option, textArea.getCaretPosition());
				}
			}
			break;
			
			case 34: //keyword, html
			{
				Object[] keywordHTML = new Object[]{"<a target=\"_blank\" href=\"\"></a>", "<img alt=\"\" src=\"\"></img>", "<font face=\"\"></font>", "<br>"};
				String option = (String)JOptionPane.showInputDialog(w, "Please choose one:", "Keyword (html)", JOptionPane.QUESTION_MESSAGE, null, keywordHTML, keywordHTML[0]);
				if (option != null)
				{
					textArea.insert(option, textArea.getCaretPosition());
				}
			}
			break;
			
			case 35: //delete blank lines
			if (textArea.isEditable())
			{
				Boolean option = BlankLineDialog.showDialog(w);
				if (option != null)
				{
					if (option.booleanValue())
					{
						String text = textArea.getText().replaceAll("\n+", "\n").replaceAll("\n[\\s\\t]*\n", "\n");
						if (text.startsWith("\n")) text = text.substring(1);
						textArea.setText(text);
					}
					else
					{
						String text = textArea.getText().replaceAll("\n+", "\n");
						if (text.startsWith("\n")) text = text.substring(1);
						textArea.setText(text);
					}
				}
			}
			else
			{
				cannotEdit();
			}
			break;
			
			case 38: //print
			{
				/*
				 * must invoke setAlwaysOnTop(false); before printing
				 * otherwise the print dialog will be blocked
				 */
				boolean isAlwaysOnTop = w.isAlwaysOnTop();
				w.setAlwaysOnTop(false);
				try
				{
					boolean printed = textArea.print();
				}
				catch (PrinterException ex)
				{
					exception(ex);
				}
				finally
				{
					w.setAlwaysOnTop(isAlwaysOnTop);
				}
			}
			break;
			
			case 39: //other options
			OptionDialog.showDialog(w);
			break;
			
			case 40: //color chooser
			{
				String value = ColorDialog.showColorDialog(w);
				if (!value.isEmpty())
				{
					textArea.insert(value, textArea.getCaretPosition());
				}
			}
			break;
			
			case 41: //base converter
			{
				String value = BaseConverter.showDialog(w);
				if (!value.isEmpty())
				{
					textArea.insert(value, textArea.getCaretPosition());
				}
			}
			break;
						
			case 43: //export as image
			{
				final JDialog exportImage = new JDialog(w, "Export to image", true);
				exportImage.setLayout(new GridLayout(2,1,0,0));
				/*
				 * choose file:
				 */
				MyPanel exportP1 = new MyPanel(MyPanel.LEFT);
				final MyTextField exportTF = new MyTextField(38);
				exportP1.add(new MyLabel("Export to: "));
				exportP1.add(exportTF);
				MyButton exportB1 = new MyButton("?");
				exportB1.setPreferredSize(new Dimension(20,20));
				exportP1.add(exportB1);
				/*
				 * "Extra gap" spinner:
				 */
				MyPanel exportP2 = new MyPanel(MyPanel.CENTER);
				exportP2.add(new MyLabel("Extra gap (in pixels):"));
				final JSpinner exportSpin = new JSpinner(new SpinnerNumberModel(5,0,100,1));
				exportSpin.setFont(new Font(f13.getName(),Font.PLAIN,14));
				exportSpin.setEnabled(false);
				exportP2.add(exportSpin);
				exportP2.add(new MyLabel("Format:"));
				/*
				 * format:
				 */
				String[] formatList = ImageIO.getWriterFormatNames();
				Set<String> formatSet = new LinkedHashSet<String>();
				for (String name: formatList)
				{
					formatSet.add(name.toLowerCase());
				}
				final JComboBox<String> exportFormat = new JComboBox<>(new Vector<String>(formatSet));
				formatList = null;
				formatSet = null;
				exportFormat.setFont(new Font(f13.getName(), Font.PLAIN, 12));
				exportFormat.setBackground(Color.WHITE);
				exportFormat.setSelectedItem("png");
				exportFormat.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						String path = exportTF.getText();
						String filename = (new File(path)).getName().toLowerCase();
						String format = exportFormat.getSelectedItem().toString();
						if (path.contains(".")&&(!filename.endsWith(format)))
						{
							exportTF.setText(path.substring(0, path.lastIndexOf(".")) + "." + format);
						}
						else if (!filename.endsWith(format))
						{
							exportTF.setText(path + "." + format);
						}
					}
				});
				exportP2.add(exportFormat);
				/*
				 * fast/stable radio buttons
				 */
				final MyRadioButton fast = new MyRadioButton("Fast",true);
				final MyRadioButton stable = new MyRadioButton("Stable",false);
				MyButtonGroup group = new MyButtonGroup(fast,stable);
				exportP2.add(fast);
				exportP2.add(stable);
				fast.setToolTipText("<html><font size=\"4\"><b>Fast export</b></font><br>Very fast, but may cause out-of-memory error. No extra gap.</html>");
				stable.setToolTipText("<html><font size=\"4\"><b>Stable export</b></font><br>Stable exportation, but is significantly slower.</html>");
				/*
				 * buttons
				 */
				MyButton exportB2 = new MyButton("Export");
				exportP2.add(exportB2);
				MyButton exportB3 = new MyButton("Cancel");
				exportP2.add(exportB3);
				/*
				 * build dialog
				 */
				exportImage.add(exportP1);
				exportImage.add(exportP2);
				exportImage.pack();
				exportImage.setLocationRelativeTo(w);
				ActionListener exportListener = new ActionListener()
				{					
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						final String formatName = exportFormat.getSelectedItem().toString();
						switch (((JButton)(ev.getSource())).getText())
						{
							case "?":
							File f1 = FileChooser.showPreferredFileDialog(w, FileChooser.SAVE, formatName);
							if (f1 != null)
							{
								exportTF.setText(f1.getPath());
							}
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
							final int size = Short.parseShort(exportSpin.getValue().toString());
							exportImage.dispose();
							if (dest == null) return;
							if (dest.isEmpty()) return;
							/*
							 * export start
							 * two ways: fast and stable
							 */
							if (fast.isSelected())
							{
								/*
								 * fast way: may not succeed due to memory restriction
								 */
								try
								{
									BufferedImage image = new BufferedImage(textArea.getWidth(),textArea.getHeight(),BufferedImage.TYPE_INT_ARGB);
									Graphics2D g2d = image.createGraphics();
									textArea.paintAll(g2d);
									g2d.dispose();
									ImageIO.write(image,"png",new File(dest));
									JOptionPane.showMessageDialog(w,"Done.","Done",JOptionPane.INFORMATION_MESSAGE);
								}
								catch (Throwable ex)
								{
									//include OutOfMemoryError
									exception(ex);
									return;
								}
							}
							else
							{
								/*
								 * stable way: higher chance to succeed, but significantly slower
								 */
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
								final RandomProgress exportProg = new RandomProgress(w, 0, lines.size());
								final Thread exportThr = new Thread()
								{
									@Override
									public void run()
									{
										Font font = textArea.getFont();
										FontMetrics f = new Canvas().getFontMetrics(font);
										int height = f.getHeight()+size; //default size=5
										BufferedImage image = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
										Graphics2D g;
										//draw each line
										for (String str: lines)
										{
											int width = f.stringWidth(str)+1;
											//lineImage: single line text
											BufferedImage lineImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
											g = lineImage.createGraphics();
											g.setFont(font);
											g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
											g.setColor(Color.WHITE);
											g.fillRect(0,0,width,height);
											g.setColor(Color.BLACK);
											g.drawString(str,0,height-size);
											g.dispose();
											int maxWidth = Math.max(image.getWidth(),width);
											int actualHeight = image.getHeight()+height;
											BufferedImage newImage = new BufferedImage(maxWidth,actualHeight,BufferedImage.TYPE_INT_RGB);
											g = newImage.createGraphics();
											g.setColor(Color.WHITE);
											g.fillRect(0,0,maxWidth,actualHeight);
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
											ImageIO.write(image, formatName, new File(dest));
										}
										catch (IOException ex)
										{
										}
										finally
										{
											//release resources
											font = null;
											f = null;
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
							}
							break;
							
							case "Cancel":
							exportImage.dispose();
							return;
						}
					}
				};
				exportB1.addActionListener(exportListener);
				exportB2.addActionListener(exportListener);
				exportB3.addActionListener(exportListener);
				exportImage.setVisible(true);
			}
			break;
			
			case 44: //character count
			{
				String selected = textArea.getSelectedText();
				String buffer;
				if (selected != null)
				{
					buffer = selected;
				}
				else
				{
					buffer = textArea.getText();
				}
				if (buffer != null)
				{
					if (!buffer.isEmpty())
					{
						CharCountDialog.showDialog(w,buffer);
					}
					else return;
				}
				else return;
			}
			break;
			
			case 45: //unicode character
			{
				final JDialog unicodeDialog = new JDialog(w, "Insert unicode character", true);
				unicodeDialog.setLayout(new BorderLayout());
				MyPanel unicodeP0 = new MyPanel(MyPanel.LEFT);
				unicodeP0.setLayout(new GridLayout(2,1,0,0));
				MyPanel unicodeP1 = new MyPanel(MyPanel.LEFT);
				unicodeP1.add(new MyLabel("Enter the unicode e.g. 2190, 00F7 etc."));
				unicodeP0.add(unicodeP1);
				MyPanel unicodeP2 = new MyPanel(MyPanel.CENTER);
				final MyTextField unicodeField = new MyTextField(25);
				unicodeP2.add(unicodeField);
				unicodeP0.add(unicodeP2);
				unicodeDialog.add(unicodeP0, BorderLayout.PAGE_START);
				MyPanel unicodeP3 = new MyPanel(MyPanel.CENTER);
				final JLabel unicodeLbl = new JLabel("N/A");
				unicodeLbl.setFont(textArea.getFont().deriveFont(50f));
				unicodeP3.add(unicodeLbl);
				unicodeDialog.add(unicodeP3, BorderLayout.CENTER);
				MyPanel unicodeP4 = new MyPanel(MyPanel.CENTER);
				MyButton unicodeB1 = new MyButton("Add")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						unicodeDialog.setVisible(false);
						try
						{
							if (textArea.isEditable())
							{
								textArea.insert(toChar(unicodeField.getText())+"", textArea.getCaretPosition());
								tab.setSaved(false);
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
			}
			break;
			
			case 46: //char to unicode value
			{
				final JDialog charDialog = new JDialog(w, "Insert unicode value", true);
				charDialog.setLayout(new BorderLayout());
				MyPanel charP0 = new MyPanel(MyPanel.LEFT);
				charP0.setLayout(new GridLayout(2,1,0,0));
				MyPanel charP1 = new MyPanel(MyPanel.LEFT);
				charP1.add(new MyLabel("Paste the character here: e.g. \u2190, \u00F7"));
				charP0.add(charP1);
				MyPanel charP2 = new MyPanel(MyPanel.CENTER);
				final MyTextField charField = new MyTextField(25);
				charP2.add(charField);
				charP0.add(charP2);
				charDialog.add(charP0, BorderLayout.PAGE_START);
				MyPanel charP3 = new MyPanel(MyPanel.CENTER);
				final JLabel charLbl = new JLabel("N/A");
				charLbl.setFont(f13.deriveFont(50f));
				charP3.add(charLbl);
				charDialog.add(charP3, BorderLayout.CENTER);
				MyPanel charP4 = new MyPanel(MyPanel.CENTER);
				MyButton charB1 = new MyButton("Add")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						charDialog.setVisible(false);
						try
						{
							if (textArea.isEditable())
							{
								textArea.insert(toUnicodeValue(charField.getText().charAt(0)), textArea.getCaretPosition());
								tab.setSaved(false);
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
						String text = charField.getText();
						if (text != null)
						{
							if ((text.length()!=1)&&(text.length()!=0))
							{
								charField.setBackground(new Color(255,133,133));
								charLbl.setText("N/A");
								return;
							}
							else if (text.length()==1)
							{
								charLbl.setText(toUnicodeValue(charField.getText().charAt(0)));
							}
							else if (text.length()==0)
							{
								charLbl.setText("N/A");
							}
						}
						charField.setBackground(Color.WHITE);
					}
				});
				String text = textArea.getSelectedText();
				if (text != null)
				{
					if (text.length() == 1)
					{
						charField.setText(text);
					}
				}
				charDialog.pack();
				charDialog.setLocationRelativeTo(w);
				charDialog.setMinimumSize(new Dimension(315,205));
				charDialog.setVisible(true);
			}
			break;
			
			case 47: //goto line
			{
				Integer line = LineChooser.showDialog(w,textArea);
				if (line != null)
				{
					int input = line.intValue()-1;
					try
					{
						int start = textArea.getLineStartOffset(input);
						int end = textArea.getLineEndOffset(input);
						textArea.select(start, end);
					}
					catch (BadLocationException ex)
					{
						throw new InternalError();
					}
				}
			}
			break;
			
			case 48: //visit GitHub
			try
			{
				Desktop.getDesktop().browse(new URI("https://github.com/tony200910041/RefluxEdit/releases"));
			}
			catch (Exception ex)
			{
			}
			break;
			
			case 49: //insert space between character
			if (textArea.isEditable())
			{
				String selected = textArea.getSelectedText();
				if (selected != null)
				{
					int start = textArea.getSelectionStart();
					String inserted = insertSpaces(selected);
					textArea.replaceSelection(inserted);
					textArea.select(start, start+inserted.length());
				}
				else
				{					
					textArea.setText(insertSpaces(textArea.getText()));
				}
			}
			else
			{
				cannotEdit();
			}
			break;
			
			case 50: //reverse all character
			if (textArea.isEditable())
			{
				String text_a = textArea.getText();
				String text_s = textArea.getSelectedText();
				if (text_s != null)
				{
					textArea.replaceSelection(reverse(text_s));
				}
				else
				{					
					textArea.setText(reverse(text_a));
				}
			}
			else
			{
				cannotEdit();
			}
			break;
			
			case 51: //open by charset
			tab.showSpecifiedCharsetDialog(file);
			break;
			
			case 52: //undo/redo list dialog
			{
				UndoDialog undoDialog = tab.getUndoDialog();
				undoDialog.resetUndoDialogList();
				undoDialog.setLocationRelativeTo(w);
				undoDialog.setVisible(true);
			}
			break;
			
			case 53: //compile
			MyCompileToolBar.getInstance().startCompile();
			break;
			
			case 54: //escape string
			{
				String selected = textArea.getSelectedText();
				try
				{
					if (selected != null)
					{
						textArea.replaceSelection(escape(selected));
					}
					else
					{
						textArea.setText(escape(textArea.getText()));
					}
				}
				catch (IOException ex)
				{
					exception(ex);
				}
			}
			break;
			
			case 55: //unescape string
			{
				String selected = textArea.getSelectedText();
				try
				{
					if (selected != null)
					{
						textArea.replaceSelection(unescape(selected));
					}
					else
					{
						textArea.setText(unescape(textArea.getText()));
					}
				}
				catch (IOException ex)
				{
					exception(ex);
				}
			}
			break;
			
			case 56: //new file from clipboard
			{
				Tab _new = Tab.getNewTab();
				if (!MainPanel.getAllTab().contains(_new))
				{				
					MainPanel.add(_new);
				}
				MainPanel.getInstance().updateTabName(_new);
				MainPanel.setSelectedComponent(_new);
				_new.getTextArea().paste();
			}
			break;
			
			case 57: //new Java class
			{
				String text = ClassCreatorDialog.showDialog(w);
				if (text != null)
				{
					Tab _new = Tab.getNewTab();
					if (!MainPanel.getAllTab().contains(_new))
					{
						MainPanel.getInstance().addTab(_new);
					}
					_new.getTextArea().setText(text);
					MainPanel.setSelectedComponent(_new);
					int option = JOptionPane.showConfirmDialog(w,"A new class has been created.\nWould you like to save it?","Saving",JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION)
					{
						new MyListener(4).update(ev); //call save as
					}
				}
			}
			break;
			
			case 58: //about MPL
			MPLDialog.showDialog(w);
			break;
			
			case 59: //file browser
			{
				FileBrowserDialog dialog = FileBrowserDialog.getInstance();
				dialog.setVisible(!dialog.isVisible());
			}
			break;
			
			default:
			break;
		}
	}
	
	public static void cannotEdit()
	{
		JOptionPane.showMessageDialog(RefluxEdit.getInstance(), "Editing the text is DISABLED!\nPlease enable editing!", "Error", JOptionPane.WARNING_MESSAGE);
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
