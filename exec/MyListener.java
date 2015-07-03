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
import myjava.gui.*;
import myjava.util.*;
import myjava.gui.common.*;
import static exec.SourceManager.*;
import static myjava.gui.ExceptionDialog.*;
import static myjava.util.StaticUtilities.*;
import static exec.RefluxEdit.*;

public class MyListener extends MouseAdapter implements ActionListener, VersionConstants, Resources
{
	private static final Clipboard clipbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
	private static final boolean isWindows = UIManager.getLookAndFeel().getName().toLowerCase().contains("windows");
	/*
	 * temporary variables 
	 */
	private static String TMP1, TMP2, TMP3;
	private static int i,j,k,l,m,n;
	private int x;
	public MyListener(int x)
	{
		this.x = x;
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		update();
	}
	
	@Override
	public void mouseReleased(MouseEvent ev)		
	{
		update();
	}
	
	void update()
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
		 * next one: 56
		 * free: 47,29,36,37
		 */
		/*
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
						Tab _tab = Tab.getNewTab();
						MainPanel.add(_tab);
						_tab.open(src);
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
				String input = JOptionPane.showInputDialog(w, "Please enter the path:", "Input", JOptionPane.QUESTION_MESSAGE);
				if (input != null)
				{
					if (!input.isEmpty())
					{
						File src = new File(input);
						if (src.exists())
						{
							Tab _tab = Tab.getNewTab();
							MainPanel.add(_tab);
							_tab.open(src);
						}
						else
						{
							error("The file " + src.getPath() + " does not exist!");
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
			w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
			break;
			
			case 7: //undo
			if (textArea.isEditable())
			{				
				int caret = textArea.getCaretPosition();
				textArea.setAutoBackup(false);
				undoManager.undo();
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
				textArea.setSaved(false);
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
			String s1 = "RefluxEdit " + VERSION_NO + BETA_STRING + BETA_NO + REV_STRING + REV_NO + " -- a lightweight plain text editor written in Java.\nBy tony200910041, http://tony200910041.wordpress.com\nDistributed under MPL 2.0.\nuser.home: " + System.getProperty("user.home") + "\nYour operating system is " + System.getProperty("os.name") + " (" + System.getProperty("os.version") + "), " + System.getProperty("os.arch") + ".\n\nIcon sources: http://www.iconarchive.com and LibreOffice.";
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
			
			case 17: //editing
			if (textArea.isEditable())
			{
				MyTextArea.setGlobalProperties(false);
				textArea.setBackground(new Color(245,245,245));
				writeConfig("isEditable", "false");
			}
			else
			{
				MyTextArea.setGlobalProperties(true);
				textArea.setBackground(Color.WHITE);
				writeConfig("isEditable", "true");
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
						textArea.setSaved(false);
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
				if (selected == null)
				{
					buffer = textArea.getText();
				}
				else
				{
					buffer = selected;
				}
				if (buffer != null)
				{
					int count = wordCount(buffer);
					if (count == 0)
					{
						JOptionPane.showMessageDialog(w, "Number of words (separated by space): 0\nNumber of characters: 0\nNumber of rows: " + buffer.split("\n").length, "Word count", JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						JOptionPane.showMessageDialog(w, "Number of words (separated by space): " + count + "\nNumber of characters: " + charCount(buffer) + "\nNumber of rows: " + buffer.split("\n").length, "Word count", JOptionPane.INFORMATION_MESSAGE);
					}
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
				if (textArea.isEditable()&&(text != null))
				{
					if (!text.isEmpty())
					{
						final JDialog replace = new JDialog(w, "Search and Replace", false);
						replace.getContentPane().setBackground(Color.WHITE);
						replace.setLayout(new GridBagLayout());
						GridBagConstraints c = new GridBagConstraints();		
						/*
						 * original text:
						 */
						MyLabel la1 = new MyLabel("Original: ");
						final MyTextField wd1 = new MyTextField(20);
						c.gridx=0;
						c.gridy=0;
						c.weightx=0.2;
						c.insets=new Insets(5,5,5,5);
						c.fill=GridBagConstraints.NONE;
						replace.add(la1,c);
						c.gridx=1;
						c.gridy=0;
						c.weightx=0.8;
						c.fill=GridBagConstraints.HORIZONTAL;
						replace.add(wd1,c);
						/*
						 * replace text:
						 */							
						MyLabel la2 = new MyLabel("Replaced by: ");
						final MyTextField wd2 = new MyTextField(20);
						c.gridx=0;
						c.gridy=1;
						c.weightx=0.2;
						c.fill=GridBagConstraints.NONE;
						replace.add(la2,c);
						c.gridx=1;
						c.gridy=1;
						c.weightx=0.8;
						c.fill=GridBagConstraints.HORIZONTAL;
						replace.add(wd2,c);
						/*
						 * options
						 */
						final MyCheckBox regex = new MyCheckBox("Use regex",false);
						final MyCheckBox caseSensitive = new MyCheckBox("Case sensitive",true);
						MyPanel panelBox = new MyPanel(MyPanel.CENTER);
						panelBox.add(regex);
						panelBox.add(caseSensitive);
						c.gridx=0;
						c.gridy=2;
						c.gridwidth=2;
						c.fill=GridBagConstraints.HORIZONTAL;
						replace.add(panelBox,c);
						/*
						 * start button
						 */
						MyPanel panelButton = new MyPanel(MyPanel.CENTER);
						MyButton button1 = new MyButton("Start")
						{
							@Override
							public void mouseReleased(MouseEvent ev)
							{
								String original = text;
								String selected = textArea.getSelectedText();
								String buffer = selected==null?text:selected;
								String find = wd1.getText();
								String match = wd2.getText();
								boolean useRegex = regex.isSelected();
								boolean isCaseSensitive = caseSensitive.isSelected();
								int count = StaticUtilities.count(buffer,find,useRegex,isCaseSensitive);
								if (count != 0)
								{
									String result = StaticUtilities.replace(buffer,find,match,useRegex,isCaseSensitive);
									if (selected != null)
									{
										textArea.replaceSelection(result);
									}
									else
									{
										textArea.setText(result);
									}
								}
								JOptionPane.showMessageDialog(w, count + " time(s) replaced", "Replace", JOptionPane.INFORMATION_MESSAGE);
							}
						};
						panelButton.add(button1);
						MyButton button2 = new MyButton("Next")
						{
							@Override
							public void mouseReleased(MouseEvent ev)
							{
								String original = text;
								String find = wd1.getText();
								boolean useRegex = regex.isSelected();
								if (!caseSensitive.isSelected())
								{
									//case insensitive
									original = original.toLowerCase();
									find = find.toLowerCase();
								}
								int caret = Math.max(Math.max(textArea.getCaretPosition(),textArea.getSelectionStart()),textArea.getSelectionEnd());
								int index=0, end=0;
								Pattern pattern = Pattern.compile(find);
								if (caret != original.length())
								{
									if (useRegex)
									{
										//use regex
										Matcher matcher = pattern.matcher(original);
										if (matcher.find(caret))
										{
											index = matcher.start();
											end = matcher.end();
										}
										else
										{
											index = -1;
										}
									}
									else
									{
										index = original.indexOf(find,caret);
										end = index+find.length();
									}
								}
								else
								{
									index = -1;
								}
								if (index != -1)
								{
									textArea.select(index, end);
								}
								else
								{
									int option = JOptionPane.showConfirmDialog(w, "Reached the end of the file!\nSearch from the start again?", "Error", JOptionPane.YES_NO_OPTION);
									if (option == JOptionPane.YES_OPTION)
									{
										textArea.setCaretPosition(0);
										this.mouseReleased(ev);
									}
								}
							}
						};
						button2.setToolTipText("Find next occurrence");
						panelButton.add(button2);
						MyButton button3 = new MyButton("Last")
						{
							@Override
							public void mouseReleased(MouseEvent ev)
							{
								String original = text;
								String find = wd1.getText();
								boolean useRegex = regex.isSelected();
								if (!caseSensitive.isSelected())
								{
									//case insensitive
									original = original.toLowerCase();
									find = find.toLowerCase();
								}
								int caret = Math.min(Math.min(textArea.getCaretPosition(),textArea.getSelectionStart()),textArea.getSelectionEnd());
								int index=0, end=0;
								Pattern pattern = Pattern.compile(find);
								if (caret != 0)
								{
									if (useRegex)
									{
										for (int i=caret-find.length(); i>=0; i--)
										{
											//use regex
											String fragment = original.substring(i,caret);
											Matcher matcher = pattern.matcher(fragment);
											if (matcher.find())
											{
												index = matcher.start()+i;
												end = matcher.end()+i;
												break;
											}
											else
											{
												index = -1;
											}
										}
									}
									else
									{
										String fragment = original.substring(0,caret);
										index = fragment.lastIndexOf(find);
										if (index != -1)
										{
											end = index + find.length();
										}
									}
								}
								else
								{
									index = -1;
								}
								if (index != -1)
								{
									textArea.select(index, end);
									return;
								}
								else
								{
									int option = JOptionPane.showConfirmDialog(w, "Reached the start of the file!\nSearch from the end again?", "Error", JOptionPane.YES_NO_OPTION);
									if (option == JOptionPane.YES_OPTION)
									{
										textArea.setCaretPosition(original.length());
										this.mouseReleased(ev);
										return;
									}
								}
							}
						};
						button3.setToolTipText("Find last occurrence");
						panelButton.add(button3);
						c.gridx=0;
						c.gridy=3;
						c.gridwidth=2;
						c.fill=GridBagConstraints.HORIZONTAL;
						replace.add(panelButton,c);
						replace.pack();
						replace.setLocationRelativeTo(w);
						replace.setVisible(true);
					}
				}
				else
				{
					cannotEdit();
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
							String text = "";
							String buffer;
							/*
							 * modify textArea consecutively, disable auto backup first
							 */
							textArea.setAutoBackup(false);
							textArea.getUndoManager().backup();
							for (currentProcess=0; currentProcess<number[0]; currentProcess++)
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
							textArea.setSaved(false);
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
							}
							textArea.getUndoManager().backup();
							textArea.setAutoBackup(true);
							textArea.setSaved(false);
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
				int count = textArea.getLineCount();
				int option = JOptionPane.YES_OPTION;
				if (count>=1000)
				{
					option = JOptionPane.showConfirmDialog(w, "This may spend very long time if you have more than 1000 lines.\nContinue?", "Warning", JOptionPane.YES_NO_OPTION);
				}
				if (option == JOptionPane.YES_OPTION)
				{
					String text = textArea.getText();
					int caret = textArea.getCaretPosition();
					for (int i=count; i>=2; i--)
					{
						String buffer = "";
						for (int k=1; k<=i; k++)
						{
							buffer+="\n";
						}
						text = text.replace(buffer, "\n");
					}
					textArea.setText(text);
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
				String value = ColorDialog.showColorDialog(w);
				if (!value.isEmpty())
				{
					textArea.insert(value, textArea.getCaretPosition());
				}
			}
			break;
			
			case 41: //number conversion
			{
				String value = BaseConverter.showBaseConverter(w);
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
				final MyTextField exportTF = new MyTextField(38,0);
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
				exportSpin.setFont(f13);
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
				exportFormat.setFont(new Font("Microsoft Jhenghei", Font.PLAIN, 12));
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
				final MyRadioButton fast = new MyRadioButton("Fast",true,1);
				final MyRadioButton stable = new MyRadioButton("Stable",false,2);
				exportP2.add(fast);
				exportP2.add(stable);
				fast.setToolTipText("<html><font size=\"4\"><b>Fast export</b></font><br>Very fast, but may cause out-of-memory error. No extra gap.</html>");
				stable.setToolTipText("<html><font size=\"4\"><b>Stable export</b></font><br>Stable exportation, but is significantly slower.</html>");
				ActionListener modeListener = new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						MyRadioButton button = (MyRadioButton)(ev.getSource());
						switch (button.getIndex())
						{
							case 1:
							fast.setSelected(true);
							stable.setSelected(false);
							exportSpin.setEnabled(false);
							break;
							
							case 2:
							fast.setSelected(false);
							stable.setSelected(true);
							exportSpin.setEnabled(true);
							break;
						}
					}
				};
				fast.addActionListener(modeListener);
				stable.addActionListener(modeListener);
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
								textArea.insert(toChar(unicodeField.getText())+"", textArea.getCaretPosition());
								textArea.setSaved(false);
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
				textArea.setSaved(false);
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
								textArea.insert(toUnicodeValue(charField.getText().charAt(0)), textArea.getCaretPosition());
								textArea.setSaved(false);
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
				textArea.setText(insertSpaces(textArea.getText()));
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
				undoDialog.pack();
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
			
			default:
			break;
		}
	}
	
	private void showOptionDialog()
	{
		final RefluxEdit w = RefluxEdit.getInstance();
		final JComponent topPanel = w.getPageStartComponent();
		final Tab tab = MainPanel.getSelectedTab();
		final MyTextArea textArea = tab.getTextArea();
		final File file = tab.getFile();
		final UndoManager undoManager = textArea.getUndoManager();
		JDialog option = new JDialog(w, "Other options", true);
		option.getContentPane().setBackground(Color.WHITE);
		JTabbedPane tabbedPane = new JTabbedPane();
		loadConfig();
		//
		//tab1: general
		JPanel tab1 = new JPanel(new GridLayout(9,1,0,0));
		String isPanelString = getConfig0("isPanel");
		final MyRadioButton isPanel = new MyRadioButton("Use panel", false, 1);
		final MyRadioButton isToolBar = new MyRadioButton("Use toolbar", false, 2);
		final MyRadioButton NoContainer = new MyRadioButton("Hide panel/toolbar", false, 3);
		switch (isPanelString)
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
		String fontName = getConfig0("TextAreaFont.fontName");
		if (fontName == null)
		{
			fontName = "Microsoft Jhenghei";
		}
		int fontStyle, fontSize;
		try
		{
			fontStyle = Integer.parseInt(getConfig0("TextAreaFont.fontStyle"));
			if ((fontStyle<0)||(fontStyle>2)) fontStyle = 0;
		}
		catch (Exception ex)
		{
			fontStyle = 0;
		}
		try
		{
			fontSize = Integer.parseInt(getConfig0("TextAreaFont.fontSize"));
			if ((fontSize<1)||(fontSize>200)) fontSize = 13;
		}
		catch (Exception ex)
		{
			fontSize = 13;
		}
		MyPanel P2 = new MyPanel(MyPanel.LEFT);
		P2.add(new MyLabel("Text area font:"));
		MyFontChooser fontChooser = new MyFontChooser(new Font(fontName,fontStyle,fontSize));
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
		MyCheckBox useIndent = new MyCheckBox("Use automatic indentation", textArea.getFilter().isAutoIndent());
		P6.add(useIndent);
		tab1.add(P6);
		//
		MyPanel P7 = new MyPanel(MyPanel.LEFT);
		MyCheckBox useUmbrella = new MyCheckBox("Show umbrella", MyUmbrellaLayerUI.isPaintUmbrella());
		P7.add(useUmbrella);
		JSlider alphaYellow = new JSlider(0,255);
		alphaYellow.setValue(MyUmbrellaLayerUI.getUmbrellaColor().getAlpha());
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
			public void actionPerformed(ActionEvent ev)
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
				table.setDragEnabled(false);
				table.setAutoCreateRowSorter(true);
				tam.addColumn("File name");
				tam.addColumn("Caret position");
				for (String name: propertyNames())
				{
					if (name.startsWith("Caret.")&&(!name.equals("Caret.save")))
					{
						tam.addRow(new String[]{name.substring(6,name.length()),getConfig0(name.toString())});
					}
				}
				dialog.add(new JScrollPane(table));
				MyPanel bottom = new MyPanel(MyPanel.CENTER);
				MyButton remove = new MyButton("Remove")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						int option = JOptionPane.showConfirmDialog(w,"Remove caret data?", "Confirm", JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION)
						{
							int[] rows = table.getSelectedRows();
							ArrayList<String> paths = new ArrayList<>();
							for (int count=0; count<rows.length; count++)
							{
								paths.add(tam.getValueAt(rows[count],0).toString());
							}
							for (String path: paths)
							{
								removeConfig0("Caret." + path);
							}
							for (int i=rows.length-1; i>=0; i--)
							{
								tam.removeRow(rows[i]);
							}
							saveConfig();
						}							
					}
				};
				if (!isWindows) remove.setPreferredSize(new Dimension(65,28));
				bottom.add(remove);
				bottom.add(new MyButton("Clear")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						int option = JOptionPane.showConfirmDialog(w,"Remove all caret data?", "Confirm", JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION)
						{
							for (Object n: propertyNames())
							{
								String name = n.toString();
								if ((name.startsWith("Caret."))&&(!name.equals("Caret.save")))
								{
									removeConfig0(name);
								}
							}
							tam.getDataVector().clear();
							tam.fireTableDataChanged();
							saveConfig();
						}							
					}
				});
				bottom.add(new MyButton("Done")
				{
					@Override
					public void actionPerformed(ActionEvent ev)
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
		if (!isWindows) manage.setPreferredSize(new Dimension(65,25));
		P8.add(manage);
		tab1.add(P8);
		//
		MyPanel P9 = new MyPanel(MyPanel.LEFT);
		boolean confirmDrag = getBoolean("ConfirmDrag");
		MyCheckBox useConfirmDrag = new MyCheckBox("Confirm drag", confirmDrag);
		P9.add(useConfirmDrag);
		tab1.add(P9);
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
		String encodingName = getConfig0("Encoding");
		if (encodingName == null)
		{
			encodingName = "default1";
		}
		switch (encodingName)
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
		final JComboBox<String> comboBox = Tab.createCharsetComboBox();
		if (!encodingName.startsWith("default"))
		{
			comboBox.setSelectedItem(encodingName);
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
		String lineSeparatorString = getConfig0("lineSeparator");
		if (lineSeparatorString == null) lineSeparatorString = "\\n";
		switch (lineSeparatorString)
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
		String chooserStyle = getConfig0("ChooserStyle");
		if (chooserStyle == null)
		{
			chooserStyle = "Java";
		}
		switch (chooserStyle)
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
		MyCheckBox isRibbonBox = new MyCheckBox("Use Ribbon UI", RefluxEdit.isRibbon);
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
				removeRegexTF.setEnabled(removeOld.isSelected());
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
			public void actionPerformed(ActionEvent ev)
			{
				(new Thread()
				{
					@Override
					public void run()
					{
						VersionChecker.showUpdateDialog(w,true);
					}
						
				}).start();
			}
		};
		if (!isWindows) checkUpdateButton.setPreferredSize(new Dimension(80,27));
		updatePanel.add(checkUpdateButton);
		//
		//
		//restore toolbar to original location
		MyToolBar.getInstance().stopFloating();
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
		int edgeType, tabSize=4;
		boolean countWords, isEditable=true, isLineWrap, isWrapStyleWord, autoIndent;
		Color newSelectionColor;
		Font font;
		{
			//general
			//topPanel
			JComponent topComponent = null;
			if (isPanel.isSelected())
			{
				setConfig("isPanel", "true");
				topComponent = FourButtonPanel.getInstance();
			}
			else if (isToolBar.isSelected())
			{
				setConfig("isPanel", "false");
				topComponent = MyToolBar.getInstance();
			}
			else if (NoContainer.isSelected())
			{
				setConfig("isPanel", "no");
				topComponent = null;
			}
			if (!RefluxEdit.isRibbon)
			{
				if (topPanel != null)
				{
					w.remove(topPanel);
				}
				w.setPageStartComponent(topComponent);
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
			font = fontChooser.getFont();
			setConfig("TextAreaFont.fontName", font.getFontName());
			setConfig("TextAreaFont.fontStyle", font.getStyle() + "");
			setConfig("TextAreaFont.fontSize", font.getSize() + "");
			textArea.setFont(font);
			//menubar
			isUseNewMenuBar = useNewMenuBar.isSelected();
			setConfig("isUseNewMenuBar", isUseNewMenuBar + "");
			ColoredMenuBar menubar = ColoredMenuBar.getInstance();
			if (RefluxEdit.isCrossPlatformLAF)
			{
				if (isUseNewMenuBar)
				{
					menubar.setStyle(ColoredMenuBar.MODERN);
				}
				else
				{
					menubar.setStyle(ColoredMenuBar.BLUE);
				}
			}
			//narrowEdge
			narrowEdge = useNarrowEdge.isSelected();
			setConfig("isUseNarrowEdge", narrowEdge+"");
			edgeType = narrowEdge?Edge.NARROW:Edge.WIDE;			
			//word count panel
			countWords = useCount.isSelected();
			setConfig("showCount", countWords+"");
			//indentation
			autoIndent = useIndent.isSelected();
			setConfig("autoIndent", autoIndent+"");
			//use umbrella
			//paint?
			boolean paintTextArea = useUmbrella.isSelected();
			setConfig("showUmbrella", paintTextArea+"");
			MyUmbrellaLayerUI.setPaintUmbrella(paintTextArea);
			//color
			int alpha = alphaYellow.getValue();
			setConfig("Umbrella.alpha", alpha+"");
			MyUmbrellaLayerUI.setUmbrellaColor(new Color(251,231,51,alpha));
			//caret
			saveCaret = saveCaretPosition.isSelected();
			setConfig("Caret.save", saveCaret+"");
			//
			confirmDrag = useConfirmDrag.isSelected();
			setConfig("ConfirmDrag", confirmDrag+"");
		}
		{
			//line wrap
			isLineWrap = lineWrap.isSelected();
			isWrapStyleWord = wrapStyleWord.isSelected();
			setConfig("LineWrap", isLineWrap + "");
			setConfig("WrapStyleWord", isWrapStyleWord + "");
		}
		{
			//encoding
			String encodingChosen = null;
			if (isDefault1.isSelected())
			{
				encodingChosen = "default1";
			}
			else if (isDefault2.isSelected())
			{
				encodingChosen = "default2";
			}
			else //if (others.isSelected())
			{
				encodingChosen = comboBox.getSelectedItem().toString();
			}
			setConfig("Encoding", encodingChosen);
		}
		{
			//filechooser
			String csChosen;
			if (isJava.isSelected())
			{
				csChosen = "Java";
			}
			else
			{
				csChosen = "System";
			}
			setConfig("ChooserStyle", csChosen);
		}
		{
			//line separator
			String lineSeparator = "\\n";
			if (sep_r.isSelected()) lineSeparator = "\\r";
			if (sep_nr.isSelected()) lineSeparator = "\\r\\n";
			setConfig("lineSeparator", lineSeparator);
		}
		{
			//tab size
			try
			{
				spinnerTabSize.commitEdit();
				tabSize = Byte.parseByte(spinnerTabSize.getValue().toString());
			}
			catch (Exception ex)
			{
				tabSize = 4;
			}				
			finally
			{
				setConfig("TabSize", tabSize+"");
			}
		}
		{
			//selection color
			newSelectionColor = colorChooser.getColor();
			setConfig("SelectionColor.r", newSelectionColor.getRed()+"");
			setConfig("SelectionColor.g", newSelectionColor.getGreen()+"");
			setConfig("SelectionColor.b", newSelectionColor.getBlue()+"");
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
		/*
		 * update:
		 */
		Tab.setGlobalProperties(edgeType, countWords, isEditable, isLineWrap, isWrapStyleWord, tabSize, newSelectionColor, font, autoIndent);
		MainPanel.updateAllTab();
		/*
		 * done 
		 */
		w.revalidate();
		w.repaint();
		saveConfig();
	}
	
	public static void cannotEdit()
	{
		JOptionPane.showMessageDialog(RefluxEdit.getInstance(), "Editing the text is DISABLED!\nPlease enable editing!", "Error", JOptionPane.WARNING_MESSAGE);
	}
}
