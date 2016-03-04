/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.option;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.*;
import java.text.*;
import exec.*;
import myjava.gui.*;
import static exec.SourceManager.*;

public class EditorTab extends OptionTab
{
	//fonts
	private MyFontChooser.PreviewPanel fontChooser = new MyFontChooser.PreviewPanel(loadFont());
	//word and character count
	private JCheckBox wordCount = new MyCheckBox("Show word and character count", getBoolean0("statusBar.showCount"));
	//line counter
	private JCheckBox lineCount = new MyCheckBox("Show line counter", getBoolean0("textArea.showLineCounter"));
	//caret location
	private JCheckBox caretLocation = new MyCheckBox("Show caret location", getBoolean0("statusBar.showCaretLocation"));
	//wrap
	private JCheckBox lineWrap = new MyCheckBox("Line Wrap", textArea.getLineWrap());
	private JCheckBox wrapStyleWord = new MyCheckBox("Wrap by word", textArea.getWrapStyleWord());
	private JSpinner tabSize = new JSpinner(new SpinnerNumberModel(textArea.getTabSize(), 1, 50, 1));
	//selection color
	private JColorChooser colorChooser = new JColorChooser();
	private JPanel preview = new JPanel();
	//constructor
	public EditorTab()
	{
		super(new FlowLayout(FlowLayout.LEFT), "Editor");
		JPanel in = new JPanel(new GridLayout(6,1,0,0));
		//fonts
		in.add(MyPanel.wrap(new MyLabel("Text area font:"),fontChooser));
		//word and character count
		in.add(MyPanel.wrap(wordCount));
		//line counter
		in.add(MyPanel.wrap(lineCount));
		//caret location
		in.add(MyPanel.wrap(caretLocation));
		//wrap
		in.add(MyPanel.wrap(lineWrap,wrapStyleWord,new MyLabel("  Tab size:"),tabSize));
		wrapStyleWord.setEnabled(lineWrap.isSelected());
		lineWrap.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				wrapStyleWord.setEnabled(lineWrap.isSelected());
			}
		});
		//selection color
		preview.setPreferredSize(new Dimension(50,28));
		preview.setBackground(loadColor());
		MyButton change = new MyButton("Change")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				ActionListener ok = new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent ev)
					{
						preview.setBackground(colorChooser.getColor());
					}
				};
				colorChooser.setColor(preview.getBackground());
				JDialog dialog = JColorChooser.createDialog(EditorTab.this,"Choose a color:",true,colorChooser,ok,null);
				dialog.setVisible(true);
			}
		};
		if (isMetal)
		{
			change.setPreferredSize(new Dimension(60,28));
		}
		MyButton reset = new MyButton("Reset")
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				preview.setBackground(new Color(244,223,255));
			}
		};
		in.add(MyPanel.wrap(new MyLabel("Selection color: "),preview,change,reset));
		this.add(in);
	}
	
	@Override
	public void onExit()
	{
		//font
		Font font = fontChooser.getSelectedFont();
		MyTextArea.setTextFont(font);
		setConfig("textArea.font.name", font.getFontName());
		setConfig("textArea.font.style", font.getStyle()+"");
		setConfig("textArea.font.size", font.getSize()+"");
		//word and character count
		setConfig("statusBar.showCount",wordCount.isSelected()+"");
		//line counter
		boolean _lineCounter = lineCount.isSelected();
		setConfig("textArea.showLineCounter", _lineCounter+"");
		MyTextArea.setEnableLineCounter(_lineCounter);
		//caret location
		setConfig("statusBar.showCaretLocation", caretLocation.isSelected()+"");
		//wrap
		boolean _wrap = lineWrap.isSelected();
		boolean _wrapStyleWord = wrapStyleWord.isSelected();
		try
		{
			tabSize.commitEdit();
		}
		catch (ParseException ex)
		{
			//pass
		}
		int _tabSize = Integer.parseInt(tabSize.getValue().toString());
		MyTextArea.setTextLineWrap(_wrap);
		MyTextArea.setTextWrapStyleWord(_wrapStyleWord);
		MyTextArea.setTab(_tabSize);
		setConfig("textArea.isLineWrap", _wrap+"");
		setConfig("textArea.isWrapStyleWord", _wrapStyleWord+"");
		setConfig("textArea.tabSize", _tabSize+"");
		//selection color
		Color selected = preview.getBackground();
		MyTextArea.setTextSelectionColor(selected);
		setConfig("textArea.selectionColor", selected.getRGB()+"");
	}
	
	private static Font loadFont()
	{
		return MainPanel.getSelectedTab().getTextArea().getFont();
	}
	
	private static Color loadColor()
	{
		try
		{
			return new Color(Integer.parseInt(getConfig0("textArea.selectionColor")));
		}
		catch (Exception ex)
		{
			return new Color(244,223,255);
		}
	}
}
