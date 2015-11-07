/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.imageio.*;
import java.util.*;
import java.text.*;
import java.io.*;
import exec.*;

public class ImageExportDialog extends JDialog
{
	//export path
	private JTextField exportPathField = new MyTextField();
	//export details
	private JRadioButton fast = new MyRadioButton("Fast",true);
	private JRadioButton stable = new MyRadioButton("Stable",false);
	private MyButtonGroup group1 = new MyButtonGroup(fast,stable);
	private JComboBox<String> format;
	private JSpinner extraGap = new JSpinner(new SpinnerNumberModel(5,5,100,1));
	protected ImageExportDialog(Frame parent)
	{
		super(parent,"Export as image",true);
		this.setLayout(new GridBagLayout());
		//
		JPanel panel1 = new JPanel(new GridBagLayout());
		panel1.setBorder(new TitledBorder("General settings"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(4,4,4,4);
		panel1.add(new MyLabel("Export path:"), c);
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		panel1.add(exportPathField, c);
		c.gridx = 2;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;		
		panel1.add(new MyButton("Change")
		{
			{
				if (isMetal) this.setPreferredSize(new Dimension(60,28));
				ImageExportDialog.this.getRootPane().setDefaultButton(this);
			}
			
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				String formatName = (String)(format.getSelectedItem());
				File dest = FileChooser.showPreferredFileDialog(RefluxEdit.getInstance(), FileChooser.SAVE, formatName);
				exportPathField.setText(dest==null?null:dest.getPath());
			}
		}, c);
		c.gridx = 0;
		c.gridy = 1;
		panel1.add(new MyLabel("Format:"), c);
		//
		Set<String> formatSet = new LinkedHashSet<>();
		for (String format: ImageIO.getWriterFormatNames())
		{
			formatSet.add(format.toLowerCase());
		}
		format = new JComboBox<>(new Vector<>(formatSet));
		format.setEditable(false);
		format.setBackground(Color.WHITE);
		c.gridx = 1;
		panel1.add(format, c);
		//
		JPanel panel2 = new JPanel(new GridBagLayout());
		panel2.setBorder(new TitledBorder("Mode"));
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		panel2.add(fast, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel2.add(stable, c);
		c.gridx = 1;
		panel2.add(new MyLabel("Extra gap:"), c);
		c.gridx = 2;
		panel2.add(extraGap, c);
		c.gridx = 3;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel2.add(new JLabel(), c); //placeholder
		ActionListener enableSpinner = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				extraGap.setEnabled(stable.isSelected());
			}
		};
		fast.addActionListener(enableSpinner);
		stable.addActionListener(enableSpinner);
		extraGap.setEnabled(false);
		extraGap.setFont(myjava.gui.common.Resources.f13);
		//
		JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel3.add(new MyButton("Start")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				String exportPath = exportPathField.getText();
				if (exportPath.isEmpty())
				{
					JOptionPane.showMessageDialog(ImageExportDialog.this,"Export path is empty.","Error",JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					ImageExportDialog.this.setVisible(false);
					writeImage(fast.isSelected()?ExportMode.FAST:ExportMode.STABLE);						
					ImageExportDialog.this.dispose();
				}
			}
		});
		panel3.add(new MyButton("Cancel")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				ImageExportDialog.this.setVisible(false);
				ImageExportDialog.this.dispose();
			}
		});
		//
		format.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				//format changed
				String newFormat = (String)(format.getSelectedItem());
				String exportPath = exportPathField.getText();
				if (!exportPath.isEmpty())
				{
					if (!exportPath.toLowerCase().endsWith("."+newFormat))
					{
						if (new File(exportPath).getName().contains("."))
						{
							exportPathField.setText(exportPath.substring(0,exportPath.lastIndexOf("."))+"."+newFormat);
						}
						else
						{
							exportPathField.setText(exportPath+"."+newFormat);
						}
					}
				}
			}
		});
		format.setSelectedItem("png");
		//
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		this.add(panel1, c);
		//
		c.gridy = 1;
		this.add(panel2, c);
		//
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		this.add(panel3, c);
	}
	
	private void writeImage(ExportMode mode)
	{
		final JTextArea textArea = MainPanel.getSelectedTab().getTextArea();
		switch (mode)
		{
			case FAST:
			Color oldBackground = textArea.getBackground();
			textArea.setBackground(Color.WHITE);
			try
			{				
				BufferedImage image = new BufferedImage(textArea.getWidth(),textArea.getHeight(),BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = image.createGraphics();
				textArea.paintAll(g2d);
				g2d.dispose();
				ImageIO.write(image,(String)(format.getSelectedItem()),new File(exportPathField.getText()));
			}
			catch (OutOfMemoryError ex)
			{
				JOptionPane.showMessageDialog(this,"An error occurred.\nThe image exporter will now switch to stable mode.","Error",JOptionPane.ERROR_MESSAGE);
				//continue
			}
			catch (IOException ex)
			{
				ExceptionDialog.exception(ex);
			}
			finally
			{
				textArea.setBackground(oldBackground);
			}
			default:
			case STABLE:
			{
				try
				{
					extraGap.commitEdit();
				}
				catch (ParseException ex)
				{
				}
				SwingWorker<Void,ExportState> worker = new SwingWorker<Void,ExportState>()
				{
					ProgressDialog dialog = new ProgressDialog(RefluxEdit.getInstance(),"Please wait...");
					//image data
					java.util.List<String> lines = new ArrayList<>(textArea.getLineCount());
					Document doc = textArea.getDocument();
					int totalLength = doc.getLength();
					Font font = textArea.getFont();
					FontMetrics fm = new Canvas().getFontMetrics(font);
					{
						dialog.setVisible(true);
					}
					@Override
					protected Void doInBackground()
					{
						int maxWidth = 0;
						int off = 0;
						while (off < doc.getLength())
						{
							try
							{
								int end = Utilities.getRowEnd(textArea,off)+1;
								String text = doc.getText(off,end-off).replace("\n","");
								maxWidth = Math.max(maxWidth,fm.stringWidth(text));
								lines.add(text);
								off = end;
								this.publish(new ExportState(1,off,totalLength));
							}
							catch (BadLocationException ex)
							{
								throw new InternalError("Bad location");
							}
						}				
						int gap = Integer.parseInt(extraGap.getValue().toString());
						int lineCount = lines.size();
						int stringHeight = fm.getHeight();
						int height = lineCount*(stringHeight+gap);
						//now create image
						BufferedImage image = new BufferedImage(maxWidth,height,BufferedImage.TYPE_INT_ARGB);
						Graphics2D g2d = image.createGraphics();
						g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
						g2d.setColor(Color.WHITE);
						g2d.fillRect(0,0,maxWidth,height);
						g2d.setColor(Color.BLACK);
						g2d.setFont(font);
						int startY = stringHeight;
						for (String line: lines)
						{
							g2d.drawString(line,0,startY);
							startY += stringHeight + gap;
							this.publish(new ExportState(2,startY,height));
						}
						g2d.dispose();
						//save image
						this.publish(new ExportState(3,1,1));
						try
						{
							File output = new File(exportPathField.getText());
							ImageIO.write(image,(String)(format.getSelectedItem()),output);
						}
						catch (IOException ex)
						{
							ExceptionDialog.exception(ex);
						}
						catch (OutOfMemoryError ex)
						{
							ExceptionDialog.error("out of memory!");
						}
						return null;
					}
					
					@Override
					protected void process(java.util.List<ExportState> chunks)
					{
						if (!chunks.isEmpty())
						{
							ExportState state = chunks.get(chunks.size()-1);
							dialog.setValue(state.getValue());
							dialog.setMax(state.getMax());
							dialog.setString(state.toString());
						}
					}
					
					@Override
					protected void done()
					{
						dialog.setVisible(false);
						dialog.dispose();
					}
				};
				worker.execute();
			}
		}
	}
	
	static enum ExportMode
	{
		FAST, STABLE;
	}
	
	static class ExportState
	{
		private int step, value, max;
		ExportState(int step, int value, int max)
		{
			super();
			this.step = step;
			this.value = value;
			this.max = max;
		}
		
		public int getValue()
		{
			return this.value;
		}
		
		public int getMax()
		{
			return this.max;
		}
		
		@Override
		public String toString()
		{
			if (step == 3) return "Step 3 of 3: saving image";
			else return "Step " + step + " of 3: " + Math.round((value*100d)/max) + "%";
		}
	}
	
	public static void showDialog(Frame parent)
	{
		JDialog dialog = new ImageExportDialog(parent);
		dialog.pack();
		dialog.setMinimumSize(new Dimension(450,310));
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}
}
