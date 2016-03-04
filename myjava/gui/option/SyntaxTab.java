/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.option;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
import myjava.gui.*;
import myjava.gui.common.*;
import myjava.gui.syntax.*;
import myjava.util.*;
import static exec.SourceManager.*;

public class SyntaxTab extends OptionTab implements Resources
{
	//master checkbox: enable/disable all other componenets
	private JCheckBox highlightSyntax = new MyCheckBox("Enable syntax highlighting",MyUmbrellaLayerUI.isSyntaxHighlightingEnabled());
	private Set<JComponent> componentSet = new HashSet<>();
	private Set<myjava.gui.syntax.Painter> removedPainters = new HashSet<>();
	//other components
	private JCheckBox matchBracket = new MyCheckBox("Enable bracket matching",MyUmbrellaLayerUI.isBracketMatchingEnabled());
	//top: painterComboBox
	private final ItemListener painterChangeListener = new PainterChangeListener();
	private JComboBox<myjava.gui.syntax.Painter> painterComboBox = new JComboBox<>();
	//center: entryListPanels
	private CardLayout cardLayout = new CardLayout();
	private JPanel centerPanel = new JPanel(cardLayout);
	private Set<EntryListPanel> listPanelSet = new TreeSet<>();
	{
		for (myjava.gui.syntax.Painter painter: myjava.gui.syntax.Painter.getPainters())
		{
			painterComboBox.addItem(painter);
			EntryListPanel panel = new EntryListPanel(painter);
			listPanelSet.add(panel);
			centerPanel.add(panel,painter.getName());
		}
		componentSet.addAll(Arrays.asList(matchBracket,painterComboBox,centerPanel));
	}
	public SyntaxTab()
	{
		super(new BorderLayout(),"Syntax highlighting");
		/*
		 * upper checkboxes
		 */
		JPanel upper = new JPanel(new GridLayout(2,1,0,0));
		upper.setOpaque(false);
		upper.add(MyPanel.wrap(highlightSyntax));
		upper.add(MyPanel.wrap(matchBracket));
		highlightSyntax.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				SyntaxTab.this.updateComponentStatus();
			}
		});
		this.add(upper,BorderLayout.PAGE_START);
		/*
		 * upper panel (painters)
		 */
		painterComboBox.setSelectedItem(myjava.gui.syntax.Painter.getCurrentInstance());
		painterComboBox.setFont(f13);
		if (isMetal) painterComboBox.setBackground(Color.WHITE);
		painterComboBox.addItemListener(this.painterChangeListener);
		JButton addPainter = new MyButton("+")
		{
			{
				if (isMetal)
				{
					this.setPreferredSize(new Dimension(28,28));
				}
			}
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				String name;
				do
				{
					name = JOptionPane.showInputDialog(SyntaxTab.this,"Enter a name:","Name",JOptionPane.QUESTION_MESSAGE);
				}
				while (!myjava.gui.syntax.Painter.isValidPrompt(name,SyntaxTab.this));
				if ((name != null)&&(!name.isEmpty()))
				{
					//name is valid, neither cancelled nor pressed enter directly
					myjava.gui.syntax.Painter newPainter = ((myjava.gui.syntax.Painter)(painterComboBox.getSelectedItem())).newInstance(name);
					addPainter(newPainter);
					System.out.println("now set, should call listener");
					painterComboBox.setSelectedItem(newPainter); //auto-call ItemListener(s)
				}
			}
		};
		JButton removePainter = new MyButton("-")
		{
			{
				if (isMetal)
				{
					this.setPreferredSize(new Dimension(28,28));
				}
			}
			@Override
			public void actionPerformed(ActionEvent ev)
			{
				myjava.gui.syntax.Painter painter = (myjava.gui.syntax.Painter)(painterComboBox.getSelectedItem());
				if (painter.equals(myjava.gui.syntax.Painter.getDefaultInstance()))
				{
					JOptionPane.showMessageDialog(SyntaxTab.this,"The default painter cannot be removed.","Error",JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					int option = JOptionPane.showConfirmDialog(SyntaxTab.this,"Remove painter \"" + painter.getName() + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION)
					{
						//remove "painter"
						removedPainters.add(painter);
						painterComboBox.removeItemListener(painterChangeListener);
						painterComboBox.setSelectedItem(myjava.gui.syntax.Painter.getDefaultInstance());
						painterComboBox.removeItem(painter);
						for (Iterator<EntryListPanel> it = listPanelSet.iterator(); it.hasNext(); )
						{
							EntryListPanel panel = it.next();
							if (panel.getPainter().getName().equals(painter.getName()))
							{
								System.out.println("removing, then break");
								it.remove();
								centerPanel.remove(panel);
								break;
							}
						}
						painterComboBox.addItemListener(painterChangeListener);
						cardLayout.show(centerPanel,myjava.gui.syntax.Painter.getDefaultInstance().getName());
					}
				}
			}
		};
		//lower part
		JPanel center = new JPanel(new BorderLayout());
		JLabel selectLabel = new MyLabel("Selected painter:");
		center.add(MyPanel.wrap(MyPanel.CENTER,selectLabel,painterComboBox,addPainter,removePainter), BorderLayout.PAGE_START);
		componentSet.addAll(Arrays.asList(selectLabel,addPainter,removePainter));
		center.add(centerPanel, BorderLayout.CENTER);
		this.add(center, BorderLayout.CENTER);
		cardLayout.show(centerPanel,myjava.gui.syntax.Painter.getCurrentInstance().getName());
	}
	
	private void updateComponentStatus()
	{
		/*
		 * enable/disable component by first checkbox
		 */
		boolean enable = highlightSyntax.isSelected();
		for (JComponent c: componentSet)
		{
			c.setEnabled(enable);
		}
	}
	
	private void addPainter(myjava.gui.syntax.Painter painter)
	{
		EntryListPanel newPanel = new EntryListPanel(painter);
		listPanelSet.add(newPanel);
		centerPanel.add(newPanel,painter.getName());
		painterComboBox.removeItemListener(painterChangeListener);
		painterComboBox.removeAllItems();
		for (EntryListPanel p: listPanelSet)
		{
			painterComboBox.addItem(p.getPainter());
		}
		removedPainters.remove(painter);
		painterComboBox.addItemListener(painterChangeListener);
	}

	private static <E> JList<E> newList(DefaultListModel<E> listModel)
	{
		JList<E> list = new JList<>(listModel);
		list.setFont(f13);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return list;
	}
	
	class PainterChangeListener implements ItemListener
	{
		PainterChangeListener()
		{
			super();
		}
		
		@Override
		public void itemStateChanged(ItemEvent ev)
		{
			if (ev.getStateChange() == ItemEvent.SELECTED)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						String newPainterName = ((myjava.gui.syntax.Painter)(painterComboBox.getSelectedItem())).getName();
						cardLayout.show(centerPanel,newPainterName);
						centerPanel.revalidate();
						centerPanel.repaint();
					}
				});
			}
		}
	}
	
	class EntryListPanel extends JPanel implements Comparable<EntryListPanel>
	{
		private myjava.gui.syntax.Painter painter;
		private DefaultListModel<TypeColorEntry> listModel = new DefaultListModel<>();
		private JList<TypeColorEntry> list = newList(listModel);
		EntryListPanel(myjava.gui.syntax.Painter painter)
		{
			super(new BorderLayout());
			this.add(new JScrollPane(list),BorderLayout.CENTER);
			this.painter = painter;
			this.updateList();
			this.list.setCellRenderer(new EntryListRenderer());
			this.list.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent ev)
				{
					/*
					 * selected a colored entry
					 */
					if (ev.getClickCount() == 2)
					{
						if (EntryListPanel.this.painter.equals(myjava.gui.syntax.Painter.getDefaultInstance()))
						{
							//cannot modify default painter instance
							JOptionPane.showMessageDialog(SyntaxTab.this,"The default painter cannot be modified.","Error",JOptionPane.ERROR_MESSAGE);
						}
						else
						{
							//start modifying, show color chooser dialog
							final TypeColorEntry entry = list.getSelectedValue();
							final JColorChooser colorChooser = new JColorChooser(entry.getColor());
							ActionListener ok = new ActionListener()
							{
								@Override
								public void actionPerformed(ActionEvent ev)
								{
									entry.setColor(colorChooser.getColor());
									list.repaint();
								}
							};
							JDialog dialog = JColorChooser.createDialog(SyntaxTab.this,"Choose a color:",true,colorChooser,ok,null);
							dialog.setVisible(true);
						}
					}
				}
			});
		}
		
		myjava.gui.syntax.Painter getPainter()
		{
			//update painter first
			this.painter = TypeColorEntry.toPainter(this.painter.getName(),Collections.list(listModel.elements()));
			return this.painter;
		}
		
		final void updateList()
		{
			Set<TypeColorEntry> entries = TypeColorEntry.fromPainter(this.painter);
			this.listModel.removeAllElements();
			for (TypeColorEntry entry: entries)
			{
				this.listModel.addElement(entry);
			}
		}
		
		@Override
		public int compareTo(EntryListPanel panel)
		{
			return this.getPainter().compareTo(panel.getPainter());
		}
	}
	
	static class TypeColorEntry implements Comparable<TypeColorEntry>
	{
		private Token.Type type;
		private Color c;
		TypeColorEntry(Token.Type type, Color c)
		{
			super();
			this.type = type;
			this.c = c;
		}
		
		Token.Type getType()
		{
			return this.type;
		}
		
		Color getColor()
		{
			return this.c;
		}
		
		void setColor(Color c)
		{
			this.c = c;
		}
		
		static SortedSet<TypeColorEntry> fromPainter(myjava.gui.syntax.Painter painter)
		{
			Token.Type[] types = Token.Type.values();
			SortedSet<TypeColorEntry> entries = new TreeSet<>();
			for (Token.Type type: types)
			{
				entries.add(new TypeColorEntry(type,painter.fromType(type)));
			}
			return entries;
		}
		
		static myjava.gui.syntax.Painter toPainter(String name, final Collection<? extends TypeColorEntry> entries)
		{
			return new myjava.gui.syntax.Painter(name)
			{
				private Map<Token.Type,Color> map = new HashMap<>();
				{
					for (TypeColorEntry entry: entries)
					{
						Token.Type type = entry.getType();
						Color color = entry.getColor();
						if ((type == Token.Type.MATCHED_BRACKET)||(type == Token.Type.UNMATCHED_BRACKET))
						{
							map.put(type,new Color(color.getRed(),color.getGreen(),color.getBlue(),80));
						}
						else
						{
							map.put(type,color);
						}
					}
				}
				
				@Override
				public Color fromType(Token.Type type)
				{
					Color c = map.get(type);
					return c==null?Color.BLACK:c;
				}
			};
		}
		
		@Override
		public int compareTo(TypeColorEntry entry)
		{
			return this.getType().compareTo(entry.getType());
		}
	}
	
	static class EntryListRenderer extends DefaultListCellRenderer
	{
		EntryListRenderer()
		{
			super();
		}
		
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			JLabel label = (JLabel)(super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus));
			if (value instanceof TypeColorEntry)
			{
				TypeColorEntry entry = (TypeColorEntry)value;
				Token.Type type = entry.getType();
				Color color = entry.getColor();
				label.setText(Utilities.normalize(type.toString()));
				if ((type == Token.Type.MATCHED_BRACKET)||(type == Token.Type.UNMATCHED_BRACKET))
				{
					label.setBackground(color);
					label.setForeground(Color.BLACK);
				}
				else
				{
					label.setForeground(color);
				}
			}
			return label;
		}
	}
	
	@Override
	public void onExit()
	{
		//save all painters
		for (EntryListPanel panel: listPanelSet)
		{
			myjava.gui.syntax.Painter painter = panel.getPainter();
			if (!painter.equals(myjava.gui.syntax.Painter.getDefaultInstance()))
			{
				myjava.gui.syntax.Painter.add(painter);
				setConfig("painter.userDefined."+painter.getName(),painter.toWritableString());
			}
			if (painter.equals(painterComboBox.getSelectedItem()))
			{
				//selected painter
				setConfig("syntax.selectedPainter",painter.getName());
				myjava.gui.syntax.Painter.setCurrentInstance(painter);
			}
		}
		//remove "removed painters"
		for (myjava.gui.syntax.Painter removed: removedPainters)
		{
			removeConfig0("painter.userDefined."+removed.getName());
			myjava.gui.syntax.Painter.remove(removed);
		}
		//highlight?
		boolean _highlightSyntax = highlightSyntax.isSelected();
		boolean _matchBracket = matchBracket.isSelected();
		MyUmbrellaLayerUI.setHighlightingStatus(_highlightSyntax,_matchBracket);
		setConfig("syntax.highlight",_highlightSyntax+"");
		setConfig("syntax.matchBrackets",_matchBracket+"");
	}
}
