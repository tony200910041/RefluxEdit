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
	private MyFontChooser fontChooser = new MyFontChooser(loadFont());
	//word and character count
	private JCheckBox wordCount = new MyCheckBox("Show word and character count", getBoolean0("showCount"));
	//line counter
	private JCheckBox lineCount = new MyCheckBox("Show line counter", getBoolean0("showLineCounter"));
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
		JPanel in = new JPanel(new GridLayout(5,1,0,0));
		//fonts
		in.add(MyPanel.wrap(new MyLabel("Text area font:"),fontChooser));
		//word and character count
		in.add(MyPanel.wrap(wordCount));
		//line counter
		in.add(MyPanel.wrap(lineCount));
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
		Font font = fontChooser.getFont();
		MyTextArea.setTextFont(font);
		setConfig("TextAreaFont.fontName", font.getFontName());
		setConfig("TextAreaFont.fontStyle", font.getStyle()+"");
		setConfig("TextAreaFont.fontSize", font.getSize()+"");
		//word and character count
		boolean _count = wordCount.isSelected();
		setConfig("showCount", _count+"");
		Tab.setEnableCountWords(_count);
		//line counter
		boolean _lineCounter = lineCount.isSelected();
		setConfig("showLineCounter", _lineCounter+"");
		MyTextArea.setEnableLineCounter(_lineCounter);
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
		setConfig("LineWrap", _wrap+"");
		setConfig("WrapStyleWord", _wrapStyleWord+"");
		setConfig("TabSize", _tabSize+"");
		//selection color
		Color selected = preview.getBackground();
		MyTextArea.setTextSelectionColor(selected);
		setConfig("SelectionColor.r", selected.getRed()+"");
		setConfig("SelectionColor.g", selected.getGreen()+"");
		setConfig("SelectionColor.b", selected.getBlue()+"");
	}
	
	private static Font loadFont()
	{
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
			if ((fontSize<1)||(fontSize>200)) fontSize = 15;
		}
		catch (Exception ex)
		{
			fontSize = 15;
		}
		return new Font(fontName, fontStyle, fontSize);
	}
	
	private static Color loadColor()
	{
		try
		{
			int r = Integer.parseInt(getConfig0("SelectionColor.r"));
			int g = Integer.parseInt(getConfig0("SelectionColor.g"));
			int b = Integer.parseInt(getConfig0("SelectionColor.b"));
			return new Color(r,g,b);
		}
		catch (Exception ex)
		{
			return new Color(244,223,255);
		}
	}
}
